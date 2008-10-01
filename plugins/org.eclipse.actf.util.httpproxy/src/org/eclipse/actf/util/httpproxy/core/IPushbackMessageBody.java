/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.core;

import java.io.PushbackInputStream;

/**
 * Body of a HTTP request/response message which can provide {@link PushbackInputStream}.
 */
public interface IPushbackMessageBody extends IMessageBody {

	/**
	 * Returns a {@link PushbackInputStream} for this message body.
	 * @return a {@link PushbackInputStream} for this message body.
	 */
	public abstract PushbackInputStream getMessageBodyPushBackInputStream();
	// Caution!!!! Without pushback option, body will be invalidated.

}