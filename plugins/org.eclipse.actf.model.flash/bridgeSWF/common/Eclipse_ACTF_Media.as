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

import flash.external.ExternalInterface;

class Eclipse_ACTF_Media {
    private static function addObject(o:Object, target:String, a:Array) {
        var len:Number = a.length;
        for (var i:Number = 0; i < len; i++) {
            var o2:Object = eval(a[i].target);
            if (o === o2) return;
        }
        a.push(new Eclipse_ACTF_Node(null, target));
    }

    private static function searchVideoInternal(o:Object,
                                                target:String,
                                                traversed:Object,
                                                r:Array) {
        if (traversed[target]) return;
        traversed[target] = true;
        if (o instanceof NetStream) {
            addObject(o, target, r);
            return;
        }

        /* else if (o instanceof XMLNode) {
            return;
        } else if (Eclipse_ACTF_Util.isInClass(o, mx.core.UIObject)) {
            return;
            }*/
        for (var c in o) {
            var co:Object = o[c];
            var cTarget:String = target + "." + c;
            if (co._target) {
                cTarget = Eclipse_ACTF_Node.normalizeTargetName(co._target);
            } else {
                co._target = cTarget;
            }
            searchVideoInternal(co, cTarget, traversed, r);
        }
    }

    public static function searchVideo():Array {
        /*
        var traversed:Object = new Object();
        var r:Array = new Array();

        for (var i = 0; i < arguments.length; i++) {
            var path:String = arguments[i];
            var o:Object = eval(path);
            searchVideoInternal(o, path, traversed, r);
        }
        return r;
        */
        var r:Array = new Array();
        var streams:Array = _global.Eclipse_ACTF_Video.streams;
        for (var i = 0; i < streams.length; i++) {
            var slot:Object = streams[i];
            addObject(slot, "_global.Eclipse_ACTF_Video.streams." + i, r);
        }
        return r;
    }

    private static function searchSoundInternal(o:Object,
                                                target:String,
                                                traversed:Object,
                                                r:Array) {
        if (traversed[target]) return;
        traversed[target] = true;
        if (o instanceof Sound) {
            addObject(o, target, r);
            return;
        }
        for (var c in o) {
            var cTarget:String = target + "." + c;
            var co:Object = eval(cTarget);
            if (co._target) {
                cTarget = Eclipse_ACTF_Node.normalizeTargetName(co._target);
            } else {
                co._target = cTarget;
            }
            // ExternalInterface.call("alert", cTarget);
            searchSoundInternal(co, cTarget, traversed, r);
        }
    }

    public static function searchSound(path:String):Array {
        /* var o:Object = eval(path);
        var traversed:Object = new Object();
        var r:Array = new Array();
        searchSoundInternal(o, path, traversed, r);
        return r; */
        if (!_global.Eclipse_ACTF_FLASHBridgeGlobalSound) {
            _global.Eclipse_ACTF_FLASHBridgeGlobalSound = new Sound();
        }
        var r:Array = new Array();
        r.push(new Eclipse_ACTF_Node(null, "_global.Eclipse_ACTF_FLASHBridgeGlobalSound"));
        return r;
    }
    /*
    public static function getVolume(path:String):Number {
        var s:Sound = eval(path);
        return s.getVolume();
    }

    public static function setVolume(path:String, val:Number) {
        var s:Sound = eval(path);
        s.setVolume(val);
    }
    */
}
