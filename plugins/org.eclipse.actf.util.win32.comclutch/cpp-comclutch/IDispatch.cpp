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
  IDispatchImpl C++ proxy
*/

#include "Com.h"
#include "org_eclipse_actf_util_win32_comclutch_impl_IDispatchImpl.h"


jlongArray JNICALL
Java_org_eclipse_actf_util_win32_comclutch_impl_IDispatchImpl__1getIDsOfNames
(JNIEnv *env, jobject obj, jlong ptr, jobjectArray jnames, jint jlcid)
{
    LCID lcid = (LCID) jlcid;
    IDispatch* idisp = (IDispatch*) ptr;
    int namesize = env->GetArrayLength(jnames);
    int i;
    OLECHAR** methodnames = (OLECHAR**) alloca(sizeof(OLECHAR*) * namesize);
    DISPID* dispids = (DISPID*) alloca(sizeof(DISPID) * namesize);
    jlong* jlongdispids = (jlong*) alloca(sizeof(jlong) * namesize);
    for (i = 0; i < namesize; i++) {
	jstring jname = (jstring) env->GetObjectArrayElement(jnames, i);
	const jchar* jc = env->GetStringChars(jname, NULL);
	int len = env->GetStringLength(jname);
	OLECHAR* str = (OLECHAR*) alloca(sizeof(OLECHAR) * (len + 1));
	memcpy(str, jc, sizeof(OLECHAR) * len);
	str[len] = 0; // ensure null termination.
	methodnames[i] = str;
	env->ReleaseStringChars(jname, jc);
    }
    HRESULT r = idisp->GetIDsOfNames(IID_NULL, methodnames, namesize, lcid, dispids);
    if (FAILED(r)) return NULL;
    jlongArray jdispids = env->NewLongArray(namesize);
    for (i = 0; i < namesize; i++) {
	jlongdispids[i] = dispids[i];
    }
    env->SetLongArrayRegion(jdispids, 0, namesize, jlongdispids);
    return jdispids;
}

jobject JNICALL
Java_org_eclipse_actf_util_win32_comclutch_impl_IDispatchImpl__1invoke
(JNIEnv *env, jobject jidisp, jlong ptr, jlong dispid, jint jlcid, jint flag, jobjectArray jargs)
{
    LCID lcid = (LCID) jlcid;
    IDispatch* idisp = (IDispatch*) ptr;
    UINT arg_idx = (UINT) -1;
    int i, j, argsize;
    VARIANT* vargs;

    if (!jargs) {
	argsize = 0;
	vargs = NULL;
    } else {
	argsize = env->GetArrayLength(jargs);
	if (argsize == 0) {
	    vargs = NULL;
	} else {
	    vargs = (VARIANT*) alloca(sizeof(VARIANT) * argsize);
	    for (i = 0, j = argsize - 1; i < argsize; i++, j--) {
		jobject jarg = env->GetObjectArrayElement(jargs, i);
		VariantInit(&vargs[j]);
		if (!j2v(env, jarg, &vargs[j])) {
		    for (j++ ; j < argsize; j++) {
			jc_variant_clear(&vargs[j], false);
		    }
		    return NULL;
		}
	    }
	}
    }

    DISPPARAMS dp;
    dp.rgvarg = vargs;
    dp.cArgs = argsize;
    dp.rgdispidNamedArgs = NULL;
    dp.cNamedArgs = 0;
    VARIANT vr;
    VariantInit(&vr);
    // TODO: deal with ExcepInfo
    HRESULT r;
    EXCEPINFO excepInfo;
    __try {
	r = idisp->Invoke(dispid, IID_NULL, lcid, flag, &dp, &vr, &excepInfo, &arg_idx);
    } __except(GetExceptionCode() == EXCEPTION_ACCESS_VIOLATION) {
	for (i = 0; i < argsize; i++) {
	    jc_variant_clear(&vargs[i], false);
	}
	jc_variant_clear(&vr, true);
	throw_DispatchException(env, L"Access Violation");
	return NULL;
    }
    for (i = 0; i < argsize; i++) {
	jc_variant_clear(&vargs[i], false);
    }
    if (!check_hresult(env, r, &excepInfo)) {
	jc_variant_clear(&vr, true);
	return NULL;
    }
    jobject jr = v2j(env, jidisp, &vr);
    jc_variant_clear(&vr, false);
    return jr;
}

void JNICALL
Java_org_eclipse_actf_util_win32_comclutch_impl_IDispatchImpl__1put
(JNIEnv* env, jobject jidisp, jlong ptr, jlong dispid, jint jlcid, jobject jval)
{
    LCID lcid = (LCID) jlcid;
    IDispatch* idisp = (IDispatch*) ptr;
    UINT arg_idx = (UINT) -1;
    VARIANT varg;
    VariantInit(&varg);
    if (!j2v(env, jval, &varg)) {
	jc_variant_clear(&varg, false);
	return;
    }

    DISPID tmpdispid = DISPID_PROPERTYPUT;
    DISPPARAMS dp;
    dp.rgvarg = &varg;
    dp.cArgs = 1;
    dp.rgdispidNamedArgs = &tmpdispid;
    dp.cNamedArgs = 1;

    WORD flag;
    if (varg.vt & VT_BYREF) {
	flag = DISPATCH_PROPERTYPUTREF;
    } else {
	flag = DISPATCH_PROPERTYPUT;
    }

    // TODO: deal with ExcepInfo
    EXCEPINFO excepInfo;
    HRESULT r = idisp->Invoke(dispid, IID_NULL, lcid, flag,
			      &dp, NULL, &excepInfo, &arg_idx);

    jc_variant_clear(&varg, false);
    check_hresult(env, r, &excepInfo);
}

jobject
create_IDispatch(JNIEnv* env, jobject jidisp, IDispatch* idisp)
{
    if (!idisp) return NULL;
    releaseInQueues();
    return env->CallObjectMethod(jidisp, newIDispatch, (jlong) idisp);
}
