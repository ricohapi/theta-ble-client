/**
 * GPS information
 */
export interface GpsInfo {
  /** Latitude (-90.000000 – 90.000000) */
  lat?: number;
  /** Longitude (-180.000000 – 180.000000) */
  lng?: number;
  /** Altitude (meters) */
  altitude?: number;
  /** Location information acquisition time */
  dateTimeZone?: string;
  /** Geodetic reference */
  datum?: string;
};
