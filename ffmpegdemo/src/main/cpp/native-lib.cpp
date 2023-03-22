
#include "DNFFmpeg.h"

#define LOG(...) __android_log_print(ANDROID_LOG_DEBUG, "JNI", __VA_ARGS__);

extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

#include "android/native_window_jni.h"
#include <string>

#include "macro.h"

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
    LOGE("测试内容 = %d\n", size);
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
ANativeWindow *window = 0;
DNFFmpeg *dnFFmpeg = 0;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
extern "C"
JNIEXPORT int JNICALL JNI_OnLoad(JavaVM *vm, void *r) {
    _vm = vm;
    return JNI_VERSION_1_6;
}


//画画
void render(uint8_t *data, int lineszie, int w, int h) {
    pthread_mutex_lock(&mutex);
    if (!window) {
        pthread_mutex_unlock(&mutex);
        return;
    }
    //设置窗口属性
    ANativeWindow_setBuffersGeometry(window, w,
                                     h,
                                     WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer window_buffer;
    if (ANativeWindow_lock(window, &window_buffer, 0)) {
        ANativeWindow_release(window);
        window = 0;
        pthread_mutex_unlock(&mutex);
        return;
    }
    //填充rgb数据给dst_data
    uint8_t *dst_data = static_cast<uint8_t *>(window_buffer.bits);
    // stride：一行多少个数据（RGBA） *4
    int dst_linesize = window_buffer.stride * 4;
    //一行一行的拷贝
    for (int i = 0; i < window_buffer.height; ++i) {
        //memcpy(dst_data , data, dst_linesize);
        memcpy(dst_data + i * dst_linesize, data + i * lineszie, dst_linesize);
    }
    ANativeWindow_unlockAndPost(window);
    pthread_mutex_unlock(&mutex);
}

JavaCallHelper *callHelper;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_native_1prepare(JNIEnv *env, jobject thiz,
                                                     jstring data_source) {
    const char *dataSource = env->GetStringUTFChars(data_source, 0);
    callHelper = new JavaCallHelper(_vm, env, thiz);
    dnFFmpeg = new DNFFmpeg(callHelper, dataSource);
    dnFFmpeg->prepare();
    dnFFmpeg->setRenderFrameCallback(render);
    env->ReleaseStringChars(data_source, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_native_1start(JNIEnv *env, jobject thiz) {
    dnFFmpeg->start();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_native_1setSurface(JNIEnv *env, jobject thiz,
                                                        jobject surface) {
    pthread_mutex_lock(&mutex);
    if (window) {
        ANativeWindow_release(window);
        window = 0;
    }
    window = ANativeWindow_fromSurface(env, surface);
    pthread_mutex_unlock(&mutex);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_ffmpegdemo_DNPlayer_native_1stop(JNIEnv *env, jobject thiz) {
    if (dnFFmpeg) {
        dnFFmpeg->stop();
    }
}
