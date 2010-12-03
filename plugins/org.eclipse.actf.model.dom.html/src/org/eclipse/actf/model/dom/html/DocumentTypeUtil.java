/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.html;

import org.eclipse.actf.model.internal.dom.sgml.impl.SGMLDocType;
import org.w3c.dom.DocumentType;

public class DocumentTypeUtil {
	
	/**
	 * @param docType
	 * @return original ID if HTML Parser overrides it. If not, it returns usual public ID from {@link DocumentType}.
	 */
	public static String getOriginalID(DocumentType docType){
		if(docType==null){
			return "";
		}
		String id = docType.getPublicId();
		if(docType instanceof SGMLDocType){
			String tmpS = ((SGMLDocType)docType).getOrgId();
			if(tmpS != null){
				return tmpS;
			}
		}
		return id;
	}

}
