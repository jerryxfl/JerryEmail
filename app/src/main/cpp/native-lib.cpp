#include <jni.h>
#include <string>




extern "C"
JNIEXPORT jstring JNICALL
Java_com_edu_cdp_jni_FFmpegKit_stringFromJNI(JNIEnv *env, jobject thiz) {

    return env->NewStringUTF("hello");
}