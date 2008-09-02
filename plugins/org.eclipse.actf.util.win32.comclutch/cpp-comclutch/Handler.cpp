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
  Handler C++ proxy
*/

#include "Com.h"
#include "org_eclipse_actf_util_win32_comclutch_Handler.h"

jmethodID defaultHandlerMethod;

class Handler : public IDispatch
{
  private:
    ULONG refCount;
    jobject jobj;

  public:
    Handler() { 
	refCount = 0;
	jobj = NULL;
    };
    ~Handler() {
	if (jobj) {
	    JNIEnv *env = getEnv();
	    env->DeleteGlobalRef(jobj);
	    jobj = NULL;
	}
    }

    void initRef(JNIEnv* env, jobject jlobj) {
	jobj = env->NewGlobalRef(jlobj);
    }

    // IUnknown
    STDMETHODIMP QueryInterface(REFIID, void **);
    STDMETHODIMP_(ULONG) AddRef() {
	return ++refCount;
    }
    STDMETHODIMP_(ULONG) Release() {
	int c = refCount--;
	if (c < 0) delete this;
	return c;
    }

    //IDispatch
    STDMETHODIMP GetTypeInfoCount(UINT* pctinfo) {
	return E_NOTIMPL;
    }
    STDMETHODIMP GetTypeInfo(UINT iTInfo,
			     LCID lcid,
			     ITypeInfo** ppTInfo) {
	return E_NOTIMPL;
    }
    STDMETHODIMP GetIDsOfNames(REFIID riid,
			       LPOLESTR *rgszNames,
			       UINT cNames,
			       LCID lcid,
			       DISPID *rgDispId);
    STDMETHODIMP Invoke(DISPID dispIdMember,
			REFIID riid,
			LCID lcid,
			WORD wFlags,
			DISPPARAMS  *pDispParams,
			VARIANT  *pVarResult,
			EXCEPINFO *pExcepInfo,
			UINT *puArgErr);
};


STDMETHODIMP
Handler::QueryInterface(REFIID riid, void** ppv)
{
    if (riid == IID_IDispatch) {
	AddRef();
	*ppv = this;
	return NOERROR;
    }
    return E_NOINTERFACE;
}

STDMETHODIMP
Handler::GetIDsOfNames(REFIID riid,
		       OLECHAR** rgszNames,
		       UINT cNames,
		       LCID lcid,
		       DISPID* rgDispId)
{
    for (int i = 0; i < cNames; i++) {
	rgDispId[i] = DISPID_VALUE;
    }
    return NOERROR;
}

STDMETHODIMP
Handler::Invoke(DISPID dispIdMember,
		REFIID riid,
		LCID lcid,
		WORD wFlags,
		DISPPARAMS* pDp,
		VARIANT* pVarResult,
		EXCEPINFO* pExcepInfo,
		UINT* puArgErr)
{
    if (dispIdMember != DISPID_VALUE) return S_FALSE;
    if (wFlags & DISPATCH_METHOD) return S_FALSE;

    JNIEnv *env = getEnv();
    jobjectArray jargs = env->NewObjectArray(pDp->cArgs, class_java_lang_object, NULL);
    int i, j;
    for (j = 0, i = pDp->cArgs - 1; i >= 0; i--, j++) {
	jobject jo = v2j(env, jobj, &pDp->rgvarg[i]);
	env->SetObjectArrayElement(jargs, j, jo);
    }

    jobject jrobj = env->CallObjectMethod(jobj, defaultHandlerMethod, jargs);
    if (!j2v(env, jrobj, pVarResult)) {
	return E_FAIL;
    }
    
    return S_OK;
}

// --------------------------------------------------------------------------------
// Java side initialization native methods.
// --------------------------------------------------------------------------------


jlong JNICALL
Java_org_eclipse_actf_util_win32_comclutch_Handler__1createHandler
(JNIEnv* env, jclass cls, jlong id)
{
    if (!defaultHandlerMethod) {
	defaultHandlerMethod = env->GetMethodID(cls, "defaultHandler",
						"([Ljava/lang/Object;)Ljava/lang/Object;");
    }
    Handler *ph = new Handler();
    return (jlong) ph;
}

void JNICALL
Java_org_eclipse_actf_util_win32_comclutch_Handler__1setObject
(JNIEnv* env, jobject jobj, jlong ptr)
{
    Handler *ph = (Handler*) ptr;
    ph->initRef(env, jobj);
}
