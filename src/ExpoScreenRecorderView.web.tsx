import * as React from 'react';

import { ExpoScreenRecorderViewProps } from './ExpoScreenRecorder.types';

export default function ExpoScreenRecorderView(props: ExpoScreenRecorderViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
