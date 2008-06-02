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

package org.eclipse.actf.model.ui.editors.ie.impl;

import org.eclipse.actf.model.ui.editor.browser.ICurrentStyles;

public class CurrentStylesImpl implements ICurrentStyles {

    //TODO re-construct
    String xpath;
	
    String tagName;

    String backgroundColor;

    String backgroundRepeat;

    String color;

    String cssText;	//Style

    String fontWeight; //Style

    String fontFamily;

    String fontSize;

    String fontStyle;

    String fontVariant;

    String lineHeight;

    String textAlign;

    String visibility;

    String display;

    String position;

    String Height;

    String Width;

    String Top;

    String left;

    String textDecoration;

    String letterSpacing;

    String offsetWidth; //IHTMLElement

    String offsetHeight; //IHTMLElement

    String offsetTop; //IHTMLElement

    String offsetLeft; //IHTMLElement

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getBackgroundColor()
	 */
    public String getBackgroundColor() {
        return backgroundColor;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getBackgroundRepeat()
	 */
    public String getBackgroundRepeat() {
        return backgroundRepeat;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getColor()
	 */
    public String getColor() {
        return color;
    }

//    /* (non-Javadoc)
//	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getCssText()
//	 */
//    public String getCssText() {
//        return cssText;
//    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getDisplay()
	 */
    public String getDisplay() {
        return display;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontFamily()
	 */
    public String getFontFamily() {
        return fontFamily;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontSize()
	 */
    public String getFontSize() {
        return fontSize;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontStyle()
	 */
    public String getFontStyle() {
        return fontStyle;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontVariant()
	 */
    public String getFontVariant() {
        return fontVariant;
    }

//    /* (non-Javadoc)
//	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getFontWeight()
//	 */
//    public String getFontWeight() {
//        return fontWeight;
//    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getHeight()
	 */
    public String getHeight() {
        return Height;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getLeft()
	 */
    public String getLeft() {
        return left;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getLetterSpacing()
	 */
    public String getLetterSpacing() {
        return letterSpacing;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getLineHeight()
	 */
    public String getLineHeight() {
        return lineHeight;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getPosition()
	 */
    public String getPosition() {
        return position;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTextAlign()
	 */
    public String getTextAlign() {
        return textAlign;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTextDecoration()
	 */
    public String getTextDecoration() {
        return textDecoration;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTop()
	 */
    public String getTop() {
        return Top;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getVisibility()
	 */
    public String getVisibility() {
        return visibility;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getWidth()
	 */
    public String getWidth() {
        return Width;
    }

    /**
     * @param string
     */
    public void setBackgroundColor(String string) {
        backgroundColor = string;
    }

    /**
     * @param string
     */
    public void setBackgroundRepeat(String string) {
        backgroundRepeat = string;
    }

    /**
     * @param string
     */
    public void setColor(String string) {
        color = string;
    }

    /**
     * @param string
     */
    public void setCssText(String string) {
        cssText = string;
    }

    /**
     * @param string
     */
    public void setDisplay(String string) {
        display = string;
    }

    /**
     * @param string
     */
    public void setFontFamily(String string) {
        fontFamily = string;
    }

    /**
     * @param string
     */
    public void setFontSize(String string) {
        fontSize = string;
    }

    /**
     * @param string
     */
    public void setFontStyle(String string) {
        fontStyle = string;
    }

    /**
     * @param string
     */
    public void setFontVariant(String string) {
        fontVariant = string;
    }

    /**
     * @param string
     */
    public void setFontWeight(String string) {
        fontWeight = string;
    }

    /**
     * @param string
     */
    public void setHeight(String string) {
        Height = string;
    }

    /**
     * @param string
     */
    public void setLeft(String string) {
        left = string;
    }

    /**
     * @param string
     */
    public void setLetterSpacing(String string) {
        letterSpacing = string;
    }

    /**
     * @param string
     */
    public void setLineHeight(String string) {
        lineHeight = string;
    }

    /**
     * @param string
     */
    public void setPosition(String string) {
        position = string;
    }

    /**
     * @param string
     */
    public void setTextAlign(String string) {
        textAlign = string;
    }

    /**
     * @param string
     */
    public void setTextDecoration(String string) {
        textDecoration = string;
    }

    /**
     * @param string
     */
    public void setTop(String string) {
        Top = string;
    }

    /**
     * @param string
     */
    public void setVisibility(String string) {
        visibility = string;
    }

    /**
     * @param string
     */
    public void setWidth(String string) {
        Width = string;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getTagName()
	 */
    public String getTagName() {
        return tagName;
    }

    /**
     * @param string
     */
    public void setTagName(String string) {
        tagName = string;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getOffsetHeight()
	 */
    public String getOffsetHeight() {
        return offsetHeight;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getOffsetLeft()
	 */
    public String getOffsetLeft() {
        return offsetLeft;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getOffsetTop()
	 */
    public String getOffsetTop() {
        return offsetTop;
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getOffsetWidth()
	 */
    public String getOffsetWidth() {
        return offsetWidth;
    }

    /**
     * @param string
     */
    public void setOffsetHeight(String string) {
        offsetHeight = string;
    }

    /**
     * @param string
     */
    public void setOffsetLeft(String string) {
        offsetLeft = string;
    }

    /**
     * @param string
     */
    public void setOffsetTop(String string) {
        offsetTop = string;
    }

    /**
     * @param string
     */
    public void setOffsetWidth(String string) {
        offsetWidth = string;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.actf.model.ui.editor.browser.ICurrentStyles#getXPath()
	 */
	public String getXPath() {
		return xpath;
	}

}
