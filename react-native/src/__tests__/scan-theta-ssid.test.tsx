import { ThetaModel, scanThetaSsid } from '..';
import { NativeModules } from 'react-native';

describe('scanThetaSsid', () => {
  const thetaBle = NativeModules.ThetaBleClientReactNative;
  const ssid = 'aaaaa.osc';
  const password = '0123456789';
  const model = ThetaModel.THETA_X;

  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(thetaBle.nativeScanThetaSsid).mockImplementation(
      jest.fn(async () => {
        return [{
          ssid,
          password,
        }];
      }),
    );
  });

  afterEach(() => {
    thetaBle.nativeScanThetaSsid = jest.fn();
  });

  test('Call scanThetaSsid normal', async () => {
    const ssidList = await scanThetaSsid({model});

    expect(thetaBle.nativeScanThetaSsid).toHaveBeenCalledWith({
      model: model,
      timeout: undefined,
    });
    expect(ssidList[0]?.ssid).toBe(ssid);
    expect(ssidList[0]?.password).toBe(password);
  });

  test('scanThetaSsid with timeout', async () => {
    jest.mocked(thetaBle.nativeScanThetaSsid).mockImplementation(
      jest.fn(async () => {
        return [];
      }),
    );

    const timeout = 100;
    const ssidList = await scanThetaSsid({
      model,
      timeout,
    });

    expect(thetaBle.nativeScanThetaSsid).toHaveBeenCalledWith({
      model: model,
      timeout: timeout,
    });
    expect(ssidList.length).toBe(0);
  });

  test('Exception for call scanThetaSsid', async () => {
    jest.mocked(thetaBle.nativeScanThetaSsid).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    try {
      await scanThetaSsid();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeScanThetaSsid).toHaveBeenCalledWith({
      model: undefined,
      timeout: undefined,
    });
  });

});
