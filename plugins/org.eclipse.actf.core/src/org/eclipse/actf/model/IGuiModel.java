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

import org.eclipse.actf.model.event.IModelEventListener;
import org.eclipse.actf.model.event.ModelEventType;


/**
 * The IGuiModel interface is implemented in order to allow the IBM Reflexive GUI Builder
 * to support a variety of GUI models.  The application is packaged with implementations
 * of the IGuiModel interface in order to accommodate GUIs designed according to the Swing, AWT,
 * and Eclipse SWT frameworks (although the AWT models is discouraged since it does not fully
 * support accessibility features provided in other models such as Swing).
 * 
 * <p>A better name for this interface is actually <code>GUIHierarchicalStructure</code> since an model is
 * a hierarchical structure that embodies the hierarchical nature of Java GUI components. Although ACTF can be used to
 * construct and render implementations of this interface, not all hierarchical structures can be built using ACTF. However,
 * the validation mechanism does support the validation of any hierarchical structure once the appropriate
 * validator implementation and validation document(s) are written.
 *
 * @author Mike Squillace
 */
public interface IGuiModel extends IModel
{

	/**
	 * get the model event types for the given source type. A <code>ModelEventType</code> object 
	 * describes the types of events that can be fired within the context of this runtime model. If the sourceType is <code>null</code>, 
	 * then all of the event types of which this model is aware will be returned else only events that can be fired by the given sourceType 
	 * will be returned.
	 *   
	 * @param sourceType type to be queried for events
	 * @return all events that can be fired by instances of the given type or 
	 * all events of which this model is aware
	 * @see org.eclipse.actf.core.model.event.ModelEventType
	 */
	public ModelEventType[] getModelEventTypes (Class sourceType);
	
	/**
	 * get the <code>ModelEventType</code> instances associated with the given event id. The event id is model-specific 
	 * and will usually be presented in the model's API. What constitutes an event id and how they are determined should be 
	 * specified in clients that implement this model.
	 * 
	 * @param eventId model-specific event id
	 * @return <code>ModelEventType</code> instances associated with this event id or <code>null</code>
	 * if no event types are associated with this id
	 */
	public ModelEventType getModelEventType (Object eventId);
	
	/**
	 * register a listener to receive notification when events with the given eventTypes are fired within this 
	 * model. The eventTypes should be obtained from one of the <code>getModelEventTypes</code> methods. 
	 * 
	 * @param listener listener to be notified when specified events occur
	 * @param eventTypes event types for which listener is being registered
	 * @see #getModelEventType(Object)
	 * @see #getModelEventTypes(Class)
	 */
	public void registerModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes);
	
	/**
	 * unregister a previously registered listener.  The eventTypes should be obtained from either 
	 * of the <code>getModelEventTypes</code> methods.
	 * 
	 * @param listener listener to be unregistered
	 * @param eventTypes event types for which listener is to be unregistered
	 * @see #registerModelEventListener(IModelEventListener, ModelEventType[])
	 * @see org.eclipse.actf.core.model.event.ModelEventType#getId()
	 */
	public void unregisterModelEventListener (IModelEventListener listener, ModelEventType[] eventTypes);
	
	/**
	 * returns whether or not the currently executing thread is the UI thread.
	 * The UI thread is the thread upon which actions effecting the state or drawing of a component
	 * or the event handlers associated with a component are to be executed. If this method returns
	 * <code>false</code>, the client will typically need to invoke the <code>invokeOnUIThread</code> method in order to effect the GUI.
	 *
	 * @return <code>true</code> if the current thread is the UI thread, <code>false</code> otherwise
	 * @see #invokeOnUIThread(Runnable) 
	 */
	public boolean isUIThread ();

	/**
	 * executes the given Runnable from within the UI thread. This method will typically be called if
	 * <code>isUIThread</code> returns <code>false</code>.
	 * 
	 * @param runnable - Runnable to be invoked in UI thread
	 * @see #isUIThread()
	 */
	public void invokeOnUIThread (Runnable runnable);

	/**
	 * asyncronously executes the given Runnable from within the UI thread. That is, the <code>Runnable</code>
	 * is placed in a queue and control is returned immediately to the calling thread.
	 * 
	 * @param runnable - Runnable to be invoked in UI thread
	 * @see #invokeOnUIThread(Runnable)
	 */
	public void asyncInvokeOnUIThread (Runnable runnable);

	/**
	 * returns whether or not the specified component is currently visible
	 * 
	 * @param component - component to be tested
	 * @return whether or not the component is currently visible or showing on the screen
	 */
	public boolean isVisible (Object component);

	/**
	 * returns whether or not the specified component is valid for access. Components may be 
	 * invalid because their underlying resources have been disposed, because the device that 
	 * displays or renders them is destroyed, or for many other reasons.
	 * 
	 * @param component - component to be tested
	 * @return <code>true</code> if this component is valid, <code>false</code> otherwise
	 * @see org.eclipse.actf.core.model.InvalidComponentException
	 */
	public boolean isValid (Object component);

	/**
	 * request the focus for the specified component. This method
	 * should be invoked, for example, just prior to validation for a report
	 * that reflects the state of the component while visible.
	 * 
	 * @param comp - component for which focus is desired
	 * @return <code>true</code> if focus is successful, <code>false</code> otherwise
	 */
	public boolean requestFocusFor (Object comp);

	/**
	 * gets the rectangle bounding the given element
	 *
	 * @param element - element for which bounds are desired
	 * @return bounding rectangle of component
	 */
	public Rectangle getBoundingRectangle (Object element);

	/**
	 * highlight or visually indicate the element that is being examined. Highlighting may take place by 
	 * placing a border around the element, flashing the element, or changing its background color.
	 * 
	 * @param element - the element to be highlited
	 */
	public void highlight (Object element);
	
} // IGuiModel
