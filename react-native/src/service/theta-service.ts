import type { ThetaDevice } from '../theta-device';
import type { BleServiceEnum } from './values/ble-service';

export abstract class ThetaService {
  abstract readonly service: BleServiceEnum;
  abstract readonly device: ThetaDevice;
}
