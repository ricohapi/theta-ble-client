/**
 * Peripheral device connection state
 */
export const PeripheralDeviceStatusEnum = {
  /**
   *  Undefined value
   */
  UNKNOWN: 'UNKNOWN',

  /**
   * Unconnected
   */
  IDLE: 'IDLE',

  /**
   * Connected
   */
  CONNECTED: 'CONNECTED',
} as const;

/** type definition of PeripheralDeviceStatusEnum */
export type PeripheralDeviceStatusEnum =
  typeof PeripheralDeviceStatusEnum[keyof typeof PeripheralDeviceStatusEnum];
