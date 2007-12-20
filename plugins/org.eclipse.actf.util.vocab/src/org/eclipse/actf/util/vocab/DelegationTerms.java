/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.util.vocab;

import org.w3c.dom.Node;



public class DelegationTerms extends AbstractTerms {

    /* This class delegates the evaluation to the target below.  */
    private final IEvalTarget delegationTarget;

    private AbstractTerms nextCandidate;

    public DelegationTerms(IEvalTarget delegationTarget) {
        this.delegationTarget = delegationTarget;
    }

    @Override
    public boolean isClickable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isClickable(delegationTarget);
    }

    @Override
    public boolean isEditable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isEditable(delegationTarget);
    }

    @Override
    public boolean hasContent(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().hasContent(delegationTarget);
    }

    @Override
    public boolean hasReadingContent(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().hasReadingContent(delegationTarget);
    }

    @Override
    public boolean isEnabled(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isEnabled(delegationTarget);
    }

    @Override
    public boolean isInputable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isInputable(delegationTarget);
    }

    @Override
    public boolean isSelectable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isSelectable(delegationTarget);
    }

    @Override
    public boolean isMultiSelectable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMultiSelectable(delegationTarget);
    }

    @Override
    public boolean isButton(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isButton(delegationTarget);
    }

    @Override
    public boolean isListItem(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isListItem(delegationTarget);
    }

    @Override
    public boolean isListTop(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isListTop(delegationTarget);
    }

    @Override
    public boolean isCheckbox(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isCheckbox(delegationTarget);
    }

    @Override
    public boolean isChecked(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isChecked(delegationTarget);
    }
    
    @Override
    public boolean isLabel(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isLabel(delegationTarget);
    }

    @Override
    public boolean isLink(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isLink(delegationTarget);
    }

    @Override
    public boolean isVisitedLink(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isVisitedLink(delegationTarget);
    }

    @Override
    public boolean isRadio(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isRadio(delegationTarget);
    }

    @Override
    public boolean isCombobox(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isCombobox(delegationTarget);
    }

    @Override
    public boolean isSubmit(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isSubmit(delegationTarget);
    }

    @Override
    public boolean isFileEdit(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isFileEdit(delegationTarget);
    }

    @Override
    public boolean isTextarea(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isTextarea(delegationTarget);
    }

    @Override
    public boolean isTextbox(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isTextbox(delegationTarget);
    }

    @Override
    public boolean isPassword(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isPassword(delegationTarget);
    }

    @Override
    public boolean isValidNode(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isValidNode(delegationTarget);
    }

    @Override
    public boolean isVisibleNode(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isVisibleNode(delegationTarget);
    }

    @Override
    public boolean isEmbeddedObject(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isEmbeddedObject(delegationTarget);
    }

    @Override
    public boolean isImage(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isImage(delegationTarget);
    }

    @Override
    public boolean isMultilineEdit(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMultilineEdit(delegationTarget);
    }

    @Override
    public boolean isSelectOption(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isSelectOption(delegationTarget);
    }

    @Override
    public boolean isReducible(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isReducible(delegationTarget);
    }

    @Override
    public boolean isConnectable(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isConnectable(delegationTarget);
    }

    @Override
    public boolean find(String str, boolean exact, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().find(str, exact, delegationTarget);
    }

    @Override
    public boolean startsWith(String str, boolean exact, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().startsWith(str, exact, delegationTarget);
    }

    @Override
    public boolean isHeading(int level, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isHeading(level, delegationTarget);
    }

    @Override
    public boolean isHeadingJumpPoint(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isHeadingJumpPoint(delegationTarget);
    }

    @Override
    public boolean nodeLocation(Node refNode, boolean backward, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().nodeLocation(refNode, backward, delegationTarget);
    }

    @Override
    public boolean isAccessKey(char key, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isAccessKey(key, delegationTarget);
    }

    @Override
    public boolean isListItemJumpPoint(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isListItemJumpPoint(delegationTarget);
    }

    @Override
    public boolean isBlockJumpPointF(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isBlockJumpPointF(delegationTarget);
    }

    @Override
    public boolean isBlockJumpPointB(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isBlockJumpPointB(delegationTarget);
    }

    @Override
    public boolean isMedia(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMedia(delegationTarget);
    }

    @Override
    public boolean isFlashTopNode(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isFlashTopNode(delegationTarget);
    }

    @Override
    public boolean isFlashLastNode(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isFlashLastNode(delegationTarget);
    }

    @Override
    public boolean isMSAAFlash(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMSAAFlash(delegationTarget);
    }
    
    @Override
    public boolean isReachable(IEvalTarget node, Node target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isReachable(delegationTarget, target);
    }
    
    @Override
    public boolean isAlterable(IEvalTarget node) {
        if (delegationTarget == null) return false;
        return delegationTarget.getTerms().isAlterable(delegationTarget);
    }

}
