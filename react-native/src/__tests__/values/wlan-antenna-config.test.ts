import { WlanAntennaConfigEnum } from '../../service';

describe('WlanAntennaConfigEnum', () => {
  const data: [WlanAntennaConfigEnum, string][] = [
    [WlanAntennaConfigEnum.UNKNOWN, 'UNKNOWN'],
    [WlanAntennaConfigEnum.SISO, 'SISO'],
    [WlanAntennaConfigEnum.MIMO, 'MIMO'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(WlanAntennaConfigEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
