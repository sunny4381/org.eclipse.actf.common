/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.ui.editors.ie.impl;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.actf.model.dom.dombycom.DomByCom;
import org.eclipse.actf.model.dom.html.util.HtmlParserUtil;
import org.eclipse.actf.model.ui.IModelServiceHolder;
import org.eclipse.actf.model.ui.IModelServiceScrollManager;
import org.eclipse.actf.model.ui.ImagePositionInfo;
import org.eclipse.actf.model.ui.ModelServiceSizeInfo;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF;
import org.eclipse.actf.model.ui.editor.browser.IWebBrowserStyleInfo;
import org.eclipse.actf.model.ui.editors.ie.BrowserIE_Plugin;
import org.eclipse.actf.model.ui.editors.ie.events.INewWiondow2EventListener;
import org.eclipse.actf.model.ui.editors.ie.events.IWindowClosedEventListener;
import org.eclipse.actf.model.ui.editors.ie.internal.events.BeforeNavigate2Parameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.BrowserEventListener;
import org.eclipse.actf.model.ui.editors.ie.internal.events.DocumentCompleteParameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.NavigateComplete2Parameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.NavigateErrorParameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.NewWindow2Parameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.ProgressChangeParameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.StatusTextChangeParameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.TitleChangeParameters;
import org.eclipse.actf.model.ui.editors.ie.internal.events.WindowClosingParameters;
import org.eclipse.actf.model.ui.editors.ie.win32.RegistryUtil;
import org.eclipse.actf.util.logging.DebugPrintUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class WebBrowserIEImpl implements IWebBrowserACTF, BrowserEventListener {

	private WebBrowserToolbar toolbar;

	private WebBrowserIEComposite browserComposite;

	private boolean _inNavigation = false;

	private boolean _inReload = false;

	private boolean _inStop = false;

	private boolean _inJavascript = false;

	// TODO back,forw,stop,replace,etc.
	private boolean _urlExist;

	private int _navigateErrorCode;

	private IModelServiceHolder _holder = null;

	private boolean onloadPopupBlock = true;

	// private boolean allowNewWindow = false;

	private IModelServiceScrollManager scrollManager;

	private int scrollbarWidth;

	private DomByCom domByCom;

	private INewWiondow2EventListener newWindow2EventListener = null;

	private IWindowClosedEventListener windowClosedEventListener = null;

	public WebBrowserIEImpl(IModelServiceHolder holder, Composite parent,
			String startURL) {
		this._holder = holder;

		GridLayout gridLayout = new GridLayout();
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		parent.setLayout(gridLayout);

		toolbar = new WebBrowserToolbar(this, parent, SWT.NONE);
		browserComposite = new WebBrowserIEComposite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		browserComposite.setLayout(layout);
		browserComposite.setLayoutData(new GridData(GridData.FILL_BOTH));

		if (null == startURL) {
			startURL = "about:blank"; //$NON-NLS-1$
		}
		toolbar.setAddressTextString(startURL);

		browserComposite.setDisableScriptDebugger(true);

		browserComposite.addBrowserEventListener(this);

		scrollManager = new WebBrowserIEScrollManager(this);

		domByCom = new DomByCom(getIWebBrowser2());

		navigate(startURL);
	}

	public void setFocusAddressText(boolean selectAll) {
		toolbar.setFocusToAddressText(selectAll);
	}

	public void showAddressText(boolean flag) {
		toolbar.showAddressText(flag);
	}

	/*
	 * browse commands
	 */

	public void navigate(String url) {

		toolbar.setAddressTextString(url);

		this._urlExist = true;
		this._navigateErrorCode = 200;

		browserComposite.navigate(url);

		// TODO file:// case (ReadyState = 1)
		// System.out.println("State:"+getReadyState());
	}

	public void goBackward() {
		// TODO rename?
		browserComposite.goBack();
	}

	public void goForward() {
		browserComposite.goForward();
	}

	public void navigateStop() {
		if (_inNavigation || _inReload) {
			_inStop = true;
			_inNavigation = false;
			_inReload = false;
			_inJavascript = false;
		}
		browserComposite.stop();
	}

	public void navigateRefresh() {
		if (!_inReload) {
			_inReload = true;
			_inJavascript = false;
			WebBrowserEventExtension.myRefresh(WebBrowserIEImpl.this);
		}
		browserComposite.refresh();
	}

	/**
	 * @param isWhole
	 * @return (browserSizeX, browserSizeY, pageSizeX, pageSizeY)
	 */
	public ModelServiceSizeInfo getBrowserSize(boolean isWhole) {
		int[] size = new int[] { 1, 1, 1, 1 };
		int width = browserComposite.getWidth() - 4;
		int height = browserComposite.getHeight() - 4;
		size[0] = width - scrollbarWidth;
		size[1] = height;
		size[2] = width - scrollbarWidth;
		size[3] = height;
		if (isWhole) {
			int[] tmpSize = browserComposite.getWholeSize();
			if (tmpSize.length == 2 && tmpSize[0] > -1 && tmpSize[1] > -1) {
				size[2] = tmpSize[0];
				size[3] = tmpSize[1];
				if (tmpSize[0] > size[0]) {
					size[1] -= scrollbarWidth;
				}
			}
		}

		return (new ModelServiceSizeInfo(size[0], size[1], size[2], size[3]));
	}

	/*
	 * navigation result
	 */

	public int getReadyState() {
		return browserComposite.getReadyState();
	}

	// TODO add to IWebBrowser Interface
	// Browser properties
	// "Width"
	// "Height"
	// "Left"
	// "Top"
	// "BrowserType"
	// "Silent"
	// "setSilent"

	// TODO remove?
	public boolean isReady() {
		return (getReadyState() == READYSTATE_COMPLETE);
	}

	public String getURL() {
		return browserComposite.getLocationURL();
	}

	public String getLocationName() {
		return browserComposite.getLocationName();
	}

	public boolean isUrlExists() {
		// TODO
		return this._urlExist;
	}

	public int getNavigateErrorCode() {
		// TODO
		return this._navigateErrorCode;
	}

	/*
	 * Scroll
	 */

	public IModelServiceScrollManager getScrollManager() {
		return scrollManager;
	}

	public void scrollY(int y) {
		browserComposite.scroll(0, y, WebBrowserIEComposite.SCROLL_BY);
	}

	public void scrollTo(int x, int y) {
		browserComposite.scroll(x, y, WebBrowserIEComposite.SCROLL_TO);
	}

	/*
	 * browser setting
	 */
	public void setHlinkStop(boolean bStop) {
		// TODO low priority
	}

	public void setWebBrowserSilent(boolean bSilent) {
		browserComposite.setSilent(bSilent);
	}

	public void setDisableScriptDebugger(boolean bDisable) {
		browserComposite.setDisableScriptDebugger(bDisable);
	}

	public void setDisplayImage(boolean display) {
		// TODO
	}

	public boolean isDisableScriptDebugger() {
		return browserComposite.getDisableScriptDebugger();
	}

	public void setFontSize(int fontSize) {
		browserComposite.setFontSize(fontSize);
	}

	public int getFontSize() {
		return browserComposite.getFontSize();
	}

	/*
	 * highlight
	 */

	public void highlightElementById(String idVal) {
		// TODO low priority
	}

	public void hightlightElementByAttribute(String name, String value) {
		// TODO low priority
	}

	public void recoveryHighlight() {
		// TODO low priority
	}

	RGB getAnchorColor() {
		String color = RegistryUtil
				.getIERegistryString(RegistryUtil.IE_ANCHOR_COLOR);
		return getRGB(color);
	}

	RGB getVisitedAnchorColor() {
		String color = RegistryUtil
				.getIERegistryString(RegistryUtil.IE_ANCHOR_COLOR_VISITED);
		return getRGB(color);
	}

	private RGB getRGB(String color) {
		if (null != color) {
			try {
				String[] strArray = color.split(","); //$NON-NLS-1$
				return new RGB(Integer.parseInt(strArray[0]), Integer
						.parseInt(strArray[1]), Integer.parseInt(strArray[2]));
			} catch (Exception e) {
			}
		}
		return null;
	}

	public int getIWebBrowser2() {
		return browserComposite.getBrowserAddress();
	}

	public String[] getSupportMIMETypes() {
		return MIMETYPES_HTML;
	}

	public String[] getSupportExtensions() {
		return EXTS_HTML;
	}

	public void open(String url) {
		navigate(url);
	}

	public void open(File target) {
		if (null != target) {
			// TODO test
			navigate(target.getAbsolutePath());
		}
	}

	public Document getDocument() {
		try {
			File tmpF = BrowserIE_Plugin.getDefault().createTempFile("actf", "html");
			HtmlParserUtil hpu = new HtmlParserUtil();
			hpu.parse(new FileInputStream(tmpF));
			return hpu.getSHDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Document getLiveDocument() {
		return domByCom.getDocument();
	}

	public Composite getTargetComposite() {
		return browserComposite;
	}

	public File saveDocumentAsHTMLFile(String file) {
		if (null != file) {
			// TODO replace with DomByCOM (need write as XML support)
			boolean saveLiveDOM = (browserComposite.saveLiveDom(file));
			if (!saveLiveDOM) {
				return new File(file);
			}
		}
		return null;
	}

	public File saveOriginalDocument(String file) {
		if (null != file) {
			boolean saveOrigHtmlSource = (browserComposite.save(file));
			if (!saveOrigHtmlSource) {
				return new File(file);
			}
		}
		return null;
	}

	public void jumpToNode(Node target) {
		// TODO impl for Runtime Dom
	}

	public String getCurrentMIMEType() {
		// TODO get info from browser
		return MIMETYPES_HTML[0];
	}

	public void setScrollbarWidth(int width) {
		// TODO Implement within Browser -> remove from interface
		// currently uses lowVisionView.getVarticalBarSize().x
		scrollbarWidth = width;
		scrollManager.setScrollBarWidth(width);
	}

	/*
	 * BrowserEventListener implementations
	 */

	public void beforeNavigate2(BeforeNavigate2Parameters param) {
		// _inNavigation = true;
		String target = param.getUrl();
		DebugPrintUtil.debugPrintln("BN: " + target + " "
				+ param.getTargetFrameName());
		if (!_inReload) {
			if (!target.startsWith("javascript")) { // TODO //$NON-NLS-1$
				_inJavascript = false;
				_inNavigation = true;
				_inReload = false;
			} else {
				_inJavascript = true;
				_inNavigation = false;
				_inReload = false;
			}
		}
		WebBrowserEventExtension.beforeNavigate(this, target, param
				.getTargetFrameName(), _inNavigation);

	}

	public void documentComplete(DocumentCompleteParameters param) {
		if (param.isTopWindow()) {
			WebBrowserEventExtension.myDocumentComplete(this);
			// System.out.println("myDocComplete");
			_inNavigation = false;
			_inJavascript = false;
			_inReload = false;
		}
		// System.out.println("Document Complete:"+param.getUrl();
	}

	public void navigateComplete2(NavigateComplete2Parameters param) {
		WebBrowserEventExtension.navigateComplete(this, param.getUrl());
		toolbar
				.setAddressTextString(browserComposite.getLocationURL()/* param.getUrl() */);
		DebugPrintUtil.debugPrintln("NavigateComplete2");
	}

	public void navigateError(NavigateErrorParameters param) {
		DebugPrintUtil.debugPrintln("Navigate Error. URL:" + param.getUrl()
				+ " Status Code:" + param.getStatusCode());

		_navigateErrorCode = param.getStatusCode();
		_urlExist = false;
		_inNavigation = false;
	}

	public void newWindow2(NewWindow2Parameters param) {
		if (_inNavigation && onloadPopupBlock/* !browser2.READYSTATE_COMPLETE */) {
			// TODO
			param.setCancel(true);
		} else if (newWindow2EventListener != null) {
			newWindow2EventListener.newWindow2(param);
		}
	}

	public void progressChange(ProgressChangeParameters param) {
		int prog = param.getProgress();
		int progMax = param.getProgressMax();
		WebBrowserEventExtension.progressChange(this, prog, progMax);
		DebugPrintUtil.debugPrintln("Stop: " + _inStop + " Reload: "
				+ _inReload + " inJavaScript: " + _inJavascript
				+ " navigation: " + _inNavigation);
		if (_inStop) {
			if (prog == 0 && progMax == 0) {
				_inStop = false;
				DebugPrintUtil.debugPrintln("stop fin");
				WebBrowserEventExtension.navigateStop(this);
			}
		} else if (_inReload) {
			if (prog == 0 && progMax == 0) {
				_inReload = false;
				DebugPrintUtil.debugPrintln("reload fin");
				WebBrowserEventExtension.myRefreshComplete(this);
			}
		} else if (_inJavascript) {
			if (prog == -1 && progMax == -1) {
				_inJavascript = false;
				DebugPrintUtil.debugPrintln("javascript fin");
			}
		} else if (!_inNavigation && !(prog == 0 && progMax == 0)) {
			// 0/0 is complete
			_inReload = true;
			DebugPrintUtil.debugPrintln("reload");
			WebBrowserEventExtension.myRefresh(this);
		}
	}

	public void statusTextChange(StatusTextChangeParameters param) {
		// System.out.println(param.getText());
	}

	public void titleChange(TitleChangeParameters param) {
		try {
			String title = param.getText();
			WebBrowserEventExtension.titleChange(this, title);
			DebugPrintUtil.debugPrintln("TitleChange");
			if (!(_inNavigation || _inStop)) {
				if (!_inReload) {
					_inReload = true;
					_inJavascript = false;
					DebugPrintUtil.debugPrintln("reload");
					WebBrowserEventExtension.myRefresh(this);
				}
			}
			_holder.setEditorTitle(title);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void windowClosing(WindowClosingParameters param) {
	}

	public void windowClosed() {
		if (windowClosedEventListener != null) {
			windowClosedEventListener.windowClosed();
		}
	}

	public Object getAttribute(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getID() {
		return WebBrowserIEImpl.class.getName() + ":" + this;
	}

	public String getTitle() {
		return getLocationName();
	}

	public void setNewWindow2EventListener(
			INewWiondow2EventListener newWindow2EventListner) {
		this.newWindow2EventListener = newWindow2EventListner;
	}

	public void setWindowClosedEventListener(
			IWindowClosedEventListener windowClosingEventListener) {
		this.windowClosedEventListener = windowClosingEventListener;
	}

	public ImagePositionInfo[] getAllImagePosition() {
		return browserComposite.getAllImagePosition();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.IModelService#getModelServiceHolder()
	 */
	public IModelServiceHolder getModelServiceHolder() {
		return _holder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.IWebBrowserACTF#getStyleInfo()
	 */
	public IWebBrowserStyleInfo getStyleInfo() {
		// TODO obtain current style info from live DOM [233615]
		// need to wait IPZilla [2323]
		return new WebBrowserStyleInfoImpl(this);
	}

}
