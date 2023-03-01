#include <jni.h>

#ifndef _Included_com_android_smartipc_RealTimeRepository
#define _Included_com_android_smartipc_RealTimeRepository
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_open(JNIEnv* env, jobject clazz, jstring name, jint length);
JNIEXPORT jlong  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_mmap(JNIEnv* env, jobject clazz, jint fileDescriptor, jint length, jint prot);
		JNIEXPORT void JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_munmap(JNIEnv* env, jobject clazz, jlong addr, jint length);
JNIEXPORT void  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_close(JNIEnv* env, jobject clazz, jint fileDescriptor);

//JNIEXPORT jint JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_read(JNIEnv* env, jobject clazz,
//		jint fileDescriptor, jlong address, jbyteArray buffer, jint srcOffset, jint destOffset,
//         jint count, jboolean unpinned);

JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_write(JNIEnv* env, jobject clazz,
		jint fileDescriptor, jlong address, jbyteArray buffer, jint srcOffset, jint destOffset,
         jint count, jboolean unpinned);


JNIEXPORT jint JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readInt(JNIEnv* env, jobject clazz,
		jlong address, jint offset);

JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeInt(JNIEnv* env, jobject clazz,
		jlong address, jint offset, jint value);


//JNIEXPORT jbyte JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readByte(JNIEnv* env, jobject clazz,
//		jlong address, jint offset);

JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeByte(JNIEnv* env, jobject clazz,
		jlong address, jint offset, jbyte value);


JNIEXPORT jint JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_getSize(JNIEnv* env, jobject clazz, jint fileDescriptor);

JNIEXPORT jdouble JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readDouble(JNIEnv* env, jobject clazz, jlong address, jint offset);
JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeDouble(JNIEnv* env, jobject clazz, jlong address, jint offset, jdouble value);

//JNIEXPORT jshort JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readShort(JNIEnv* env, jobject clazz,
//		jlong address, jint offset);
//
//JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeShort(JNIEnv* env, jobject clazz,
//		jlong address, jint offset, jshort value);
//
//JNIEXPORT jfloat JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readFloat(JNIEnv* env, jobject clazz, jlong address, jint offset);
//JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeFloat(JNIEnv* env, jobject clazz, jlong address, jint offset, jfloat value);

#ifdef __cplusplus
}
#endif
#endif
