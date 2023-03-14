/**
 * Plugin orders
 *
 * When not specifying, set 0. If an 0 is placed mid-way, it will be moved to the front.
 * Specifying zero plugin will result in an error.
 *
 * RICOH THETA Z1 or later.
 */
export interface PluginOrders {
  /**
   * Plugin number to be set the first plugin
   */
  first: number,

  /**
   * Plugin number to be set the second plugin
   */
  second: number,

  /**
   * Plugin number to be set the third plugin
   */
  third: number,
}
