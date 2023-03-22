//
// Created by yanghai on 2023/3/10.
//
#include "JavaCallHelper.h"
#include "macro.h"

JavaCallHelper::JavaCallHelper(JavaVM *vm, JNIEnv *env, jobject thiz) {
    this->vm = vm;
    this->env = env;
    this->thiz = env->NewGlobalRef(thiz);

    this->jclazz = env->GetObjectClass(thiz);
    this->onErrorId = env->GetMethodID(jclazz, "onError", "(I)V");
    this->onPrepaseId = env->GetMethodID(jclazz, "onPrepase", "()V");
}

JavaCallHelper::~JavaCallHelper() {
    JNIEnv *env;
    vm->AttachCurrentThread(&env, 0);
    env->DeleteGlobalRef(thiz);
    vm->DetachCurrentThread();

}

void JavaCallHelper::OnError(int thread, int errorCode) {
    if (thread == THREAD_MAIN) {
        env->CallVoidMethod(thiz, onErrorId, errorCode);
    } else {
        //子线程中需要vm获得当前线程的env;然后再回调方法
        JNIEnv *curEnv;
        //将当前JNIEnv附加到当前线程
        vm->AttachCurrentThread(&curEnv, 0);
        //然后再根据当前的Env调用方法
        curEnv->CallVoidMethod(thiz, onErrorId, errorCode);
        //在释放掉当前的线程的env
        vm->DetachCurrentThread();
    }
    //TODO 调用java类中的方法
}

void JavaCallHelper::onPrepase(int threadId) {
    if (threadId == THREAD_MAIN) {
        env->CallVoidMethod(thiz, onPrepaseId);
    } else {
        //子线程中需要vm获得当前线程的env;然后再回调方法
        JNIEnv *curEnv;
        //将当前JNIEnv附加到当前线程
        vm->AttachCurrentThread(&curEnv, 0);
        //然后再根据当前的Env调用方法
        curEnv->CallVoidMethod(thiz, onPrepaseId);
        //在释放掉当前的线程的env
        vm->DetachCurrentThread();
    }
}