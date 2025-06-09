import React from 'react';
import { ScrollView, StyleProp, Text, TouchableOpacity, View, ViewProps, ViewStyle } from 'react-native';
import styles from './styles';

interface Props extends Pick<ViewProps, 'testID'> {
    style?: StyleProp<ViewStyle>;
    ssidList: string[];
    onSelectedSsid?: (ssid?: string) => void;
    selectedSsid?: string;
  }
  
export const SsidListView: React.FC<Props> = ({ ssidList, onSelectedSsid, selectedSsid }) => {

  const isSelectedSsid = (ssid: string) => {
    return ssid === selectedSsid;
  };

  const onPressItem = (item: string) => {
    // setSelected(item);
    onSelectedSsid?.(item);
  };

  const items =
  ssidList.map((item) => (
    <TouchableOpacity
      style={
        isSelectedSsid(item)
          ? styles.listItemBaseSelected
          : styles.listItemBase
      }
      key={item}
      onPress={() => onPressItem(item)}
    >
      <View>
        <Text style={styles.itemText}>{item}</Text>
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
