import type { CameraPowerEnum, CaptureModeEnum, NetworkTypeEnum, WlanAntennaConfigEnum, WlanFrequencyEnum } from '../values';
import type { AccessInfo } from './access-info';

/** camera setting options */
export interface ThetaOptions {
  /** Connected network information */
  accessInfo?: AccessInfo;
  /** Camera power state */
  cameraPower?: CameraPowerEnum;
  /** Shooting mode. */
  captureMode?: CaptureModeEnum;
  /** Default WiFi password in AP mode (factory settings). */
  defaultWifiPassword?: string;
  /** Network type of the camera */
  networkType?: NetworkTypeEnum;
  /** Password used for digest authentication when _networkType is set to client mode. */
  password?: string;
  /** SSID to connect when in AP mode. */
  ssid?: string;
  /** User name used for digest authentication when _networkType is set to client mode. */
  username?: string;
  /** Configure SISO or MIMO for Wireless LAN. */
  wlanAntennaConfig?: WlanAntennaConfigEnum;
  /** WlanAntennaConfig */
  wlanFrequency?: WlanFrequencyEnum;
}
