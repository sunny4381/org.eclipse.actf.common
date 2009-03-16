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

package org.eclipse.actf.model.internal.ui.editors.ooo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.actf.model.dom.odf.ODFParser;
import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.util.converter.ODFConverter;
import org.eclipse.actf.model.dom.odf.util.converter.ODFConverterCreator;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.ODFException;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.OOoNavigation;
import org.eclipse.actf.model.ui.IModelService;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.IModelServiceScrollManager;
import org.eclipse.actf.model.ui.ImagePositionInfo;
import org.eclipse.actf.model.ui.editors.ooo.initializer.util.OOoEditorInitUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class OOoComposite extends Composite implements IModelService {
	private OOoWindowComposite _oOoWindowComposite = null;

	private Composite _comp;

	private OOoEditorScrollManager scrollManager;

	OOoEditorToolbar _toolbar;

	private String lastURL = null;

	private String title = ""; //$NON-NLS-1$
	
	private IModelServiceHolder holder;

	public OOoComposite(Composite parent, int style, IModelServiceHolder holder) {
		super(parent, style);
		if (OOoEditorInitUtil.isOOoInstalled(true)) {
			init();
		}
		this.holder = holder;
	}

	private void init() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		setLayout(gridLayout);

		this._toolbar = new OOoEditorToolbar(this, this, SWT.NONE, true);
		this._comp = new Composite(this, SWT.EMBEDDED);
		this._comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		try {
			this._comp.setLayout(gridLayout);
			this._oOoWindowComposite = new OOoWindowComposite(this._comp,
					SWT.EMBEDDED);
			this._oOoWindowComposite.setLayoutData(new GridData(SWT.FILL,
					SWT.FILL, true, true));
			scrollManager = new OOoEditorScrollManager(this._oOoWindowComposite);
		} catch (ODFException e) {
			e.printStackTrace();
		}
	}

	public void dispose() {
		super.dispose();
		// this._oOoBeanPanel.close();
	}

	public String[] getSupportMIMETypes() {
		return MIMETYPES_ODF;
	}

	public String[] getSupportExtensions() {
		return EXTS_ODF;
	}

	public void open(String url) {
		if (null != url && null != this._oOoWindowComposite) {
			// move this url format to OOoWindowComposite::load
			/*
			 * if (0 != url.indexOf("private:") && 0 != url.indexOf("file:///")) {
			 * try { File srcFile = new File(url); StringBuffer sb = new
			 * StringBuffer("file:///");
			 * sb.append(srcFile.getCanonicalPath().replace('\\', '/')); url =
			 * sb.toString(); if (!srcFile.canRead()) { return; } } catch
			 * (IOException ioe) { ioe.printStackTrace(); return; } }
			 */
			File file = new File(url);
			if (file.exists()) {
				this._oOoWindowComposite.open(url);
				this._toolbar.getAddressText().setText(url);

				// ODFBrowserEditor odfBrowserEditor =
				// ODFBrowserEditorManager.getODFEditorPart(url);
				// odfBrowserEditor.changeTitle(url);

				titleChange(file.getName());
			}
		}
	}

	public void open(File target) {
		// TODO Auto-generated method stub

	}

	public String getURL() {
		if (null != _oOoWindowComposite) {
			return this._oOoWindowComposite.getUrl();
		}
		return ""; //$NON-NLS-1$
	}

	public List<Element> getElemList(Node root) {
		List<Element> elemList = new ArrayList<Element>();

		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				elemList.add((Element) node);
			}
			if (node.hasChildNodes()) {
				elemList.addAll(getElemList(node));
			}
		}
		return elemList;
	}

	public Document getDocument() {
		String currentURL = getURL();
		if (currentURL == null)
			return null;
		OOoNavigation oooNavigation = _oOoWindowComposite.getOooNavigation();
		ODFDocument odfDoc = oooNavigation.getContentXML();
		if ((odfDoc != null) && (currentURL.equals(lastURL)))
			return odfDoc;

		// create ODF Document by ODF API
		ODFParser odfParser = new ODFParser();
		odfDoc = odfParser.getDocument(currentURL);
		// set ID to each ODF element
		/*
		 * NodeIterator nodeIter = ((DocumentTraversal)
		 * odfDoc).createNodeIterator(odfDoc, NodeFilter.SHOW_ELEMENT, null,
		 * false); int contentID = 0; Node node = nodeIter.nextNode(); while
		 * (node != null) { ((Element)
		 * node).setAttribute(ODFConstants.ODF_CONTENT_ID, new
		 * Integer(contentID).toString()); node = nodeIter.nextNode();
		 * contentID++; }
		 */
		int contentID = 0;
		List<Element> elemList = getElemList(odfDoc.getDocumentElement());
		for (int i = 0; i < elemList.size(); i++) {
			Element elem = elemList.get(i);
			elem.setAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID,
					new Integer(contentID).toString());
			contentID++;
		}

		oooNavigation.setContentXML(odfDoc);
		lastURL = currentURL;
		return odfDoc;
	}

	public Document getLiveDocument() {
		return null;
	}

	public Composite getTargetComposite() {
		return _comp;
	}

	public File saveDocumentAsHTMLFile(String file) {
		if (null == file)
			return null;

		// TODO
		// setStatusMessage(OdfPlugin.getResourceString("ODFEditor.convertHTML"));

		// TODO file check
		ODFDocument odfDoc = (ODFDocument) getDocument();
		ODFConverter odfConverter = ODFConverterCreator.createHTMLConverter();
		odfConverter.setDocument(odfDoc);
		odfConverter.convertDocument(file, false);
		return (new File(file));
	}

	public IModelServiceScrollManager getScrollManager() {
		return scrollManager;
	}

	public void jumpToNode(Node target) {
		if (null != target) {
			new OOoNavigationThread(target).start();
		}
	}

	private class OOoNavigationThread extends Thread {

		private Node _targetNode;

		public OOoNavigationThread(Node targetNode) {
			this._targetNode = targetNode;
		}

		public void run() {
			try {
				_oOoWindowComposite.getOooNavigation().jumpToProblemPosition(
						_targetNode);
			} catch (ODFException odfe) {
				odfe.printStackTrace();
			}
		}
	}

	public String getCurrentMIMEType() {
		// TODO return actual MIME type
		return MIMETYPES_ODF[0];
	}

	private void titleChange(String title) {
		this.title = title;
		holder.setEditorTitle(title);
	}

	public Object getAttribute(String name) {
		// do nothing
		return null;
	}

	public String getID() {
		return OOoComposite.class.getName() + ":" + this; //$NON-NLS-1$
	}

	public String getTitle() {
		return title;
	}

	public File saveOriginalDocument(String file) {
		// TODO impl
		return null;
	}

	public ImagePositionInfo[] getAllImagePosition() {
		// TODO impl
		return new ImagePositionInfo[0];
	}

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.IModelService#getModelServiceHolder()
	 */
	public IModelServiceHolder getModelServiceHolder() {
		return holder;
	}

}
