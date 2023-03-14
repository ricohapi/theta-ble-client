import { ThetaDevice } from '../theta-device';
import { NativeModules } from 'react-native';
import { BleServiceEnum } from '../service';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

afterEach(() => {
  thetaBle.nativeContainService = jest.fn();
});

describe('ThetaDevice service', () => {

  const devId = 1;
  const devName = '0123456789';

  test.each([
    BleServiceEnum.CAMERA_CONTROL_COMMANDS,
    BleServiceEnum.CAMERA_CONTROL_COMMAND_V2,
    BleServiceEnum.CAMERA_INFORMATION,
    BleServiceEnum.CAMERA_STATUS_COMMAND,
    BleServiceEnum.SHOOTING_CONTROL_COMMAND,
  ])(
    'getService normal',
    async (serviceEnum) => {
      const testService = serviceEnum;
      jest.mocked(thetaBle.nativeContainService).mockImplementation(
        jest.fn(async (id, service) => {
          expect(id).toBe(devId);
          expect(service).toBe(testService);
          return true;
        }),
      );
  
      const device = new ThetaDevice(devId, devName);
      const service = await device.getService(serviceEnum);
  
      expect(service?.device.id).toBe(devId);
      expect(service?.service).toBe(testService);
      expect(thetaBle.nativeContainService).toHaveBeenCalledWith(devId, testService);
    },
  );

  test('getService unsupported', async () => {
    const testService = BleServiceEnum.CAMERA_CONTROL_COMMAND_V2;
    jest.mocked(thetaBle.nativeContainService).mockImplementation(
      jest.fn(async (id, service) => {
        expect(id).toBe(devId);
        expect(service).toBe(testService);
        return false;   // unsupported
      }),
    );

    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);

    expect(service).toBeUndefined();
    expect(thetaBle.nativeContainService).toHaveBeenCalledWith(devId, testService);
  });
});
