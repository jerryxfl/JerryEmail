cmake_minimum_required(VERSION 3.10.2)

project("JerryEmail")

#include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)
#include_directories(${CMAKE_SOURCE_DIR}/src/main/jniLibs/armv8-a)






add_library(
        native-lib

        SHARED

        native-lib.cpp)

#查找log库
find_library(log-lib
        log)


#连接库
target_link_libraries(
        native-lib

        ${log-lib})

