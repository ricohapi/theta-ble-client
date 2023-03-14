export async function setBleOnWebApi(isOn: boolean): Promise<void> {
  const url = 'http://192.168.1.1/osc/commands/execute';
  const power = isOn ? 'ON' : 'OFF';

  const controller = new AbortController();
  const timeout = setTimeout(() => {
    controller.abort();
  }, 5000);

  try {
    await fetch(url, {
      signal: controller.signal,
      method: 'POST',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        name: 'camera.setOptions',
        parameters: {
          options: {
            _bluetoothPower: `${power}`,
          },
        },
      }),
    });
  } finally {
    clearTimeout(timeout);
  }
}
