
#include "DNFFmpeg.h"

#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, "JNI", __VA_ARGS__);

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_ffmpegdemo_DNPlayer_stringFromJNI(JNIEnv *env, jobject thiz) {
    std::string hello = "Hello from C++";
    std::string s3;
    s3.append(hello); // 将s1转换为string并添加到s3末尾
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_newFun(JNIEnv *env, jobject thiz, jintArray array,
                                            jobjectArray strArray) {
    jsize size = env->GetArrayLength(strArray);
    LOG("测试内容 = %d\n", size);
    for (int i = 0; i < size; ++i) {
        //强转为jstring类型
        jstring str = static_cast<jstring>(env->GetObjectArrayElement(strArray, i));

        const char *s = env->GetStringUTFChars(str, 0);
        LOG("字符串=%s\n", s);
        env->ReleaseStringUTFChars(str, s);
    }
    std::string hello = "Hello from C++";

}
JavaVM *_vm = 0;
extern "C"
JNIEXPORT int JNICALL JNI_OnLoad(JavaVM *vm, void *r) {
    _vm = vm;
    return JNI_VERSION_1_6;
}


DNFFmpeg *dnFFmpeg = 0;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_native_1prepare(JNIEnv *env, jobject thiz,
                                                     jstring data_source) {
    const char *dataSource = env->GetStringUTFChars(data_source, 0);
    JavaCallHelper *callHelper = new JavaCallHelper(_vm, env, thiz);
    dnFFmpeg = new DNFFmpeg(callHelper, dataSource);
    dnFFmpeg->prepare();
    env->ReleaseStringChars(data_source, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_native_1start(JNIEnv *env, jobject thiz) {

    dnFFmpeg->start();
}