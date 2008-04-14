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
class Eclipse_ACTF_AutoTranslator {
    private static function isButton(n:Eclipse_ACTF_Node):Boolean {
        if (n.type == "object" && n.className == "Button")
            return true;
        if (n.type == "movieclip" && (eval(n.target).onRelease))
            return true;
        return false;
    }
	
    private static function isEmpty(n:Eclipse_ACTF_Node):Boolean {
        if (n.text && n.text.length > 0) return false;
        if (n.accInfo && n.accInfo.name) return false;
        if (isButton(n)) {
            n.text = n.target.substr(n.target.lastIndexOf(".") + 1);
            return false;
        }
        return true;
    }

    private static function buildItem(n:Eclipse_ACTF_Node, depth:Number):Array {
        var r:Array = new Array();
        if (n.isOpaqueObject) return r;
        if (n.accInfo && n.accInfo.silent) return r;
        var cr:Array = n.getChildNodesArray();
        cr.reverse();
        for (var i = 0; i < cr.length; i++) {
            var c:Eclipse_ACTF_Node = cr[i];

            if (!(c.type == "movieclip" ||
                  (c.type == "object" && (c.className == "TextField" || c.className == "Button"))))
                continue;
			
            if (isEmpty(c)) {
                if ((depth <= 0) && (r.length > 0)) {
                    r.push(c);
                } else {
                    r = r.concat(buildItem(c, depth - 1));
                }
            } else {
                r.push(c);
            }
        }
        return r;
    }

    public static function translate(path:String):Array {
        var n:Eclipse_ACTF_Node = Eclipse_ACTF_Node.getNodeFromPath(path);
        if (!n) return null;
        return buildItem(n, 1);
    }
}

