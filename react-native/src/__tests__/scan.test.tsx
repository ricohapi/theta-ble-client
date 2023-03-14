import { TIMEOUT_CONNECT, TIMEOUT_PERIPHERAL, TIMEOUT_SCAN, TIMEOUT_TAKE_PICTURE, TimeoutObject, scan } from '..';
import { NativeModules } from 'react-native';

describe('scan', () => {
  const thetaBle = NativeModules.ThetaBleClientReactNative;
  const devId = 1;
  const devName = '0123456789';

  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(thetaBle.nativeScan).mockImplementation(
      jest.fn(async () => devId),
    );
  });

  afterEach(() => {
    thetaBle.nativeScan = jest.fn();
  });

  test('Call scan normal', async () => {
    const device = await scan(devName);

    expect(thetaBle.nativeScan).toHaveBeenCalledWith({
      name: devName,
      timeout: undefined,
    });
    expect(device?.id).toBe(devId);
    expect(device?.name).toBe(devName);
    expect(device?.uuid).toBeUndefined();
  });

  test('Timeout for call scan', async () => {
    jest.mocked(thetaBle.nativeScan).mockImplementation(
      jest.fn(async () => {
        return 0;
      }),
    );

    const device = await scan(devName);

    expect(thetaBle.nativeScan).toHaveBeenCalledWith({
      name: devName,
      timeout: undefined,
    });
    expect(device).toBeUndefined();
  });

  test('scan with props', async () => {
    jest.mocked(thetaBle.nativeScan).mockImplementation(
      jest.fn(async () => {
        return 0;
      }),
    );

    const timeout = new TimeoutObject({timeoutScan: 100});
    const device = await scan({
      name: devName,
      timeout,
    });

    expect(thetaBle.nativeScan).toHaveBeenCalledWith({
      name: devName,
      uuid: undefined,
      timeout: timeout,
    });
    expect(device).toBeUndefined();
  });

  test('Exception for call scan', async () => {
    jest.mocked(thetaBle.nativeScan).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    try {
      await scan(devName);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeScan).toHaveBeenCalledWith({
      name: devName,
      timeout: undefined,
    });
  });

  test.each([
    [new TimeoutObject({timeoutScan: 100}), 100, TIMEOUT_PERIPHERAL, TIMEOUT_CONNECT, TIMEOUT_TAKE_PICTURE],
    [new TimeoutObject({timeoutPeripheral: 100}), TIMEOUT_SCAN, 100, TIMEOUT_CONNECT, TIMEOUT_TAKE_PICTURE],
    [new TimeoutObject({timeoutConnect: 100}), TIMEOUT_SCAN, TIMEOUT_PERIPHERAL, 100, TIMEOUT_TAKE_PICTURE],
    [new TimeoutObject({timeoutTakePicture: 100}), TIMEOUT_SCAN, TIMEOUT_PERIPHERAL, TIMEOUT_CONNECT, 100],
  ])('set timeout', async (
    testValue,
    timeoutScan,
    timeoutPeripheral,
    timeoutConnect,
    timeoutTakePicture,
  ) => {

    jest.mocked(thetaBle.nativeScan).mockImplementation(
      jest.fn(async ({timeout}) => {
        expect(timeout.timeoutScan).toBe(timeoutScan);
        expect(timeout.timeoutPeripheral).toBe(timeoutPeripheral);
        expect(timeout.timeoutConnect).toBe(timeoutConnect);
        expect(timeout.timeoutTakePicture).toBe(timeoutTakePicture);
      }),
    );
    await scan(devName, testValue);

    expect(thetaBle.nativeScan).toHaveBeenCalledWith({
      name: devName,
      timeout: testValue,
    });

  });
});
