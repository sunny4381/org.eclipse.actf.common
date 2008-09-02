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
#include "Com.h"
#include "org_eclipse_actf_util_win32_comclutch_impl_IServiceProviderImpl.h"


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_impl_IServiceProviderImpl
 * Method:    _QueryService
 * Signature: (JJJJJ)Lorg/eclipse/actf/util/comclutch/win32/IUnknown;
 */
JNIEXPORT jobject JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IServiceProviderImpl__1QueryService
  (JNIEnv *env, jobject obj, jlong ptr, jlong msb1, jlong lsb1, jlong msb2, jlong lsb2)
{
    IServiceProvider* iunk = (IServiceProvider*) ptr;
    IID iid1, iid2;
    GUID_from_uuidbits(msb1, lsb1, &iid1);
    GUID_from_uuidbits(msb2, lsb2, &iid2);
    
    void* pretobj;

    HRESULT r = iunk->QueryService(iid1, iid2, &pretobj);
    if (!check_hresult(env, r, NULL)) return NULL;

    return create_IUnknown(env, obj, (IUnknown*) pretobj);
}