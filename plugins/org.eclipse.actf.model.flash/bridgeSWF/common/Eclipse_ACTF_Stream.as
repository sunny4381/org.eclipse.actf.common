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


class Eclipse_ACTF_Stream {
    private var netStream:NetStream;
    private var sound:Sound;
    private var pos:Number;
    private var target:Object;

    public function equals(obj:Object):Boolean {
        return (obj === this.target);
    }

    public function stop() {
        if (this.netStream) {
            this.netStream.pause(true);
            this.netStream.seek(0);
        } else if (this.sound) {
            this.sound.stop();
            this.pos = 0;
        }
    }

    private static function searchPlayButtonInternal(o:Object,
                                                     target:String,
                                                     traversed:Object):Boolean {
        if (traversed[target]) return;
        traversed[target] = true;

        if (o instanceof XMLNode) {
            return false;
        }

        for (var c in o) {
            var co:Object = o[c];
            // Eclipse_ACTF_Util.send("..." + c + " " + c.toUpperCase().indexOf("PLAY"));
            if (c.toUpperCase().indexOf("PLAY") == 0) {
                if (co && co.onRelease) {
                    // Eclipse_ACTF_Util.send("Try to Push:" + co);
                    co.onRelease();
                    return true;
                }
            }
            var cTarget:String = target + "." + c;
            if (co._target) {
                cTarget = Eclipse_ACTF_Node.normalizeTargetName(co._target);
            } else {
                co._target = cTarget;
            }
            if (searchPlayButtonInternal(co, cTarget, traversed)) return true;
        }
        return false;
    }

    public static function searchPlayButton() {
        var traversed:Object = new Object();

        for (var i = 0; i < arguments.length; i++) {
            var path:String = arguments[i];
            var o:Object = eval(path);
            searchPlayButtonInternal(o, path, traversed);
        }
    }

    public function play() {
        if (this.netStream) {
            // Eclipse_ACTF_Util.send("bytesLoaded:" + this.netStream.bytesLoaded);
            if (this.netStream.bytesLoaded <= 1) {
                // Eclipse_ACTF_Util.send("Search button...");
                searchPlayButton("_level0");
            }
            this.netStream.pause(false);
        } else if (this.sound) {
            this.sound.start(this.pos / 1000);
        }
    }

    public function pause() {
        if (this.netStream) {
            this.netStream.pause(true);
        } else {
            this.pos = this.sound.duration;
            this.sound.stop();
        }
    }

    public function getCurrentState() {
        // TODO
    }

    public function getCurrentPosition():Number {
        if (this.netStream) {
            return this.netStream.time;
        } else if (sound) {
            return this.sound.duration;
        }
        return -1;
    }

    public function update(src) {
        if (src instanceof NetStream) {
            this.netStream = src;
        } else if (src instanceof Sound) {
            this.sound = src;
        }
    }

    public function Eclipse_ACTF_Stream(tgt, src) {
        this.target = tgt;
        this.update(src);
    }
}
