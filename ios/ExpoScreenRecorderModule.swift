import ExpoModulesCore
import ReplayKit

public class ExpoScreenRecorderModule: Module {
    // Each module class must implement the definition function. The definition consists of components
    // that describes the module's functionality and behavior.
    // See https://docs.expo.dev/modules/module-api for more details about available components.
    public func definition() -> ModuleDefinition {
        // Sets the name of the module that JavaScript code will use to refer to the module. Takes a string as an argument.
        // Can be inferred from module's class name, but it's recommended to set it explicitly for clarity.
        // The module will be accessible from `requireNativeModule('ExpoScreenRecorder')` in JavaScript.
        Name("ExpoScreenRecorder")
        
        AsyncFunction("startRecording") { (micEnabled: Bool, promise: Promise) in
            // we cant enable camera until we implement a View for the camera preview
            // (ideally we allow creating that view in React Native)
            startRecording(micEnabled: micEnabled, cameraEnabled: false, onComplete: { error in
                if error != nil {
                    promise.reject(error!)
                } else {
                    promise.resolve()
                }
            })
        }
        
        AsyncFunction("stopRecording") { (fileName: String?) async throws -> String  in
            // Send an event to JavaScript.
            return try await stopRecording(fileName: fileName)
        }
    }

    func startRecording(micEnabled: Bool, cameraEnabled: Bool, onComplete: @escaping (Error?) -> ()) {
        let recorder = RPScreenRecorder.shared()
        recorder.isCameraEnabled = cameraEnabled
        recorder.isMicrophoneEnabled = micEnabled
        recorder.startRecording(handler: onComplete)
    }

    func stopRecording(fileName: String?) async throws -> String {
        let name = (fileName ?? UUID().uuidString) + ".mov"
        let url = FileManager.default.temporaryDirectory.appendingPathComponent(name)
        let recorder = RPScreenRecorder.shared()
        
        if #available(iOS 14.0, *) {
            try await recorder.stopRecording(withOutput: url)
        } else {
            // Fallback on earlier versions
            throw StopRecordingError.iOSVersionTooLow
        }
        
        return url.absoluteString
    }
}

enum StopRecordingError: Error {
    case iOSVersionTooLow
}

