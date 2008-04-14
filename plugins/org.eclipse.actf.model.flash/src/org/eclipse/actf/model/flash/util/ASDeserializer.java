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

package org.eclipse.actf.model.flash.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class ASDeserializer {
    private static final Map<String, Object> tokenDic = new HashMap<String, Object>();
    private static final Object UNDEFINED = "undefined".intern();
    private static final Object NULL = "null".intern();

    static {
        tokenDic.put("true", Boolean.valueOf(true));
        tokenDic.put("false", Boolean.valueOf(false));
        tokenDic.put("undefined", UNDEFINED);
        tokenDic.put("null", NULL);
    }

    private int idx;
    private String str;

    private int skipSP() {
        while (idx < str.length()) {
            char ch = str.charAt(idx);
            switch (ch) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':
                idx++;
                break;
            default:
                return idx;
            }
        }
        return -1;


    }

    private int getTokenEndIdx() {
        int idx2 = idx;
        while (idx2 < str.length()) {
            char ch = str.charAt(idx2);
            switch (ch) {
            case ' ':
            case '\t':
            case '\n':
            case '\r':
            case ',':
            case ':':
            case '}':
            case ']':
                return idx2;
                
            default:
                idx2++;
            }
        }
        return idx2;
    }

    private String deserializeString() {
        StringBuffer ret = new StringBuffer();
        idx++;
        while (idx < str.length()) {
            char ch = str.charAt(idx);
            switch (ch) {
            case '"':
                idx++;
                return ret.toString();
            case '\\':
                idx++;
                if (idx == str.length()) {
                    throw new IllegalArgumentException("Abnormal end of the string:" + str);
                }
                ret.append(str.charAt(idx));
                idx++;
                break;
            default:
                ret.append(ch);
                idx++;
            }
        }
        throw new IllegalArgumentException("Invalid String:" + str);
    }

    private Object[] deserializeArray() {
        idx++;
        ArrayList<Object> ret = new ArrayList<Object>();
        while (idx < str.length()) {
            skipSP();
            char ch = str.charAt(idx);
            if (ch == ']') {
                idx++;
                return ret.toArray(new Object[ret.size()]);
            } else {
                ret.add(deserialize());
            }
            skipSP();
            ch = str.charAt(idx);
            if (ch == ']') {
                idx++;
                return ret.toArray(new Object[ret.size()]);
            }
            if (ch != ',') {
                throw new IllegalArgumentException("Missing ',':" + str);
            }
            idx++;
        }
        throw new IllegalArgumentException("Abnormal end of the array:" + str);
    }

    private ASObject deserializeASObject() {
        idx++;
        ASObject ret = new ASObject();
        while (idx < str.length()) {
            skipSP();
            char ch = str.charAt(idx);
            if (ch == '}') {
                idx++;
                return ret;
            } else {
                String prop = deserializeString();
                skipSP();
                if (str.charAt(idx) != ':') {
                    throw new IllegalArgumentException("Missing ':':" + str);
                }
                idx++;
                Object o = deserialize();
                ret.put(prop, o);
            }
            skipSP();
            ch = str.charAt(idx);
            if (ch == '}') {
                idx++;
                return ret;
            }
            if (str.charAt(idx) != ',') {
                throw new IllegalArgumentException("Missing ',':" + str);
            }
            idx++;
        }
        throw new IllegalArgumentException("Abnormal end of the array:" + str);
    }

    public Object deserialize() {
        int r = skipSP();
        if (r < 0) return null;
        char ch = str.charAt(idx);
        switch (ch) {
        case '[':
            return deserializeArray();
        case '{':
            return deserializeASObject();
        case '"':
            return deserializeString();
        default:
            int idx2 = getTokenEndIdx();
            String tok = str.substring(idx, idx2);
            Object o = tokenDic.get(tok);
            this.idx = idx2;
            if (o == NULL) {
                return null;
            }
            if (o != null) {
                return o;
            }
            try {
                int i = Integer.parseInt(tok);
                return Integer.valueOf(i);
            } catch (NumberFormatException e) {
            }
            try {
                double d = Double.parseDouble(tok);
                return new Double(d);
            } catch (NumberFormatException e) {
            }
            throw new IllegalArgumentException(tok + " is not a valid token.");
        }
        
    }

    public ASDeserializer(String str) {
        this.idx = 0;
        this.str = str;
    }
}
