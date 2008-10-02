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

package org.eclipse.actf.util.vocab;

import java.util.HashMap;

import org.eclipse.actf.util.internal.vocab.impl.AndOperator;
import org.eclipse.actf.util.internal.vocab.impl.Function;
import org.eclipse.actf.util.internal.vocab.impl.OrOperator;
import org.eclipse.actf.util.vocab.ui.preferences.VocabPreferenceConstants;
import org.w3c.dom.Node;

/**
 * Vocabulary treats the meaning of instances of {@link IEvalTarget} by using
 * {@link IProposition}. {@link IProposition} represents a question whether a
 * {@link IEvalTarget} instance is matched with the proposition or not. Actual
 * meaning is provided by the term implementation which extends
 * {@link AbstractTerms}. Each instance of the {@link IEvalTarget} is
 * associated with a term implementation.
 * 
 * The following is the flow to get the meaning of the {@link IEvalTarget}
 * instance.
 * 
 * <pre>
 *  1. get an {@link IProposition} from Vocabulary.
 *  2. call {@link IProposition#eval(IEvalTarget)} method with an {@link IEvalTarget}.
 *  3. {@link IProposition#eval(IEvalTarget)} method gets the associated term.
 *  4. {@link IProposition#eval(IEvalTarget)} method calls the method corresponding to itself with the {@link IEvalTarget}.
 *     For example, {@link Vocabulary#isClickable()} calls {@link AbstractTerms#isClickable(IEvalTarget)}.
 * </pre>
 */
public class Vocabulary {
	private static HashMap<String, IProposition> propMap = new HashMap<String, IProposition>();

	private static class IsClickable implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isClickable(node);
		}

		public String getName() {
			return "isClickable";
		}
	}

	private static class IsInputable implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isInputable(node);
		}

		public String getName() {
			return "isInputable";
		}
	}

	private static class IsSelectable implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isSelectable(node);
		}

		public String getName() {
			return "isSelectable";
		}
	}

	private static class IsMultiSelectable implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isMultiSelectable(node);
		}

		public String getName() {
			return "isMultiSelectable";
		}
	}

	private static class IsValidNode implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isValidNode(node);
		}

		public String getName() {
			return "isValidNode";
		}
	}

	private static class HasContent implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().hasContent(node);
		}

		public String getName() {
			return "hasContent";
		}
	}

	private static class HasReadingContent implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().hasReadingContent(node);
		}

		public String getName() {
			return "hasReadingContent";
		}
	}

	private static class IsVisibleNode implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isVisibleNode(node);
		}

		public String getName() {
			return "isVisibleNode";
		}
	}

	private static class IsEmbeddedObject implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isEmbeddedObject(node);
		}

		public String getName() {
			return "isEmbeddedObject";
		}
	}

	private static class IsReducible implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isReducible(node);
		}

		public String getName() {
			return "isReducible";
		}
	}

	private static class IsBlockJumpPointF implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isBlockJumpPointF(node);
		}

		public String getName() {
			return "isBlockJumpPointF";
		}
	}

	private static class IsBlockJumpPointB implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isBlockJumpPointB(node);
		}

		public String getName() {
			return "isBlockJumpPointB";
		}
	}

	private static class IsMedia implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isMedia(node);
		}

		public String getName() {
			return "isMedia";
		}
	}

	private static class IsReachable implements IProposition {
		private Node target;

		public IsReachable(Node target) {
			this.target = target;
		}

		public boolean eval(IEvalTarget node) {
			return node.getTerms().isReachable(node, target);
		}

		public String getName() {
			return "isReachable";
		}
	}

	private static class IsConnectable implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isConnectable(node);
		}

		public String getName() {
			return "isConnectable";
		}
	}

	private static class IsAlterable implements IProposition { // TODO awesome
		// method
		// name... :(
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isAlterable(node);
		}

		public String getName() {
			return "isAlterable";
		}
	}

	static {
		WidgetsVocabulary.initialize();

		add("Clickable", new IsClickable());
		add("Clickable", or("Button", "Checkbox", "Submit", "Link"));
		add("Inputable", new IsInputable());
		add("Inputable", or("Textbox", "Textarea", "Password"));
		add("Selectable", new IsSelectable());
		add("MultiSelectable", new IsMultiSelectable());
		add("Selectable", or("Combobox", "Radio"));
		add("Editable", or("Inputable", "Selectable", "MultiSelectable"));
		add("ValidNode", new IsValidNode());
		add("HasContent", new HasContent());
		add("HasReadingContent", new HasReadingContent());
		add("VisibleNode", new IsVisibleNode());
		add("EmbeddedObject", new IsEmbeddedObject());
		add("Reducible", new IsReducible());
		add("BlockJumpPointF", new IsBlockJumpPointF());
		add("BlockJumpPointB", new IsBlockJumpPointB());
		add("Media", new IsMedia());
		add("Connectable", new IsConnectable());
		add("Alterable", new IsAlterable());
	}

	static void addFunc(String name, String funcName, String... args) {
		add(name, new Function(funcName, args));
	}

	static void add(String name, IProposition prop) {
		IProposition existProp = propMap.get(name);
		if (existProp == null) {
			propMap.put(name, prop);
		} else {
			IProposition newProp = new OrOperator(existProp, prop);
			propMap.put(name, newProp);
		}
	}

	private static IProposition or(String... args) {
		OrOperator ret = new OrOperator();
		for (String s : args) {
			ret.add(get(s));
		}
		return ret;
	}

	// private static IProposition and(String... args) {
	// AndOperator ret = new AndOperator();
	// for (String s : args) {
	// ret.add(get(s));
	// }
	// return ret;
	// }

	/**
	 * @param args
	 *            the instances of IPropsition to be concatenated by "OR"
	 * @return the result of the logical OR operation
	 */
	public static IProposition or(IProposition... args) {
		OrOperator ret = new OrOperator();
		for (IProposition ip : args) {
			ret.add(ip);
		}
		return ret;
	}

	/**
	 * @param args
	 *            the instances of IPropsition to be concatenated by "AND"
	 * @return the result of the logical AND operation
	 */
	public static IProposition and(IProposition... args) {
		AndOperator ret = new AndOperator();
		for (IProposition ip : args) {
			ret.add(ip);
		}
		return ret;
	}

	private static class FindProposition implements IProposition {
		private final String str;

		private final boolean exact;

		public FindProposition(String str, boolean exact) {
			this.str = str;
			this.exact = exact;
		}

		public boolean eval(IEvalTarget node) {
			return node.getTerms().find(str, exact, node);
		}

		public String getName() {
			return "find(" + str + ")";
		}
	}

	private static class StartsWithProposition implements IProposition {
		private final String str;

		private final boolean exact;

		public StartsWithProposition(String str, boolean exact) {
			this.str = str;
			this.exact = exact;
		}

		public boolean eval(IEvalTarget node) {
			return node.getTerms().startsWith(str, exact, node);
		}

		public String getName() {
			return "startsWith(" + str + ")";
		}
	}

	private static class NodeLocationProposition implements IProposition {
		private final Node refNode;
		private final boolean backward;

		public NodeLocationProposition(Node refNode, boolean backward) {
			this.refNode = refNode;
			this.backward = backward;
		}

		public boolean eval(IEvalTarget node) {
			return node.getTerms().nodeLocation(refNode, backward, node);
		}

		public String getName() {
			return "nodeLocation(" + refNode + ":" + backward + ")";
		}
	}

	private static class IsAccessKey implements IProposition {
		private final char key;

		public IsAccessKey(char key) {
			this.key = key;
		}

		public boolean eval(IEvalTarget node) {
			return node.getTerms().isAccessKey(key, node);
		}

		public String getName() {
			return "accessKey(" + key + ")";
		}
	}

	// public interface

	public static IProposition get(String name) {
		return propMap.get(name);
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is clickable or not
	 */
	public static IProposition isClickable() {
		return get("Clickable");
	}

	/**
	 * Having content means that the element has content to be displayed in the
	 * aiBrowser
	 * 
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node has content
	 */
	public static IProposition hasContent() {
		return get("HasContent");
	}

	/**
	 * text box, text area, password, combo box, check box, radio button, and so on.
	 * 
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node can be edited.
	 */
	public static IProposition isEditable() {
		return get("Editable");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node has some options to be selected.
	 */
	public static IProposition isSelectable() {
		return get("Selectable");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is embedded object.
	 */
	public static IProposition isEmbeddedObject() {
		return get("EmbeddedObject");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is valid for aiBrowser's tree item.
	 */
	public static IProposition isValidNode() {
		return get("ValidNode");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is visible or not.
	 */
	public static IProposition isVisibleNode() {
		return get("VisibleNode");
	}

	/**
	 * text box, text area, password
	 * 
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node can be input text.
	 */
	public static IProposition isInputable() {
		return get("Inputable");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is button or not.
	 */
	public static IProposition isButton() {
		return get("Button");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is list item or not
	 */
	public static IProposition isListItem() {
		return get("ListItem");
	}

	/**
	 * OL, UL, DL tags
	 * 
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is list element or not
	 */
	public static IProposition isListTop() {
		return get("ListTop");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is check box or not
	 */
	public static IProposition isCheckbox() {
		return get("Checkbox");
	}
	
	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is checked or not
	 */
	public static IProposition isChecked() {
		return get("Checked");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is label element or not
	 */
	public static IProposition isLabel() {
		return get("Label");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is anchor element or not
	 */
	public static IProposition isLink() {
		return get("Link");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is visited link or not
	 */
	public static IProposition isVisitedLink() {
		return get("VisitedLink");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is radio button or not
	 */
	public static IProposition isRadio() {
		return get("Radio");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is submit button or not
	 */
	public static IProposition isSubmit() {
		return get("Submit");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is file edit or not
	 */
	public static IProposition isFileEdit() {
		return get("FileEdit");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is combo box or not
	 */
	public static IProposition isCombobox() {
		return get("Combobox");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is text area or not
	 */
	public static IProposition isTextarea() {
		return get("Textarea");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is text box or not
	 */
	public static IProposition isTextbox() {
		return get("Textbox");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is password or not
	 */
	public static IProposition isPassword() {
		return get("Password");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is option of select form or not
	 */
	public static IProposition isSelectOption() {
		return get("SelectOption");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is image element or not
	 */
	public static IProposition isImage() {
		return get("Image");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is text edit control having multiple lines
	 */
	public static IProposition isMultilineEdit() {
		return get("MultilineEdit");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node can be omitted to create simplified tree by fennec auto translator
	 */
	public static IProposition isReducible() {
		return get("Reducible");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading element or not
	 */
	public static IProposition isHeading() {
		return get("Heading");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading level 1 element or not
	 */
	public static IProposition isHeading1() {
		return get("Heading1");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading level 2 element or not
	 */
	public static IProposition isHeading2() {
		return get("Heading2");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading level 3 element or not
	 */
	public static IProposition isHeading3() {
		return get("Heading3");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading level 4 element or not
	 */
	public static IProposition isHeading4() {
		return get("Heading4");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading level 5 element or not
	 */
	public static IProposition isHeading5() {
		return get("Heading5");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is heading level 6 element or not
	 */
	public static IProposition isHeading6() {
		return get("Heading6");
	}

	/**
	 * @param str the string to be found
	 * @param exact if true then case-sensitive
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node has the string
	 */
	public static IProposition find(String str, boolean exact) {
		return new FindProposition(str, exact);
	}

	/**
	 * @param str the string to be found
	 * @param exact if true then case-sensitive
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node has string which starts with str
	 */
	public static IProposition startsWith(String str, boolean exact) {
		return new StartsWithProposition(str, exact);
	}
	
	/**
	 * @param refNode the reference node to be compared
	 * @param backward the target node is appeared before the refNode or not
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is appeared before or after the reference node
	 */
	public static IProposition nodeLocation(Node refNode, boolean backward) {
		return new NodeLocationProposition(refNode, backward);
	}

	/**
	 * @param key the character of the access key
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node has the access key
	 */
	public static IProposition isAccessKey(char key) {
		return new IsAccessKey(key);
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node can be read with next sibling or not
	 */
	public static IProposition isConnectable() {
		return get("Connectable");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node has content can be read.
	 */
	public static IProposition hasReadingContent() {
		return get("HasReadingContent");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is suitable for block jump point in forward direction
	 */
	public static IProposition isBlockJumpPointF() {
		return get("BlockJumpPointF");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is suitable for block jump point in backward direction
	 */
	public static IProposition isBlockJumpPointB() {
		return get("BlockJumpPointB");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is multi selectable list
	 */
	public static IProposition isMultiSelectable() {
		return get("MultiSelectable");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is sound or video object
	 */
	public static IProposition isMedia() {
		return get("Media");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is Flash element
	 */
	public static IProposition isFlashTopNode() {
		return get("FlashTopNode");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is the last node of Flash element
	 */
	public static IProposition isFlashLastNode() {
		return get("FlashLastNode");
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node is Flash element from MSAA
	 */
	public static IProposition isMSAAFlash() {
		return get("MSAAFlash");
	}

	/**
	 * @param baseNode the node to be checked for reaching
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node can be read with next element in HTML level.
	 */
	public static IProposition isReachable(Node baseNode) {
		return new IsReachable(baseNode);
	}

	/**
	 * @return the instance of {@link IProposition} which evaluates whether a
	 *         node can be added alternative text
	 */
	public static IProposition isAlterable() {
		return get("Alterable");
	}

	public enum FlashMode {
		MSAA, FLASH_DOM, NO_FLASH
	}

	/**
	 * @return the mode for normal Flash
	 */
	public static FlashMode getNormalFlashMode() {
		String mode = VocabPlugin.getDefault().getPreferenceStore().getString(
				VocabPreferenceConstants.NORMAL_FLASH);
		if (VocabPreferenceConstants.VALUE_MSAA_FLASH.equals(mode))
			return FlashMode.MSAA;
		if (VocabPreferenceConstants.VALUE_FLASH_DOM.equals(mode))
			return FlashMode.FLASH_DOM;
		if (VocabPreferenceConstants.VALUE_NO_FLASH.equals(mode))
			return FlashMode.NO_FLASH;
		return FlashMode.NO_FLASH;
	}

	/**
	 * @return the mode for windowless flash mode
	 */
	public static FlashMode getWindowlessFlashMode() {
		String mode = VocabPlugin.getDefault().getPreferenceStore().getString(
				VocabPreferenceConstants.WNDLESS_FLASH);
		if (VocabPreferenceConstants.VALUE_FLASH_DOM.equals(mode))
			return FlashMode.FLASH_DOM;
		if (VocabPreferenceConstants.VALUE_NO_FLASH.equals(mode))
			return FlashMode.NO_FLASH;
		return FlashMode.NO_FLASH;
	}

	/**
	 * @return whether the system reads no alt image
	 */
	public static boolean isReadNoAltImage() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NO_ALT);
	}

	/**
	 * @return whether the system read null alt image
	 */
	public static boolean isReadNullAltImage() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NULL_ALT);
	}

	/**
	 * @return whether the system reads no alt image link
	 */
	public static boolean isReadNoAltImageLink() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NO_ALT_LINK);
	}

	/**
	 * @return whether the system reads null alt image link
	 */
	public static boolean isReadNullAltImageLink() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NULL_ALT_LINK);
	}

	/**
	 * @return
	 */
	public static boolean isSkipIconLink() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.SKIP_ICON_LINK);
	}
}
