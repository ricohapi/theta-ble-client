import { MaxRecordableTimeEnum } from '../../service';

describe('MaxRecordableTimeEnum', () => {
  const data: [MaxRecordableTimeEnum, string][] = [
    [MaxRecordableTimeEnum.RECORDABLE_TIME_300, 'RECORDABLE_TIME_300'],
    [MaxRecordableTimeEnum.RECORDABLE_TIME_1500, 'RECORDABLE_TIME_1500'],
    [MaxRecordableTimeEnum.RECORDABLE_TIME_3000, 'RECORDABLE_TIME_3000'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(MaxRecordableTimeEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
