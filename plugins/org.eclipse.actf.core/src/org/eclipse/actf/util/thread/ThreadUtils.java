/*******************************************************************************
* Copyright (c) 2004, 2007 IBM Corporation.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*  Mike Squillace - initial API and implementation
*******************************************************************************/ 

package org.eclipse.actf.util.thread;

/*
 * The following imports are specific to JDK 5.0 and need to be commented
 * out for JDK 1.4 compatibility.
 */
//import java.lang.management.ManagementFactory;
//import java.lang.management.ThreadInfo;
//import java.lang.management.ThreadMXBean;
public class ThreadUtils
{

	// TODO
	// The THREAD_DEBUG constant should be used to conditionally compile code
	// that enables thread debugging. The implementation here is also using
	// the thread management beans from jdk 1.5 and this class will ncompile
	// compile nor run under jdk 1.4 or lower. The imports are particularly hard 
	// to conditionally compile. In this case the real solution is to create a
	// wrapper class that can instantiate either version of the thread code and
	// conditionally compile it.
	//
	public static final boolean THREAD_DEBUG = false;

	private static Object _synchLock = new Object();

	/**
	 * @param args
	 */
	public static void main (String[] args) {
		// TODO Auto-generated method stub
	}

	public static void dumpThreadState () {
		dumpThreadState("");
	}

	public static void dumpThreadState (String msg) {
		/* 
		 * The following code is commented out because the dependent the mbean 
		 * Thread classes are unique to JDK 5.0. 
		 */
		/*****				
		 synchronized(_synchLock) {
		 System.out.println("\n\n");
		 if (msg.length() > 0 )
		 System.out.println(msg);
		 System.out.println("\n\nDumping Thread state at [" + new Date(System.currentTimeMillis()) + "]");    				
		 
		 System.out.println("Current Thread - " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
		 ThreadMXBean mbean = ManagementFactory.getThreadMXBean(); 
		 long[] ids = mbean.getAllThreadIds();
		 for( int i=0; i<ids.length; i++) {
		 ThreadInfo ti = mbean.getThreadInfo(ids[i],Integer.MAX_VALUE);
		 if (ti != null) {
		 System.out.println("\n\tThreadInfo for thread id - [" + ti.getThreadId() + "], name - [" + ti.getThreadName() + "]");
		 System.out.println("\t\tLock name - " + ti.getLockName());
		 System.out.println("\t\tLock owner id - " + ti.getLockOwnerId());
		 System.out.println("\t\tLock owner name - " + ti.getLockOwnerName());
		 System.out.println("\t\tisSuspended() - " + ti.isSuspended());
		 System.out.println("\t\tisInNative() - " + ti.isInNative());
		 System.out.println("\t\tThread.State - " + ti.getThreadState());
		 if(ti.getThreadState() == Thread.State.BLOCKED) {
		 System.out.println("\t\t\tStackTrace for this thread:");
		 StackTraceElement[] ste = ti.getStackTrace();
		 for (int j=0; j < ste.length; j++){
		 System.out.println("\t\t\t" + ste[j]);
		 }
		 }
		 }
		 else {
		 System.out.println("\n\tNull thread returned from ThreadMXBean.getThreadInfo(), continuing.");				
		 }
		 }
		 System.out.flush();
		 }
		 *****/
	}

	public static StringBuffer getThreadState () {
		return getThreadState("");
	}

	public static StringBuffer getThreadState (String msg) {
		StringBuffer sb = new StringBuffer();
		/* 
		 * The following code is commented out because the dependent Thread classes
		 * are unique to JDK 5.0. 
		 */
		/*****
		 synchronized(_synchLock){
		 sb.append("\n\n)");
		 sb.append(msg);
		 sb.append("Dumping Thread state at [" + new Date(System.currentTimeMillis()) + "]");
		 sb.append("Current Thread - " + Thread.currentThread().getId() + ", " + Thread.currentThread().getName());
		 ThreadMXBean mbean = ManagementFactory.getThreadMXBean(); 
		 long[] ids = mbean.getAllThreadIds();
		 for( int i=0; i<ids.length; i++) {
		 ThreadInfo ti = mbean.getThreadInfo(ids[i],Integer.MAX_VALUE);
		 if (ti != null) {
		 sb.append("\n\tThreadInfo for thread id - [" + ti.getThreadId() + "], name - [" + ti.getThreadName() + "]");
		 sb.append("\t\tLock name - " + ti.getLockName());
		 sb.append("\t\tLock owner id - " + ti.getLockOwnerId());
		 sb.append("\t\tLock owner name - " + ti.getLockOwnerName());
		 sb.append("\t\tisSuspended() - " + ti.isSuspended());
		 sb.append("\t\tisInNative() - " + ti.isInNative());
		 sb.append("\t\tThread.State - " + ti.getThreadState());
		 if(ti.getThreadState() == Thread.State.BLOCKED) {
		 sb.append("\t\t\tStackTrace for this thread:");
		 StackTraceElement[] ste = ti.getStackTrace();
		 for (int j=0; j < ste.length; j++){
		 sb.append("\t\t\t" + ste[j]);
		 }
		 }
		 }
		 else {
		 System.out.println("\n\tNull thread returned from ThreadMXBean.getThreadInfo(), continuing.");								
		 }
		 }		
		 }
		 *****/
		return sb;
	}
}