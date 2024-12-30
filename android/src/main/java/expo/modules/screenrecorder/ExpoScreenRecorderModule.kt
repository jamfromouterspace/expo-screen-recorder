package expo.modules.screenrecorder

import android.Manifest
import expo.modules.kotlin.modules.Module
import expo.modules.kotlin.modules.ModuleDefinition
import expo.modules.kotlin.Promise
import android.app.Activity
import android.content.Context
import android.media.MediaCodecList
import android.app.Application
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Environment
import androidx.core.content.ContextCompat
import expo.modules.kotlin.exception.CodedException
import java.io.File
import java.util.UUID

import com.hbisoft.hbrecorder.HBRecorder
import com.hbisoft.hbrecorder.HBRecorderListener



class ExpoScreenRecorderModule : Module(), HBRecorderListener {
  private var mediaProjectionManager: MediaProjectionManager? = null
    private var REQUEST_CODE_SCREEN_CAPTURE = 1001
    private var startRecordingPromise: Promise? = null
      private var stopRecordingPromise: Promise? = null
        private var hbRecorder: HBRecorder? = null;
        private var outputUri: File? = null;

        override fun HBRecorderOnStart() {
          println("HBRecorderOnStart")
        }

        override fun HBRecorderOnComplete() {
          println("HBRecorderOnComplete")
          if (stopRecordingPromise != null) {
            val uri = hbRecorder!!.filePath;
            println("uri!!! $uri")
            //      val response = WritableNativeMap();
            //      val result = WritableNativeMap();
            //      result.putString("outputURL", uri);
            //      response.putString("status", "success");
            //      response.putMap("result", result);
            stopRecordingPromise!!.resolve(uri);
            stopRecordingPromise = null
          }
        }

        override fun HBRecorderOnError(errorCode: Int, reason: String?) {
          startRecordingPromise?.reject(CodedException("ScreenRecorderError: $errorCode $reason"))
          startRecordingPromise = null
          stopRecordingPromise?.reject(CodedException("ScreenRecorderError: $errorCode $reason"))
          stopRecordingPromise = null
          println("HBRecorderOnError")
          println("errorCode")
          println(errorCode)
          println("reason")
          println(reason)
        }

        override fun HBRecorderOnPause() {
          println("HBRecorderOnPause")
        }

        override fun HBRecorderOnResume() {
          println("HBRecorderOnResume")
        }

        private fun setup(micEnabled: Boolean) {
          Application().onCreate()
          outputUri = appContext.reactContext!!.getExternalFilesDir("ExpoScreenRecorder")
          println("outputUri ${outputUri.toString()}")
          hbRecorder = HBRecorder(appContext.reactContext!!, this)

          val activity = appContext.activityProvider?.currentActivity as? Activity
          ?: throw Exception("Activity is null")

          // Check if microphone permission is granted
          val permissionsGranted = ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

          // Enable or disable audio based on permissions and micEnabled flag
          hbRecorder!!.isAudioEnabled(micEnabled && permissionsGranted)
          hbRecorder!!.setOutputPath(outputUri!!.toString())
          hbRecorder!!.setScreenDimensions(900, 600)

          if (doesSupportEncoder("h264")) {
            println("doesSupportEncoder")
            hbRecorder!!.setVideoEncoder("H264")
          } else {
            hbRecorder!!.setVideoEncoder("DEFAULT")
          }
        }


        // Each module class must implement the definition function. The definition consists of components
        // that describes the module's functionality and behavior.
        // See https://docs.expo.dev/modules/module-api for more details about available components.
        override fun definition() = ModuleDefinition {
          // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
          // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
          // The module will be accessible from `requireNativeModule('ExpoScreenRecorder')` in JavaScript.
          Name("ExpoScreenRecorder")

          // Defines a JavaScript function that always returns a Promise and whose native code
          // is by default dispatched on the different thread than the JavaScript runtime runs on.
          AsyncFunction("startRecording") { micEnabled: Boolean, promise: Promise ->
            startRecordingPromise = promise
            val activity = appContext.activityProvider?.currentActivity as? Activity
            if (activity == null) {
              promise.reject(CodedException("yo"))
              return@AsyncFunction
            }
            println("setup...")
            if (hbRecorder == null) {
              try{
                setup(micEnabled)
              }
              catch (e: Exception) {
                promise.reject(CodedException("Screen recorder not initialized ${e.message}"))
              }
            }
            println("done setup!")
            mediaProjectionManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val captureIntent = mediaProjectionManager?.createScreenCaptureIntent()
            (appContext.currentActivity)?.startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE)
          }

          AsyncFunction("stopRecording") { fileName: String?, promise: Promise ->
            println("React stopRecording")
            stopRecordingPromise = promise
            if (hbRecorder == null) {
              stopRecordingPromise?.reject(CodedException("Screen recorder not initialized"))
              stopRecordingPromise = null
            } else {
              hbRecorder!!.stopScreenRecording()
            }
          }

          //    OnCreate {
          //      setup()
          //    }

          OnActivityResult { activity, onActivityResultPayload ->
            // handle results here
            val requestCode = onActivityResultPayload.requestCode
            val resultCode = onActivityResultPayload.resultCode
            val data = onActivityResultPayload.data
            println("OnActivityResult...")
            if (hbRecorder == null) {
              startRecordingPromise?.reject(CodedException("Screen recorder not initialized"))
              startRecordingPromise = null
            } else if (requestCode == SCREEN_RECORD_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
              hbRecorder!!.startScreenRecording(data, resultCode);
              startRecordingPromise?.resolve("")
              startRecordingPromise = null
            } else {
              startRecordingPromise?.reject(CodedException("Screen recording permission denied"))
              startRecordingPromise = null
            }
          }
        }

        private fun getOutputFile(fileName: String? = null): File {
          val name = (fileName ?: UUID.randomUUID().toString()) + ".mp4"
          return File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), name)
        }

        companion object {
          private const val SCREEN_RECORD_REQUEST_CODE = 1001
        }

        private fun doesSupportEncoder(encoder: String): Boolean {
          val list = MediaCodecList(MediaCodecList.ALL_CODECS).codecInfos
          val size = list.size
          for (i in 0 until size) {
            val codecInfo = list[i]
            if (codecInfo.isEncoder) {
              if (codecInfo!!.name.contains(encoder)) {
                return true
              }
            }
          }
          return false
        }
}
