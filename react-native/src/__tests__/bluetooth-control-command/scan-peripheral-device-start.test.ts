import { NativeModules } from 'react-native';
import { BaseNotify, ThetaDevice } from '../../theta-device';
import { BleServiceEnum, BluetoothControlCommand, PeripheralDeviceStatusEnum } from '../../service';
import { NativeEventEmitter_addListener } from '../../__mocks__/react-native';

describe('BluetoothControlCommand scanPeripheralDeviceStart', () => {
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
    device: 'name1',
    macAddress: 'mac address2',
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
    thetaBle.nativeBluetoothControlCommandScanPeripheralDevice = jest.fn();
  });
  
  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.BLUETOOTH_CONTROL_COMMAND);
    return service as BluetoothControlCommand;
  };

  test('scanPeripheralDeviceStart', async () => {
    let notifyCallback: (notify: BaseNotify) => void = () => {
      expect(true).toBeFalsy();
    };
    jest.mocked(NativeEventEmitter_addListener).mockImplementation(
      jest.fn((_eventType, callback) => {
        notifyCallback = callback;
        return {
          remove: jest.fn(),
        };
      }),
    );
    thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStart = jest.fn().mockImplementation( async (id, timeout) => {
      expect(id).toBe(devId);
      expect(timeout).toBe(100);
      return;
    });

    const service = await setupService();
    const onNotify = jest.fn();
    const onCompleted = jest.fn();

    await service.scanPeripheralDeviceStart(100, onNotify, onCompleted);
    expect(service.device.notifyList.get('NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE')).toBeDefined();
    expect(service.device.notifyList.get('SCAN_BLUETOOTH_PERIPHERAL_DEVICE')).toBeDefined();

    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE',
      params: peripheralDevice1,
    });
    notifyCallback({
      deviceId: devId,
      characteristic: 'NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE',
      params: peripheralDevice2,
    });

    notifyCallback({
      deviceId: devId,
      characteristic: 'SCAN_BLUETOOTH_PERIPHERAL_DEVICE',
      params: [peripheralDevice1, peripheralDevice2],
    });

    expect(onNotify).toBeCalledTimes(2);
    expect(onNotify).nthCalledWith(1, peripheralDevice1);
    expect(onNotify).nthCalledWith(2, peripheralDevice2);
    expect(onCompleted).toBeCalledWith([peripheralDevice1, peripheralDevice2]);
    expect(service.device.notifyList.get('NOTIFICATION_SCANNED_BLUETOOTH_PERIPHERAL_DEVICE')).toBeUndefined();
    expect(service.device.notifyList.get('SCAN_BLUETOOTH_PERIPHERAL_DEVICE')).toBeUndefined();
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStart).toHaveBeenCalledWith(devId, 100);
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStop).toHaveBeenCalledWith(devId);
  });

  test('Exception scanPeripheralDevice', async () => {

    thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStart = jest.fn().mockImplementation( () => {
      throw 'error';
    });

    const service = await setupService();
    try {
      const onNotify = jest.fn();
      await service.scanPeripheralDeviceStart(100, onNotify);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStart).toHaveBeenCalledWith(devId, 100);
    expect(thetaBle.nativeBluetoothControlCommandScanPeripheralDeviceStop).toHaveBeenCalledWith(devId);
  });
});
