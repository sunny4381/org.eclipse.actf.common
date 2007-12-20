/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Takashi ITOH - initial API and implementation
 *******************************************************************************/
#include "stdafx.h"
#include "org_eclipse_actf_util_win32_COMUtil.h"

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__III
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3, jint arg4)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3, arg4);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIIIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3, jint arg4, jint arg5)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3, arg4, arg5);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIIIIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3, jint arg4, jint arg5, jint arg6)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint, jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3, arg4, arg5, arg6);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIIIIIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3, jint arg4, jint arg5, jint arg6, jint arg7)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint, jint, jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3, arg4, arg5, arg6, arg7);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIIIIIIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3, jint arg4, jint arg5, jint arg6, jint arg7, jint arg8)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint, jint, jint, jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_util_win32_COMUtil_VtblCall__IIIIIIIIII
  (JNIEnv *env, jclass that, jint arg0, jint arg1, jint arg2, jint arg3, jint arg4, jint arg5, jint arg6, jint arg7, jint arg8, jint arg9)
{
	jint rc = 0;
	try {
		rc = (jint)((jint (STDMETHODCALLTYPE *)(jint, jint, jint, jint, jint, jint, jint, jint, jint))(*(jint **)arg1)[arg0])(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
	}
	catch(...) {
		rc = 1;
	}
	return rc;
}
