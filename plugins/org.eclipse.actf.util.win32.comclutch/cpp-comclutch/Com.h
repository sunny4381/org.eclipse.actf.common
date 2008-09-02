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
#ifndef __COM_H__
#define __COM_H__
#include <jni.h>
#include <atlbase.h>
#include <atlconv.h>

extern const char classname_IDispatch[];
extern const char classname_IUnknown[];
extern const char classname_IResource[];
extern const char classname_IOleContainer[];
extern const char classname_IEnumUnknown[];
extern const char classname_DispatchException[];

extern const char classname_RefContainer[];
extern const char classname_RefString[];
extern const char classname_RefByte[];
extern const char classname_RefShort[];
extern const char classname_RefInt[];
extern const char classname_RefLong[];
extern const char classname_RefFloat[];
extern const char classname_RefDouble[];

extern jclass class_IDispatch;
extern jclass class_IUnknown;
extern jclass class_IResource;
extern jclass class_IOleContainer;
extern jclass class_IEnumUnknown;
extern jclass class_DispatchException;

extern jclass class_RefContainer;
extern jclass class_RefString;
extern jclass class_RefByte;
extern jclass class_RefShort;
extern jclass class_RefInt;
extern jclass class_RefLong;
extern jclass class_RefFloat;
extern jclass class_RefDouble;


extern jclass class_java_lang_object;
extern jclass class_java_lang_string;
extern jclass class_java_lang_boolean;
extern jclass class_java_lang_byte;
extern jclass class_java_lang_short;
extern jclass class_java_lang_integer;
extern jclass class_java_lang_long;
extern jclass class_java_lang_float;
extern jclass class_java_lang_double;

extern jmethodID toString;
extern jmethodID booleanValue;
extern jmethodID booleanConstructor;
extern jmethodID byteValue;
extern jmethodID byteConstructor;
extern jmethodID shortValue;
extern jmethodID shortConstructor;
extern jmethodID intValue;
extern jmethodID intConstructor;
extern jmethodID longValue;
extern jmethodID longConstructor;
extern jmethodID floatValue;
extern jmethodID floatConstructor;
extern jmethodID doubleValue;
extern jmethodID doubleConstructor;

extern jmethodID getPtrMethod;

extern jmethodID newIUnknown;
extern jmethodID newIDispatch;

/* Common.cpp */
extern void GUID_from_uuidbits(jlong juuidmsb, jlong juuidlsb, GUID *pguid);
extern void throw_DispatchException(JNIEnv* env, const wchar_t* fmt, ...);
extern int check_hresult(JNIEnv* env, HRESULT r, EXCEPINFO *pexcepInfo);
extern void* getPtr(JNIEnv* env, jobject o);
extern wchar_t* jobject_to_string(JNIEnv* env, jobject o);
extern JNIEnv* getEnv();

/* JCTypeBridge.cpp */
extern int j2v(JNIEnv* env, jobject o, VARIANT* v);
extern jobject v2j(JNIEnv* env, jobject jidisp, VARIANT* v);
extern int jc_variant_clear(VARIANT* pv, int releaseIUnknown);

/* JCTypeBridgeArray.cpp */
extern jobject jc_make_array(JNIEnv* env, jobject jidisp, VARIANT* v);

/* IUnknown.cpp */
extern jobject create_IUnknown(JNIEnv* env, jobject jiunk, IUnknown* iunk);
extern int releaseInQueues();
extern int initialize_IUnknown(JNIEnv* env);

/* IDispatch.cpp */
extern jobject create_IDispatch(JNIEnv* env, jobject jidisp, IDispatch* idisp);


#endif /* __COM_H__*/

