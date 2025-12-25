/** Camera setting options name. */
export const OptionName = {
  /** _accessInfo */
  AccessInfo: 'AccessInfo',
  /** cameraPower */
  CameraPower: 'CameraPower',
  /** captureMode */
  CaptureMode: 'CaptureMode',
  /** defaultWifiPassword */
  DefaultWifiPassword: 'DefaultWifiPassword',
  /** networkType */
  NetworkType: 'NetworkType',
  /** password */
  Password: 'Password',
  /** ssid */
  Ssid: 'Ssid',
  /** username */
  Username: 'Username',
  /** wlanAntennaConfig */
  WlanAntennaConfig: 'WlanAntennaConfig',
  /** wlanFrequency */
  WlanFrequency: 'WlanFrequency',
  /** wifiPassword */
  WifiPassword: 'WifiPassword',
} as const;

/** type definition of OptionNameEnum */
export type OptionName =
  (typeof OptionName)[keyof typeof OptionName];
