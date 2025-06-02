export interface ThetaInfo {
  model: string;
  serialNumber: string;
}

export async function getThetaInfoWebApi(): Promise<ThetaInfo | undefined> {
  const url = 'http://192.168.1.1/osc/info';
  const controller = new AbortController();
  const timeout = setTimeout(() => {
    controller.abort();
  }, 5000);

  try {
    const response = await fetch(url, {
      signal: controller.signal,
      method: 'GET',
      headers: {
        Accept: 'application/json',
        'Content-Type': 'application/json',
      },
    });
    const json = await response.json();
    return json;
  } catch {
    return;
  } finally {
    clearTimeout(timeout);
  }
}
