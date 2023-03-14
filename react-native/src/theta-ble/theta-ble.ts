import { ThetaDevice } from '../theta-device';
import * as ThetaBleClient from '../native';
import type { Timeout } from '../values';

/**
 * scan properties
 */
interface ScanProps {
  /**
   * Name of THETA to connect.
   */
  name: string;
  /**
   * Configuration of timeout.
   */
  timeout?: Timeout;
}

/**
 * Scan for nearby THETA.
 * 
 * Call {@link ThetaDevice.release} when the use of the acquired {@link theta-device.ThetaDevice} is complete.
 * 
 * @param props scan properties.
 * @returns Found ID of ThetaDevice
 */
export async function scan(props: ScanProps): Promise<ThetaDevice | undefined>;

/**
 * Deprecated
 * Scan for nearby THETA.
 * 
 * Call {@link ThetaDevice.release} when the use of the acquired {@link theta-device.ThetaDevice} is complete.
 * 
 * @param name Name of THETA to connect.
 * @param uuid UUID used for authentication.
 * @param timeout Configuration of timeout.
 * @returns Found ID of ThetaDevice
 */
export async function scan(name: string, timeout?: Timeout): Promise<ThetaDevice | undefined>;
export async function scan(props: ScanProps | string, timeout?: Timeout): Promise<ThetaDevice | undefined> {
  let paramName = '';
  let paramTimeout = timeout;
  if (typeof props === 'string' ) {
    paramName = props;
  } else {
    const {name, timeout} = props as ScanProps;
    paramName = name;
    paramTimeout = timeout;
  }

  return new Promise((resolve, reject) => {
    ThetaBleClient.nativeScan(paramName, paramTimeout)
      .then((result) => {
        if (result !== 0) {
          resolve(new ThetaDevice(result, paramName));
        } else {
          resolve(undefined);
        }
      })
      .catch((error) => {
        reject(error);
      });
  });
}
