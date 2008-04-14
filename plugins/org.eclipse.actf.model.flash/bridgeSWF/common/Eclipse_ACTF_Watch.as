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

class Eclipse_ACTF_Watch {
    private static var baseObject:Object;
    private static var origOnEnterFrame:Function;
    private static var watchLists:Object = new Object();
    private static var callback:String;
    private var path:String;
    private var ranges:Array;
    private var exists:Boolean;
    private var currentFrame:Number;

    private function Eclipse_ACTF_Watch(path:String) {
        this.path = path;
        this.ranges = new Array();
        var obj = eval (path);
        if (obj) {
            this.exists = true;
            this.currentFrame = obj._currentFrame;
        } else {
            this.exists = false;
            this.currentFrame = -1;
        }
    }

    private static function isInFrame(fn:Number, r:Object) {
        return (fn >= r.min) && (fn < r.max);
    }

    private static function watchEntry() {
        // origOnEnterFrame.call(baseObject);
        for (var path in watchLists) {
            var w:Eclipse_ACTF_Watch = watchLists[path];
            var call = false;
            var obj = eval (w.path);
            if (!obj == w.exists) {
                w.exists = !w.exists;
                call = true;
            } else if (obj) {
                var fn = obj._currentFrame;
                var pfn = w.currentFrame;
                w.currentFrame = fn;
                for (var i = 0; i < w.ranges.length; i++) {
                    var r:Object = w.ranges[i];
                    var f = Eclipse_ACTF_Watch.isInFrame(fn, r);
                    var pf = Eclipse_ACTF_Watch.isInFrame(pfn, r);
                    if (f != pf) {
                        call = true;
                        break;
                    }
                }
            }
            if (call) {
                ExternalInterface.call(callback, w.path,
                                       w.exists, w.currentFrame);
            }
        }
    }

    public static function watchInit(cb:String) {
        callback = cb;
        baseObject = eval ("_level0");
        origOnEnterFrame = baseObject.onEnterFrame;
        baseObject.onEnterFrame = function() {
            Eclipse_ACTF_Watch.watchEntry();
        }
    }

    public static function watchObject(path:String,
                                       minFrame:Number,
                                       maxFrame:Number) {
        var w:Eclipse_ACTF_Watch = watchLists[path];
        if (!w) {
            w = new Eclipse_ACTF_Watch(path);
            watchLists[path] = w;
        }
        if ((minFrame >= 0) && (maxFrame >= 0)) {
            var r:Object = new Object();
            r.min = minFrame;
            r.max = maxFrame;
            w.ranges[w.ranges.length] = r;
        }
    }

    public static function removeWatchList(path:String) {
        watchLists[path] = undefined;
    }
}
