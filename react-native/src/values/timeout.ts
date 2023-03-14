export const TIMEOUT_SCAN = 30_000;
export const TIMEOUT_PERIPHERAL = 1_000;
export const TIMEOUT_CONNECT = 5_000;
export const TIMEOUT_TAKE_PICTURE = 10_000;

/**
 * Configuration of timeout.
 */
export interface Timeout {
  /**
   * Specifies a time period (in milliseconds) required to scan THETA.
   */
  timeoutScan?: number;

  /**
   * Specifies a time period (in milliseconds) required to process an ble peripheral.
   */
  timeoutPeripheral?: number ;

  /**
   * Specifies a time period (in milliseconds) required to connection with THETA.
   */
  timeoutConnect?: number;

  /**
   * Specifies a time period (in milliseconds) required to take a picture.
   */
  timeoutTakePicture?: number;
}

/**
 * Configuration of timeout.
 */
export class TimeoutObject implements Timeout {
  /**
   * Specifies a time period (in milliseconds) required to scan THETA.
   */
  timeoutScan: number;

  /**
   * Specifies a time period (in milliseconds) required to process an ble peripheral.
   */
  timeoutPeripheral: number;

  /**
   * Specifies a time period (in milliseconds) required to connection with THETA.
   */
  timeoutConnect: number;

  /**
   * Specifies a time period (in milliseconds) required to take a picture.
   */
  timeoutTakePicture: number;

  constructor(timeout?: Timeout) {
    this.timeoutScan = timeout?.timeoutScan ?? TIMEOUT_SCAN;
    this.timeoutPeripheral = timeout?.timeoutPeripheral ?? TIMEOUT_PERIPHERAL;
    this.timeoutConnect = timeout?.timeoutConnect ?? TIMEOUT_CONNECT;
    this.timeoutTakePicture = timeout?.timeoutTakePicture ?? TIMEOUT_TAKE_PICTURE;
  }
}
