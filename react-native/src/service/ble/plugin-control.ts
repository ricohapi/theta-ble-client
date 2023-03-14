import type { PluginPowerStatusEnum } from '../values';

/**
 * Plugin Control
 */
export interface PluginControl {
  /**
   * Plugin power status (Kind of action)
   * (0: Running (Start plugin)、1: Stop)
   */
  pluginControl: PluginPowerStatusEnum,

  /**
   * Target plugin number. Set the target plugin number to Plugin Orders before write.
   * This parameter is ignored when Plugin Control parameter is 1 (stop).
   * RICOH THETA Z1 or later
   */
  plugin?: number,
}
