import * as React from 'react';
import type { OptionEditProps } from '..';
import { InputString } from '../../ui/input-string';
import { ThetaOptions } from 'theta-ble-client-react-native';

interface Props extends OptionEditProps {
  propName: string;
  placeHolder?: string;
}

export const StringEdit: React.FC<Props> = ({
  propName,
  onChange,
  options,
  placeHolder,
}) => {
  const getOptionPropString = (_options: ThetaOptions, _propName: string) => {
    if (_options != null) {
      const option = Object.entries(_options).find(
        element => element[0] === _propName,
      );
      if (option != null && option[1] != null) {
        return option[1] as string;
      }
    }
    return undefined;
  };
  return (
    <InputString
      title={propName}
      placeHolder={placeHolder}
      value={
        options != null ? getOptionPropString(options, propName) : undefined
      }
      onChange={value => {
        const newValue = value.length === 0 ? undefined : value;
        const option = { ...options, [propName]: newValue };
        onChange(option);
      }}
    />
  );
};

StringEdit.displayName = 'StringEdit';

export default StringEdit;
