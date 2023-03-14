/**
 * Plugin power status (Kind of action)
 * @see PluginControl
 */
export const PluginPowerStatusEnum = {
  /**
   * Running (Start plugin)
   */
  RUNNING: 'RUNNING',

  /**
   * Stop
   */
  STOP: 'STOP',
} as const;

/** type definition of PluginControlEnum */
export type PluginPowerStatusEnum =
  typeof PluginPowerStatusEnum[keyof typeof PluginPowerStatusEnum];
