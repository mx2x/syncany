/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2014 Philipp C. Heckel <philipp.heckel@gmail.com> 
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.syncany.tests.daemon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.syncany.config.to.DaemonConfigTO;
import org.syncany.database.FileVersion;
import org.syncany.operations.daemon.LocalEventBus;
import org.syncany.operations.daemon.WatchServer;
import org.syncany.operations.daemon.messages.GetFileTreeRequest;
import org.syncany.operations.daemon.messages.GetFileTreeResponse;
import org.syncany.operations.daemon.messages.Response;
import org.syncany.plugins.local.LocalConnection;
import org.syncany.plugins.transfer.TransferSettings;
import org.syncany.tests.util.TestClient;
import org.syncany.tests.util.TestConfigUtil;
import org.syncany.tests.util.TestDaemonUtil;

import com.google.common.eventbus.Subscribe;

/**
 * The BasicWatchServerTest tests the WatchServer as a seperate entity. It
 * should test if all basic functionality works as expected.
 * 
 * @author Pim Otte
 *
 */
public class BasicWatchServerTest {
	public static final Map<Integer, Response> responses = new HashMap<Integer, Response>();

	@Test
	public void AddSingleFileTest() throws Exception {
		final TransferSettings testConnection = TestConfigUtil.createTestLocalConnection();		
		final TestClient clientA = new TestClient("ClientA", testConnection);
		final TestClient clientB = new TestClient("ClientB", testConnection);
		

		// Load config template
		DaemonConfigTO daemonConfig = TestDaemonUtil.loadDaemonConfig("daemonTwoFoldersNoWebServer.xml");
		
		// Dynamically insert paths
		daemonConfig.getFolders().get(0).setPath(clientA.getConfig().getLocalDir().getAbsolutePath());
		daemonConfig.getFolders().get(1).setPath(clientB.getConfig().getLocalDir().getAbsolutePath());
		
		// Create access token (not needed in this test, but prevents errors in daemon)
		daemonConfig.setPortTO(TestDaemonUtil.createPortTO(daemonConfig.getWebServer().getPort()));
			
		// Create watchServer
		WatchServer watchServer = new WatchServer();
		
		clientA.createNewFile("file-1");
		watchServer.start(daemonConfig);
		
		for (int i = 0; i < 20; i++) {
			if(clientB.getLocalFile("file-1").exists()) {
				break;
			}
			Thread.sleep(1000);
		}
		
		assertTrue("File has not synced to clientB", clientB.getLocalFile("file-1").exists());
		assertEquals(clientA.getLocalFile("file-1").length(), clientB.getLocalFile("file-1").length());
		
		watchServer.stop();
		clientA.deleteTestData();
		clientB.deleteTestData();
		
	}
	
	@Test
	public void getFileTreeRequestTest() throws Exception {
		final TransferSettings testConnection = TestConfigUtil.createTestLocalConnection();	
		final TestClient clientA = new TestClient("ClientA", testConnection);
		

		// Load config template
		DaemonConfigTO daemonConfig = TestDaemonUtil.loadDaemonConfig("daemonTwoFoldersNoWebServer.xml");
		
		// Dynamically insert paths
		daemonConfig.getFolders().get(0).setPath(clientA.getConfig().getLocalDir().getAbsolutePath());
		daemonConfig.getFolders().get(1).setEnabled(false);
		
		// Create access token (not needed in this test, but prevents errors in daemon)
		daemonConfig.setPortTO(TestDaemonUtil.createPortTO(daemonConfig.getWebServer().getPort()));
			
		// Create watchServer
		WatchServer watchServer = new WatchServer();
		
		clientA.createNewFile("file-1");
		clientA.createNewFolder("folder");
		clientA.createNewFile("folder/file-2");
		watchServer.start(daemonConfig);
		
		
		Thread.sleep(6000);
		// Repeat request until 3 files are found.
		List<FileVersion> files = new ArrayList<FileVersion>();
		for(int i = 0; i < 20; i++) {
			GetFileTreeRequest request = new GetFileTreeRequest();
			request.setId(i);
			request.setRoot(clientA.getConfig().getLocalDir().getAbsolutePath());
			LocalEventBus eventBus = LocalEventBus.getInstance();
			
			eventBus.register(this);
			
			eventBus.post(request);
			
			Response response = waitForResponse(i);
			
			assertTrue(response instanceof GetFileTreeResponse);
			GetFileTreeResponse treeResponse = (GetFileTreeResponse) response;
			
			files = treeResponse.getFiles();
			
			if (files.size() == 2) {
				break;
			}
			
			if (i == 19) {
				assertEquals(2, files.size());
			}
			else {
				Thread.sleep(1000);
			}
		}

		// Check if information is correct
		for (FileVersion fileVersion : files) {
			if (fileVersion.getName().equals("file-1")) {
				assertEquals(clientA.getLocalFile("file-1").length(), (long)fileVersion.getSize());
			}			
			else if (fileVersion.getName().equals("folder")) {
				assertTrue(clientA.getLocalFile("folder").isDirectory());
				assertEquals(fileVersion.getType(), FileVersion.FileType.FOLDER);
			}
			else {
				assertTrue("fileVersion should not exist", false);
			}
			
		}
		
		watchServer.stop();
		clientA.deleteTestData();
	}
	
	@Subscribe
	public void onResponseReceived(Response response) {	
		responses.put(response.getRequestId(), response);
	}
	
	private Response waitForResponse(int id) throws Exception {
		while (responses.containsKey(id) == false) {
			Thread.sleep(100);
		}
		return responses.get(id);
	}

}