cmake_minimum_required(VERSION 3.22.1)
project("Compose2023Project")

#创建一个变量叫source_file ,指向当前目录下的所有.cpp文件

add_subdirectory("${CMAKE_SOURCE_DIR}/libzip/")
include_directories("${CMAKE_SOURCE_DIR}/libzip/")
#
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
#message("CMAKE_SOURCE_DIR == ${CMAKE_SOURCE_DIR}")
file(GLOB source_file_cpp *.cpp)
add_library(
        native-lib #生成的库的名字
        SHARED #表示生成的库位动态库
        ${source_file_cpp} bspatch.c)#编译的源文件，如果是多个则依次写在后面，这里是用到了相对路径，如果其他目录需要指定路径

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries(native-lib
        ${log-lib}
        log
        zip
        #Android NDK 提供的进行音频播放工具
        )