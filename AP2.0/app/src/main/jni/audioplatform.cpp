#include <jni.h>
#include <RangeFinder.h>
#include <climits>
#include <cmath>

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jlong JNICALL
Java_com_fruitbasket_audioplatform_play2_PhaseProcessI_createNativeRangeFinder(JNIEnv *env,
                                                                               jobject instance,
                                                                               jint inMaxFramesPerSlice,
                                                                               jint inNumFreqs,
                                                                               jfloat inStartFreq,
                                                                               jfloat inFreqInterv) {

    //RangeFinder *rf=new RangeFinder(1,2,3.0,4.0);
    return (jlong) (new RangeFinder(inMaxFramesPerSlice, inNumFreqs, inStartFreq, inFreqInterv));
}
#ifdef __cplusplus
}
#endif

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jfloat JNICALL
Java_com_fruitbasket_audioplatform_play2_PhaseProcessI_getDistanceChange(JNIEnv *env,
                                                                         jobject instance,
                                                                         jlong thizptr,
                                                                         jshortArray recordData,
                                                                         jint size) {
    jfloat fDistance = 0.0;
    jshort *carr;
    carr = env->GetShortArrayElements(recordData, 0);
    if(carr == NULL) {
        return 0;
    }
    fDistance = ((RangeFinder*)thizptr)->GetDistanceChange(carr, size);
    env->ReleaseShortArrayElements(recordData, carr, 0);
    return fDistance;
}
#ifdef __cplusplus
}
#endif

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jfloatArray JNICALL
Java_com_fruitbasket_audioplatform_play2_PhaseProcessI_getBaseBand(JNIEnv *env, jobject instance,
                                                                   jlong thizptr, jint size) {
    //创建一个指定大小的数组
    int len = size * 32 * 2;
    jfloatArray jint_arr = env->NewFloatArray(len);
    jfloat *elems = env->GetFloatArrayElements(jint_arr, 0);
    ((RangeFinder*)thizptr)->getBaseBand(elems);
    //同步
    env->ReleaseFloatArrayElements(jint_arr, elems, 0);
    return jint_arr;
}
#ifdef __cplusplus
}
#endif

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jstring JNICALL
Java_com_fruitbasket_audioplatform_play2_PhaseProcessI_getJniString(JNIEnv *env, jobject instance) {
    return env->NewStringUTF("Hello World from jni");
}
#ifdef __cplusplus
}
#endif

#ifdef __cplusplus
extern "C" {
#endif
JNIEXPORT jshortArray JNICALL
Java_com_fruitbasket_audioplatform_play_WavePlayer_getMulripleFreqSignal(
        JNIEnv *env,
        jobject instance,
        jint bufferSize,
        jint sampleRate,
        jint iBeginHz,
        jint iStepHz,
        jint ifreqNum) {

    //保存得到的波形
    short *freqsNum = new short[bufferSize];

    //依据步进值iStepHz来叠加频率
    for (int i = 0; i < bufferSize; i++) {
        double sum = 0;
        for (int j = 0; j < ifreqNum; j++) {
            double sampleCountInWave = sampleRate / (double) (iBeginHz + j * iStepHz);
            sum += SHRT_MAX / 2 * sin(2.0 * M_PI * i / sampleCountInWave);
        }
        freqsNum[i] = (short) (sum / ifreqNum);
    }

    jshortArray result = (env)->NewShortArray(bufferSize);
    (env)->SetShortArrayRegion(result,0,bufferSize,freqsNum);

    delete freqsNum;
    return result;

}
#ifdef __cplusplus
}
#endif
