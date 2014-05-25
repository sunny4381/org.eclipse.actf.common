/*******************************************************************************
 * Copyright (c) 2007, 2014 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *    Kentarou FUKUDA (IBM) - [383882] - Eclipse 4.2 adaptation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ooo;

import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.editors.ooo.actions.OpenODFAction;
import org.eclipse.actf.model.ui.util.ModelServiceMessages;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

public class OOoEditorToolbar extends Composite {

	private Text _addressText;

	private boolean _isFocusOnText;

	private IModelService modelService;

	public OOoEditorToolbar(IModelService modelService, Composite parent,
			int style, boolean isEditor) {
		super(parent, style);

		this.modelService = modelService;

		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginBottom = 1;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.numColumns = 3;
		setLayout(gridLayout);

		initLayout(isEditor);
	}

	private void initLayout(boolean isEditor) {

		ToolBar toolBarL = new ToolBar(this, SWT.LEFT);
		ToolBarManager toolBarManagerL = new ToolBarManager(toolBarL);
		toolBarManagerL.add(new OpenODFAction());
		toolBarManagerL.update(true);

		/*
		Label addressLabel = new Label(this, SWT.NONE);
		addressLabel.setLayoutData(new GridData());
		addressLabel.setText(" " + ModelServiceMessages.WebBrowser_Address); //$NON-NLS-1$
		addressLabel.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent arg0) {
				if (arg0.stateMask == SWT.ALT
						&& (arg0.character == 'd' || arg0.character == 'D')) {
					_addressText.setFocus();
					_addressText.selectAll();
				}
			}
		});
		*/

		this._addressText = new Text(this, SWT.SINGLE | SWT.BORDER);
		GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		// gridData.widthHint = 1024;
		_addressText.setLayoutData(gridData);
		this._addressText.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent arg0) {
				_isFocusOnText = false;
			}

			public void focusGained(FocusEvent arg0) {
				_addressText.selectAll();
			}
		});
		
		_addressText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent arg0) {
				if (arg0.stateMask == SWT.ALT
						&& (arg0.character == 'd' || arg0.character == 'D')) {
					_addressText.setFocus();
					_addressText.selectAll();
				}
			}
		});

		this._addressText.addMouseListener(new MouseAdapter() {
			public void mouseUp(MouseEvent arg0) {
				if (!_isFocusOnText) {
					_addressText.selectAll();
					_isFocusOnText = true;
				}
			}

			public void mouseDoubleClick(MouseEvent e) {
				_addressText.selectAll();
			}
		});

		this._addressText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.CR) {
					// BrowserEventListenerManager.getInstance().fireNavigate(_addressText.getText());
					modelService.open(_addressText.getText());
				}
			}

			public void keyReleased(KeyEvent e) {
			}
		});

		ToolBar toolBar = new ToolBar(this, SWT.RIGHT);
		ToolBarManager toolBarManager = new ToolBarManager(toolBar);

		Action openAction = new Action(ModelServiceMessages.WebBrowser_Go,
				OOoEditorPlugin.imageDescriptorFromPlugin(
						"org.eclipse.actf.model.ui", "icons/browser/go.png")) {//$NON-NLS-1$ //$NON-NLS-2$
			public void run() {
				if (modelService != null && _addressText != null) {
					modelService.open(_addressText.getText());
				}
			}
		};
		openAction.setToolTipText(ModelServiceMessages.WebBrowser_Go_tp);

		ActionContributionItem navigateAction = new ActionContributionItem(
				openAction);
		navigateAction.setMode(ActionContributionItem.MODE_FORCE_TEXT);
		toolBarManager.add(navigateAction);

		toolBarManager.update(true);

		// _compositeParent.getShell().setDefaultButton(searchButton);
	}

	public Text getAddressText() {
		return this._addressText;
	}
}
