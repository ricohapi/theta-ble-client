import { WifiSecurityModeEnum } from '../../service';

describe('WlanFrequencyEnum', () => {
  const data: [WifiSecurityModeEnum, string][] = [
    [WifiSecurityModeEnum.UNKNOWN, 'UNKNOWN'],
    [WifiSecurityModeEnum.NONE, 'NONE'],
    [WifiSecurityModeEnum.WEP, 'WEP'],
    [WifiSecurityModeEnum.WPA_WPA2_PSK, 'WPA_WPA2_PSK'],
    [WifiSecurityModeEnum.WPA3_SAE, 'WPA3_SAE'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(WifiSecurityModeEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
