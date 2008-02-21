/*******************************************************************************
 * Copyright (c) 2003, 2008 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kentarou FUKUDA - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.model.dom.html.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.actf.model.dom.html.HTMLParser;
import org.eclipse.actf.model.dom.html.impl.SHDocument;
import org.eclipse.actf.model.dom.html.internal.util.JapaneseEncodingDetector;
import org.eclipse.actf.model.dom.sgml.IErrorLogListener;
import org.eclipse.actf.model.dom.sgml.SGMLParser;


public class HtmlParserUtil {
	
	public static String DEFAULT_ENCODING = "MS932";

    ArrayList<IErrorLogListener> errorLogLisnterList = new ArrayList<IErrorLogListener>();

    SHDocument doc;

    String encoding;

    HTMLParser p1;

    public HtmlParserUtil() {
        doc = null;
        encoding = DEFAULT_ENCODING;
    }

    private HTMLParser setupHTMLParser() {
        HTMLParser p1 = new HTMLParser();
        p1.setTagCase(SGMLParser.LOWER_CASE);
        p1.setAttrNameCase(SGMLParser.LOWER_CASE);
        p1.setDefaultTagCase(SGMLParser.LOWER_CASE);
        p1.keepUnknownElements(true);


        // ErrorHandler[] ehs = p1.getErrorHandlers();
        //
        // for(int i=0;i<ehs.length;i++){
        // ErrorHandler eh = ehs[i];
        // //if(eh instanceof DefaultErrorHandler){
        // p1.removeErrorHandler(eh);
        // //}
        // }
        // ehs = p1.getErrorHandlers();
        //
        // for(int i=0;i<ehs.length;i++){
        // System.out.println(ehs[i].toString());
        // }

        p1.elementHandle(false); // to get line number
        for (Iterator i = errorLogLisnterList.iterator(); i.hasNext();) {
            p1.addErrorLogListener((IErrorLogListener) i.next());
        }
        p1.keepUnknownElements(true);
        return (p1);
    }

    public void addErrorLogListener(IErrorLogListener listener) {
        if (listener != null) {
            errorLogLisnterList.add(listener);
        }
    }

    public void parseWithoutEncoding(InputStream is) throws Exception {
        // parse
        p1 = setupHTMLParser();

        try {
            p1.parseSwitchEnc(is, "ISO-8859-1");
            doc = (SHDocument) p1.getDocument();
            encoding = p1.getEncoding();
            // System.out.println(encoding);
        } catch (Exception e2) {
            e2.printStackTrace();
            doc = null;
            encoding = DEFAULT_ENCODING;
        }
    }

    public void parse(InputStream is) throws Exception {

        encoding = DEFAULT_ENCODING;
        doc = null;

        JapaneseEncodingDetector JED = null;

        try {
            try {
                JED = new JapaneseEncodingDetector(is);
                encoding = JED.detect();
                // JED.detect2();
            } catch (Exception e2) {
                // e2.printStackTrace();
                throw (e2);
            }

            // System.out.println("Detect Encoding: "+encoding);

            // parse
            p1 = setupHTMLParser();

            try {
                p1.parse(JED.getInputStream(), encoding);
            } catch (Exception e2) {
                // System.out.println();
                System.err.println("HPU: Can't parse as " + encoding);
                p1 = setupHTMLParser();
                try {
                    p1.parse(JED.getInputStream(), "MS932");
                } catch (Exception e3) {
                    System.err.println("HPU: Can't parse as MS932");
                    p1 = setupHTMLParser();
                    p1.parseSwitchEnc(JED.getInputStream());
                }

            }
            doc = (SHDocument) p1.getDocument();
            encoding = p1.getEncoding();

        } catch (Exception e) {
            e.printStackTrace();
            throw (e);
        } finally {
            // System.out.println("close is");
            is.close();
        }
    }

    /**
     * @return
     */
    public SHDocument getSHDocument() {
        return doc;
    }

    /**
     * @return
     */
    public String getEncoding() {
        return encoding;
    }

    public static boolean saveHtmlDocumentAsUTF8(SHDocument result, String tmpFile, String targetFile) {
        return saveHtmlDocumentAsUTF8(result, new File(tmpFile), new File(targetFile));
    }

    public static boolean saveHtmlDocumentAsUTF8(SHDocument result, File tmpFile, File targetFile) {
        String tmpEncoding = "UTF8";
        try {
            FileOutputStream fos = new FileOutputStream(tmpFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, tmpEncoding); //$NON-NLS-1$
            PrintWriter pw = new PrintWriter(osw);

            result.printAsSGML(pw, true);
            fos.flush();
            fos.close();

            FileInputStream fis = new FileInputStream(tmpFile);
            BufferedReader tmpBR = new BufferedReader(new InputStreamReader(fis, tmpEncoding)); //$NON-NLS-1$
            fos = new FileOutputStream(targetFile);
            osw = new OutputStreamWriter(fos, tmpEncoding); //$NON-NLS-1$
            pw = new PrintWriter(osw);
            String tmpS;
            while ((tmpS = tmpBR.readLine()) != null) {
                // for MSN
                tmpS = tmpS.replaceAll("&#32;", " ");

                // Added on 2004/03/03 for MSN
                tmpS = tmpS.replaceAll("&#x20;", " ");

                // for amazon
                tmpS = tmpS.replaceAll("&#039;", "'").replaceAll("&#39;", "'");

                // for java script
                tmpS = tmpS.replaceAll("&#0091;", "[").replaceAll("&#0093;", "]");

                // for description
                tmpS = tmpS.replaceAll("&amp;lt;", "&lt;").replaceAll("&amp;gt;", "&gt;");

                pw.println(tmpS);
            }
            pw.flush();
            pw.close();
            fis.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                FileOutputStream fos = new FileOutputStream(targetFile);
                // OutputStreamWriter osw = new OutputStreamWriter(fos,
                // encoding);
                PrintWriter pw = new PrintWriter(fos);
                result.printAsSGML(pw, true);
                fos.flush();
                fos.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return false;
    }

    public static void main(String args[]) {
        HtmlParserUtil hpu = new HtmlParserUtil();
        hpu.addErrorLogListener(new IErrorLogListener() {
            public void errorLog(int errorCode, String msg) {
                System.out.println(errorCode + ": " + msg);
            }
        });

        String filename = "sample-map.html";
        if (args.length > 0) {
            filename = args[0];
        }

        try {
            hpu.parse(new FileInputStream(filename));
            saveHtmlDocumentAsUTF8(hpu.getSHDocument(), File.createTempFile("tmp", "html", new File("tmp")), new File(
                    "tmp.html"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}