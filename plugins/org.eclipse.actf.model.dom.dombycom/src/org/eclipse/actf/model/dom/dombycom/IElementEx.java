/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.dom.dombycom;

import org.w3c.dom.Element;




public interface IElementEx {
    public class Position {
        public int index;
        public int total;
    }
    public IStyle getStyle();
    
    public Position getRadioPosition();
    public Position getListPosition();
    public Element getFormLabel();
}
