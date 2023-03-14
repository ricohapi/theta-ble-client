/**
 * Command error description
 */
export const CommandErrorDescriptionEnum = {
  /**
   * Disabled Command
   * Command cannot be executed due to the camera status
   */
  DISABLED_COMMAND: 'DISABLED_COMMAND',

  /**
   * Missing Parameter
   * Insufficient required parameters to issue the command
   */
  MISSING_PARAMETER: 'MISSING_PARAMETER',

  /**
   * Invalid Parameter Value
   * Parameter value when command was issued is invalid
   */
  INVALID_PARAMETER_VALUE: 'INVALID_PARAMETER_VALUE',

  /**
   * Power Off Sequence Running
   * Process request when power supply is off
   */
  POWER_OFF_SEQUENCE_RUNNING: 'POWER_OFF_SEQUENCE_RUNNING',

  /**
   * Invalid File Format
   * Invalid file format specified
   */
  INVALID_FILE_FORMAT: 'INVALID_FILE_FORMAT',

  /**
   * Service Unavailable
   * Processing requests cannot be received temporarily
   */
  SERVICE_UNAVAILABLE: 'SERVICE_UNAVAILABLE',

  /**
   * Device Busy
   */
  DEVICE_BUSY: 'DEVICE_BUSY',

  /**
   * Unexpected
   * Other errors
   */
  UNEXPECTED: 'UNEXPECTED',
} as const;

/** type definition of CommandErrorDescriptionEnum */
export type CommandErrorDescriptionEnum =
  typeof CommandErrorDescriptionEnum[keyof typeof CommandErrorDescriptionEnum];
