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
package org.eclipse.actf.util.httpproxy.core.impl;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class BifurcatedOutputStream extends FilterOutputStream {
    private OutputStream o2;
        
    public void close() throws IOException {
        super.close();
        o2.close();
    }
        
    public void flush() throws IOException {
        super.flush();
        o2.flush();
    }

    public void write(int b) throws IOException {
        super.write(b);
        o2.write(b);
    }
        
    public BifurcatedOutputStream(OutputStream o1, OutputStream o2) {
        super(o1);
        this.o2 = o2;
    }
}
	
