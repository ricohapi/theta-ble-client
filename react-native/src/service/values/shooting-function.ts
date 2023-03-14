/**
 * Shooting function.
 */
export const ShootingFunctionEnum = {
  /**
   * Undefined item
   */
  UNKNOWN: 'UNKNOWN',
  /** Normal shooting function */
  NORMAL: 'NORMAL',
  /** Self-timer shooting function(RICOH THETA X is not supported.) */
  SELF_TIMER: 'SELF_TIMER',
  /** My setting shooting function */
  MY_SETTING: 'MY_SETTING',
} as const;

/** type definition of ShootingFunctionEnum */
export type ShootingFunctionEnum =
  (typeof ShootingFunctionEnum)[keyof typeof ShootingFunctionEnum];
