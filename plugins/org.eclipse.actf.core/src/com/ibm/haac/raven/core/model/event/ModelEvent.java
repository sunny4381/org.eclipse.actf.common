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


package com.ibm.haac.raven.core.model.event;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * an event fired within a model
 * 
 * @author <a href="mailto:masquill@us.ibm.com>Mike Squillace</a>
 *
 */
public abstract class ModelEvent extends EventObject
{

	public static final String TIMESTAMP_PROPERTY = "timestamp";
	public static final String EVENT_TYPE_PROPERTY = "eventType";
	
	protected Map properties = new HashMap();
	
	/**
	 * @param source
	 */
	public ModelEvent(Object source) {
		super(source);
	}
	
	public Object getProperty (String name) {
		return properties.get(name);
	}
	
	public void setProperty (String name, Object value) {
		if (name != null && name.trim().length() > 0 && value != null) {
			properties.put(name, value);
		}
	}
		
	/**
	 * convenience method for getting the timestamp of the event
	 * 
	 * @return timestamp of event
	 */
		public long getTimeMillis () {
			Long time = (Long) getProperty(TIMESTAMP_PROPERTY);
			long res = 0;
			if (time != null) {
				res = time.longValue();
			}
			return res;
		}
		
		/**
		 * convenience method for retreaving the timestamp of the event
		 * 
		 * @param time timestamp of event
		 */
		public void setTimeMillis (long time) {
			setProperty(TIMESTAMP_PROPERTY, new Long(time));
		}
		
		/**
		 * convenience method for getting the symbolic name or type of the event. This should be a more informative token than the 
		 * event id used by the underlying model.
		 * 
		 * @return symbolic name or type of event
		 */
		public String getEventType () {
			return (String) getProperty(EVENT_TYPE_PROPERTY);
		}
		
		/**
		 * convenience method for setting the symbolic name or type of the event. This should be a more informative token than the 
		 * event id used by the underlying model. Typically, this will be obtained from the corresponding <code>ModelEventType</code> object.
		 * 
		 * @param eventType symbolic name or type of event
		 * @see ModelEventType
		 */
		public void setEventType (String eventType) {
			if (eventType != null && eventType.trim().length() > 0) {
				setProperty(EVENT_TYPE_PROPERTY, eventType);
			}
		}
		
}
