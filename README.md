FlameEdgeApp – Android + Web Edge Detection Project

This project implements real-time camera frame processing using Android (Kotlin + OpenCV + NDK/JNI) and a web interface (TypeScript).
The Android app captures camera frames, processes them in native C++ using OpenCV, and displays the output using an OpenGL surface.

**Features Implemented**

Android

 Live camera feed using Camera2 API

 Frame conversion (YUV → NV21 → RGBA)

 Native image processing using OpenCV + C++

 Real-time rendering using OpenGL (GLSurfaceView)

 JNI bridge between Kotlin and C++

 FPS indicator

 Orientation-corrected camera preview

Web

 Simple TypeScript script to interact with the Android module

 Demonstrates bidirectional communication concept (architecture explanation)

**Screenshots**
Screenshots are uploaded in this repository as:  
Screenshot1.jpg
https://github.com/varshiniadventure/FlameEdgeApp/blob/main/Screenshot1.jpg

Screenshot2.jpg
https://github.com/varshiniadventure/FlameEdgeApp/blob/main/Screenshot2.jpg

**Setup Instructions**
1. Install Requirements

  Android Studio (latest)

  Android NDK (Version 21+)

  CMake + LLDB installed in SDK Tools

  OpenCV Android SDK (OpenCV-4.x)

2. Add OpenCV to Project

  Copy the sdk/native and java bindings into your project

  Add opencv_java4 and c++_shared loading in MainActivity

3. Build Native Code

  CMake builds:

    native-lib.cpp

    OpenCV .so libraries

    Texture rendering methods

4. Run on Device

  Enable USB debugging

  Build & Run App

  Grant camera permission

  App displays processed camera frames

**Architecture Overview**
Camera frames are captured using Camera2 in Android and first handled in Kotlin. The raw YUV frame is passed to the native C++ layer through JNI, where OpenCV performs edge detection. The processed frame is then converted to a texture and rendered in real time using OpenGL/GLSurfaceView.

Camera2 → Kotlin → JNI → C++ (OpenCV) → OpenGL → Screen


A minimal TypeScript web module communicates with the Android side to demonstrate cross-platform interaction — sending configuration commands (e.g., enable filter, toggle mode) and receiving status information such as FPS and processing state. It represents a future-ready model for remote control or monitoring of the native image processing pipeline.
