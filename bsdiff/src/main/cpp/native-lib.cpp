#include <jni.h>

//
// Created by yanghai on 2023/3/23.
//
extern "C" {
extern int main(int argc, char *argv[]);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_bsdiff_MainActivity_patch(JNIEnv *env, jobject thiz, jstring _oldapk,
                                           jstring _patch, jstring _output) {

    const char *oldapk = env->GetStringUTFChars(_oldapk, 0);
    const char *patch = env->GetStringUTFChars(_patch, 0);
    const char *output = env->GetStringUTFChars(_output, 0);
    char *argv[] = {
            "",
            const_cast<char *>(oldapk),
            const_cast<char *>(output),
            const_cast<char *>(patch),
    };
    main(4, argv);
    env->ReleaseStringUTFChars(_oldapk, oldapk);
    env->ReleaseStringUTFChars(_patch, patch);
    env->ReleaseStringUTFChars(_output, output);

}