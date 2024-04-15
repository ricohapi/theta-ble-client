import { FileFormatEnum, ShootingControlCommand, ThetaDevice } from '../..';
import { NativeModules } from 'react-native';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

afterEach(() => {
  thetaBle.nativeGetFileFormat = jest.fn();
  thetaBle.nativeSetFileFormat = jest.fn();
});

const devId = 1;
const devName = '0123456789';

async function getFileFormatTest(fileFormat: FileFormatEnum) {
  jest.mocked(thetaBle.nativeGetFileFormat).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return fileFormat as string;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  const response = await service.getFileFormat();

  expect(response).toBe(fileFormat);
  expect(thetaBle.nativeGetFileFormat).toHaveBeenCalledWith(devId);
}

test('Call getFileFormat normal', () => {
  const valueList = [
    FileFormatEnum.IMAGE_5K,
    FileFormatEnum.VIDEO_4K,
    FileFormatEnum.VIDEO_2K,
    FileFormatEnum.IMAGE_6_7K,
    FileFormatEnum.RAW_P_6_7K,
    FileFormatEnum.VIDEO_2_7K,
    FileFormatEnum.VIDEO_3_6K,
    FileFormatEnum.RESERVED,
  ];
  valueList.forEach(function(element){
    getFileFormatTest(element);
  });
});

test('Exception for Call getFileFormat', async () => {
  jest.mocked(thetaBle.nativeGetFileFormat).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  try {
    await service.getFileFormat();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetFileFormat).toHaveBeenCalledWith(devId);
});

async function setFileFormatTest(fileFormat: FileFormatEnum) {
  jest.mocked(thetaBle.nativeSetFileFormat).mockImplementation(
    jest.fn(async (id, value: string) => {
      expect(id).toBe(devId);
      expect(value).toBe(fileFormat as string);
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  await service.setFileFormat(fileFormat);

  expect(thetaBle.nativeSetFileFormat).toHaveBeenCalledWith(devId, fileFormat as string);
}

test('Call setFileFormat normal', () => {
  const valueList = [
    FileFormatEnum.IMAGE_5K,
    FileFormatEnum.VIDEO_4K,
    FileFormatEnum.VIDEO_2K,
    FileFormatEnum.IMAGE_6_7K,
    FileFormatEnum.RAW_P_6_7K,
    FileFormatEnum.VIDEO_2_7K,
    FileFormatEnum.VIDEO_3_6K,
  ];
  valueList.forEach(function(element){
    setFileFormatTest(element);
  });
});

test('Exception for Call setFileFormat', async () => {
  jest.mocked(thetaBle.nativeSetFileFormat).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new ShootingControlCommand(device);
  try {
    await service.setFileFormat(FileFormatEnum.IMAGE_5K);
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeSetFileFormat).toHaveBeenCalledWith(devId, FileFormatEnum.IMAGE_5K as string);
});
