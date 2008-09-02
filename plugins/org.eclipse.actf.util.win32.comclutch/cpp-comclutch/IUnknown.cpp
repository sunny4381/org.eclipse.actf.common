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
/*
  IUnknownImpl C++ proxy
*/

#include "Com.h"
#include "org_eclipse_actf_util_win32_comclutch_impl_IUnknownImpl.h"

static int releaseWaitQueuesSize;
static int releaseWaitQueuesIdx;
static IUnknown** releaseWaitQueues;
static CRITICAL_SECTION critSecWaitQueue;
static int addInQueue(IUnknown *piunk);

jint JNICALL
Java_org_eclipse_actf_util_win32_comclutch_impl_IUnknownImpl__1release
(JNIEnv* env, jobject obj, jlong ptr)
{
    IUnknown* iunk = (IUnknown*) ptr;
    return iunk->Release();
}

jint JNICALL
Java_org_eclipse_actf_util_win32_comclutch_impl_IUnknownImpl__1addRef
(JNIEnv* env, jobject obj, jlong ptr)
{
    IUnknown* iunk = (IUnknown*) ptr;
    return iunk->AddRef();
}

void JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IUnknownImpl__1addReleaseWaitQueue
(JNIEnv* env, jclass jcls, jlong ptr)
{
    IUnknown* iunk = (IUnknown*) ptr;
    addInQueue(iunk);
}

jobject JNICALL
Java_org_eclipse_actf_util_win32_comclutch_impl_IUnknownImpl__1queryInterface
(JNIEnv* env, jobject obj, jlong ptr, jlong jiidmsb, jlong jiidlsb)
{
    IUnknown* iunk = (IUnknown*) ptr;
    IID iid;
    GUID_from_uuidbits(jiidmsb, jiidlsb, &iid);
    void * pretobj;

    HRESULT r = iunk->QueryInterface(iid, &pretobj);
    if (!check_hresult(env, r, NULL)) return NULL;

    return create_IUnknown(env, obj, (IUnknown*) pretobj);
}

jobject
create_IUnknown(JNIEnv* env, jobject jiunk, IUnknown* iunk)
{
    releaseInQueues();
    if (!iunk) return NULL;
    return env->CallObjectMethod(jiunk, newIUnknown, (jlong) iunk);
}

static int
enlarge_queuesize()
{
    releaseWaitQueuesSize *= 2;
    releaseWaitQueues = (IUnknown**) realloc(releaseWaitQueues,
					     sizeof(IUnknown*) * releaseWaitQueuesSize);
    if (!releaseWaitQueues) return 0;
    return 1;
}

int
releaseInQueues()
{
    if (releaseWaitQueuesIdx > 0) {
	EnterCriticalSection(&critSecWaitQueue);
	for (int i = 0; i < releaseWaitQueuesIdx; i++) {
	    releaseWaitQueues[i]->Release();
	}
	releaseWaitQueuesIdx = 0;
	LeaveCriticalSection(&critSecWaitQueue);
    }
    return 1;
}

static int
addInQueue(IUnknown *piunk)
{
    EnterCriticalSection(&critSecWaitQueue);
    if (releaseWaitQueuesIdx == releaseWaitQueuesSize) {
	enlarge_queuesize();
    }
    releaseWaitQueues[releaseWaitQueuesIdx++] = piunk;
    LeaveCriticalSection(&critSecWaitQueue);
    return 1;
}

int
initialize_IUnknown(JNIEnv* env)
{
    releaseWaitQueuesIdx = 0;
    releaseWaitQueuesSize = 4;
    releaseWaitQueues = (IUnknown**) malloc(sizeof(IUnknown*) * releaseWaitQueuesSize);
    if (!releaseWaitQueues) return 0;
    InitializeCriticalSection(&critSecWaitQueue);
    return 1;
}


