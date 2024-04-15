/**
 * File format.
 */
export const FileFormatEnum = {
  /**
   *  Reserved value
   */
  RESERVED: 'RESERVED',

  /**
   * Still image. 5376x2688
   * 
   * For RICOH THETA V
   */
  IMAGE_5K: 'IMAGE_5K',

  /**
   * Movie. 3840x1920. H.264/MPEG-4 AVC
   * 
   * For RICOH THETA V, RICOH THETA Z1 or later
   */
  VIDEO_4K: 'VIDEO_4K',

  /**
   * Movie. 1920x960. H.264/MPEG-4 AVC
   * 
   * For RICOH THETA V, RICOH THETA Z1 or later
   */
  VIDEO_2K: 'VIDEO_2K',

  /**
   * Still image JPEG format. 6720x3360 (Equirectangular) or 7296x3648 (Dual-Fisheye)
   * 
   * For RICOH THETA Z1 or later
   */
  IMAGE_6_7K: 'IMAGE_6_7K',

  /**
   * Still image RAW+ format. 7296x3648
   * 
   * For RICOH THETA Z1 or later
   */
  RAW_P_6_7K: 'RAW_P_6_7K',

  /**
   * Movie. 2688x2688. H.264/MPEG-4 AVC
   *
   * RICOH THETA Z1 firmware v3.01.1 or later. This mode outputs two fisheye video for each lens.
   * The MP4 file name ending with _0 is the video file on the front lens,
   * and _1 is back lens. This mode does not record audio track to MP4 file.
   */
  VIDEO_2_7K: 'VIDEO_2_7K',

  /**
   * Movie. 3648x3648. H.264/MPEG-4 AVC
   *
   * RICOH THETA Z1 firmware v3.01.1 or later. This mode outputs two fisheye video for each lens.
   * The MP4 file name ending with _0 is the video file on the front lens,
   * and _1 is back lens. This mode does not record audio track to MP4 file.
   */
  VIDEO_3_6K: 'VIDEO_3_6K',
} as const;

/** type definition of FileFormatEnum */
export type FileFormatEnum =
  typeof FileFormatEnum[keyof typeof FileFormatEnum];
