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

package org.eclipse.actf.model.dom.html.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

import org.eclipse.actf.model.dom.html.HTMLParser;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLBRElement;
import org.w3c.dom.html.HTMLDListElement;
import org.w3c.dom.html.HTMLDirectoryElement;
import org.w3c.dom.html.HTMLDivElement;
import org.w3c.dom.html.HTMLDocument;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.html.HTMLFieldSetElement;
import org.w3c.dom.html.HTMLFormElement;
import org.w3c.dom.html.HTMLHRElement;
import org.w3c.dom.html.HTMLHeadingElement;
import org.w3c.dom.html.HTMLImageElement;
import org.w3c.dom.html.HTMLIsIndexElement;
import org.w3c.dom.html.HTMLLIElement;
import org.w3c.dom.html.HTMLMenuElement;
import org.w3c.dom.html.HTMLOListElement;
import org.w3c.dom.html.HTMLParagraphElement;
import org.w3c.dom.html.HTMLPreElement;
import org.w3c.dom.html.HTMLTableElement;
import org.w3c.dom.html.HTMLTableRowElement;
import org.w3c.dom.html.HTMLUListElement;


public class HTML2PlainText {
	private static final String lineSeparator = "\n";

	private HTMLDocument htdoc;

	private boolean image = false;

	public void includeImages(boolean b) {
		this.image = b;
	}

	public HTML2PlainText(HTMLDocument htdoc) {
		this.htdoc = htdoc;
	}

	private int stack[] = new int[256];

	private int sp = -1;

	private void pre(HTMLElement element, StringWriter writer) {
		if (element instanceof HTMLOListElement) {
			stack[++sp] = 1;
		} else if (element instanceof HTMLUListElement) {
			stack[++sp] = 0;
		} else if (element instanceof HTMLLIElement) {
			StringBuffer pre = new StringBuffer(lineSeparator);
			for (int i = sp; i > 0; i--)
				pre.append(' ');
			writer.write(pre.toString());
			if (element.getParentNode() instanceof HTMLOListElement) {
				writer.write(stack[sp] + ". ");
				stack[sp]++;
			} else {
				writer.write("- ");
			}
		} else if (element instanceof HTMLHRElement) {
			writer.write(lineSeparator
					+ "----------------------------------------"
					+ lineSeparator);
		} else if (element instanceof HTMLBRElement || isBlockElement(element)) {
			writer.write(lineSeparator);
		} else if (element instanceof HTMLImageElement) {
			if (image) {
				writer.write(element.toString());
			} else {
				String alt = ((HTMLImageElement) element).getAlt();
				if (alt != null && alt.length() > 0) {
					writer.write(alt);
				}
			}
		}
	}

	public static boolean isBlockElement(HTMLElement element) {
		return element instanceof HTMLParagraphElement
				|| element instanceof HTMLHeadingElement
				|| element instanceof HTMLUListElement
				|| element instanceof HTMLOListElement
				|| element instanceof HTMLDirectoryElement
				|| element instanceof HTMLMenuElement
				|| element instanceof HTMLPreElement
				|| element instanceof HTMLDListElement
				|| element instanceof HTMLDivElement
				|| element.getTagName().equalsIgnoreCase("CENTER")
				|| element.getTagName().equalsIgnoreCase("NOSCRIPT")
				|| element.getTagName().equalsIgnoreCase("NOFRAMES")
				|| element.getTagName().equalsIgnoreCase("BLOCKQUOTE")
				|| element instanceof HTMLFormElement
				|| element instanceof HTMLIsIndexElement
				|| element instanceof HTMLHRElement
				|| element instanceof HTMLTableElement
				|| element instanceof HTMLFieldSetElement
				|| element.getTagName().equalsIgnoreCase("ADDRESS");
	}

	private void post(HTMLElement element, StringWriter writer) {
		if (element instanceof HTMLUListElement
				|| element instanceof HTMLOListElement) {
			sp--;
		} else if (element instanceof HTMLTableRowElement) {
			writer.write(lineSeparator);
		}
	}

	public Reader getPlainTextReader() {
		Node tmp1, tmp2;
		StringWriter writer = new StringWriter();
		tmp1 = htdoc.getDocumentElement();
		outer: while (tmp1 != null && tmp1 != htdoc) {
			if (tmp1 instanceof HTMLElement) {
				pre((HTMLElement) tmp1, writer);
			}
			if ((tmp2 = tmp1.getFirstChild()) == null) {
				if (tmp1.getNodeType() == Node.TEXT_NODE) {
					// writer.write(tmp1.getNodeValue());
					writer.write(tmp1.toString());
				}
				if (tmp1 != htdoc) {
					tmp2 = tmp1.getNextSibling();
				} else {
					break outer;
				}
				// post tmp1
				if (tmp2 != null && tmp1 instanceof HTMLElement) {
					post((HTMLElement) tmp1, writer);
				}
			}
			while (tmp2 == null && tmp1 != null) {
				tmp2 = tmp1.getParentNode();
				// post tmp1
				if (tmp2 != null && tmp1 instanceof HTMLElement) {
					post((HTMLElement) tmp1, writer);
				}
				tmp1 = tmp2;
				if (tmp1 != htdoc) {
					tmp2 = tmp1.getNextSibling();
					// post tmp1
					if (tmp2 != null && tmp1 instanceof HTMLElement) {
						post((HTMLElement) tmp1, writer);
					}
				} else {
					break outer;
				}
			}
			tmp1 = tmp2;
		}
		return new StringReader(writer.toString());
	}

	public static void main(String args[]) throws Exception {
		HTMLParser parser;
		for (int i = 0; i < args.length; i++) {
			parser = new HTMLParser();
			try {
				parser.parseSwitchEnc(new FileInputStream(args[i]));
			} catch (org.eclipse.actf.model.dom.sgml.ParseException e) {
				e.printStackTrace();
				continue;
			}
			HTML2PlainText h2t = new HTML2PlainText((HTMLDocument) parser
					.getDocument());
			Reader reader = h2t.getPlainTextReader();
			if (reader == null) {
				System.err.println("Null Text");
				System.exit(1);
			}
			BufferedReader br = new BufferedReader(reader);
			for (String str = br.readLine(); str != null; str = br.readLine()) {
				System.out.println(str);
			}
		}
	}
}
