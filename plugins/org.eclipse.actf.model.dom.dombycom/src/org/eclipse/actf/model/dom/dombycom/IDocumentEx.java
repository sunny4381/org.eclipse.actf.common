/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public interface IDocumentEx extends Document {
    String CSS1_COMPAT = "CSS1Compat";
    String BACK_COMPAT = "BackCompat";
    List<Node> getElementsByIdInAllFrames(String id);
    String getCompatMode();
    Element getTargetElement(String target);
}
