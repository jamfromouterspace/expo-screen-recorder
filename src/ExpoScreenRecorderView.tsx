import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { ExpoScreenRecorderViewProps } from './ExpoScreenRecorder.types';

const NativeView: React.ComponentType<ExpoScreenRecorderViewProps> =
  requireNativeViewManager('ExpoScreenRecorder');

export default function ExpoScreenRecorderView(props: ExpoScreenRecorderViewProps) {
  return <NativeView {...props} />;
}
