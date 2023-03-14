/* eslint no-useless-catch: 0 */

import type {
  BatteryLevelNotify,
  BatteryStatusNotify, 
  CameraPowerNotify,
  CommandErrorDescriptionNotify, 
  NotifyError,
  PluginControlNotify,
  ThetaDevice,
} from '../theta-device';
import { ThetaService } from './theta-service';
import {
  BleServiceEnum,
  CameraPowerEnum,
  ChargingStateEnum, 
  CommandErrorDescriptionEnum, 
} from './values';
import * as ThetaBleClient from '../native';
import type { PluginControl } from './ble';

/**
 * Camera Status Command Service
 *
 * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
 */
export class CameraStatusCommand extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;
  
  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.CAMERA_STATUS_COMMAND;
    this.device = device;
  }

  /**
   * Acquires the battery level of the camera.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: 875FC41D-4980-434C-A653-FD4A4D4410C4
   * 
   * @returns battery level. 0 to 100
   */
  async getBatteryLevel(): Promise<number> {
    try {
      return await ThetaBleClient.nativeGetBatteryLevel(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the battery level notification.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: 875FC41D-4980-434C-A653-FD4A4D4410C4
   *
   * @param callback Notification function
   */
  async setBatteryLevelNotify(callback?: (value?: number, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeSetBatteryLevelNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('BATTERY_LEVEL', callback ? (event: BatteryLevelNotify) => {
        callback(event.params?.batteryLevel, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the charging state of the camera.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: 5429B6A0-66D6-491B-B906-902737D5442F
   * 
   * @returns charging state
   */
  async getBatteryStatus(): Promise<ChargingStateEnum> {
    try {
      return await ThetaBleClient.nativeGetBatteryStatus(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the charging state notification.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: 5429B6A0-66D6-491B-B906-902737D5442F
   *
   * @param callback Notification function
   */
  async setBatteryStatusNotify(callback?: (value?: ChargingStateEnum, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeSetBatteryStatusNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('BATTERY_STATUS', callback ? (event: BatteryStatusNotify) => {
        callback(event.params?.batteryState, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the camera's start-up status.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: B58CE84C-0666-4DE9-BEC8-2D27B27B3211
   * 
   * @returns start-up status
   */
  async getCameraPower(): Promise<CameraPowerEnum> {
    try {
      return await ThetaBleClient.nativeGetCameraPower(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the camera's start-up status.
   *
   * When the camera is turned off or put to sleep, it is necessary to reauthorize from connect.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: B58CE84C-0666-4DE9-BEC8-2D27B27B3211
   * 
   * @param value start-up status
   */
  async setCameraPower(value: CameraPowerEnum) {
    try {
      return await ThetaBleClient.nativeSetCameraPower(this.device.id, value);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the camera's start-up status notification.
   *
   * When the camera is turned off or put to sleep, it is necessary to reauthorize from connect.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: B58CE84C-0666-4DE9-BEC8-2D27B27B3211
   *
   * @param callback Notification function
   */
  async setCameraPowerNotify(callback?: (value?: CameraPowerEnum, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeSetCameraPowerNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('CAMERA_POWER', callback ? (event: CameraPowerNotify) => {
        callback(event.params?.cameraPower, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the camera's error description in detail notification.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: 4B03D05E-02D2-412B-A20B-578AE82B9C01
   * 
   * @param callback Notification function
   */
  async setCommandErrorDescriptionNotify(callback?: (value?: CommandErrorDescriptionEnum, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeSetCommandErrorDescriptionNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('COMMAND_ERROR_DESCRIPTION', callback ? (event: CommandErrorDescriptionNotify) => {
        callback(event.params?.commandErrorDescription, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the plugin power status.
   *
   * RICOH THETA V firmware v2.21.1 or later.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: A88732D5-6786-4312-9364-B9A4514DC123
   * 
   * @returns plugin control
   */
  async getPluginControl(): Promise<PluginControl> {
    try {
      return await ThetaBleClient.nativeGetPluginControl(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Starts or stops plugin.
   *
   * RICOH THETA V firmware v2.21.1 or later.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: A88732D5-6786-4312-9364-B9A4514DC123
   * 
   * @param value plugin control
   * @returns 
   */
  async setPluginControl(value: PluginControl) {
    try {
      return await ThetaBleClient.nativeSetPluginControl(this.device.id, value);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the plugin power status notification.
   *
   * RICOH THETA V firmware v2.21.1 or later.
   *
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   * Characteristic: A88732D5-6786-4312-9364-B9A4514DC123
   * 
   * @param callback Notification function
   */
  async setPluginControlNotify(callback?: (value?: PluginControl, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeSetPluginControlNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('PLUGIN_CONTROL', callback ? (event: PluginControlNotify) => {
        callback(event.params, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }
}
