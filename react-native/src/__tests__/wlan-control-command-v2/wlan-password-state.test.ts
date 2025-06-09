import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { WlanControlCommandV2, WlanPasswordStateEnum } from '../../service';

describe('WlanPasswordStateEnum', () => {
  const data: [WlanPasswordStateEnum, string][] = [
    [WlanPasswordStateEnum.UNKNOWN, 'UNKNOWN'],
    [WlanPasswordStateEnum.SERIAL, 'SERIAL'],
    [WlanPasswordStateEnum.RANDOM, 'RANDOM'],
    [WlanPasswordStateEnum.CHANGED, 'CHANGED'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(WlanPasswordStateEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});

describe('Wlan Password State', () => {
  const devId = 99;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterEach(() => {
    thetaBle.nativeWlanControlCommandV2GetWlanPasswordState = jest.fn();
  });

  test('Call normal get', async () => {
    const bleValue = WlanPasswordStateEnum.SERIAL;
    jest.mocked(thetaBle.nativeWlanControlCommandV2GetWlanPasswordState).mockImplementation(
      jest.fn(async (id) => {
        expect(id).toBe(devId);
        return bleValue;
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new WlanControlCommandV2(device);
    const response = await service.getWlanPasswordState();

    expect(response).toBe(bleValue);
    expect(thetaBle.nativeWlanControlCommandV2GetWlanPasswordState).toHaveBeenCalledWith(devId);
  });

  test('Exception get', async () => {
    jest.mocked(thetaBle.nativeWlanControlCommandV2GetWlanPasswordState).mockImplementation(
      jest.fn(async () => {
        throw 'error';
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = new WlanControlCommandV2(device);
    try {
      await service.getWlanPasswordState();
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }

    expect(thetaBle.nativeWlanControlCommandV2GetWlanPasswordState).toHaveBeenCalledWith(devId);
  });
});
