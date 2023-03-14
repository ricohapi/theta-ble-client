import type { CameraPowerEnum,
  ChargingStateEnum,
  CommandErrorDescriptionEnum,
  PluginControl,
  ThetaState,
} from '../service';

export interface BaseNotify {
  deviceId: number
  characteristic: string,
  error?: NotifyError,
  /* eslint-disable-next-line @typescript-eslint/no-explicit-any */
  params?: any,
}

export interface NotifyError {
  message: string,
  params?: string,
}

export interface BatteryLevelNotify extends BaseNotify {
  params?: {
      batteryLevel: number,
  }
}

export interface BatteryStatusNotify extends BaseNotify {
  params?: {
    batteryState: ChargingStateEnum,
  }
}

export interface CameraPowerNotify extends BaseNotify {
  params?: {
    cameraPower: CameraPowerEnum,
  }
}

export interface CommandErrorDescriptionNotify extends BaseNotify {
  params?: {
    commandErrorDescription: CommandErrorDescriptionEnum,
  }
}

export interface PluginControlNotify extends BaseNotify {
  params?: PluginControl,
}

export interface ThetaStateNotify extends BaseNotify {
  params?: ThetaState,
}
