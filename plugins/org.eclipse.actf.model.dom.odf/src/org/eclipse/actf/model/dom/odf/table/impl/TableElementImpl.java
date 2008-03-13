/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Tatsuya ISHIHARA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.odf.table.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.actf.model.dom.odf.ODFException;
import org.eclipse.actf.model.dom.odf.base.ContentBaseElement;
import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.impl.ODFStylableElementImpl;
import org.eclipse.actf.model.dom.odf.office.BodyElement;
import org.eclipse.actf.model.dom.odf.office.DocumentContentElement;
import org.eclipse.actf.model.dom.odf.range.impl.ITextElementContainerUtil;
import org.eclipse.actf.model.dom.odf.table.TableCellElement;
import org.eclipse.actf.model.dom.odf.table.TableColumnElement;
import org.eclipse.actf.model.dom.odf.table.TableConstants;
import org.eclipse.actf.model.dom.odf.table.TableElement;
import org.eclipse.actf.model.dom.odf.table.TableHeaderColumnsElement;
import org.eclipse.actf.model.dom.odf.table.TableHeaderRowsElement;
import org.eclipse.actf.model.dom.odf.table.TableRowElement;
import org.eclipse.actf.model.dom.odf.table.TableRowsElement;
import org.eclipse.actf.model.dom.odf.text.SequenceElement;
import org.eclipse.actf.model.dom.odf.text.TextConstants;
import org.eclipse.actf.util.xpath.XPathUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class TableElementImpl extends ODFStylableElementImpl implements TableElement {
	private static final long serialVersionUID = -1072814785496953249L;

	protected TableElementImpl(ODFDocument odfDoc, Element element) {
		super(odfDoc, element);
	}

	public String getAttrTableName() {
		if (hasAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
				TableConstants.ATTR_NAME))
			return getAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
					TableConstants.ATTR_NAME);
		return null;
	}

	public String getAttrTableStyleName() {
		if (hasAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
				TableConstants.ATTR_STYLE_NAME))
			return getAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
					TableConstants.ATTR_STYLE_NAME);
		return null;
	}

	public boolean getAttrTableAttrPrint() {
		if (hasAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
				TableConstants.ATTR_ATTR_PRINT)) {
			return new Boolean(getAttributeNS(
					TableConstants.TABLE_NAMESPACE_URI,
					TableConstants.ATTR_ATTR_PRINT)).booleanValue();
		}
		return false;
	}

	public boolean getAttrTableProtected() {
		if (hasAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
				TableConstants.ATTR_PROTECTED)) {
			return new Boolean(getAttributeNS(
					TableConstants.TABLE_NAMESPACE_URI,
					TableConstants.ATTR_PROTECTED)).booleanValue();
		}
		return false;
	}

	public int getTableIndex() {
		NodeList list = getContent().getElementsByTagNameNS(
				TableConstants.TABLE_NAMESPACE_URI, "table");
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).equals(this)) {
				return i;
			}
		}
		return -1;
	}

	public TableColumnElement getTableColumnChild(int column) {
		NodeList children = getChildNodes();
		if (children != null) {
			for (int i = 0, count = 0; i < children.getLength(); i++) {
				if (children.item(i) instanceof TableColumnElement) {
					if (count == column)
						return (TableColumnElement) children.item(i);
					count++;
				}
			}
		}
		return null;
	}

	public TableRowElement getTableRowChild(int row) {
		NodeList children = getChildNodes();
		if (children != null) {
			for (int i = 0, count = 0; i < children.getLength(); i++) {
				if (children.item(i) instanceof TableRowElement) {
					if (count == row)
						return (TableRowElement) children.item(i);
					count++;
				}
			}
		}
		return null;
	}

	public int getTableColumnSize() {
		int size = 0;
		NodeList children = getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof TableColumnElement) {
					int len = ((TableColumnElement) child)
							.getAttrTableNumberColumnsRepeated();
					if (len > 0)
						size += len;
					else
						size++;
				} else if ((child instanceof TableHeaderColumnsElement)
						|| (child instanceof TableColumnElement)) {
					NodeList grandChildren = child.getChildNodes();
					for (int j = 0; j < grandChildren.getLength(); j++) {
						Node grandChild = grandChildren.item(j);
						if (grandChild instanceof TableColumnElement) {
							int len = ((TableColumnElement) grandChild)
									.getAttrTableNumberColumnsRepeated();
							if (len > 0)
								size += len;
							else
								size++;
						}
					}
				}
			}
		}
		return size;
	}

	public List getTableColumnChildren() {
		List<Node> colList = null;
		NodeList children = getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof TableColumnElement) {
					if (colList == null)
						colList = new Vector<Node>();
					colList.add(child);
				} else if ((child instanceof TableHeaderColumnsElement)
						|| (child instanceof TableColumnElement)) {
					NodeList grandChildren = child.getChildNodes();
					for (int j = 0; j < grandChildren.getLength(); j++) {
						Node grandChild = grandChildren.item(j);
						if (grandChild instanceof TableColumnElement) {
							if (colList == null)
								colList = new Vector<Node>();
							colList.add(grandChild);
						}
					}
				}
			}
		}
		return colList;
	}

	public int getTableRowSize() {
		int size = 0;
		NodeList children = getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child instanceof TableRowElement) {
					int len = ((TableRowElement) child)
							.getAttrTableNumberRowsRepeated();
					if (len > 0)
						size += len;
					else
						size++;
				} else if ((child instanceof TableHeaderRowsElement)
						|| (child instanceof TableRowElement)) {
					NodeList grandChildren = child.getChildNodes();
					for (int j = 0; j < grandChildren.getLength(); j++) {
						Node grandChild = grandChildren.item(j);
						if (grandChild instanceof TableRowElement) {
							int len = ((TableRowElement) grandChild)
									.getAttrTableNumberRowsRepeated();
							if (len > 0)
								size += len;
							else
								size++;
						}
					}
				}
			}
		}
		return size;
	}

	public List getTableRowChildren() {
		List<Node> rowList = null;
		NodeList children = getChildNodes();
		if (children != null) {
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i) instanceof TableRowElement) {
					if (rowList == null)
						rowList = new Vector<Node>();
					rowList.add(children.item(i));
				} else if ((children.item(i) instanceof TableHeaderRowsElement)
						|| (children.item(i) instanceof TableRowsElement)) {
					NodeList grandChildren = children.item(i).getChildNodes();
					for (int j = 0; j < grandChildren.getLength(); j++) {
						if (grandChildren.item(j) instanceof TableRowElement) {
							if (rowList == null)
								rowList = new Vector<Node>();
							rowList.add(grandChildren.item(j));
						}
					}
				}
			}
		}
		return rowList;
	}

	public ContentBaseElement getContent() {
		if (odfDoc == null)
			return null;
		ContentBaseElement contentElement = null;

		Element root = odfDoc.getDocumentElement();
		if (root instanceof DocumentContentElement) {
			BodyElement body = ((DocumentContentElement) root).getBodyElement();
			contentElement = body.getContent();
		}

		return contentElement;
	}

	public boolean hasTableCellStyle(int column, int row) {
		TableRowElement rowElement = getTableRowChild(row);
		List cellElems = rowElement.getTableCellChildren();
		for (int i = 0, colCount = 0; i < cellElems.size(); i++) {
			TableCellElement cellElem = (TableCellElement) cellElems.get(i);
			if (cellElem.hasAttributeNS(TableConstants.TABLE_NAMESPACE_URI,
					TableConstants.ATTR_NUMBER_COLUMNS_REPEATED)) {
				colCount += cellElem.getAttrTableNumberColumnsRepeated();
			} else {
				colCount++;
			}
			if (colCount - 1 >= column)
				return cellElem.hasAttributeNS(
						TableConstants.TABLE_NAMESPACE_URI,
						TableConstants.ATTR_STYLE_NAME);
		}
		return false;
	}

	public TableRowElement appendNewTableRowElement() {
		String tablePrefix = this
				.lookupPrefix(TableConstants.TABLE_NAMESPACE_URI);
		TableRowElement rowElement = (TableRowElement) getOwnerDocument()
				.createElement(
						tablePrefix + ":" + TableConstants.ELEMENT_TABLE_ROW);
		return (TableRowElement) this.appendChild(rowElement);
	}

	public NodeList getTableHeaderColumns() {
		return XPathUtil.evalXPathNodeList(this, "./*[namespace-uri()='"
				+ TableConstants.TABLE_NAMESPACE_URI + "' and local-name()='"
				+ TableConstants.ELEMENT_TABLE_HEADER_COLUMNS + "']");
	}

	public NodeList getTableHeaderRows() {
		return XPathUtil.evalXPathNodeList(this, "./*[namespace-uri()='"
				+ TableConstants.TABLE_NAMESPACE_URI + "' and local-name()='"
				+ TableConstants.ELEMENT_TABLE_HEADER_ROWS + "']");
	}

	public SequenceElement getTextSequenceElement() {
		NodeList fnl = XPathUtil.evalXPathNodeList(this,
				"following-sibling::*[namespace-uri()='"
						+ TextConstants.TEXT_NAMESPACE_URI
						+ "' and local-name()='" + TextConstants.ELEMENT_P
						+ "'][1]" + "/*[namespace-uri()='"
						+ TextConstants.TEXT_NAMESPACE_URI
						+ "' and local-name()='"
						+ TextConstants.ELEMENT_SEQUENCE + "']");
		if ((fnl != null) && (fnl.getLength() == 1))
			return (SequenceElement) fnl.item(0);
		if ((fnl != null) && (fnl.getLength() > 1)) {
			new ODFException(
					"draw:image has more than one text:sequence elements.")
					.printStackTrace();
		}

		NodeList pnl = XPathUtil.evalXPathNodeList(this,
				"preceding-sibling::*[namespace-uri()='"
						+ TextConstants.TEXT_NAMESPACE_URI
						+ "' and local-name()='" + TextConstants.ELEMENT_P
						+ "'][1]" + "/*[namespace-uri()='"
						+ TextConstants.TEXT_NAMESPACE_URI
						+ "' and local-name()='"
						+ TextConstants.ELEMENT_SEQUENCE + "']");
		if ((pnl != null) && (pnl.getLength() == 1))
			return (SequenceElement) pnl.item(0);
		if ((pnl != null) && (pnl.getLength() > 1)) {
			new ODFException(
					"draw:image has more than one text:sequence elements.")
					.printStackTrace();
		}
		return null;
	}

	public long getContentSize() {
		return ITextElementContainerUtil.getContentSize(this);
	}

	public Iterator getChildIterator() {
		return ITextElementContainerUtil.getChildIterator(this);
	}
}