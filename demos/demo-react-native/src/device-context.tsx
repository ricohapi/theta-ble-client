import * as React from 'react';
import type { ThetaDevice } from 'theta-ble-client-react-native';

interface Props {
  thetaDevice?: ThetaDevice;
  setThetaDevice: (device?: ThetaDevice) => void;
}

const DeviceContext = React.createContext<Props>({
  setThetaDevice: () => {
    console.log('error');
  },
});

export function useDeviceContext() {
  return React.useContext(DeviceContext);
}
export function DeviceProvider({ children }) {
  const [thetaDevice, setThetaDevice] = React.useState<ThetaDevice>();

  const value = {
    thetaDevice,
    setThetaDevice,
  };

  return (
    <DeviceContext.Provider value={value}>{children}</DeviceContext.Provider>
  );
}
