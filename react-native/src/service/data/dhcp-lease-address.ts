/**
 * client devices information
 */
export interface DhcpLeaseAddress {
  /**
   * IP address of client device
   */
  ipAddress: string;

  /**
   * MAC address of client device
   */
  macAddress: string;

  /**
   * host name of client device
   */
  hostName: string;
}
