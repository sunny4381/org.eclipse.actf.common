/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/


package org.eclipse.actf.model.internal.ui.editors.ie;

import org.eclipse.actf.model.ui.ModelUIPlugin;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.util.ModelServiceMessages;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class WebBrowserToolbar extends Composite {

    // TODO move to base plugin

    private Text _addressText;

    private boolean _isFocusOnText;

    private IWebBrowserACTF browser;

    public WebBrowserToolbar(IWebBrowserACTF browser, Composite parent, int style) {
        super(parent, style);

        this.browser = browser;
        
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginBottom = 1;
        gridLayout.verticalSpacing = 0;
        gridLayout.marginWidth = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.numColumns = 3;
        setLayout(gridLayout);

        initLayout();
    }

    private void initLayout() {
        Label addressLabel = new Label(this, SWT.NONE);
        addressLabel.setLayoutData(new GridData());
        addressLabel.setText(" " + ModelServiceMessages.getString("WebBrowser.Address")); //$NON-NLS-1$ //$NON-NLS-2$
        addressLabel.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent arg0) {
                if (arg0.stateMask == SWT.ALT && (arg0.character == 'd' || arg0.character == 'D')) {
                    _addressText.setFocus();
                    _addressText.selectAll();
                }
            }
        });

        this._addressText = new Text(this, SWT.SINGLE | SWT.BORDER);
        GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        gridData.widthHint = 1024;
        _addressText.setLayoutData(gridData);
        this._addressText.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent arg0) {
                _isFocusOnText = false;
                if (browser != null) {
                    WebBrowserEventExtension.focusLostOfAddressText(browser);
                }
            }

            public void focusGained(FocusEvent arg0) {
                _addressText.selectAll();
                if (browser != null) {
                    WebBrowserEventExtension.focusGainedOfAddressText(browser);
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

        // TODO use action

        this._addressText.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.character == SWT.CR) {
                    if (browser != null) {
                        browser.open(_addressText.getText());
                        // TODO: Remaining the focus on the address field causes navigation problem on aiBrowser
                        // As a makeshift, we move the focus to the toolbar parent but we should reconsider
                        // this treatment.
                        WebBrowserToolbar.this.forceFocus();
                    }
                }
            }

            public void keyReleased(KeyEvent e) {
            }
        });

        Button searchButton = new Button(this, SWT.NULL);
        searchButton.setLayoutData(getButtonsGridData());
        searchButton.setText(ModelServiceMessages.getString("WebBrowser.Go")); //$NON-NLS-1$
        searchButton.setToolTipText(ModelServiceMessages.getString("WebBrowser.Go_tp")); //$NON-NLS-1$
        //TODO
        searchButton.setImage(BrowserIE_Plugin.imageDescriptorFromPlugin(ModelUIPlugin.PLUGIN_ID, "icons/browser/go.png").createImage());
        searchButton.addMouseListener(new MouseAdapter() {
            public void mouseUp(MouseEvent e) {
                if (browser != null) {
                    browser.open(_addressText.getText());
                }
            }
        });
        // _compositeParent.getShell().setDefaultButton(searchButton);
    }

    private GridData getButtonsGridData() {
        GridData gridData = new GridData();
        gridData.heightHint = 32;
        gridData.widthHint = 32;
        gridData.horizontalAlignment = GridData.HORIZONTAL_ALIGN_CENTER;

        return gridData;
    }

    public String getAddressTextString() {
        return _addressText.getText();
    }
    
    public void setAddressTextString(String targetS) {
        _addressText.setText(targetS);
    }
    
    public void setFocusToAddressText(boolean selectAll) {
        if (!isVisible()) setVisible(true);
        _addressText.setFocus();
        if (selectAll) {
            _addressText.selectAll();
        } else {
            _addressText.setSelection(0);
        }
    }

    public void showAddressText(boolean flag) {
        this.setVisible(flag);
    }

}
