package org.syncany.gui.settings;

import static org.syncany.gui.ApplicationResourcesManager.DEFAULT_BUTTON_HEIGHT;
import static org.syncany.gui.ApplicationResourcesManager.DEFAULT_BUTTON_WIDTH;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.syncany.gui.Launcher;
import org.syncany.gui.WidgetDecorator;
import org.syncany.gui.config.ApplicationConfiguration;
import org.syncany.gui.config.Profile;
import org.syncany.util.I18n;


/**
 * @author Vincent Wiencek <vwiencek@gmail.com>
 *
 */
public class AccountSettingsPanel extends Composite {
	private ApplicationConfiguration configuration;
	
	private Label introductionLabel;
	private Label introductionTitleLabel;
	private Table table;
	private Composite composite;
	private Button deleteProfileButton;
	private Label separatorLabel;
	
	/**
	 * @param parent
	 * @param style
	 */
	public AccountSettingsPanel(Composite parent, int style) {
		super(parent, style);
		initComposite();
	}
	
	private void initComposite() {
		setLayout(new GridLayout(1, false));
		
		introductionTitleLabel = new Label(this, SWT.WRAP);
		introductionTitleLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		introductionTitleLabel.setText(I18n.getString("dialog.settings.account.introduction.title"));
		
		introductionLabel = new Label(this, SWT.WRAP | SWT.HORIZONTAL);
		introductionLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		introductionLabel.setText(I18n.getString("dialog.settings.account.introduction"));
		
		separatorLabel = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
		separatorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		separatorLabel.setText("New Label");
		
		table = new Table(this, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		composite = new Composite(this, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		composite.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		deleteProfileButton = new Button(composite, SWT.NONE);
		deleteProfileButton.setText("Delete Profile");
		deleteProfileButton.setLayoutData(new RowData(DEFAULT_BUTTON_WIDTH, DEFAULT_BUTTON_HEIGHT));
		deleteProfileButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				handleDeleteProfile();
			}
		});
		
		WidgetDecorator.bold(introductionTitleLabel);
		WidgetDecorator.normal(introductionLabel, table, deleteProfileButton);
	}

	
	protected void handleDeleteProfile() {
		int idx = table.getSelectionIndex();
		
		if (idx != -1){
			configuration.getProfiles().remove(idx);
			Launcher.saveConfiguration();
			
			updateTable();
		}
	}

	public void setApplicationParameters(ApplicationConfiguration configuration){
		this.configuration = configuration;

		TableColumn colonne1 = new TableColumn(table, SWT.LEFT);
		colonne1.setText("Folder");
		colonne1.setWidth(100);
		
		TableColumn colonne2 = new TableColumn(table, SWT.LEFT);
		colonne2.setText("Watch Interval");
		colonne2.setWidth(100);
		
		TableColumn colonne3 = new TableColumn(table, SWT.LEFT);
		colonne3.setText("Automatic ?");
		colonne3.setWidth(100);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true); 
		
		updateTable();
	}
	
	private void updateTable(){
		table.removeAll();
		
		for (Profile p : configuration.getProfiles()){
			TableItem item = new TableItem(table,SWT.NONE);
			item.setText(new String[] {p.getFolder(),p.getWatchInterval()+"", p.isAutomaticSync()+""});
		}
	}
}