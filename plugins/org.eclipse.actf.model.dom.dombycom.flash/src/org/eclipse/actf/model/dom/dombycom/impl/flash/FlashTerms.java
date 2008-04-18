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

package org.eclipse.actf.model.dom.dombycom.impl.flash;

import org.eclipse.actf.model.dom.dombycom.impl.html.ElementImpl;
import org.eclipse.actf.model.dom.dombycom.impl.html.HTMLTerms;
import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.vocab.IEvalTarget;
import org.w3c.dom.Node;




public class FlashTerms extends AbstractTerms {
    @Override
    public boolean isAlterable(IEvalTarget target) {
        return isClickable(target);
    }

    private static FlashTerms instance;

    public static FlashTerms getInstance() {
        if (instance == null) {
            instance = new FlashTerms();
        }
        return instance;
    }

    @Override
    public boolean isValidNode(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isVisibleNode(IEvalTarget target) {
        if (target instanceof FlashNodeImpl) {
            FlashNodeImpl flash = (FlashNodeImpl) target;

            if (flash.accInfo != null) {
                Boolean b = (Boolean) flash.accInfo.get("silent");
                if (b != null)
                    return !b.booleanValue();
            }
        }
        return true;
    }

    @Override
    public boolean isInputable(IEvalTarget target) {
        if (target instanceof FlashNodeImpl) {
            FlashNodeImpl flash = (FlashNodeImpl) target;

            Boolean b = (Boolean) flash.nodeASObj.get("isInputable");
            if (b == null)
                return false;
            return b.booleanValue();
        }
        return false;
    }

    @Override
    public boolean isClickable(IEvalTarget target) {
        if (target instanceof FlashNodeImpl) {
            FlashNodeImpl flash = (FlashNodeImpl) target;

            String clickableTarget = flash.getClickableTarget(flash.getTarget());
            if (clickableTarget != null)
                return true;

        }
        return false;
    }

    @Override
    public boolean isButton(IEvalTarget target) {
        if (target instanceof FlashNodeImpl) {
            FlashNodeImpl flash = (FlashNodeImpl) target;

            String clickableTarget = flash.getClickableTarget(flash.getTarget());
            if (clickableTarget != null)
                return true;

        }
        return false;
    }

    @Override
    public boolean isFlashTopNode(IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return true;
        return false;
    }
    
    @Override
    public boolean isMSAAFlash(IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl) {
            return ((FlashTopNodeImpl) target).getMSAA() != null;
        }
        return false;
    }

    @Override
    public boolean isEmbeddedObject(IEvalTarget target) {
        if (target instanceof ElementImpl) {
            ElementImpl element = (ElementImpl) target;

            String name = element.getLocalName();
            if ("EMBED".equals(name) || "OBJECT".equals(name))
                return true;
        }
        return false;
    }

    @Override
    public boolean isAccessKey(char key, IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return HTMLTerms.getInstance().isAccessKey(key, target);
        return super.isAccessKey(key, target);
    }

    @Override
    public boolean isBlockJumpPointB(IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return HTMLTerms.getInstance().isBlockJumpPointB(target);
        return super.isBlockJumpPointB(target);
    }

    @Override
    public boolean isBlockJumpPointF(IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return HTMLTerms.getInstance().isBlockJumpPointF(target);
        return super.isBlockJumpPointF(target);
    }

    @Override
    public boolean isHeading(int level, IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return HTMLTerms.getInstance().isHeading(level, target);
        return super.isHeading(level, target);
    }

    @Override
    public boolean isHeadingJumpPoint(IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return HTMLTerms.getInstance().isHeadingJumpPoint(target);
        return super.isHeadingJumpPoint(target);
    }

    @Override
    public boolean nodeLocation(Node refNode, boolean backward, IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl)
            return HTMLTerms.getInstance().nodeLocation(refNode, backward, target);
        return super.nodeLocation(refNode, backward, target);
    }

    @Override
    public boolean isMedia(IEvalTarget target) {
        if (target instanceof FlashTopNodeImpl) {
            FlashTopNodeImpl swf = (FlashTopNodeImpl) target;
            return swf.hasMedia();
        } else {
            return super.isMedia(target);
        }
    }

    @Override
    public boolean hasContent(IEvalTarget target) {
        if (isFlashTopNode(target)) return true;
        if (isInputable(target)) return true;
        if (isListTop(target)) return true;
        return super.hasContent(target);
    }
}
