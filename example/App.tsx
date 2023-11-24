import { StyleSheet, Text, View } from 'react-native';

import * as ExpoScreenRecorder from 'expo-screen-recorder';

export default function App() {
  return (
    <View style={styles.container}>
      <Text>{ExpoScreenRecorder.hello()}</Text>
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
