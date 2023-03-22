//
// Created by yanghai on 2023/3/21.
//

#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring
JNICALL
Java_com_snw_samllnewweather_jni_JNIHelper_getPrivateKey(JNIEnv *env, jobject thiz) {
    std::string key = "dc418e957f504a0ea777f9e91ae88329";
    return env->NewStringUTF(key.c_str());
}