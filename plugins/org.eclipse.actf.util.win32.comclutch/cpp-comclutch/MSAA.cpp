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
#include <windows.h>
#include <oleacc.h>
#include "org_eclipse_actf_util_win32_msaa_MSAA.h"

/*
 * Class:     org_eclipse_actf_util_win32_msaa_MSAA
 * Method:    _WindowFromAccessibleObject
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_eclipse_actf_util_win32_msaa_MSAA__1WindowFromAccessibleObject
  (JNIEnv *env, jclass cls, jlong pAcc)
{
	HWND hwnd = NULL;
	if( FAILED(WindowFromAccessibleObject((IAccessible*)pAcc,&hwnd)) ) {
		return NULL;
	}
	return (jlong)hwnd;
}


/*
 * Class:     org_eclipse_actf_util_win32_msaa_MSAA
 * Method:    _getAccessibleChildren
 * Signature: (JII)[Ljava/lang/Object;
 */
JNIEXPORT jobjectArray JNICALL Java_org_eclipse_actf_util_win32_msaa_MSAA__1getAccessibleChildren
  (JNIEnv *env, jclass cls, jobject idisp, jlong ptr, jint start, jint count)
{
	VARIANT* children = (VARIANT*) alloca(sizeof(VARIANT) * count);
	
	for (int i = 0; i < count; i++) {
	  VariantInit(&children[i]);
	}
	
	LONG nChild;
	HRESULT h = AccessibleChildren((IAccessible*)ptr, start, count, children, &nChild);
	if( FAILED(h) ) {
	  return NULL;
	}
	
	//fprintf(stdout, "nChild = %d\n", nChild);
	//fflush(stdout);
	
	jobjectArray ret = env->NewObjectArray(count, class_java_lang_object, NULL);
	
	for (int i = 0; i < count; i++) {
	   env->SetObjectArrayElement(ret, i, v2j(env, idisp, &children[i]));
    	   jc_variant_clear(&children[i], false);
	}
	
	free(children);
		
	return ret;
}

/*
 * Class:     org_eclipse_actf_util_win32_msaa_MSAA
 * Method:    _getRoleText
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_eclipse_actf_util_win32_msaa_MSAA__1getRoleText
  (JNIEnv *env, jclass cls, jint lRole)
{
  	int count = 0;
	TCHAR lpRole[256];

	count = (int) GetRoleTextW(lRole, (LPWSTR)lpRole, 256);

	return env->NewString((jchar *) lpRole, count);
}


/*
 * Class:     org_eclipse_actf_util_win32_msaa_MSAA
 * Method:    _AcessibleObjectFromWindow
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_org_eclipse_actf_util_win32_msaa_MSAA__1AcessibleObjectFromWindow
  (JNIEnv *env, jclass cls, jlong hwnd)
{
	void *pObject = NULL;
//	if( FAILED( AccessibleObjectFromWindow((HWND)hwnd,OBJID_CLIENT,IID_IAccessible,&pObject) ) ) {
	if( FAILED( AccessibleObjectFromWindow((HWND)hwnd,OBJID_WINDOW,IID_IAccessible,&pObject) ) ) {
		return NULL;
	}
	return (jint) pObject;
}
