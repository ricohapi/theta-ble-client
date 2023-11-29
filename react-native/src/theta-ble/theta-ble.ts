import { ThetaDevice } from '../theta-device';
import * as ThetaBleClient from '../native';
import type { Timeout } from '../values';
import type { ThetaModel } from '../service';

/**
 * scan properties
 */
interface ScanProps {
  /**
   * Name of THETA to connect.
   */
  name?: string;
  /**
   * Configuration of timeout.
   */
  timeout?: Timeout;
}

async function scanImpl(name?: string, timeout?: Timeout): Promise<ThetaDevice[]> {
  /* eslint-disable-next-line no-useless-catch */
  try {
    const deviceList = await ThetaBleClient.nativeScan({name, timeout});
    const result = deviceList.map((element) => {
      return new ThetaDevice(element.deviceId, element.name);
    });
    return result;
  } catch (error) {
    throw error;
  }
}

/**
 * Scan for nearby THETA.
 * 
 * Call {@link ThetaDevice.release} when the use of the acquired {@link ThetaDevice} is complete.
 * 
 * @param props scan properties.
 * @returns Found THETA. If no name is specified, an array is returned.
 */
export async function scan(props?: ScanProps): Promise<ThetaDevice | ThetaDevice[] | undefined>;

/**
 * Deprecated
 * Scan for nearby THETA.
 * 
 * Call {@link ThetaDevice.release} when the use of the acquired {@link theta-device.ThetaDevice} is complete.
 * 
 * @param name Name of THETA to connect.
 * @param uuid UUID used for authentication.
 * @param timeout Configuration of timeout.
 * @returns Found THETA
 */
export async function scan(name: string, timeout?: Timeout): Promise<ThetaDevice | undefined>;
export async function scan(
  props?: ScanProps | string,
  timeout?: Timeout,
): Promise<ThetaDevice | ThetaDevice[] | undefined> {
  let paramName: string | undefined;
  let paramTimeout = timeout;
  if (typeof props === 'string') {
    paramName = props;
  } else if (props != null) {
    const {name, timeout} = props as ScanProps;
    paramName = name;
    paramTimeout = timeout;
  }

  /* eslint-disable-next-line no-useless-catch */
  try {
    const result = await scanImpl(paramName, paramTimeout);
    if (typeof paramName === 'string') {
      return result.length === 0 ? undefined : result[0];
    } else {
      return result;
    }
  } catch (error) {
    throw error;
  }
}

/**
 * scan SSID properties
 */
interface ScanSsidParams {
  /**
   * THETA model
   */
  model?: ThetaModel;

  /**
   * Specifies a time period (in milliseconds) required to scan THETA.
   */
  timeout?: number;
}

/**
 * SSID and default password
 */
interface SsidListItem {
  /**
   * SSID
   */
  ssid: string;

  /**
   * Default password
   */
  password: string;
}

/**
 * Scan for nearby THETA SSID.
 * 
 * @param params scan SSID properties.
 * @returns Found THETA SSID list.
 */
export async function scanThetaSsid(params?: ScanSsidParams): Promise<SsidListItem[]> {
  return ThetaBleClient.nativeScanThetaSsid(params ?? {});
}
