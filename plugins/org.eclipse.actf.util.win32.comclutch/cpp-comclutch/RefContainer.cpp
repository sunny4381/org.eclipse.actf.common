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
#include "org_eclipse_actf_util_win32_comclutch_RefContainer.h"


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _free
 * Signature: (J)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1free
  (JNIEnv *env, jobject obj, jlong ptr)
{
    free((void*) ptr); 
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _calloc
 * Signature: (I)J
 */
jlong JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1calloc
  (JNIEnv *env, jobject obj, jint size)
{
    void* ptr = calloc(1, size);
    return (jlong) ptr;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByBoolean
 * Signature: (J)Z
 */
jboolean JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByBoolean
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jboolean*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByByte
 * Signature: (J)B
 */
jbyte JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByByte
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jbyte*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByShort
 * Signature: (J)S
 */
jshort JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByShort
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jshort*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByInt
 * Signature: (J)I
 */
jint JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByInt
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jint*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByLong
 * Signature: (J)J
 */
jlong JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByLong
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jlong*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByFloat
 * Signature: (J)F
 */
jfloat JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByFloat
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jfloat*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByDouble
 * Signature: (J)D
 */
jdouble JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByDouble
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jdouble*) ptr);
}


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByIUnknown
 * Signature: (J)J
 */
jlong JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByIUnknown
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jlong*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByObject
 * Signature: (J)Ljava/lang/Object;
 */
jobject JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByObject
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jobject*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByVoidPtr
 * Signature: (J)J
 */
jlong JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByVoidPtr
  (JNIEnv *env, jobject obj, jlong ptr){
  return *((jlong*) ptr);
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _getValueByString
 * Signature: (J)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1getValueByString
  (JNIEnv *env, jobject obj, jlong ptr) {
  int len = strlen((char *) ptr);
  return env->NewString((jchar *) ptr, len); 
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForBoolean
 * Signature: (JZ)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForBoolean
  (JNIEnv *env, jobject obj, jlong ptr, jboolean value){
  *((jboolean*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForByte
 * Signature: (JB)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForByte
  (JNIEnv *env, jobject obj, jlong ptr, jbyte value){
  *((jbyte*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForShort
 * Signature: (JS)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForShort
  (JNIEnv *env, jobject obj, jlong ptr, jshort value){
  *((jshort*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForInt
 * Signature: (JI)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForInt
  (JNIEnv *env, jobject obj, jlong ptr, jint value){
  *((jint*) ptr) = value;
}


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForLong
 * Signature: (JJ)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForLong
  (JNIEnv *env, jobject obj, jlong ptr, jlong value){
  *((jlong*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForFloat
 * Signature: (JF)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForFloat
  (JNIEnv *env, jobject obj, jlong ptr, jfloat value){
  *((jfloat*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForDouble
 * Signature: (JD)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForDouble
  (JNIEnv *env, jobject obj, jlong ptr, jdouble value){
  *((jdouble*) ptr) = value;
}


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForIUnknown
 * Signature: (JJ)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForIUnknown
  (JNIEnv *env, jobject obj, jlong ptr, jlong value){
  *((jlong*) ptr) = value;
}


/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForObject
 * Signature: (JLjava/lang/Object;)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForObject
  (JNIEnv *env, jobject obj, jlong ptr, jobject value){
  *((jobject*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForVoidPtr
 * Signature: (JJ)V
 */
void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForVoidPtr
  (JNIEnv *env, jobject obj, jlong ptr, jlong value){
  *((jlong*) ptr) = value;
}

/*
 * Class:     org_eclipse_actf_util_win32_comclutch_RefContainer
 * Method:    _setValueForString
 * Signature: (JLjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_org_eclipse_actf_util_win32_comclutch_RefContainer__1setValueForString
  (JNIEnv *env, jobject obj, jlong ptr, jstring str)
 {
   *((char**) ptr) = (char*) env->GetStringChars(str, NULL);
 }
