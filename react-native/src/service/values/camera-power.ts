/**
 * Camera power
 */
export const CameraPowerEnum = {
  /**
   *  Undefined value
   */
  UNKNOWN: 'UNKNOWN',

  /**
   * Camera power off.
   */
  OFF: 'OFF',

  /**
   * Camera power on.
   */
  ON: 'ON',

  /**
   * Camera sleep.
   */
  SLEEP: 'SLEEP',

  /**
   * Power on, power saving mode. Camera is closed.
   * Unavailable parameter when plugin is running. In this case, invalidParameterValue error will be returned.
   */
  POWER_SAVING: 'POWER_SAVING',

  /**
   * Power on, silent mode. LCD/LED is turned off.
   * Unavailable parameter when plugin is running. In this case, invalidParameterValue error will be returned.
   */
  SILENT_MODE: 'SILENT_MODE',
} as const;

/** type definition of CameraPowerEnum */
export type CameraPowerEnum =
  typeof CameraPowerEnum[keyof typeof CameraPowerEnum];
