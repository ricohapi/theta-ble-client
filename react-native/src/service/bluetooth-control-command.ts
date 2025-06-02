import type {  ScanPeripheralDeviceCompletedNotify, ScanPeripheralDeviceNotify, ThetaDevice } from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum } from './values';
import * as ThetaBleClient from '../native';
import type { PeripheralDevice } from './data';

/* eslint no-useless-catch: 0 */

/**
 * Bluetooth Control Command Service
 *
 * Service: 0F291746-0C80-4726-87A7-3C501FD3B4B6
 */
export class BluetoothControlCommand extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;

  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.BLUETOOTH_CONTROL_COMMAND;
    this.device = device;
  }

  private setNotifyScan(callback?: (value: PeripheralDevice) => void) {
    const notifyCallBack = callback != null ? (event: ScanPeripheralDeviceNotify) => {
      if (event.params != null) {
        callback(event.params);
      }
    } : undefined;
    this.device.notifyList.set('NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE', notifyCallBack);
  }

  private setCompletedScan(callback?: (value: PeripheralDevice[]) => void) {
    const notifyCallBack = callback != null ? (event: ScanPeripheralDeviceCompletedNotify) => {
      if (event.params != null) {
        this.device.notifyList.delete('NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE');
        this.device.notifyList.delete('SCAN_BLUETOOTH_PERIPHERAL_DEVICE');
        callback(event.params);
      }
    } : undefined;
    this.device.notifyList.set('SCAN_BLUETOOTH_PERIPHERAL_DEVICE', notifyCallBack);
    if (!notifyCallBack) {
      this.setNotifyScan();
    }
  }

  /**
   * Scanning for peripheral device
   *
   * Scan characteristic: 03F423B3-A71F-4D70-A4BC-437C3137AFCD
   * Notify characteristic: 7B058429-DF5C-4454-88A2-C81086131C30
   *
   * @param timeout Timeout of scanning
   * @return Scanned peripheral device list.
   */
  async scanPeripheralDevice(timeout: number): Promise<PeripheralDevice[]> {
    try {
      await this.scanPeripheralDeviceStop();
      return await ThetaBleClient.nativeBluetoothControlCommandScanPeripheralDevice(
        this.device.id, timeout);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Scanning start for peripheral device
   *
   * onCompleted is called in a timeout.
   *
   * Scan characteristic: 03F423B3-A71F-4D70-A4BC-437C3137AFCD
   * Notify characteristic: 7B058429-DF5C-4454-88A2-C81086131C30
   *
   * @param timeout Timeout of scanning
   * @param onNotify Notification of discovery of peripheral device
   * @param onCompleted Notification of scan completion
   */
  async scanPeripheralDeviceStart(
    timeout: number,
    onNotify: (peripheralDevice: PeripheralDevice) => void,
    onCompleted?: (peripheralDeviceList: PeripheralDevice[]) => void,
  ) {
    try {
      await this.scanPeripheralDeviceStop();
      this.setNotifyScan(onNotify);
      this.setCompletedScan(onCompleted);
      return await ThetaBleClient.nativeBluetoothControlCommandScanPeripheralDeviceStart(
        this.device.id, timeout);
    } catch (error) {
      this.setNotifyScan();
      this.setCompletedScan();
      throw error;
    }
  }

  /**
   * Scanning stop for peripheral device
   *
   * Notify characteristic: 7B058429-DF5C-4454-88A2-C81086131C30
   */
  async scanPeripheralDeviceStop() {
    try {
      this.setNotifyScan();
      await ThetaBleClient.nativeBluetoothControlCommandScanPeripheralDeviceStop(this.device.id);
      this.setCompletedScan();
    } catch (error) {
      throw error;
    }
  }

  /**
   * Connect to peripheral device
   *
   * Characteristic: 1FA3E524-BAD5-4F75-808B-94487A4B9024
   *
   * @param macAddress MAC address of peripheral device
   */
  async connectPeripheralDevice(peripheralDevice: PeripheralDevice) {
    try {
      return await ThetaBleClient.nativeBluetoothControlCommandConnectPeripheralDevice(
        this.device.id, peripheralDevice.macAddress);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Unsubscribe from peripheral device
   *
   * Characteristic: 61A37C82-D635-43B9-A973-5857EFE64094
   *
   * @param peripheralDevice Peripheral device
   */
  async deletePeripheralDevice(peripheralDevice: PeripheralDevice) {
    try {
      return await ThetaBleClient.nativeBluetoothControlCommandDeletePeripheralDevice(
        this.device.id, peripheralDevice.macAddress);
    } catch (error) {
      throw error;
    }
  }

}
