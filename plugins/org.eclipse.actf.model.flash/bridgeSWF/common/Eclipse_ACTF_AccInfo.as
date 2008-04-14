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
class Eclipse_ACTF_AccInfo {
    var silent:Boolean;
    var forceSimple:Boolean;
    var name:String;
    var description:String;
    var shortcut:String;

    var role:Number;
    var state:Number;
    var defaultAction:String;
    var children:Array;

    private function Eclipse_ACTF_AccInfo(o:Object) {
        if (o._accProps) {
            this.silent = o._accProps.silent;
            this.forceSimple = o._accProps.forceSimple;
            this.name = o._accProps.name;
            this.description = o._accProps.description;
            this.shortcut = o._accProps.shortcut;
        }

        if (o._accImpl) {
            var ai:Object = o._accImpl;
            var nameAccImpl:String = ai.get_accName(0);
            if (nameAccImpl) this.name = nameAccImpl;
            this.role = ai.get_accRole(0);
            this.state = ai.get_accState(0);
            this.defaultAction = ai.get_accDefaultAction(0);
            this.children = ai.getChildIdArray();
        }
    }

    public static function newAccInfo(o:Object) {
        if (o._accProps || o._accImpl) return new Eclipse_ACTF_AccInfo(o);
        return undefined;
    }
}
