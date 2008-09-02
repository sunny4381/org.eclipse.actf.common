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
  Java <-> COM Type Bridge Array
*/
#include "Com.h"


jobject
make_byte_array(JNIEnv* env, SAFEARRAY *parray, long l, long u) {
	long i, len;
	long indices[1];
	jbyte value[1];
	HRESULT hr;
	
	len = u - l + 1;
	jbyteArray array = env->NewByteArray(len);
	if (!array)
		return NULL;
		
	for (i = l; i <= u; i++) {
		indices[0] = i;
		hr = SafeArrayGetElement(parray, indices, value);
		if (FAILED(hr))
			return NULL;
	    env->SetByteArrayRegion(array, i-l, 1, value);
	}
	return array;
}

jobject
make_short_array(JNIEnv* env, SAFEARRAY *parray, long l, long u) {
	long i, len;
	long indices[1];
	jshort value[1];
	HRESULT hr;
	
	len = u - l + 1;
	jshortArray array = env->NewShortArray(len);
	if (!array)
		return NULL;
	
	for (i = l; i <= u; i++) {
		indices[0] = i;
	    hr = SafeArrayGetElement(parray, indices, value);
		if (FAILED(hr))
			return NULL;
	    env->SetShortArrayRegion(array, i-l, 1, value);
	}
	return array;
}

jobject
make_int_array(JNIEnv* env, SAFEARRAY *parray, long l, long u) {
	long i, len;
	long indices[1];
	jint value[1];
	HRESULT hr;
	
	len = u - l + 1;
	jintArray array = env->NewIntArray(len);
	if (!array)
		return NULL;
	
	for (i = l; i <= u; i++) {
		indices[0] = i;
	    hr = SafeArrayGetElement(parray, indices, value);
		if (FAILED(hr))
			return NULL;
	    env->SetIntArrayRegion(array, i-l, 1, value);
	}
	return array;
}

jobject
make_long_array(JNIEnv* env, SAFEARRAY *parray, long l, long u) {
	long i, len;
	long indices[1];
	jlong value[1];
	HRESULT hr;
		
	len = u - l + 1;
	jlongArray array = env->NewLongArray(len);
	if (!array)
		return NULL;
	
	for (i = l; i <= u; i++) {
		indices[0] = i;
	    hr = SafeArrayGetElement(parray, indices, value);
	    if (FAILED(hr))
			return NULL;
	    env->SetLongArrayRegion(array, i-l, 1, value);
	}
	return array;
}

jobject
make_float_array(JNIEnv* env, SAFEARRAY *parray, long l, long u) {
	long i, len;
	long indices[1];
	jfloat value[1];
	HRESULT hr;
	
	len = u - l + 1;
	jfloatArray array = env->NewFloatArray(len);
	if (!array)
		return NULL;
	
	for (i = l; i <= u; i++) {
		indices[0] = i;
		hr = SafeArrayGetElement(parray, indices, value);
		if (FAILED(hr))
			return NULL;
	    env->SetFloatArrayRegion(array, i-l, 1, value);
	}
	return array;
}

jobject
make_double_array(JNIEnv* env, SAFEARRAY *parray, long l, long u) {
	long i, len;
	long indices[1];
	jdouble value[1];
	HRESULT hr;
	
	len = u - l + 1;
	jdoubleArray array = env->NewDoubleArray(len);
	if (!array)
		return NULL;
	
	for (i = l; i <= u; i++) {
		indices[0] = i;
	    hr = SafeArrayGetElement(parray, indices, value);
		if (FAILED(hr))
			return NULL;
	    env->SetDoubleArrayRegion(array, i-l, 1, value);
	}
	return array;
}


jobject
jc_make_array(JNIEnv* env, jobject jidisp, VARIANT* v)
{
	long l, u, d;
	HRESULT hr;
	VARTYPE vt = v->vt & (~VT_ARRAY);
	d = SafeArrayGetDim(v->parray);

	if (d != 1) {
		throw_DispatchException(env, L"VT_ARRAY (dim != 1) is not yet supported.");
		return NULL;
	}
	
	hr = SafeArrayGetLBound(v->parray, 1, &l);
	if (FAILED(hr)) {
		return NULL;
	}
	hr = SafeArrayGetUBound(v->parray, 1, &u);
	if (FAILED(hr)) {
		return NULL;
	}
	
	switch(vt) {
		case VT_I1:
			return make_byte_array(env, v->parray, l, u);
	    	case VT_I2:
			return make_short_array(env, v->parray, l, u);
		case VT_INT:
		case VT_I4:
			return make_int_array(env, v->parray, l, u);
		case VT_I8:
			return make_long_array(env, v->parray, l, u);
		case VT_R4:
			return make_float_array(env, v->parray, l, u);
		case VT_R8:
			return make_double_array(env, v->parray, l, u);
		   
		default:
		   throw_DispatchException(env, L"Not supported VT_ARRAY type.");
		   return NULL;
	}
	return NULL;
}
