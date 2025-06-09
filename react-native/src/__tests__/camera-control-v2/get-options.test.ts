import { NativeModules } from 'react-native';
import { ThetaDevice } from '../../theta-device';
import {
  BleServiceEnum,
  CameraControlCommandV2,
  CameraPowerEnum,
  CaptureModeEnum,
  NetworkTypeEnum,
  OptionName,
  WlanAntennaConfigEnum,
  WlanFrequencyEnum,
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
      accessInfo: {
        ssid: 'test_ssid',
        ipAddress: '10.0.0.222',
        subnetMask: '255.255.255.0',
        defaultGateway: '10.0.0.2',
        proxyURL: 'proxy-url',
        frequency: 'GHZ_2_4',
        wlanSignalStrength: -51,
        wlanSignalLevel: 4,
        lteSignalStrength: 0,
        lteSignalLevel: 0,
        dhcpLeaseAddress: [
          {
            ipAddress: '10.0.0.3',
            macAddress: '58:38:79:9f:00:00',
            hostName: 'host_name',
          },
        ],
      },
      cameraPower: CameraPowerEnum.SILENT_MODE,
      captureMode: CaptureModeEnum.IMAGE,
      defaultWifiPassword: '11110000',
      networkType: NetworkTypeEnum.DIRECT,
      password: 'pass123',
      ssid: 'ssid_123',
      username: 'user1',
      wlanAntennaConfig: WlanAntennaConfigEnum.SISO,
      wlanFrequency: WlanFrequencyEnum.GHZ_5_8,
    };
    thetaBle.nativeCameraControlCommandV2GetOptions = jest.fn().mockImplementation(async (id, names) => {
      expect(id).toBe(devId);
      expect(names).toStrictEqual([
        OptionName.AccessInfo,
        OptionName.CameraPower,
        OptionName.CaptureMode,
        OptionName.DefaultWifiPassword,
        OptionName.NetworkType,
        OptionName.Password,
        OptionName.Ssid,
        OptionName.Username,
        OptionName.WlanAntennaConfig,
        OptionName.WlanFrequency,
      ]);
      return testData;
    });

    const service = await setupService();
    const options = await service.getOptions([
      OptionName.AccessInfo,
      OptionName.CameraPower,
      OptionName.CaptureMode,
      OptionName.DefaultWifiPassword,
      OptionName.NetworkType,
      OptionName.Password,
      OptionName.Ssid,
      OptionName.Username,
      OptionName.WlanAntennaConfig,
      OptionName.WlanFrequency,
    ]);

    expect(options.cameraPower).toBe(CameraPowerEnum.SILENT_MODE);
    expect(options.captureMode).toBe(CaptureModeEnum.IMAGE);
    expect(options.defaultWifiPassword).toBe('11110000');
    expect(options.networkType).toBe(NetworkTypeEnum.DIRECT);
    expect(options.password).toBe('pass123');
    expect(options.ssid).toBe('ssid_123');
    expect(options.username).toBe('user1');
    expect(options.wlanAntennaConfig).toBe(WlanAntennaConfigEnum.SISO);
    expect(options.wlanFrequency).toBe(WlanFrequencyEnum.GHZ_5_8);

    const accessInfo = options.accessInfo;
    expect(accessInfo).toBeDefined();
    expect(accessInfo?.ssid).toBe('test_ssid');
    expect(accessInfo?.ipAddress).toBe('10.0.0.222');
    expect(accessInfo?.subnetMask).toBe('255.255.255.0');
    expect(accessInfo?.defaultGateway).toBe('10.0.0.2');
    expect(accessInfo?.proxyURL).toBe('proxy-url');
    expect(accessInfo?.frequency).toBe(WlanFrequencyEnum.GHZ_2_4);
    expect(accessInfo?.wlanSignalStrength).toBe(-51);
    expect(accessInfo?.wlanSignalLevel).toBe(4);
    expect(accessInfo?.lteSignalStrength).toBe(0);
    expect(accessInfo?.lteSignalLevel).toBe(0);
    expect(accessInfo?.dhcpLeaseAddress?.length).toBe(1);
    const dhcpLeaseAddress = accessInfo?.dhcpLeaseAddress?.at(0);
    expect(dhcpLeaseAddress).toBeDefined();
    expect(dhcpLeaseAddress?.ipAddress).toBe('10.0.0.3');
    expect(dhcpLeaseAddress?.macAddress).toBe('58:38:79:9f:00:00');
    expect(dhcpLeaseAddress?.hostName).toBe('host_name');
  });

  test('Exception getOptions', async () => {

    thetaBle.nativeCameraControlCommandV2GetOptions = jest.fn().mockImplementation(() => {
      throw 'error';
    });

    const service = await setupService();
    try {
      await service.getOptions([
        OptionName.CameraPower,
        OptionName.CaptureMode,
        OptionName.DefaultWifiPassword,
        OptionName.NetworkType,
        OptionName.Password,
        OptionName.Ssid,
        OptionName.Username,
        OptionName.WlanAntennaConfig,
        OptionName.WlanFrequency,
      ]);
      throw new Error('failed');
    } catch (error) {
      expect(error).toBe('error');
    }
  });
});
