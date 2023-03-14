import { CaptureModeEnum, ShootingControlCommand, ThetaDevice } from '../..';
import { NativeModules } from 'react-native';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

afterEach(() => {
  thetaBle.nativeGetCaptureMode = jest.fn();
  thetaBle.nativeSetCaptureMode = jest.fn();
});

const devId = 1;
const devName = '0123456789';

async function getCaptureModeTest(captureMode: CaptureModeEnum) {
  jest.mocked(thetaBle.nativeGetCaptureMode).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return captureMode as string;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  const response = await service.getCaptureMode();

  expect(response).toBe(captureMode);
  expect(thetaBle.nativeGetCaptureMode).toHaveBeenCalledWith(devId);
}

test('Call getCaptureMode normal', () => {
  const valueList = [
    CaptureModeEnum.IMAGE,
    CaptureModeEnum.VIDEO,
    CaptureModeEnum.LIVE,
  ];
  valueList.forEach(function(element){
    getCaptureModeTest(element);
  });
});

test('Exception for Call getCaptureMode', async () => {
  jest.mocked(thetaBle.nativeGetCaptureMode).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  try {
    await service.getCaptureMode();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetCaptureMode).toHaveBeenCalledWith(devId);
});

async function setCaptureModeTest(captureMode: CaptureModeEnum) {
  jest.mocked(thetaBle.nativeSetCaptureMode).mockImplementation(
    jest.fn(async (id, mode: string) => {
      expect(id).toBe(devId);
      expect(mode).toBe(captureMode as string);
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  await service.setCaptureMode(captureMode);

  expect(thetaBle.nativeSetCaptureMode).toHaveBeenCalledWith(devId, captureMode as string);
}

test('Call setCaptureMode normal', () => {
  const valueList = [
    CaptureModeEnum.IMAGE,
    CaptureModeEnum.VIDEO,
    CaptureModeEnum.LIVE,
  ];
  valueList.forEach(function(element){
    setCaptureModeTest(element);
  });
});

test('Exception for Call setCaptureMode', async () => {
  jest.mocked(thetaBle.nativeSetCaptureMode).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  try {
    await service.setCaptureMode(CaptureModeEnum.IMAGE);
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeSetCaptureMode).toHaveBeenCalledWith(devId, CaptureModeEnum.IMAGE as string);
});
