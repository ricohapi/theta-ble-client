/**
 * Wifi Encryption Types
 */
export const WifiSecurityModeEnum = {
  /** Undefined value */
  UNKNOWN: 'UNKNOWN',
  /** none */
  NONE: 'NONE',
  /** WEP */
  WEP: 'WEP',
  /** WPA/WPA2 PSK */
  WPA_WPA2_PSK: 'WPA_WPA2_PSK',
  /** WPA3-SAE */
  WPA3_SAE: 'WPA3_SAE',
} as const;

/** type definition of WifiSecurityModeEnum */
export type WifiSecurityModeEnum =
  typeof WifiSecurityModeEnum[keyof typeof WifiSecurityModeEnum];
