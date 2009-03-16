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

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.actf.util.httpproxy.core.IHTTPHeader;

public abstract class HTTPHeader implements IHTTPHeader {
    public static byte toUpcase(byte b) {
        if ((b >= 'a') && (b <= 'z')) {
            return (byte) (b - ('a' - 'A'));
        }
        return b;
    }

    public static boolean compare(byte[] v1, byte[] v2) {
        int len = v1.length;
        if (len != v2.length) {
            return false;
        }
        for (int i = len - 1; i >= 0; i--) {
            if (v1[i] != v2[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean compareIgnoreCase(byte[] v1, byte[] v2) {
        int len = v1.length;
        if (len != v2.length) {
            return false;
        }
        for (int i = len - 1; i >= 0; i--) {
            if (HTTPHeader.toUpcase(v1[i]) != HTTPHeader.toUpcase(v2[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean isRemoved = false;
    
    protected HTTPHeader() {
    }
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#setRemoved(boolean)
	 */
    public void setRemoved(boolean removed) {
    	isRemoved = removed;
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#isRemoved()
	 */
    public boolean isRemoved() {
    	return isRemoved;
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#isFieldNameEqualsTo(byte[])
	 */
    public abstract boolean isFieldNameEqualsTo(byte[] name);

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#isFieldValueEqualsTo(byte[])
	 */
    public abstract boolean isFieldValueEqualsTo(byte[] value);

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#getName()
	 */
    public abstract byte[] getName();
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#getValue()
	 */
    public abstract byte[] getValue();
        
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#setValue(byte[])
	 */
    public abstract void setValue(byte[] value);
    
    protected abstract void write(OutputStream out) throws IOException;

    protected void writeHeaderSeparator(OutputStream out) throws IOException {
        out.write(':');
        out.write(HTTPReader.SP);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#compareValue(byte[])
	 */
    public boolean compareValue(byte[] value) {
        return compare(getValue(), value);
    }

    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#compareValueIgnoreCase(byte[])
	 */
    public boolean compareValueIgnoreCase(byte[] value) {
        return compareIgnoreCase(getValue(), value);
    }
    
    /* (non-Javadoc)
	 * @see org.eclipse.actf.util.httpproxy.core.IHTTPHeader#writeLine(java.io.OutputStream)
	 */
    public void writeLine(OutputStream out) throws IOException {
    	if (!isRemoved) {
            write(out);
            out.write(HTTPReader.CR);
            out.write(HTTPReader.LF);
    	}
    }

    public String toString() {
        return "Header:" + new String(getName()) + ":" + new String(getValue()); //$NON-NLS-1$ //$NON-NLS-2$
    }
}
