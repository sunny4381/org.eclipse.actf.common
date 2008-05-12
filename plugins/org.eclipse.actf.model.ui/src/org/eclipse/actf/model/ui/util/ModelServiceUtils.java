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

package org.eclipse.actf.model.ui.util;

import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.editor.DummyEditorInput;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.util.DebugPrintUtil;
import org.eclipse.actf.util.ui.PlatformUIUtil;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;




public class ModelServiceUtils {

    private static class EditorNotFoundException extends Exception {
        private static final long serialVersionUID = -5760127077107164112L;

    }

    public static IEditorPart launch(String targetUrl) {
        IWorkbenchPage activePage = PlatformUIUtil.getActivePage();
        if (activePage == null) {
            return null;
        }

        if (targetUrl != null && targetUrl.length() != 0) {
            IEditorReference[] editorRefs = activePage.getEditorReferences();
            for (int i = 0; i < editorRefs.length; i++) {
                IWorkbenchPart part = editorRefs[i].getPart(false);
                if (part instanceof IModelServiceHolder) {
                    IModelService modelService = ((IModelServiceHolder) part).getModelService();
                    if (targetUrl.equals(modelService.getURL())) {
                        activePage.activate(part);
                        // reload?
                        return null;
                    }
                }
            }

            targetUrl = targetUrl.trim();

            try {
                return(launch(targetUrl, getEditorId(targetUrl)));
            } catch (EditorNotFoundException e) {
                System.err.println("Editor not found: " + targetUrl);
            }
        }
        return null;
    }

    private static String getEditorId(String targetUrl) throws EditorNotFoundException {
        // TODO support multiple editor, dialog, preference
        IEditorRegistry editors = PlatformUI.getWorkbench().getEditorRegistry();
        IEditorDescriptor editor;

        if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
            editor = editors.getDefaultEditor(targetUrl);
            if (editor == null) {
                editor = editors.getDefaultEditor("default.html");
            }
        } else {
            editor = editors.getDefaultEditor("default.html");

        }
        if (editor == null) {
            throw new EditorNotFoundException();
        }

        return (editor.getId());
    }

    public static IEditorPart launch(String targetUrl, String id) {

        // TODO check current url
        IWorkbenchPage activePage = PlatformUIUtil.getActivePage();
        if (activePage == null) {
            return null;
        }
        if (targetUrl != null) {
            targetUrl = targetUrl.trim();
        }else{
            targetUrl = "";
        }

        IEditorPart blankEditorPart = getBlankBrowserEditorPart(id);
        if (blankEditorPart != null) {
            activePage.activate(blankEditorPart);
            if (targetUrl != null) {
                ((IModelServiceHolder) blankEditorPart).getModelService().open(targetUrl);
                return blankEditorPart;
            }
        } else {
            IEditorReference[] editorRefs = activePage.getEditorReferences();
            for (int i = 0; i < editorRefs.length; i++) {
                if (editorRefs[i].getId().equals(id)) {
                    IWorkbenchPart part = editorRefs[i].getPart(false);
                    if (part instanceof IModelServiceHolder) {
                        IModelService modelService = ((IModelServiceHolder) part).getModelService();
                        if (targetUrl.equals(modelService.getURL())) {
                            activePage.activate(part);
                            return (IEditorPart)part;
                        }
                    }
                }
            }

            try {
                String editorName = "";
                IEditorRegistry editors = PlatformUI.getWorkbench().getEditorRegistry();
                IEditorDescriptor editorDesc = editors.findEditor(id);
                if (editorDesc != null) {
                    editorName = editorDesc.getLabel();
                    return activePage.openEditor(
                            new DummyEditorInput(targetUrl, editorName), id);
                } else {
                    System.err.println("Editor not found: " + id);
                }
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static IEditorPart getBlankBrowserEditorPart(String id) {
        IEditorReference[] editors = PlatformUIUtil.getActivePage()
                .getEditorReferences();
        for (int i = 0; i < editors.length; i++) {
            if (editors[i].getId().equals(id)) {
                IWorkbenchPart part = editors[i].getPart(false);
                if (part instanceof IModelServiceHolder) {
                    IModelService modelService = ((IModelServiceHolder) part).getModelService();
                    System.out.println(modelService.getURL());
                    if (modelService.getURL() == null)
                        return (IEditorPart)part;
                    if (modelService instanceof IWebBrowserACTF
                            && ("about:blank".equals(modelService.getURL()) || ("".equals(modelService.getURL()))))
                        return (IEditorPart)part;
                }
            }
        }
        return null;
    }

    public static void openInExistingEditor(String targetUrl) {
        targetUrl = targetUrl.trim();
        String editorId;

        try {
            editorId = getEditorId(targetUrl);
        } catch (EditorNotFoundException e) {
            System.err.println("Editor not found: " + targetUrl);
            return;
        }
        IWorkbenchPage activePage = PlatformUIUtil.getActivePage();
        if (activePage == null) {
            return;
        }
        if (activateEditorPart(editorId)) {
            IEditorPart editor = activePage.getActiveEditor();
            if (editor instanceof IModelServiceHolder) {
                ((IModelServiceHolder) editor).getModelService().open(targetUrl);
            } else {
                launch(targetUrl);
            }
        } else {
            launch(targetUrl);
        }

    }

    public static boolean activateEditorPart(String id) {
        if (id != null) {
            IWorkbenchPage activePage = PlatformUIUtil.getActivePage();
            if (activePage == null) {
                return false;
            }
            IEditorPart editor = activePage.getActiveEditor();
            if (editor != null && editor.getSite().getId().equals(id)) {
                return true;
            }

            IEditorReference[] editors = activePage.getEditorReferences();
            for (int i = 0; i < editors.length; i++) {
                System.out.println(editors[i].getId());
                if (editors[i].getId().equals(id)) {
                    editor = editors[i].getEditor(false);
                    if (editor != null) {
                        activePage.activate(editor);
                        return true;
                    }
                }
            }
        }

        return false;
    }

	public static IModelServiceHolder getActiveModelServiceHolder() {
	    IEditorPart editor = PlatformUIUtil.getActiveEditor();
	    if (editor != null && editor instanceof IModelServiceHolder) {
	        return (IModelServiceHolder) editor;
	    }
	
	    DebugPrintUtil.devOrDebugPrintln("editor: " + editor + " isn't IModelServiceHolder");
	
	    return null;
	}

	public static IModelService getActiveModelService() {
		IModelServiceHolder holder = getActiveModelServiceHolder();
		if (holder != null) {
		    return (holder.getModelService());
		}
		return null;
	}

}
