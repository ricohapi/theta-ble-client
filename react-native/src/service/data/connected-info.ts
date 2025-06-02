/**
 * Wi-Fi connection status (Wifi connection, SSID, Internet access)
 */
export interface ConnectedInfo {
  /**
   * SSID
   *
   * If there is no SSID to connect to, "unknown ssid"
   */
  ssid: string;

  /** Whether you are connected to an Access Point */
  isConnected: boolean;

  /**
   * Availability of Internet access
   *
   * Whether ping 8.8.8.8 is accessible
   */
  isInternetAccessible: boolean;
};
