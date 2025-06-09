import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, BluetoothControlCommand, PeripheralDeviceStatusEnum } from '../../service';

describe('BluetoothControlCommand connectPeripheralDevice', () => {
  const devId = 1;
  const devName = '0123456789';
  const peripheralMacAddress = 'mac address';
  const thetaBle = NativeModules.ThetaBleClientReactNative;
  const peripheralDevice = {
    device: 'name1',
    macAddress: peripheralMacAddress,
    pairing: false,
    status: PeripheralDeviceStatusEnum.IDLE,
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
    thetaBle.nativeBluetoothControlCommandConnectPeripheralDevice = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.BLUETOOTH_CONTROL_COMMAND);
    return service as BluetoothControlCommand;
  };

  test('connectPeripheralDevice', async () => {
    thetaBle.nativeBluetoothControlCommandConnectPeripheralDevice = jest.fn().mockImplementation( async (id, macAddress) => {
      expect(id).toBe(devId);
      expect(macAddress).toBe(peripheralMacAddress);
    });

    const service = await setupService();
    await service.connectPeripheralDevice(peripheralDevice);

    expect(thetaBle.nativeBluetoothControlCommandConnectPeripheralDevice).toHaveBeenCalledWith(devId, peripheralMacAddress);
  });

  test('Exception connectPeripheralDevice', async () => {

    thetaBle.nativeBluetoothControlCommandConnectPeripheralDevice = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.connectPeripheralDevice(peripheralDevice);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
