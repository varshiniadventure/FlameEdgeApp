cmake_minimum_required(VERSION 3.10.2)
project("flameedgeapp")

# Your native library
add_library(
        native-lib
        SHARED
        native-lib.cpp
)

# Include OpenCV headers
include_directories(${CMAKE_SOURCE_DIR}/opencv/include)

# Import the OpenCV shared library
add_library(opencv_java4 SHARED IMPORTED)

set_target_properties(opencv_java4 PROPERTIES
        IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/arm64-v8a/libopencv_java4.so
)

# Import libc++_shared
add_library(cxx_shared SHARED IMPORTED)

set_target_properties(cxx_shared PROPERTIES
        IMPORTED_LOCATION
        ${CMAKE_SOURCE_DIR}/../jniLibs/arm64-v8a/libc++_shared.so
)

# Link system libraries
find_library(log-lib log)
find_library(gles-lib GLESv2)

# Link everything together
target_link_libraries(
        native-lib
        opencv_java4
        cxx_shared
        ${log-lib}
        ${gles-lib}
)
