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

import org.eclipse.actf.util.vocab.IEvalTarget;
import org.eclipse.swt.graphics.Rectangle;
import org.w3c.dom.Node;


public interface INodeEx extends Node, IEvalTarget{
    String extractString();
    short getHeadingLevel();
    String getLinkURI();
    char getAccessKey();
    // maybe heavy task
    int getNth();
    
    boolean doClick();
    boolean highlight();
    boolean unhighlight();
    boolean setFocus();
    boolean setText(String text);
    String getText();

    //!FN!
    String[] getStillPictureData();

    AnalyzedResult analyze(AnalyzedResult ar);
    Rectangle getLocation();
}
