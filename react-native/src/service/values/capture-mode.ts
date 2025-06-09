/**
 * Capture mode.
 */
export const CaptureModeEnum = {
  /**
   *  Undefined value
   */
  UNKNOWN: 'UNKNOWN',

  /**
   *  Still image shooting mode.
   */
  IMAGE: 'IMAGE',

  /**
   * Movie shooting mode.
   */
  VIDEO: 'VIDEO',

  /**
   * Live streaming mode.
   */
  LIVE: 'LIVE',

  /**
   * Interval still image capture mode just for Theta SC2 and Theta SC2 for business
   */
  INTERVAL: 'INTERVAL',

  /**
   * Preset mode just for Theta SC2 and Theta SC2 for business
   */
  PRESET: 'PRESET',

  /**
   * WebRTC
   */
  WEB_RTC: 'WEB_RTC',
} as const;

/** type definition of CaptureModeEnum */
export type CaptureModeEnum =
  typeof CaptureModeEnum[keyof typeof CaptureModeEnum];
