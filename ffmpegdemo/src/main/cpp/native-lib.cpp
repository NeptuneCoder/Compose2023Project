
#include "DNFFmpeg.h"


extern "C" {
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
}

#include "android/native_window_jni.h"
#include <string>

#include "macro.h"


JavaVM *_vm = 0;
ANativeWindow *window = 0;
DNFFmpeg *dnFFmpeg = 0;
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
extern "C"
JNIEXPORT int JNICALL JNI_OnLoad(JavaVM *vm, void *r) {
    _vm = vm;
    return JNI_VERSION_1_6;
}


/**
 * 将读取到的数据渲染到window上
 * @param data
 * @param lineszie
 * @param w
 * @param h
 */
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
                                                     jstring url) {
    const char *addUrl = env->GetStringUTFChars(url, 0);
    callHelper = new JavaCallHelper(_vm, env, thiz);
    dnFFmpeg = new DNFFmpeg(callHelper, addUrl);
    dnFFmpeg->prepare();
    dnFFmpeg->setRenderFrameCallback(render);
    env->ReleaseStringChars(url, 0);
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
