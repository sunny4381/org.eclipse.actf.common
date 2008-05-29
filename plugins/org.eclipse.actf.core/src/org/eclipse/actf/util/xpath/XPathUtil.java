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

package org.eclipse.actf.util.xpath;

import java.util.Vector;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.actf.util.dom.EmptyNodeListImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class XPathUtil {
    static XPath xpath = XPathFactory.newInstance().newXPath();

    static public String xp_nodeClassVal(String nodeName, String attVal) {
        return nodeName + "[@class=\"" + attVal + "\"]";
    }

    static public String xp_nodeIDVal(String nodeName, String attVal) {
        return nodeName + "[@id=\"" + attVal + "\"]";
    }

    static public String xp_nodeIDValNeg(String nodeName, String attVal) {
        return nodeName + "[@id!=\"" + attVal + "\"]";
    }

    static public String xp_nodeListOr(String nl1, String nl2) {
        return nl1 + "|" + nl2;
    }

    static public String xp_nodeTagNameNeg(String tagName) {
        return "*" + "[local-name()!=\"" + tagName + "\"]";
    }

    static public String xp_predAttNotExist(String attName) {
        return "[count(@" + attName + ")=0]";
    }

    static public String nodeClassValOnly(String nodeName, String attVal) {
        return nodeName + "[@class=\"" + attVal + "\"][count(@id)=0]";
    }

    static public String nodeIDValNeg(String nodeName, String attVal) {
        return nodeName + "[@id!=\"" + attVal + "\"][count(@class)=0]";
    }

    static public String nodeIDValOnly(String nodeName, String attVal) {
        return nodeName + "[@id=\"" + attVal + "\"][count(@class)=0]";
    }

    static public String predAttExists(String attName) {
        return "[@" + attName + "]";
    }

    static public String predClassVal(String attVal) {
        return "[@class=\"" + attVal + "\"]";
    }

    static public String predClassValNeg(String attVal) {
        return "[@class!=\"" + attVal + "\"]";
    }

    static public String predIDVal(String attVal) {
        return "[@id=\"" + attVal + "\"]";
    }

    static public String predIDValNeg(String attVal) {
        return "[@id!=\"" + attVal + "\"]";
    }

    static public int evalXPathCount(Node context, String expression) {
        try {
            return ((Double) xpath.evaluate("count(" + expression + ")", context, XPathConstants.NUMBER)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    static public NodeList evalXPathNodeList(Node context, String expression) {
        try {
            return (NodeList)xpath.evaluate(expression, context, XPathConstants.NODESET);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return new EmptyNodeListImpl();
    }

    static public String evalXPathString(Node context, String expression) {

        try {
            return (String)xpath.evaluate("string(" + expression + ")", context, XPathConstants.STRING);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO retun null -> null check  or  throws TransformerException
        return "";
    }

    static public void showNodeList(NodeList nl, boolean flag) {
        for (int i = 0; i < nl.getLength(); i++) {
            if (flag)
                System.out.println(nl.item(i).toString());
        }
    }

    public static Vector<Node> NL2Vector(NodeList nl) {
        Vector<Node> tmpV = new Vector<Node>();
        for (int i = 0; i < nl.getLength(); i++) {
            tmpV.add(nl.item(i));
        }
        return tmpV;
    }

    static public String elementStr(String tagName, boolean isClass, String attVal) {
        return ("<" + tagName + " " + (isClass ? "class" : "id") + "=\"" + attVal + "\">");
    }

    public static String canonicalize(String targetS) {
        return (targetS.replaceAll("\\p{Cntrl}", "").replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">",
                "&gt;").replaceAll("\"", "&quot;").replaceAll("\'", "&apos;"));
    }

}
