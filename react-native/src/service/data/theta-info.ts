import type { ThetaModel } from '../values/theta-model';

/**
 * Static attributes of Theta.
 */
export interface ThetaInfo {
  /** Manufacturer name */
  manufacturer: string;
  /** Theta model name */
  model: ThetaModel;
  /** Theta serial number */
  serialNumber: string;
  /** MAC address of wireless LAN */
  wlanMacAddress?: string;
  /** MAC address of Bluetooth */
  bluetoothMacAddress?: string;
  /** Theta firmware version */
  firmwareVersion: string;
  /** Number of seconds since Theta boot */
  uptime: number;
}
