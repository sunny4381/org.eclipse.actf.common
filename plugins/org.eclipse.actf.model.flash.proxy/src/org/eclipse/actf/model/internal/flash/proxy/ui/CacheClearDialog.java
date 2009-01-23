/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.flash.proxy.ui;

import org.eclipse.actf.model.internal.flash.proxy.Messages;
import org.eclipse.actf.model.internal.flash.proxy.preferences.ProxyPreferenceConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;




public class CacheClearDialog extends Dialog {

    private String status;
    
    private String appName;

    public CacheClearDialog(Shell shell, String appName) {
        super(shell);
        this.appName = appName;
    }

    protected Control createDialogArea(Composite parent) {
        
        parent.getShell().setText(Messages.proxy_dialog_text);
        
        Composite composite = new Composite(parent, SWT.NULL);

        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_END);
        composite.setLayoutData(gridData);

        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        layout.verticalSpacing = 20;
        layout.marginWidth = 20;
        layout.marginHeight = 20;
        composite.setLayout(layout);
        
//        String settings = ProxyPlugin.getDefault().getPreferenceStore().getString(
//                ProxyPreferenceConstants.P_CACHE_CLEAR);
        
        Label label1 = new Label(composite, SWT.NONE | SWT.READ_ONLY);
        label1.setText("    "+appName+" "+Messages.proxy_cache_confirmation1);

        Font f = label1.getFont();
        Font f9b = new Font(f.getDevice(), f.getFontData()[0].getName(), 9, SWT.BOLD);
        Font f9 = new Font(f.getDevice(), f.getFontData()[0].getName(), 9, SWT.NORMAL);
        label1.setFont(f9b);
        
        new Label(composite, SWT.NONE);
        
        status = ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP_AND_CACHE_CLEAR;

        createRadioButton(ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP_AND_CACHE_CLEAR, //
                Messages.proxy_cache_clear_when_startup_and_cache_clear, composite, true).setFont(f9b);
        createRadioButton(ProxyPreferenceConstants.CONFIRM_AND_CACHE_CLEAR, //
                Messages.proxy_confirm_and_cache_clear, composite, false).setFont(f9);
        createRadioButton(ProxyPreferenceConstants.CONFIRM_AND_NO_OPERATION, //
                Messages.proxy_confirm_and_no_operation, composite, false).setFont(f9);

        return composite;
    }

    private Button createRadioButton(final String id, String text, Composite parent, boolean flag) {
        Button radio = new Button(parent, SWT.RADIO);
        radio.setText(text);
        radio.setSelection(flag);

        radio.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                status = id;
            }

            public void widgetSelected(SelectionEvent e) {
                status = id;
            }
        });
        return radio;
    }

    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    protected void buttonPressed(int buttonId) {
        if (buttonId == IDialogConstants.OK_ID) {
            close();
        }
    }

    public String getSelection() {
        return status;
    }

    protected void handleShellCloseEvent() {
        this.status = ProxyPreferenceConstants.CONFIRM_AND_NO_OPERATION;
        super.handleShellCloseEvent();
    }
}
