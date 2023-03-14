import { ShootingFunctionEnum } from '../../service';

describe('ShootingFunctionEnum', () => {
  const data: [ShootingFunctionEnum, string][] = [
    [ShootingFunctionEnum.UNKNOWN, 'UNKNOWN'],
    [ShootingFunctionEnum.NORMAL, 'NORMAL'],
    [ShootingFunctionEnum.SELF_TIMER, 'SELF_TIMER'],
    [ShootingFunctionEnum.MY_SETTING, 'MY_SETTING'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(ShootingFunctionEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
