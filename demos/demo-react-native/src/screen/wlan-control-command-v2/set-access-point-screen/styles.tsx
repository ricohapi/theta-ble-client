import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  safeAreaContainer: {
    flex: 1,
    backgroundColor: 'white',
  },
  messageContainerLayout: {
    height: 120,
  },
  messageText: {
    marginLeft: 4,
    color: 'black',
  },
  messageArea: {
    flex: 1,
    borderColor: 'gray',
    borderWidth: 1,
    margin: 10,
  },
  labelText: {
    marginLeft: 4,
    color: 'black',
  },
  buttonViewContainerLayout: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 70,
  },
  button: {
    paddingHorizontal: 5,
  },
  editViewContainerLayout: {
    flexDirection: 'column',
  },
  inputText: {
    height: 60,
  },
});

export default styles;
