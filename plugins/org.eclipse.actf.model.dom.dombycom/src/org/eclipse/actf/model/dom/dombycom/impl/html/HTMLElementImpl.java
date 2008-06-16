/*******************************************************************************
 * Copyright (c) 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.dombycom.impl.html;

import org.eclipse.actf.model.dom.dombycom.impl.DocumentImpl;
import org.eclipse.actf.model.dom.dombycom.impl.NodeImpl;
import org.eclipse.actf.util.win32.comclutch.IDispatch;
import org.w3c.dom.html.HTMLElement;

public class HTMLElementImpl extends ElementImpl implements HTMLElement {

	private static final String TITLE = "title";
	private static final String LANG = "lang";
	private static final String ID = "id";
	private static final String DIR = "dir";
	private static final String CLASS = "class";

	protected HTMLElementImpl(NodeImpl baseNode, IDispatch inode) {
		super(baseNode, inode);
	}

	protected HTMLElementImpl(DocumentImpl baseDoc, IDispatch inode) {
		super(baseDoc, inode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#getClassName()
	 */
	public String getClassName() {
		return getAttribute(CLASS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#getDir()
	 */
	public String getDir() {
		return getAttribute(DIR);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#getId()
	 */
	public String getId() {
		return getAttribute(ID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#getLang()
	 */
	public String getLang() {
		return getAttribute(LANG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#getTitle()
	 */
	public String getTitle() {
		return getAttribute(TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#setClassName(java.lang.String)
	 */
	public void setClassName(String className) {
		setAttribute(CLASS, className);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#setDir(java.lang.String)
	 */
	public void setDir(String dir) {
		setAttribute(DIR, dir);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#setId(java.lang.String)
	 */
	public void setId(String id) {
		setAttribute(ID, id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#setLang(java.lang.String)
	 */
	public void setLang(String lang) {
		setAttribute(LANG, lang);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.w3c.dom.html.HTMLElement#setTitle(java.lang.String)
	 */
	public void setTitle(String title) {
		setAttribute(TITLE, title);
	}

}
