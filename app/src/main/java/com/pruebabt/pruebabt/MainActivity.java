package com.pruebabt.pruebabt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String DEVICE_ADDRESS = "00:06:66:08:16:CA"; //MAC Address del modulo Bluetooth
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;

    Button bluetooth_connect_btn;

    String command; //variable de string que almacenará el valor que se transmitirá al módulo bluetooth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declaracion de la variable button
        bluetooth_connect_btn = findViewById(R.id.bluetooth_connect_btn);

        //Button que conecta el dispositivo al módulo bluetooth cuando se presiona
        bluetooth_connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(BTinit())
                {
                    BTconnect();
                }

            }
        });

    }

    //Inicializando BT modulo
    public boolean BTinit()
    {
        boolean found = false;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  //verifica si el dispositivo soporta bluetooth

        if(bluetoothAdapter == null) //verifica si el dispositivo soporta bluetooth
        {
            Toast.makeText(getApplicationContext(), "El Dispositivo No Soporta Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if(!bluetoothAdapter.isEnabled()) //Verifica si el BT esta habilitado. Si no, le solicita al usuario que lo active
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); // Solicita al usuario que  active BT
            startActivityForResult(enableAdapter,0);                       //Solicita al usuario que  active BT

            try
            {
                Thread.sleep(1000);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();

        if(bondedDevices.isEmpty()) //Comprueba si hay dispositivos Bluetooth emparejados
        {
            Toast.makeText(getApplicationContext(), "Por favor Emparejar Primero el Dispositvo", Toast.LENGTH_SHORT).show();
        }
        else
        {
            for(BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean BTconnect()
    {
        boolean connected = true;

        try
        {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID); //Crea un socket para manejar la conexión saliente
            socket.connect();

            Toast.makeText(getApplicationContext(),
                    "Conexión al Dispositivo Bluetooth exitosa", Toast.LENGTH_LONG).show();
        }
        catch(IOException e)
        {
            e.printStackTrace();
            connected = false;
        }

        if(connected)
        {
            try
            {
                outputStream = socket.getOutputStream(); //obtiene el output stream del socket
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        return connected;
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite
            // que no se deje abierto el socket
            socket.close();
        } catch (IOException e2) {}
    }

}
