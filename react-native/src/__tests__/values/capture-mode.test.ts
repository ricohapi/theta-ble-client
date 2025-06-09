import { CaptureModeEnum } from '../../service';

describe('CaptureModeEnum', () => {
  const data: [CaptureModeEnum, string][] = [
    [CaptureModeEnum.UNKNOWN, 'UNKNOWN'],
    [CaptureModeEnum.IMAGE, 'IMAGE'],
    [CaptureModeEnum.VIDEO, 'VIDEO'],
    [CaptureModeEnum.LIVE, 'LIVE'],
    [CaptureModeEnum.INTERVAL, 'INTERVAL'],
    [CaptureModeEnum.PRESET, 'PRESET'],
    [CaptureModeEnum.WEB_RTC, 'WEB_RTC'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(CaptureModeEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
