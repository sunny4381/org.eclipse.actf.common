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

package org.eclipse.actf.model.ui.editors.ie;

import org.eclipse.actf.model.internal.ui.editors.ie.WebBrowserIEImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.INewWiondow2EventListener;
import org.eclipse.actf.model.internal.ui.editors.ie.events.IWindowClosedEventListener;
import org.eclipse.actf.model.internal.ui.editors.ie.events.NewWindow2Parameters;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.editor.DummyEditorInput;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.WebBrowserEventUtil;
import org.eclipse.actf.model.ui.util.ModelServiceUtils;
import org.eclipse.actf.ui.util.PlatformUIUtil;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPathEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * The Editor implementation to open Web content by using Internet Explorer. It
 * also provide access to HTML DOM via {@link IModelService}. The model service
 * also implements {@link IWebBrowserACTF}.
 * 
 * @see IModelServiceHolder
 * @see IModelService
 * @see IWebBrowserACTF
 */
public class WebBrowserEditor extends EditorPart implements IModelServiceHolder {

	private static final String ABOUT_BLANK = "about:blank"; //$NON-NLS-1$

	/**
	 * ID of this Editor
	 */
	public static final String ID = WebBrowserEditor.class.getName();

	WebBrowserIEImpl webBrowser;

	IEditorInput input;

	/**
	 * Creates a new Internet Explorer Editor.
	 */
	public WebBrowserEditor() {
		super();
	}

	public void createPartControl(Composite parent) {
		String targetUrl = ABOUT_BLANK;
		if (input instanceof DummyEditorInput) {
			targetUrl = ((DummyEditorInput) input).getUrl();
			if ("".equals(targetUrl)) { //$NON-NLS-1$
				targetUrl = ABOUT_BLANK;
			}
		}else if(input instanceof IPathEditorInput){
			targetUrl = ((IPathEditorInput)input).getPath().toFile().getAbsolutePath();
		}

		webBrowser = new WebBrowserIEImpl(this, parent, targetUrl);
		webBrowser.setNewWindow2EventListener(new INewWiondow2EventListener() {

			public void newWindow2(NewWindow2Parameters param) {
				IEditorPart newEditor = ModelServiceUtils.launch(ABOUT_BLANK,
						ID);
				if (newEditor instanceof WebBrowserEditor) {
					IWebBrowserACTF browser = (IWebBrowserACTF) ((WebBrowserEditor) newEditor)
							.getModelService();
					param.setBrowserAddress(browser.getBrowserAddress());
					WebBrowserEventUtil.newWindow(browser);
				} else {
					// TODO
				}
			}
		});

		webBrowser
				.setWindowClosedEventListener(new IWindowClosedEventListener() {
					public void windowClosed() {
						IWorkbenchPage page = PlatformUIUtil.getActivePage();
						if (page != null) {
							IEditorReference[] editorRefs = page
									.getEditorReferences();
							for (IEditorReference i : editorRefs) {
								if (WebBrowserEditor.this == i.getEditor(false)) {
									PlatformUIUtil.getActivePage().closeEditor(
											WebBrowserEditor.this, false);
								}
							}
						}
					}
				});

	}

	public void dispose() {
		WebBrowserEventUtil.browserDisposed(webBrowser, getPartName());
	}

	public void setFocus() {
		WebBrowserEventUtil.getFocus(webBrowser);
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO
	}

	public void doSaveAs() {
		// TODO
	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		this.input = input;
	}

	public boolean isDirty() {
		return false;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public IModelService getModelService() {
		return (this.webBrowser);
	}

	public IEditorPart getEditorPart() {
		return this;
	}

	public void setEditorTitle(String title) {
		setPartName(title);
	}

}
