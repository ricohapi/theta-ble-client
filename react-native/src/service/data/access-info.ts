import { WlanFrequencyEnum } from '../values';
import { DhcpLeaseAddress } from './dhcp-lease-address';

/**
 * Connected network information.
 */
export interface AccessInfo {
  /**
   * SSID of the wireless LAN access point that THETA connects to
   */
  ssid: string;

  /**
   * IP address of access point
   */
  ipAddress: string;

  /**
   * subnet mask of access point
   */
  subnetMask: string;

  /**
   * default gateway of access point
   */
  defaultGateway: string;

  /**
   * proxy URL of access point
   */
  proxyURL: string;

  /**
   * Radio frequency.
   * @see WlanFrequencyEnum
   */
  frequency: WlanFrequencyEnum;

  /**
   * WLAN signal strength.
   */
  wlanSignalStrength: number;

  /**
   * WLAN signal level.
   */
  wlanSignalLevel: number;

  /**
   * LTE signal strength.
   */
  lteSignalStrength: number;

  /**
   * LTE signal level.
   */
  lteSignalLevel: number;

  /**
   * client devices information
   */
  dhcpLeaseAddress?: DhcpLeaseAddress[];
};
