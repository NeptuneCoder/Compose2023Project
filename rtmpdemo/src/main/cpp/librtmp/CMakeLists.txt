cmake_minimum_required(VERSION 3.22.1)
project("Compose2023Project")


include_directories("/usr/local/Cellar/openssl/1.0.2j/include")
link_directories("/usr/local/Cellar/openssl/1.0.2j/lib")
#创建一个变量叫source_file ,指向当前目录下的所有.cpp文件
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")
file(GLOB source_file_c *.c)
add_library(
        rtmp #生成的库的名字
        STATIC #表示生成的库位动态库
        ${source_file_c})#编译的源文件，如果是多个则依次写在后面，这里是用到了相对路径，如果其他目录需要指定路径
