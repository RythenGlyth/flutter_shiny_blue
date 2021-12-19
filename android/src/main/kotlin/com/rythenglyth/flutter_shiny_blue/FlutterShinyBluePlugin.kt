package com.rythenglyth.flutter_shiny_blue

import android.Manifest
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
import android.bluetooth.BluetoothDevice.ACTION_FOUND
import android.content.Intent
import android.content.Context
import android.content.IntentFilter
import android.content.BroadcastReceiver
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/** FlutterShinyBluePlugin */
class FlutterShinyBluePlugin : FlutterPlugin, ActivityAware {
    private lateinit var defaultChannel: MethodChannel
    private lateinit var stateChannel: EventChannel
    private lateinit var stateReceiver: BroadcastReceiver
    private lateinit var deviceFoundChannel: EventChannel
    private lateinit var deviceFoundReceiver: BroadcastReceiver

    final var namespace = "com.rythenglyth.flutter_shiny_blue";

    private lateinit var activity: Activity

    override fun onAttachedToActivity(@NonNull binding: ActivityPluginBinding) {
        this.activity = binding.activity;

        this.stateChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
                stateReceiver = object : BroadcastReceiver() {
                    override fun onReceive(contxt: Context, intent: Intent) {
                        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                            eventSink?.success(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothDevice.ERROR))
                        }
                    }
                };
                activity.applicationContext.registerReceiver(stateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
            }

            override fun onCancel(arguments: Any?) {}
        })
        this.deviceFoundChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, eventSink: EventChannel.EventSink?) {
                deviceFoundReceiver = object : BroadcastReceiver() {
                    override fun onReceive(contxt: Context, intent: Intent) {
                        if (intent.action == BluetoothAdapter.ACTION_STATE_CHANGED) {
                            System.out.println("sas");
                            val device: BluetoothDevice? =
                                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                            System.out.println(device);
                            eventSink?.success(device)
                        }
                    }
                };
                activity.applicationContext.registerReceiver(deviceFoundReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
            }

            override fun onCancel(arguments: Any?) {}
        })
        if(ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    arrayOf<String>( Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION ),
                    0)
        }
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
            when (call.method) {
                "doesDeviceSupportBluetooth" -> {
                    result.success(bluetoothAdapter != null)
                }
                "getBluetoothState" -> {
                    result.success(bluetoothAdapter?.state)
                }
                "enableBluetooth" -> {
                    if (bluetoothAdapter?.isEnabled == false) {
                        ActivityCompat.startActivityForResult(activity, Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0, null)
                    }
                    result.success(null);
                }
                "startDiscovery" -> {
                    result.success(bluetoothAdapter?.startDiscovery());
                }
                else -> {
                    result.notImplemented()
                }
            }
        }


        this.deviceFoundChannel = EventChannel(flutterPluginBinding.binaryMessenger, "$namespace/bluetooth_deviceFound");
        this.stateChannel = EventChannel(flutterPluginBinding.binaryMessenger, "$namespace/bluetooth_state");

    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        defaultChannel.setMethodCallHandler(null)
        stateChannel.setStreamHandler(null)

        activity.applicationContext.unregisterReceiver(stateReceiver);
        activity.applicationContext.unregisterReceiver(deviceFoundReceiver);
    }
}
