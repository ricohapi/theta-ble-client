package com.ricoh360.thetableclient

enum class BleService(
    val uuid: String,
    val serviceName: String,
) {
    SERVICE_UUID(
        "99999999-9999-9999-9999-999999999999",
        ""
    ),   // TODO: For THETA search, and set it up if you get permission to publish.
    CAMERA_INFORMATION(
        "9A5ED1C5-74CC-4C50-B5B6-66A48E7CCFF1",
        "Camera Information"
    ),
    CAMERA_STATUS_COMMAND(
        "8AF982B1-F1FF-4D49-83F0-A56DB4C431A7",
        "Camera Status Command"
    ),
    CAMERA_CONTROL_COMMANDS(
        "32886D39-BA23-425C-BCAE-9C1DB0066922",
        "Camera Control Commands"
    ),
    SHOOTING_STATUS_COMMAND(
        "9AD04FDF-E62B-43E4-8593-7631FCD29874",
        "Shooting Status Command"
    ),
    SHOOTING_CONTROL_COMMAND(
        "1D0F3602-8DFB-4340-9045-513040DAD991",
        "Shooting Control Command"
    ),
    SHOOTING_CONTROL_COMMAND_V2(
        "38EF1533-B0CC-4722-B6B6-8B23C27ECE5C",
        "Shooting Control Command v2"
    ),
    GPS_CONTROL_COMMAND(
        "84A0DD62-E8AA-4D0F-91DB-819B6724C69E",
        "GPS Control Command"
    ),
    WLAN_CONTROL_COMMAND(
        "F37F568F-9071-445D-A938-5441F2E82399",
        "WLAN Control Command"
    ),
    BLUETOOTH_CONTROL_COMMAND(
        "0F291746-0C80-4726-87A7-3C501FD3B4B6",
        "Bluetooth Control Command"
    ),
    CAMERA_CONTROL_COMMAND_V2(
        "B6AC7A7E-8C01-4A52-B188-68D53DF53EA2",
        "Camera Control Command v2"
    ),
    WLAN_CONTROL_COMMAND_V2(
        "3C6FEEB6-F335-4F93-A4BB-495F926DB409",
        "WLAN Control Command v2"
    ),
    ;
}
