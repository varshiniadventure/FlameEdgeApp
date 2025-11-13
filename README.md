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
Screenshot1.png
Screenshot2.png

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
Frame Flow
Camera2 → YUV_420_888 → Kotlin → JNI → C++ → OpenCV → Texture → GLSurfaceView
