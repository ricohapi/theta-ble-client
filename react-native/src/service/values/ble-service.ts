/**
 * BLE service
 */
export const BleServiceEnum = {
  /**
   * Camera Information
   */
  CAMERA_INFORMATION: 'CAMERA_INFORMATION',

  /**
   * Camera Status Command
   */
  CAMERA_STATUS_COMMAND: 'CAMERA_STATUS_COMMAND',

  /**
   * Camera Control Commands
   */
  CAMERA_CONTROL_COMMANDS: 'CAMERA_CONTROL_COMMANDS',

  /**
   * Camera Control Command v2
   */
  CAMERA_CONTROL_COMMAND_V2: 'CAMERA_CONTROL_COMMAND_V2',

  /**
   * Shooting Control Command
   */
  SHOOTING_CONTROL_COMMAND: 'SHOOTING_CONTROL_COMMAND',

} as const;

/** type definition of BleServiceEnum */
export type BleServiceEnum =
  typeof BleServiceEnum[keyof typeof BleServiceEnum];
