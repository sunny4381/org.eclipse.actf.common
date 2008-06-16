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
package org.eclipse.actf.util.dom;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLTitleElement;
import org.w3c.dom.traversal.NodeFilter;

public class DomPrintUtil {

	private static final String LINE_SEP = System.getProperty("line.separator");
	private static final String LT = "<";
	private static final String GT = ">";
	private static final String AMP = "&";
	private static final String QUAT = "\"";
	private static final String SINGLE_QUAT = "'";

	private static final String ESC_LT = "&lt;";
	private static final String ESC_GT = "&gt;";
	private static final String ESC_AMP = "&amp;";

	private Document document;
	private int whatToShow = NodeFilter.SHOW_ALL;
	private NodeFilter nodeFilter = null;
	private boolean entityReferenceExpansion = false;

	private boolean indent = true;
	private boolean escapeTagBracket = false;

	private AttributeFilter attrFilter = new AttributeFilter() {
		public boolean acceptNode(Node element, Node attr) {
			return true;
		}
	};

	public interface AttributeFilter {
		public boolean acceptNode(Node element, Node attr);
	}

	public DomPrintUtil(Document document) {
		this.document = document;
	}

	private String getXMLString(String targetS) {
		return targetS.replaceAll(AMP, ESC_AMP).replaceAll(LT, ESC_LT)
				.replaceAll(GT, ESC_GT);
	}

	private String getAttributeString(Node element, Node attr) {
		if (attrFilter.acceptNode(element, attr)) {
			String value = getXMLString(attr.getNodeValue());
			String quat = QUAT;
			if (value.indexOf(QUAT) > 0) {
				quat = SINGLE_QUAT;
			}
			return " " + attr.getNodeName() + "=" + quat + value + quat;
		}
		return "";
	}

	private boolean checkNewLine(Node target) {
		if (indent && target.hasChildNodes()) {
			short type = target.getFirstChild().getNodeType();
			if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
				return false;
			}
			return true;
		}
		return false;
	}

	public String toXMLString() {
		StringBuffer tmpSB = new StringBuffer(8192);

		TreeWalkerImpl treeWalker = new TreeWalkerImpl(document, whatToShow,
				nodeFilter, entityReferenceExpansion);

		String lt = escapeTagBracket ? ESC_LT : LT;
		String gt = escapeTagBracket ? ESC_GT : GT;
		String line_sep = indent ? LINE_SEP : "";

		Node tmpN = treeWalker.nextNode();
		boolean prevIsText = false;

		String indentS = "";
		while (tmpN != null) {
			short type = tmpN.getNodeType();
			switch (type) {
			case Node.ELEMENT_NODE:
				if (prevIsText) {
					tmpSB.append(line_sep);
				}
				tmpSB.append(indentS + lt + tmpN.getNodeName());
				NamedNodeMap attrs = tmpN.getAttributes();
				int len = attrs.getLength();
				for (int i = 0; i < len; i++) {
					Node attr = attrs.item(i);
					String value = attr.getNodeValue();
					if (null != value) {
						tmpSB.append(getAttributeString(tmpN, attr));
					}
				}
				if (tmpN instanceof HTMLTitleElement && !tmpN.hasChildNodes()) {
					tmpSB.append(gt + ((HTMLTitleElement) tmpN).getText());
					prevIsText = true;
				} else if (checkNewLine(tmpN)) {
					tmpSB.append(gt + line_sep);
					prevIsText = false;
				} else {
					tmpSB.append(gt);
					prevIsText = true;
				}
				break;
			case Node.TEXT_NODE:
				if (!prevIsText) {
					tmpSB.append(indentS);
				}
				tmpSB.append(getXMLString(tmpN.getNodeValue()));
				prevIsText = true;
				break;
			case Node.COMMENT_NODE:
				tmpSB.append(line_sep + indentS + lt + "!--"
						+ tmpN.getNodeValue() + "--" + gt + line_sep);
				prevIsText = false;
				break;
			case Node.CDATA_SECTION_NODE:
				tmpSB.append(line_sep + indentS + lt + "!CDATA["
						+ tmpN.getNodeValue() + "]]" + line_sep);
				break;
			case Node.DOCUMENT_TYPE_NODE:
				if (tmpN instanceof DocumentType) {
					DocumentType docType = (DocumentType) tmpN;
					String pubId = docType.getPublicId();
					String sysId = docType.getSystemId();
					if (null != pubId && pubId.length() > 0) {
						if (null != sysId && sysId.length() > 0) {
							tmpSB.append(line_sep + indentS + lt + "!DOCTYPE "
									+ docType.getName() + " PUBLIC \"" + pubId
									+ " \"" + sysId + "\">" + line_sep);
						} else {
							tmpSB.append(line_sep + indentS + lt + "!DOCTYPE "
									+ docType.getName() + " PUBLIC \"" + pubId
									+ "\">" + line_sep);
						}
					} else {
						tmpSB.append(line_sep + indentS + lt + "!DOCTYPE "
								+ docType.getName() + " SYSTEM \""
								+ docType.getSystemId() + "\">" + line_sep);

					}
				} else {
					System.out
							.println("Document Type node does not implement DocumentType: "
									+ tmpN);
				}
				break;
			default:
				System.out.println(tmpN.getNodeType() + " : "
						+ tmpN.getNodeName());
			}

			Node next = treeWalker.firstChild();
			if (null != next) {
				if (indent && type == Node.ELEMENT_NODE) {
					indentS = indentS + " ";
				}
				tmpN = next;
				continue;
			}

			if (tmpN.getNodeType() == Node.ELEMENT_NODE) {
				tmpSB.append(lt + "/" + tmpN.getNodeName() + gt + line_sep);
				prevIsText = false;
			}

			next = treeWalker.nextSibling();
			if (null != next) {
				tmpN = next;
				continue;
			}

			tmpN = null;
			next = treeWalker.parentNode();
			while (null != next) {
				if (next.getNodeType() == Node.ELEMENT_NODE) {
					if (indent) {
						indentS = indentS.substring(1);
					}
					tmpSB.append(line_sep + indentS + lt + "/"
							+ next.getNodeName() + gt + line_sep);
					prevIsText = false;
				}
				next = treeWalker.nextSibling();
				if (null != next) {
					tmpN = next;
					break;
				} else {
					next = treeWalker.parentNode();
				}
			}
		}
		return tmpSB.toString();
	}

	@Override
	public String toString() {
		return toXMLString();
	}

	public void setWhatToShow(int whatToShow) {
		this.whatToShow = whatToShow;
	}

	public void setNodeFilter(NodeFilter nodeFilter) {
		this.nodeFilter = nodeFilter;
	}

	public void setEntityReferenceExpansion(boolean entityReferenceExpansion) {
		this.entityReferenceExpansion = entityReferenceExpansion;
	}

	public void setIndent(boolean indent) {
		this.indent = indent;
	}

	public void setEscapeTagBracket(boolean escapeTagBracket) {
		this.escapeTagBracket = escapeTagBracket;
	}

	public void setAttrFilter(AttributeFilter attrFilter) {
		this.attrFilter = attrFilter;
	}

}
