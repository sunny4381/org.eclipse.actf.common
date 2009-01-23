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
package org.eclipse.actf.util.internal.httpproxy.core;

import java.util.Comparator;

import org.eclipse.actf.util.httpproxy.core.TimeoutException;

public class FixedSizeQueue {
	private Object[] fQueue;
	private int fSize;
	private int fNextIndex = 0;
	private int fMinIndex = 0;

	public FixedSizeQueue(int queueSize) {
		fQueue = new Object[queueSize];
		fSize = queueSize;
	}

	public int getQueueSize() {
		return fSize;
	}

	public synchronized void clear() {
		if (fMinIndex != fNextIndex) {
			do {
				fQueue[fMinIndex++] = null;
				if (fMinIndex > fSize) {
					fMinIndex = 0;
				}
			} while (fMinIndex != fNextIndex);
		}
		fNextIndex = 0;
		fMinIndex = 0;
		fQueue[0] = null;
	}

	public synchronized boolean isEmpty() {
		return (fNextIndex == fMinIndex);
	}

	public synchronized int getSize() {
		int d = fNextIndex - fMinIndex;
		return (d >= 0) ? d : d + fSize;
	}

	public synchronized void put(Object obj) throws InterruptedException {
		if (obj == null) {
			throw new IllegalArgumentException("null");
		}
		while (fQueue[fNextIndex] != null) {
			this.wait();
		}
		fQueue[fNextIndex++] = obj;
		if (fNextIndex >= fSize) {
			fNextIndex = 0;
		}
		this.notifyAll();
	}

	public synchronized void put(Object obj, long timeout)
			throws TimeoutException, InterruptedException {
		if (timeout == 0) {
			put(obj);
		} else {
			if (obj == null) {
				throw new IllegalArgumentException("null");
			}
			if (fQueue[fNextIndex] != null) {
				long t0 = System.currentTimeMillis();
				long wait = timeout;
				boolean timedout = true;
				do {
					this.wait(wait);
					if (fQueue[fNextIndex] == null) {
						timedout = false;
						break;
					}
					long elapsed = System.currentTimeMillis() - t0;
					wait = timeout - elapsed;
				} while (wait > 0);
				if (timedout) {
					throw new TimeoutException("FixedSizeQueue.put");
				}
			}
			fQueue[fNextIndex++] = obj;
			if (fNextIndex >= fSize) {
				fNextIndex = 0;
			}
			this.notifyAll();
		}
	}

	public synchronized Object remove() throws InterruptedException {
		Object req;
		while ((req = fQueue[fMinIndex]) == null) {
			this.wait();
		}
		fQueue[fMinIndex++] = null;
		if (fMinIndex >= fSize) {
			fMinIndex = 0;
		}
		this.notifyAll();
		return req;
	}

	public synchronized Object remove(long timeout) throws TimeoutException,
			InterruptedException {
		Object req = fQueue[fMinIndex];
		if (req == null) {
			long t0 = System.currentTimeMillis();
			long wait = timeout;
			do {
				this.wait(wait);
				if ((req = fQueue[fMinIndex]) != null) {
					break;
				}
				long elapsed = System.currentTimeMillis() - t0;
				wait = timeout - elapsed;
			} while (wait > 0);
			if (req == null) {
				throw new TimeoutException("FixedSizeQueue.remove");
			}
		}
		fQueue[fMinIndex++] = null;
		if (fMinIndex >= fSize) {
			fMinIndex = 0;
		}
		this.notifyAll();
		return req;
	}

	public synchronized Object nonBlockingRemove() {
		Object req = fQueue[fMinIndex];
		if (req != null) {
			fQueue[fMinIndex++] = null;
			if (fMinIndex >= fSize) {
				fMinIndex = 0;
			}
			this.notifyAll();
		}
		return req;
	}

	public synchronized Object matchAndRemove(Object o,
			Comparator<Object> comparator) {
		if (o == null) {
			throw new IllegalArgumentException();
		}
		Object req = fQueue[fMinIndex];
		if (req == null) {
			return null;
		}
		if (comparator.compare(o, req) == 0) {
			fQueue[fMinIndex++] = null;
			if (fMinIndex >= fSize) {
				fMinIndex = 0;
			}
			this.notifyAll();
			return req;
		} else {
			return null;
		}
	}
}
