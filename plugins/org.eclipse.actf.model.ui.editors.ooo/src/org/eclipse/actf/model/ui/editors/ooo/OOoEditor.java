/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Norimasa HAYASHIDA - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.editors.ooo;

import org.eclipse.actf.model.internal.ui.editors.ooo.OOoComposite;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.editor.DummyEditorInput;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

/**
 * The Editor implementation to open OpenDocument Format (ODF) files by using
 * OpenOffice.org. It also provide access to model via {@link IModelService}.
 * 
 * @see IModelServiceHolder
 * @see IModelService
 */
public class OOoEditor extends EditorPart implements IModelServiceHolder {

	/**
	 * ID of this Editor
	 */
	public static final String ID = OOoEditor.class.getName();

	private OOoComposite _odfBrowser;

	private IEditorInput input;

	/**
	 * Creates a new OpenOffice.org Editor.
	 */
	public OOoEditor() {
		super();
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		this.input = input;
	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void createPartControl(Composite parent) {
		String targetUrl = null;
		if (input instanceof DummyEditorInput) {
			targetUrl = ((DummyEditorInput) input).getUrl();
			if ("".equals(targetUrl)) {
				targetUrl = null;
			}
		}

		this._odfBrowser = new OOoComposite(parent, SWT.NONE, this);
		if (targetUrl != null) {
			_odfBrowser.open(targetUrl);
		}
	}

	public void setFocus() {
		// TODO Auto-generated method stub

	}

	public IModelService getModelService() {
		return this._odfBrowser;
	}

	public IEditorPart getEditorPart() {
		return this;
	}

	public void dispose() {
		this._odfBrowser.dispose();
	}

	public void setEditorTitle(String title) {
		setPartName(title);
		setInputWithNotify(new DummyEditorInput(_odfBrowser.getURL(), input
				.getName()));
	}
}
