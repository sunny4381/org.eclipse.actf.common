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
// --------------------------------------------------------------------------------
//   Bridge Initialization module for SWF bootloader
// --------------------------------------------------------------------------------
import flash.external.ExternalInterface;

var Eclipse_ACTF_DEBUG:Boolean = false;

trace('SWF bridge ver 1.3.2 is loaded');
trace('registering getSwfVersion again...');
System.security.allowDomain('*');   // shin
ExternalInterface.addCallback('getSwfVersion', null, Eclipse_ACTF_Controller.getSwfVersion);
trace('done.');

if (Eclipse_ACTF_DEBUG){
  var Eclipse_ACTF_lc:LocalConnection = new LocalConnection();
  var mess:String = "";
  mess = "SWF bridge ver 1.3.2" + newline + "this._url = " + this._url + newline;
}

for (prop in _global.Eclipse_ACTF_PROPERTIES) {
  if (Eclipse_ACTF_DEBUG){
    mess += " :" + prop + "=" + _global.Eclipse_ACTF_PROPERTIES[prop] + newline;
    mess += " :0:" + prop + "=" + _level0[prop] + newline;
  }

  if (Object.prototype[prop]) {
    delete Object.prototype[prop];
  }
  if (_level0[prop] == undefined) {
    _level0[prop] = _global.Eclipse_ACTF_PROPERTIES[prop];
  }

  if (Eclipse_ACTF_DEBUG){
    mess += " :c:" + prop + "=" + _level0[prop] + newline;
  }
}

if (Eclipse_ACTF_DEBUG){
  Eclipse_ACTF_lc.send("_testLC", "dump", mess);
}

_global.encodeStringForBridge = function(arg:String) {
  return arg;
}

#include "Eclipse_ACTF_initializer.as"
