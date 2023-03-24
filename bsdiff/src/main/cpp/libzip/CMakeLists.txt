cmake_minimum_required(VERSION 3.22.1)
project("Compose2023Project")

#创建一个变量叫source_file ,指向当前目录下的所有.cpp文件

#
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
#message("CMAKE_SOURCE_DIR == ${CMAKE_SOURCE_DIR}")
file(GLOB source_file_c *.c)
add_library(
        zip #生成的库的名字
        STATIC #表示生成的库位动态库
        ${source_file_c})#编译的源文件，如果是多个则依次写在后面，这里是用到了相对路径，如果其他目录需要指定路径

