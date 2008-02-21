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

package org.eclipse.actf.model.dom.sgml;

public interface ISGMLConstants {
    int EOF = -1;
    int MDO = 1;         // <!
    int CDATA = 2; 
    int INCO = 3;      // +(
    int EXCO = 4;      // -(
    int STRING = 5;
    int DQSTR = 6;       // "....."
    int QSTR = 7;        // '.....'
    int ETAGO = 8;       // </
    int LETTER = 9;
    int DIGIT = 10;
    int NAME_CHAR = 11;
    int NUM = 12;
    int COMMENT = 13;   // <!-- .... -->
    int PCDATA = 14;
    int WHITESPACE = 15;
    int PI = 17;
    int MISC = 1000;
    
    int EQ = '=';        // =
    int TAGC = '>';      // >
    int STAGO = '<';     // <
    int DSO = '[';       // [
    int DSC = ']';       // ]
    int PERCENT = '%';   // %
    int LEFTPAR = '(';   // (
    int RIGHTPAR = ')';  // )
    int MINUS = '-';     // -
    int OMITTABLE = 'O'; // O
    int COMMA = ',';     // ,
    int BAR = '|';       // |
    int AMPA = '&';      // &
    int QUESTION = '?';  // ?
    int PLUS = '+';      // +
    int MULTI = '*';     // *
    
    int DEFAULT = 0;    
    int TAG = 1;
    int ETAG = 2;

    /**
     *  Represents org.eclipse.actf.model.dom.sgml.EndTag
     */
    public static final int ENDTAG = -1;

    /**
     *  Error code for missing DOCTYPE declaration.  This kind of error is not
     * dealt with error handlers
     */
    public static final int DOCTYPE_MISSED = 1;
    /**
     *  Error code for syntax error of DOCTYPE declaration.  This kind of error
     * is not dealt with error handlers
     */
    public static final int ILLEGAL_DOCTYPE = 2;
    /**
     *  Error code for an illegal top element.  For HTML example,  if an HTML
     * document starts with &lt;LI&gt; as follows, this is an illegal top element.
     * Because of the doctype declaration the document must start with &lt;HTML&gt;
     * <pre>
     *   &lt;!DOCTYPE <em>HTML</em> PUBLIC "-//W3C//DTD HTML 4.0//EN"&gt;
     *   <em>&lt;LI&gt;</em>
     * </pre>
     *   This kind of error is not dealt with error handlers
     */
    public static final int ILLEGAL_TOP_ELEMENT = 3;
    /**
     *  Error code for an illegal attribute.  If an element has an unknown
     * attribute,  that is an illegal attribute.
     */
    public static final int ILLEGAL_ATTRIBUTE = 4;
    /**
     *  Error code for a floating endtag.  Endtags whose corresponding start tag
     * is missing are defined as <em>floating</em>.  For HTML example, following
     * &lt;/P&gt; is floating because the P element is closed before HR.
     * <pre>
     *   &lt;P&gt;
     *     ...
     *   &lt;!-- Here is an omitted end tag of P becase following HR is now allowed
     *           as a child of P --&gt;
     *   &lt;HR&gt;
     *     ...
     *   &lt;/P&gt;   
     */
    public static final int FLOATING_ENDTAG = 5;
    /**
     *  Error code for sudden endtag.  A strange endtag that appears in some
     * context is defined as <em>sudden</em>.  For HTML example, following <em>
     * &lt;/I&gt;</em> is sudden.
     * <pre>
     *    &lt;I&gt; ... &lt;B&gt; ... <em>&lt;/I&gt;</em> ... &lt;/B&gt;
     * </pre>
     */
    public static final int SUDDEN_ENDTAG = 6;
    /**
     *  Error code for illegal child.  A node that is not allowed as a child of
     * context's element is defined as an illegal child.  For HTML example,
     * following <em>P</em> element is an illegal child of HEAD
     * <pre>
     *  &lt;HTML&gt;
     *   &lt;HEAD&gt;
     *    <em>&lt;P&gt; Illegal &lt;/P&gt; </em>
     *    &lt;TITLE&gt;
     *  ...
     * </pre>
     */
    public static final int ILLEGAL_CHILD = 7;
    /**
     *  Error code for unknown elements (not defined in pre-read DTD)
     */
    public static final int UNKNOWN_ELEMENT = 8;
    /**
     *  Error code for syntax error of start tag text.
     */
    public static final int STARTTAG_SYNTAX_ERR = 9;
    /**
     *  Error code for miscellenious
     */
    public static final int MISC_ERR = 10;

    /**
     *  Error code for token-level error of attribute value.
     */
    public static final int ATTR_VALUE = 11;
    /**
     *  Error code for token-level error before attribute's name.
     */
    public static final int BEFORE_ATTRNAME = 12;
    /**
     *  Error code for token-level error of tag name.
     */
    public static final int TAG_NAME = 13;
}
