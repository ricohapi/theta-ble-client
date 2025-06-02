import React from 'react';
import { ScrollView, StyleProp, Text, TouchableOpacity, View, ViewProps, ViewStyle } from 'react-native';
import { PeripheralDevice } from 'theta-ble-client-react-native';
import styles from './styles';

interface Props extends Pick<ViewProps, 'testID'> {
    style?: StyleProp<ViewStyle>;
    deviceList: PeripheralDevice[];
    onSelectedDevice?: (device?: PeripheralDevice) => void;
    selectedDevice?: PeripheralDevice;
  }
  
export const DeviceListView: React.FC<Props> = ({ deviceList, onSelectedDevice, selectedDevice }) => {

  const isSelectedDevice = (peripheralDevice: PeripheralDevice) => {
    return peripheralDevice.macAddress === selectedDevice?.macAddress;
  };

  const onPressItem = (item: PeripheralDevice) => {
    // setSelected(item);
    onSelectedDevice?.(item);
  };

  const items =
  deviceList.map((item) => (
    <TouchableOpacity
      style={
        isSelectedDevice(item)
          ? styles.listItemBaseSelected
          : styles.listItemBase
      }
      key={item.macAddress}
      onPress={() => onPressItem(item)}
    >
      <View>
        <Text style={styles.itemText}>{item.device}  status:{item.status}  pairing:{item.pairing ? 'true' : 'false'}</Text>
      </View>
    </TouchableOpacity>
  )) ?? [];

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.listContentContainer}
      >
        {items}
      </ScrollView>
    </View>
  );
};
