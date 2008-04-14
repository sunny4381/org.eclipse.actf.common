/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shin SAITO, Hisashi MIYASHITA - initial API and implementation
 *******************************************************************************/
// AS Serializer/Deserialization library

class Eclipse_ACTF_ASSerializer {
    private static var ignorePropertiesWithUnderScores:Boolean = true;

    public static function serialize(o:Object):String {
        function serializeArray(o: Array) {
	    var ret:String = "[";
	    for(var i = 0; i < o.length; i++){
		ret += serializeInternal(o[i]);
                ret += ",";
	    }
            if (ret.length > 1) {
                ret = ret.substring(0, ret.length - 1);
            }
	    ret += "]";
	    return ret;
        }

        function serializeString(o: String) {
	    var rest:String = _global.encodeStringForBridge(o);
	    var ret:String = '"';
	    var index;
	    while ((index = rest.indexOf('"')) >= 0) {
		ret += rest.substring(0,index);
		ret += "\\\"";
		rest = rest.substr(index + 1);
	    }
	    ret+=rest + '"';
	    return ret;
        }

        function serializeObject(o: Object) {
	    var ret:String = '{';
	    for (var prop in o){
                if (o[prop] != null && o[prop] != undefined) {
                    if (!ignorePropertiesWithUnderScores || prop.substr(0,2) != '__') {
                        ret += serializeString(prop) + ":" ;
                        ret += serializeInternal(o[prop]);
                        ret += ",";
                    }
                }
	    }
            if (ret.length > 1) {
                ret = ret.substring(0, ret.length - 1);
            }
	    ret += "}";
	    return ret;
        }

        function serializeInternal(o: Object) {
            if (o instanceof Array) {
                return serializeArray(o);
            } 
            switch (typeof(o)) {
	    case "undefined":
		return "null";
            case "string":
                return serializeString(o);
            case "object":
                return serializeObject(o);
	    case "movieclip":
		return "null";
	    case "function":
		return "null";
                
            default:
                return String(o);
            }
        }
        return serializeInternal(o);
    }

    public static function deserialize(str:String):Object {
        var idx:Number = 0;
        var obj:Object = undefined;
        var c:String = str.charAt(idx);

        function skipSP():Boolean {
            for (;;) {
                c = str.charAt(idx);
                switch (c) {
                case ' ':
                case '\n':
                case '\r':
                case '\t':
                    idx++;
		    continue;
                case undefined:
                    return false;
                default:
                    return true;
                }
            }
        }

        function tokenEnd():Boolean {
            for (;;) {
                c = str.charAt(idx);
                switch (c) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                case ',':
                case ':':
                case ']':
                case '}':
                case undefined:
                    return true;
                }
                idx++;
            }
        }

        function deserializeArray() {
	    // trace("dA");
	    idx++;
            var ret:Array = new Array();
            for (;;) {
                skipSP();
                if (c == ']') {
                    obj = ret;
		    idx++;
		    // trace("dA-exit: " + obj);
                    return true;
                }
                if (deserializeInternal()) {
		    // trace("dA-internal: "+obj);
                    ret.push(obj);
		}
                skipSP();
                if (c == ']') {
                    obj = ret;
		    idx++;
		    // trace("dA-exit: " + obj);
                    return true;
                }
                if (c != ',') return false;
		idx++;
            }
        }

        function deserializeObject() {
	    idx++;
            var ret:Object = new Object();
            for (;;) {
                skipSP();
                if (c == '}') {
                    obj = ret;
		    idx++;
		    // trace("dA-exit: " + obj);
                    return true;
                }
                if (deserializeString()) {
		    var propName = obj;
		    // trace(propName);
		    skipSP();
		    if (c != ':') return false;
		    idx++;
		    skipSP();
		    if (deserializeInternal()) {
			var propVal:Object = obj;
			// trace(propVal);
			ret[propName] = propVal;
		    }
		}
                skipSP();
                if (c == '}') {
                    obj = ret;
		    idx++;
		    // trace("dA-exit: " + obj);
                    return true;
                }
                if (c != ',') return false;
		idx++;
            }
            return false;
        }

        function deserializeString() {
            idx++;
            var ret:String = "";
            for (;;) {
                c = str.charAt(idx);
                switch (c) {
                case '"':
                    idx++;
                    obj = ret;
                    return true;
                case '\\':
                    idx++;
                    c = str.charAt(idx);
                    if (!c) return false;
                case undefined:
                    return false;
                }
                ret += c;
                idx++;
            }
        }

        function deserializeInternal():Boolean {
            if (!skipSP()) return true;
            switch (c) {
            case '[':
                return deserializeArray();
            case '{':
                return deserializeObject();
            case '"':
                return deserializeString();
            default:
                var idxSt:Number = idx;
                if (tokenEnd()) {
		    var substr = str.substring(idxSt, idx);
		    if (('0'<=substr.charAt(0) && substr.charAt(0)<='9')
			|| substr.charAt(0)=='+' || substr.charAt(0)=='-')
			obj = Number(substr);
		    else
			obj = eval(substr);
		    // trace(obj);
                    return true;
                }
            }
            return false;
        }

        deserializeInternal();
        return obj;
    }
}
