import { PeripheralDeviceStatusEnum } from '../values';

/**
 * Peripheral Device
 */
export interface PeripheralDevice {
  /**
   * Device name
   */
  device: string;

  /**
   * MAC address
   */
  macAddress: string;

  /**
   * Paired or not
   */
  pairing: boolean;

  /**
   * State of connection
   */
  status: PeripheralDeviceStatusEnum;
}
