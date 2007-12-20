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

package org.eclipse.actf.util.httpproxy.proxy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

import org.eclipse.actf.util.httpproxy.Config;
import org.eclipse.actf.util.httpproxy.core.HTTPHeader;
import org.eclipse.actf.util.httpproxy.core.HTTPRequestMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseInMemoryMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponseMessage;
import org.eclipse.actf.util.httpproxy.core.HTTPResponsePushbackMessage;
import org.eclipse.actf.util.httpproxy.core.PushbackMessageBody;
import org.eclipse.actf.util.httpproxy.swftranscoder.ISwfTranscoder;
import org.eclipse.actf.util.httpproxy.swftranscoder.ISwfTranscoderFactory;
import org.eclipse.actf.util.httpproxy.util.Logger;
import org.eclipse.actf.util.httpproxy.util.TimeoutException;




public class SWFTranscoder extends HTTPProxyTranscoder {
    private static final Logger LOGGER = Logger.getLogger(SWFTranscoder.class);

    private static ISwfTranscoderFactory getSwfTranscoderFactory() {
        try {
            // TODO: maybe we should rename the factory package.
            Class clazz = Class.forName("org.eclipse.actf.util.swftranscoder.SwfTranscoderFactory");
            return (ISwfTranscoderFactory) clazz.newInstance();
        } catch (Exception e) {
        }
        return null;
    }

    private static final ISwfTranscoderFactory swfTranscoderFactory = getSwfTranscoderFactory();

    private final ISwfTranscoder swfTranscoder;

    private SWFTranscoder(int id) {
        this.swfTranscoder = swfTranscoderFactory.newInstance(id);
    }

    public static SWFTranscoder newInstance(int id) {
        if (swfTranscoderFactory == null) return null;
        return new SWFTranscoder(id);
    }

    private static Object imposedSWF;

    public static void setSWFTranscodingImposedFile(InputStream is) {
        if (swfTranscoderFactory == null) return;
        ISwfTranscoder st = swfTranscoderFactory.newInstance(0);
        try {
            imposedSWF = st.parse(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getTemporaryDestSWF(File src) throws IOException {
        return new File(src.getAbsolutePath() + ".out");
    }

    private File getTemporarySrcSWF() throws IOException {
        return File.createTempFile("SWFTC-", ".swf");
    }

    private boolean isTranscodeTarget(int id, HTTPRequestMessage request,
                                      HTTPResponsePushbackMessage response) {
        if (request.getHeader(SWFUtil.X_FLASH_VERSION_A) == null) return false;
        if (!SWFUtil.isPossiblySWFContentType(response)) return false;

        PushbackMessageBody body = (PushbackMessageBody) response.getMessageBody();
        if (body == null) return false;
        PushbackInputStream bodyInputStream = body.getMessageBodyPushBackInputStream();
        int version = SWFUtil.isSWF(bodyInputStream);
        //
        if (version >= Config.getInstance().getSWFTranscodingMinimumVersion()) {
            LOGGER.info("[id:" + id + "] SWTTranscoder: SWF to be transcoded ("
                        + request.getOriginalRequestURIString()
                        + ")- Version:" + version);
            return true;
        } else {
            if (version > 0)
                LOGGER.info("[id:" + id + "] SWTTranscoder: SWF not to be transcoded ("
                        + request.getOriginalRequestURIString() + ")- Version:" + version);
            return false;
        }
    }

    private void outputFailureLog(StringBuffer buf, HTTPRequestMessage request, File outFile, Throwable e) {
        buf.append("(");
        buf.append(request.getOriginalRequestURIString());
        buf.append(")\n");
        buf.append(ThrowableUtil.stackTraceToString(e));
        if (LOGGER.isDebugEnabled() && (outFile != null)) {
            buf.append("The SWF file is secured in ");
            buf.append(outFile.getAbsolutePath());
            buf.append("\n");
        }
        LOGGER.fatal(buf.toString());
    }

    private File saveSWFFile(File f, byte[] target) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(f);
            os.write(target);
            return f;
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    public HTTPResponseMessage transcode(int id,
                                         HTTPRequestMessage request,
                                         HTTPResponseMessage response) {
        if (!Config.getInstance().getSWFTranscodingFlag())
            return response;
        if (imposedSWF == null) return response;
        HTTPResponsePushbackMessage newResponse
            = new HTTPResponsePushbackMessage(response,
                                              SWFUtil.FLASH_MAGIC_NUMBER_SIZE);

        if (!isTranscodeTarget(id, request, newResponse)) return newResponse;

        byte[] transcoded;
        File srcSWFFile = null;
        File destSWFFile = null;
        try {
            srcSWFFile = getTemporarySrcSWF();
            byte[] targetBytes;
            try {
                targetBytes = newResponse.readBody(0, false);
            } catch (TimeoutException e) {
                LOGGER.fatal("[id:" + id + "] Message body cannot be read by timeout.");
                // This should be integrated with sendGatewayTimeout...
                return new HTTPResponseInMemoryMessage(request.getSerial(),
                                                       HTTPHeader.HTTP_VERSION_1_0_A,
                                                       "504".getBytes(),
                                                       "Gateway Timeout".getBytes(),
                                                       HTTPResponseInMemoryMessage.EMPTY_BODY);
            }
            if (targetBytes == null) return response;

            if (LOGGER.isDebugEnabled()) {
                saveSWFFile(srcSWFFile, targetBytes);
            }
            Object srcSwf = swfTranscoder.parse(new ByteArrayInputStream(targetBytes));
            Object destSwf = swfTranscoder.impose(imposedSWF, srcSwf);
            transcoded = swfTranscoder.generate(destSwf, false, 8);

            destSWFFile = getTemporaryDestSWF(srcSWFFile);
            if (LOGGER.isDebugEnabled()) {
                saveSWFFile(destSWFFile, transcoded);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        } catch (Exception e) {
            StringBuffer buf = new StringBuffer("SWF transcoding failed ");
            outputFailureLog(buf, request, srcSWFFile, e);
            return response;
        }

        HTTPResponseInMemoryMessage msg = new HTTPResponseInMemoryMessage(response, transcoded);
        return msg;
    }
}
