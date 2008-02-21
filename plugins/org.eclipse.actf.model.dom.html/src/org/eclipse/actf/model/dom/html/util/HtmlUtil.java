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

import org.eclipse.actf.model.dom.sgml.util.SgmlUtil;
import org.w3c.dom.Node;
import org.w3c.dom.html.HTMLCollection;


public class HtmlUtil {
	public static Node commonAncester(HTMLCollection ancesters) {
		switch (ancesters.getLength()) {
		case 0:
			return null;
		case 1:
			return ancesters.item(0).getParentNode();
		default:
			Node ret = SgmlUtil
					.commonAncester(ancesters.item(0), ancesters.item(1));
			for (int i = 2; i < ancesters.getLength(); i++) {
				Node tmp = SgmlUtil.commonAncester(ancesters.item(i - 1), ancesters
						.item(i));
				if (ret != tmp) {
					ret = SgmlUtil.commonAncester(ret, tmp);
				}
			}
			return ret;
		}
	}
}
