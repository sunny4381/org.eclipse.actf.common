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


class Eclipse_ACTF_Util {
    public static function classJudge(o:Object, cls:Function):Boolean {
        return (o.__proto__ == cls.prototype);
    }

    public static function isInClass(o:Object, cls:Function):Boolean {
        o = o.__proto__;
        var clsProto = cls.prototype;
        while (o) {
            if (o == clsProto) return true;
            o = o.__proto__;
        }
        return false;
    }

    public static function send(mes:String) {
        // new LocalConnection().send("_testLC", "dump", mes);
        var lc:LocalConnection = new LocalConnection();
	lc.send("_testLC", "dump", mes);
	// lc.close();
    }
    
}
