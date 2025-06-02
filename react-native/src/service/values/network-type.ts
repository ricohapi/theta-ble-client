/**
 * Network type of the camera.
 * Can be acquired by camera.getOptions and set by camera.setOptions.
 */
export const NetworkTypeEnum = {
  /** Undefined value */
  UNKNOWN: 'UNKNOWN',
  /** Direct mode */
  DIRECT: 'DIRECT',
  /** Client mode via WLAN */
  CLIENT: 'CLIENT',
  /** Client mode via Ethernet cable */
  ETHERNET: 'ETHERNET',
  /** Network is off. This value can be gotten only by plugin. */
  OFF: 'OFF',
  /** LTE plan-D */
  LTE_PLAN_D: 'LTE_PLAN_D',
  /** LTE plan-DU */
  LTE_PLAN_DU: 'LTE_PLAN_DU',
  /** LTE plan01s */
  LTE_PLAN_01S: 'LTE_PLAN_01S',
  /** LTE planX3 */
  LTE_PLAN_X3: 'LTE_PLAN_X3',
  /** LTE planP1 */
  LTE_PLAN_P1: 'LTE_PLAN_P1',
  /** LTE plan-K2 */
  LTE_PLAN_K2: 'LTE_PLAN_K2',
  /** LTE plan-K */
  LTE_PLAN_K: 'LTE_PLAN_K',
  /**
   * SSID scan mode
   * 
   * Switch to CL mode and search for SSID
   */
  SCAN: 'SCAN',
} as const;

/** Type definition of NetworkTypeEnum */
export type NetworkTypeEnum =
  (typeof NetworkTypeEnum)[keyof typeof NetworkTypeEnum];
