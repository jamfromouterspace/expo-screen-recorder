import ExpoScreenRecorderModule from "./ExpoScreenRecorderModule";

export async function startRecording(
  micEnabled: boolean = false,
): Promise<void> {
  return ExpoScreenRecorderModule.startRecording(micEnabled);
}

export async function stopRecording(): Promise<string> {
  return ExpoScreenRecorderModule.stopRecording();
}
