/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.flash.proxy.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.internal.flash.proxy.Messages;
import org.eclipse.actf.model.internal.flash.proxy.ProxyPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


public class ProxyPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private List<Group> groups = new ArrayList<Group>();
    
    protected Group createFieldGroup(String name) {
        Group group = new Group(getFieldEditorParent(),SWT.NONE);
        if( null != name ) {
            group.setText(name);
        }
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        group.setLayoutData(data);
        groups.add(group);
        return group;
    }
    
    protected void adjustGridLayout() {
        super.adjustGridLayout();
        int numColumns = ((GridLayout)getFieldEditorParent().getLayout()).numColumns;
        for( int i=0; i< groups.size(); i++ ) {
            Group group = groups.get(i);
            GridLayout layout = (GridLayout)group.getLayout();
            GridData data = (GridData)group.getLayoutData(); 
            layout.numColumns = numColumns;
            layout.marginWidth = 5;
            layout.marginHeight = 5;
            data.horizontalSpan = numColumns;
        }
    }
    
    public ProxyPreferencePage() {
        super(GRID);
//        setDescription(Messages.getString("proxy.pref_description")); //$NON-NLS-1$
        setPreferenceStore(ProxyPlugin.getDefault().getPreferenceStore());
    }
    
    protected void createFieldEditors() {
        Group httpProxyGroup = createFieldGroup(Messages.proxy_pref_description); 
        addField(new RadioGroupFieldEditor(ProxyPreferenceConstants.P_PROXY_TYPE,
                Messages.proxy_type,1, 
                new String[][]{
                    {Messages.proxy_none,ProxyPreferenceConstants.PROXY_NONE}, 
                    {Messages.proxy_session,ProxyPreferenceConstants.PROXY_SESSION}, 
                    {Messages.proxy_global,ProxyPreferenceConstants.PROXY_GLOBAL} }, 
                httpProxyGroup));

        if( Platform.inDebugMode() ) {
            addField(new RadioGroupFieldEditor(ProxyPreferenceConstants.P_PROXY_SWF_METHOD,
                    Messages.proxy_swfmethod,1, 
                    new String[][]{
                        {Messages.proxy_swfmethod_none, ProxyPreferenceConstants.PROXY_SWF_METHOD_NONE}, 
                        {Messages.proxy_swfmethod_bootloader, ProxyPreferenceConstants.PROXY_SWF_METHOD_BOOTLOADER}/*, 
                        {Messages.getString("proxy.swfmethod.transcoder"), ProxyPreferenceConstants.PROXY_SWF_METHOD_TRANSCODER}*/ },
                    httpProxyGroup));
        }

        IntegerFieldEditor port = new IntegerFieldEditor(ProxyPreferenceConstants.PROXY_PORT, Messages.proxy_port, httpProxyGroup);
        port.setValidRange(0, 65535);
        addField(port);
        
        IntegerFieldEditor timeout = new IntegerFieldEditor(ProxyPreferenceConstants.P_TIMEOUT,
                                    Messages.proxy_timeout,httpProxyGroup); 
        timeout.setValidRange(0,99);
        addField(timeout);

        IntegerFieldEditor version = new IntegerFieldEditor(ProxyPreferenceConstants.P_SWF_MINIMUM_VERSION,
                Messages.proxy_swf_version,httpProxyGroup); 
        version.setValidRange(3,99);
        addField(version);
        
        
        addField(new RadioGroupFieldEditor(ProxyPreferenceConstants.P_CACHE_CLEAR,
                Messages.proxy_cache_clear,1, 
                new String[][]{
                    {Messages.proxy_cache_clear_when_startup, ProxyPreferenceConstants.CACHE_CLEAR_WHEN_STARTUP}, 
                    {Messages.proxy_confirm_cache_clear, ProxyPreferenceConstants.CONFIRM_WHEN_STARTUP}, 
                    {Messages.proxy_no_cache_clear, ProxyPreferenceConstants.NO_CACHE_CLEAR} }, 
                getFieldEditorParent(),true));
    }

    public void init(IWorkbench workbench) {
    }

}
