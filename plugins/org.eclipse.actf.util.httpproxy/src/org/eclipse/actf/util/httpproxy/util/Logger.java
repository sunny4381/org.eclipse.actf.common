/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hideki TAI - initial API and implementation
 *******************************************************************************/
package org.eclipse.actf.util.httpproxy.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;
import java.util.logging.Level;


public class Logger {
    public static void setConfigPropertyName(String name) {
        PROP_LOGGING_CONFIGURATION = name;
    }
    private static String PROP_LOGGING_CONFIGURATION = "httpproxy.conf.logging";
    public static final String DEFAULT_LOGGING_CONFIGURATION = "logging.conf";

    private static String sName;

    public static Logger getLogger(Class clazz) {
        return new Logger(clazz);
    }

    public static void configure(String name) throws Exception {
        sName = name;
        String config = System.getProperty(PROP_LOGGING_CONFIGURATION, DEFAULT_LOGGING_CONFIGURATION);
        Properties props = new Properties();
        Exception exception = null;
        if (config != null) {
            try {
                FileInputStream in = new FileInputStream(config);
                java.util.logging.LogManager.getLogManager().readConfiguration(in);

                in = new FileInputStream(config);
                props.load(in);
                
                exception = null;
            } catch (FileNotFoundException e) {
                //System.err.println("WARNING: File not found " + config);
            	exception = e;
            } catch (IOException e) {
                //System.err.println("WARNING: Cannot read file " + config);
            	exception = e;
            }
        }

        java.util.logging.Logger l = java.util.logging.Logger.getLogger("");
        l.setLevel(Level.ALL);

        //Set a FileHandler... disabled now. We can use Chainsaw or some other log viewers via network.
        //setFileHandler(l, name, props);
        
        if (exception != null) {
        	throw exception;
        }
    }

    public static void configure(String name, InputStream configIS) throws Exception {
        sName = name;
        Exception exception = null;
        if (configIS != null) {
            try {
                java.util.logging.LogManager.getLogManager().readConfiguration(configIS);
                exception = null;
            } catch (FileNotFoundException e) {
                //System.err.println("WARNING: File not found " + config);
            	exception = e;
            } catch (IOException e) {
                //System.err.println("WARNING: Cannot read file " + config);
            	exception = e;
            }
        }

        java.util.logging.Logger l = java.util.logging.Logger.getLogger("");
        l.setLevel(Level.ALL);
        
        if (exception != null) {
            throw exception;
        }
    }

/*
    private static void setFileHandler(java.util.logging.Logger l, String name, Properties props) throws IOException {
        String pattern = props.getProperty("java.util.logging.FileHandler.pattern", "logs/%s_%u.log");
        pattern = pattern.replaceAll("%s", name);
        Handler fileHandler = new FileHandler(pattern);
        String formatterName = props.getProperty("java.util.logging.FileHandler.formatter");
        if (formatterName != null) {
            try {
                Class formatterClass = Class.forName(formatterName);
                Object o = formatterClass.newInstance();
                if (o instanceof Formatter) {
                    fileHandler.setFormatter((Formatter) o);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        //fileHandler.setLevel(Level.ALL);
        //l.addHandler(fileHandler);
        final MemoryHandler mh = new MemoryHandler(fileHandler, 1000, Level.ALL);
        l.addHandler(mh);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            public void run() {
                mh.push();
                mh.flush();
            }
        }, 0, 1000L);
    }
*/

    private final java.util.logging.Logger fDelegate;
    private final String fName;

    private Logger(Class clazz) {
        fName = sName;
        fDelegate = java.util.logging.Logger.getLogger(clazz.getName());
    }

    public final boolean isDebugEnabled() {
        return fDelegate.isLoggable(Level.FINE);
    }

    public final boolean isMethodTracingEnabled() {
        return fDelegate.isLoggable(Level.FINER);
    }

    private final String createMessage(String msg) {
        StringBuffer sb = new StringBuffer();
        //sb.append('[');
        //sb.append(fName);
        //sb.append("] ");
        sb.append(msg);
        return sb.toString();
    }

    public void entering(String methodName) {
        fDelegate.entering(fDelegate.getName(), methodName);
    }

    private Object arrayToString(Object obj1) {
        if (!obj1.getClass().isArray() || obj1.getClass().getComponentType().isPrimitive()) {
            return obj1;
        }
        Object[] objs = (Object[]) obj1;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < objs.length; ++i) {
            if (i != 0) {
                buf.append(",");
            }
            buf.append(arrayToString(objs[i]));
        }
        return buf.toString();
    }

    public void entering(String methodName, Object obj1) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        fDelegate.entering(fDelegate.getName(), methodName, obj1);
    }

    public void entering(String methodName, Object obj1, Object obj2) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        if (obj2 != null && obj2.getClass().isArray()) {
            obj2 = arrayToString(obj2);
        }
        fDelegate.entering(fDelegate.getName(), methodName, new Object[] { obj1, obj2 });
    }

    public void entering(String methodName, Object obj1, Object obj2, Object obj3) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        if (obj2 != null && obj2.getClass().isArray()) {
            obj2 = arrayToString(obj2);
        }
        if (obj3 != null && obj3.getClass().isArray()) {
            obj3 = arrayToString(obj3);
        }
        fDelegate.entering(fDelegate.getName(), methodName, new Object[] { obj1, obj2, obj3 });
    }

    public void entering(String methodName, Object obj1, Object obj2, Object obj3, Object obj4) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        if (obj2 != null && obj2.getClass().isArray()) {
            obj2 = arrayToString(obj2);
        }
        if (obj3 != null && obj3.getClass().isArray()) {
            obj3 = arrayToString(obj3);
        }
        if (obj4 != null && obj4.getClass().isArray()) {
            obj4 = arrayToString(obj4);
        }
        fDelegate.entering(fDelegate.getName(), methodName, new Object[] { obj1, obj2, obj3, obj4 });
    }

    public void exiting(String methodName) {
        fDelegate.exiting(fDelegate.getName(), methodName);
    }

    public void exiting(String methodName, Object obj1) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        fDelegate.exiting(fDelegate.getName(), methodName, obj1);
    }

    public void exiting(String methodName, Object obj1, Object obj2) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        if (obj2 != null && obj2.getClass().isArray()) {
            obj2 = arrayToString(obj2);
        }
        fDelegate.exiting(fDelegate.getName(), methodName, new Object[] { obj1, obj2 });
    }

    public void exiting(String methodName, Object obj1, Object obj2, Object obj3) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        if (obj2 != null && obj2.getClass().isArray()) {
            obj2 = arrayToString(obj2);
        }
        if (obj3 != null && obj3.getClass().isArray()) {
            obj3 = arrayToString(obj3);
        }
        fDelegate.exiting(fDelegate.getName(), methodName, new Object[] { obj1, obj2, obj3 });
    }

    public void exiting(String methodName, Object obj1, Object obj2, Object obj3, Object obj4) {
        if (obj1 != null && obj1.getClass().isArray()) {
            obj1 = arrayToString(obj1);
        }
        if (obj2 != null && obj2.getClass().isArray()) {
            obj2 = arrayToString(obj2);
        }
        if (obj3 != null && obj3.getClass().isArray()) {
            obj3 = arrayToString(obj3);
        }
        if (obj4 != null && obj4.getClass().isArray()) {
            obj4 = arrayToString(obj4);
        }
        fDelegate.exiting(fDelegate.getName(), methodName, new Object[] { obj1, obj2, obj3, obj4 });
    }

    /**
     * Write a log message for serious situation.
     * @param msg
     */
    public void fatal(String msg) {
        fDelegate.severe(createMessage(msg));
    }

    public void fatal(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(msg);
        sb.append(", ");
        sb.append(dumpStackTrace(e));
        fDelegate.severe(sb.toString());
    }

    /**
     * Write a log message notifying a potential problem.
     * @param msg
     */
    public void warning(String msg) {
        fDelegate.warning(createMessage(msg));
    }

    public void warning(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(msg);
        sb.append(", ");
        sb.append(dumpStackTrace(e));
        fDelegate.warning(sb.toString());
    }

    /**
     * Write a log message for informational messages.
     * @param msg
     */
    public void info(String msg) {
        fDelegate.info(createMessage(msg));
    }

    /**
     * Write a log message for debug.
     * @param msg
     */
    public void debug(String msg) {
        fDelegate.fine(createMessage(msg));
    }

    public void debug(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(msg);
        sb.append(", ");
        sb.append(dumpStackTrace(e));
        fDelegate.fine(sb.toString());
    }

    /**
     * Write a log message for debug.
     * @param msg
     */
    public void debug2(String msg) {
        fDelegate.finest(createMessage(msg));
    }

    public void debug2(String msg, Throwable e) {
        StringBuffer sb = new StringBuffer();
        sb.append(msg);
        sb.append(", ");
        sb.append(dumpStackTrace(e));
        fDelegate.finest(sb.toString());
    }

    private StringBuffer dumpStackTrace(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
        pw.flush();
        return w.getBuffer();
    }
}
