package com.ricoh360.thetableclient

import com.juul.kable.Characteristic
import com.juul.kable.characteristicOf

enum class BleCharacteristic(val bleService: BleService, val uuid: String) {

    // Camera Information

    FIRMWARE_REVISION(
        BleService.CAMERA_INFORMATION,
        "B4EB8905-7411-40A6-A367-2834C2157EA7",
    ),
    MANUFACTURER_NAME(
        BleService.CAMERA_INFORMATION,
        "F5666A48-6A74-40AE-A817-3C9B3EFB59A6",
    ),
    MODEL_NUMBER(
        BleService.CAMERA_INFORMATION,
        "35FE6272-6AA5-44D9-88E1-F09427F51A71",
    ),
    SERIAL_NUMBER(
        BleService.CAMERA_INFORMATION,
        "0D2FC4D5-5CB3-4CDE-B519-445E599957D8",
    ),
    WLAN_MAC_ADDRESS(
        BleService.CAMERA_INFORMATION,
        "1C5C6C55-8E57-4B32-AD80-B124AE229DEC",
    ),
    BLUETOOTH_MAC_ADDRESS(
        BleService.CAMERA_INFORMATION,
        "97E34DA2-2E1A-405B-B80D-F8F0AA9CC51C",
    ),

    // Camera Status Command

    BATTERY_LEVEL(
        BleService.CAMERA_STATUS_COMMAND,
        "875FC41D-4980-434C-A653-FD4A4D4410C4",
    ),
    BATTERY_STATUS(
        BleService.CAMERA_STATUS_COMMAND,
        "5429B6A0-66D6-491B-B906-902737D5442F",
    ),
    CAMERA_POWER(
        BleService.CAMERA_STATUS_COMMAND,
        "B58CE84C-0666-4DE9-BEC8-2D27B27B3211",
    ),
    COMMAND_ERROR_DESCRIPTION(
        BleService.CAMERA_STATUS_COMMAND,
        "4B03D05E-02D2-412B-A20B-578AE82B9C01",
    ),
    PLUGIN_CONTROL(
        BleService.CAMERA_STATUS_COMMAND,
        "A88732D5-6786-4312-9364-B9A4514DC123",
    ),

    // Camera Control Command

    PLUGIN_LIST(
        BleService.CAMERA_CONTROL_COMMANDS,
        "E83264B2-C52D-454E-95BD-6485DE912430",
    ),
    PLUGIN_ORDERS(
        BleService.CAMERA_CONTROL_COMMANDS,
        "8F710EDC-6F9B-45D4-A5F7-E6EDA304E790",
    ),

    // Shooting Control Command

    CAPTURE_MODE(
        BleService.SHOOTING_CONTROL_COMMAND,
        "78009238-AC3D-4370-9B6F-C9CE2F4E3CA8",
    ),
    CONTINUOUS_SHOOTING(
        BleService.SHOOTING_CONTROL_COMMAND,
        "E33B80DC-4661-458F-B873-AC5270F8AB5C",
    ),
    EXPOSURE_DELAY(
        BleService.SHOOTING_CONTROL_COMMAND,
        "D22B7C92-556E-4038-A5EF-A9AD56899B40",
    ),
    FILE_FORMAT(
        BleService.SHOOTING_CONTROL_COMMAND,
        "E8F0EDD1-6C0F-494A-95C3-3244AE0B9A01",
    ),
    ISO(
        BleService.SHOOTING_CONTROL_COMMAND,
        "ABB94D51-189F-455B-951D-ABE9B0333080",
    ),
    MAX_RECORDABLE_TIME(
        BleService.SHOOTING_CONTROL_COMMAND,
        "6EABAB73-7F2B-4061-BE7C-1D71D143CB7D",
    ),
    TAKE_PICTURE(
        BleService.SHOOTING_CONTROL_COMMAND,
        "FEC1805C-8905-4477-B862-BA5E447528A5",
    ),

    // Bluetooth Control Command

    AUTH_BLUETOOTH_DEVICE(
        BleService.BLUETOOTH_CONTROL_COMMAND,
        "EBAFB2F0-0E0F-40A2-A84F-E2F098DC13C3",
    ),
    BLUETOOTH_POWER_STATUS(
        BleService.BLUETOOTH_CONTROL_COMMAND,
        "1FBCBBFE-063D-411C-A1BD-67D758E804ED",
    ),

    // Camera Control Command v2

    GET_INFO(
        BleService.CAMERA_CONTROL_COMMAND_V2,
        "A0452E2D-C7D8-4314-8CD6-7B8BBAB4D523",
    ),

    GET_STATE(
        BleService.CAMERA_CONTROL_COMMAND_V2,
        "083D92B0-21E0-4FB2-9503-7D8B2C2BB1D1",
    ),

    GET_STATE2(
        BleService.CAMERA_CONTROL_COMMAND_V2,
        "8881CE4E-96FC-4C6C-8103-5DDA0AD138FB",
    ),

    NOTIFY_STATE(
        BleService.CAMERA_CONTROL_COMMAND_V2,
        "D32CE140-B0C2-4C07-AF15-2301B5057B8C",
    ),

    ;
    companion object {

        val keyName: String
            get() = "characteristic"

        fun getFromUuid(uuid: String): BleCharacteristic? {
            return values().firstOrNull { it.uuid.equals(uuid, true) }
        }

        fun get(characteristic: Characteristic): BleCharacteristic? {
            return getFromUuid(characteristic.characteristicUuid.toString().uppercase())
        }

        fun getServiceList(uuid: String): List<BleCharacteristic> {
            val list = mutableListOf<BleCharacteristic>()
            values().forEach {
                if (it.bleService.uuid.equals(uuid, true)) {
                    list.add(it)
                }
            }

            return list
        }
    }

    fun getCharacteristic(): Characteristic {
        return characteristicOf(bleService.uuid, uuid)
    }
}
