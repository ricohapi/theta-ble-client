/**
 * Capture mode.
 */
export const CaptureModeEnum = {
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
} as const;

/** type definition of CaptureModeEnum */
export type CaptureModeEnum =
  typeof CaptureModeEnum[keyof typeof CaptureModeEnum];
