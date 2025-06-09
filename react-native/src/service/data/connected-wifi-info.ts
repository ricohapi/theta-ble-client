import type { ConnectedInfo } from './connected-info';

/**
 * Wi-Fi connection status (Wifi connection, SSID, Internet access)
 */
export interface ConnectedWifiInfo {
  /**
   * State of Wi-Fi connection case
   */
  wifiInfo?: ConnectedInfo;

  /**
   * State of ethernet connection case
   */
  ethernet?: ConnectedInfo;

  /**
   * State of LTE connection case
   */
  lte?: ConnectedInfo;
};
