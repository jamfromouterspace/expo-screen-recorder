import ExpoScreenRecorderModule from "./ExpoScreenRecorderModule";

export async function startRecording(
  micEnabled: boolean = false,
): Promise<void> {
  return ExpoScreenRecorderModule.startRecording(micEnabled);
}

/**
 * @param fileName
 * The name of the output file (dont include the file extension, we append ".mov" to the fileName you provide)
 * Default is a UUID.
 * */
export async function stopRecording(fileName?: string): Promise<string> {
  return ExpoScreenRecorderModule.stopRecording(fileName);
}
