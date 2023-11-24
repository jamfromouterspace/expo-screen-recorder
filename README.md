# expo-screen-recorder

Programmatically start and stop screen recording. iOS-only for now. If you get a build error because it can't find ReplayKit, check my GitHub for the `expo-record-screen-config-plugin` or something similar-sounding – it adds ReplayKit to the build.


## Installation
```
yarn add expo-screen-recorder
```

## Usage
```
import * as ScreenRecorder from "expo-screen-recorder"

// anywhere in your code
await ScreenRecorder.startRecording() // this includes the permission request, and no it can't be separated unfortunately, ReplayKit doesn't let you (as far as I know)
                                      // if you want to separate it, create a function that starts and stops screen recording right after
                                      // also, once permissions accepted, they wont be asked for again ONLY for that session, or if it's been longer than 8minutes lol
const url = await ScreenRecorder.stopRecording()
```

# Contributing

Contributions are very welcome! Please refer to guidelines described in the [contributing guide]( https://github.com/expo/expo#contributing).
