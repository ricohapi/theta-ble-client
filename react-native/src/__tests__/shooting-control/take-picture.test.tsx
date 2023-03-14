import { ShootingControlCommand, ThetaDevice } from '../..';
import { NativeModules } from 'react-native';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

const devId = 1;
const devName = '0123456789';

test('Call takePicture normal', async () => {
  jest.mocked(thetaBle.nativeTakePicture).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
    }),
  );

  async function callTakePicture(device: ThetaDevice) {
    const service = new ShootingControlCommand(device);
    return new Promise((resolve) => {
      service.takePicture((error) => {
        expect(error).toBeUndefined();
        resolve(error);
      });
    });
  }

  await callTakePicture(new ThetaDevice(devId, devName));

  expect(thetaBle.nativeTakePicture).toHaveBeenCalledWith(devId);
});

test('Call takePicture without complete', async () => {
  jest.mocked(thetaBle.nativeTakePicture).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  service.takePicture();

  expect(thetaBle.nativeTakePicture).toHaveBeenCalledWith(devId);
});

test('Exception for Call takePicture', async () => {
  jest.mocked(thetaBle.nativeTakePicture).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      throw 'error';
    }),
  );

  async function callTakePicture(device: ThetaDevice) {
    return new Promise((resolve) => {
      const service = new ShootingControlCommand(device);
      service.takePicture((error) => {
        expect(error).toBe('error');
        resolve(error);
      });
    });
  }

  await callTakePicture(new ThetaDevice(devId, devName));

  expect(thetaBle.nativeTakePicture).toHaveBeenCalledWith(devId);
});
