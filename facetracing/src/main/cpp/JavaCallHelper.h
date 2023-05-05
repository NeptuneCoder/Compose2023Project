//
// Created by yanghai on 2023/3/9.
//

#ifndef COMPOSE2023PROJECT_JAVACALLHELPER_H
#define COMPOSE2023PROJECT_JAVACALLHELPER_H

#include <jni.h>
#include "macro.h"

class JavaCallHelper {
public:

    JavaCallHelper(JavaVM *vm, JNIEnv *env, jobject thiz);

    ~JavaCallHelper();

    void OnError(int thread, int errorCode);

    void onPrepase(int threadId);

private:
    JavaVM *vm;
    JNIEnv *env;
    jobject thiz;
    jclass jclazz;
    jmethodID onErrorId;
    jmethodID onPrepaseId;
};

#endif //COMPOSE2023PROJECT_JAVACALLHELPER_H
