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


class Eclipse_ACTF_StageProxy {
    public static function setScaleMode(mode:String):Boolean {
        Stage.scaleMode = mode;
        return (Stage.scaleMode == mode)
    }

    public static function getScaleMode():String {
        return Stage.scaleMode;
    }

    public static function getStageWidth():Number {
        return Stage.width;
    }

    public static function getStageHeight():Number {
        return Stage.height;
    }

    public static function getStageAlign():String {
        return Stage.align;
    }
}
