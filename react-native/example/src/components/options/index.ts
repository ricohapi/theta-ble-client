import type { ThetaOptions } from '../../modules/theta-ble-client';

export interface OptionEditProps {
  onChange: (options: ThetaOptions) => void;
  options?: ThetaOptions;
}
export * from './enum-edit';
export * from './string-edit';
