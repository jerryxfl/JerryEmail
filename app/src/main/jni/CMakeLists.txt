# 设置cmake 的最小版本  一般系统自动生成
cmake_minimum_required(VERSION 3.6.0)

set(var hello)
message(${var})

add_library(
        # 设置生成.so 的文件名
        Test

        #设置库的类型  一种静态文件  STATIC .a   一种动态文件  SHARED .so
        SHARED

        # 需要编译的c/c++ 文件
        Test.cpp)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


target_link_libraries( # Specifies the target library.
        # 指定链接的目标库
        Test

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})