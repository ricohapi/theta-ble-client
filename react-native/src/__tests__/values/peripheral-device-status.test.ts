import { PeripheralDeviceStatusEnum } from '../../service';

describe('PeripheralDeviceStatusEnum', () => {
  const data: [PeripheralDeviceStatusEnum, string][] = [
    [PeripheralDeviceStatusEnum.UNKNOWN, 'UNKNOWN'],
    [PeripheralDeviceStatusEnum.IDLE, 'IDLE'],
    [PeripheralDeviceStatusEnum.CONNECTED, 'CONNECTED'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(PeripheralDeviceStatusEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
