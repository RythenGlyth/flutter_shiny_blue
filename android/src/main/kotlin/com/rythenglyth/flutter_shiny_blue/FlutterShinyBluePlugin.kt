package com.rythenglyth.flutter_shiny_blue

import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat;
import android.app.Activity;

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.EventChannel
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.content.BroadcastReceiver

/** FlutterShinyBluePlugin */
class FlutterShinyBluePlugin: FlutterPlugin, ActivityAware {
  private lateinit var defaultChannel : MethodChannel
  private lateinit var stateChannel : EventChannel
  
  final var namespace = "com.rythenglyth.flutter_shiny_blue";

  private lateinit var activity : Activity

    override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
        this.activity = binding.activity;
    }
    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }
    override fun onDetachedFromActivity() {}
    override fun onDetachedFromActivityForConfigChanges() {}

      override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        defaultChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "$namespace/bluetooth");
        defaultChannel.setMethodCallHandler { call, result ->
            when(call.method) {
                "doesDeviceSupportBluetooth" -> {
                    result.success(bluetoothAdapter != null)
                }
                "getBluetoothState" -> {
                    result.success(bluetoothAdapter?.state)
                }
                "enableBluetooth" -> {
                    if(bluetoothAdapter?.isEnabled == false) {
                        ActivityCompat.startActivityForResult(activity, Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0, null)
                    }
                    result.success(null);
                }
                else -> {
                    result.notImplemented()
                }
            }
        }


        this.stateChannel = EventChannel(flutterPluginBinding.binaryMessenger, "$namespace/bluetooth_state");
        this.stateChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
                activity.applicationContext.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(contxt: Context, intent: Intent) {
                        if(intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                            eventSink?.success(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothDevice.ERROR))
                        }
                    }
                }, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
            }

            override fun onCancel(arguments: Any?) {}
        })
      }
      override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        defaultChannel.setMethodCallHandler(null)
        stateChannel.setStreamHandler(null)
      }
}
