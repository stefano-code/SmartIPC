#include "RealTimeRepository.h"

    #include <jni.h>
    #include <android/log.h>
    #include "ashmem.h"
    #include <errno.h>
    #include <unistd.h>
    #include <sys/mman.h>

    static JavaVM* gVm = NULL;

    extern "C" JNIEXPORT jint JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_open(
            JNIEnv* env, jobject clazz, jstring name, jint length) {
        __android_log_print(ANDROID_LOG_ERROR, "MemoryFile", "open");
        const char* namestr = (name ? env->GetStringUTFChars(name, NULL) : NULL);
        int result = ashmem_create_region(namestr, length);
        return (jint) result;
    }

    extern "C" JNIEXPORT jlong JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_mmap(
            JNIEnv* env, jobject clazz, jint fd, jint length, jint prot) {
        __android_log_print(ANDROID_LOG_ERROR, "MemoryFile", "mmap");
        jlong result = (jlong)mmap(NULL, length, prot, MAP_SHARED, fd, 0);
        return result;
     }

     extern "C" JNIEXPORT void JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_close(
             JNIEnv* env, jobject clazz, jint fd) {
         if (fd >= 0)
             close(fd);
     }

    extern "C" JNIEXPORT jint JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readInt(
            JNIEnv* env, jobject clazz, jlong address, jint offset) {
        int res = *(int*)(address + offset);
        return (jint) res;
    }

    extern "C" JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeInt(
            JNIEnv* env, jobject clazz, jlong address, jint offset, jint value) {
        *((int*)(address + offset)) = (int)value;
        return (jint) sizeof(int);
    }


    extern "C" JNIEXPORT jdouble JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readDouble(
            JNIEnv* env, jobject clazz, jlong address, jint offset) {
        volatile long long* p = (volatile long long*)(address + offset);
        long long v = *p;

        // makes atomic the read operation
        v = __sync_add_and_fetch_8(p, 0);
        double res = *((double*)(&v));

        return (jdouble) res;
    }

    extern "C" JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeDouble(
            JNIEnv* env, jobject clazz, jlong address, jint offset, jdouble value) {
        long long* p = (long long*)(address + offset);

        double v = (double) value;
        volatile long long lv = *(( volatile long long*)(&v));

        // makes atomic the write operation
        __sync_lock_test_and_set_8(p, lv);

        return (jint) sizeof(double);
    }

    extern "C" JNIEXPORT jint JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_write(
            JNIEnv* env, jobject clazz, jint fd, jlong address, jbyteArray buffer,
            jint srcOffset, jint destOffset, jint count, jboolean unpinned)
    {
        if (unpinned && ashmem_pin_region(fd, 0, 0) == ASHMEM_WAS_PURGED) {
            ashmem_unpin_region(fd, 0, 0);
            return -1;
        }
        env->GetByteArrayRegion(buffer, srcOffset, count, (jbyte *)address + destOffset);
        if (unpinned) {
            ashmem_unpin_region(fd, 0, 0);
        }
        return count;
    }

    extern "C" JNIEXPORT jint JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_getSize(
            JNIEnv* env, jobject clazz, jint fd) {
         // Use ASHMEM_GET_SIZE to find out if the fd refers to an ashmem region.
         // ASHMEM_GET_SIZE should succeed for all ashmem regions, and the kernel
         // should return ENOTTY for all other valid file descriptors
         int result = ashmem_get_size_region(fd);
         if (result < 0) {
             if (errno == ENOTTY) {
                 // ENOTTY means that the ioctl does not apply to this object,
                 // i.e., it is not an ashmem region.
                 return (jint) -1;
             }
             return (jint) -1;
         }
         return (jint) result;
    }

    int JNI_OnLoad (JavaVM* vm, void* reserved)
      {
        // Cache the JavaVM interface pointer
        gVm = vm;
        return JNI_VERSION_1_6;
      }


//extern "C" JNIEXPORT void JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_munmap(JNIEnv* env, jobject clazz, jlong addr, jint length)
//{
//    __android_log_print(ANDROID_LOG_ERROR, "MemoryFile", "munmap");
//    int result = munmap((void *)addr, length);
//    //  if (result < 0)
//    //     jniThrowException(env, "java/io/IOException", "munmap failed");
//}

//extern "C" JNIEXPORT jint JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_read(JNIEnv* env, jobject clazz,
//                                                                                           jint fd, jlong address, jbyteArray buffer, jint srcOffset, jint destOffset,
//                                                                                           jint count, jboolean unpinned)
//{
//    if (unpinned && ashmem_pin_region(fd, 0, 0) == ASHMEM_WAS_PURGED) {
//        ashmem_unpin_region(fd, 0, 0);
//        return -1;
//    }
//    env->SetByteArrayRegion(buffer, destOffset, count, (const jbyte *)address + srcOffset);
//    if (unpinned) {
//        ashmem_unpin_region(fd, 0, 0);
//    }
//    return count;
//}
//
//extern "C" JNIEXPORT jint JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_write(JNIEnv* env, jobject clazz,
//                                                                                            jint fd, jlong address, jbyteArray buffer, jint srcOffset, jint destOffset,
//                                                                                            jint count, jboolean unpinned)
//{
//    if (unpinned && ashmem_pin_region(fd, 0, 0) == ASHMEM_WAS_PURGED) {
//        ashmem_unpin_region(fd, 0, 0);
//        return -1;
//    }
//    env->GetByteArrayRegion(buffer, srcOffset, count, (jbyte *)address + destOffset);
//    if (unpinned) {
//        ashmem_unpin_region(fd, 0, 0);
//    }
//    return count;
//}
//
//
//extern "C" JNIEXPORT jbyte JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readByte(JNIEnv* env, jobject clazz, jlong address, jint offset)
//{
//    char res = *(char*)(address + offset);
//    return (jint) res;
//}
//
//extern "C" JNIEXPORT jshort JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readShort(JNIEnv* env, jobject clazz, jlong address, jint offset)
//{
//    short res = *(short*)(address + offset);
//    return (jshort) res;
//}
//
//extern "C" JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeShort(JNIEnv* env, jobject clazz,
//                                                                                                  jlong address, jint offset, jshort value)
//{
//    *((short*)(address + offset)) = (short)value;
//    return (jint) sizeof(short);
//}
//
//
//extern "C" JNIEXPORT jfloat JNICALL  Java_com_android_smartipc_rtrepo_RealTimeRepository_readFloat(JNIEnv* env, jobject clazz, jlong address, jint offset)
//{
//    //  __android_log_print(ANDROID_LOG_ERROR, "MemoryFile", "readDouble lock 1");
//
//    volatile long long* p = (volatile long long*)(address + offset);
//    long long v = *p;
//    // __android_log_print(ANDROID_LOG_ERROR, "addr3", "%ld", p);
//
//    // rende atomica l'operazione di lettura
//    v = __sync_add_and_fetch_4(p, 0);
//    float res = *((float*)(&v));
//
//    return (jfloat) res;
//}
//
//extern "C" JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeFloat(JNIEnv* env, jobject clazz,
//                                                                                                  jlong address, jint offset, jfloat value)
//{
//    // __android_log_print(ANDROID_LOG_ERROR, "MemoryFile", "writeDouble lock 1");
//
//    long long* p = (long long*)(address + offset);
//
//    float v = (float) value;
//    volatile long long lv = *(( volatile long long*)(&v));
//
//    // rende atomica l'operazione di scrittura
//    __sync_lock_test_and_set_4(p, lv);
//
//    return (jint) sizeof(float);
//}
//
//
//extern "C" JNIEXPORT jint  JNICALL Java_com_android_smartipc_rtrepo_RealTimeRepository_writeByte(
//        JNIEnv* env, jobject clazz, jlong address, jint offset, jbyte value) {
//    *((char*)(address + offset)) = (char)value;
//    return (jint) sizeof(char);
//}
