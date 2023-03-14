/**
 * Camera power
 */
export const CameraPowerEnum = {
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
} as const;

/** type definition of CameraPowerEnum */
export type CameraPowerEnum =
  typeof CameraPowerEnum[keyof typeof CameraPowerEnum];
