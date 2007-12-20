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
#include "org_eclipse_actf_model_flash_proxy_internal_WinInet.h"

JNIEXPORT jint JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_InternetOpenW
  (JNIEnv *env, jclass that, jint lpszAgent, jint dwAccessType, jint lpszProxy, jint lpszProxyBypass, jint dwFlags) 
{
	return (jint)InternetOpenW((LPWSTR)lpszAgent, dwAccessType, (LPWSTR)lpszProxy, (LPWSTR)lpszProxyBypass, dwFlags);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_InternetCloseHandle
  (JNIEnv *env, jclass that, jint hInternet)
{
	return InternetCloseHandle((HINTERNET)hInternet);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_InternetSetOptionW
  (JNIEnv *env, jclass that, jint hInternet, jint dwOption, jint lpBuffer, jint dwBufferLength)
{
	return InternetSetOptionW((HINTERNET)hInternet, dwOption, (LPVOID)lpBuffer, dwBufferLength);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_InternetQueryOptionW
  (JNIEnv *env, jclass that, jint hInternet, jint dwOption, jint lpBuffer, jintArray lpdwBufferLength)
{
	jint *lpNativeLength = NULL;
	if( lpdwBufferLength ) {
		lpNativeLength = env->GetIntArrayElements(lpdwBufferLength,NULL);
	}
	jboolean rc = InternetQueryOptionW((HINTERNET)hInternet, dwOption, (LPVOID)lpBuffer, (LPDWORD)lpNativeLength);
	if( lpdwBufferLength && lpNativeLength ) {
		env->ReleaseIntArrayElements(lpdwBufferLength,lpNativeLength,0);
	}
	return rc;
}

JNIEXPORT jint JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_FindFirstUrlCacheEntryW
  (JNIEnv *env, jclass that, jint lpszUrlSearchPattern, jint lpFirstCacheEntryInfo, jintArray lpcbCacheEntryInfo)
{
	jint *lpNativeLength = NULL;
	if( lpcbCacheEntryInfo ) {
		lpNativeLength = env->GetIntArrayElements(lpcbCacheEntryInfo,NULL);
	}
	HANDLE handle = FindFirstUrlCacheEntryW((LPWSTR)lpszUrlSearchPattern, (LPINTERNET_CACHE_ENTRY_INFOW)lpFirstCacheEntryInfo, (LPDWORD)lpNativeLength);
	if( lpcbCacheEntryInfo && lpNativeLength ) {
		env->ReleaseIntArrayElements(lpcbCacheEntryInfo,lpNativeLength,0);
	}
	return (jint)handle;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_FindNextUrlCacheEntryW
  (JNIEnv *env, jclass that, jint hEnumHandle, jint lpNextCacheEntryInfo, jintArray lpcbCacheEntryInfo)
{
	jint *lpNativeLength = NULL;
	if( lpcbCacheEntryInfo ) {
		lpNativeLength = env->GetIntArrayElements(lpcbCacheEntryInfo,NULL);
	}
	BOOL rc = FindNextUrlCacheEntryW((HANDLE)hEnumHandle, (LPINTERNET_CACHE_ENTRY_INFOW)lpNextCacheEntryInfo, (LPDWORD)lpNativeLength);
	if( lpcbCacheEntryInfo && lpNativeLength ) {
		env->ReleaseIntArrayElements(lpcbCacheEntryInfo,lpNativeLength,0);
	}
	return rc;
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_FindCloseUrlCache
  (JNIEnv *env, jclass that, jint hEnumHandle)
{
	return FindCloseUrlCache((HANDLE)hEnumHandle);
}

JNIEXPORT jboolean JNICALL Java_org_eclipse_actf_model_flash_proxy_internal_WinInet_DeleteUrlCacheEntryW
  (JNIEnv *env, jclass that, jint lpszUrlName)
{
	return DeleteUrlCacheEntryW((LPWSTR)lpszUrlName);
}
