/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/

package org.eclipse.actf.model.internal.flash.proxy.logs;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class ProxyLogRecord {

    private long millis;
    private Level level;
    private String message;
    private String loggerName;
    private String id;
    // private static final Pattern pattern = Pattern.compile("-(\\d+):");
    private static final Pattern pattern = Pattern.compile("\\[id:(\\d+)\\] (.*)");
    
    public ProxyLogRecord(LogRecord record) {
        millis = record.getMillis();
        level = record.getLevel();
        loggerName = record.getLoggerName();
        message = record.getMessage();
        id = "";
        if( null != message ) {
            Matcher matcher = pattern.matcher(message);
            if( matcher.find() ) {
                id= matcher.group(1);
                // message = matcher.replaceFirst(":");
                message=matcher.group(2);
            }
            if( message.length()>256 ) {
                message = message.substring(0,256)+"...";
            }
        }
    }

    public long getMillis() {
        return millis;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getID() {
        return id;
    }
    
    public String getLoggerName() {
        return loggerName;
    }
}
