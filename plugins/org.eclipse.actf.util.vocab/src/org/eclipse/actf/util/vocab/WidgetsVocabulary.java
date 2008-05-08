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

/**
 * WidgetsVocabulary defines propositions related to widgets such as button and
 * radio button.
 */
public class WidgetsVocabulary {
	private static class IsButton implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isButton(node);
		}

		public String getName() {
			return "isButton";
		}
	}

	private static class IsListItem implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isListItem(node);
		}

		public String getName() {
			return "isListItem";
		}
	}

	private static class IsListTop implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isListTop(node);
		}

		public String getName() {
			return "isListTop";
		}
	}

	private static class IsCheckbox implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isCheckbox(node);
		}

		public String getName() {
			return "isCheckbox";
		}
	}

	private static class IsChecked implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isChecked(node);
		}

		public String getName() {
			return "isChecked";
		}
	}

	private static class IsRadio implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isRadio(node);
		}

		public String getName() {
			return "isRadio";
		}
	}

	private static class IsCombobox implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isCombobox(node);
		}

		public String getName() {
			return "isCombobox";
		}
	}

	private static class IsSubmit implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isSubmit(node);
		}

		public String getName() {
			return "isSubmit";
		}
	}

	private static class IsFileEdit implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isFileEdit(node);
		}

		public String getName() {
			return "isFileEdit";
		}
	}

	private static class IsLabel implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isLabel(node);
		}

		public String getName() {
			return "isLabel";
		}
	}

	private static class IsLink implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isLink(node);
		}

		public String getName() {
			return "isLink";
		}
	}

	private static class IsVisitedLink implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isVisitedLink(node);
		}

		public String getName() {
			return "isVisitedLink";
		}
	}

	private static class IsTextarea implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isTextarea(node);
		}

		public String getName() {
			return "isTextarea";
		}
	}

	private static class IsTextbox implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isTextbox(node);
		}

		public String getName() {
			return "isTextbox";
		}
	}

	private static class IsPassword implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isPassword(node);
		}

		public String getName() {
			return "isPassword";
		}
	}

	private static class IsMultilineEdit implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isMultilineEdit(node);
		}

		public String getName() {
			return "isMulti";
		}
	}

	private static class IsSelectOption implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isSelectOption(node);
		}

		public String getName() {
			return "isSelectOption";
		}
	}

	private static class IsImage implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isImage(node);
		}

		public String getName() {
			return "isImage";
		}
	}

	private static class IsFlashTopNode implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isFlashTopNode(node);
		}

		public String getName() {
			return "isFlashTopNode";
		}
	}

	private static class IsFlashLastNode implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isFlashLastNode(node);
		}

		public String getName() {
			return "isFlashLastNode";
		}
	}

	private static class IsMSAAFlash implements IProposition {
		public boolean eval(IEvalTarget node) {
			return node.getTerms().isMSAAFlash(node);
		}

		public String getName() {
			return "isMSAAFlash";
		}
	}

	private static class IsHeading implements IProposition {
		private int level = 0;

		public IsHeading() {
		}

		public IsHeading(int level) {
			this.level = level;
		}

		public boolean eval(IEvalTarget node) {
			return node.getTerms().isHeading(level, node);
		}

		public String getName() {
			if (level != 0)
				return "isHeading" + level;
			return "isHeading";
		}
	}

	static void initialize() {
		Vocabulary.add("Button", new IsButton());
		Vocabulary.add("ListItem", new IsListItem());
		Vocabulary.add("ListTop", new IsListTop());
		Vocabulary.add("Checkbox", new IsCheckbox());
		Vocabulary.add("Checked", new IsChecked());
		Vocabulary.add("Radio", new IsRadio());
		Vocabulary.add("Combobox", new IsCombobox());
		Vocabulary.add("Submit", new IsSubmit());
		Vocabulary.add("FileEdit", new IsFileEdit());
		Vocabulary.add("Label", new IsLabel());
		Vocabulary.add("Link", new IsLink());
		Vocabulary.add("VisitedLink", new IsVisitedLink());
		Vocabulary.add("Textarea", new IsTextarea());
		Vocabulary.add("Textbox", new IsTextbox());
		Vocabulary.add("Password", new IsPassword());
		Vocabulary.add("MultilineEdit", new IsMultilineEdit());
		Vocabulary.add("SelectOption", new IsSelectOption());
		Vocabulary.add("Image", new IsImage());
		Vocabulary.add("Heading", new IsHeading());
		Vocabulary.add("Heading1", new IsHeading(1));
		Vocabulary.add("Heading2", new IsHeading(2));
		Vocabulary.add("Heading3", new IsHeading(3));
		Vocabulary.add("Heading4", new IsHeading(4));
		Vocabulary.add("Heading5", new IsHeading(5));
		Vocabulary.add("Heading6", new IsHeading(6));

		Vocabulary.add("FlashTopNode", new IsFlashTopNode());
		Vocabulary.add("FlashLastNode", new IsFlashLastNode());
		Vocabulary.add("MSAAFlash", new IsMSAAFlash());
	}
}
