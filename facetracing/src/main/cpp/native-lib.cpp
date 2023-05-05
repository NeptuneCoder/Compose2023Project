#include <jni.h>

//
// Created by yanghai on 2023/3/26.

#include "android/native_window_jni.h"
#include "JavaCallHelper.h"
#include <string>
#include <pthread.h>
#include "opencv2/opencv.hpp"
#include "FaceTracking.h"

JavaCallHelper *callHelper;
JavaVM *_vm = 0;
ANativeWindow *window = 0;
pthread_mutex_t mutex_t = PTHREAD_MUTEX_INITIALIZER;


extern "C"
JNIEXPORT int JNICALL JNI_OnLoad(JavaVM *vm, void *r) {
    _vm = vm;
    return JNI_VERSION_1_6;
}


DetectorAgregator *result = 0;
extern "C"
JNIEXPORT void JNICALL
Java_com_example_facetracing_MainActivity_native_1init(JNIEnv *env, jobject thiz,
                                                       jstring modelPath) {

    callHelper = new JavaCallHelper(_vm, env, thiz);
    const char *model = env->GetStringUTFChars(modelPath, 0);
    if (result) {
        result->tracker->stop();
        delete result;
        result = 0;
    }
    //加载分类器
    String stdFileName = model;

    cv::Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
    cv::Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
    result = new DetectorAgregator(mainDetector, trackingDetector);
    result->tracker->run();
    env->ReleaseStringUTFChars(modelPath, model);


}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_facetracing_MainActivity_native_1setSurface(JNIEnv *env, jobject thiz,
                                                             jobject surface) {
    pthread_mutex_lock(&mutex_t);
    if (window) {
        ANativeWindow_release(window);
        window = 0;
    }
    window = ANativeWindow_fromSurface(env, surface);
    pthread_mutex_unlock(&mutex_t);
}


/**
 * 将读取到的数据渲染到window上
 * @param data
 * @param lineszie
 * @param w
 * @param h
 */
void render(uint8_t *data, int lineszie, int w, int h) {
    pthread_mutex_lock(&mutex_t);
    if (!window) {
        pthread_mutex_unlock(&mutex_t);
        return;
    }
    //设置窗口属性
    ANativeWindow_setBuffersGeometry(window, h,
                                     w,
                                     WINDOW_FORMAT_RGBA_8888);

    ANativeWindow_Buffer window_buffer;
    if (ANativeWindow_lock(window, &window_buffer, 0)) {
        ANativeWindow_release(window);
        window = 0;
        pthread_mutex_unlock(&mutex_t);
        return;
    }
    //填充rgb数据给dst_data
//    uint8_t *dst_data = static_cast<uint8_t *>(window_buffer.bits);
//    // stride：一行多少个数据（RGBA） *4
//    int dst_linesize = window_buffer.stride * 4;
//    //一行一行的拷贝
//    for (int i = 0; i < window_buffer.height; ++i) {
//        //memcpy(dst_data , data, dst_linesize);
//        memcpy(dst_data + i * dst_linesize, data + i * lineszie, dst_linesize);
//    }
    memcpy(window_buffer.bits, data, window_buffer.stride * window_buffer.height * 4);
    ANativeWindow_unlockAndPost(window);
//    delete dst_data;
    pthread_mutex_unlock(&mutex_t);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_facetracing_MainActivity_native_1postData(JNIEnv *env, jobject thiz,
                                                           jbyteArray _data,
                                                           jint width, jint height, jint cameraId) {

//    jbyte *data = env->GetByteArrayElements(_data, 0);
//    Mat src(height + height / 2, width, CV_8UC1, data);
//
//    cvtColor(src, src, COLOR_YUV2RGBA_NV21);
//    if (cameraId == 1) {//前置摄像头，需要逆时针旋转90度
//        rotate(src, src, ROTATE_90_COUNTERCLOCKWISE);
//        //要达到手机相机的效果，需要镜像一下
//        flip(src, src, 1); //1. 水平反正；0 :垂直翻转
//    } else {
//        rotate(src, src, ROTATE_90_CLOCKWISE);
//    }
//    Mat gray;
//    cvtColor(src, gray, COLOR_RGBA2GRAY);
//    std::vector<Rect> faces;
//    //增加轮廓对比(直方图均衡)
//    equalizeHist(gray, gray);
//    result->tracker->process(gray);
//    result->tracker->getObjects(faces);
//    for (auto face: faces) {
//        rectangle(src, face, Scalar(255, 0, 255));
//    }
//    render(src.data, 0, width, height);
//    src.release();
//    gray.release();
//    //TODO  显示


// nv21的数据
    jbyte *data = env->GetByteArrayElements(_data, NULL);
    //mat  data-》Mat
    //1、高 2、宽
    Mat src(height + height / 2, width, CV_8UC1, data);
    //颜色格式的转换 nv21->RGBA
    //将 nv21的yuv数据转成了rgba
    cvtColor(src, src, COLOR_YUV2RGBA_NV21);
    // 正在写的过程 退出了，导致文件丢失数据
    //imwrite("/sdcard/src.jpg",src);
    if (cameraId == 1) {
        //前置摄像头，需要逆时针旋转90度
        rotate(src, src, ROTATE_90_COUNTERCLOCKWISE);
        //水平翻转 镜像
        flip(src, src, 1);
    } else {
        //顺时针旋转90度
        rotate(src, src, ROTATE_90_CLOCKWISE);
    }
    Mat gray;
    //灰色
    cvtColor(src, gray, COLOR_RGBA2GRAY);
    //增强对比度 (直方图均衡)
    equalizeHist(gray, gray);
    std::vector<Rect> faces;
    //定位人脸 N个
    result->tracker->process(gray);
    result->tracker->getObjects(faces);
    for (Rect face: faces) {
        //画矩形
        //分别指定 bgra
        rectangle(src, face, Scalar(255, 0, 255));
    }
    //显示
    if (window) {
        //设置windows的属性
        // 因为旋转了 所以宽、高需要交换
        //这里使用 cols 和rows 代表 宽、高 就不用关心上面是否旋转了
        ANativeWindow_setBuffersGeometry(window, src.cols, src.rows, WINDOW_FORMAT_RGBA_8888);
        ANativeWindow_Buffer buffer;
        do {
            //lock失败 直接brek出去
            if (ANativeWindow_lock(window, &buffer, 0)) {
                ANativeWindow_release(window);
                window = 0;
                break;
            }
            //src.data ： rgba的数据
            //把src.data 拷贝到 buffer.bits 里去
            // 一行一行的拷贝
            memcpy(buffer.bits, src.data, buffer.stride * buffer.height * 4);
            //提交刷新
            ANativeWindow_unlockAndPost(window);
        } while (0);
    }
    //释放Mat
    //内部采用的 引用计数
    src.release();
    gray.release();
    env->ReleaseByteArrayElements(_data, data, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_facetracing_MainActivity_native_1release(JNIEnv *env, jobject thiz) {

    delete callHelper;
    callHelper = 0;
//    delete window;
//    window = 0;

}