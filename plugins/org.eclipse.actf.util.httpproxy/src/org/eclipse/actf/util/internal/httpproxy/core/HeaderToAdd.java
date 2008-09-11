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


public final class HeaderToAdd extends HTTPHeader {
    private byte[] fName;
    private byte[] fValue;
	
    public HeaderToAdd() {
        fName = null;
        fValue = null;
    }
	
    public void init(byte[] name, byte[] value) {
        fName = name;
        fValue = value;
    }
	
    public void reset() {
        fName = null;
        fValue = null;
    }
	
    public boolean isFieldNameEqualsTo(byte[] name) {
        return compareIgnoreCase(name, fName);
    }
	
    public boolean isFieldValueEqualsTo(byte[] value) {
        return compare(value, fValue);
    }
    
    public byte[] getName() {
        return fName;
    }
	
    public byte[] getValue() {
        return fValue;
    }
	
    public void setValue(byte[] value) {
        fValue = value;
    }

    protected void write(OutputStream out) throws IOException {
        out.write(fName);
        writeHeaderSeparator(out);
        out.write(fValue);
    }
}
