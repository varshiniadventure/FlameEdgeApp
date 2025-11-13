#include <jni.h>
#include <android/log.h>
#include <GLES2/gl2.h>
#include <opencv2/opencv.hpp>
#include <vector>

#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, "native-lib", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "native-lib", __VA_ARGS__)

using namespace cv;

static std::vector<unsigned char> processedBuffer;
static int procWidth = 0;
static int procHeight = 0;

extern "C"
JNIEXPORT void JNICALL
Java_com_example_flameedgeapp_MainActivity_nativeProcessFrame(
        JNIEnv *env, jobject instance,
        jbyteArray nv21ByteArray, jint width, jint height
) {
    jbyte *nv21 = env->GetByteArrayElements(nv21ByteArray, NULL);

    Mat yuv(height + height / 2, width, CV_8UC1, (unsigned char *)nv21);
    Mat rgba;

    cvtColor(yuv, rgba, COLOR_YUV2RGBA_NV21);
    rotate(rgba, rgba, ROTATE_90_CLOCKWISE);

    Mat gray;
    cvtColor(rgba, gray, COLOR_RGBA2GRAY);

    Mat edges;
    Canny(gray, edges, 80, 160);

    Mat outRGBA;
    cvtColor(edges, outRGBA, COLOR_GRAY2RGBA);

    procWidth = outRGBA.cols;
    procHeight = outRGBA.rows;

    size_t bufferSize = procWidth * procHeight * 4;

    processedBuffer.resize(bufferSize);
    memcpy(processedBuffer.data(), outRGBA.data, bufferSize);

    env->ReleaseByteArrayElements(nv21ByteArray, nv21, JNI_ABORT);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_flameedgeapp_MainActivity_nativeFillTexture(
        JNIEnv *env, jobject instance,
        jint textureId
) {
    if (processedBuffer.empty()) return;

    glBindTexture(GL_TEXTURE_2D, (GLuint)textureId);
    glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    glTexImage2D(
            GL_TEXTURE_2D,
            0,
            GL_RGBA,
            procWidth,
            procHeight,
            0,
            GL_RGBA,
            GL_UNSIGNED_BYTE,
            processedBuffer.data()
    );
}
