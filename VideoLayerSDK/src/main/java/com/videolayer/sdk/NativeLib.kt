package com.videolayer.sdk

class NativeLib {

    /**
     * A native method that is implemented by the 'sdk' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'sdk' library on application startup.
        init {
            System.loadLibrary("sdk")
        }
    }
}