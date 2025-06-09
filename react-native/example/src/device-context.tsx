import React, { createContext, ReactNode } from 'react';
import type { ThetaDevice } from './modules/theta-ble-client';

interface Props {
  thetaDevice?: ThetaDevice;
  setThetaDevice: (device?: ThetaDevice) => void;
}

const DeviceContext = createContext<Props>({
  setThetaDevice: () => {
    console.log('error');
  },
});

export function useDeviceContext() {
  return React.useContext(DeviceContext);
}

interface DeviceProviderProps {
  children: ReactNode;
}

export const DeviceProvider: React.FC<DeviceProviderProps> = ({ children }) => {
  const [thetaDevice, setThetaDevice] = React.useState<ThetaDevice>();

  const value = {
    thetaDevice,
    setThetaDevice,
  };

  return (
    <DeviceContext.Provider value={value}>{children}</DeviceContext.Provider>
  );
};
