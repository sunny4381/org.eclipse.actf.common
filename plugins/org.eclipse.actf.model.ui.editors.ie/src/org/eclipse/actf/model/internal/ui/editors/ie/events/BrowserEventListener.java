/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.ui.editors.ie.events;



public interface BrowserEventListener {

    public void beforeNavigate2(BeforeNavigate2Parameters param);
    
    public void navigateComplete2(NavigateComplete2Parameters param);

    public void navigateError(NavigateErrorParameters param);

    public void documentComplete(DocumentCompleteParameters param);

    public void newWindow2(NewWindow2Parameters param);

    public void windowClosing(WindowClosingParameters param);

    public void statusTextChange(StatusTextChangeParameters param);

    public void titleChange(TitleChangeParameters param);

    public void progressChange(ProgressChangeParameters param);
    
    public void windowClosed();

}
