import * as React from 'react';
import { ThetaModel, scanThetaSsid } from '../../modules/theta-ble-client';
import { SafeAreaView } from 'react-native-safe-area-context';
import styles from './styles';
import { ScrollView, Text, View } from 'react-native';
import { Item, ItemListView } from '../../components/ui/item-list';
import Button from '../../components/ui/button';

interface ModelItem extends Item {
  value: ThetaModel | undefined;
}

function getJsonString(object: any) {
  return JSON.stringify(JSON.parse(JSON.stringify(object)), null, 2);
}

const ScanSsidScreen = () => {
  const [selectedModel, setSelectedModel] = React.useState<ModelItem>();
  const [message, setMessage] = React.useState('');

  const commandList: ModelItem[] = [
    {
      name: 'undefined',
      value: undefined,
    },
    {
      name: 'THETA_A1',
      value: ThetaModel.THETA_A1,
    },
    {
      name: 'THETA_X',
      value: ThetaModel.THETA_X,
    },
    {
      name: 'THETA_Z1',
      value: ThetaModel.THETA_Z1,
    },
    {
      name: 'THETA_SC2',
      value: ThetaModel.THETA_SC2,
    },
    {
      name: 'THETA_SC2_B',
      value: ThetaModel.THETA_SC2_B,
    },
    {
      name: 'THETA_V',
      value: ThetaModel.THETA_V,
    },
    {
      name: 'THETA_S',
      value: ThetaModel.THETA_S,
    },
  ];

  const onSelected = (item: Item) => {
    console.log('selected: ' + item.name);
    setSelectedModel(item);
    setMessage('');
  };

  const onExecute = async () => {
    if (selectedModel == null) {
      return;
    }
    try {
      setMessage('scanning...');
      const ssidList = await scanThetaSsid({
        model: selectedModel.value,
        timeout: 10000,
      });
      setMessage(getJsonString(ssidList));
    } catch (error) {
      setMessage(JSON.stringify(error));
    }
  };

  return (
    <SafeAreaView
      style={styles.safeAreaContainer}
      edges={['left', 'right', 'bottom']}>
      <View style={styles.commandListContainer}>
        <ItemListView
          itemList={commandList}
          onSelected={onSelected}
          selectedItem={selectedModel}
        />
      </View>
      <View style={styles.buttonViewContainer}>
        <View style={styles.buttonViewContainerLayout}>
          <Button
            style={styles.button}
            title="Execute"
            disabled={selectedModel == null}
            onPress={onExecute}
          />
        </View>
      </View>
      <ScrollView style={styles.messageArea}>
        <Text style={styles.messageText}>{message}</Text>
      </ScrollView>
    </SafeAreaView>
  );
};

export default ScanSsidScreen;
