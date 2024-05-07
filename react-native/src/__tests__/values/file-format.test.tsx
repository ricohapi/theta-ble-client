import { FileFormatEnum } from '../../service';

describe('FileFormatEnum', () => {
  const data: [FileFormatEnum, string][] = [
    [FileFormatEnum.RESERVED, 'RESERVED'],
    [FileFormatEnum.IMAGE_5K, 'IMAGE_5K'],
    [FileFormatEnum.VIDEO_4K, 'VIDEO_4K'],
    [FileFormatEnum.VIDEO_2K, 'VIDEO_2K'],
    [FileFormatEnum.IMAGE_6_7K, 'IMAGE_6_7K'],
    [FileFormatEnum.RAW_P_6_7K, 'RAW_P_6_7K'],
    [FileFormatEnum.VIDEO_2_7K, 'VIDEO_2_7K'],
    [FileFormatEnum.VIDEO_3_6K, 'VIDEO_3_6K'],
  ];

  test('length', () => {
    expect(data.length).toBe(Object.keys(FileFormatEnum).length);
  });

  test('data', () => {
    data.forEach((item) => {
      expect(item[0]).toBe(item[1]);
    });
  });
});
