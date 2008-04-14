/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.flash.internal;

import org.eclipse.actf.model.flash.FlashModelPlugin;
import org.eclipse.actf.model.flash.FlashPlayer;
import org.eclipse.actf.model.flash.bridge.IWaXcoding;
import org.eclipse.actf.model.flash.util.ASDeserializer;
import org.eclipse.actf.model.flash.util.ASSerializer;
import org.eclipse.swt.widgets.Display;



public class ASBridge {
    public static final String ROOTLEVEL_PATH = "_level0"; //$NON-NLS-1$
    public static final String BRIDGELEVEL_PATH = "_level53553"; //$NON-NLS-1$

	private FlashPlayer player;
    private String requestArgsPath;
    private String responseValuePath;

    private String contentId;
    private String secret;
    
    public static ASBridge getInstance(FlashPlayer player) {
    	if( "true".equals(player.getVariable(ROOTLEVEL_PATH+".Eclipse_ACTF_is_available")) ) { //$NON-NLS-1$ //$NON-NLS-2$
    		return new ASBridge(player,ROOTLEVEL_PATH);
    	}
    	else if( "true".equals(player.getVariable(BRIDGELEVEL_PATH+".Eclipse_ACTF_is_available")) ) { //$NON-NLS-1$ //$NON-NLS-2$
    		return new ASBridge(player,BRIDGELEVEL_PATH);
		}
    	return null;
    }

    private ASBridge(FlashPlayer player, String rootPath) {
    	this.player = player;
        this.requestArgsPath = rootPath + ".Eclipse_ACTF_request_args"; //$NON-NLS-1$
        this.responseValuePath = rootPath + ".Eclipse_ACTF_response_value"; //$NON-NLS-1$
        this.contentId = rootPath + ".Eclipse_ACTF_SWF_CONTENT_ID"; //$NON-NLS-1$
    }
    
    public Object invoke(Object[] args) {
        int counter = 0;
        try {
            if (secret == null) {
                initSecret();
                if (secret == null) return null;
            }
            player.setVariable(responseValuePath, ""); //$NON-NLS-1$
            String argsStr = ASSerializer.serialize(secret,args);
            player.setVariable(requestArgsPath, argsStr);
            while (++counter < 100) {
                String value = player.getVariable(responseValuePath);
                if( null==value ) return null;
                if (value.length() > 0) {
                    ASDeserializer asd = new ASDeserializer(value);
                    return asd.deserialize();
                }
                Display.getCurrent().readAndDispatch();//Yield.once();
            }
        }
        catch( Exception e ) {
        	e.printStackTrace();
        }
        finally {
        	if( 1 != counter ) {
                System.out.println("Warning: counter="+counter); //$NON-NLS-1$
                for( int i=0; i<args.length; i++ ) {
                    System.out.println("args["+i+"]="+args[i]); //$NON-NLS-1$ //$NON-NLS-2$
                }
        	}
        }
        return null;
    }
    
    
    private void initSecret() {
        try {
            String id = player.getVariable(contentId);
            if (id.length() == 0) return;
            IWaXcoding waxcoding = FlashModelPlugin.getDefault().getIWaXcoding();
            this.secret = waxcoding.getSecret(id,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
