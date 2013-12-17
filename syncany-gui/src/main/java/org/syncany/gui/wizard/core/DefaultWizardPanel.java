/*
 * Syncany, www.syncany.org
 * Copyright (C) 2011-2013 Philipp C. Heckel <philipp.heckel@gmail.com> 
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
package org.syncany.gui.wizard.core;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.syncany.gui.util.I18n;
import org.syncany.gui.util.SWTResourceManager;

/**
 * @author Vincent Wiencek <vwiencek@gmail.com>
 *
 */
public abstract class DefaultWizardPanel extends DefaultDialog implements SelectionListener {
	private int type;
	
	private Button cancelButton;
	private Button previousButton;
	private Button nextButton;
	private Button connectButton;
	
	/**
	 * @param parent
	 * @param style
	 */
	public DefaultWizardPanel(int type, Shell parent, int style) {
		super(parent, style);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Create contents of the dialog.
	 */
	protected void createContents() {
		super.createContents();
		shell.setSize(450, 330);
		shell.setText(getText());
		GridLayout gl_shell = new GridLayout(2, false);
		gl_shell.horizontalSpacing = 0;
		gl_shell.verticalSpacing = 0;
		gl_shell.marginWidth = 0;
		gl_shell.marginHeight = 0;
		shell.setLayout(gl_shell);
		
		Label wizardImageLabel = new Label(shell, SWT.NONE);
		wizardImageLabel.setImage(SWTResourceManager.getImage("/images/panel.png"));
		
		createComposite(shell);
		
		Label label = new Label(shell, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		
		Composite buttonComposite = new Composite(shell, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, true, 2, 1));
		RowLayout buttonRowLayout = new RowLayout(SWT.HORIZONTAL);
		buttonRowLayout.marginRight = 20;
		buttonComposite.setLayout(buttonRowLayout);
		
		if ((type & WizardType.CANCEL) == WizardType.CANCEL){
			cancelButton = new Button(buttonComposite, SWT.NONE);
			cancelButton.setLayoutData(new RowData(80, 30));
			cancelButton.setText(I18n.getString(DefaultWizardPanel.class.getSimpleName()+".dialog.cancel"));
			cancelButton.addSelectionListener(this);
		}
		if ((type & WizardType.PREVIOUS) == WizardType.PREVIOUS){
			previousButton = new Button(buttonComposite, SWT.NONE);
			previousButton.setLayoutData(new RowData(80, 30));
			previousButton.setText(I18n.getString(DefaultWizardPanel.class.getSimpleName()+".dialog.previous"));
			previousButton.addSelectionListener(this);
		}
		if ((type & WizardType.NEXT) == WizardType.NEXT){
			nextButton = new Button(buttonComposite, SWT.NONE);
			nextButton.setLayoutData(new RowData(80, 30));
			nextButton.setText(I18n.getString(DefaultWizardPanel.class.getSimpleName()+".dialog.next"));
			nextButton.addSelectionListener(this);
		}
		
		if ((type & WizardType.CONNECT) == WizardType.CONNECT){
			connectButton = new Button(buttonComposite, SWT.NONE);
			connectButton.setLayoutData(new RowData(80, 30));
			connectButton.setText(I18n.getString(DefaultWizardPanel.class.getSimpleName()+".dialog.connect"));
			connectButton.addSelectionListener(this);
		}
		
		centerOnScreen();
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.widget == cancelButton){
			handleAction(WizardAction.CANCEL);
		}
		else if (e.widget == previousButton){
			handleAction(WizardAction.PREVIOUS);
		}
		else if (e.widget == nextButton){
			handleAction(WizardAction.NEXT);
		}
		else if (e.widget == connectButton){
			handleAction(WizardAction.CONNECT);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		
	}
	
	/**
	 * @return the connectButton
	 */
	public Button getConnectButton() {
		return connectButton;
	}
	
	protected abstract Composite createComposite(Shell shell);
	
	protected abstract void handleAction(WizardAction action);
}