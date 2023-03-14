/**
 * Battery charging state
 */
export const ChargingStateEnum = {
  /**
   * Undefined item
   */
  UNKNOWN: 'UNKNOWN',

  /**
   * battery charging
   */
  CHARGING: 'CHARGING',

  /**
   * battery charged
   */
  CHARGED: 'CHARGED',

  /**
   * battery disconnect
   */
  DISCONNECT: 'DISCONNECT',
} as const;

/** type definition of ChargingStateEnum */
export type ChargingStateEnum =
  typeof ChargingStateEnum[keyof typeof ChargingStateEnum];
