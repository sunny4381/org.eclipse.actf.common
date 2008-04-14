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

class Eclipse_ACTF_Marker {
    private static var markers = new Array();
    var movieClip:MovieClip;
    var idx:Number;
    var x, y, w, h:Number;
    var txt:TextField;

    private function nextMarkerIdx() {
        for (var i = 0; i < markers.length; i++) {
            if (!markers[i]) return i;
        }
        return markers.length;
    }

    private function Eclipse_ACTF_Marker(idx:Number) {
        var baseMC:MovieClip = eval("_level0");
        this.idx = this.nextMarkerIdx();
        var nextDepth:Number = baseMC.getNextHighestDepth();
        // For AS version 2 component
        if (nextDepth >= 1048676) {
            nextDepth -= 1000 + this.idx;
        }
        this.movieClip = baseMC.createEmptyMovieClip("__Eclipse_ACTF_marker-" + this.idx, nextDepth);
        markers[this.idx] = this;
        this.movieClip._alpha = 100;
        this.movieClip._visible = false;

        this.txt = this.movieClip.createTextField("txt", 1, 0, 0, 10, 10);
    }

    private function setText(text:String) {
        var dpp = 4.0 * 96 / 72;
        var h = this.h / 2;
        this.txt._x = 0;
        this.txt._y = 0;
        this.txt._width = this.w;
        this.txt._height = h;
        this.txt.multiline = true;
        this.txt.autoSize = "right";
        var fmt:TextFormat = new TextFormat();
        fmt.font = "Times New Roman";
        var size = h / dpp + 7;
        if (size > 20) size = 20;
        fmt.size = size;
        fmt.color = 0x0010FF;
        this.txt.text = text;
        this.txt.setTextFormat(0, text.length, fmt);
    }

    private static function getMarker(idx:Number) {
        return markers[idx];
    }

    private function set(x:Number, y:Number,
                         w:Number, h:Number) {
        var lw = 4;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.movieClip.clear();
        this.movieClip._x = x;
        this.movieClip._y = y;
        this.movieClip._w = w;
        this.movieClip._h = h;
        this.movieClip.lineStyle(lw, 0x00FF00, 100);
        this.movieClip.moveTo(0, 0);
        this.movieClip.lineTo(w, 0);
        this.movieClip.lineTo(w, h);
        this.movieClip.lineTo(0, h);
        this.movieClip.lineTo(0, 0);
        this.movieClip._visible = true;
    }

    private function unset(x:Number, y:Number,
                         w:Number, h:Number) {
        this.movieClip._visible = false;
    }

    public static function setMarker(idx:Number,
                                     x:Number, y:Number,
                                     w:Number, h:Number) {
        var mk:Eclipse_ACTF_Marker = Eclipse_ACTF_Marker.getMarker(idx);
        mk.set(x, y, w, h);
    }

    public static function unsetMarker(idx:Number) {
        var mk:Eclipse_ACTF_Marker = Eclipse_ACTF_Marker.getMarker(idx);
        mk.unset();
    }

    public static function newMarker():Number {
        var mk:Eclipse_ACTF_Marker = new Eclipse_ACTF_Marker();
        return mk.idx;
    }

    private static var builtMarkers:Array = new Array();

    private static function showMarkers(a:Array) {
        for (var i = 0; i < a.length; i++) {
            var mk:Eclipse_ACTF_Marker = new Eclipse_ACTF_Marker();
            builtMarkers.push(mk.idx);
            var c:Eclipse_ACTF_Node = a[i];
            mk.set(c.x, c.y, c.w, c.h);
            mk.setText(c.target + "|" + c.currentFrame);
            var a2:Array = Eclipse_ACTF_Node.getInnerNodes(c.target);
            Eclipse_ACTF_Marker.showMarkers(a2);
        }
    }

    public static function showAllMarkers(path:String) {
        var a:Array = Eclipse_ACTF_Node.getInnerNodes(path);
        Eclipse_ACTF_Marker.showMarkers(a);
    }

    private function remove() {
        this.movieClip.clear();
        this.txt.text = "";
        this.movieClip.removeMovieClip();
    }

    public static function clearAllMarkers() {
        for (var i = 0; i < builtMarkers.length; i++) {
            var j = builtMarkers[i];
            markers[j].remove();
            markers[j] = undefined;
        }
    }

}
