/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie;

import java.net.URL;

import org.eclipse.actf.model.dom.dombycom.IElementEx;
import org.eclipse.actf.model.dom.dombycom.IStyle;
import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;
import org.eclipse.actf.util.xpath.XPathCreator;
import org.eclipse.swt.graphics.Rectangle;

public class CurrentStylesImpl implements ICurrentStyles {

	private static final String A_VISIBILITY = "visibility";

	private static final String A_TEXT_DECORATION = "textDecoration";

	private static final String A_TEXT_ALIGN = "textAlign";

	private static final String A_POSITION = "position";

	private static final String A_LINE_HEIGHT = "lineHeight";

	private static final String A_LETTER_SPACING = "letterSpacing";

	private static final String A_FONT_VARIANT = "fontVariant";

	private static final String A_FONT_STYLE = "fontStyle";

	private static final String A_FONT_SIZE = "fontSize";

	private static final String A_FONT_FAMILY = "fontFamily";

	private static final String A_DISPLAY = "display";

	private static final String A_COLOR = "color";

	private static final String A_BACKGROUND_REPEAT = "backgroundRepeat";

	private static final String A_BACKGROUND_COLOR = "backgroundColor";

	private static final String HREF = "href";

	private static final String TAG_A = "A";

	private final String xpath;

	private final String tagName;

	private final Rectangle rect;

	private final String backgroundColor;

	private final String backgroundRepeat;

	private final String color;

	private final String fontFamily;

	private final String fontSize;

	private final String fontStyle;

	private final String fontVariant;

	private final String lineHeight;

	private final String textAlign;

	private final String visibility;

	private final String display;

	private final String position;

	private final String textDecoration;

	private final String letterSpacing;

	private boolean isLink;

	private URL target = null;

	/**
	 * 
	 */
	public CurrentStylesImpl(IElementEx element, URL baseUrl) {
		rect = element.getLocation();
		xpath = XPathCreator.childPathSequence(element);
		tagName = element.getTagName();

		isLink = false;
		if (TAG_A.equalsIgnoreCase(tagName)) {
			String href = element.getAttribute(HREF);
			if (null != href) {
				if (null != baseUrl) {
					try {
						target = new URL(baseUrl, href);
						isLink = true;
					} catch (Exception e) {
						// e.printStackTrace();
						try {
							target = new URL(href);
							isLink = true;
						} catch (Exception e2) {
							// e2.printStackTrace();
						}
					}
				} else {
					try {
						target = new URL(href);
						isLink = true;
					} catch (Exception e2) {
						//e2.printStackTrace();
					}
				}
			}
		}

		IStyle style = element.getStyle();
		backgroundColor = (String) style.get(A_BACKGROUND_COLOR);
		backgroundRepeat = (String) style.get(A_BACKGROUND_REPEAT);
		color = (String) style.get(A_COLOR);
		display = (String) style.get(A_DISPLAY);
		fontFamily = (String) style.get(A_FONT_FAMILY);
		fontSize = (String) style.get(A_FONT_SIZE);
		fontStyle = (String) style.get(A_FONT_STYLE);
		fontVariant = (String) style.get(A_FONT_VARIANT);
		letterSpacing = (String) style.get(A_LETTER_SPACING);
		lineHeight = (String) style.get(A_LINE_HEIGHT);
		position = (String) style.get(A_POSITION);
		textAlign = (String) style.get(A_TEXT_ALIGN);
		textDecoration = (String) style.get(A_TEXT_DECORATION);
		visibility = (String) style.get(A_VISIBILITY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getBackgroundColor()
	 */
	public String getBackgroundColor() {
		return backgroundColor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getBackgroundRepeat()
	 */
	public String getBackgroundRepeat() {
		return backgroundRepeat;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getColor()
	 */
	public String getColor() {
		return color;
	}

	// /* (non-Javadoc)
	// * @see
	// org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getCssText()
	// */
	// public String getCssText() {
	// return cssText;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getDisplay()
	 */
	public String getDisplay() {
		return display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontFamily()
	 */
	public String getFontFamily() {
		return fontFamily;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontSize()
	 */
	public String getFontSize() {
		return fontSize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontStyle()
	 */
	public String getFontStyle() {
		return fontStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontVariant()
	 */
	public String getFontVariant() {
		return fontVariant;
	}

	// /* (non-Javadoc)
	// * @see
	// org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontWeight()
	// */
	// public String getFontWeight() {
	// return fontWeight;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getLetterSpacing()
	 */
	public String getLetterSpacing() {
		return letterSpacing;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getLineHeight()
	 */
	public String getLineHeight() {
		return lineHeight;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getPosition()
	 */
	public String getPosition() {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTextAlign()
	 */
	public String getTextAlign() {
		return textAlign;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTextDecoration()
	 */
	public String getTextDecoration() {
		return textDecoration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getVisibility()
	 */
	public String getVisibility() {
		return visibility;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTagName()
	 */
	public String getTagName() {
		return tagName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getXPath()
	 */
	public String getXPath() {
		return xpath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getRectangle()
	 */
	public Rectangle getRectangle() {
		return rect;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getLinkURL()
	 */
	public URL getLinkURL() {
		return target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#isLink()
	 */
	public boolean isLink() {
		return isLink;
	}

}
