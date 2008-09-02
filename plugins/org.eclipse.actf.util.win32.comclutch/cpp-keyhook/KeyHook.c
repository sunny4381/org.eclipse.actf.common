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

#include <windows.h>
#include <jni.h>
#include "KeyHook.h"
#include "org_eclipse_actf_util_win32_keyhook_impl_KeyHookImpl.h"

static HHOOK hKeyboardHook;

typedef struct KeyFilterExtry KeyFilterEntry;
typedef struct KeyFilterExtry {
    int vkey;
    int modifier;
    int idx;
    KeyFilterEntry *pnext;
} KeyFilterEntry;

/* prime number */
#define HASHSIZE 107
static KeyFilterEntry* hash[HASHSIZE];
static int filterAllKeyFlag = 0;

static int
hashValue(KeyFilterEntry* pent)
{
    return (pent->vkey | (pent->modifier << 16)) % HASHSIZE;
}

static int
isEqualKeyFilter(KeyFilterEntry *pent1, KeyFilterEntry *pent2)
{
    return ((pent1->vkey == pent2->vkey)
	    && (pent1->modifier == pent2->modifier));
}

static void
putHash(KeyFilterEntry* pent)
{
    int val = hashValue(pent);
    KeyFilterEntry* pp = hash[val];
    for (pp = hash[val]; pp; pp = pp->pnext) {
	if (isEqualKeyFilter(pent, pp)) {
	    pp->vkey = pent->vkey;
	    pp->modifier = pent->modifier;
	    pp->idx = pent->idx;
	    free(pent);
	    return;
	}
    }
    pent->pnext = hash[val];
    hash[val] = pent;
}

static KeyFilterEntry*
getHash(KeyFilterEntry* pent)
{
    int val = hashValue(pent);
    KeyFilterEntry *pp;
    for (pp = hash[val]; pp; pp = pp->pnext) {
	if (isEqualKeyFilter(pent, pp)) return pp;
    }
    return NULL;
}

static const int SHIFT_MASK = 1;
static const int CTRL_MASK = 2;
static const int ALT_MASK = 8;

static JavaVM *jvm;
static jclass keyHookImplClass;
static jmethodID hookedKeyEntryID;

static int filterationStopped = 0;

void stopFilter()
{
    filterationStopped = 1;
}

void startFilter()
{
    filterationStopped = 0;
}

int filter(int idx, int vkey, int modifier, jboolean isUp)
{
    JNIEnv *env;
    jboolean r;
    // jclass cls;

    if ((*jvm)->GetEnv(jvm, (void**) &env, JNI_VERSION_1_2) != JNI_OK) return 0;
    r = (*env)->CallStaticBooleanMethod(env, keyHookImplClass, hookedKeyEntryID,
					(jint) idx, (jint) vkey, (jint) modifier, isUp);
#ifdef DEBUG    
    fprintf(stderr, "Filtered Key:%d, %d\n", pent->vkey, pent->modifier);
    fflush(stderr);
#endif
    return r;
}

LRESULT CALLBACK
KeyboardProc(int code, WPARAM wParam, LPARAM lParam)
{
    int vkey, modifier;
    KeyFilterEntry ent;
    KeyFilterEntry *pent;
    jboolean isUp;

    if ((code != HC_ACTION) || filterationStopped)
	return CallNextHookEx(hKeyboardHook, code, wParam, lParam);

#ifdef DEBUG
    fprintf(stderr, "Keyboard Hooked:%d, %d\n", wParam, lParam);
    fflush(stderr);
#endif

    vkey = (int) wParam;
    modifier = 0;
    if (GetKeyState(VK_SHIFT) < 0) {
	modifier |= SHIFT_MASK;
    }
    if (GetKeyState(VK_CONTROL) < 0) {
	modifier |= CTRL_MASK;
    }
    if (GetKeyState(VK_MENU) < 0) {
	modifier |= ALT_MASK;
    }
    ent.vkey = vkey;
    ent.modifier = modifier;
    if (lParam < 0) {
	isUp = JNI_TRUE;
    } else {
	isUp = JNI_FALSE;
    }
    if (filterAllKeyFlag) {
	pent = NULL;
	if (filter(0, vkey, modifier, isUp)) return 1;
    } else {
	pent = getHash(&ent);
	if (!pent) return CallNextHookEx(hKeyboardHook, code, wParam, lParam);
	if (filter(pent->idx, vkey, modifier, isUp)) return 1;
    }
    return CallNextHookEx(hKeyboardHook, code, wParam, lParam);
}


void JNICALL
Java_org_eclipse_actf_util_win32_keyhook_impl_KeyHookImpl_initialize
(JNIEnv* env, jobject thisObj, jlong hwnd)
{
    HINSTANCE hInst;
    jclass cls;
    DWORD threadId = GetCurrentThreadId();

#ifdef DEBUG    
    fprintf(stderr, "Hook initialization\n");
#endif

    memset(hash, 0, sizeof(hash));
    hInst = (HINSTANCE) GetModuleHandle(0);
    hKeyboardHook = SetWindowsHookEx(WH_KEYBOARD, KeyboardProc, NULL, threadId);
#ifdef DEBUG    
    fprintf(stderr, "Hook result:%d\n", hKeyboardHook);
    fflush(stderr);
#endif

    (*env)->GetJavaVM(env, &jvm);
    keyHookImplClass = (*env)->GetObjectClass(env, thisObj);
    keyHookImplClass = (*env)->NewGlobalRef(env, keyHookImplClass);
    hookedKeyEntryID = (*env)->GetStaticMethodID(env, keyHookImplClass, "hookedKeyEntry", "(IIIZ)Z");

    initialize_WindowHook(hwnd);
}

JNIEXPORT void JNICALL
Java_org_eclipse_actf_util_win32_keyhook_impl_KeyHookImpl_filterKey(JNIEnv *env, jobject thisObj,
								      jint idx, jint vkey, jint mod)
{
    KeyFilterEntry* pent = (KeyFilterEntry*) malloc(sizeof(KeyFilterEntry));
    memset(pent, 0, sizeof(KeyFilterEntry));
    pent->vkey = (int) vkey;
    pent->modifier = (int) mod;
    pent->idx = idx;
#ifdef DEBUG    
    fprintf(stderr, "filter key:%d, %d \n", vkey, mod);
#endif
    putHash(pent);
}

JNIEXPORT void JNICALL
Java_org_eclipse_actf_util_win32_keyhook_impl_KeyHookImpl_filterAllKey(JNIEnv *env, jobject thisObj,
									 jboolean flag)
{
    filterAllKeyFlag = flag;
}
