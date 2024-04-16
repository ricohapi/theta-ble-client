/**
 * Maximum recordable time (in seconds) of the camera.
 */
export const MaxRecordableTimeEnum = {
  /**
   * Maximum recordable time. 300sec for other than SC2.
  */
  RECORDABLE_TIME_300: 'RECORDABLE_TIME_300',

  /**
   * Maximum recordable time. 1500sec for other than SC2.
  */
  RECORDABLE_TIME_1500: 'RECORDABLE_TIME_1500',

  /**
   * Maximum recordable time. 3000sec for THETA Z1 Version 3.01.1 or later
   * only for 3.6K 1/2fps and 2.7K 1/2fps.
   * If you set 3000 seconds in 3.6K 2fps mode and then set back to 4K 30fps mode,
   * the max recordable time will be overwritten to 300 seconds automatically.
   */
  RECORDABLE_TIME_3000: 'RECORDABLE_TIME_3000',
} as const;

/** type definition of MaxRecordableTimeEnum */
export type MaxRecordableTimeEnum =
  typeof MaxRecordableTimeEnum[keyof typeof MaxRecordableTimeEnum];
