cmake_minimum_required(VERSION 3.22.1)
project("Compose2023Project")
#当前电脑系统配置的路径
#include_directories("/usr/local/Cellar/ffmpeg/5.1.2_5/include")
#link_directories("/usr/local/Cellar/ffmpeg/5.1.2_5/lib")

include_directories("${CMAKE_SOURCE_DIR}/include/")
#设置连接动态库的路径
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI}")
set(CMAKE_SHARED_LINKER_FLAGS "-fPIC -Wl,--exclude-libs,libgcc.a -Wl,--exclude-libs,libatomic.a -static-libstdc++ -Wl,--build-id  -Wall -v -Wl,--no-warn-shared-textrel -Wl,--fatal-warnings -Wl,--no-undefined -Qunused-arguments -Wl,-z,noexecstack -fPIE")

#创建一个变量叫source_file ,指向当前目录下的所有.cpp文件
file(GLOB source_file_c *.cpp)
add_library(
        nativeLib #生成的库的名字
        SHARED #表示生成的库位动态库
        ${source_file_c} SafeQueue.h)#编译的源文件，如果是多个则依次写在后面，这里是用到了相对路径，如果其他目录需要指定路径

find_library( # Sets the name of the path variable.
        log-lib
        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries(nativeLib avcodec avfilter avformat avutil swresample swscale ${log-lib} z android
        OpenSLES #Android NDK 提供的进行音频播放工具
        )