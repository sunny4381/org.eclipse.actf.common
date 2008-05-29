/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.comparator;


import java.util.Comparator;


public abstract class ChainComparator implements Comparator {
    private static final int DEFVAL = 0;

    private ChainComparator next;

    public ChainComparator setNext(ChainComparator next) {
        this.next = next;
        return next;
    }

    public final int intResolve(int i1, int i2) {
        Integer int1 = new Integer(i1);
        Integer int2 = new Integer(i2);
        return int1.compareTo(int2);
    }

    protected abstract int resolve(Object o1, Object o2);

    public int compare(Object o1, Object o2) {
        int res = resolve(o1, o2);
        if (res != 0)
            return res;
        else if (next == null)
            return DEFVAL;
        else
            return next.compare(o1, o2);
    }
}
