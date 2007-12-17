/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.model;

import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.actf.model.event.IModelEventListener;
import org.eclipse.actf.model.event.ModelEventType;


/**
 * base implementation for <code>IGuiModel</code>. Clients should extend this class 
 * rather than attempting to implement <code>IGuiModel</code>
 * 
 * @author Mike Squillace
 *
 */
public abstract class AbstractGuiModel extends AbstractModel
	implements IGuiModel
{

	/** keyed by event ids with values that are instances of <code>ModelEventType</code> */
	protected Map eventIdMap = new HashMap();

	/**
	 * create a IGuiModel implementation
	 * 
	 * @param modelType --
	 *            name of model
	 */
	public AbstractGuiModel (String modelType) {
		super(modelType);
		initEventIdMap();
	}
	
	/**
	 * called by the constructor of this class to initialize the eventid-&gt;ModelEventType map
	 *
	 */
	//protected abstract void initEventIdMap ();
	
	/** {@inheritDoc} */
	public ModelEventType getModelEventType (Object eventId) {
		return (ModelEventType) eventIdMap.get(eventId);
	}
	
	/** {@inheritDoc} */
	public boolean isVisible (Object comp) {
		return false;
	}

	/** {@inheritDoc}
	 *  
	 *  This default implementation merely checks that the component is not <code>null</code>.
	 */
	public boolean isValid (Object comp) {
		return comp != null;
	}

	/** {@inheritDoc} */
	public boolean requestFocusFor (Object comp) {
		return false;
	}

	/** {@inheritDoc} */
	public void highlight (Object element) {
	}

	/** {@inheritDoc} */
	public Rectangle getBoundingRectangle (Object element) {
		return null;
	}

	protected void initEventIdMap () {
	}
	
	public ModelEventType[] getModelEventTypes (Class c) {
		return null;
	}
	
	// these next two are stubs
	public void registerModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes)  {
	}
	
	public void unregisterModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes)  {
	}
	
} // AbstractGuiModel
