/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Daisuke SATO
 *******************************************************************************/
/*
  Java <-> COM Type Bridge
*/
#include "Com.h"

/*
  VT_NULL
  VT_BOOL
  VT_BSTR
  VT_CY
  VT_DATE
  VT_DISPATCH
  VT_EMPTY
  VT_ERROR
  VT_I1
  VT_I2
  VT_I4
  VT_INT
  VT_I8
  VT_R4
  VT_R8
  VT_UI1
  VT_UI2
  VT_UI4
  VT_UINT
  VT_UNKNOWN
  VT_BOOL | VT_BYREF
  VT_BSTR | VT_BYREF
  VT_CY | VT_BYREF
  VT_DATE | VT_BYREF
  VT_DECIMAL | VT_BYREF
  VT_DISPATCH | VT_BYREF
  VT_ERROR | VT_BYREF
  VT_I1 | VT_BYREF
  VT_I2 | VT_BYREF
  VT_I4 | VT_BYREF
  VT_INT | VT_BYREF
  VT_I8 | VT_BYREF
  VT_R4 | VT_BYREF
  VT_R8 | VT_BYREF
  VT_UI1 | VT_BYREF
  VT_UI2 | VT_BYREF
  VT_UI4 | VT_BYREF
  VT_UINT | VT_BYREF
  VT_UNKNOWN | VT_BYREF
  VT_VARIANT | VT_BYREF
  VT_GUID | VT_BYREF
  VT_ARRAY | *
*/

int j2v(JNIEnv* env, jobject o, VARIANT* v)
{
    if (!o) {
		v->vt = VT_NULL;
		return 1;
    }
    if (env->IsInstanceOf(o, class_java_lang_string)) {
		jstring js = (jstring) o;
		v->vt = VT_BSTR;
		int len = env->GetStringLength(js);
		const jchar* jc = env->GetStringChars(js, NULL);
		v->bstrVal = SysAllocStringLen((OLECHAR*) jc, len);
		env->ReleaseStringChars(js, jc);
    } else if (env->IsInstanceOf(o, class_IDispatch)) {
		v->vt = VT_DISPATCH;
		v->pdispVal = (IDispatch*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_IUnknown)) {
		v->vt = VT_UNKNOWN;
		v->punkVal = (IUnknown*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_java_lang_boolean)) {
		v->vt = VT_BOOL;
		v->boolVal = env->CallBooleanMethod(o, booleanValue);
    } else if (env->IsInstanceOf(o, class_java_lang_integer)) {
		v->vt = VT_I4;
		v->lVal = env->CallIntMethod(o, intValue);
    } else if (env->IsInstanceOf(o, class_java_lang_byte)) {
		v->vt = VT_UI1;
		v->bVal = (BYTE) env->CallByteMethod(o, byteValue);
    } else if (env->IsInstanceOf(o, class_java_lang_short)) {
		v->vt = VT_I2;
		v->iVal = env->CallShortMethod(o, shortValue);
    } else if (env->IsInstanceOf(o, class_java_lang_long)) {
		v->vt = VT_I8;
		v->llVal = env->CallLongMethod(o, longValue);
    } else if (env->IsInstanceOf(o, class_java_lang_double)) {
		v->vt = VT_R8;
		v->dblVal = env->CallDoubleMethod(o, doubleValue);
    } else if (env->IsInstanceOf(o, class_java_lang_float)) {
		v->vt = VT_R4;
		v->fltVal = env->CallFloatMethod(o, floatValue);
    } else if (env->IsInstanceOf(o, class_RefString)) {
		v->vt = VT_BSTR | VT_BYREF;
		v->pbstrVal = (BSTR*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_RefInt)) {
		v->vt = VT_I4 | VT_BYREF;
		v->pintVal = (INT*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_RefByte)) {
		v->vt = VT_UI1 | VT_BYREF;
		v->pbVal = (BYTE*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_RefShort)) {
		v->vt = VT_I2 | VT_BYREF;
		v->piVal = (SHORT*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_RefLong)) {
		v->vt = VT_I8 | VT_BYREF;
		v->pllVal = (LONGLONG*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_RefDouble)) {
		v->vt = VT_R8 | VT_BYREF;
		v->pdblVal = (DOUBLE*) getPtr(env, o);
    } else if (env->IsInstanceOf(o, class_RefFloat)) {
		v->vt = VT_R4 | VT_BYREF;
		v->pfltVal = (FLOAT*) getPtr(env, o);
    } else {
		//
		// currently do not support safearray, CY, DATE, ERROR.
		// 
		wchar_t *pobjstr = jobject_to_string(env, o);
		throw_DispatchException(env, L"%s is not yet supported.", pobjstr);
		free(pobjstr);
		return 0;
    }
	
    return 1;
}

jobject v2j(JNIEnv* env, jobject jidisp, VARIANT* v)
{
	if (v->vt & VT_ARRAY) {
		return jc_make_array(env, jidisp, v);
	}
    if (v->vt & VT_BYREF) {
		// TODO
		return NULL;
    }
	
    switch (v->vt) {
      case VT_NULL:
       return NULL;
	   
      case VT_BOOL:
       return env->NewObject(class_java_lang_boolean,
							 booleanConstructor,
							 v->boolVal ? JNI_TRUE : JNI_FALSE);
	   
      case VT_BSTR:
      {
		  int len = SysStringLen(v->bstrVal);
		  return env->NewString((const jchar*) v->bstrVal, len);
      }
	  
      case VT_I1:
       return env->NewObject(class_java_lang_byte,
							 byteConstructor, (jbyte) v->cVal);
      case VT_I2:
       return env->NewObject(class_java_lang_short,
							 shortConstructor, (jshort) v->iVal);
      case VT_INT:
       return env->NewObject(class_java_lang_integer,
							 intConstructor, (jint) v->intVal);
      case VT_I4:
       return env->NewObject(class_java_lang_integer,
							 intConstructor, (jint) v->lVal);
      case VT_I8:
       return env->NewObject(class_java_lang_long,
							 longConstructor, (jlong) v->llVal);
	   
      case VT_R4:
       return env->NewObject(class_java_lang_float,
							 floatConstructor, (jfloat) v->fltVal);
      case VT_R8:
       return env->NewObject(class_java_lang_double,
							 doubleConstructor, (jdouble) v->dblVal);
	   
	   
      case VT_DISPATCH:
       return create_IDispatch(env, jidisp, v->pdispVal);
       
      case VT_UNKNOWN:
       return create_IUnknown(env, jidisp, v->punkVal);
	   
      case VT_UI1:
       return env->NewObject(class_java_lang_byte,
							 byteConstructor, (jbyte) v->bVal);
       /* All unsigned integers other than byte are treated as long in Java. */
      case VT_UI2:
       return env->NewObject(class_java_lang_long,
							 longConstructor, (jlong) v->uiVal);
      case VT_UI4:
       return env->NewObject(class_java_lang_long,
							 longConstructor, (jlong) v->ulVal);
      case VT_UINT:
       return env->NewObject(class_java_lang_long,
							 longConstructor, (jlong) v->uintVal);
	   
      case VT_EMPTY:
       // This should be illegal but it is used for representing void.
       // throw_DispatchException(env, L"Unexpectedly VT_EMPTY is returned!");
       return NULL;
	   
      case VT_CY:
	   throw_DispatchException(env, L"VT_CY is not yet supported.");
	   break;
      case VT_DATE:
	   throw_DispatchException(env, L"VT_DATE is not yet supported.");
	   break;
      case VT_ERROR:
	   throw_DispatchException(env, L"VT_ERROR is not yet supported.");
	   break;
       
      default:
       throw_DispatchException(env, L"Unexpected variant type:%x.", v->vt);
       break;
    }
    return NULL;
}

int
jc_variant_clear(VARIANT* pv, int releaseIUnknown)
{
    switch (pv->vt) {
      case VT_BSTR:
       SysFreeString(pv->bstrVal);
       return 1;
	   
      case VT_DISPATCH:
       if (releaseIUnknown) {
           IDispatch* pdispVal = pv->pdispVal;
           if (pdispVal) {
               pdispVal->Release();
           }
       }
       return 1;
      case VT_UNKNOWN:
       if (releaseIUnknown) {
           IUnknown* punkVal = pv->punkVal;
           if (punkVal) {
               punkVal->Release();
           }
       }
       return 1;
	   
      case VT_ARRAY:
       SafeArrayDestroy(pv->parray);
       return 0;
    }
    return 1;
}

