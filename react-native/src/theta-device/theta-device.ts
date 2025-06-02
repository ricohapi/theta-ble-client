import type {
  EmitterSubscription,
} from 'react-native';
import type {
  BaseNotify,
} from './notify';
import * as ThetaBleClient from '../native';
import {
  BleServiceEnum,
  BluetoothControlCommand,
  CameraControlCommandV2,
  CameraControlCommands,
  CameraInformation,
  CameraStatusCommand, 
  ShootingControlCommand,
  WlanControlCommandV2,
  type ThetaService,
} from '../service';

/* eslint no-useless-catch: 0 */

/**
 * THETA camera device
 * 
 * Call ThetaBle.scan() to obtain.
 */
export class ThetaDevice {
  /**
   * ID of THETA
   * 
   * Granted by {@link theta-ble.scan}
   */
  readonly id: number;
  /**
   * Name of THETA.
   */
  readonly name: string;
  /**
   * UUID registered with THETA.
   */
  get uuid() {
    return this._uuid;
  }
  private _uuid?: string;

  eventListener: EmitterSubscription;
  notifyList = new Map<string, ((notify: BaseNotify) => void) | undefined>();

  /**
   * Use in {@link theta-ble.scan}
   * 
   * @param id ID obtained from scan
   * @param name THETA name
   */
  constructor(id: number, name: string) {
    this.id = id;
    this.name = name;
    this.eventListener = ThetaBleClient.addNotifyListener(notify => {
      if (notify.deviceId === this.id) {
        this.notifyList.get(notify.characteristic)?.(notify);
      }
    });
  }

  /**
   * Connect to THETA.
   */
  async connect(uuid?: string) {
    try {
      await ThetaBleClient.nativeConnect(this.id, uuid);
      if (uuid) {
        this._uuid = uuid;
      }
    } catch(error) {
      throw error;
    }
  }

  /**
   * Whether connected to THETA.
   *
   * @return Whether connected or not.
   */
  async isConnected(): Promise<boolean> {
    return await ThetaBleClient.nativeIsConnected(this.id);
  }
  
  /**
   * Disconnect from THETA.
   */
  async disconnect() {
    try {
      await ThetaBleClient.nativeDisconnect(this.id);
    } catch(error) {
      throw error;
    }
  }

  async getService(service: BleServiceEnum): Promise<ThetaService| undefined> {
    try {
      if (!await ThetaBleClient.nativeContainService(this.id, service)) {
        return;
      }
      return this.newThetaService(service);
    } catch(error) {
      throw error;
    }
  }

  private newThetaService(service: BleServiceEnum): ThetaService | undefined {
    switch (service){
    case BleServiceEnum.CAMERA_CONTROL_COMMANDS:
      return new CameraControlCommands(this);
    case BleServiceEnum.CAMERA_CONTROL_COMMAND_V2:
      return new CameraControlCommandV2(this);
    case BleServiceEnum.CAMERA_INFORMATION:
      return new CameraInformation(this);
    case BleServiceEnum.CAMERA_STATUS_COMMAND:
      return new CameraStatusCommand(this);
    case BleServiceEnum.SHOOTING_CONTROL_COMMAND:
      return new ShootingControlCommand(this);
    case BleServiceEnum.BLUETOOTH_CONTROL_COMMAND:
      return new BluetoothControlCommand(this);
    case BleServiceEnum.WLAN_CONTROL_COMMAND_V2:
      return new WlanControlCommandV2(this);
    default:
      break;
    }
    return;
  }

  /**
   * Clean up resources.
   * 
   * Call when you are done using it.
   */
  release() {
    this.eventListener.remove();
    ThetaBleClient.nativeReleaseDevice(this.id)
      .then()
      .catch();
  }
}
