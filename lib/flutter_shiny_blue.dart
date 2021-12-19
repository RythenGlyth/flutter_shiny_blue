// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'bluetooth_state.dart';

class FlutterShinyBlue {
  static const bluetoothPlatform =
      MethodChannel('com.rythenglyth.flutter_shiny_blue/bluetooth');

  static FlutterShinyBlue _instance = FlutterShinyBlue();

  static FlutterShinyBlue get instance => _instance;

  //methods

  Future<bool> doesDeviceSupportBluetooth() async {
    final bool does =
        await bluetoothPlatform.invokeMethod('doesDeviceSupportBluetooth');
    return does;
  }

  Future<BluetoothState> getBluetoothState() async {
    final int state = await bluetoothPlatform.invokeMethod('getBluetoothState');
    return BluetoothState.fromValue(state);
  }

  Future<void> enableBluetooth() async {
    await bluetoothPlatform.invokeMethod('enableBluetooth');
  }

  Future<bool> startDiscovery() async {
    final bool succeeded =
        await bluetoothPlatform.invokeMethod('startDiscovery');
    return succeeded;
  }

  //listener

  static const EventChannel _stateChannel =
      EventChannel('com.rythenglyth.flutter_shiny_blue/bluetooth_state');

  Stream<BluetoothState> onStateChanged() => _stateChannel
      .receiveBroadcastStream()
      .map((val) => BluetoothState.fromValue(val));

  static const EventChannel _deviceFoundChannel =
      EventChannel('com.rythenglyth.flutter_shiny_blue/bluetooth_deviceFound');

  Stream<dynamic> onDeviceFound() =>
      _deviceFoundChannel.receiveBroadcastStream();
}

class BluetoothAdapter extends ChangeNotifier {
  static BluetoothAdapter _instance = BluetoothAdapter();

  static BluetoothAdapter get instance => _instance;

  BluetoothState state = BluetoothState.UNKNOWN;

  BluetoothAdapter() {
    FlutterShinyBlue.instance.getBluetoothState().then((state) {
      this.state = state;
      notifyListeners();
    });
    FlutterShinyBlue.instance.onStateChanged().listen((state) {
      this.state = state;
      notifyListeners();
    });
    FlutterShinyBlue.instance.onDeviceFound().listen((device) {
      print("asas");
      print(device);
    });
  }
}
