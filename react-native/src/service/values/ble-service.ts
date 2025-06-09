/**
 * BLE service
 */
export const BleServiceEnum = {
  /**
   * Bluetooth Control Command
   * 
   * Service: 0F291746-0C80-4726-87A7-3C501FD3B4B6
   */
  BLUETOOTH_CONTROL_COMMAND: 'BLUETOOTH_CONTROL_COMMAND',

  /**
   * Camera Information
   * 
   * Service: 9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1
   */
  CAMERA_INFORMATION: 'CAMERA_INFORMATION',

  /**
   * Camera Status Command
   * 
   * Service: 8AF982B1-F1FF-4D49-83F0-A56DB4C431A7
   */
  CAMERA_STATUS_COMMAND: 'CAMERA_STATUS_COMMAND',

  /**
   * Camera Control Commands
   * 
   * Service: 32886D39-BA23-425C-BCAE-9C1DB0066922
   */
  CAMERA_CONTROL_COMMANDS: 'CAMERA_CONTROL_COMMANDS',

  /**
   * Camera Control Command v2
   * 
   * Service: B6AC7A7E-8C01-4A52-B188-68D53DF53EA2
   */
  CAMERA_CONTROL_COMMAND_V2: 'CAMERA_CONTROL_COMMAND_V2',

  /**
   * Shooting Control Command
   * 
   * Service: 1D0F3602-8DFB-4340-9045-513040DAD991
   */
  SHOOTING_CONTROL_COMMAND: 'SHOOTING_CONTROL_COMMAND',

  /**
   * WLAN Control Command v2
   * 
   * Service: 3C6FEEB6-F335-4F93-A4BB-495F926DB409
   */
  WLAN_CONTROL_COMMAND_V2: 'WLAN_CONTROL_COMMAND_V2',

} as const;

/** type definition of BleServiceEnum */
export type BleServiceEnum =
  typeof BleServiceEnum[keyof typeof BleServiceEnum];
