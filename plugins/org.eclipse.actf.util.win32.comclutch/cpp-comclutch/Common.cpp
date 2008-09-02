/*******************************************************************************
 * Copyright (c) 2007 IBM Corporation and Others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Hisashi MIYASHITA - initial API and implementation
 *    Daisuke SATO - initial API and implementation
 *******************************************************************************/
/*
  Common library
*/

#include <stdarg.h>
#include "org_eclipse_actf_util_win32_comclutch_ComService.h"
#include "Com.h"

#define BASECLASS(name) "org/eclipse/actf/util/win32/comclutch/" #name


const char classname_IDispatch[] = BASECLASS(IDispatch);
const char classname_IUnknown[] = BASECLASS(IUnknown);
const char classname_IResource[] = BASECLASS(IResource);
const char classname_IOleContainer[] = BASECLASS(IOleContainer);
const char classname_IEnumUnknown[] = BASECLASS(IEnumUnknown);
const char classname_DispatchException[] = BASECLASS(DispatchException);

const char classname_RefContainer[] = BASECLASS(RefContainer);
const char classname_RefString[] = BASECLASS(RefString);
const char classname_RefByte[] = BASECLASS(RefByte);
const char classname_RefShort[] = BASECLASS(RefShort);
const char classname_RefInt[] = BASECLASS(RefInt);
const char classname_RefLong[] = BASECLASS(RefLong);
const char classname_RefFloat[] = BASECLASS(RefFloat);
const char classname_RefDouble[] = BASECLASS(RefDouble);

static JavaVM *jvm;

jclass class_IDispatch;
jclass class_IUnknown;
jclass class_IResource;
jclass class_IOleContainer;
jclass class_IEnumUnknown;
jclass class_DispatchException;

jclass class_RefContainer;
jclass class_RefString;
jclass class_RefByte;
jclass class_RefShort;
jclass class_RefInt;
jclass class_RefLong;
jclass class_RefFloat;
jclass class_RefDouble;

jclass class_java_lang_object;
jclass class_java_lang_string;
jclass class_java_lang_boolean;
jclass class_java_lang_byte;
jclass class_java_lang_short;
jclass class_java_lang_integer;
jclass class_java_lang_long;
jclass class_java_lang_float;
jclass class_java_lang_double;

jmethodID toString;
jmethodID booleanValue;
jmethodID booleanConstructor;
jmethodID byteValue;
jmethodID byteConstructor;
jmethodID shortValue;
jmethodID shortConstructor;
jmethodID intValue;
jmethodID intConstructor;
jmethodID longValue;
jmethodID longConstructor;
jmethodID floatValue;
jmethodID floatConstructor;
jmethodID doubleValue;
jmethodID doubleConstructor;

jmethodID getPtrMethod;

jmethodID newIUnknown;
jmethodID newIDispatch;
jmethodID newIOleContainer;
jmethodID newIEnumUnknown;

static jclass
initialize_class_reference(JNIEnv* env,
			   const char* classname)
{
    jclass c;

    c = env->FindClass(classname);
    if (!c) return NULL;
    return (jclass) env->NewGlobalRef(c);
}

static jmethodID
initialize_method_reference(JNIEnv* env,
			    jclass c,
			    const char* methodname,
			    const char* argSignature)
{
    jmethodID m;

    m = env->GetMethodID(c, methodname, argSignature);
    return m;
}

int initialize_Common(JNIEnv* env)
{
    env->GetJavaVM(&jvm);

    class_DispatchException = initialize_class_reference(env, classname_DispatchException);
    if (!class_DispatchException) return 0;

    class_IDispatch = initialize_class_reference(env, classname_IDispatch);
    if (!class_IDispatch) return 0;
    newIDispatch = initialize_method_reference(env, class_IDispatch,
					       "newIDispatch",
					       "(J)L" BASECLASS(IDispatch) ";");
    class_IUnknown = initialize_class_reference(env, classname_IUnknown);
    if (!class_IUnknown) return 0;
    newIUnknown = initialize_method_reference(env, class_IUnknown,
					      "newIUnknown",
					      "(J)L" BASECLASS(IUnknown) ";");

    class_IOleContainer = initialize_class_reference(env, classname_IOleContainer);
    if (!class_IOleContainer) return 0;

    class_IEnumUnknown = initialize_class_reference(env, classname_IEnumUnknown);
    if (!class_IEnumUnknown) return 0;
    
    class_IResource = initialize_class_reference(env, classname_IResource);
    if (!class_IResource) return 0;
    getPtrMethod = initialize_method_reference(env, class_IResource,
					       "getPtr", "()J");

    class_RefContainer = initialize_class_reference(env, classname_RefContainer);
    if (!class_RefContainer) return 0;
    class_RefString = initialize_class_reference(env, classname_RefString);
    if (!class_RefString) return 0;
    class_RefByte = initialize_class_reference(env, classname_RefByte);
    if (!class_RefByte) return 0;
    class_RefShort = initialize_class_reference(env, classname_RefShort);
    if (!class_RefShort) return 0;
    class_RefInt = initialize_class_reference(env, classname_RefInt);
    if (!class_RefInt) return 0;
    class_RefLong = initialize_class_reference(env, classname_RefLong);
    if (!class_RefLong) return 0;
    class_RefFloat = initialize_class_reference(env, classname_RefFloat);
    if (!class_RefFloat) return 0;
    class_RefDouble = initialize_class_reference(env, classname_RefDouble);
    if (!class_RefDouble) return 0;
    
    class_java_lang_object = initialize_class_reference(env, "java/lang/Object");
    if (!class_java_lang_object) return 0;
    toString = initialize_method_reference(env, class_java_lang_object,
					   "toString", "()Ljava/lang/String;");
    if (!toString) return 0;

    class_java_lang_string = initialize_class_reference(env, "java/lang/String");
    if (!class_java_lang_string) return 0;

    class_java_lang_boolean = initialize_class_reference(env, "java/lang/Boolean");
    if (!class_java_lang_boolean) return 0;
    booleanValue = initialize_method_reference(env, class_java_lang_boolean,
					       "booleanValue", "()Z");
    if (!booleanValue) return 0;
    booleanConstructor = initialize_method_reference(env, class_java_lang_boolean,
						     "<init>", "(Z)V");
    if (!booleanConstructor) return 0;

    class_java_lang_byte = initialize_class_reference(env, "java/lang/Byte");
    if (!class_java_lang_byte) return 0;
    byteValue = initialize_method_reference(env, class_java_lang_byte,
					    "byteValue", "()B");
    if (!byteValue) return 0;
    byteConstructor = initialize_method_reference(env, class_java_lang_byte,
						  "<init>", "(B)V");
    if (!byteConstructor) return 0;


    class_java_lang_short = initialize_class_reference(env, "java/lang/Short");
    if (!class_java_lang_short) return 0;
    shortValue = initialize_method_reference(env, class_java_lang_short,
					     "shortValue", "()S");
    if (!shortValue) return 0;
    shortConstructor = initialize_method_reference(env, class_java_lang_short,
						  "<init>", "(S)V");
    if (!shortConstructor) return 0;

    class_java_lang_integer = initialize_class_reference(env, "java/lang/Integer");
    if (!class_java_lang_integer) return 0;
    intValue = initialize_method_reference(env, class_java_lang_integer,
					   "intValue", "()I");
    if (!intValue) return 0;
    intConstructor = initialize_method_reference(env, class_java_lang_integer,
						 "<init>", "(I)V");
    if (!intConstructor) return 0;

    class_java_lang_long = initialize_class_reference(env, "java/lang/Long");
    if (!class_java_lang_long) return 0;
    longValue = initialize_method_reference(env, class_java_lang_long,
					   "longValue", "()J");
    if (!longValue) return 0;
    longConstructor = initialize_method_reference(env, class_java_lang_long,
						  "<init>", "(J)V");
    if (!longConstructor) return 0;

    class_java_lang_float = initialize_class_reference(env, "java/lang/Float");
    if (!class_java_lang_float) return 0;
    floatValue = initialize_method_reference(env, class_java_lang_float,
					     "floatValue", "()F");
    if (!floatValue) return 0;
    floatConstructor = initialize_method_reference(env, class_java_lang_float,
						   "<init>", "(F)V");
    if (!floatConstructor) return 0;

    class_java_lang_double = initialize_class_reference(env, "java/lang/Double");
    if (!class_java_lang_double) return 0;
    doubleValue = initialize_method_reference(env, class_java_lang_double,
					      "doubleValue", "()D");
    if (!doubleValue) return 0;
    doubleConstructor = initialize_method_reference(env, class_java_lang_double,
						    "<init>", "(D)V");
    if (!doubleConstructor) return 0;

    return 1;
}

void
GUID_from_uuidbits(jlong juuidmsb, jlong juuidlsb, GUID *pguid)
{
    unsigned long long guidmsb = (unsigned long long) juuidmsb;
    unsigned long long guidlsb = (unsigned long long) juuidlsb;
    pguid->Data1 = (guidmsb >> 32) & 0xFFFFFFFF;
    pguid->Data2 = (guidmsb >> 16) & 0xFFFF;
    pguid->Data3 = guidmsb & 0xFFFF;
    pguid->Data4[0] = (guidlsb >> 56) & 0xFF;
    pguid->Data4[1] = (guidlsb >> 48) & 0xFF;
    pguid->Data4[2] = (guidlsb >> 40) & 0xFF;
    pguid->Data4[3] = (guidlsb >> 32) & 0xFF;
    pguid->Data4[4] = (guidlsb >> 24) & 0xFF;
    pguid->Data4[5] = (guidlsb >> 16) & 0xFF;
    pguid->Data4[6] = (guidlsb >> 8) & 0xFF;
    pguid->Data4[7] = guidlsb & 0xFF;
}

wchar_t*
jobject_to_string(JNIEnv* env, jobject o)
{
    jstring js = (jstring) env->CallObjectMethod(o, toString);
    int len = env->GetStringLength(js);
    wchar_t* pret = (wchar_t*) malloc(sizeof(wchar_t) * (len + 1));
    const jchar* jc = env->GetStringChars(js, NULL);
    memcpy(pret, jc, len * sizeof(wchar_t));
    env->ReleaseStringChars(js, jc);
    pret[len] = 0;
    wprintf(L"Obj:%s\n", pret);
    fflush(stdout);
    return pret;
}

void
throw_DispatchException(JNIEnv* env, const wchar_t* fmt, ...)
{
    wchar_t message[1024];
    va_list vl;
    va_start(vl, fmt);
    int len = _vsnwprintf(message, sizeof(message) / sizeof(wchar_t) - 1, fmt, vl);

    jmethodID constructor = env->GetMethodID(class_DispatchException,
					     "<init>", "(Ljava/lang/String;)V");
    jthrowable ex = (jthrowable) env->NewObject(class_DispatchException, constructor,
						env->NewString((jchar*) message, len));
    env->Throw(ex);
}

int
check_hresult(JNIEnv* env, HRESULT r, EXCEPINFO *pexcepInfo)
{
    if (FAILED(r)) {
    
    if (r == DISP_E_EXCEPTION && pexcepInfo != NULL) {
		throw_DispatchException(env, L"Failed, FACILITY:%d, CODE:%d. (%x) %s",
					HRESULT_FACILITY(r), HRESULT_CODE(r), r, pexcepInfo->bstrDescription);
	} else {
		throw_DispatchException(env, L"Failed, FACILITY:%d, CODE:%d. (%x) ",
					HRESULT_FACILITY(r), HRESULT_CODE(r), r);
	}

	return 0;
    }
    if (r == S_FALSE) {
	throw_DispatchException(env, L"S_FALSE is returned.");
	return 0;
    }
    return 1;
}

void*
getPtr(JNIEnv* env, jobject o)
{
    return (void*) env->CallLongMethod(o, getPtrMethod);
}

JNIEnv*
getEnv()
{
    JNIEnv* env;
    if (jvm->GetEnv((void**) &env, JNI_VERSION_1_2) != JNI_OK) return NULL;
    return env;
}


void JNICALL
Java_org_eclipse_actf_util_win32_comclutch_ComService__1initialize
(JNIEnv* env, jclass cls)
{
    if (!initialize_Common(env)) return;
    if (!initialize_IUnknown(env)) return;
    HRESULT hr = CoInitialize(NULL);
    if ((hr != S_OK) && (hr != S_FALSE)) {
	if (!check_hresult(env, hr, NULL)) return;
    }
}

void JNICALL
Java_org_eclipse_actf_util_win32_comclutch_ComService__1uninitialize
(JNIEnv* env, jclass cls)
{
    CoUninitialize();
}


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_ComService
 * Method:    _coCreateInstance
 * Signature: (Ljava/lang/String;I)J
 */
JNIEXPORT jlong JNICALL Java_org_eclipse_actf_util_win32_comclutch_ComService__1coCreateInstance
  (JNIEnv *env, jclass cls, jstring js, jint dwClsContext)
{  
    wchar_t* str = jobject_to_string(env, js);
    
    IUnknown *ptr;
    CLSID clsid;
    IID iid;
	
    CLSIDFromString(str, &clsid);
    free(str);
	
    CoCreateInstance(clsid, NULL, dwClsContext, IID_IUnknown, (LPVOID *)&ptr);
    
    return (jlong) ptr;
}

