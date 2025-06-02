import { Text, TouchableOpacity, View } from 'react-native';
import styles from './styles';
import React from 'react';

interface OptionItem {
    label: string;
    value: number;
}
interface Props {
    options: OptionItem[];
    selected?: number;
    onSelected: (value: number) => void;
}

export const RadioButton: React.FC<Props> = ({
  options,
  selected,
  onSelected,
}) => {

  return (
    <View style={styles.container}>
      {options.map(option => {
        const active = option.value === selected;
        return (
          <TouchableOpacity
            disabled={active}
            key={option.value}
            onPress={() => {
              onSelected(option.value);
            }}
          >
            <View style={[styles.option, active && styles.iconActive]}>
              <Text style={[styles.label, active && styles.labelActive]}>
                {option.label}
              </Text>
              <View style={[styles.icon, active && styles.iconActive]}>
                {active && <View style={styles.iconInner} />}
              </View>
            </View>
          </TouchableOpacity>
        );
      })}
    </View>
  );
};

RadioButton.displayName = 'RadioButton';

export default RadioButton;
