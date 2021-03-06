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

import org.eclipse.actf.util.vocab.AbstractTerms;
import org.eclipse.actf.util.vocab.IEvalTarget;
import org.eclipse.actf.util.win32.msaa.MSAA;



public class FlashMSAATerms extends AbstractTerms {
    private static FlashMSAATerms instance;

    public static FlashMSAATerms getInstance() {
        if (instance == null) {
            instance = new FlashMSAATerms();
        }
        return instance;
    }

    @Override
    public boolean isClickable(IEvalTarget target) {
        if (target instanceof FlashMSAANodeImpl) {
            FlashMSAANodeImpl node = (FlashMSAANodeImpl) target;

            int role = node.aObject.getAccRole();

            return role == MSAA.ROLE_SYSTEM_PUSHBUTTON || role == MSAA.ROLE_SYSTEM_RADIOBUTTON
                    || role == MSAA.ROLE_SYSTEM_CHECKBUTTON || role == MSAA.ROLE_SYSTEM_LINK;
        }
        return false;
    }

    @Override
    public boolean isEmbeddedObject(IEvalTarget target) {
        return false;
    }

    @Override
    public boolean isInputable(IEvalTarget target) {
        if (target instanceof FlashMSAANodeImpl) {
            FlashMSAANodeImpl node = (FlashMSAANodeImpl) target;
            
            int role = node.aObject.getAccRole();
            int state = node.aObject.getAccState();

            return role == MSAA.ROLE_SYSTEM_TEXT && state != MSAA.STATE_READONLY;
        }
        return false;
    }

    @Override
    public boolean isValidNode(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isVisibleNode(IEvalTarget target) {
        return true;
    }

    @Override
    public boolean isButton(IEvalTarget target) {
        if (target instanceof FlashMSAANodeImpl) {
            FlashMSAANodeImpl node = (FlashMSAANodeImpl) target;

            int role = node.aObject.getAccRole();
            
            return role == MSAA.ROLE_SYSTEM_PUSHBUTTON;
        }

        return false;
    }

    @Override
    public boolean isAlterable(IEvalTarget target) {
        if (!(target instanceof FlashMSAANodeImpl)) return false;
        if (!isClickable(target)) return false;
        FlashMSAANodeImpl mn = (FlashMSAANodeImpl) target;
        String id = mn.getID();
        if (id == null) return false;
        if (id.length() == 0) return false;
        return true;
    }
}
