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

/**
 * The CLSwitch class is used in conjuction with the CLArgumentProcessor to embody a
 * single command line switch. An implementation of <code>IArgumentResolver</code>
 * should be associated with any <code>CLSwitch</code>.
 * 
 *
 *@see org.eclipse.actf.util.command.CLArgumentProcessor
 * @author Mike Squillace
 */
public class CLSwitch
{

	private String _name;

	private String _prefix = "-";

	private String _description = "";

	private String _value;

	private Object _resolvedValue;

	private String _defaultValue = "";

	private Class _type = Object.class;

	private boolean _required = false;

	private int _expectedArgs = 1;

	private IArgumentResolver _resolver;

	/**
	 * create a command line switch with the given name and type. By default, the
	 * switch is not required.
	 * 
	 * @param name -switch name (denoted with a hypthen (-) on the command line)
	 * @param desc - switch description or usage
	 * @param type - type of resolved value
	 */
	public CLSwitch (String name, String desc, Class type) {
		this(name, desc, type, false);
	}

	/**
	 * create a command line switch with the given name and type. By default, the
	 * switch is not required. Type resoltuion is left to the specified resolver.
	 * 
	 * @param name -switch name
	 * @param desc - switch description or usage
	 * @param resolver - argument resolver
	 */
	public CLSwitch (String name, String desc, IArgumentResolver resolver) {
		this(name, null, null, false, resolver);
	}

	/**
	 * create a command line switch with the given name and type. By default, the
	 * <code>org.eclipse.actf.util.command.DefaultArgumentResolver</code> is used to resolve switch values.
	 *  
	 * @param name -switch name
	 * @param desc - switch description or usage
	 * @param type - type of resolved value
	 * @param required - whether or not this switch is required on the command line
	 * @see org.eclipse.actf.util.command.DefaultArgumentResolver
	 */
	public CLSwitch (String name, String desc, Class type, boolean required) {
		this(name, desc, type, required, null);
	}

	/**
	 * create a command line switch with the given name and type. The argument or value
	 * passed to this switch is resolved via the specified implementation of the
	 * Argument<code>Resolver</code> interface.
	 *  
	 * @param name -switch name
	 * @param desc - switch description or usage
	 * @param type - type of resolved value
	 * @param required - whether or not this switch is required on the command line
	 * @param resolver - argument resolver
	 */
	public CLSwitch (String name, String desc, Class type, boolean required,
						IArgumentResolver resolver) {
		setName(name);
		setDescription(desc);
		setValueType(type);
		setRequired(required);
		setArgumentResolver(resolver);
	}

	/**
	 * @return Returns the switch name.
	 */
	public String getName () {
		return _name;
	}

	/**
	 * set the name of the switch. This name will also be used to refer
	 * to the switch on the command line when preceded by a hyphen (-).
	 * 
	 * @param name - The switch name
	 */
	public void setName (String name) {
		if (name == null) {
			throw new IllegalArgumentException("Switch names may not be null");
		}else if (!Character.isLetter(name.charAt(0))) {
			throw new IllegalArgumentException("Switch names must begin with a letter: "
					+ name);
		}else {
			for (int c = 1; c < name.length(); ++c) {
				if (!Character.isLetter(name.charAt(c))
						&& !Character.isDigit(name.charAt(c))) { throw new IllegalArgumentException("Switch names must be alpha-numeric: "
						+ name); }
			}
		}
		_name = name;
	}

	/**
	 * @return returns the switch prefix as it should be used on the command line
	 */
	public String getPrefix () {
		return _prefix;
	}

	/**
	 * set the command line switch's prefix. The switch must ocur on the command line
	 * preceded with this prefix. The default prefix is '0'.
	 * 
	 * @param prefix - prefix for switch
	 */
	public void setPrefix (String prefix) {
		_prefix = prefix == null ? "-" : prefix;
	}

	/**
	 * returns the full  switch name which is composed of its prefix
	 * followed by its name.
	 * 
	 * @return full switch name including prefix
	 */
	public String getFullSwitchName () {
		return _prefix + _name;
	}

	/**
	 * @return Returns the description or help for the switch.
	 */
	public String getDescription () {
		return _description;
	}

	/**
	 * @param description the help or description of the switch (appears in the usage message)
	 */
	public void setDescription (String description) {
		_description = description == null ? "" : description;
	}

	/**
	 * 
	 * @return expected number of arguments for this switch
	 * @see #setExpectedNumberOfArgs(int)
	 */
	public int getExpectedNumberOfArgs () {
		return _expectedArgs;
	}

	/**
	 * set the number of arguments expected by this switch. This value determines
	 * the number of arguments from the original command line argument array as received by the <code>main(String[])</code>
	 * method will be used for this switch. Supplying fewer than the number of expected
	 * number of arguments on the command line is acceptable and additional arguments beyond the number of expected arguments
	 * will be flagged.
	 * 
	 * @param args - expected number of arguments (default is 1)
	 */
	public void setExpectedNumberOfArgs (int args) {
		_expectedArgs = args >= 0 ? args : 1;
	}

	/**
	 * @return Returns whether or not this switch is required
	 */
	public boolean isRequired () {
		return _required;
	}

	/**
	 * set to <code>true</code> if the switch and its corresponding value are required
	 * for the application.
	 * 
	 * @param required <code>true</code> if the switch is required, <code>false</code> otherwise
	 */
	public void setRequired (boolean required) {
		_required = required;
	}

	/**
	 * returns the desired type of the value or agument corresponding to the swithh. An implementation of
	 * <code>IArgumentResolver</coe> is responsible for resolving the value or argument to this type.
	 * 
	 * @return Returns the type of the value/argument of the switch
	 */
	public Class getValueType () {
		return _type;
	}

	/**
	 * set the type of the value or argument to this switch.
	 * 
	 * @param type the type to which the argument to this switch is to be resolved
	 * @see #getValueType()
	 */
	public void setValueType (Class type) {
		_type = type;
	}

	/**
	 * returns the unresolved value or argument passed to the switch on the command line.
	 * 
	 * @return Returns the unresolved argument or value passed to this switch
	 */
	public String getValue () {
		return _value;
	}

	/**
	 * @param value The argument or value given to the switch on the command line
	 */
	public void setValue (String value) {
		_value = value;
	}

	/**
	 * resolve the value passed from the command line. The value will be resolved via
	 * this switch's IArgumentResolver and be of the type with which this switch
	 * was defined.
	 * 
	 * @param arg - command line argument for this switch
	 * @return returns the resolved value for the argument
	 * @throws Exception
	 */
	public Object resolveValue (String arg) throws Exception {
		if (_resolvedValue == null) {
			setValue(arg);
			if (_type.equals(Boolean.class) || _type.equals(Boolean.TYPE)) {
				_resolvedValue = Boolean.valueOf(_value != null
						&& _value.equals("__true__"));
			}else {
				_resolvedValue = _resolver.resolve(_value, _type);
			}
		}
		return _resolvedValue;
	}

	/**
	 * @return Returns the default value (if any) for this switch.
	 */
	public String getDefaultValue () {
		return _defaultValue;
	}

	/**
	 * @param value The default value for this switch if no value is given
	 */
	public void setDefaultValue (String value) {
		_defaultValue = _value = value;
	}

	/**
	 * set the switch to its default value
	 *
	 */
	public void setToDefaultValue () {
		_value = _defaultValue;
	}

	/**
	 * returns the resolved value of the argument passed to the switch. The resolved
	 * value is of the type specified during the creation of the switch or via
	 * the <code>setType(Class)</code> method.
	 * 
	 * @return the resolved value of the switch
	 */
	public Object getResolvedValue () {
		Object result = _resolvedValue;
		if (_resolvedValue == null) {
			try {
				result = resolveValue(_value);
			}catch (Exception e) {
				System.err.println("Could not resolve " + _value
						+ " for switch named " + _name);
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @return Returns the argument resolver.
	 */
	public IArgumentResolver getArgumentResolver () {
		return _resolver;
	}

	/**
	 * @param resolver the argument resolved to be used
	 */
	public void setArgumentResolver (IArgumentResolver resolver) {
		_resolver = resolver == null ? new DefaultArgumentResolver() : resolver;
	}
} // CLSwitch
