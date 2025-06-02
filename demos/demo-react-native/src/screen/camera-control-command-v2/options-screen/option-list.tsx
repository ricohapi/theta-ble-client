import type { Item } from '../../../components/ui/item-list';
import {
  CameraPowerEnum,
  CaptureModeEnum,
  NetworkTypeEnum,
  OptionName,
  ThetaOptions,
  WlanFrequencyEnum,
} from '../../../modules/theta-ble-client';
import { EnumEdit, StringEdit } from '../../../components/options';
import React from 'react';

export interface OptionItem extends Item {
  value: {
    optionName: OptionName;
    editor?: (
      options: ThetaOptions,
      onChange: (options: ThetaOptions) => void,
    ) => React.ReactElement;
    defaultValue?: ThetaOptions;
    onWillSet?: (options: ThetaOptions) => void;
  };
}

export const optionList: OptionItem[] = [
  {
    name: 'accessInfo',
    value: {
      optionName: OptionName.AccessInfo,
    },
  },
  {
    name: 'cameraPower',
    value: {
      optionName: OptionName.CameraPower,
      editor: (options, onChange) => (
        <EnumEdit
          title={'cameraPower'}
          option={options?.cameraPower}
          onChange={cameraPower => {
            onChange({ cameraPower });
          }}
          optionEnum={CameraPowerEnum}
        />
      ),
      defaultValue: { cameraPower: CameraPowerEnum.ON },
    },
  },
  {
    name: 'captureMode',
    value: {
      optionName: OptionName.CaptureMode,
      editor: (options, onChange) => (
        <EnumEdit
          title={'captureMode'}
          option={options?.captureMode}
          onChange={captureMode => {
            onChange({ captureMode });
          }}
          optionEnum={CaptureModeEnum}
        />
      ),
      defaultValue: { captureMode: CaptureModeEnum.IMAGE },
    },
  },
  {
    name: 'networkType',
    value: {
      optionName: OptionName.NetworkType,
      editor: (options, onChange) => (
        <EnumEdit
          title={'networkType'}
          option={options.networkType}
          onChange={networkType => {
            onChange({ networkType });
          }}
          optionEnum={NetworkTypeEnum}
        />
      ),
    },
  },
  {
    name: 'password',
    value: {
      optionName: OptionName.Password,
      editor: (options, onChange) => (
        <StringEdit
          propName={'password'}
          options={options}
          onChange={password => {
            onChange(password);
          }}
        />
      ),
      defaultValue: { password: '' },
    },
  },
  {
    name: 'username',
    value: {
      optionName: OptionName.Username,
      editor: (options, onChange) => (
        <StringEdit
          propName={'username'}
          options={options}
          onChange={username => {
            onChange(username);
          }}
        />
      ),
      defaultValue: { username: '' },
    },
  },
  {
    name: 'wlanFrequency',
    value: {
      optionName: OptionName.WlanFrequency,
      editor: (options, onChange) => (
        <EnumEdit
          title={'wlanFrequency'}
          option={options.wlanFrequency}
          onChange={wlanFrequency => {
            onChange({ wlanFrequency });
          }}
          optionEnum={WlanFrequencyEnum}
        />
      ),
    },
  },
];
