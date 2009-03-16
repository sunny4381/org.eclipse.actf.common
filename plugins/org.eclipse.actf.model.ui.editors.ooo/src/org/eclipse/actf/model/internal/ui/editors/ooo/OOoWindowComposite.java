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
package org.eclipse.actf.model.internal.ui.editors.ooo;

import java.io.File;
import java.io.IOException;

import org.eclipse.actf.model.dom.odf.ODFParser;
import org.eclipse.actf.model.dom.odf.ODFConstants.ContentType;
import org.eclipse.actf.model.dom.odf.manifest.ManifestConstants;
import org.eclipse.actf.model.dom.odf.util.ODFFileUtils;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.BootstrapForOOoComposite;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.ODFException;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.ODFUtils;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.OOoNavigation;
import org.eclipse.actf.model.internal.ui.editors.ooo.util.OOoUIControls;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.sun.star.accessibility.AccessibleRole;
import com.sun.star.accessibility.XAccessible;
import com.sun.star.accessibility.XAccessibleAction;
import com.sun.star.accessibility.XAccessibleContext;
import com.sun.star.awt.PosSize;
import com.sun.star.awt.Rectangle;
import com.sun.star.awt.XSystemChildFactory;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.beans.PropertyValue;
import com.sun.star.beans.UnknownPropertyException;
import com.sun.star.beans.XPropertySet;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.drawing.XDrawPage;
import com.sun.star.drawing.XDrawPages;
import com.sun.star.drawing.XDrawPagesSupplier;
import com.sun.star.drawing.XDrawView;
import com.sun.star.frame.FrameSearchFlag;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;
import com.sun.star.frame.XFrames;
import com.sun.star.frame.XFramesSupplier;
import com.sun.star.lang.DisposedException;
import com.sun.star.lang.IndexOutOfBoundsException;
import com.sun.star.lang.SystemDependent;
import com.sun.star.lang.WrappedTargetException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiServiceFactory;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.text.XTextDocument;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;

public class OOoWindowComposite extends Composite {
	public static final int ACTION_SCROLL_DECREMENT_LINE = 0;
	public static final int ACTION_SCROLL_INCREMENT_LINE = 1;
	public static final int ACTION_SCROLL_DECREMENT_BLOCK = 2;
	public static final int ACTION_SCROLL_INCREMENT_BLOCK = 3;

	private XDesktop xDesktop = null;
	private XMultiServiceFactory xMSF = null;
	private OOoWindow oooWin = null;
	private XFrame xFrame = null;
	private XComponent xComponent = null;
	private XAccessibleAction[] scrollAction = null;

	private String _url = null;

	private OOoNavigation oooNavigation = null;

	public OOoWindowComposite(Composite parent, int style) throws ODFException {
		super(parent, style);
		init();
		addControlListener(new OOoWindowControlListener());
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}
		});
	}

	public void init() throws ODFException {
		try {
			ClassLoader classLoader = ODFUtils.loadOpenOfficeLibs();
			BootstrapForOOoComposite.bootstrap(classLoader);
			xMSF = BootstrapForOOoComposite.getXMultiServiceFactory();
			xDesktop = BootstrapForOOoComposite.getXDesktop();
		} catch (BootstrapException e) {
			throw new ODFException(e.getMessage());
		}
	}

	private class OOoWindowControlListener implements ControlListener {
		public void controlMoved(ControlEvent e) {
			// TODO Auto-generated method stub

		}

		public void controlResized(ControlEvent e) {
			if (oooWin != null) {
				Object source = e.getSource();
				if (source instanceof Composite) {
					Point size = ((Composite) source).getSize();
					oooWin.setSize(size.x, size.y);
				}
			}
		}
	}

	public void dispose() {
		super.dispose();
		xMSF = null;
		if (oooWin != null)
			oooWin.dispose();
		if (xFrame != null)
			closeFrame();
		xFrame = null;
		xComponent = null;
		if ((xDesktop != null) && (ODFUtils.getOpenOfficeFrameNum() == 0)) {
			dispathMessageThread(new Runnable() {
				public void run() {
					try {
						xDesktop.terminate();
					} catch (DisposedException de) {
					}
				}
			});
		}
		xDesktop = null;
		if (scrollAction != null) {
			for (int i = 0; i < scrollAction.length; i++)
				scrollAction[i] = null;
		}
		scrollAction = null;
	}

	public void dispathMessageThread(Runnable runnable) {
		Thread thread = new Thread(runnable);
		thread.start();
		Display display = Display.getDefault();
		while (thread.isAlive()) {
			display.readAndDispatch();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void closeFrame() {
		if (xFrame != null) {
			dispathMessageThread(new Runnable() {
				public void run() {
					XCloseable xCloseable = (XCloseable) UnoRuntime
							.queryInterface(XCloseable.class, xFrame);
					if (xCloseable != null) {
						try {
							xCloseable.close(true);
						} catch (CloseVetoException e) {
							e.printStackTrace();
						}
					} else {
						XComponent xComponent = (XComponent) UnoRuntime
								.queryInterface(XComponent.class, xFrame);
						if (xComponent != null) {
							xComponent.dispose();
						}
					}
				}
			});
			xFrame = null;
			xComponent = null;
		}
	}

	@SuppressWarnings("nls")
	private void load(String sUrl) {
		if (0 != sUrl.indexOf("private:") && 0 != sUrl.indexOf("file:///")) {
			try {
				File srcFile = new File(sUrl);
				StringBuffer sb = new StringBuffer("file:///");
				sb.append(srcFile.getCanonicalPath().replace('\\', '/'));
				sUrl = sb.toString();
				if (!srcFile.canRead()) {
					return;
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
				return;
			}
		}

		XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime
				.queryInterface(XComponentLoader.class, xFrame);
		try {
			PropertyValue[] loadProp = new PropertyValue[1];
			loadProp[0] = new PropertyValue();
			loadProp[0].Name = "ReadOnly";
			loadProp[0].Value = Boolean.TRUE;
			xComponent = xComponentLoader.loadComponentFromURL(sUrl, xFrame
					.getName(), FrameSearchFlag.ALL, loadProp);
			oooNavigation = new OOoNavigation(xMSF, xComponent);
		} catch (Exception e) {
			xComponent = null;
		}
	}

	private XAccessible findXAccessibleByRole(XAccessible xAc, short role) {
		XAccessibleContext xAcContext = xAc.getAccessibleContext();
		if (xAc.getAccessibleContext().getAccessibleRole() == role) {
			return xAc;
		}
		for (int i = 0; i < xAcContext.getAccessibleChildCount(); i++) {
			XAccessible xAcChild = null;
			try {
				xAcChild = xAcContext.getAccessibleChild(i);
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
			XAccessible foundAc = findXAccessibleByRole(xAcChild, role);
			if (foundAc != null)
				return foundAc;
		}
		return null;
	}

	// index 0 : X scroll bar
	// index 1 : Y scroll bar
	// action for SCROLL PANE
	// 0 --- decrementLine
	// 1 --- incrementLine
	// 2 --- decrementBlock
	// 3 --- incrementBlock
	private XAccessibleAction[] findScrollAction() {
		XWindow xWindow = oooWin.getXWindow();
		XAccessible xAcWindow = (XAccessible) UnoRuntime.queryInterface(
				XAccessible.class, xWindow);
		XAccessible xAcScrollPane = findXAccessibleByRole(xAcWindow,
				AccessibleRole.SCROLL_PANE);

		XAccessibleContext scrollPaneContext = xAcScrollPane
				.getAccessibleContext();
		XAccessible[] xAcScrollBar = new XAccessible[2];
		XAccessibleAction[] scrollAction = new XAccessibleAction[2];
		for (int i = 0, j = 0; i < scrollPaneContext.getAccessibleChildCount(); i++) {
			try {
				XAccessible child = scrollPaneContext.getAccessibleChild(i);
				XAccessibleContext childContext = child.getAccessibleContext();
				if (childContext.getAccessibleRole() == AccessibleRole.SCROLL_BAR) {
					xAcScrollBar[j] = child;
					scrollAction[j] = (XAccessibleAction) UnoRuntime
							.queryInterface(XAccessibleAction.class,
									xAcScrollBar[j].getAccessibleContext());
					j++;
				}
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		return scrollAction;
	}

	private class OOoWindow {
		Composite parent = null;
		XWindow xWindow = null;

		public OOoWindow(Composite parent) {
			this.parent = parent;
		}

		public void dispose() {
			xWindow = null;
		}

		public void create(XMultiServiceFactory xMSF) {
			if (xWindow == null) {
				XToolkit xToolkit = null;
				try {
					xToolkit = (XToolkit) UnoRuntime
							.queryInterface(XToolkit.class, xMSF
									.createInstance("com.sun.star.awt.Toolkit")); //$NON-NLS-1$
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (xToolkit != null) {
					XSystemChildFactory xSystemChildFactory = (XSystemChildFactory) UnoRuntime
							.queryInterface(XSystemChildFactory.class, xToolkit);
					if (xSystemChildFactory != null) {
						byte[] procID = new byte[0];
						XWindowPeer xWindowPeer = xSystemChildFactory
								.createSystemChild(new Integer(parent.handle),
										procID, SystemDependent.SYSTEM_WIN32);
						if (xWindowPeer != null) {
							xWindow = (XWindow) UnoRuntime.queryInterface(
									XWindow.class, xWindowPeer);
						}
					}
				}
			}
		}

		public XWindow getXWindow() {
			return xWindow;
		}

		public void setSize(int width, int height) {
			if (xWindow != null) {
				xWindow.setPosSize(0, 0, width, height, PosSize.SIZE);
			}
		}

		public void setWidth(int width) {
			if (xWindow != null) {
				xWindow.setPosSize(0, 0, width, 0, PosSize.WIDTH);
			}
		}

		public void setHeight(int height) {
			if (xWindow != null) {
				xWindow.setPosSize(0, 0, 0, height, PosSize.HEIGHT);
			}
		}

		public void setVisible(boolean visible) {
			if (xWindow != null) {
				xWindow.setVisible(visible);
			}
		}
	}

	public boolean isPasswordProtected(String url) {
		ODFParser parser = new ODFParser();
		Document manifestDoc = parser
				.getDocument(
						url,
						org.eclipse.actf.model.dom.odf.ODFConstants.ODF_MANIFEST_FILENAME);
		if (manifestDoc == null)
			return false;
		NodeList nl = manifestDoc.getElementsByTagNameNS(
				ManifestConstants.MANIFEST_NAMESPACE_URI,
				ManifestConstants.ELEMENT_ENCRYPTION_DATA);
		if ((nl != null) && (nl.getLength() != 0))
			return true;
		return false;
	}

	public void open(String sUrl) {
		if (isPasswordProtected(sUrl)) {
			// TODO
			PlatformUI
					.getWorkbench()
					.getHelpSystem()
					.displayHelpResource(
							"/org.eclipse.actf.examples.adesigner.doc/docs/odf/error.html"); //$NON-NLS-1$
			return;
		}

		// remove previous OOo window
		if (oooWin != null) {
			oooWin.dispose();
		}
		if (xFrame != null) {
			closeFrame();
		}

		oooWin = new OOoWindow(this);
		this._url = sUrl;

		dispathMessageThread(new Runnable() {
			public void run() {
				try {
					oooWin.create(xMSF);

					xFrame = (XFrame) UnoRuntime.queryInterface(XFrame.class,
							xMSF.createInstance("com.sun.star.frame.Frame")); //$NON-NLS-1$
					XFramesSupplier xFramesSupplier = (XFramesSupplier) UnoRuntime
							.queryInterface(
									XFramesSupplier.class,
									xMSF
											.createInstance("com.sun.star.frame.Desktop")); //$NON-NLS-1$
					XFrames xFrames = xFramesSupplier.getFrames();
					xFrames.append(xFrame);
					xFrame.initialize(oooWin.getXWindow());

					load(_url);

					scrollAction = findScrollAction();

					try {
						OOoUIControls.hideUIElements(xFrame, xMSF);
					} catch (ODFException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		Point size = this.getSize();
		oooWin.setVisible(true);
		oooWin.setSize(size.x, size.y);
	}

	public String getUrl() {
		return this._url;
	}

	public OOoNavigation getOooNavigation() {
		return this.oooNavigation;
	}

	public XAccessibleAction[] getScrollAction() {
		return scrollAction;
	}

	public int[] getViewData() {
		ContentType odfFileType = ODFFileUtils.getODFFileType(_url);
		if (ContentType.WRITE.equals(odfFileType)) {
			XTextDocument xTextDocument = (XTextDocument) UnoRuntime
					.queryInterface(XTextDocument.class, xComponent);
			if (xTextDocument != null) {
				String viewData = (String) xFrame.getController().getViewData();
				String[] pointsStr = viewData.split(";"); //$NON-NLS-1$
				int[] points = new int[4];
				points[0] = new Integer(pointsStr[3]).intValue();
				points[1] = new Integer(pointsStr[4]).intValue();
				points[2] = new Integer(pointsStr[5]).intValue();
				points[3] = new Integer(pointsStr[6]).intValue();
				return points;
			}
		} else if (ContentType.SPREADSHEET.equals(odfFileType)) {
			XController xController = xFrame.getController();
			XSpreadsheetView xSpreadView = (XSpreadsheetView) UnoRuntime
					.queryInterface(XSpreadsheetView.class, xController);
			XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xSpreadView);
			try {
				Rectangle visibleArea = (Rectangle) UnoRuntime.queryInterface(
						Rectangle.class, xPropSet
								.getPropertyValue("VisibleArea")); //$NON-NLS-1$
				int[] points = new int[4];
				points[0] = visibleArea.X;
				points[1] = visibleArea.Y;
				points[2] = visibleArea.X + visibleArea.Width;
				points[3] = visibleArea.Y + visibleArea.Height;
				return points;
			} catch (UnknownPropertyException e1) {
				e1.printStackTrace();
			} catch (WrappedTargetException e1) {
				e1.printStackTrace();
			}
		} else if (ContentType.PRESENTATION.equals(odfFileType)) {
			XController xController = xFrame.getController();
			XDrawView xDrawView = (XDrawView) UnoRuntime.queryInterface(
					XDrawView.class, xController);
			XPropertySet xPropSet = (XPropertySet) UnoRuntime.queryInterface(
					XPropertySet.class, xDrawView);
			try {
				Rectangle visibleArea = (Rectangle) UnoRuntime.queryInterface(
						Rectangle.class, xPropSet
								.getPropertyValue("VisibleArea")); //$NON-NLS-1$
				int[] points = new int[4];
				points[0] = visibleArea.X;
				points[1] = visibleArea.Y;
				points[2] = visibleArea.X + visibleArea.Width;
				points[3] = visibleArea.Y + visibleArea.Height;
				return points;
			} catch (UnknownPropertyException e1) {
				e1.printStackTrace();
			} catch (WrappedTargetException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public int[] getOOoWinSize() throws ODFException {
		if (xFrame == null) {
			throw new ODFException("not initialized"); //$NON-NLS-1$
		}

		XWindow componentWindow = xFrame.getComponentWindow();
		Rectangle rect = componentWindow.getPosSize();
		int[] size = new int[2];
		size[0] = rect.Width;
		size[1] = rect.Height;
		return size;
	}

	public int getPresentationPageCount() {
		return ODFUtils.getDrawPageCount(xComponent);
	}

	public int jumpToPresentationPage(int pageNumber) {
		XDrawPagesSupplier xDrawPagesSupp = (XDrawPagesSupplier) UnoRuntime
				.queryInterface(XDrawPagesSupplier.class, xComponent);
		XDrawPages xDrawPages = xDrawPagesSupp.getDrawPages();

		XController xController = xFrame.getController();
		XDrawView xDrawView = (XDrawView) UnoRuntime.queryInterface(
				XDrawView.class, xController);
		try {
			XDrawPage page = (XDrawPage) UnoRuntime.queryInterface(
					XDrawPage.class, xDrawPages.getByIndex(pageNumber - 1));
			xDrawView.setCurrentPage(page);
			return 1;
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getCurrentPageNumber() {
		XController xController = xFrame.getController();
		XDrawView xDrawView = (XDrawView) UnoRuntime.queryInterface(
				XDrawView.class, xController);
		XDrawPage curPage = xDrawView.getCurrentPage();
		XPropertySet curPageProp = (XPropertySet) UnoRuntime.queryInterface(
				XPropertySet.class, curPage);
		try {
			return ((Short) curPageProp.getPropertyValue("Number")).intValue(); //$NON-NLS-1$
		} catch (UnknownPropertyException e1) {
			e1.printStackTrace();
		} catch (WrappedTargetException e1) {
			e1.printStackTrace();
		}
		return -1;
	}

	public int movePresentationPage(boolean forward) {
		XDrawPagesSupplier xDrawPagesSupp = (XDrawPagesSupplier) UnoRuntime
				.queryInterface(XDrawPagesSupplier.class, xComponent);
		XDrawPages xDrawPages = xDrawPagesSupp.getDrawPages();
		int pageCount = xDrawPages.getCount();

		int curPageNumber = getCurrentPageNumber();
		if (curPageNumber == -1)
			return -1;

		if ((forward == true) && (curPageNumber == pageCount))
			return 0;
		if ((forward == false) && (curPageNumber == 1))
			return 0;

		int newPageIndex;
		if (forward)
			newPageIndex = curPageNumber;
		else
			newPageIndex = curPageNumber - 2;

		XController xController = xFrame.getController();
		XDrawView xDrawView = (XDrawView) UnoRuntime.queryInterface(
				XDrawView.class, xController);
		try {
			XDrawPage newPage = (XDrawPage) UnoRuntime.queryInterface(
					XDrawPage.class, xDrawPages.getByIndex(newPageIndex));
			xDrawView.setCurrentPage(newPage);
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (WrappedTargetException e) {
			e.printStackTrace();
		}
		try {
			return getOOoWinSize()[1];
		} catch (ODFException e) {
			return -1;
		}
	}

	public boolean setDrawingMode() {
		try {
			return OOoUIControls.setDrawingMode(xMSF, xFrame.getController());
		} catch (ODFException e) {
			e.printStackTrace();
		}
		return false;
	}
}
