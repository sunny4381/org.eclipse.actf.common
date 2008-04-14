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

class Eclipse_ACTF_Init {
    public static var debug:Boolean = false;
    private static var debugTextField;

    public static var callbacks = {
        getRootNode : Eclipse_ACTF_Node.getRootNode,
        getNodeFromPath : Eclipse_ACTF_Node.getNodeFromPath,

        getNumSuccessorNodes : Eclipse_ACTF_Node.getNumSuccessorNodes,
        getSuccessorNodes : Eclipse_ACTF_Node.getSuccessorNodes,

        getNumChildNodes : Eclipse_ACTF_Node.getNumChildNodes,
        getChildNodes : Eclipse_ACTF_Node.getChildNodes,

        getNodeAtDepth : Eclipse_ACTF_Node.getNodeAtDepth,
        getInnerNodes : Eclipse_ACTF_Node.getInnerNodes,
	setFocus : Eclipse_ACTF_Node.setFocus,

        callMethod : Eclipse_ACTF_Controller.callMethod,
        callMethodA : Eclipse_ACTF_Controller.callMethodA,
        getProperty : Eclipse_ACTF_Controller.getProperty,
        setProperty : Eclipse_ACTF_Controller.setProperty,
        addFilter :  Eclipse_ACTF_Controller.addFilter,
        removeFilter : Eclipse_ACTF_Controller.removeFilter,

        searchVideo : Eclipse_ACTF_Media.searchVideo,
        searchSound : Eclipse_ACTF_Media.searchSound,

        watchInit :  Eclipse_ACTF_Watch.watchInit,
        watchObject : Eclipse_ACTF_Watch.watchObject,
        removeWatchList : Eclipse_ACTF_Watch.removeWatchList,

        newMarker : Eclipse_ACTF_Marker.newMarker,
        setMarker :  Eclipse_ACTF_Marker.setMarker,
        unsetMarker : Eclipse_ACTF_Marker.unsetMarker,
        showAllMarkers : Eclipse_ACTF_Marker.showAllMarkers,
        clearAllMarkers : Eclipse_ACTF_Marker.clearAllMarkers,

	getScaleMode : Eclipse_ACTF_StageProxy.getScaleMode,
	setScaleMode : Eclipse_ACTF_StageProxy.setScaleMode,
	getStageWidth : Eclipse_ACTF_StageProxy.getStageWidth,
	getStageHeight : Eclipse_ACTF_StageProxy.getStageHeight,
	getStageAlign : Eclipse_ACTF_StageProxy.getStageAlign,

        translate : Eclipse_ACTF_AutoTranslator.translate,
		
        //repairFlash : Eclipse_ACTF_AccRepair.repairFlash,

        updateTarget : Eclipse_ACTF_AccTarget.updateTarget
    };

    private static function searchAccInternal(o:Object,
                                              target:String,
                                              traversed:Object,
                                              r:Array) {
        if (traversed[target]) return;
        traversed[target] = true;
        for (var c in o) {
            if (c == "_accProps") {
                debugTextField.text += " ACCPROPS!";
                // Alert.show("Launch Stock Application?", "Stock Price Alert", Alert.OK | Alert.CANCEL, this, myClickHandler, "stockIcon", Alert.OK)
            }
            if (c == "_accImpl") {
                debugTextField.text += " ACCIMPL!";
            }
            var cTarget:String = target + "." + c;
            var co:Object = eval(cTarget);
            if (co._target) {
                cTarget = Eclipse_ACTF_Node.normalizeTargetName(co._target);
            } else {
                co._target = cTarget;
            }
            // ExternalInterface.call("alert", cTarget);
            searchAccInternal(co, cTarget, traversed, r);
        }
    }

    public static function searchAcc(path:String) {
        var o:Object = eval(path);
        var traversed:Object = new Object();
        var r:Array = new Array();
        searchAccInternal(o, path, traversed, r);
    }
    
    private static function addCallback(name:String, func:Function) {
	var result:Boolean = ExternalInterface.addCallback(name, null, func);
        if (debug) {
            // debugTextField.text += " " + name + " " + result + "; ";
        }
        return result;
    }

    public static function initialize() {
        // trace("Eclipse_ACTF_Init.initialize()");

        // var Eclipse_ACTF_is_available:Boolean = ExternalInterface.available;
        if (debug) {
          var lv0Obj = eval("_level0"); 
          debugTextField = lv0Obj.createTextField("debugText", 1, 0, 0, 10, 10);
          debugTextField._width = lv0Obj._width;
          debugTextField._height = lv0Obj._height;
          searchAcc("_level0");
          searchAcc("_level1");
          debugTextField.text += " DONE!!!!";
          //ExternalInterface.call("alert", "successfully executed init. AS");
        }

        System.security.allowDomain('*');   // shin

        /*
        for (var h in callbacks) {
            var res = addCallback(h, callbacks[h]);
            _level0.Eclipse_ACTF_reg_results.push(res);
            if (!res) break;
        }
        */
        // trace("Eclipse_ACTF_Init.initialize() done.");
    }
}

