cmake_minimum_required(VERSION 3.10.2)

project("JerryEmail")


#设置两个目录，${ANDROID_ABI}会根据设备变化
set(DIR ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
set(DIR2 ${PROJECT_SOURCE_DIR}/../jniLibs)

#指定h文件的目录，编译过程需要找.h文件
include_directories(${DIR2}/include)


add_library(
        native-lib

        SHARED

        native-lib.cpp)

#导入FFmpeg的库
add_library(libavcodec SHARED IMPORTED)

#so文件的位置
set_target_properties(libavcodec
        PROPERTIES IMPORTED_LOCATION
        ${DIR}/libavcodec.so)


add_library(libavfilter SHARED IMPORTED)
set_target_properties(libavfilter
        PROPERTIES IMPORTED_LOCATION
        ${DIR}/libavfilter.so)

add_library(libavformat SHARED IMPORTED)
set_target_properties(libavformat
        PROPERTIES IMPORTED_LOCATION
        ${DIR}/libavformat.so)

add_library(libavutil SHARED IMPORTED)
set_target_properties(libavutil
        PROPERTIES IMPORTED_LOCATION
        ${DIR}/libavutil.so)

add_library(libswresample SHARED IMPORTED)
set_target_properties(libswresample
        PROPERTIES IMPORTED_LOCATION
        ${DIR}/libswresample.so)

add_library(libswscale SHARED IMPORTED)
set_target_properties(libswscale
        PROPERTIES IMPORTED_LOCATION
        ${DIR}/libswscale.so)

#查找log库
find_library(log-lib
        log)


#连接库
target_link_libraries(
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib}
        libavfilter
        libavformat
        libswresample
        libswscale
        libavutil
        libavcodec)

