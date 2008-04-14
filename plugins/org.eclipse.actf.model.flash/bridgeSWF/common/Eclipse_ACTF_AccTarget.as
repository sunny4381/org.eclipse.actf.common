/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/

class Eclipse_ACTF_AccTarget {

    public static function updateTarget(target:String, depth:Number):Boolean {
        var obj:Object = eval(target);
        var log:Object = new Object();
        updateImpl(obj, target, depth, log);
        Accessibility.updateProperties();
        return log._Eclipse_ACTF_updated;
    }
    
    private static function updateImpl(obj:Object, target:String, depth:Number, log:Object) {
        if (depth < 0) {
            return;
        }
		if (log[target]) {
			return;
		}
        log[target] = true;

        if (!(obj instanceof MovieClip) && !(obj instanceof Button)) {
            return;
        }

        var isButton:Boolean = obj.onRelease || obj.onPress || obj instanceof Button;

        if (isButton) {
            obj._accProps = obj._accProps || {};
            obj._accProps.shortcut = target;
            log._Eclipse_ACTF_updated = true;
            return;
        }
        if (!(obj instanceof MovieClip)) {
            return;
        }
        
		for (var c in obj) {
			var cTarget:String = target+"."+c;
			var co:Object = eval(cTarget);
			cTarget = targetPath(co);
			if (!co._target) {
				co._target = cTarget;
			}
			updateImpl(co, cTarget, depth - 1, log);
		}
    }
}
