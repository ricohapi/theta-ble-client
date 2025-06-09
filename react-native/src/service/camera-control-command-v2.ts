import type { NotifyError, ThetaDevice, ThetaStateNotify } from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum, OptionName } from './values';
import * as ThetaBleClient from '../native';
import type { ThetaInfo, ThetaOptions, ThetaState, ThetaState2 } from './data';

const MESSAGE_NO_ARGUMENT = 'No Argument.';

/* eslint no-useless-catch: 0 */

/**
 * Camera Control Command V2 Service
 *
 * Service: B6AC7A7E-8C01-4A52-B188-68D53DF53EA2
 */
export class CameraControlCommandV2 extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;

  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.CAMERA_CONTROL_COMMAND_V2;
    this.device = device;
  }

  /**
   * Acquires basic information of the camera and supported functions.
   * 
   * Characteristic: A0452E2D-C7D8-4314-8CD6-7B8BBAB4D523
   * 
   * @returns Static attributes of Theta.
   */
  async getInfo(): Promise<ThetaInfo> {
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2GetInfo(this.device.id);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Acquires the camera states.
   * 
   * Characteristic: 083D92B0-21E0-4FB2-9503-7D8B2C2BB1D1
   * 
   * @returns Mutable values representing Theta status.
   */
  async getState(): Promise<ThetaState> {
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2GetState(this.device.id);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Set camera state notification.
   * 
   * Characteristic: D32CE140-B0C2-4C07-AF15-2301B5057B8C
   * 
   * @param callback Notification function
   */
  async setStateNotify(callback?: (value?: ThetaState, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeCameraControlCommandV2SetStateNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('NOTIFY_STATE', callback ? (event: ThetaStateNotify) => {
        callback(event.params, event.error);
      } : undefined);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Acquires the camera states.
   * 
   * Characteristic: 8881CE4E-96FC-4C6C-8103-5DDA0AD138FB
   * 
   * @returns Mutable values representing Theta status.
   */
  async getState2(): Promise<ThetaState2> {
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2GetState2(this.device.id);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Acquires the properties and property support specifications for shooting, the camera, etc.
   *
   * Characteristic: 7CFFAAE3-8467-4D0C-A9DD-7F70B4F52863
   *
   * @param optionNames List of [OptionName]
   * @return Options acquired
   */
  async getOptions(optionNames: OptionName[]): Promise<ThetaOptions> {
    if (optionNames.length === 0) {
      throw new Error(MESSAGE_NO_ARGUMENT);
    }
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2GetOptions(this.device.id, optionNames ?? []);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Acquires the properties and property support specifications for shooting, the camera, etc.
   *
   * Characteristic: 7CFFAAE3-8467-4D0C-A9DD-7F70B4F52863
   *
   * @param optionNames List of strings representing the names of the options
   * @return A map where each key is an option name and each value is the corresponding value for that option.
   */
  async getOptionsByString(optionNames: string[]): Promise<Record<string, unknown>> {
    if (optionNames.length === 0) {
      throw new Error(MESSAGE_NO_ARGUMENT);
    }
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2GetOptionsByString(
        this.device.id, optionNames ?? [],
      );
    } catch (error) {
      throw error;
    }
  }

  /**
   * Property settings for shooting, the camera, etc.
   *
   * Characteristic: F0BCD2F9-5862-4653-B50D-80DC51E8CB82
   *
   * @param options Camera setting options
   */
  async setOptions(options: ThetaOptions) {
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2SetOptions(this.device.id, options);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Release shutter
   *
   * If CaptureMode is IMAGE, perform `camera.takePicture`
   * If CaptureMode is VIDEO, and CaptureState is IDLE, then `camera.startCapture` is executed
   * If CaptureMode is VIDEO, and CaptureStatus is SHOOTING, then `camera.stopCapture` is executed
   *
   * Characteristic: 6E2DEEBE-88B0-42A5-829D-1B2C6ABCE750
   */
  async releaseShutter() {
    try {
      return await ThetaBleClient.nativeCameraControlCommandV2ReleaseShutter(this.device.id);
    } catch (error) {
      throw error;
    }
  }
}
