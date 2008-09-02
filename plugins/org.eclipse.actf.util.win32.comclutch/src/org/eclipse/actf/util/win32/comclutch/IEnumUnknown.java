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

package org.eclipse.actf.util.win32.comclutch;


public interface IEnumUnknown extends IUnknown {
    IUnknown[] next(int num);
    boolean skip(int num);
    boolean reset();
    IEnumUnknown _clone();
}
