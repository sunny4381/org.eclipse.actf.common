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

package com.ibm.haac.raven.core.model;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.ibm.haac.raven.core.arch.format.DefaultElementFormatter;
import com.ibm.haac.raven.core.arch.format.ElementFormatter;
import com.ibm.haac.raven.core.config.ConfigurationException;
import com.ibm.haac.raven.core.config.IConfiguration;
import com.ibm.haac.raven.core.logging.IReporter;
import com.ibm.haac.raven.core.model.event.IModelChangeListener;
import com.ibm.haac.raven.core.model.event.ModelChangeEvent;
import com.ibm.haac.raven.core.model.locate.INodeLocator;
import com.ibm.haac.raven.core.runtime.IRuntimeContext;
import com.ibm.haac.raven.core.runtime.RuntimeContextFactory;
import com.ibm.haac.raven.core.traverse.ITreeNodeWalker;
import com.ibm.haac.raven.core.traverse.filters.NodeIdFilter;
import com.ibm.haac.raven.core.traverse.filters.NodeNameFilter;
import com.ibm.haac.util.Utils;

/**
 * provides basic services for any model instance. Note that the IGuiModel
 * interface must be implemented as well by clients that wish to validate
 * structures via validation documents using the ACTF Engine.
 * 
 * @see com.ibm.haac.raven.core.model.IGuiModel
 * @author Mike Squillace
 */
public abstract class AbstractModel implements IModel
{

	protected IConfiguration configuration;

	protected IRuntimeContext runtimeContext = RuntimeContextFactory.getInstance().getRuntimeContext();

	protected INodeLocator locator;

	protected ITreeNodeWalker treeNodeWalker;

	private List changeListenerList = new LinkedList();

	private String _type;

	/**
	 * create a new model
	 * 
	 * @param type -
	 *            type of model
	 */
	public AbstractModel (String type) {
		_type = type;
		try {
			configuration = runtimeContext.getConfiguration();
		}catch (ConfigurationException e) {
			Utils.println(
				IReporter.SYSTEM_FATAL, "Trouble instantiating model of type "
						+ type + " - no configuration object", e);
		}
	}

	/** {@inheritDoc} */
	public String getName () {
		return _type;
	}

	protected void setModelType (String type) {
		_type = type;
	}

	/**
	 * returns empty array
	 * 
	 * @return init packages
	 */
	public String[] getPackageNames () {
		return new String[0];
	}

	/** {@inheritDoc} */
	public void printModel (Object component, Writer writer) {
		try {
			StringBuffer sb = new StringBuffer();
			printTree(component, sb, 0);
			if (writer == null) {
				writer = new PrintWriter(System.out, true);
			}
			writer.write(sb.toString());
			writer.flush();
		}catch (Exception e) {
			Utils.println(IReporter.SYSTEM_NONFATAL, "Problem printing model", e);
		}
	}

	protected void printTree (Object component, StringBuffer sb, int nest)
		throws InvalidComponentException {
		ElementFormatter formatter = getElementFormatterFor(component);
		ITreeNodeWalker tw = getTreeWalker();
		Object[] ca = new Object[0];
		if (tw != null) {
			ca = tw.getFilteredChildren(component);
		}
		if (formatter != null) {
			formatter.formatElement(component, nest, sb);
		}
		if (ca != null) {
			for (int i = 0; i < ca.length; i++) {
				printTree(ca[i], sb, nest + 1);
			}
		}
	} // printTree

	protected ElementFormatter getElementFormatterFor (Object obj) {
		Map formatterMap = configuration.getModelSymbolPoolContents(
			getName(), IConfiguration.FORMATTER_ID);
		String cName = obj.getClass().getName();
		String prefix = cName.substring(0, cName.lastIndexOf('.'));
		int last = -1;
		ElementFormatter formatter = null;
		while (formatterMap != null && formatter == null && prefix.length() > 0) {
			String name = (String) formatterMap.get(prefix);
			if (name != null) {
				try {
					formatter = (ElementFormatter) Class.forName(name).newInstance();
				}catch (Exception e) {
					Utils.println(
						IReporter.SYSTEM_NONFATAL,
						"Cannot form formatter class from name " + name, e);
				}
			}
			last = prefix.lastIndexOf('.');
			prefix = last > 0 ? prefix.substring(0, last) : "";
		}
		return formatter == null ? new DefaultElementFormatter() : formatter;
	}

	/** {@inheritDoc} */
	public void setNodeID (Object comp, String id) {
	}

	/**
	 * default implementation returns the hexadecimal representation of <code>System.identityHashCode</code> of 
	 * the given element.
	 * 
	 * @see java.lang.System#identityHashCode(Object)
	 * 
	 */
	public String getNodeId (Object element) {
		int id = System.identityHashCode(element);
		String xid = Integer.toHexString(id).toUpperCase();
		return "00000000".substring(0, 8 - xid.length()) + xid;
	}

	/**
	 * {@inheritDoc} returns the qualified class name (i.e. without the package
	 * name) of the given element
	 */
	public String getNodeName (Object element) {
		String name = element.getClass().getName();
		return name.substring(name.lastIndexOf('.') + 1);
	}

	/** {@inheritDoc} */
	public String getDefaultAliasPrefix () {
		return "";
	}

	/** {@inheritDoc} */
	public ITreeNodeWalker getTreeWalker () {
		return treeNodeWalker;
	}

	/** {@inheritDoc} */
	public INodeLocator getNodeLocator () {
		configuration.setModelSymbolPool(getName(), IConfiguration.MODEL_ID);
		Class locatorCls = configuration.getClassParameter(IConfiguration.MODEL_LOCATOR);
		if (locator == null && locatorCls != null) {
			try {
				locator = (INodeLocator) locatorCls.newInstance();
			}catch (Exception e) {
				Utils.println(IReporter.SYSTEM_NONFATAL,
					"Could not find locator for model of type: " + _type, e);
			}
		}
		return locator;
	}

	/**
	 * retrieve the desired model property. The property is always found in the
	 * appropriate model configuration file. The name of this file is of the
	 * form &lt;type&gt;.xml.
	 * 
	 * @param name
	 *            name of desired property
	 * @return desired property or <code>null</code> if not found
	 */
	protected String getModelProperty (String name) {
		configuration.setModelSymbolPool(getName(), IConfiguration.MODEL_ID);
		return configuration.getStringParameter(name);
	}

	/** {@inheritDoc} */
	public void addModelChangeListener (IModelChangeListener listener) {
		if (listener != null) {
			changeListenerList.add(listener);
		}
	}

	/** {@inheritDoc} */
	public void removeModelChangeListener (IModelChangeListener listener) {
		if (listener != null) {
			changeListenerList.remove(listener);
		}
	}

	/**
	 * fire a ModelChangeEvent notification for this model
	 * 
	 * @param mce
	 *            the {@link ModelChangeEvent} The method triggered will vary
	 *            depending on the event type: NODE_INSERTED, NODE_REMOVED,
	 *            NODE_MODIFIED
	 */
	protected void fireModelChangeEvent (ModelChangeEvent mce) {
		int eventType = mce.getEventType();
		switch (eventType) {
		case ModelChangeEvent.NODE_INSERTED: {
			for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
				((IModelChangeListener) iter.next()).nodeInserted(mce);
			}
			break;
		}
		case ModelChangeEvent.NODE_REMOVED: {
			for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
				((IModelChangeListener) iter.next()).nodeRemoved(mce);
			}
			break;
		}
		case ModelChangeEvent.NODE_MODIFIED: {
			for (Iterator iter = changeListenerList.iterator(); iter.hasNext();) {
				((IModelChangeListener) iter.next()).nodeModified(mce);
			}
			break;
		}
		}
	}

	protected void setFilters () {
		String names = getModelProperty(IConfiguration.MODEL_IGNORENODENAMES);
		if (names != null && names.length() > 0) {
			NodeNameFilter nameFilter = new NodeNameFilter(this, names);
			treeNodeWalker.addNodeFilter(nameFilter);
		}
		String ids = getModelProperty(IConfiguration.MODEL_IGNOREIDS);
		if (ids != null && ids.length() > 0) {
			NodeIdFilter idFilter = new NodeIdFilter(this, ids);
			treeNodeWalker.addNodeFilter(idFilter);
		}
	}

	// These next methods are stubs.
	public int getOrder (Object head) {
		return 1;
	}
	
} // AbstractModel
