/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.internal.httpproxy.core;

import org.eclipse.actf.util.httpproxy.core.IBufferRange;

public class BufferRange implements IBufferRange {
    private int fStart;
    private int fLength;
        
    public BufferRange() {
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.internal.httpproxy.core.IBufferRange#reset()
	 */
    public void reset() {
        fStart = 0;
        fLength = 0;
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.internal.httpproxy.core.IBufferRange#setStart(int)
	 */
    public void setStart(int start) {
        fStart = start;
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.internal.httpproxy.core.IBufferRange#setLength(int)
	 */
    public void setLength(int length) {
        fLength = length;
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.internal.httpproxy.core.IBufferRange#getStart()
	 */
    public int getStart() {
        return fStart;
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.internal.httpproxy.core.IBufferRange#getLength()
	 */
    public int getLength() {
        return fLength;
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.internal.httpproxy.core.IBufferRange#toString()
	 */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[').append(Integer.toString(fStart));
        sb.append(',').append(Integer.toString(fLength));
        sb.append(']');
        return sb.toString();
    }
}
