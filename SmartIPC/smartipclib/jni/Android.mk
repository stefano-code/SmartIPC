LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := SystemLib
LOCAL_SRC_FILES := RealTimeRepository.cpp ashmem-dev.c

LOCAL_LDLIBS :=  -llog 
APP_STL := stlport_static

include $(BUILD_SHARED_LIBRARY)
