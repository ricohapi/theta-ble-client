import { NetworkTypeEnum } from '../../service';

describe('NetworkTypeEnum', () => {
  const data: [NetworkTypeEnum, string][] = [
    [NetworkTypeEnum.UNKNOWN, 'UNKNOWN'],
    [NetworkTypeEnum.DIRECT, 'DIRECT'],
    [NetworkTypeEnum.CLIENT, 'CLIENT'],
    [NetworkTypeEnum.ETHERNET, 'ETHERNET'],
    [NetworkTypeEnum.OFF, 'OFF'],
    [NetworkTypeEnum.LTE_PLAN_D, 'LTE_PLAN_D'],
    [NetworkTypeEnum.LTE_PLAN_DU, 'LTE_PLAN_DU'],
    [NetworkTypeEnum.LTE_PLAN_01S, 'LTE_PLAN_01S'],
    [NetworkTypeEnum.LTE_PLAN_X3, 'LTE_PLAN_X3'],
    [NetworkTypeEnum.LTE_PLAN_P1, 'LTE_PLAN_P1'],
    [NetworkTypeEnum.LTE_PLAN_K2, 'LTE_PLAN_K2'],
    [NetworkTypeEnum.LTE_PLAN_K, 'LTE_PLAN_K'],
    [NetworkTypeEnum.SCAN, 'SCAN'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(NetworkTypeEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
