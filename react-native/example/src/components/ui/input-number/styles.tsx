import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  titleText: {
    color: 'black',
    fontSize: 16,
    paddingRight: 10,
  },
  containerLayout: {
    flexDirection: 'row',
    padding: 5,
    alignItems: 'center',
  },
  titleBack: {
    alignItems: 'center',
    alignSelf: 'center',
  },
  itemBack: {
    alignItems: 'center',
    paddingHorizontal: 5,
    margin: 5,
    alignSelf: 'center',
    borderColor: 'gray',
    borderWidth: 1,
  },
  inputText: {
    minWidth: 70,
    fontSize: 16,
    textAlign: 'right',
    color: 'black',
  },
});

export default styles;
