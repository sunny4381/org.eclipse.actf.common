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

import flash.filters.*;

class Eclipse_ACTF_Controller {
    public static function callMethod(path:String, methodName:String, args:Array) {
        var o = eval(path);
        var f = o[methodName];
        return f.apply(o, args);
    }

    public static function callMethodA(path:String, methodName:String) {
        var o:Object = eval(path);
        var f:Function = o[methodName];
        var a:Array = new Array();
        for (var i = 2; i < arguments.length; i++) {
            a.push(arguments[i]);
        }
        return f.apply(o, a);
    }

    public static function getProperty(path:String, prop:String):Object {
        var o:Object = eval(path);
        return o[prop];
    }

    public static function setProperty(path:String, prop:String, value:Object) {
        var o:Object = eval(path);
        o[prop] = value;
    }

    public static function addFilter(path:String) {
        var o = eval(path);
        var bfilter:BlurFilter = new BlurFilter(4, 4, 2);
        o.filters = [bfilter];
    }

    public static function removeFilter(path:String) {
        var o = eval(path);
        o.filters = [];
    }
}
