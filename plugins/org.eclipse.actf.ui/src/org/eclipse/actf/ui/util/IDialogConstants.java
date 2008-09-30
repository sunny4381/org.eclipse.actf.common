/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.ui.util;

/**
 * Interface to provide commonly used Dialog messages
 */
public interface IDialogConstants {
	/**
	 * OK
	 */
	public static final String OK = " " + Messages.getString("DialogConst.OK")
			+ " ";
	/**
	 * cancel
	 */
	public static final String CANCEL = Messages
			.getString("DialogConst.Cancel");
	/**
	 * none
	 */
	public static final String NONE = Messages.getString("DialogConst.None");
	/**
	 * Help
	 */
	public static final String HELP = " "
			+ Messages.getString("DialogConst.Help") + " ";
	/**
	 * Add
	 */
	public static final String ADD = Messages.getString("DialogConst.Add");
	/**
	 * Delete
	 */
	public static final String DELETE = Messages
			.getString("DialogConst.Delete");
	/**
	 * Close
	 */
	public static final String CLOSE = Messages.getString("DialogConst.Close");
	/**
	 * Browse
	 */
	public static final String BROWSE = Messages
			.getString("DialogConst.Browse");
	/**
	 * Type target URL, then press enter.
	 */
	public static final String OPENFILE_INFO = Messages
			.getString("DialogConst.OpenFile");
}
