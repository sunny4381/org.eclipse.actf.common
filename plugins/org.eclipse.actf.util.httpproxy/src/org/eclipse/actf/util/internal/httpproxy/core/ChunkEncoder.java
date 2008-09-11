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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.actf.util.httpproxy.util.Logger;


public class ChunkEncoder extends FilterOutputStream {
    static final Logger LOGGER = Logger.getLogger(ChunkEncoder.class);
    public static final int DEFAULT_CHUNK_SIZE = 1024; 
    
    private int fMaxChunkSize;
    private byte[] fChunkBuffer;
    private int fChunkSize;
    
    public ChunkEncoder(OutputStream out) {
        this(out, DEFAULT_CHUNK_SIZE);
    }
    
    public ChunkEncoder(OutputStream out, int maxChunkSize) {
        super(out);
        fMaxChunkSize = maxChunkSize;
        fChunkSize = 0;
        fChunkBuffer = new byte[maxChunkSize];
    }
    
    public void setMaxChunkSize(int maxChunkSize) throws IOException {
        if (fMaxChunkSize == maxChunkSize) {
            return;
        }
        if (fChunkSize > 0) {
            flushChunk();
        }
        fMaxChunkSize = maxChunkSize;
        fChunkBuffer = new byte[maxChunkSize];
    }
    
    public void write(int b) throws IOException {
        fChunkBuffer[fChunkSize++] = (byte) b;
        if (fChunkSize == fMaxChunkSize) {
            flushChunk();
        }
    }

    public void flush() throws IOException {
        flushChunk();
        out.flush();
    }
    
    public void close() throws IOException {
        flushChunk();
        out.write('0');
        out.write(HTTPReader.CR);
        out.write(HTTPReader.LF);
        out.write(HTTPReader.CR);
        out.write(HTTPReader.LF);
        out.close();
    }
    
    private void flushChunk() throws IOException {
        if (fChunkSize > 0) {
            byte[] size = Integer.toHexString(fChunkSize).getBytes();
            out.write(size);
            out.write(HTTPReader.CR);
            out.write(HTTPReader.LF);
            out.write(fChunkBuffer, 0, fChunkSize);
            out.write(HTTPReader.CR);
            out.write(HTTPReader.LF);
            fChunkSize = 0;
        }
    }
    
    public static void main(String[] args) throws Exception {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        ChunkEncoder encoder = new ChunkEncoder(buf, 10);
        final int size = 39; 
        for (int i = 0; i < size; i++) {
            int b = i & 0xff;
            encoder.write(b);
        }
        encoder.close();
        
        byte[] a = buf.toByteArray();
        for (int i = 0; i < a.length; i++) {
            int b = a[i];
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append(Integer.toString(i));
            sb.append("] ");
            //sb.append("0x").append(Integer.toString(b >> 8, 16)).append(Integer.toString(b & 0xf, 16));
            sb.append(Integer.toString(b));
            if (Character.isLetterOrDigit((char) b)) {
                sb.append(" '").append((char) b).append('\'');
            }
            String line = sb.toString();
            System.out.println(line);
        }
        
        System.out.println();
        ChunkDecoder decoder = new ChunkDecoder(new ByteArrayInputStream(a));
        for (int i = 0; true; i++) {
            int b = decoder.read();
            if (b < 0) {
                break;
            }
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append(Integer.toString(i));
            sb.append("] ");
            sb.append(Integer.toString(b));
            String line = sb.toString();
            System.out.println(line);
        }
    }
}
