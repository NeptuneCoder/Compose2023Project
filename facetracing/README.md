### 项目说明

### 注意事项

1. 这里是加载动态库，so动态块需要放在main/jniLibs/abi下面
2. 需要再CMakeLists.txt中进行配置

```cmake
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI}")
```