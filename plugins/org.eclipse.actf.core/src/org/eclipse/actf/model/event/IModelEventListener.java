/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  <a href="mailto:masquill@us.ibm.com>Mike Squillace</a> - initial API and implementation
*******************************************************************************/ 


package org.eclipse.actf.model.event;

import java.util.EventObject;

/**
 * a listener to be implemented by clients wishing to monitor events within a model
 * 
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public interface IModelEventListener
{

	/**
	 * handle the dispatched event
	 * 
	 * @param event
	 */
	public void handleEvent (EventObject event);
	
}
