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

package org.eclipse.actf.util.internal.httpproxy;

import java.util.ArrayList;

public class WorkpileControllerImpl implements IWorkpileController {
	private final String name;
	private final ThreadGroup threadGroup;
	private final ArrayList<Thread> workpile;

	public void input(Runnable work) {
		Thread th;
		synchronized (this) {
			th = new Thread(threadGroup, work, name + "-" + workpile.size()); //$NON-NLS-1$
			workpile.add(th);
		}
		th.setDaemon(true);
		th.start();
	}

	public String toString() {
		return "WPC:[" + name + "]:" + threadGroup.toString(); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public WorkpileControllerImpl(String name) {
		this.name = name;
		this.threadGroup = new ThreadGroup(name);
		this.workpile = new ArrayList<Thread>();
	}
}
