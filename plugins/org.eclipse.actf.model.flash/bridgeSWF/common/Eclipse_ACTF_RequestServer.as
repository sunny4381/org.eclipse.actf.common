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
class Eclipse_ACTF_RequestServer {
    private static var secret:String;

    private static function process(propName, oldVal, newVal, userData) {
        // dtrace("processing: " + propName + " " + oldVal + " " + newVal + " " + userData);
        if (newVal.substring(0, secret.length) == secret) {
            var args = Eclipse_ACTF_ASSerializer.deserialize(newVal.substring(secret.length));
            var name = args.shift();
	    // dtrace("  calling function " + name + ": " + args[0] + " " + args[1]);
            var method:Function = Eclipse_ACTF_Init.callbacks[name];
            var retObj = method.apply(null, args);
	    // dtrace("  return value [" + retObj + "]");
            _root.Eclipse_ACTF_response_value = Eclipse_ACTF_ASSerializer.serialize(retObj);
        }
        return oldVal;
    }

    public static function start(sec:String) {
        secret = sec;
        _root.watch("Eclipse_ACTF_request_args", Eclipse_ACTF_RequestServer.process);
        // dtrace("request server started.");
    }

    /*
    private static function dtrace(mes:String) {
        if (debug) {
            trace(mes);
        }
    }
    */
}

