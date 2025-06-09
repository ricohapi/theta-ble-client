/**
 * Proxy information to be used for the access point.
 */
export type Proxy = {
  /** true: use proxy false: do not use proxy */
  use: boolean;
  /** Proxy server URL */
  url?: string;
  /** Proxy server port number: 0 to 65535 */
  port?: number;
  /** User ID used for proxy authentication */
  userid?: string;
  /** Password used for proxy authentication */
  password?: string;
};
