package com.bp.pruebabluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private int BLUETOOTH_ACTIVATION = 0;
    private int BLUETOOTH_SCAN = 1;

    private Set<BluetoothDevice> knownDevices;
    private final String ACTION_DISCOVER_START = BluetoothAdapter.ACTION_DISCOVERY_STARTED;
    private final String ACTION_DISCOVER_END = BluetoothAdapter.ACTION_DISCOVERY_FINISHED;

    private BluetoothAdapter bluetoothAdapter;

    /* BroadcastReceiver que permite la busqueda de nuevos dispositivos. La
    * busqueda debe ser un proceso asincrono. */
    private BroadcastReceiver discoverDevicesStarted = new BroadcastReceiver() {

        /* Gestiona la busqueda de nuevos dispositivos. */
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_DISCOVER_START)) { //Inicio de la busqueda.

            } else if (intent.getAction().equals(ACTION_DISCOVER_END)) { //Final de la busqueda.
                /* ACTION_FOUND se invoca cada vez que se encuentra un dispositivo.
                * Se obtendrá la lista de dispositivos encontrados. */
            } else if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                if (intent != null && intent.hasExtra(BluetoothDevice.EXTRA_DEVICE)) {
                    BluetoothDevice newDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Mediante getDefaultAdapter se comprueba si el dispositivo tiene Bluetooth.
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Toast.makeText(this, "Este dispositivo dispone de Bluetooth", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Este dispositivo no dispone de Bluetooth", Toast.LENGTH_LONG).show();
        }

        //Forzar activación del bluetooth sin solicitar permiso.
        //bluetoothAdapter.enable();

        /* Si no está activado se solicita permiso al usuario para activarlo
        mediante startActivityForResult. */
        if(!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BLUETOOTH_ACTIVATION);
        } else {
            Toast.makeText(this, "Bluetooth activado: " + bluetoothAdapter.getName(), Toast.LENGTH_LONG).show();
        }

        /* Solicita permiso al usuario para hacer detectable al dispositivo y permitir
        la conexión de otros dispositivos. */
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE), BLUETOOTH_SCAN);

        /* getBondedDevices permite conocer la lista de dispositivos ya asociados. */
        knownDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : knownDevices) { //Recorre y muestra los dispositivos asociados.
            Log.v("BluetoothActivity", "dispositivo = " + device.getName());
        }


    }

    /* Registra los BroadcastReceivers de la busqueda de dispositivos. */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(discoverDevicesStarted, new IntentFilter(ACTION_DISCOVER_START));
        registerReceiver(discoverDevicesStarted, new IntentFilter(ACTION_DISCOVER_END));
        //Inicia la busqueda.
        bluetoothAdapter.startDiscovery();
    }

    /* Cancela los BroadcastReceivers. */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(discoverDevicesStarted);
        //Detiene la busqueda.
        bluetoothAdapter.cancelDiscovery();
    }

    /* Gestiona la respuesta del usuario a la activación del bluetooth. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLUETOOTH_ACTIVATION) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Activación Bluetooth: Aceptada.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Activación Bluetooth: Rechazada.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
