#include "com_edu_cdp_jni_test.h"


extern "C"
JNIEXPORT jstring JNICALL
Java_com_edu_cdp_jni_TestJni_JniGetString(JNIEnv *env, jobject thiz) {
    // TODO: implement JniGetString()
    return env->NewStringUTF("你好");
}


extern "C"
JNIEXPORT void JNICALL
Java_com_edu_cdp_jni_CallNativeMethod_CallJniMethod(JNIEnv *env, jclass clazz) {
    // TODO: implement CallJniMethod()
    

}