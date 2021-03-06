/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.actf.model.internal.ui.editors.ie.events.BeforeNavigate2Parameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.BrowserEventListener;
import org.eclipse.actf.model.internal.ui.editors.ie.events.DocumentCompleteParameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.NavigateComplete2Parameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.NavigateErrorParameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.NewWindow2Parameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.ProgressChangeParameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.StatusTextChangeParameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.TitleChangeParameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.WindowClosingParameters;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.BeforeNavigate2ParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.DocumentCompleteParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.NavigateComplete2ParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.NavigateErrorParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.NewWindow2ParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.ProgressChangeParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.StatusTextChangeParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.TitleChangeParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.events.impl.WindowClosingParametersImpl;
import org.eclipse.actf.model.internal.ui.editors.ie.win32.IPersistFile;
import org.eclipse.actf.model.ui.ImagePositionInfo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.internal.Callback;
import org.eclipse.swt.internal.ole.win32.COM;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.ole.win32.OLE;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleControlSite;
import org.eclipse.swt.ole.win32.OleEvent;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.OleListener;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class WebBrowserIEComposite extends Composite {

	public static final int SCROLL_BY = 0;

	public static final int SCROLL_TO = 1;

	OleFrame frame;

	OleControlSite site;// , site2;

	OleAutomation auto;// , auto2;

	boolean disposing = false;

	boolean bDisableScriptDebugger = false;

	private BrowserEventListener[] eventListeners = new BrowserEventListener[0];

	static final String BROWSER_PROGID = "Shell.Explorer"; //$NON-NLS-1$

	private int oldProc = 0;

	private boolean isClosed = false;

	// Event IDs
	// TODO in capital
	@SuppressWarnings("unused")
	private static final int EventID_BEFORENAVIGATE = 100,
			EventID_NAVIGATECOMPLETE = 101, EventID_STATUSTEXTCHANGE = 102,
			EventID_QUIT = 103, EventID_DOWNLOADCOMPLETE = 104,
			EventID_COMMANDSTATECHANGE = 105, EventID_DOWNLOADBEGIN = 106,
			EventID_NEWWINDOW = 107, EventID_PROGRESSCHANGE = 108,
			EventID_WINDOWMOVE = 109, EventID_WINDOWRESIZE = 110,
			EventID_WINDOWACTIVATE = 111, EventID_PROPERTYCHANGE = 112,
			EventID_TITLECHANGE = 113, EventID_TITLEICONCHANGE = 114,
			EventID_FRAMEBEFORENAVIGATE = 200,
			EventID_FRAMENAVIGATECOMPLETE = 201, EventID_FRAMENEWWINDOW = 204,
			EventID_BEFORENAVIGATE2 = 250, EventID_NEWWINDOW2 = 251,
			EventID_NAVIGATECOMPLETE2 = 252, EventID_ONQUIT = 253,
			EventID_ONVISIBLE = 254, EventID_ONTOOLBAR = 255,
			EventID_ONMENUBAR = 256, EventID_ONSTATUSBAR = 257,
			EventID_ONFULLSCREEN = 258, EventID_DOCUMENTCOMPLETE = 259,
			EventID_ONTHEATERMODE = 260, EventID_ONADDRESSBAR = 261,
			EventID_WINDOWSETRESIZABLE = 262, EventID_WINDOWCLOSING = 263,
			EventID_WINDOWSETLEFT = 264, EventID_WINDOWSETTOP = 265,
			EventID_WINDOWSETWIDTH = 266, EventID_WINDOWSETHEIGHT = 267,
			EventID_CLIENTTOHOSTWINDOW = 268, EventID_SETSECURELOCKICON = 269,
			EventID_FILEDOWNLOAD = 270, EventID_NAVIGATEERROR = 271,
			EventID_PRIVACYIMPACTEDSTATECHANGE = 272, EventID_NEWWINDOW3 = 273;

	public WebBrowserIEComposite(Composite parent, int style) {
		super(parent, style);
		/*
		 * Create OLE frame
		 */
		try {
			frame = new OleFrame(this, SWT.NONE);
			frame.setLayoutData(new GridData(GridData.FILL_BOTH));
			site = new WebBrowserIEControlSite(frame, SWT.NONE, BROWSER_PROGID);
			site.doVerb(OLE.OLEIVERB_INPLACEACTIVATE);
			auto = new OleAutomation(site);

		} catch (SWTException e) {
			dispose();
			SWT.error(SWT.ERROR_NO_HANDLES);
		}

		/*
		 * Misc settings
		 */
		setBrowserBoolean("RegisterAsBrowser", true); //$NON-NLS-1$
		setBrowserBoolean("RegisterAsDropTarget", true); //$NON-NLS-1$

		/*
		 * Add SWT Event handlers
		 */
		Listener swtListener = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.Dispose: {
					if (!disposing) {
						disposing = true;
						notifyListeners(e.type, e);
						e.type = SWT.NONE;
						if (auto != null) {
							auto.dispose();
							auto = null;
						}
					}
					break;
				}
				case SWT.Resize: {
					frame.setBounds(getClientArea());
					break;
				}
				}
			}
		};
		addListener(SWT.Dispose, swtListener);
		addListener(SWT.Resize, swtListener);

		Listener keyListener = new Listener() {
			public void handleEvent(Event e) {
				switch (e.type) {
				case SWT.KeyDown:
				case SWT.KeyUp: {
					notifyListeners(e.type, e);
					break;
				}
				}
			}
		};
		site.addListener(SWT.KeyDown, keyListener);
		site.addListener(SWT.KeyUp, keyListener);

		/*
		 * Add OLE Event handlers
		 */
		OleListener browserListener = new OleListener() {
			public void handleEvent(OleEvent event) {
				if (null != auto) {
					try {
						switch (event.type) {
						case EventID_BEFORENAVIGATE2:
							onBeforeNavigate2(new BeforeNavigate2ParametersImpl(
									event));
							break;
						case EventID_NAVIGATECOMPLETE2:
							onNavigateComplete2(new NavigateComplete2ParametersImpl(
									event));
							break;
						case EventID_NAVIGATEERROR:
							onNavigateError(new NavigateErrorParametersImpl(
									event));
							break;
						case EventID_DOCUMENTCOMPLETE:
							onDocumentComplete(new DocumentCompleteParametersImpl(
									event));
							break;
						case EventID_NEWWINDOW2:
							onNewWindow2(new NewWindow2ParametersImpl(event));
							break;
						case EventID_WINDOWCLOSING:
							onWindowClosing(new WindowClosingParametersImpl(
									event));
							break;
						case EventID_STATUSTEXTCHANGE:
							onStatusTextChange(new StatusTextChangeParametersImpl(
									event));
							break;
						case EventID_TITLECHANGE:
							onTitleChange(new TitleChangeParametersImpl(event));
							break;
						case EventID_PROGRESSCHANGE:
							onProgressChange(new ProgressChangeParametersImpl(
									event));
							break;
						default:
							System.out
									.println("event.type=" + event.type + " ,event.arguments=" + dumpArags(event.arguments)); //$NON-NLS-1$ //$NON-NLS-2$
							break;
						}
					} catch (Exception e) {
						System.err.println(dumpArags(event.arguments));
						e.printStackTrace();
					}
				}
			}
		};

		site.addEventListener(EventID_BEFORENAVIGATE2, browserListener);
		site.addEventListener(EventID_DOCUMENTCOMPLETE, browserListener);
		site.addEventListener(EventID_NAVIGATECOMPLETE2, browserListener);
		site.addEventListener(EventID_NAVIGATEERROR, browserListener);
		site.addEventListener(EventID_NEWWINDOW2, browserListener);
		site.addEventListener(EventID_PROGRESSCHANGE, browserListener);
		site.addEventListener(EventID_STATUSTEXTCHANGE, browserListener);
		site.addEventListener(EventID_TITLECHANGE, browserListener);
		site.addEventListener(EventID_WINDOWCLOSING, browserListener);

		// for JavaScript window.close()

		Callback callback = new Callback(this, "siteWindowProc", 4); //$NON-NLS-1$
		int address = callback.getAddress();
		if (address != 0) {
			oldProc = OS.GetWindowLong(site.handle, OS.GWL_WNDPROC);
			OS.SetWindowLong(site.handle, OS.GWL_WNDPROC, address);
		} else {
			callback.dispose();
		}

	}

	/*
	 * Public interfaces
	 */

	public void addBrowserEventListener(BrowserEventListener listener) {
		Set<BrowserEventListener> set = getEventListeners();
		set.add(listener);
		setEventListeners(set);
	}

	public void removeBrowserEventListener(BrowserEventListener listener) {
		Set<BrowserEventListener> set = getEventListeners();
		set.remove(listener);
		setEventListeners(set);
	}

	public int getLeft() {
		return getBrowserInteger("Left"); //$NON-NLS-1$
	}

	public int getTop() {
		return getBrowserInteger("Top"); //$NON-NLS-1$
	}

	public int getWidth() {
		return getBrowserInteger("Width"); //$NON-NLS-1$
	}

	public int getHeight() {
		return getBrowserInteger("Height"); //$NON-NLS-1$
	}

	public int getReadyState() {
		return getBrowserInteger("ReadyState"); //$NON-NLS-1$
	}

	public String getType() {
		return getBrowserString("Type"); //$NON-NLS-1$
	}

	public String getLocationURL() {
		return getBrowserString("LocationURL"); //$NON-NLS-1$
	}

	public String getLocationName() {
		return getBrowserString("LocationName"); //$NON-NLS-1$
	}

	public boolean getSilent() {
		return getBrowserBoolean("Silent"); //$NON-NLS-1$
	}

	public void setSilent(boolean silent) {
		setBrowserBoolean("Silent", silent); //$NON-NLS-1$
	}

	public boolean getDisableScriptDebugger() {
		return bDisableScriptDebugger;
	}

	public void setDisableScriptDebugger(boolean bDisable) {
		bDisableScriptDebugger = bDisable;
	}

	public boolean goBack() {
		return invokeBrowserMethod("GoBack"); //$NON-NLS-1$
	}

	public boolean goForward() {
		return invokeBrowserMethod("GoForward"); //$NON-NLS-1$
	}

	public boolean stop() {
		return invokeBrowserMethod("Stop"); //$NON-NLS-1$
	}

	public boolean refresh() {
		return invokeBrowserMethod("Refresh"); //$NON-NLS-1$
	}

	public boolean navigate(String url) {
		int[] pIdNavigate = auto
				.getIDsOfNames(new String[] { "Navigate", "URL" }); //$NON-NLS-1$ //$NON-NLS-2$
		if (null != pIdNavigate) {
			Variant varUrl = new Variant(url);
			Variant varResult = auto.invoke(pIdNavigate[0],
					new Variant[] { varUrl }, new int[] { pIdNavigate[1] });
			varUrl.dispose();
			if (varResult != null) {
				boolean result = varResult.getType() == OLE.VT_EMPTY;
				varResult.dispose();
				return result;
			}
		}
		return false;
	}

	public boolean navigate2(String url, int flags, String targetFrameName,
			Object postData, String headers) {
		int[] pIdNavigate2 = auto.getIDsOfNames(new String[] { "Navigate2" }); //$NON-NLS-1$
		if (null != pIdNavigate2) {
			Variant varUrl = new Variant(url);
			Variant varFlags = new Variant(flags);
			Variant varTargetFrameName = null == targetFrameName ? new Variant()
					: new Variant(targetFrameName);
			Variant varHeaders = null == headers ? new Variant() : new Variant(
					headers);
			if (!(postData instanceof Variant)) {
				postData = new Variant();
			}
			Variant varResult = auto.invoke(pIdNavigate2[0], new Variant[] {
					varUrl, varFlags, varTargetFrameName, (Variant) postData,
					varHeaders });
			varUrl.dispose();
			varFlags.dispose();
			varTargetFrameName.dispose();
			varHeaders.dispose();
			if (varResult != null) {
				boolean result = varResult.getType() == OLE.VT_EMPTY;
				varResult.dispose();
				return result;
			}
		}
		return false;
	}

	public boolean setFontSize(int nSize) {
		Variant varIn = new Variant(nSize);
		try {
			return OLE.S_OK == site.exec(OLE.OLECMDID_ZOOM,
					OLE.OLECMDEXECOPT_DONTPROMPTUSER, varIn, null);
		} catch (Exception e) {
		} finally {
			varIn.dispose();
		}
		return false;
	}

	public int getFontSize() {
		Variant varOut = new Variant();
		try {
			if (OLE.S_OK == site.exec(OLE.OLECMDID_ZOOM,
					OLE.OLECMDEXECOPT_DONTPROMPTUSER, null, varOut)) {
				if (OLE.VT_I4 == varOut.getType()) {
					return varOut.getInt();
				}
			}
		} catch (Exception e) {
		} finally {
			varOut.dispose();
		}
		return -1;
	}

	@SuppressWarnings("nls")
	public boolean saveLiveDom(String fileName) {
		Variant varDocument = getBrowserVariant("Document"); //$NON-NLS-1$
		if (null != varDocument) {
			try {
				OleAutomation document = varDocument.getAutomation();
				Variant varDocEle = getVariant(document, "documentElement");
				document.dispose();
				if (null != varDocEle) {
					try {
						OleAutomation docEle = varDocEle.getAutomation();
						Variant varOuterHTML = getVariant(docEle, "outerHTML");
						docEle.dispose();
						if (null != varOuterHTML) {
							try {
								// for bug
								// replace "alt=\u3000" with "alt=\" \""
								// replace "alt=* " with "alt=\"*\" "

								PrintWriter pw = new PrintWriter(
										new OutputStreamWriter(
												new FileOutputStream(fileName),
												"UTF-8"));
								pw.println(varOuterHTML.getString());
								pw.flush();
								pw.close();
								return true;
							} catch (Exception e3) {
								e3.printStackTrace();
							} finally {
								varOuterHTML.dispose();
							}
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					} finally {
						varDocEle.dispose();
					}
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			} finally {
				varDocument.dispose();
			}
		}
		return false;
	}

	public boolean save(String fileName) {
		Variant varDocument = getBrowserVariant("Document"); //$NON-NLS-1$
		if (null != varDocument) {
			try {
				return saveDocument(varDocument, fileName);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				varDocument.dispose();
			}
		}
		return false;
	}

	public int getBrowserAddress() {
		Variant variant = new Variant(auto);
		return variant.getDispatch().getAddress();
	}

	@SuppressWarnings("nls")
	public int[] getWholeSize() {
		int[] result = new int[] { -1, -1 };
		Variant varDocument = getBrowserVariant("Document"); //$NON-NLS-1$
		if (null != varDocument) {
			try {
				OleAutomation document = varDocument.getAutomation();
				Variant varDocEle = getVariant(document, "documentElement");
				if (null != varDocEle) {
					try {
						OleAutomation docEle = varDocEle.getAutomation();
						result[0] = getIntFromOleAutomation(docEle,
								"scrollWidth");
						result[1] = getIntFromOleAutomation(docEle,
								"scrollHeight");
						docEle.dispose();

						int bodySize[] = getBodySize(document);

						// System.out.println(result[0]+" "+result[1]+" :
						// "+bodySize[0]+" "+bodySize[1]);

						if (result[0] < bodySize[0]) {
							result[0] = bodySize[0];
						}
						if (result[1] < bodySize[1]) {
							result[1] = bodySize[1];
						}

					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						varDocEle.dispose();
					}
				}
				document.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				varDocument.dispose();
			}
		}
		return (result);
	}

	@SuppressWarnings("nls")
	public ImagePositionInfo[] getAllImagePosition() {
		ImagePositionInfo[] result = new ImagePositionInfo[0];
		Variant varDocument = getBrowserVariant("Document");
		if (null != varDocument) {
			try {
				OleAutomation document = varDocument.getAutomation();
				Variant varImages = getVariant(document, "images");
				document.dispose();
				if (null != varImages) {
					try {
						OleAutomation images = varImages.getAutomation();
						result = getAllImagePosition(images);
						images.dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						varImages.dispose();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				varDocument.dispose();
			}
		}
		return result;
	}

	@SuppressWarnings("nls")
	public boolean scroll(int x, int y, int type) {
		if (type == 0 || type == 1) {
			Variant varDocument = getBrowserVariant("Document"); //$NON-NLS-1$
			if (null != varDocument) {
				try {
					OleAutomation document = varDocument.getAutomation();
					Variant varWindow = getVariant(document, "parentWindow");
					document.dispose();
					if (null != varWindow) {
						try {
							OleAutomation window = varWindow.getAutomation();
							String scrollType = "scrollBy";
							if (type == 1) {
								scrollType = "scrollTo";
							}
							int[] idScroll = window
									.getIDsOfNames(new String[] { scrollType });
							if (null != idScroll) {
								window.invoke(idScroll[0], new Variant[] {
										new Variant(x), new Variant(y) });
								window.dispose();
								return true;
							}
							window.dispose();
						} catch (Exception e1) {
							e1.printStackTrace();
						} finally {
							varWindow.dispose();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					varDocument.dispose();
				}
			}
		}
		return false;
	}

	/*
	 * Browser event handlers
	 */

	protected void onBeforeNavigate2(BeforeNavigate2Parameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].beforeNavigate2(param);
		}
	}

	protected void onNavigateComplete2(NavigateComplete2Parameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].navigateComplete2(param);
		}
	}

	protected void onNavigateError(NavigateErrorParameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].navigateError(param);
		}
	}

	protected void onDocumentComplete(DocumentCompleteParameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].documentComplete(param);
		}
	}

	protected void onNewWindow2(NewWindow2Parameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].newWindow2(param);
		}
	}

	protected void onWindowClosing(WindowClosingParameters param) {
		// param.setCancel(true);
		//
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].windowClosing(param);
		}
	}

	protected void onWindowClosed() {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].windowClosed();
		}
	}

	protected void onStatusTextChange(StatusTextChangeParameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].statusTextChange(param);
		}
	}

	protected void onTitleChange(TitleChangeParameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].titleChange(param);
		}
	}

	protected void onProgressChange(ProgressChangeParameters param) {
		for (int i = 0; i < eventListeners.length; i++) {
			eventListeners[i].progressChange(param);
		}
	}

	/*
	 * Private methods
	 */

	private Set<BrowserEventListener> getEventListeners() {
		return new LinkedHashSet<BrowserEventListener>(
				Arrays.asList(eventListeners));
	}

	private void setEventListeners(Set<BrowserEventListener> set) {
		eventListeners = set.toArray(new BrowserEventListener[set.size()]);
	}

	private static boolean saveDocument(Variant varDocument, String fileName) {
		if (OLE.VT_DISPATCH == varDocument.getType()) {
			int[] pPersistFile = new int[1];
			try {
				if (OLE.S_OK == varDocument.getDispatch().QueryInterface(
						COM.IIDIPersistFile, pPersistFile)) {
					int pStrAddress = COM
							.SysAllocString((fileName + "\0").toCharArray()); //$NON-NLS-1$
					try {
						return OLE.S_OK == new IPersistFile(pPersistFile[0])
								.Save(pStrAddress, false);
					} finally {
						COM.SysFreeString(pStrAddress);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean invokeBrowserMethod(String name) {
		int[] pIdName = auto.getIDsOfNames(new String[] { name });
		if (null != pIdName) {
			Variant varResult = auto.invoke(pIdName[0]);
			if (null != varResult) {
				try {
					return varResult.getType() == OLE.VT_EMPTY;
				} finally {
					varResult.dispose();
				}
			}
		}
		return false;
	}

	private Variant getBrowserVariant(String name) {
		int[] pIdName = auto.getIDsOfNames(new String[] { name });
		if (null != pIdName) {
			return auto.getProperty(pIdName[0]);
		}
		return null;
	}

	private int getBrowserInteger(String name) {
		Variant varResult = getBrowserVariant(name);
		if (null != varResult) {
			try {
				return varResult.getInt();
			} finally {
				varResult.dispose();
			}
		}
		return 0;
	}

	private String getBrowserString(String name) {
		Variant varResult = getBrowserVariant(name);
		if (null != varResult) {
			try {
				return varResult.getString();
			} finally {
				varResult.dispose();
			}
		}
		return null;
	}

	private boolean getBrowserBoolean(String name) {
		Variant varResult = getBrowserVariant(name);
		if (null != varResult) {
			try {
				return varResult.getBoolean();
			} finally {
				varResult.dispose();
			}
		}
		return false;
	}

	private boolean setBrowserVariant(String name, Variant var) {
		int[] pIdName = auto.getIDsOfNames(new String[] { name });
		if (null != pIdName) {
			return auto.setProperty(pIdName[0], var);
		}
		return false;
	}

	private boolean setBrowserInteger(String name, int value) {
		return setBrowserVariant(name, new Variant(value));
	}

	private boolean setBrowserString(String name, String value) {
		return setBrowserVariant(name, new Variant(value));
	}

	private boolean setBrowserBoolean(String name, boolean value) {
		return setBrowserVariant(name, new Variant(value));
	}

	@SuppressWarnings("nls")
	private int[] getBodySize(OleAutomation document) {
		int[] result = { -1, -1 };
		Variant varBody = getVariant(document, "body");
		if (null != varBody) {
			try {
				OleAutomation body = varBody.getAutomation();
				int scrollWidth = getIntFromOleAutomation(body, "scrollWidth");
				int offsetLeft = getIntFromOleAutomation(body, "offsetLeft");
				int scrollHeight = getIntFromOleAutomation(body, "scrollHeight");
				int offsetTop = getIntFromOleAutomation(body, "offsetTop");

				// System.out.println(scrollWidth+" "+offsetLeft+"
				// "+scrollHeight+" "+offsetTop);

				result[0] = scrollWidth + offsetLeft * 2;
				result[1] = scrollHeight + offsetTop * 2;

				body.dispose();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				varBody.dispose();
			}
		}
		return result;
	}

	@SuppressWarnings("nls")
	private ImagePositionInfo[] getAllImagePosition(OleAutomation images) {
		ImagePositionInfo[] result = new ImagePositionInfo[0];
		int size = getIntFromOleAutomation(images, "length");
		if (size > -1) {
			ImagePositionInfo[] tmp = new ImagePositionInfo[size];
			int[] idItem = images.getIDsOfNames(new String[] { "Item" });
			for (int i = 0; i < size; i++) {
				Variant varImage = images.invoke(idItem[0],
						new Variant[] { new Variant(i) });
				if (null != varImage) {
					try {
						OleAutomation image = varImage.getAutomation();
						int[] bcr = getBoundingClientRect(image);
						int width = getIntFromOleAutomation(image, "Width");
						int height = getIntFromOleAutomation(image, "Height");
						String url = getStringFromOleAutomation(image, "src");
						image.dispose();

						// System.out.println(bcr[0]+" "+bcr[1]+" "+bcr[2]+"
						// "+bcr[3]+" "+width+" "+height+" "+url);

						tmp[i] = new ImagePositionInfo(bcr[0], bcr[1], width,
								height, url);

					} catch (Exception e2) {
						e2.printStackTrace();
						return result;
					} finally {
						varImage.dispose();
					}
				}
			}
			result = tmp;
		}
		return result;
	}

	@SuppressWarnings("nls")
	private int[] getBoundingClientRect(OleAutomation image) {
		int[] result = new int[] { 0, 0 };// Left, Top
		if (null != image) {
			int[] idBCR = image
					.getIDsOfNames(new String[] { "getBoundingClientRect" });
			Variant varBCR = image.invoke(idBCR[0]);
			if (null != varBCR) {
				try {
					OleAutomation bcr = varBCR.getAutomation();
					result = new int[] { getIntFromOleAutomation(bcr, "Left"),
							getIntFromOleAutomation(bcr, "Top"),
					// getIntFromOleAutomation(bcr, "Right"),
					// getIntFromOleAutomation(bcr, "Bottom")
					};
					bcr.dispose();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					varBCR.dispose();
				}
			}
		}
		return result;//
	}

	private Variant getVariant(OleAutomation target, String name) {
		if (null != target) {
			int[] pIdName = target.getIDsOfNames(new String[] { name });
			if (null != pIdName) {
				return target.getProperty(pIdName[0]);
			}
		}
		return null;
	}

	private int getIntFromOleAutomation(OleAutomation target, String name) {
		Variant varResult = getVariant(target, name);
		if (null != varResult) {
			try {
				return varResult.getInt();
			} finally {
				varResult.dispose();
			}
		}
		return -1;// TODO
	}

	private String getStringFromOleAutomation(OleAutomation target, String name) {
		Variant varResult = getVariant(target, name);
		if (null != varResult) {
			try {
				return varResult.getString();
			} finally {
				varResult.dispose();
			}
		}
		return "";// TODO //$NON-NLS-1$
	}

	/**
	 * Window procedure
	 * 
	 * @param hwnd
	 * @param msg
	 * @param wParam
	 * @param lParam
	 * @return
	 */
	int siteWindowProc(int hwnd, int msg, int wParam, int lParam) {
		try {
			if (!isClosed && OS.WM_PARENTNOTIFY == msg
					&& wParam == OS.WM_DESTROY) {
				isClosed = true;
				onWindowClosed();
				return 0;
			}
			return OS.CallWindowProc(oldProc, hwnd, msg, wParam, lParam);
		} catch (Exception e) {
			System.out.println("error: siteWindowProc"); //$NON-NLS-1$
			// e.printStackTrace();
		}
		return 0;
	}

	/*
	 * for debug
	 */
	private static String dumpArags(Variant[] args) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				sb.append(", "); //$NON-NLS-1$
			}
			sb.append(i + ":"); //$NON-NLS-1$
			if ((OLE.VT_BYREF | OLE.VT_VARIANT) == args[i].getType()) {
				int byRef = args[i].getByRef();
				short[] dataType = new short[1];
				COM.MoveMemory(dataType, byRef, 2);
				String strType = ""; //$NON-NLS-1$
				int type = dataType[0];
				if ((type & OLE.VT_BYREF) != 0) {
					strType = "VT_BYREF|"; //$NON-NLS-1$
					type = type & ~OLE.VT_BYREF;
				}
				switch (type) {
				case OLE.VT_BOOL:
					strType += "VT_BOOL";break; //$NON-NLS-1$
				case OLE.VT_I2:
					strType += "VT_I2";break; //$NON-NLS-1$
				case OLE.VT_I4:
					strType += "VT_I4";break; //$NON-NLS-1$
				case OLE.VT_I8:
					strType += "VT_I8";break; //$NON-NLS-1$
				case OLE.VT_R4:
					strType += "VT_R4";break; //$NON-NLS-1$
				case OLE.VT_R8:
					strType += "VT_R8";break; //$NON-NLS-1$
				case OLE.VT_BSTR:
					strType += "VT_BSTR";break; //$NON-NLS-1$
				case OLE.VT_DISPATCH:
					strType += "VT_DISPATCH";break; //$NON-NLS-1$
				case OLE.VT_UNKNOWN:
					strType += "VT_UNKNOWN";break; //$NON-NLS-1$
				case OLE.VT_EMPTY:
					strType += "VT_EMPTY";break; //$NON-NLS-1$
				case OLE.VT_VARIANT:
					strType += "VT_VARIANT";break; //$NON-NLS-1$
				default:
					strType += "type=" + type;break; //$NON-NLS-1$
				}
				sb.append("REF_VARIANT(" + strType + ")"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				sb.append(args[i]);
			}
		}
		return sb.toString();
	}

	private Variant invokeWindowMethod(String method, Variant[] vars) {
		Variant varDocument = getBrowserVariant("Document"); //$NON-NLS-1$
		if (null != varDocument) {
			try {
				OleAutomation document = varDocument.getAutomation();
				Variant varWindow = getVariant(document, "parentWindow");
				document.dispose();
				if (null != varWindow) {
					try {
						OleAutomation window = varWindow.getAutomation();
						int[] id = window
								.getIDsOfNames(new String[] { method });
						if (null != id) {
							Variant result = window.invoke(id[0], vars);
							window.dispose();
							return result;
						}
						window.dispose();
					} catch (Exception e1) {
						e1.printStackTrace();
					} finally {
						varWindow.dispose();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				varDocument.dispose();
			}
		}
		return null;
	}

	public boolean clearInterval(int id) {
		Variant varResult = invokeWindowMethod("clearInterval",
				new Variant[] { new Variant(id) });
		if (varResult != null) {
			return true;
		}
		return false;
	}

	public boolean clearTimeout(int id) {
		Variant varResult = invokeWindowMethod("clearTimeout",
				new Variant[] { new Variant(id) });
		if (varResult != null) {
			return true;
		}
		return false;
	}

	public int setInterval(String script, int interval) {
		Variant[] vars = new Variant[] { new Variant(script),
				new Variant((long) interval), new Variant("JavaScript"),
				new Variant((long) 0) };
		Variant varResult = invokeWindowMethod("setInterval", vars);
		if (varResult != null) {
			return varResult.getInt();
		}
		return -1;
	}

	public int setTimeout(String script, int msec) {
		Variant[] vars = new Variant[] { new Variant(script),
				new Variant((long) msec), new Variant("JavaScript"),
				new Variant((long) 0) };
		Variant varResult = invokeWindowMethod("setTimeout", vars);
		if (varResult != null) {
			return varResult.getInt();
		}
		return -1;
	}

}
