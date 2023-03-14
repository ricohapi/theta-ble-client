import type { GpsInfo } from './gps-info';

/**
 * Mutable values representing Theta status.
 */
export interface ThetaState2 {
  /**
   * gpsInfo set by API.
   */
  externalGpsInfo: {
    gpsInfo: GpsInfo
  };

  /**
   * gpsInfo from the built-in GPS module.
   */
  internalGpsInfo: {
    gpsInfo: GpsInfo
  };
};
