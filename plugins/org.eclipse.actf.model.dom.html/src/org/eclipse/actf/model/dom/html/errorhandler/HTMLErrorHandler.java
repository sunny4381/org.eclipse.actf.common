/*******************************************************************************
 * Copyright (c) 1998, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Goh KONDOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.html.errorhandler;

import java.io.IOException;

import org.eclipse.actf.model.dom.sgml.ElementDefinition;
import org.eclipse.actf.model.dom.sgml.EndTag;
import org.eclipse.actf.model.dom.sgml.ParseException;
import org.eclipse.actf.model.dom.sgml.SGMLParser;
import org.eclipse.actf.model.dom.sgml.SGMLText;
import org.eclipse.actf.model.dom.sgml.errorhandler.IErrorHandler;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class HTMLErrorHandler implements IErrorHandler {
	/**
	 * Searches proper a parent node of node. Behavier is described as follows.
	 * <ol>
	 * <li> If <code>node</code> is LINK, STYLE BASE, ISINDEX or META element,
	 * add it to last HEAD element.
	 * <li> If context and <code>node</code> are equal nodes, Changes Context
	 * to its parent and append <code>node</code> to context. <!--
	 * <li> If context is HTML element,
	 * <ol>
	 * <li>If <code>node<code> is BODY
	 * element, append <code>node</code> to context and change it to
	 * <code>node</code> (This strange case occurs when frameset dtd is used).
	 * <li> If context has only a HEAD element, makes BODY element as
	 * a next sibling of the HEAD and appends <code>node</code> and changes 
	 * context to it.
	 * </ol>
	 * -->
	 * </ol>
	 * @param node illegal child node.
	 * @return true if found.  Otherwise false.
	 * @see org.eclipse.actf.model.dom.sgml.SGMLParser#getContext()
	 * @see org.eclipse.actf.model.dom.sgml.SGMLParser#setContext(org.w3c.dom.Element)
	 */
	public boolean handleError(int code, SGMLParser parser, Node errorNode)
			throws ParseException, IOException, SAXException {
		if (code == ILLEGAL_ATTRIBUTE) {
			return false;
		}
		String nodeName = errorNode.getNodeName();
		Element context = parser.getContext();
		Node contextParent = context.getParentNode();
		String contextName = context.getNodeName();
		NodeList bodies;
		if (errorNode instanceof Element) {
			ElementDefinition ed = parser.getDTD().getElementDefinition(
					nodeName);
			if (ed == null) {
				return false;
			}
			if (nodeName.equalsIgnoreCase("LINK")
					|| nodeName.equalsIgnoreCase("STYLE")
					|| nodeName.equalsIgnoreCase("META")
					|| nodeName.equalsIgnoreCase("BASE")
					|| nodeName.equalsIgnoreCase("ISINDEX")) {
				Element html = parser.getDocument().getDocumentElement();
				for (Node child = html.getLastChild(); child != null; child = child
						.getPreviousSibling()) {

					// System.out.println("ErrorHandler: "+child.getNodeName());

					if (child instanceof Element
							&& child.getNodeName().equalsIgnoreCase("HEAD")) {
						child.insertBefore(errorNode, null);
						parser.error(code, errorNode + " must be under "
								+ child);
						return true;
					}
				}
			} else if (nodeName.equalsIgnoreCase("BODY")) {
				Element top = parser.getDocument().getDocumentElement();
				for (Node child = top.getFirstChild(); child != null; child = child
						.getNextSibling()) {
					if (child instanceof Element
							&& (child.getNodeName().equalsIgnoreCase("BODY") || child
									.getNodeName().equalsIgnoreCase("FRAMESET"))) {
						return false;
					}
				}
				top.insertBefore(errorNode, null);
				parser.setContext((Element) errorNode);
				parser.error(code, errorNode + " must be under " + top);
				return true;
			} else if (nodeName.equalsIgnoreCase("HEAD")) {
				Document doc = parser.getDocument();
				if (context.getElementsByTagName("HEAD").getLength() == 0
						&& ((bodies = doc.getElementsByTagName("BODY"))
								.getLength() > 0 || (bodies = doc
								.getElementsByTagName("FRAMESET")).getLength() > 0)) {
					Element body = (Element) bodies.item(0);
					body.getParentNode().insertBefore(errorNode, body);
					parser.setContext((Element) errorNode);
					parser.error(code, errorNode + " must be before " + body);
					return true;
				} else {
					if (doc.getElementsByTagName("BODY").getLength() > 0
							|| doc.getElementsByTagName("FRAMESET").getLength() > 0) {
						parser.error(code,
								"HTMLErrorHandler makes parser ignore "
										+ errorNode);
						return true;
					}
				}
			} else if (contextName.equalsIgnoreCase("HTML")) {
				for (Node child = context.getLastChild(); child != null; child = child
						.getPreviousSibling()) {
					if (child instanceof Element
							&& child.getNodeName().equalsIgnoreCase("BODY")) {
						parser.error(code,
								"BODY context is already closed.  Reopen it.");
						parser.reopenContext(1);
						parser.getContext().insertBefore(errorNode, null);
						parser.setContext((Element) errorNode);
						return true;
					} else if (child instanceof Comment
							|| child instanceof ProcessingInstruction
							|| child instanceof Text
							&& whitespaceText((Text) child)) {
						continue;
					} else {
						break;
					}
				}
			} else if (nodeName.equalsIgnoreCase(contextName)
					&& ed.endTagOmittable() && contextParent instanceof Element) {
				// parser.setContext((Element)contextParent);
				contextParent.insertBefore(errorNode, null);
				parser.setContext((Element) errorNode);
				return true;
			}
		} else if (code == SUDDEN_ENDTAG) {
			try {
				java.util.Vector missedEndtags = (java.util.Vector) parser
						.getExtraErrInfo();
				for (java.util.Enumeration e = missedEndtags.elements(); e
						.hasMoreElements();) {
					EndTag missedEtag = (EndTag) e.nextElement();
					String missedEtagName = missedEtag.getNodeName();
					if (missedEtagName.equalsIgnoreCase("TABLE")) {
						// ignore the endtag
						return true;
					} else if (keepForm
							&& missedEtagName.equalsIgnoreCase("FORM")
							&& (nodeName.equalsIgnoreCase("TD") || nodeName
									.equalsIgnoreCase("TR"))) {
						// TD is not able to close FORM
						return true;
					}
				}
			} catch (ClassCastException e) {
			}
		}
		return false;
	}

	private boolean keepForm = true;

	void setKeepForm(boolean keep) {
		this.keepForm = keep;
	}

	private boolean whitespaceText(Text text) {
		if (text instanceof SGMLText) {
			return ((SGMLText) text).getIsWhitespaceInElementContent();
		}
		char str[] = text.getData().toCharArray();
		for (int i = 0; i < str.length; i++) {
			if (!Character.isWhitespace(str[i]))
				return false;
		}
		return true;
	}
}
