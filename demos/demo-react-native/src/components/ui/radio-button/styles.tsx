import { StyleSheet } from 'react-native';

const colors = {
  accent: '#03A9F4',
  border: '#0',
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
  },
  option: {
    padding: 10,
    marginLeft: -10,
    marginRight: 30,
    flexDirection: 'row',
    alignItems: 'center',
  },
  label: {
    fontSize: 16,
    color: 'black',
  },
  labelActive: {
    color: colors.accent,
  },
  icon: {
    width: 20,
    height: 20,
    marginLeft: 10,
    borderWidth: 1,
    borderRadius: 10,
    borderColor: colors.border,
    justifyContent: 'center',
    alignItems: 'center',
  },
  iconActive: {
    borderColor: colors.accent,
  },
  iconInner: {
    width: 10,
    height: 10,
    backgroundColor: 'black',
    borderRadius: 5,
  },
});

export default styles;
