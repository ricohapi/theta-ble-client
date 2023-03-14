import { CameraInformation, ThetaDevice } from '..';
import { NativeModules } from 'react-native';

const thetaBle = NativeModules.ThetaBleClientReactNative;

beforeEach(() => {
  jest.clearAllMocks();
});

const devId = 1;
const devName = '0123456789';

test('Call getFirmwareRevision normal', async () => {
  const bleValue = '01234567890123';
  jest.mocked(thetaBle.nativeGetFirmwareRevision).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  const response = await service.getFirmwareRevision();

  expect(response).toBe(bleValue);
  expect(thetaBle.nativeGetFirmwareRevision).toHaveBeenCalledWith(devId);
});

test('Exception for Call getFirmwareRevision', async () => {
  jest.mocked(thetaBle.nativeGetFirmwareRevision).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  try {
    await service.getFirmwareRevision();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetFirmwareRevision).toHaveBeenCalledWith(devId);
});

test('Call getManufacturerName normal', async () => {
  const bleValue = '01234567890123';
  jest.mocked(thetaBle.nativeGetManufacturerName).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  const response = await service.getManufacturerName();

  expect(response).toBe(bleValue);
  expect(thetaBle.nativeGetManufacturerName).toHaveBeenCalledWith(devId);
});

test('Exception for Call getManufacturerName', async () => {
  jest.mocked(thetaBle.nativeGetManufacturerName).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  try {
    await service.getManufacturerName();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetManufacturerName).toHaveBeenCalledWith(devId);
});

test('Call getModelNumber normal', async () => {
  const bleValue = '01234567890123';
  jest.mocked(thetaBle.nativeGetModelNumber).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  const response = await service.getModelNumber();

  expect(response).toBe(bleValue);
  expect(thetaBle.nativeGetModelNumber).toHaveBeenCalledWith(devId);
});

test('Exception for Call getModelNumber', async () => {
  jest.mocked(thetaBle.nativeGetModelNumber).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  try {
    await service.getModelNumber();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetModelNumber).toHaveBeenCalledWith(devId);
});

test('Call getSerialNumber normal', async () => {
  const bleValue = '01234567890123';
  jest.mocked(thetaBle.nativeGetSerialNumber).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  const response = await service.getSerialNumber();

  expect(response).toBe(bleValue);
  expect(thetaBle.nativeGetSerialNumber).toHaveBeenCalledWith(devId);
});

test('Exception for Call getSerialNumber', async () => {
  jest.mocked(thetaBle.nativeGetSerialNumber).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  try {
    await service.getSerialNumber();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetSerialNumber).toHaveBeenCalledWith(devId);
});

test('Call getWlanMacAddress normal', async () => {
  const bleValue = '01234567890123';
  jest.mocked(thetaBle.nativeGetWlanMacAddress).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  const response = await service.getWlanMacAddress();

  expect(response).toBe(bleValue);
  expect(thetaBle.nativeGetWlanMacAddress).toHaveBeenCalledWith(devId);
});

test('Exception for Call getWlanMacAddress', async () => {
  jest.mocked(thetaBle.nativeGetWlanMacAddress).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  try {
    await service.getWlanMacAddress();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetWlanMacAddress).toHaveBeenCalledWith(devId);
});

test('Call getBluetoothMacAddress normal', async () => {
  const bleValue = '01234567890123';
  jest.mocked(thetaBle.nativeGetBluetoothMacAddress).mockImplementation(
    jest.fn(async (id) => {
      expect(id).toBe(devId);
      return bleValue;
    }),
  );

  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  const response = await service.getBluetoothMacAddress();

  expect(response).toBe(bleValue);
  expect(thetaBle.nativeGetBluetoothMacAddress).toHaveBeenCalledWith(devId);
});

test('Exception for Call getBluetoothMacAddress', async () => {
  jest.mocked(thetaBle.nativeGetBluetoothMacAddress).mockImplementation(
    jest.fn(async () => {
      throw 'error';
    }),
  );
  
  const device = new ThetaDevice(devId, devName);
  const service = new CameraInformation(device);
  try {
    await service.getBluetoothMacAddress();
    throw new Error('failed');
  } catch (error) {
    expect(error).toBe('error');
  }

  expect(thetaBle.nativeGetBluetoothMacAddress).toHaveBeenCalledWith(devId);
});
