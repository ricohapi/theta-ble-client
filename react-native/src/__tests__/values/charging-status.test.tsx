import { ChargingStateEnum } from '../../service';

describe('CaptureStatusEnum', () => {
  const data: [ChargingStateEnum, string][] = [
    [ChargingStateEnum.UNKNOWN, 'UNKNOWN'],
    [ChargingStateEnum.CHARGING, 'CHARGING'],
    [ChargingStateEnum.CHARGED, 'CHARGED'],
    [ChargingStateEnum.DISCONNECT, 'DISCONNECT'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(ChargingStateEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
