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
  IUrlHistoryStg2Impl C++ proxy
*/
#include <atlbase.h>
#include <shlobj.h>
#include <UrlHist.h>
#include "org_eclipse_actf_util_win32_comclutch_impl_IUrlHistoryStg2Impl.h"

jlong JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IUrlHistoryStg2Impl__1initialize
(JNIEnv *env, jobject obj)
{
    IUrlHistoryStg2* pUrlHistoryStg2 = NULL;
    HRESULT hr = CoCreateInstance(CLSID_CUrlHistory,
				  NULL, CLSCTX_INPROC, IID_IUrlHistoryStg2,
				  (void**)&pUrlHistoryStg2);
    return (jlong) pUrlHistoryStg2;
}

jboolean JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IUrlHistoryStg2Impl__1isVisited
(JNIEnv *env, jobject obj, jlong ptr, jstring str)
{
    IUrlHistoryStg2* pUrlHistoryStg2 = (IUrlHistoryStg2*) ptr;
  
    int len = env->GetStringLength(str);
    const jchar* jc = env->GetStringChars(str, NULL);
    BSTR bstr = SysAllocStringLen((OLECHAR*) jc, len);
    env->ReleaseStringChars(str, jc);

    STATURL stat;
    HRESULT r = pUrlHistoryStg2->QueryUrl(bstr, STATURL_QUERYFLAG_TOPLEVEL, &stat);
    SysFreeString(bstr);
    if (FAILED(r)) return false;
    return stat.pwcsUrl != NULL;
}

void JNICALL Java_org_eclipse_actf_util_win32_comclutch_impl_IUrlHistoryStg2Impl__1release
(JNIEnv *env, jobject obj, jlong ptr)
{
    IUrlHistoryStg2* pUrlHistoryStg2 = (IUrlHistoryStg2*) ptr;
    pUrlHistoryStg2->Release();
}
