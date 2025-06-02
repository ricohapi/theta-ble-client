import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import { BleServiceEnum, BluetoothControlCommand, PeripheralDeviceStatusEnum } from '../../service';

describe('BluetoothControlCommand deletePeripheralDevice', () => {
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
    thetaBle.nativeBluetoothControlCommandDeletePeripheralDevice = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.BLUETOOTH_CONTROL_COMMAND);
    return service as BluetoothControlCommand;
  };

  test('deletePeripheralDevice', async () => {
    thetaBle.nativeBluetoothControlCommandDeletePeripheralDevice = jest.fn().mockImplementation( async (id, macAddress) => {
      expect(id).toBe(devId);
      expect(macAddress).toBe(peripheralMacAddress);
    });

    const service = await setupService();
    await service.deletePeripheralDevice(peripheralDevice);

    expect(thetaBle.nativeBluetoothControlCommandDeletePeripheralDevice).toHaveBeenCalledWith(devId, peripheralMacAddress);
  });

  test('Exception deletePeripheralDevice', async () => {

    thetaBle.nativeBluetoothControlCommandDeletePeripheralDevice = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.deletePeripheralDevice(peripheralDevice);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
