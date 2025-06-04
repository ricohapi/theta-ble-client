import type {
  ConnectedWifiInfoNotify,
  NetworkTypeNotify,
  NotifyError,
  ScanSsidNotify,
  ThetaDevice,
} from '../theta-device';
import { ThetaService } from './theta-service';
import { BleServiceEnum, NetworkTypeEnum, WifiSecurityModeEnum, WlanPasswordStateEnum } from './values';
import * as ThetaBleClient from '../native';
import type { ConnectedWifiInfo, Proxy } from './data';

/* eslint no-useless-catch: 0 */

/**
 * WLAN Control Command V2 Service
 *
 * Service: 3C6FEEB6-F335-4F93-A4BB-495F926DB409
 */
export class WlanControlCommandV2 extends ThetaService {
  readonly service: BleServiceEnum;
  readonly device: ThetaDevice;

  constructor(device: ThetaDevice) {
    super();
    this.service = BleServiceEnum.WLAN_CONTROL_COMMAND_V2;
    this.device = device;
  }

  private setNotifyScan(
    callback?: {
      onNotify?: (value: string) => void,
      onCompleted?: (value: string[]) => void,
    },
  ) {
    const notifyCallBack = callback != null ? (event: ScanSsidNotify) => {
      if (event.params?.ssid != null && callback.onNotify != null) {
        callback.onNotify(event.params.ssid);
      }
      if (event.params?.ssidList != null) {
        callback.onCompleted?.(event.params.ssidList);
        // Callback is deleted because it is finished.
        this.device.notifyList.delete('NOTIFICATION_SCANNED_SSID');
      }
    } : undefined;
    this.device.notifyList.set('NOTIFICATION_SCANNED_SSID', notifyCallBack);
  }

  /**
   * Set the network type
   *
   * Characteristic: 4B181146-EF3B-4619-8C82-1BA4A743ACFE
   *
   * @param networkType Network type
   */
  async setNetworkType(networkType: NetworkTypeEnum) {
    try {
      return await ThetaBleClient.nativeWlanControlCommandV2SetNetworkType(this.device.id, networkType);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Set network type notification.
   *
   * Characteristic: 4B181146-EF3B-4619-8C82-1BA4A743ACFE
   *
   * @param callback Notification function
   */
  async setNetworkTypeNotify(callback?: (value?: NetworkTypeEnum, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeWlanControlCommandV2SetNetworkTypeNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('WRITE_SET_NETWORK_TYPE', callback ? (event: NetworkTypeNotify) => {
        callback(event.params?.networkType, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Acquires the Wi-Fi/LAN/LTE connection status
   *
   * Characteristic: 01DFF9FF-00FA-44DD-AA6A-71D5E537ABCF
   *
   * @return Wi-Fi/LAN/LTE connection status
   */
  async getConnectedWifiInfo(): Promise<ConnectedWifiInfo> {
    try {
      return await ThetaBleClient.nativeWlanControlCommandV2GetConnectedWifiInfo(this.device.id);
    } catch (error) {
      throw error;
    }
  }

  /**
   * Set Wi-Fi/LAN/LTE connection status notification
   *
   * Characteristic: A90381FC-2DDA-4EED-B24B-60F3E6651134
   *
   * @param callback Notification function
   */
  async setConnectedWifiInfoNotify(callback?: (value?: ConnectedWifiInfo, error?: NotifyError) => void) {
    try {
      await ThetaBleClient.nativeWlanControlCommandV2SetConnectedWifiInfoNotify(this.device.id, callback ? true : false);
      this.device.notifyList.set('NOTIFICATION_CONNECTED_WIFI_INFO', callback ? (event: ConnectedWifiInfoNotify) => {
        callback(event.params, event.error);
      } : undefined);
    } catch(error) {
      throw error;
    }
  }

  /**
   * Start scanning for SSID
   *
   * Change the network type to SCAN and start scanning for SSID
   *
   * Notify characteristic: 60EEDCCC-426A-49CF-9AE1-F602284703D7
   *
   * @param timeout Timeout of scanning
   * @param callback Notification function
   */
  async scanSsidStart(
    timeout: number,
    onNotify: (ssid: string) => void,
    onCompleted?: (ssidList: string[]) => void,
  ) {
    try {
      await ThetaBleClient.nativeWlanControlCommandV2ScanSsidStart(
        this.device.id, timeout);
      this.setNotifyScan({ onNotify, onCompleted });
    } catch (error) {
      this.setNotifyScan();
      throw error;
    }
  }

  /**
   * Scanning stop for SSID
   *
   * Notify characteristic: 60EEDCCC-426A-49CF-9AE1-F602284703D7
   */
  async scanSsidStop() {
    try {
      await ThetaBleClient.nativeWlanControlCommandV2ScanSsidStop(this.device.id);
      this.setNotifyScan();
    } catch (error) {
      throw error;
    }
  }

  /**
   * Set access point. IP address is set dynamically.
   *
   * Characteristic: 4980ACBA-E2A5-460B-998B-9AD4C49FBE39
   *
   * @param ssid SSID of the access point.
   * @param ssidStealth True if SSID stealth is enabled.
   * @param security Authentication mode.
   * @param password Password. If [security] is "NONE", pass empty String.
   * @param connectionPriority Connection priority 1 to 5.
   * @param proxy Proxy information to be used for the access point.
   */
  async setAccessPointDynamically(
    ssid: string,
    ssidStealth = false,
    security: WifiSecurityModeEnum = WifiSecurityModeEnum.NONE,
    password = '',
    connectionPriority = 1,
    proxy?: Proxy,
  ) {
    try {
      await ThetaBleClient.nativeWlanControlCommandV2SetAccessPointDynamically(
        this.device.id,
        {
          ssid,
          ssidStealth,
          security,
          password,
          connectionPriority,
          proxy,
        },
      );
    } catch (error) {
      throw error;
    }
  }
  
  /**
   * Set access point. IP address is set statically.
   *
   * Characteristic: 4980ACBA-E2A5-460B-998B-9AD4C49FBE39
   *
   * @param ssid SSID of the access point.
   * @param ssidStealth True if SSID stealth is enabled.
   * @param security Authentication mode.
   * @param password Password. If [security] is "NONE", pass empty String.
   * @param connectionPriority Connection priority 1 to 5.
   * @param ipAddress IP address assigns to Theta.
   * @param subnetMask Subnet mask.
   * @param defaultGateway Default gateway.
   * @param proxy Proxy information to be used for the access point.
   */
  async setAccessPointStatically(
    ssid: string,
    ssidStealth = false,
    security: WifiSecurityModeEnum = WifiSecurityModeEnum.NONE,
    password = '',
    connectionPriority = 1,
    ipAddress: string,
    subnetMask: string,
    defaultGateway: string,
    proxy?: Proxy,
  ) {
    try {
      await ThetaBleClient.nativeWlanControlCommandV2SetAccessPointStatically(
        this.device.id,
        {
          ssid,
          ssidStealth,
          security,
          password,
          connectionPriority,
          ipAddress,
          subnetMask,
          defaultGateway,
          proxy,
        },
      );
    } catch (error) {
      throw error;
    }
  }

  /**
   * Read WLAN password state.
   *
   * Service: 3C6FEEB6-F335-4F93-A4BB-495F926DB409
   * Characteristic: E522112A-5689-4901-0803-0520637DC895
   * 
   * @returns start-up status
   */
  async getWlanPasswordState(): Promise<WlanPasswordStateEnum> {
    try {
      return await ThetaBleClient.nativeWlanControlCommandV2GetWlanPasswordState(this.device.id);
    } catch (error) {
      throw error;
    }
  }
}
