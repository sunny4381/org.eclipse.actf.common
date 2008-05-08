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

import org.w3c.dom.Node;

/**
 * AbstractTerms is the default term definition. All methods return <i>false</i>.
 * Each class extends AbstractTerms should implements methods which used in
 * itself.
 * 
 * @see Vocabulary
 */
public abstract class AbstractTerms {

	public boolean isClickable(IEvalTarget target) {
		return false;
	}

	public boolean isEditable(IEvalTarget target) {
		return false;
	}

	public boolean hasContent(IEvalTarget target) {
		return false;
	}

	public boolean isEnabled(IEvalTarget target) {
		return false;
	}

	public boolean isInputable(IEvalTarget target) {
		return false;
	}

	public boolean isSelectable(IEvalTarget target) {
		return false;
	}

	public boolean isButton(IEvalTarget target) {
		return false;
	}

	public boolean isCheckbox(IEvalTarget target) {
		return false;
	}

	public boolean isLabel(IEvalTarget target) {
		return false;
	}

	public boolean isLink(IEvalTarget target) {
		return false;
	}

	public boolean isRadio(IEvalTarget target) {
		return false;
	}

	public boolean isCombobox(IEvalTarget target) {
		return false;
	}

	public boolean isSubmit(IEvalTarget target) {
		return false;
	}

	public boolean isTextarea(IEvalTarget target) {
		return false;
	}

	public boolean isTextbox(IEvalTarget target) {
		return false;
	}

	public boolean isPassword(IEvalTarget target) {
		return false;
	}

	public boolean isValidNode(IEvalTarget target) {
		return false;
	}

	public boolean isVisibleNode(IEvalTarget target) {
		return false;
	}

	public boolean isEmbeddedObject(IEvalTarget target) {
		return false;
	}

	public boolean isImage(IEvalTarget target) {
		return false;
	}

	public boolean isMultilineEdit(IEvalTarget node) {
		return false;
	}

	public boolean isReducible(IEvalTarget node) {
		return false;
	}

	public boolean isConnectable(IEvalTarget node) {
		return false;
	}

	public boolean find(String str, boolean exact, IEvalTarget node) {
		return false;
	}

	public boolean startsWith(String str, boolean exact, IEvalTarget node) {
		return false;
	}

	public boolean isHeading(int level, IEvalTarget node) {
		return false;
	}

	public boolean nodeLocation(Node refNode, boolean backward, IEvalTarget node) {
		return false;
	}

	public boolean isHeadingJumpPoint(IEvalTarget node) {
		return false;
	}

	public boolean hasReadingContent(IEvalTarget node) {
		return false;
	}

	public boolean isListItemJumpPoint(IEvalTarget node) {
		return false;
	}

	public boolean isBlockJumpPointF(IEvalTarget node) {
		return false;
	}

	public boolean isBlockJumpPointB(IEvalTarget node) {
		return false;
	}

	public boolean isFileEdit(IEvalTarget node) {
		return false;
	}

	public boolean isChecked(IEvalTarget node) {
		return false;
	}

	public boolean isListItem(IEvalTarget node) {
		return false;
	}

	public boolean isListTop(IEvalTarget node) {
		return false;
	}

	public boolean isSelectOption(IEvalTarget node) {
		return false;
	}

	public boolean isVisitedLink(IEvalTarget node) {
		return false;
	}

	public boolean isMultiSelectable(IEvalTarget node) {
		return false;
	}

	public boolean isAccessKey(char key, IEvalTarget node) {
		return false;
	}

	public boolean isMedia(IEvalTarget node) {
		return false;
	}

	public boolean isFlashTopNode(IEvalTarget node) {
		return false;
	}

	public boolean isFlashLastNode(IEvalTarget node) {
		return false;
	}

	public boolean isMSAAFlash(IEvalTarget node) {
		return false;
	}

	public boolean isReachable(IEvalTarget node, Node target) {
		return false;
	}

	public boolean isAlterable(IEvalTarget target) {
		return false;
	}

}
