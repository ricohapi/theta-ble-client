export async function setBleUuidWebApi(
  uuid: string,
): Promise<string | undefined> {
  const url = 'http://192.168.1.1/osc/commands/execute';
  const controller = new AbortController();
  const timeout = setTimeout(() => {
    controller.abort();
  }, 5000);

  try {
    const response = await fetch(url, {
      signal: controller.signal,
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: 'camera._setBluetoothDevice',
        parameters: {
          uuid,
        },
      }),
    });
    const json = await response.json();
    const name: string = json.results.deviceName;
    return name;
  } catch {
    return;
  } finally {
    clearTimeout(timeout);
  }
}
