cmake_minimum_required(VERSION 3.22.1)
project("Compose2023Project")
#当前电脑系统配置的路径
include_directories("${CMAKE_SOURCE_DIR}/include/")
#link_libraries("${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
#设置连接动态库的路径
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")
message("abi path = ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
# 添加 OpenCV 库
#find_package(OpenCV REQUIRED)
#
#include_directories(${OpenCV_INCLUDE_DIRS})
#创建一个变量叫source_file ,指向当前目录下的所有.cpp文件
file(GLOB source_file_cpp *.cpp)
add_library(
        nativeLib #生成的库的名字
        SHARED #表示生成的库位动态库
        ${source_file_cpp} JavaCallHelper.cpp)#编译的源文件，如果是多个则依次写在后面，这里是用到了相对路径，如果其他目录需要指定路径

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)


target_link_libraries(nativeLib android opencv_java4 log)

set(BUILD_SHARED_LIBS ON)