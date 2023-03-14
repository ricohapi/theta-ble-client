import type { ThetaDevice } from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum } from './values';
import * as ThetaBleClient from '../native';

/* eslint no-useless-catch: 0 */

/**
 * Camera Information Service
 *
 * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
 */
export class CameraInformation extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;
  
  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.CAMERA_INFORMATION;
    this.device = device;
  }

  /**
   * Acquires the firmware version of the camera.
   *
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   * 
   * Characteristic: B4EB8905-7411-40A6-A367-2834C2157EA7
   * 
   * @returns Firmware revision.
   */
  async getFirmwareRevision(): Promise<string> {
    try {
      return await ThetaBleClient.nativeGetFirmwareRevision(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the manufacturer name of the camera.
   *
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   * 
   * Characteristic: F5666A48-6A74-40AE-A817-3C9B3EFB59A6
   * 
   * @returns Manufacturer Name.
   */
  async getManufacturerName(): Promise<string> {
    try {
      return await ThetaBleClient.nativeGetManufacturerName(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the model name of the camera.
   *
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   * 
   * Characteristic: 35FE6272-6AA5-44D9-88E1-F09427F51A71
   * 
   * @returns Model Number of THETA.
   */
  async getModelNumber(): Promise<string> {
    try {
      return await ThetaBleClient.nativeGetModelNumber(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the serial name of the camera.
   *
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   * 
   * Characteristic: 0D2FC4D5-5CB3-4CDE-B519-445E599957D8
   * 
   * @returns Serial Number of THETA.
   */
  async getSerialNumber(): Promise<string> {
    try {
      return await ThetaBleClient.nativeGetSerialNumber(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the MAC address of wireless LAN.
   *
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   * 
   * Characteristic: 1C5C6C55-8E57-4B32-AD80-B124AE229DEC
   * 
   * @returns WLAN MAC Address of THETA.
   */
  async getWlanMacAddress(): Promise<string> {
    try {
      return await ThetaBleClient.nativeGetWlanMacAddress(this.device.id);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the MAC address of Bluetooth.
   *
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   * 
   * Characteristic: 97E34DA2-2E1A-405B-B80D-F8F0AA9CC51C
   * 
   * @returns Bluetooth MAC Address of THETA.
   */
  async getBluetoothMacAddress(): Promise<string> {
    try {
      return await ThetaBleClient.nativeGetBluetoothMacAddress(this.device.id);
    } catch(error) {
      throw error;
    }
  }
}
