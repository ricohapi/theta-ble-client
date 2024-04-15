/* eslint no-useless-catch: 0 */

import type { ThetaDevice } from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum, CaptureModeEnum, FileFormatEnum } from './values';
import * as ThetaBleClient from '../native';

/**
 * Shooting Control Command Service
 *
 * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
 */
export class ShootingControlCommand extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;
  
  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.SHOOTING_CONTROL_COMMAND;
    this.device = device;
  }

  /**
   * Acquires the capture mode of the camera.
   *
   * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
   * 
   * Characteristic: 78009238-AC3D-4370-9B6F-C9CE2F4E3CA8
   *
   * @returns Capture Mode.
   */
  async getCaptureMode(): Promise<CaptureModeEnum> {
    return new Promise((resolve, reject) => {
      ThetaBleClient.nativeGetCaptureMode(this.device.id)
        .then((value) => {
          resolve(value as CaptureModeEnum);
        })
        .catch((error) => reject(error));
    });
  }

  /**
   * Set the capture mode of the camera.
   *
   * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
   * 
   * Characteristic: 78009238-AC3D-4370-9B6F-C9CE2F4E3CA8
   * 
   * @param mode Capture Mode.
   */
  async setCaptureMode(mode: CaptureModeEnum) {
    try {
      return await ThetaBleClient.nativeSetCaptureMode(this.device.id, mode);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the recording size (pixels) of the camera.
   *
   * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
   * 
   * Characteristic: E8F0EDD1-6C0F-494A-95C3-3244AE0B9A01
   *
   * @returns File format.
   */
  async getFileFormat(): Promise<FileFormatEnum> {
    return new Promise((resolve, reject) => {
      ThetaBleClient.nativeGetFileFormat(this.device.id)
        .then((value) => {
          resolve(value as FileFormatEnum);
        })
        .catch((error) => reject(error));
    });
  }

  /**
   * Set the recording size (pixels) of the camera.
   *
   * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
   * 
   * Characteristic: E8F0EDD1-6C0F-494A-95C3-3244AE0B9A01
   * 
   * @param mode File format.
   */
  async setFileFormat(value: FileFormatEnum) {
    try {
      return await ThetaBleClient.nativeSetFileFormat(this.device.id, value);
    } catch(error) {
      throw error;
    }
  }
  
  /**
   * Instructs the camera to start shooting a still image. Also, acquires the shooting status.
   *
   * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
   * 
   * Characteristic: FEC1805C-8905-4477-B862-BA5E447528A5
   * 
   * @param complete Notification of end of shooting. If an error occurs, notify the argument.
   */
  /* eslint-disable-next-line @typescript-eslint/no-explicit-any */
  takePicture(complete?: (error?: any) => void) {
    ThetaBleClient.nativeTakePicture(this.device.id)
      .then(() => {
        complete?.();
      })
      .catch((error) => {
        complete?.(error);
      });
  }
}
