import type { ThetaDevice } from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum, WlanPasswordStateEnum } from './values';
import * as ThetaBleClient from '../native';

/* eslint no-useless-catch: 0 */

/**
 * WLAN Control Command Service
 *
 * Service: F37F568F-9071-445D-A938-5441F2E82399
 */
export class WlanControlCommand extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;

  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.WLAN_CONTROL_COMMAND;
    this.device = device;
  }

  /**
   * Read WLAN password state.
   *
   * Characteristic: E522112A-5689-4901-0803-0520637DC895
   * 
   * @returns start-up status
   */
  async getWlanPasswordState(): Promise<WlanPasswordStateEnum> {
    try {
      return await ThetaBleClient.nativeWlanControlCommandGetWlanPasswordState(this.device.id);
    } catch (error) {
      throw error;
    }
  }
}
