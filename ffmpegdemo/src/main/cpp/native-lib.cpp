#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
//com.example.ffmpegdemo
Java_com_example_ffmpegdemo_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}