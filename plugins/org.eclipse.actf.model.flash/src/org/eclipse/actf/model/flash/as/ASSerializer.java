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

package org.eclipse.actf.model.flash.as;


public class ASSerializer {
    public static String serialize(String str) {
        if ((str.indexOf('"') >= 0) || (str.indexOf('\\') >= 0)) {
            StringBuffer ret = new StringBuffer('"');
            for (int i = 0; i < str.length(); i++) {
                char ch = str.charAt(i);
                switch (ch) {
                case '"':
                    ret.append("\\\"");
                    break;
                case '\\':
                    ret.append("\\\\");
                    break;
                default:
                    ret.append(ch);
                }
            }
            ret.append('"');
            return ret.toString();
        } else {
            return "\"" + str + "\"";
        }
    }

    private static String serialize(Object a) {
        if (a instanceof String) {
            return serialize((String) a);
        } else if (a instanceof Number) {
            return a.toString();
        } else if (a instanceof Boolean) {
            return a.toString();
        } else if (a instanceof Object[]) {
            return serialize((Object[]) a);
        }
        throw new IllegalArgumentException(a + " cannot be serialized.");
    }

    public static String serialize(String secret, Object[] args) {
        StringBuffer ret = new StringBuffer(secret);
        ret.append('[');
        if (args.length > 0) {
            ret.append(serialize(args[0]));
            for (int i = 1; i < args.length; i++) {
                ret.append(",");
                Object a = args[i];
                ret.append(serialize(a));
            }
        }
        ret.append("]");
        return ret.toString();
    }

}
