/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *******************************************************************************/


package org.eclipse.actf.model.ui.editors.ooo.editor;

import org.eclipse.actf.model.ui.editors.ooo.actions.OpenODFAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.part.EditorActionBarContributor;




public class OOoEditorContributor extends EditorActionBarContributor {

    public OOoEditorContributor() {
        super();
    }

    public void contributeToToolBar(IToolBarManager toolBarManager) {
        toolBarManager.add(new OpenODFAction());
//        toolBarManager.add(new EditODFAction());
//        toolBarManager.add(new SaveODFAction());
    }
}
