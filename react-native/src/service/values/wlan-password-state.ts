/**
 * WLAN password state
 */
export const WlanPasswordStateEnum = {
  /**
   *  Undefined value
   */
  UNKNOWN: 'UNKNOWN',

  /**
   * The WLAN password has not been changed and the initial password is a part of the Theta's serial number.
   */
  SERIAL: 'SERIAL',

  /**
   * The WLAN password has not been changed and the initial password is a random string.
   */
  RANDOM: 'RANDOM',

  /**
   * The WLAN password has been changed.
   */
  CHANGED: 'CHANGED',
} as const;

/** type definition of WlanPasswordStateEnum */
export type WlanPasswordStateEnum =
  typeof WlanPasswordStateEnum[keyof typeof WlanPasswordStateEnum];
