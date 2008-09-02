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
  IEnumUnknownImpl C++ proxy
*/

#include "Com.h"
#include "org_eclipse_actf_util_win32_comclutch_impl_IEnumUnknownImpl.h"

jobjectArray JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IEnumUnknownImpl__1next
(JNIEnv *env, jobject obj, jlong ptr, jint num)
{
    IEnumUnknown* ieu = (IEnumUnknown*) ptr;
    ULONG celt = (ULONG) num;
    IUnknown **rgelt = (IUnknown**) alloca(celt * sizeof(IUnknown*));
    ULONG pceltFetched;
    int i;

    HRESULT r = ieu->Next(celt, rgelt, &pceltFetched);
    if (pceltFetched <= 0) return NULL;
    if (celt < pceltFetched) return NULL;

    jobjectArray array = env->NewObjectArray(pceltFetched, class_IUnknown, NULL);
  
    for (i = 0; i < pceltFetched; i++) {
	env->SetObjectArrayElement(array, i, create_IUnknown(env, obj, rgelt[i]));
    }
    return array;
}

jboolean JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IEnumUnknownImpl__1reset
(JNIEnv *env, jobject obj, jlong ptr)
{
    IEnumUnknown* ieu = (IEnumUnknown*) ptr;
    HRESULT r = ieu->Reset();
    return r == S_OK;
}

jboolean JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IEnumUnknownImpl__1skip
(JNIEnv *env, jobject obj, jlong ptr, jint num)
{
    IEnumUnknown* ieu = (IEnumUnknown*) ptr;
    ULONG celt = (ULONG) num;
    HRESULT r = ieu->Skip(celt);
    return r == S_OK;
}

jobject JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IEnumUnknownImpl__1_1clone
(JNIEnv *env, jobject obj, jlong ptr)
{
    IEnumUnknown* ieu = (IEnumUnknown*) ptr;
    IEnumUnknown* ppenum;
    HRESULT r = ieu->Clone(&ppenum);
    if (FAILED(r)) return NULL;
    return create_IUnknown(env, obj, ppenum);
}
