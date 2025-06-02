import { StyleSheet } from 'react-native';

const styles = StyleSheet.create({
  safeAreaContainer: {
    flex: 1,
    backgroundColor: 'white',
  },
  messageText: {
    marginLeft: 4,
    color: 'black',
  },
  messageArea: {
    width: '90%',
    height: 100,
    borderColor: 'gray',
    borderWidth: 1,
    margin: 10,
  },
  buttonViewContainerLayout: {
    flexDirection: 'row',
    alignItems: 'center',
    height: 70,
  },
  button: {
    paddingHorizontal: 5,
  },
  listContainerLayout: {
    flex: 1,
    borderColor: 'gray',
    borderWidth: 1,
    margin: 10,
  },
  itemText: {
    color: 'black',
    fontSize: 16,
    paddingHorizontal: 10,
    paddingVertical: 2,
  },
  container: {
    flex: 1,
  },
  listContentContainer: {
    flex: 1,
  },
  listItemBase: {
    width: '100%',
  },
  listItemBaseSelected: {
    width: '100%',
    backgroundColor: 'yellow',
  },
});

export default styles;
