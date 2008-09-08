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

	public static IProposition or(IProposition... args) {
		OrOperator ret = new OrOperator();
		for (IProposition ip : args) {
			ret.add(ip);
		}
		return ret;
	}

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

	public static IProposition isClickable() {
		return get("Clickable");
	}

	public static IProposition hasContent() {
		return get("HasContent");
	}

	public static IProposition isEditable() {
		return get("Editable");
	}

	public static IProposition isSelectable() {
		return get("Selectable");
	}

	public static IProposition isEmbeddedObject() {
		return get("EmbeddedObject");
	}

	public static IProposition isValidNode() {
		return get("ValidNode");
	}

	public static IProposition isVisibleNode() {
		return get("VisibleNode");
	}

	public static IProposition isInputable() {
		return get("Inputable");
	}

	public static IProposition isButton() {
		return get("Button");
	}

	public static IProposition isListItem() {
		return get("ListItem");
	}

	public static IProposition isListTop() {
		return get("ListTop");
	}

	public static IProposition isCheckbox() {
		return get("Checkbox");
	}

	public static IProposition isChecked() {
		return get("Checked");
	}

	public static IProposition isLabel() {
		return get("Label");
	}

	public static IProposition isLink() {
		return get("Link");
	}

	public static IProposition isVisitedLink() {
		return get("VisitedLink");
	}

	public static IProposition isRadio() {
		return get("Radio");
	}

	public static IProposition isSubmit() {
		return get("Submit");
	}

	public static IProposition isFileEdit() {
		return get("FileEdit");
	}

	public static IProposition isCombobox() {
		return get("Combobox");
	}

	public static IProposition isTextarea() {
		return get("Textarea");
	}

	public static IProposition isTextbox() {
		return get("Textbox");
	}

	public static IProposition isPassword() {
		return get("Password");
	}

	public static IProposition isSelectOption() {
		return get("SelectOption");
	}

	public static IProposition isImage() {
		return get("Image");
	}

	public static IProposition isMultilineEdit() {
		return get("MultilineEdit");
	}

	public static IProposition isReducible() {
		return get("Reducible");
	}

	public static IProposition isHeading() {
		return get("Heading");
	}

	public static IProposition isHeading1() {
		return get("Heading1");
	}

	public static IProposition isHeading2() {
		return get("Heading2");
	}

	public static IProposition isHeading3() {
		return get("Heading3");
	}

	public static IProposition isHeading4() {
		return get("Heading4");
	}

	public static IProposition isHeading5() {
		return get("Heading5");
	}

	public static IProposition isHeading6() {
		return get("Heading6");
	}

	public static IProposition find(String str, boolean exact) {
		return new FindProposition(str, exact);
	}

	public static IProposition startsWith(String str, boolean exact) {
		return new StartsWithProposition(str, exact);
	}

	public static IProposition nodeLocation(Node refNode, boolean backward) {
		return new NodeLocationProposition(refNode, backward);
	}

	public static IProposition isAccessKey(char key) {
		return new IsAccessKey(key);
	}

	public static IProposition isConnectable() {
		return get("Connectable");
	}

	public static IProposition hasReadingContent() {
		return get("HasReadingContent");
	}

	public static IProposition isBlockJumpPointF() {
		return get("BlockJumpPointF");
	}

	public static IProposition isBlockJumpPointB() {
		return get("BlockJumpPointB");
	}

	public static IProposition isMultiSelectable() {
		return get("MultiSelectable");
	}

	public static IProposition isMedia() {
		return get("Media");
	}

	public static IProposition isFlashTopNode() {
		return get("FlashTopNode");
	}

	public static IProposition isFlashLastNode() {
		return get("FlashLastNode");
	}

	public static IProposition isMSAAFlash() {
		return get("MSAAFlash");
	}

	public static IProposition isReachable(Node baseNode) {
		return new IsReachable(baseNode);
	}

	public static IProposition isAlterable() {
		return get("Alterable");
	}

	public enum FlashMode {
		MSAA, FLASH_DOM, NO_FLASH
	}

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

	public static FlashMode getWindowlessFlashMode() {
		String mode = VocabPlugin.getDefault().getPreferenceStore().getString(
				VocabPreferenceConstants.WNDLESS_FLASH);
		if (VocabPreferenceConstants.VALUE_FLASH_DOM.equals(mode))
			return FlashMode.FLASH_DOM;
		if (VocabPreferenceConstants.VALUE_NO_FLASH.equals(mode))
			return FlashMode.NO_FLASH;
		return FlashMode.NO_FLASH;
	}

	public static boolean isReadNoAltImage() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NO_ALT);
	}

	public static boolean isReadNullAltImage() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NULL_ALT);
	}

	public static boolean isReadNoAltImageLink() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NO_ALT_LINK);
	}

	public static boolean isReadNullAltImageLink() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.READ_NULL_ALT_LINK);
	}

	public static boolean isSkipIconLink() {
		return VocabPlugin.getDefault().getPreferenceStore().getBoolean(
				VocabPreferenceConstants.SKIP_ICON_LINK);
	}
}
