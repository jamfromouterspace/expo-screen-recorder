import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to ExpoScreenRecorder.web.ts
// and on native platforms to ExpoScreenRecorder.ts
import ExpoScreenRecorderModule from './ExpoScreenRecorderModule';
import ExpoScreenRecorderView from './ExpoScreenRecorderView';
import { ChangeEventPayload, ExpoScreenRecorderViewProps } from './ExpoScreenRecorder.types';

// Get the native constant value.
export const PI = ExpoScreenRecorderModule.PI;

export function hello(): string {
  return ExpoScreenRecorderModule.hello();
}

export async function setValueAsync(value: string) {
  return await ExpoScreenRecorderModule.setValueAsync(value);
}

const emitter = new EventEmitter(ExpoScreenRecorderModule ?? NativeModulesProxy.ExpoScreenRecorder);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { ExpoScreenRecorderView, ExpoScreenRecorderViewProps, ChangeEventPayload };
