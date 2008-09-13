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




public interface BeforeNavigate2Parameters {

    public int getBrowserAddress();

    public String getUrl();

    public int getFlags();

    public String getTargetFrameName();

    public Object getPostData();

    public String getHeaders();

    public boolean getCancel();

    public void setCancel(boolean cancel);

}
