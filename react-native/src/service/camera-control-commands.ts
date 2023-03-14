/* eslint no-useless-catch: 0 */

import type { ThetaDevice } from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum } from './values';
import type { PluginList, PluginOrders } from './ble';
import * as ThetaBleClient from '../native';

/**
 * Camera Control Commands Service
 *
 * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
 */
export class CameraControlCommands extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;
  
  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.CAMERA_CONTROL_COMMANDS;
    this.device = device;
  }

  /**
   * Acquires a list of installed plugins.
   *
   * RICOH THETA V firmware v2.21.1 or later.
   *
   * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
   * Characteristic: E83264B2-C52D-454E-95BD-6485DE912430
   * 
   * @returns plugin number list
   */
  async getPluginList(): Promise<PluginList> {
    try {
      return await ThetaBleClient.nativeGetPluginList(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the plugins for plugin mode.
   *
   * RICOH THETA Z1 or later.
   *
   * When not specifying, set 0. If an 0 is placed mid-way, it will be moved to the front.
   * Specifying zero plugin will result in an error.
   * 
   * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
   * Characteristic: 8F710EDC-6F9B-45D4-A5F7-E6EDA304E790
   * 
   * @returns plugin order
   */
  async getPluginOrders(): Promise<PluginOrders> {
    try {
      return await ThetaBleClient.nativeGetPluginOrders(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Set the plugins for plugin mode.
   *
   * RICOH THETA Z1 or later.
   * 
   * When not specifying, set 0. If an 0 is placed mid-way, it will be moved to the front.
   * Specifying zero plugin will result in an error.
   *
   * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
   * Characteristic: 8F710EDC-6F9B-45D4-A5F7-E6EDA304E790
   * 
   * @param value plugin order
   */
  async setPluginOrders(value: PluginOrders) {
    try {
      return await ThetaBleClient.nativeSetPluginOrders(this.device.id, value);
    } catch(error) {
      throw error;
    }
  }
}
