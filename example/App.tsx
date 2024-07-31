import { StyleSheet, Text, View, Share, TouchableOpacity } from 'react-native';

import * as ExpoScreenRecorder from 'expo-screen-recorder';

export default function App() {
  const onPressStart = async () => {
    try {
      const result = await ExpoScreenRecorder.startRecording()
      console.log("result", result)
    } catch (err) {
      console.log("errorrr", err)
    }
  }
  const onPressStop = async () => {
    try {
      const url = await ExpoScreenRecorder.stopRecording()
      console.log("url!!", url)
      Share.share({ url: url })
    } catch (err) {
      console.log("errorrr", err)
    }
  }
  return (
    <View style={styles.container}>
      <TouchableOpacity 
        style={{ margin: 20, padding: 20, backgroundColor: "black", borderRadius: 50 }} 
        onPress={onPressStart}
      >
        <Text style={{ color: "white", fontSize: 16 }}>START RECORDING</Text>
      </TouchableOpacity>
      <TouchableOpacity 
        style={{ margin: 20, padding: 20, backgroundColor: "black", borderRadius: 50 }} 
        onPress={onPressStop}
      >
          <Text style={{ color: "white", fontSize: 16 }}>STOP RECORDING</Text>
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
