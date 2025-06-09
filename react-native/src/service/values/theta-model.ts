/**
 * Support THETA model
 */
export const ThetaModel = {
  /**
   * Undefined item
   */
  UNKNOWN: 'UNKNOWN',
  /** THETA S */
  THETA_S: 'THETA_S',
  /** THETA SC */
  THETA_SC: 'THETA_SC',
  /** THETA V */
  THETA_V: 'THETA_V',
  /** THETA Z1 */
  THETA_Z1: 'THETA_Z1',
  /** THETA X */
  THETA_X: 'THETA_X',
  /** THETA SC2 */
  THETA_SC2: 'THETA_SC2',
  /** THETA SC2 for business */
  THETA_SC2_B: 'THETA_SC2_B',
  /** THETA A1 */
  THETA_A1: 'THETA_A1',
} as const;

/** type definition of ThetaModel */
export type ThetaModel = (typeof ThetaModel)[keyof typeof ThetaModel];
