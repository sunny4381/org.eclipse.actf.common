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



/**
 * This class delegates the evaluation to the delegation target.
 */
public class DelegationTerms extends AbstractTerms {

    private final IEvalTarget delegationTarget;

    //private AbstractTerms nextCandidate;

    /**
     * @param delegationTarget the delegation target.
     */
    public DelegationTerms(IEvalTarget delegationTarget) {
        this.delegationTarget = delegationTarget;
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isClickable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isClickable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isClickable(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isEditable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isEditable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isEditable(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#hasContent(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean hasContent(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().hasContent(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#hasReadingContent(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean hasReadingContent(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().hasReadingContent(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isEnabled(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isEnabled(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isEnabled(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isInputable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isInputable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isInputable(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isSelectable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isSelectable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isSelectable(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isMultiSelectable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isMultiSelectable(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMultiSelectable(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isButton(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isButton(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isButton(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isListItem(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isListItem(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isListItem(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isListTop(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isListTop(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isListTop(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isCheckbox(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isCheckbox(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isCheckbox(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isChecked(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isChecked(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isChecked(delegationTarget);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isLabel(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isLabel(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isLabel(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isLink(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isLink(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isLink(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isVisitedLink(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isVisitedLink(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isVisitedLink(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isRadio(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isRadio(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isRadio(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isCombobox(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isCombobox(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isCombobox(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isSubmit(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isSubmit(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isSubmit(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isFileEdit(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isFileEdit(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isFileEdit(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isTextarea(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isTextarea(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isTextarea(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isTextbox(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isTextbox(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isTextbox(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isPassword(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isPassword(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isPassword(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isValidNode(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isValidNode(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isValidNode(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isVisibleNode(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isVisibleNode(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isVisibleNode(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isEmbeddedObject(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isEmbeddedObject(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isEmbeddedObject(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isImage(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isImage(IEvalTarget target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isImage(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isMultilineEdit(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isMultilineEdit(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMultilineEdit(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isSelectOption(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isSelectOption(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isSelectOption(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isReducible(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isReducible(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isReducible(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isConnectable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isConnectable(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isConnectable(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#find(java.lang.String, boolean, org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean find(String str, boolean exact, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().find(str, exact, delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#startsWith(java.lang.String, boolean, org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean startsWith(String str, boolean exact, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().startsWith(str, exact, delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isHeading(int, org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isHeading(int level, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isHeading(level, delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isHeadingJumpPoint(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isHeadingJumpPoint(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isHeadingJumpPoint(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#nodeLocation(org.w3c.dom.Node, boolean, org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean nodeLocation(Node refNode, boolean backward, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().nodeLocation(refNode, backward, delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isAccessKey(char, org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isAccessKey(char key, IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isAccessKey(key, delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isListItemJumpPoint(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isListItemJumpPoint(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isListItemJumpPoint(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isBlockJumpPointF(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isBlockJumpPointF(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isBlockJumpPointF(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isBlockJumpPointB(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isBlockJumpPointB(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isBlockJumpPointB(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isMedia(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isMedia(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMedia(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isFlashTopNode(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isFlashTopNode(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isFlashTopNode(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isFlashLastNode(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isFlashLastNode(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isFlashLastNode(delegationTarget);
    }

    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isMSAAFlash(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isMSAAFlash(IEvalTarget node) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isMSAAFlash(delegationTarget);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isReachable(org.eclipse.actf.util.vocab.IEvalTarget, org.w3c.dom.Node)
     */
    @Override
    public boolean isReachable(IEvalTarget node, Node target) {
        if (delegationTarget == null)
            return false;
        return delegationTarget.getTerms().isReachable(delegationTarget, target);
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.actf.util.vocab.AbstractTerms#isAlterable(org.eclipse.actf.util.vocab.IEvalTarget)
     */
    @Override
    public boolean isAlterable(IEvalTarget node) {
        if (delegationTarget == null) return false;
        return delegationTarget.getTerms().isAlterable(delegationTarget);
    }

}
