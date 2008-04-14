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
// boot_loader.as
var Eclipse_ACTF_DEBUG:Boolean = false;
var Eclipse_ACTF_RANDOM_STRING_1 = "";

if (Eclipse_ACTF_DEBUG) {
    var Eclipse_ACTF_lc:LocalConnection = new LocalConnection();
    var mess:String = "";
    mess = "SWF bootloader ver 1.00" + newline + "this._url = " + this._url + newline;
}

if (_level53553.Eclipse_ACTF_is_available) {
    if (Eclipse_ACTF_DEBUG) {
        mess += "this is child SWF." + newline;
    }
    for (prop in this) {
        if (prop != "getSameDomainURL" && prop != "mess" && prop != "Eclipse_ACTF_lc" && prop != "$version" &&
            prop != "Eclipse_ACTF_DEBUG") {
            if (Eclipse_ACTF_DEBUG) {
                mess += " :"+prop + "=" + this[prop] + newline;
            }
            _global.Eclipse_ACTF_PROPERTIES[prop] = this[prop];
            Object.prototype[prop] = this[prop];
        }
    }
    if (Eclipse_ACTF_DEBUG) {
        Eclipse_ACTF_Util.send(mess);
    }
    this.loadMovie(this._url, "POST");
} else {
    if (Eclipse_ACTF_DEBUG) {
        mess += "this is root SWF." + newline;
    }
    _global.Eclipse_ACTF_PROPERTIES = new Object();

    var getSameDomainURL = function(url:String, filename) {
        if (url.indexOf("http:")<0 && url.indexOf("https:")<0) {
            return filename;
        }
	var index = url.lastIndexOf("?");
	if (index >= 0){
	    url = url.substring(0,index);
	}
	index = url.lastIndexOf("/");
        if (url.charAt(index-1) == "/") {
            return url + "/" + filename; 
        } else {
            return url.substring(0, index+1)+filename;
        }
    }

    // --------------------------------------------------------------------------------
    _global.Eclipse_ACTF_Video = new Object();
    _global.Eclipse_ACTF_Video.streams = new Array();
    _global.Eclipse_ACTF_Video.register = function(target:Object, source:Object) {
        if ((!target) || (!source)) return;
        var ar:Array = _global.Eclipse_ACTF_Video.streams;
        var slot:Eclipse_ACTF_Stream;
        for (var i = 0; ; i++) {
            if (i == ar.length) {
                slot = new Eclipse_ACTF_Stream(target, source);
                ar.push(slot);
                break;
            }
            slot = ar[i];
            if (slot.equals(target)) {
                slot.update(source);
            }
        }
    }

    _global.Eclipse_ACTF_Video.attachVideo = Video.prototype.attachVideo;
    Video.prototype.attachVideo = function(source:Object) {
        // Eclipse_ACTF_Util.send("Video.attachVideo!!!");
        _global.Eclipse_ACTF_Video.register(this, source);
        _global.Eclipse_ACTF_Video.attachVideo.call(this, source);
    };

    // _global.Eclipse_ACTF_Video.soundp = new Object();
    // ASSetPropFlags(_global.Sound.prototype, "attachSound", 0, true);
    // _global.Eclipse_ACTF_Video.soundp.onLoad = Sound.prototype.onLoad;
    //Sound.prototype.attachSound = function() {
    //    Eclipse_ACTF_Util.send("Invoked: " + "attachSound");
    //    _global.Eclipse_ACTF_Video.register(this, this);
    //    return _global.Eclipse_ACTF_Video.soundp.attachSound.apply(this, arguments);
    //}
    // Eclipse_ACTF_Util.send("Init...");

    // --------------------------------------------------------------------------------

    for (prop in this) {
        if (prop != "getSameDomainURL"
            && prop != "mess"
            && prop != "Eclipse_ACTF_lc"
            && prop != "$version"
            && prop != "Eclipse_ACTF_DEBUG") {
            if (Eclipse_ACTF_DEBUG) {
                mess += " :"+prop + "=" + this[prop] + newline;
            }
            _global.Eclipse_ACTF_PROPERTIES[prop] = this[prop];
            Object.prototype[prop] = this[prop];
            ASSetPropFlags(Object.prototype, prop, 1);

            /*
              if (Object.prototype.setPropertyIsEnumerable) {
              Object.prototype.setPropertyIsEnumerable(prop, false);
              }
            */
        }
    }
    if (Eclipse_ACTF_DEBUG) {
        Eclipse_ACTF_lc.send("_testLC", "dump", mess);
    }

    // load the secret.

    var lv:LoadVars = new LoadVars();
    lv.onLoad = function(flag:Boolean) {
        if (flag) {
            _global.Eclipse_ACTF_SWF_CONTENT_ID = lv.contentid;
            _global.Eclipse_ACTF_SECRET = lv.secret;
            // load the original
            loadMovieNum(_root._url, 0, "POST");
   
            // load boot_bridge.swf
            loadMovieNum(getSameDomainURL(_root._url, "Eclipse-ACTF-SWF-Initialize-Bridge-Package-1082017309130187472.swf"), 53553);
        }
    };
    lv.load(getSameDomainURL(this._url, "http://localhost/Eclipse-ACTF-SWF-LoadVars-1082017309130187472.txt"));
}
