/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package com.ibm.haac.raven.core.arch.format;

import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.runtime.IRuntimeContext;
import com.ibm.haac.raven.core.runtime.RuntimeContextFactory;

/**
 * @author Mike Squillace
 */
public abstract class AbstractElementFormatter implements ElementFormatter
{

	protected IConfiguration configuration;
	protected int maxStringSize = 30;
	protected String indentation = "    ";
	protected String[] displayedProperties = new String[0];

	public AbstractElementFormatter () {
		try {
			IRuntimeContext context = RuntimeContextFactory.getInstance().getRuntimeContext();
			configuration = context.getConfiguration();
		}catch (ConfigurationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/** {@inheritDoc} */
	public String[] getDisplayedProperties () {
		return displayedProperties;
	}
	
	/**
	 * print indentation and nesting level followed by a colon (:)
	 * 
	 * @param out buffer into which to print
	 * @param nest nesting level
	 */
	protected void printIndentation (StringBuffer out, int nest) {
		for (int i = 0; i < nest; i++) {
			out.append(indentation);
		}
		out.append(nest);
		out.append(": ");
	}

	/**
	 * output the class name and the toString of the element into the buffer. If the length of
	 * the <code>element.toString()</code> is greater than <code>maxStringSize</code>,
	 * the resulting string will be truncated. New lines ('\n') and returns ('\r') are removed
	 * by default.
	 * 
	 * <p>The formatted element will be enclosed by calls to <code>startElement</code> and
	 * <code>endElement</code>.
	 *
	 * @param element - element being formatted
	 * @param nest - nesting level 
	 * @param out - buffer into which to write
	 * @see #startElement(Object, int, StringBuffer)
	 * @see #endElement(Object, int, StringBuffer)
	 */
	protected void formatString (Object element, int nest, StringBuffer out) {
		String str = element.toString();
		if (str.length() > maxStringSize) {
			str = str.substring(0, maxStringSize) + "...";
		}
		startElement(element, nest, out);
		out.append(element.getClass().getName());
		out.append(", ");
		out.append('\'');
		out.append(removeNewLines(str));
		out.append('\'');
		endElement(element, nest, out);
	}

	protected String removeNewLines (String text) {
		int posnn = text.indexOf('\r');
		int posnr = text.indexOf('\n');
		int posn = posnn > 0 && posnr > 0 ? Math.min(posnn, posnr) : Math.max(
			posnn, posnr);
		if (posn > 0) {
			text = text.substring(0, posn) + "...";
		}
		return text;
	}

	/**
	 * start an element. This default implementation prints the indentation,
	 * the nesting level, and an opening square bracket.
	 * 
	 * @param o - object being formatted
	 * @param nest - nesting level
	 * @param out - buffer into which we are writing
	 */
	protected void startElement (Object o, int nest, StringBuffer out) {
		printIndentation(out, nest);
	}

	/**
	 * end an element. This default implementation prints a closing square bracket and a new-line.
	 * 
	 * @param o - object being formatted
	 * @param nest - nesting level
	 * @param out - buffer into which we are writing
	 */
	protected void endElement (Object o, int nest, StringBuffer out) {
		out.append('\n');
	}
} // AbstractElementFormatter
