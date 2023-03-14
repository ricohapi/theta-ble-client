import type { CameraErrorEnum, CaptureStatusEnum, ChargingStateEnum, ShootingFunctionEnum } from '../values';

/**
 * Mutable values representing Theta status.
 */
export interface ThetaState {
  /**
   * Battery level
   * (0.0 to 1.0)
   * When using an external power source, 1 (100%)
   */
  batteryLevel?: number;

  /**
   * Continuously shoots state
   */
  captureStatus?: CaptureStatusEnum;

  /**
   * Shooting time of movie (sec)
   */
  recordedTime?: number;

  /**
   * Remaining time of movie (sec)
   */
  recordableTime?: number;

  /**
   * Number of still images captured during continuous shooting, Unit: images
   */
  capturedPictures?: number;

  /**
   * URL of the last saved file
   *
   * Z1: http://[IP address]/files/[eMMC ID]/[Directory name]/[File name]
   * X: http://[IP address]/files/[Directory name]/[File name]
   * DNG format files are not displayed. For burst shooting, files in the DNG format are displayed.
   */
  latestFileUrl?: string;

  /**
   * Charging state
   */
  batteryState?: ChargingStateEnum;

  /**
   * Shooting function status
   */
  shootingFunction?: ShootingFunctionEnum;

  /**
   * Error information of the camera
   */
  cameraError?: CameraErrorEnum[];

  /**
   * true: Battery inserted; false: Battery not inserted
   *
   * RICOH THETA X or later
   */
  batteryInsert?: boolean;

  /**
   * Camera main board temperature
   */
  boardTemp?: number;

  /**
   * Battery temperature
   */
  batteryTemp?: number;
}
