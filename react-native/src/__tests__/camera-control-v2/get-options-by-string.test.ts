import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import {
  BleServiceEnum,
  CameraControlCommandV2,
} from '../../service';

describe('CameraControlCommandV2 getOptions', () => {
  const devId = 1;
  const devName = '0123456789';
  const thetaBle = NativeModules.ThetaBleClientReactNative;

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
    thetaBle.nativeCameraControlCommandV2GetOptions = jest.fn();
  });

  const setupService = async () => {
    const device = new ThetaDevice(devId, devName);
    const service = await device.getService(BleServiceEnum.CAMERA_CONTROL_COMMAND_V2);
    return service as CameraControlCommandV2;
  };

  test('getOptions', async () => {
    const testData = {
      '_cameraPower': 'on',
      'captureMode': 'image',
      '_networkType': 'AP',
      '_password': 'password123',
      '_ssid': 'ssid123',
      '_username': 'adminUser',
      '_wlanAntennaConfig': 'MIMO',
      '_wlanFrequency': 2.4,
      '_accessInfo': {
        'ssid': 'test_ssid',
        'ipAddress': '10.0.0.222',
        'subnetMask': '255.255.255.0',
        'defaultGateway': '10.0.0.2',
        'proxyURL': 'proxy-url',
        'frequency': '2.4',
        'wlanSignalStrength': -51,
        'wlanSignalLevel': 4,
        'lteSignalStrength': 0,
        'lteSignalLevel': 0,
        '_dhcpLeaseAddress': [
          {
            'ipAddress': '10.0.0.3',
            'macAddress': '58:38:79:9f:00:00',
            'hostName': 'host_name',
          },
        ],
      },
    };
    thetaBle.nativeCameraControlCommandV2GetOptionsByString = jest.fn().mockImplementation(async (id, names) => {
      expect(id).toBe(devId);
      expect(names).toStrictEqual([
        '_cameraPower',
        'captureMode',
        '_networkType',
        '_password',
        '_ssid',
        '_username',
        '_wlanAntennaConfig',
        '_wlanFrequency',
        '_accessInfo',
      ]);
      return testData;
    });

    const service = await setupService();
    const optionMap = await service.getOptionsByString([
      '_cameraPower',
      'captureMode',
      '_networkType',
      '_password',
      '_ssid',
      '_username',
      '_wlanAntennaConfig',
      '_wlanFrequency',
      '_accessInfo',
    ]);

    expect(optionMap._cameraPower).toBe('on');
    expect(optionMap.captureMode).toBe('image');
    expect(optionMap._networkType).toBe('AP');
    expect(optionMap._password).toBe('password123');
    expect(optionMap._ssid).toBe('ssid123');
    expect(optionMap._username).toBe('adminUser');
    expect(optionMap._wlanAntennaConfig).toBe('MIMO');
    expect(optionMap._wlanFrequency).toBe(2.4);

    expect(optionMap._wlanFrequency).toBe(2.4);

    const accessInfo = optionMap._accessInfo as Record<string, unknown> | undefined;
    expect(accessInfo).toBeDefined();
    expect(accessInfo?.ssid).toBe('test_ssid');
    expect(accessInfo?.ipAddress).toBe('10.0.0.222');
    expect(accessInfo?.subnetMask).toBe('255.255.255.0');
    expect(accessInfo?.defaultGateway).toBe('10.0.0.2');
    expect(accessInfo?.proxyURL).toBe('proxy-url');
    expect(accessInfo?.frequency).toBe('2.4');
    expect(accessInfo?.wlanSignalStrength).toBe(-51);
    expect(accessInfo?.wlanSignalLevel).toBe(4);
    expect(accessInfo?.lteSignalStrength).toBe(0);
    expect(accessInfo?.lteSignalLevel).toBe(0);
    
    const dhcpLeaseAddresses = accessInfo?._dhcpLeaseAddress as Record<string, unknown>[];
    expect(dhcpLeaseAddresses?.length).toBe(1);
    const dhcpLeaseAddress = dhcpLeaseAddresses?.at(0);
    expect(dhcpLeaseAddress).toBeDefined();
    expect(dhcpLeaseAddress?.ipAddress).toBe('10.0.0.3');
    expect(dhcpLeaseAddress?.macAddress).toBe('58:38:79:9f:00:00');
    expect(dhcpLeaseAddress?.hostName).toBe('host_name');
  });

  test('Exception getOptions', async () => {

    thetaBle.nativeCameraControlCommandV2GetOptionsByString = jest.fn().mockImplementation(() => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.getOptionsByString([
        '_cameraPower',
        'captureMode',
        '_networkType',
        '_password',
        '_ssid',
        '_username',
        '_wlanAntennaConfig',
        '_wlanFrequency',
        '_accessInfo',
      ]);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
