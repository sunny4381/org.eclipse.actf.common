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

package org.eclipse.actf.util.command;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The CLArgumentProcessor can be used to process arguments on the command line for
 * any application. A set of CLSwitches are associated with the processor upon its
 * instantiation and the resolved values of these switches can be obtained via the set of
 * <code>getXXXArgument</code> methods of this class. The type of the value to which the
 * argument for a given switch is resolved is determined by the <code>IArgumentResolver</code> for that switch.
 * 
 *@see org.eclipse.actf.util.command.CLSwitch
 * @author Mike Squillace
 */
public class CLArgumentProcessor
{

	private Map _switchMap;

	private String _preUsage, _postUsage;

	private boolean _preserveCase = false; // true - preserves case of arguments, false - converts arguments to lower case

	/**
	 * construct a new command line argument processor with no switches associated with it
	 */
	public CLArgumentProcessor () {
		this(new CLSwitch[0]);
	}

	/**
	 * construct a new command line argument processor with the given switches.
	 * 
	 * @param switches - valid switches for this processor
	 */
	public CLArgumentProcessor (CLSwitch[] switches) {
		setSwitches(switches);
	}

	/**
	 * construct a new command line argument processor with no switches associated with it
	 * @param preserveCase
	 */
	public CLArgumentProcessor (boolean preserveCase) {
		this(new CLSwitch[0]);
		_preserveCase = preserveCase;
	}

	/**
	 * construct a new command line argument processor with the given switches.
	 * 
	 * @param switches - valid switches for this processor
	 * @param preserveCase - boolean to process arguments as case sensitive or not
	 */
	public CLArgumentProcessor (CLSwitch[] switches, boolean preserveCase) {
		_preserveCase = preserveCase;
		setSwitches(switches);
	}

	/**
	 * return the command line switch with the given name
	 * 
	 * @param name - name of desired switch (Without prefix)
	 * @return the desired switch
	 */
	public CLSwitch getSwitch (String name) {
		if (_preserveCase) {
			return (CLSwitch) _switchMap.get(name);
		}else {
			return (CLSwitch) _switchMap.get(name.toLowerCase());
		}
	}

	/**
	 * return the switches that are currently being handled by this processor
	 * 
	 * @return switches which are currently being processed
	 */
	public CLSwitch[] getSwitches () {
		return (CLSwitch[]) _switchMap.values().toArray(
			new CLSwitch[_switchMap.size()]);
	}

	/**
	 * set the command line switches to be associated with this command line processor. This
	 * method will automatically clear any previous switches associated with this processor.
	 * 
	 * @param switches - valid switches for this command line argument processor
	 */
	public void setSwitches (CLSwitch[] switches) {
		if (_switchMap == null) {
			_switchMap = new HashMap();
		}else {
			_switchMap.clear();
		}
		for (int s = 0; switches != null && s < switches.length; ++s) {
			if (_preserveCase) {
				_switchMap.put(switches[s].getName(), switches[s]);
			}else {
				_switchMap.put(switches[s].getName().toLowerCase(), switches[s]);
			}
		}
	}

	/**
	 * add a switch to the set of switches processed by this processor
	 * 
	 * @param s - switch to be added
	 */
	public void addSwitch (CLSwitch s) {
		if (_preserveCase) {
			_switchMap.put(s.getName(), s);
		}else {
			_switchMap.put(s.getName().toLowerCase(), s);
		}
	}

	/**
	 * remove the switch with the given name from this processor
	 * 
	 * @param name - name of switch to be removed
	 * @return command line switch that was removed
	 */
	public CLSwitch removeSwitch (String name) {
		if (_preserveCase) {
			return (CLSwitch) _switchMap.remove(name);
		}else {
			return (CLSwitch) _switchMap.remove(name.toLowerCase());
		}
	}

	/**
	 * process the given arguments from the command line. Resolved arguments
	 * can be retreaved with one of the <code>getXXXArgument</code> methods of this class.
	 * 
	 * <p>If the argument array is empty, the <code>printAUsage()</code> method
	 * is invoked. Usage for a switch consists of the name of
	 * that switch along with its description.
	 *
	 *@param args - arguments from command line
	 *@throws CLArgumentException
	 */
	public void processArguments (String[] args) throws CLArgumentException {
		if (args.length == 0) {
			printUsage();
			System.exit(0);
		}
		for (int arg = 0; arg < args.length; ++arg) {
			String name = nameIfValidSwitch(args[arg]);
			if (name == null) {
				System.err.println("Unknown switch: " + args[arg]);
			}else {
				CLSwitch clSwitch = getSwitch(name);
				if (clSwitch.getValueType().equals(Boolean.class)
						|| clSwitch.getValueType().equals(Boolean.TYPE)) {
					clSwitch.setValue("__true__");
					continue;
				}
				// concatenate all arguments for this switch
				StringBuffer argBuff = new StringBuffer();
				int start = arg;
				int numArgs = clSwitch.getExpectedNumberOfArgs();
				while (++arg < args.length && arg - start <= numArgs
						& nameIfValidSwitch(args[arg]) == null) {
					argBuff.append(args[arg]);
					argBuff.append(' ');
				}
				if (arg < args.length && arg - start > numArgs
						&& nameIfValidSwitch(args[arg]) == null) {
					do {
						System.err.println("Ignoring argument: "
								+ args[arg]
								+ " - beyond expected number of args for switch "
								+ clSwitch.getName());
						if (!Character.isLetter(args[arg].charAt(0))) {
							System.err.println("May be unknown switch: "
									+ args[arg]);
						}
						++arg;
					}while (arg < args.length
							&& nameIfValidSwitch(args[arg]) == null);
				}
				try {
					clSwitch.resolveValue(argBuff.toString().trim());
				}catch (Exception e) {
					System.err.println("Warning: could not resolve argument "
							+ argBuff.toString() + " for switch "
							+ clSwitch.getName() + " to type "
							+ clSwitch.getValueType().getName());
					--arg;
					continue;
				}
				--arg;
			}
		} // next argument
		Iterator iter = _switchMap.values().iterator();
		while (iter.hasNext()) {
			CLSwitch cls = (CLSwitch) iter.next();
			if (cls.isRequired() && cls.getResolvedValue() == null) { throw new CLArgumentException("Switch "
					+ cls.getName() + " is required and has not been set"); }
		}
	} // processArguments

	/**
	 * get the int argument value of the switch with the given name
	 * 
	 * @param name - name of switch for which value is desired
	 * @return value of desired switch or -1 if switch is not found
	 * @throws CLArgumentException - if switch value is not of appropriate type
	 */
	public int getIntArgument (String name) throws CLArgumentException {
		CLSwitch clSwitch = getSwitch(name);
		int val = -1;
		if (clSwitch != null) {
			if (clSwitch.getResolvedValue() != null
					&& (Integer.class.isAssignableFrom(clSwitch.getValueType()) || clSwitch.getValueType().equals(
						Integer.TYPE))) {
				val = ((Integer) clSwitch.getResolvedValue()).intValue();
			}else {
				throw new CLArgumentException("Switch " + clSwitch.getName()
						+ " does not have an associated value of type int");
			}
		}
		return val;
	} // getIntArgument

	/**
	 * get the long argument value of the switch with the given name
	 * 
	 * @param name - name of switch for which value is desired
	 * @return value of desired switch or -1 if switch is not found
	 * @throws CLArgumentException - if switch value is not of appropriate type
	 */
	public long getLongArgument (String name) throws CLArgumentException {
		CLSwitch clSwitch = getSwitch(name);
		long val = -1;
		if (clSwitch != null) {
			if (clSwitch.getResolvedValue() != null
					&& (Long.class.isAssignableFrom(clSwitch.getValueType()) || clSwitch.getValueType().equals(
						Long.TYPE))) {
				val = ((Long) clSwitch.getResolvedValue()).longValue();
			}else {
				throw new CLArgumentException("Switch " + clSwitch.getName()
						+ " does not have an associated value of type long");
			}
		}
		return val;
	} // getLongArgument

	/**
	 * get the float argument value of the switch with the given name
	 * 
	 * @param name - name of switch for which value is desired
	 * @return value of desired switch or -1 if switch is not found
	 * @throws CLArgumentException - if switch value is not of appropriate type
	 */
	public float getFloatArgument (String name) throws CLArgumentException {
		CLSwitch clSwitch = getSwitch(name);
		float val = -1;
		if (clSwitch != null) {
			if (clSwitch.getResolvedValue() != null
					&& (Float.class.isAssignableFrom(clSwitch.getValueType()) || clSwitch.getValueType().equals(
						Float.TYPE))) {
				val = ((Float) clSwitch.getResolvedValue()).floatValue();
			}else {
				throw new CLArgumentException("Switch " + clSwitch.getName()
						+ " does not have an associated value of type float");
			}
		}
		return val;
	} // getFloatArgument

	/**
	 * get the double argument value of the switch with the given name
	 * 
	 * @param name - name of switch for which value is desired
	 * @return value of desired switch or -1 if switch is not found
	 * @throws CLArgumentException - if switch value is not of appropriate type
	 */
	public double getDoubleArgument (String name) throws CLArgumentException {
		CLSwitch clSwitch = getSwitch(name);
		double val = -1;
		if (clSwitch != null) {
			if (clSwitch.getResolvedValue() != null
					&& (Double.class.isAssignableFrom(clSwitch.getValueType()) || clSwitch.getValueType().equals(
						Double.TYPE))) {
				val = ((Number) clSwitch.getResolvedValue()).doubleValue();
			}else {
				throw new CLArgumentException("Switch " + clSwitch.getName()
						+ " does not have an associated value of type double");
			}
		}
		return val;
	} // getDoubleArgument

	/**
	 * get the boolean argument value of the switch with the given name
	 * 
	 * @param name - name of switch for which value is desired
	 * @return value of desired switch or -1 if switch is not found
	 * @throws CLArgumentException - if switch value is not of appropriate type
	 */
	public boolean getBooleanArgument (String name) throws CLArgumentException {
		CLSwitch clSwitch = getSwitch(name);
		boolean val = false;
		if (clSwitch != null) {
			if (Boolean.class.isAssignableFrom(clSwitch.getValueType())
					|| clSwitch.getValueType().equals(Boolean.TYPE)) {
				val = clSwitch.getResolvedValue() != null
						&& ((Boolean) clSwitch.getResolvedValue()).booleanValue();
			}else {
				throw new CLArgumentException("Switch " + clSwitch.getName()
						+ " does not have an associated value of type bool");
			}
		}
		return val;
	} // getBooleanArgument

	/**
	 * get the resolved argument for the switch with the given name
	 * 
	 * @param name - name of switch for which argument is desired
	 * @return - the resolved value of that switch or <code>null</code>
	 * if switch with the given name is not found
	 */
	public Object getArgument (String name) {
		CLSwitch s = getSwitch(name);
		return s == null ? null : s.getResolvedValue();
	}

	/**
	 * determines whether or not the given string is a valid switch. The proposed
	 * switch will be valid if it contains a switch name recognized by this processor
	 * and is preceded by the prefix associated with that switch. If the
	 * proposed string is a valid switch, its name is returned.
	 * 
	 * @param sw - string to examine for validity
	 * @return name of the switch if valid or <code>null</code> if not
	 */
	public String nameIfValidSwitch (String sw) {
		int index = 0;
		String prefix, name;
		CLSwitch clSwitch;
		String res = null;
		while (index < sw.length() && !Character.isLetter(sw.charAt(index++))) {
			;
		}
		prefix = sw.substring(0, index - 1);
		name = sw.substring(index - 1);
		clSwitch = getSwitch(name);
		if (clSwitch != null) {
			if (_preserveCase
					&& clSwitch.getFullSwitchName().equals(prefix + name)) {
				res = name;
			}else if (!_preserveCase
					&& clSwitch.getFullSwitchName().equalsIgnoreCase(
						prefix + name)) {
				res = name;
			}
		}
		return res;
	} // isValidSwitch

	/**
	 * prepend the given string to the usage statement. This will be printed first
	 * when the <code>printUsage</code> method is called.
	 * 
	 * @param pre - first message to be printed for usage
	 */
	public void prependToUsage (String pre) {
		_preUsage = pre;
	}

	/**
	 * append the given string to the usage. After switch names and descriptions
	 * are printed, this message will be output.
	 * 
	 * @param post - to be printed after the usage
	 */
	public void appendToUsage (String post) {
		_postUsage = post;
	}

	protected void printUsage () {
		Iterator iter = _switchMap.values().iterator();
		System.out.println(_preUsage == null ? "" : _preUsage);
		System.out.println();
		System.out.println("Options:");
		System.out.println();
		while (iter.hasNext()) {
			CLSwitch clSwitch = (CLSwitch) iter.next();
			System.out.print(clSwitch.getFullSwitchName() + " "
					+ clSwitch.getDescription());
			System.out.print(clSwitch.isRequired() ? " [required] " : " ");
			System.out.println(clSwitch.getDefaultValue() != null ? " {default="
					+ clSwitch.getDefaultValue() + "}"
					: "");
		}
		System.out.println();
		System.out.println(_postUsage == null ? "" : _postUsage);
	} // printUsage

	public String toString () {
		StringBuffer sb = new StringBuffer(getClass().getName());
		Iterator iter = _switchMap.values().iterator();
		sb.append(":\n");
		while (iter.hasNext()) {
			CLSwitch s = (CLSwitch) iter.next();
			Object val = s.getResolvedValue();
			sb.append(s.getFullSwitchName());
			sb.append('[');
			sb.append("arg:" + s.getValue());
			sb.append(',');
			sb.append("resolved:");
			sb.append(val == null ? "<unresolved>" : val.toString());
			if (val != null) {
				sb.append(',');
				sb.append("type:" + s.getResolvedValue().getClass().getName());
			}
			sb.append(']');
			sb.append('\n');
		}
		return sb.toString();
	} // toString
} // CLArgumentProcessor
