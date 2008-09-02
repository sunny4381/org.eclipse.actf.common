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
/*
  IOleContainerImpl C++ proxy
*/

#include "Com.h"
#include "org_eclipse_actf_util_win32_comclutch_impl_IOleContainerImpl.h"

jobject 
create_IEnumUnknown(JNIEnv* env, jobject jiole, IEnumUnknown* ieu);

jobject JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IOleContainerImpl__1enumObjects
(JNIEnv *env, jobject obj, jlong ptr, jint flags) 
{
    IOleContainer* iole = (IOleContainer*) ptr;
    DWORD grfFlags = (DWORD) flags;
    IEnumUnknown *penum;

    HRESULT r = iole->EnumObjects(grfFlags, &penum);
    if (r != S_OK) return NULL;

    penum->AddRef();

    jobject jieu = create_IUnknown(env, obj, penum);
    return jieu;
}

jboolean JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IOleContainerImpl__1lockContainer
(JNIEnv *env, jobject obj, jlong ptr, jboolean lock)
{
    IOleContainer* iole = (IOleContainer*) ptr;
    BOOL fLock = (BOOL) lock;

    HRESULT r = iole->LockContainer(fLock);
    return r == S_OK;
}
