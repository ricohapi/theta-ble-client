import { MaxRecordableTimeEnum, ShootingControlCommand, ThetaDevice } from '../..';
import { NativeModules } from 'react-native';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

afterEach(() => {
  thetaBle.nativeGetMaxRecordableTime = jest.fn();
  thetaBle.nativeSetMaxRecordableTime = jest.fn();
});

const devId = 1;
const devName = '0123456789';

async function getMaxRecordableTimeTest(value: MaxRecordableTimeEnum) {
  jest.mocked(thetaBle.nativeGetMaxRecordableTime).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return value as string;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  const response = await service.getMaxRecordableTime();

  expect(response).toBe(value);
  expect(thetaBle.nativeGetMaxRecordableTime).toHaveBeenCalledWith(devId);
}

test('Call getMaxRecordableTime normal', () => {
  const valueList = [
    MaxRecordableTimeEnum.RECORDABLE_TIME_300,
    MaxRecordableTimeEnum.RECORDABLE_TIME_1500,
    MaxRecordableTimeEnum.RECORDABLE_TIME_3000,
  ];
  valueList.forEach(function(element){
    getMaxRecordableTimeTest(element);
  });
});

test('Exception for Call getMaxRecordableTime', async () => {
  jest.mocked(thetaBle.nativeGetMaxRecordableTime).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  try {
    await service.getMaxRecordableTime();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetMaxRecordableTime).toHaveBeenCalledWith(devId);
});

async function setMaxRecordableTimeTest(value: MaxRecordableTimeEnum) {
  jest.mocked(thetaBle.nativeSetMaxRecordableTime).mockImplementation(
    jest.fn(async (id, mode: string) => {
      expect(id).toBe(devId);
      expect(mode).toBe(value as string);
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  await service.setMaxRecordableTime(value);

  expect(thetaBle.nativeSetMaxRecordableTime).toHaveBeenCalledWith(devId, value as string);
}

test('Call setMaxRecordableTime normal', () => {
  const valueList = [
    MaxRecordableTimeEnum.RECORDABLE_TIME_300,
    MaxRecordableTimeEnum.RECORDABLE_TIME_1500,
    MaxRecordableTimeEnum.RECORDABLE_TIME_3000,
  ];
  valueList.forEach(function(element){
    setMaxRecordableTimeTest(element);
  });
});

test('Exception for Call setMaxRecordableTime', async () => {
  jest.mocked(thetaBle.nativeSetMaxRecordableTime).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  try {
    await service.setMaxRecordableTime(MaxRecordableTimeEnum.RECORDABLE_TIME_1500);
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeSetMaxRecordableTime)
    .toHaveBeenCalledWith(devId, MaxRecordableTimeEnum.RECORDABLE_TIME_1500 as string);
});
