class BluetoothState {
  final int value;
  int toValue() => value;
  @override
  String toString() => value == 2
      ? "STATE_CONNECTED"
      : value == 1
          ? "STATE_CONNECTING"
          : value == 0
              ? "STATE_DISCONNECTED"
              : value == 3
                  ? "STATE_DISCONNECTING"
                  : value == 10
                      ? "STATE_OFF"
                      : value == 12
                          ? "STATE_ON"
                          : value == 13
                              ? "STATE_TURNING_OFF"
                              : value == 11
                                  ? "STATE_TURNING_ON"
                                  : value == -1
                                      ? 'ERROR'
                                      : 'UNKNOWN';

  BluetoothState.fromValue(this.value);

  static final STATE_CONNECTED = BluetoothState.fromValue(2);
  static final STATE_CONNECTING = BluetoothState.fromValue(1);
  static final STATE_DISCONNECTED = BluetoothState.fromValue(0);
  static final STATE_DISCONNECTING = BluetoothState.fromValue(3);
  static final STATE_OFF = BluetoothState.fromValue(10);
  static final STATE_ON = BluetoothState.fromValue(12);
  static final STATE_TURNING_OFF = BluetoothState.fromValue(13);
  static final STATE_TURNING_ON = BluetoothState.fromValue(11);
  static final UNKNOWN = BluetoothState.fromValue(-2);

  @override
  operator ==(Object other) {
    return (other is BluetoothState && other.value == value) || value == other;
  }

  @override
  int get hashCode => value.hashCode;
}
