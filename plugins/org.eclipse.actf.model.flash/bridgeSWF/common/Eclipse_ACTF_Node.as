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


class Eclipse_ACTF_Node {
    private var __object;

    var objectName;
    var type;
    var className;
    var text;
    var title;
    var target:String;
    var x:Number, y:Number, w:Number, h:Number;
    var value;
    var isOpaqueObject:Boolean;
    var isUIComponent:Boolean;
    var isInputable:Boolean;
    var depth;
    var currentFrame:Number;
    var tabIndex:Number;

    var accInfo:Eclipse_ACTF_AccInfo;

    /*
      public function Eclipse_ACTF_Node(iName, tn, cn, txt, c) {
      this.instanceName = iName;
      this.type = tn;
      this.className = cn;
      this.text = txt;
      this.children = c;
      trace("Node created.");
      }
    */

    // 
    var swfVersion:String;
    var url:String;
    var mName:String;
    var mTarget:String;
    var parentTarget:String;
    var targetModified:Boolean;
    var targetIsString:Boolean;

    public function toString():String {
        var ret;
        ret = this.objectName + " - (type:" + this.type + ") " + (this.value ? "(value:" + this.value + ")" : "") + " " + (target ? "(target:" + this.target + ")" : "") + " ";
        ret += (this.className ? "(cName:" + this.className + ")" : "") + " " + (this.text ? "(text:" + this.text + ")" : "");
        return ret;
    }

    private function uiObjectCheck():Boolean {
        if (!(Eclipse_ACTF_Util.isInClass(this.__object, mx.core.UIObject))) return false;
        if (Eclipse_ACTF_Util.isInClass(this.__object, mx.core.UIComponent)) {
            if (Eclipse_ACTF_Util.classJudge(this.__object, mx.containers.Accordion)) {
                this.className = "mx.containers.Accordion";
                this.isUIComponent = true;
            } else if (Eclipse_ACTF_Util.isInClass(this.__object, mx.containers.Window)) {
                this.isUIComponent = true;
                if (this.__object.title) {
                    this.text = this.__object.title;
                }
                if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.Alert)) {
                    this.className = "mx.containers.Alart";
                } else {
                    this.className = "mx.containers.Window";
                }
            } else if (Eclipse_ACTF_Util.isInClass(this.__object, mx.controls.List)) {
                this.isUIComponent = true;
                this.isInputable = true;
                if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.DataGrid)) {
                    this.className = "mx.controls.DataGrid";
                } else {
                    this.className = "mx.controls.List";
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.Button)) {
                this.isUIComponent = true;
                this.isOpaqueObject = true;
                this.className = "mx.controls.Button";
                // selected, toggle
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.CheckBox)) {
                this.isUIComponent = true;
                this.isInputable = true;
                this.isOpaqueObject = true;
                this.className = "mx.controls.CheckBox";
                if (this.__object.label) {
                    this.text = this.__object.label;
                }
                // selected
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.ComboBox)) {
                this.isInputable = true;
                this.isUIComponent = true;
                this.isOpaqueObject = true;
                this.className = "mx.controls.ComboBox";
                if (this.__object.text) {
                    this.text = this.__object.text;
                }
                // value, selectedItem, selectedIndex, ...
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.DateField)) {
                this.isUIComponent = true;
                this.isInputable = true;
                this.isOpaqueObject = true;
                this.className = "mx.controls.DateField";
                // TODO...
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.DateChooser)) {
                this.isUIComponent = true;
                this.isInputable = true;
                this.isOpaqueObject = true;
                this.className = "mx.controls.DateChooser";
                // TODO...
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.Label)) {
                this.className = "mx.controls.Label";
                this.isUIComponent = true;
                this.isOpaqueObject = true;
                if (this.__object.text) {
                    this.text = this.__object.text;
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.Loader)) {
                // View
                this.isOpaqueObject = true;
                this.className = "mx.controls.Loader";
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.Menu)) {
                this.className = "mx.controls.Menu";
                this.isUIComponent = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.MenuBar)) {
                this.className = "mx.controls.MenuBar";
                this.isUIComponent = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.NumericStepper)) {
                this.className = "mx.controls.NumericStepper";
                this.isUIComponent = true;
                this.isInputable = true;
                if (this.__object.value) {
                    this.text = String(this.__object.value);
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.RadioButton)) {
                this.className = "mx.controls.RadioButton";
                this.isUIComponent = true;
                this.isOpaqueObject = true;
                this.isInputable = true;
                if (this.__object.label) {
                    this.text = this.__object.groupName + " " + this.__object.label;
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.containers.ScrollPane)) {
                this.className = "mx.containers.ScrollPane";
                this.isUIComponent = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.TextArea)) {
                this.className = "mx.controls.TextArea";
                this.isOpaqueObject = true;
                this.isUIComponent = true;
                this.isInputable = true;
                if (this.__object.text) {
                    this.text = this.__object.text;
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.TextInput)) {
                this.className = "mx.controls.TextInput";
                this.isOpaqueObject = true;
                this.isUIComponent = true;
                this.isInputable = true;
                if (this.__object.text) {
                    this.text = this.__object.text;
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.Tree)) {
                this.className = "mx.controls.Tree";
                this.isUIComponent = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.UIScrollBar)) {
                this.className = "mx.controls.UIScrollBar";
                this.isUIComponent = true;
                if (this.__object.scrollPosition) {
                    this.text = this.__object.scrollPosition;
                }
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.managers.FocusManager)) {
                this.className = "mx.managers.FocusManager";
                this.isOpaqueObject = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.MediaController)) {
                this.className = "mx.controls.MediaController";
                this.isUIComponent = true;
                this.isOpaqueObject = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.MediaDisplay)) {
                this.className = "mx.controls.MediaDisplay";
                this.isUIComponent = true;
                this.isOpaqueObject = true;
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.MediaPlayback)) {
                this.className = "mx.controls.MediaPlayback";
                this.isUIComponent = true;
                this.isOpaqueObject = true;
            } else {
                this.className = "mx.core.UIComponent";
            }
        } else {
            if (Eclipse_ACTF_Util.classJudge(this.__object, mx.controls.ProgressBar)) {
                this.isOpaqueObject = true;
                this.className = "mx.controls.ProgressBar";
                this.isUIComponent = true;
                if (this.__object.label) {
                    this.text = this.__object.label;
                }
            } else {
                this.className = "mx.core.UIObject";
            }
        }
        return true;
    }

    public function Eclipse_ACTF_Node(parent, objectStr) {
        // dtrace("  ctor:");
        this.objectName = objectStr;
        // dtrace("   path = " + ((parent != null && parent != undefined) ? parent : "**") + ":" + objectStr);
        if (parent != null && parent != undefined) {
            // trace(parent + ":" + objectStr);
            var parentO = eval(parent);
            this.__object = parentO[objectStr];
            this.target = parent + "." + objectStr;
        } else {
            // trace(this.__object);
            this.target = objectStr;
            this.__object = eval(objectStr);
        }
        this.type = typeof (this.__object);
        // dtrace("   type: " + this.type);

        this.accInfo = Eclipse_ACTF_AccInfo.newAccInfo(this.__object);
        if (this.__object.getSWFVersion) {
            this.swfVersion = this.__object.getSWFVersion(); // shin for debug
        }
        this.url = this.__object._url; // shin for debug
        this.w = this.__object._width;
        this.h = this.__object._height;
        if (this.__object.getRect) {
            var bobj:Object = this.__object.getRect();
            this.x = bobj.xMin;
            this.y = bobj.yMin;
        } else {
            /*
            // In this case, the object is put in its parent MC.
            // The origin is located at the center of the object.
            this.x = object._x - this.w / 2;
            this.y = object._y - this.h / 2;
            */
            this.x = this.__object._x;
            this.y = this.__object._y;
        }
        if (this.__object.localToGlobal) {
            this.__object.localToGlobal(this);
        } else if (this.__object._parent.localToGlobal) {
            this.__object._parent.localToGlobal(this);
        }
        if (this.type == "movieclip") {
            var targetPath:String = targetPath(this.__object);
            var rObj:Object = eval(targetPath);
            if (typeof(rObj) == "string") {
                this.targetIsString = true;
            }
            if (eval(rObj) === this.__object) {
                this.targetModified = true;
                this.target = targetPath;
            }
            // dtrace("   target: " + this.target);
            this.mName = this.__object._name;
            this.mTarget = this.__object._target;
            if (this.__object._parent) {
                this.parentTarget = this.__object._parent._target;
            }
            this.currentFrame = this.__object._currentframe;
            this.depth = this.__object.getDepth();
            if (!uiObjectCheck()) {
                if (Eclipse_ACTF_Util.classJudge(this.__object, mx.video.FLVPlayback)) {
                    this.className = "mx.video.FLVPlayback";
                    this.isUIComponent = true;
                    this.isOpaqueObject = true;
                } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.video.VideoPlayer)) {
                    this.isUIComponent = true;
                    this.className = "mx.video.VideoPlayer";
                    this.isOpaqueObject = true;
                }
            }

            if (this.text == null || this.text == undefined || this.text == "") {
                // dtrace("   obtaining text from snapshot...");
                var ts:TextSnapshot = this.__object.getTextSnapshot();
                if (ts) {
                    var len:Number = ts.getCount();
                    var text = ts.getText(0, len, false);
                    // dtrace("   obtained text is [[" + text + "]]");
                    if (text != null && text != undefined && text != "") 
                        this.text = text;
                }
            }
        } else if (this.type == "object") {
            if (this.__object instanceof TextField) {
                this.className = "TextField";
                this.target = targetPath(this.__object);
                if (this.__object.type == "input") {
                    this.isInputable = true;
                }
                if ((this.__object.__showContents != false) && this.__object.text != null && 
                    this.__object.text != undefined && this.__object.text != "") {
                    this.text = this.__object.text;
                }
                this.isOpaqueObject = true;
                // trace(" Text:" + this.text);
                // if (this.__object[o].__showContents != false) {
                // _level0.area_txt.text += " [" + this.__object[o].text + "]";
            } else if (this.__object instanceof Button) {
                this.className = "Button";
                this.target = targetPath(this.__object);
                // if (this.__object[o].__showContents != false) {
                // _level0.area_txt.text += " [" + this.__object[o].text + "]";
                this.isOpaqueObject = true;
            } else if (this.__object instanceof XML) {
                this.className = "XML";
                this.isOpaqueObject = true;
            } else if (this.__object instanceof XMLNode) {
                this.className = "XMLNode";
                this.isOpaqueObject = true;
            } else if (this.__object instanceof Array) {
                this.className = "Array";
                this.isOpaqueObject = true;
            } else if (this.__object instanceof Sound) {
                this.className = "Sound";
                this.isOpaqueObject = true;
            } else if (this.__object instanceof Video) {
                this.className = "Video";
                this.isOpaqueObject = true;
                this.target = targetPath(this.__object);
            } else if (Eclipse_ACTF_Util.classJudge(this.__object, mx.managers.DepthManager)) {
                this.className = "mx.managers.DepthManager";
                this.isOpaqueObject = true;
            } else {
                this.className = "??? Unknown";
            }
            // dtrace("  class name: " + this.className);
            this.depth = this.__object.getDepth();
        } else {
            this.value = String(eval(this.target));
        }
        this.tabIndex = this.__object.tabIndex;
    }

    // --------------------------------------------------------------------------------
    // SuccessorNodes. (In graph theory, we can directly reach succeros nodes from "this" node)
    // --------------------------------------------------------------------------------

    public function getNumberOfSuccessorNodes():Number {
        return this.getSuccessorNodesArray().length;
    }
    public function getSuccessorNodesArray():Array {
        // dtrace("Node::getSuccessorNodesArray()");
        var successors:Array = new Array();
        if (this.type != "movieclip" && this.type != "object") {
            return successors;
        } else {
            for (var child in eval(this.target)) {
                var cNode:Eclipse_ACTF_Node = new Eclipse_ACTF_Node(this.target, child);
                successors.push(cNode);
            }
            return successors;
        }
    }

    public static function getNumSuccessorNodes(path:String):Number {
        // dtrace("static Node::getNumSuccessorNodes()");
        var node:Eclipse_ACTF_Node = Eclipse_ACTF_Node.getNodeFromPath(path);
        if (!node) {
            return undefined;
        }
        return node.getNumberOfSuccessorNodes();
    }

    public static function getSuccessorNodes(path:String):Array {
        var node:Eclipse_ACTF_Node = Eclipse_ACTF_Node.getNodeFromPath(path);
        if (!node) {
            return undefined;
        }
        return node.getSuccessorNodesArray();
    }
    // --------------------------------------------------------------------------------
    // ChildNodes. (In tree theory, the child nodes has "this" parent node)
    // --------------------------------------------------------------------------------
    public function getNumberOfChildNodes():Number {
        return this.getChildNodesArray().length;
    }
    public function getChildNodesArray():Array {
        // dtrace("Node::getChildNodesArray()");
        var children:Array = new Array();
        var objNames:Object = new Object();

        var tp:String = targetPath(this.__object);
        // dtrace(" tp = " + tp);
        if (tp == null || tp == undefined) {
            // dtrace(" ** tp is null. abort.");
            return children;
        }

        // copied from getSuccessorNodesArray(). needless?
        if (this.type != "movieclip" && this.type != "object") {
            // dtrace(" ** the entity is not movieclip nor object. abort.");
            return children;
        }

        // Q: why do we use both targetPath and this.target?
        var i:Number = 0;
        for (var child in eval(this.target)) {
            // dtrace(" index " + (i++) + ": " + child);
            var cNode:Eclipse_ACTF_Node = new Eclipse_ACTF_Node(this.target, child);
            var cNodeTarget:String = cNode.target;
            // dtrace("  child's target: " + cNodeTarget);
            var idx:Number = cNodeTarget.lastIndexOf(".");
            if (idx < 0) {
                // dtrace("  ** this child does not have '.'. continue.");
                continue;
            }

            if (tp != cNodeTarget.substring(0, idx)) {
                // dtrace("  ** this child is not real child. continue.");
                continue;
            }

            // Remove bridge-internal objects.
            if (cNodeTarget.indexOf("__Eclipse_ACTF_") >= 0) {
                // dtrace("  ** this child is bridge-internal entity. continue.");
                continue;
            }

	    var objName:String = cNodeTarget.substring(idx+1);
	    // Eclipse_ACTF_Util.send(objName);
	    if (objNames[objName] == 1){
		// Eclipse_ACTF_Util.send(" -- duplicated!");
                // dtrace("  ** duplicated child name. continue.");
		continue;
	    }
	    objNames[objName] = 1;
            children.push(cNode);
        }
        // dtrace("processed children.");
        return children;
    }

    public static function getNumChildNodes(path:String):Number {
        // dtrace("static Node::getNumChildNodes()");
        var node:Eclipse_ACTF_Node = Eclipse_ACTF_Node.getNodeFromPath(path);
        if (!node) {
            return undefined;
        }
        return node.getNumberOfChildNodes();
    }

    public static function getChildNodes(path:String):Array {
        var node:Eclipse_ACTF_Node = Eclipse_ACTF_Node.getNodeFromPath(path);
        if (!node) {
            return undefined;
        }
        return node.getChildNodesArray();
    }

    // --------------------------------------------------------------------------------
    // Ends here.
    // --------------------------------------------------------------------------------
    public static function getNodeFromPath(path:String):Eclipse_ACTF_Node {
        if (!eval(path)) {
            return undefined;
        }
        var node:Eclipse_ACTF_Node = new Eclipse_ACTF_Node(null, path);
        return node;
    }
    public static function getRootNode():Eclipse_ACTF_Node {
        var node:Eclipse_ACTF_Node = new Eclipse_ACTF_Node(null, "_level0");
        return node;
    }
    public static function isProperlyNested(x1:Number, y1:Number, w1:Number, h1:Number, x2:Number, y2:Number, w2:Number, h2:Number):Boolean {
        var l = Math.max(x1, x2);
        var r = Math.min(x1 + w1, x2 + w2);
        var t = Math.max(y1, y2);
        var b = Math.min(y1 + h1, y2 + h2);
        return (((r - l) > 0) && ((b - t) > 0));
    }
    public static function getNodeAtDepth(path:String, depth:Number):Eclipse_ACTF_Node {
        var b = eval(path);
        var o = b.getInstanceAtDepth(depth);
        if (!o) return undefined;
        var target = Eclipse_ACTF_Node.normalizeTargetName(o._target);
        return new Eclipse_ACTF_Node(null, target);
    }
    private static function cmpxy(a:Eclipse_ACTF_Node, b:Eclipse_ACTF_Node):Number {
        if (a.y > b.y) {
            return 1;
        }
        if (a.y < b.y) {
            return -1;
        }
        if (a.x > b.x) {
            return 1;
        }
        if (a.x < b.x) {
            return -1;
        }
        return 0;
    }
    public static function normalizeTargetName(target:String):String {
        if (target.charAt(0) != '/') {
            return target;
        }
        var ra:Array = target.split("/");
        var nt:String = "_level0";
        for (var i = 0; i < ra.length; i++) {
            if (ra[i].length > 0) {
                nt = nt + "." + ra[i];
            }
        }
        return nt;
    }
    public static function getInnerNodes(path:String, flag:Boolean, startBaseDepth:Boolean):Array {
        var array:Array = new Array();
        // trace("PATH:"+path);
        var b = eval(path);
        var o;
        // trace("TY:" + typeof(b));
        if (b && b.getNextHighestDepth) {
            var ds:Number, de:Number, dc:Number;
            de = b.getNextHighestDepth();
            if (de > 1000) {
                de = 1000;
            }
            // trace("IDS:" + b.getDepth() +":"+b._x+":"+b._y+":"+b._width+":" +b._height); 
            // trace("IDE:" + de);
            dc = b.getDepth();
            if (startBaseDepth) {
                ds = dc + 1;
            } else {
                ds = -16383;
                // -16384(_level0.getDepth() + 1)
            }
            for (; ds < de; ds++) {
                // if (ds == dc) continue;
                o = b.getInstanceAtDepth(ds);
                if (o._target == b._target) {
                    continue;
                }
                if (o && o._visible) {
                    /*
                      if (!b._x
                      || !o._x
                      || Eclipse_ACTF_Node.isProperlyNested(b._x, b._y, b._width, b._height,
                      o._x, o._y, o._width, o._height)) {
                      trace("ID:" + ds + ":" + o._target);
                      array.push(new Eclipse_ACTF_Node(null, o._target));
                      }
                    */
                    var target = Eclipse_ACTF_Node.normalizeTargetName(o._target);
                    var o2 = eval(target);
                    if (flag || (ds == 0) || (o2.getDepth() == ds)) {
                        array.push(new Eclipse_ACTF_Node(null, target));
                    }
                }
            }
        }
        // trace("LEN:"+array.length); 
        array.sort(Eclipse_ACTF_Node.cmpxy);
        return array;
    }

    public static function setFocus(path:String):Boolean {
        var o = eval(path);
        return Selection.setFocus(o);
    }

    private static function dtrace(mes:String) {
        // if (debug) {
        if (true) {
            trace(mes);
        }
    }
}
