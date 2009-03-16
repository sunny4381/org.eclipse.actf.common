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
package org.eclipse.actf.model.internal.ui.editors.ooo.util;

import org.eclipse.actf.model.dom.odf.ODFConstants.ContentType;
import org.eclipse.actf.model.dom.odf.base.DrawingObjectElement;
import org.eclipse.actf.model.dom.odf.base.ODFDocument;
import org.eclipse.actf.model.dom.odf.base.ODFElement;
import org.eclipse.actf.model.dom.odf.dr3d.Dr3dConstants;
import org.eclipse.actf.model.dom.odf.draw.ControlElement;
import org.eclipse.actf.model.dom.odf.draw.DrawConstants;
import org.eclipse.actf.model.dom.odf.draw.FrameElement;
import org.eclipse.actf.model.dom.odf.draw.PageElement;
import org.eclipse.actf.model.dom.odf.table.TableConstants;
import org.eclipse.actf.model.dom.odf.table.TableElement;
import org.eclipse.actf.model.dom.odf.util.converter.ODFConverter;
import org.eclipse.actf.util.xpath.XPathServiceFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.star.container.NoSuchElementException;
import com.sun.star.container.XNameAccess;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPageSupplier;
import com.sun.star.drawing.XDrawPages;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.frame.XController;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XModel;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.table.XCell;
import com.sun.star.text.XTextDocument;
import com.sun.star.text.XTextEmbeddedObjectsSupplier;
import com.sun.star.text.XTextGraphicObjectsSupplier;
import com.sun.star.text.XTextTable;
import com.sun.star.text.XTextTablesSupplier;
import com.sun.star.text.XTextViewCursor;
import com.sun.star.text.XTextViewCursorSupplier;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.view.XSelectionSupplier;

public class OOoNavigation {

	private static final String COLON = ":"; //$NON-NLS-1$
	
	private String _targetContentID = null;
	private ODFDocument _contentXML = null;
	private ContentType _fileType = null;
	private XMultiServiceFactory _xMSF = null;
	private XComponent _xComp = null;

	public OOoNavigation(XMultiServiceFactory xMSF, XComponent xComponent) {
		this._xMSF = xMSF;
		this._xComp = xComponent;
	}

	public void setContentXML(ODFDocument contentXML) {
		this._contentXML = contentXML;
	}

	public ODFDocument getContentXML() {
		return this._contentXML;
	}

	// TODO find Node from Browser
	public void jumpToProblemPosition(Node targetNode) throws ODFException {
		this._targetContentID = ((Element) targetNode)
				.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);
		if (null == this._targetContentID) {
			return;
		}

		if (null == this._fileType) {
			this._fileType = ODFUtils.getODFFileType(this._contentXML);
		}
		if (this._fileType == ContentType.NONE) {
			return;
		}

		XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
				this._xComp);
		XController xController = xModel.getCurrentController();
		// before select error content set Drawing mode
		if (this._fileType == ContentType.PRESENTATION) {
			OOoUIControls.setDrawingMode(_xMSF, xController);
		}

		String drawPrefix = this._contentXML
				.lookupPrefix(DrawConstants.DRAW_NAMESPACE_URI);
		String dr3dPrefix = this._contentXML
				.lookupPrefix(Dr3dConstants.DR3D_NAMESPACE_URI);

		String targetNodeName = targetNode.getNodeName();
		String targetOdfNodeName = ((Element) targetNode)
				.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_TAGNAME);
		if (targetNodeName.equalsIgnoreCase("img")) { //$NON-NLS-1$
			jumpToProblemImage(targetNode);
		} else if (targetOdfNodeName.equals(drawPrefix + COLON
				+ DrawConstants.ELEMENT_OBJECT)) {
			jumpToProblemObject(targetNode);
		} else if (targetNodeName.equalsIgnoreCase("table")) { //$NON-NLS-1$
			jumpToProblemTable(targetNode);
		} else if ((targetOdfNodeName.equals(drawPrefix + COLON
				+ DrawConstants.ELEMENT_CAPTION))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_CIRCLE))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_CUSTOM_SHAPE))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_ELLIPSE))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_G))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_LINE))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_CONNECTOR))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_POLYGON))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_POLYLINE))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_RECT))
				|| (targetOdfNodeName.equals(drawPrefix + COLON
						+ DrawConstants.ELEMENT_REGULAR_POLYGON))
				|| (targetOdfNodeName.equals(dr3dPrefix + COLON
						+ Dr3dConstants.ELEMENT_SCENE))) {
			jumpToProblemDrawingShape(targetNode);
		} else if (targetOdfNodeName.equals(drawPrefix + COLON
				+ DrawConstants.ELEMENT_PAGE)) {
			jumpToProblemDrawingPage(targetNode);
		} else if (targetOdfNodeName.equals(drawPrefix + COLON
				+ DrawConstants.ELEMENT_CONTROL)) {
			jumpToProblemFormControl(targetNode);
		}

		// after select error content hide UI elements
		try {
			XFrame xFrame = xController.getFrame();
			OOoUIControls.hideUIElements(xFrame, this._xMSF);
		} catch (ODFException e) {
			e.printStackTrace();
		}
	}

	private void jumpToProblemTable(Node targetNode) throws ODFException {
		XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
				this._xComp);
		XSelectionSupplier selectionSupplier = (XSelectionSupplier) UnoRuntime
				.queryInterface(XSelectionSupplier.class, xModel
						.getCurrentController());

		if (ContentType.WRITE.equals(this._fileType)) {
			XTextDocument xTextDoc = (XTextDocument) UnoRuntime.queryInterface(
					XTextDocument.class, this._xComp);

			XTextTablesSupplier xTableSupplier = (XTextTablesSupplier) UnoRuntime
					.queryInterface(XTextTablesSupplier.class, xTextDoc);

			XNameAccess xNameAccess = xTableSupplier.getTextTables();
			try {
				if (!(targetNode instanceof Element))
					return;
				String odfContentId = ((Element) targetNode)
						.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);

				@SuppressWarnings("nls")
				NodeList nl = XPathServiceFactory
						.newService()
						.evalPathForNodeList(
								"//*[namespace-uri()='"
										+ TableConstants.TABLE_NAMESPACE_URI
										+ "' and local-name()='"
										+ TableConstants.ELEMENT_TABLE
										+ "'][@"
										+ ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID
										+ "='" + odfContentId + "']",
								_contentXML.getDocumentElement());
				if ((nl == null) || (nl.getLength() != 1))
					return;
				TableElement tableElem = (TableElement) nl.item(0);
				String tableName = tableElem.getAttributeNS(
						TableConstants.TABLE_NAMESPACE_URI,
						TableConstants.ATTR_NAME);
				Object oTable = xNameAccess.getByName(tableName);
				XTextTable xTable = (XTextTable) UnoRuntime.queryInterface(
						XTextTable.class, oTable);
				selectionSupplier.select(xTable);

				// select whole table
				XController xController = xModel.getCurrentController();
				XTextViewCursorSupplier xTextViewCursorSupplier = (XTextViewCursorSupplier) UnoRuntime
						.queryInterface(XTextViewCursorSupplier.class,
								xController);
				XTextViewCursor xTextViewCursor = xTextViewCursorSupplier
						.getViewCursor();
				xTextViewCursor.collapseToStart();

				XCell lastCell = xTable
						.getCellByName(xTable.getCellNames()[xTable
								.getCellNames().length - 1]);
				selectionSupplier.select(lastCell);
				xTextViewCursor.gotoStart(true);
			} catch (NoSuchElementException e) {
				e.printStackTrace();
				throw new ODFException(e.getMessage());
			} catch (WrappedTargetException e) {
				e.printStackTrace();
				throw new ODFException(e.getMessage());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new ODFException(e.getMessage());
			}
		}
	}

	private void jumpToProblemImage(Node targetNode) throws ODFException {
		XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
				this._xComp);
		XSelectionSupplier selectionSupplier = (XSelectionSupplier) UnoRuntime
				.queryInterface(XSelectionSupplier.class, xModel
						.getCurrentController());

		if (!(targetNode instanceof Element))
			return;
		String odfContentId = ((Element) targetNode)
				.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);

		@SuppressWarnings("nls")		
		NodeList nl = XPathServiceFactory.newService()
				.evalPathForNodeList(
						"//*[@" + ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID
								+ "='" + odfContentId + "']",
						_contentXML.getDocumentElement());
		if ((nl == null) || (nl.getLength() != 1))
			return;
		if (!(nl.item(0) instanceof ODFElement))
			return;

		Node parent = nl.item(0).getParentNode();
		if (!(parent instanceof FrameElement))
			return;

		FrameElement frameElem = (FrameElement) parent;
		String drawName = frameElem.getAttributeNS(
				DrawConstants.DRAW_NAMESPACE_URI, DrawConstants.ATTR_NAME);
		if (drawName == null)
			return;

		Object xDoc = UnoRuntime.queryInterface(XTextDocument.class,
				this._xComp);
		if ((xDoc != null) && (xDoc instanceof XTextDocument)) {
			XTextDocument xTextDoc = (XTextDocument) xDoc;
			XTextGraphicObjectsSupplier xGraphicSupplier = (XTextGraphicObjectsSupplier) UnoRuntime
					.queryInterface(XTextGraphicObjectsSupplier.class, xTextDoc);
			XNameAccess xNameAccess = xGraphicSupplier.getGraphicObjects();
			try {
				Object obj = xNameAccess.getByName(drawName);
				selectionSupplier.select(obj);
				return;
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			} catch (WrappedTargetException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		Object xPageSupplier = UnoRuntime.queryInterface(
				XDrawPagesSupplier.class, this._xComp);
		if ((xPageSupplier != null)
				&& (xPageSupplier instanceof XDrawPagesSupplier)) {
			XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) xPageSupplier;
			int zIndex = (int) frameElem.getZIndex();
			int pageIndex = (int) frameElem.getPageIndex();
			if ((zIndex != -1) && (pageIndex != -1)) {
				XDrawPages xDrawPages = xDrawPagesSupplier.getDrawPages();
				try {
					Object xPage = UnoRuntime.queryInterface(XDrawPage.class,
							xDrawPages.getByIndex(pageIndex));
					if ((xPage != null) && (xPage instanceof XDrawPage)) {
						XDrawPage xDrawPage = (XDrawPage) xPage;
						Object obj = xDrawPage.getByIndex(zIndex);
						selectionSupplier.select(obj);
						return;
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (WrappedTargetException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

	private void jumpToProblemObject(Node targetNode) throws ODFException {
		XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
				this._xComp);
		XSelectionSupplier selectionSupplier = (XSelectionSupplier) UnoRuntime
				.queryInterface(XSelectionSupplier.class, xModel
						.getCurrentController());

		if (!(targetNode instanceof Element))
			return;
		String odfContentId = ((Element) targetNode)
				.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);

		@SuppressWarnings("nls")		
		NodeList nl = XPathServiceFactory.newService()
				.evalPathForNodeList(
						"//*[@" + ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID
								+ "='" + odfContentId + "']",
						_contentXML.getDocumentElement());
		if ((nl == null) || (nl.getLength() != 1))
			return;
		if (!(nl.item(0) instanceof ODFElement))
			return;

		Node parent = nl.item(0).getParentNode();
		if (!(parent instanceof FrameElement))
			return;

		FrameElement frameElem = (FrameElement) parent;
		String drawName = frameElem.getAttributeNS(
				DrawConstants.DRAW_NAMESPACE_URI, DrawConstants.ATTR_NAME);
		if (drawName == null)
			return;

		Object xDoc = UnoRuntime.queryInterface(XTextDocument.class,
				this._xComp);
		if ((xDoc != null) && (xDoc instanceof XTextDocument)) {
			XTextDocument xTextDoc = (XTextDocument) xDoc;
			XTextEmbeddedObjectsSupplier xGraphicSupplier = (XTextEmbeddedObjectsSupplier) UnoRuntime
					.queryInterface(XTextEmbeddedObjectsSupplier.class,
							xTextDoc);
			XNameAccess xNameAccess = xGraphicSupplier.getEmbeddedObjects();
			try {
				Object obj = xNameAccess.getByName(drawName);
				selectionSupplier.select(obj);
				return;
			} catch (NoSuchElementException e) {
				e.printStackTrace();
			} catch (WrappedTargetException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		Object xPageSupplier = UnoRuntime.queryInterface(
				XDrawPagesSupplier.class, this._xComp);
		if ((xPageSupplier != null)
				&& (xPageSupplier instanceof XDrawPagesSupplier)) {
			XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) xPageSupplier;
			int zIndex = (int) frameElem.getZIndex();
			int pageIndex = (int) frameElem.getPageIndex();
			if ((zIndex != -1) && (pageIndex != -1)) {
				XDrawPages xDrawPages = xDrawPagesSupplier.getDrawPages();
				try {
					Object xPage = UnoRuntime.queryInterface(XDrawPage.class,
							xDrawPages.getByIndex(pageIndex));
					if ((xPage != null) && (xPage instanceof XDrawPage)) {
						XDrawPage xDrawPage = (XDrawPage) xPage;
						Object obj = xDrawPage.getByIndex(zIndex);
						selectionSupplier.select(obj);
						return;
					}
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
				} catch (WrappedTargetException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		return;
	}

	private void jumpToProblemDrawingShape(Node targetNode) throws ODFException {
		try {
			XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
					this._xComp);
			XSelectionSupplier selectionSupplier = (XSelectionSupplier) UnoRuntime
					.queryInterface(XSelectionSupplier.class, xModel
							.getCurrentController());

			if (!(targetNode instanceof Element))
				return;
			String odfContentId = ((Element) targetNode)
					.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);

			@SuppressWarnings("nls")
			NodeList nl = XPathServiceFactory.newService().evalPathForNodeList(
					"//*[@" + ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID + "='"
							+ odfContentId + "']",
					_contentXML.getDocumentElement());
			if ((nl == null) || (nl.getLength() != 1))
				return;
			if (!(nl.item(0) instanceof DrawingObjectElement))
				return;

			DrawingObjectElement shapeElem = (DrawingObjectElement) nl.item(0);
			int zIndex = (int) shapeElem.getZIndex();
			if (zIndex >= 0) {
				Object xPageSupplier = UnoRuntime.queryInterface(
						XDrawPageSupplier.class, this._xComp);
				if (xPageSupplier instanceof XDrawPageSupplier) {
					XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier) xPageSupplier;
					XDrawPage xDrawPage = xDrawPageSupplier.getDrawPage();
					if (null != xDrawPage) {
						try {
							Object obj = xDrawPage.getByIndex(zIndex);
							selectionSupplier.select(obj);
							return;
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (WrappedTargetException e) {
							e.printStackTrace();
						}
					}
				}
				Object xPagesSupplier = UnoRuntime.queryInterface(
						XDrawPagesSupplier.class, this._xComp);
				if ((xPagesSupplier != null)
						&& (xPagesSupplier instanceof XDrawPagesSupplier)) {
					XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) xPagesSupplier;
					int pageIndex = (int) shapeElem.getPageIndex();
					if ((zIndex != -1) && (pageIndex != -1)) {
						XDrawPages xDrawPages = xDrawPagesSupplier
								.getDrawPages();
						try {
							Object xPage = UnoRuntime.queryInterface(
									XDrawPage.class, xDrawPages
											.getByIndex(pageIndex));
							if ((xPage != null) && (xPage instanceof XDrawPage)) {
								XDrawPage xDrawPage = (XDrawPage) xPage;
								Object obj = xDrawPage.getByIndex(zIndex);
								selectionSupplier.select(obj);
								return;
							}
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (WrappedTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			throw new ODFException(iae.getMessage());
		}
	}

	private void jumpToProblemDrawingPage(Node targetNode) throws ODFException {
		try {
			XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
					this._xComp);
			XSelectionSupplier selectionSupplier = (XSelectionSupplier) UnoRuntime
					.queryInterface(XSelectionSupplier.class, xModel
							.getCurrentController());

			if (ContentType.PRESENTATION.equals(this._fileType)) {
				if (!(targetNode instanceof Element))
					return;
				String odfContentId = ((Element) targetNode)
						.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);

				@SuppressWarnings("nls")
				NodeList nl = XPathServiceFactory
						.newService()
						.evalPathForNodeList(
								"//*[namespace-uri()='"
										+ DrawConstants.DRAW_NAMESPACE_URI
										+ "' and local-name()='"
										+ DrawConstants.ELEMENT_PAGE
										+ "'][@"
										+ ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID
										+ "='" + odfContentId + "']",
								_contentXML.getDocumentElement());
				if ((nl == null) || (nl.getLength() != 1))
					return;
				if (nl.item(0) instanceof PageElement) {
					PageElement pageElem = (PageElement) nl.item(0);
					int pageIndex = pageElem.getPageIndex();
					XDrawPage xDrawPage = ODFUtils.getDrawPageByIndex(
							this._xComp, pageIndex);
					selectionSupplier.select(xDrawPage);
					return;
				}
			}
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			throw new ODFException(iae.getMessage());
		}
	}

	private void jumpToProblemFormControl(Node targetNode) throws ODFException {
		try {
			XModel xModel = (XModel) UnoRuntime.queryInterface(XModel.class,
					this._xComp);
			XSelectionSupplier selectionSupplier = (XSelectionSupplier) UnoRuntime
					.queryInterface(XSelectionSupplier.class, xModel
							.getCurrentController());

			if (!(targetNode instanceof Element))
				return;
			String odfContentId = ((Element) targetNode)
					.getAttribute(ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID);

			@SuppressWarnings("nls")
			NodeList nl = XPathServiceFactory.newService().evalPathForNodeList(
					"//*[namespace-uri()='" + DrawConstants.DRAW_NAMESPACE_URI
							+ "' and local-name()='"
							+ DrawConstants.ELEMENT_CONTROL + "'][@"
							+ ODFConverter.OUTPUT_ATTR_ODF_CONTENT_ID + "='"
							+ odfContentId + "']",
					_contentXML.getDocumentElement());
			if ((nl == null) || (nl.getLength() != 1))
				return;
			if (!(nl.item(0) instanceof ControlElement))
				return;

			ControlElement elem = (ControlElement) nl.item(0);
			int zIndex = (int) elem.getZIndex();
			if (zIndex >= 0) {
				Object xPageSupplier = UnoRuntime.queryInterface(
						XDrawPageSupplier.class, this._xComp);
				if (xPageSupplier instanceof XDrawPageSupplier) {
					XDrawPageSupplier xDrawPageSupplier = (XDrawPageSupplier) xPageSupplier;
					XDrawPage xDrawPage = xDrawPageSupplier.getDrawPage();
					if (null != xDrawPage) {
						try {
							Object obj = xDrawPage.getByIndex(zIndex);
							selectionSupplier.select(obj);
							return;
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (WrappedTargetException e) {
							e.printStackTrace();
						}
					}
				}
				Object xPagesSupplier = UnoRuntime.queryInterface(
						XDrawPagesSupplier.class, this._xComp);
				if ((xPagesSupplier != null)
						&& (xPagesSupplier instanceof XDrawPagesSupplier)) {
					XDrawPagesSupplier xDrawPagesSupplier = (XDrawPagesSupplier) xPagesSupplier;
					int pageIndex = (int) elem.getPageIndex();
					if ((zIndex != -1) && (pageIndex != -1)) {
						XDrawPages xDrawPages = xDrawPagesSupplier
								.getDrawPages();
						try {
							Object xPage = UnoRuntime.queryInterface(
									XDrawPage.class, xDrawPages
											.getByIndex(pageIndex));
							if ((xPage != null) && (xPage instanceof XDrawPage)) {
								XDrawPage xDrawPage = (XDrawPage) xPage;
								Object obj = xDrawPage.getByIndex(zIndex);
								selectionSupplier.select(obj);
								return;
							}
						} catch (IndexOutOfBoundsException e) {
							e.printStackTrace();
						} catch (WrappedTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			throw new ODFException(iae.getMessage());
		}
	}
}
