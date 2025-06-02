import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, BluetoothControlCommand, PeripheralDeviceStatusEnum } from '../../service';

describe('BluetoothControlCommand scanPeripheralDevice', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;
  const peripheralDevice1 = {
    device: 'name1',
    macAddress: 'mac address1',
    pairing: false,
    status: PeripheralDeviceStatusEnum.IDLE,
  };
  const peripheralDevice2 = {
    device: 'name2',
    macAddress: 'mac address2',
    pairing: true,
    status: PeripheralDeviceStatusEnum.CONNECTED,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    jest.mocked(thetaBle.nativeContainService).mockImplementation(
      jest.fn(async () => {
        return true;
      }),
    );
  });

  afterEach(() => {
    thetaBle.nativeContainService = jest.fn();
    thetaBle.nativeBluetoothControlCommandScanPeripheralDevice = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.BLUETOOTH_CONTROL_COMMAND);
    return service as BluetoothControlCommand;
  };

  test('scanPeripheralDevice', async () => {
    thetaBle.nativeBluetoothControlCommandScanPeripheralDevice = jest.fn().mockImplementation( async (id, timeout) => {
      expect(id).toBe(devId);
      expect(timeout).toBe(100);
      return [peripheralDevice1, peripheralDevice2];
    });

    const service = await setupService();
    const deviceList = await service.scanPeripheralDevice(100);

    expect(deviceList.length).toBe(2);
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDevice).toHaveBeenCalledWith(devId, 100);
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStop).toHaveBeenCalledWith(devId);
  });

  test('Exception scanPeripheralDevice', async () => {

    thetaBle.nativeBluetoothControlCommandScanPeripheralDevice = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.scanPeripheralDevice(100);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStop).toHaveBeenCalledWith(devId);
  });
});
