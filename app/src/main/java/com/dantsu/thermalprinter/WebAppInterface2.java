package com.dantsu.thermalprinter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.tcp.TcpConnection;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.thermalprinter.async.AsyncBluetoothEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrinter;
import com.dantsu.thermalprinter.async.AsyncTcpEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncUsbEscPosPrint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WebAppInterface2 {
    Context context;
    String bussname,data2,invoice;
    private String ItemName,DeliveryMode;
    private int ItemQun,itemtotal,valuetotal;
    StringBuilder str = new StringBuilder();
    public WebAppInterface2(Context c){
        context=c;
    }

    @JavascriptInterface
    public void printtextbill(String toast) {
        data2=toast;
        Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
        browseBluetoothDevice();
        jsonparse2();
        printBluetooth();
    }


    public void jsonparse2(){

        try {
            str.setLength(0);
            JSONObject obj = new JSONObject(String.valueOf(data2));
            JSONArray cartitem = obj.getJSONArray("orderItems");
            String Mode= obj.getString("deliveryMode");
            int grosstot=obj.getInt("grossTotal");
            int itmtot=obj.getInt("itemCount");
            itemtotal=itemtotal;
            valuetotal=grosstot;
            DeliveryMode=Mode;
            int length = obj .length();

            for(int i=0; i<length; i++) {
                JSONObject jsonObj = cartitem.getJSONObject(i);
                Toast.makeText(context, jsonObj.getString("name"), Toast.LENGTH_LONG).show();

                // getting inner array Ingredients
                int quantity = jsonObj.getInt("quantity");
                String name = jsonObj.getString("name");
                int price=jsonObj.getInt("price");
                int value=quantity * price;
                str.append((String.format("%s [C] %s", name,quantity,price,value)+"\n"));
          }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    /*==============================================================================================
    ======================================BLUETOOTH PART============================================
    ==============================================================================================*/

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_BLUETOOTH:
                case PERMISSION_BLUETOOTH_ADMIN:
                case PERMISSION_BLUETOOTH_CONNECT:
                case PERMISSION_BLUETOOTH_SCAN:
                    this.printBluetooth();
                    break;
            }
        }
    }

    private BluetoothConnection selectedDevice;

    @SuppressLint("MissingPermission")
    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if (index == -1) {
                        selectedDevice = null;
                    } else {
                        selectedDevice = bluetoothDevicesList[index];
                    }
//                    Button button = (Button) findViewById(R.id.button_bluetooth_browse);
//                    button.setText(items[i]);
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
//            alert.show();

        }
    }

    public void printBluetooth() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.BLUETOOTH_SCAN}, PERMISSION_BLUETOOTH_SCAN);
        } else {
            new AsyncBluetoothEscPosPrint(
                    context,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                        }
                    }
            )
                    .execute(this.getAsyncEscPosPrinter(selectedDevice));
        }
    }

    /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager)getSystemService(context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            new AsyncUsbEscPosPrint(
                                    context,
                                    new AsyncEscPosPrint.OnPrintFinished() {
                                        @Override
                                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                                        }

                                        @Override
                                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                                        }
                                    }
                            )
                                    .execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice)));
                        }
                    }
                }
            }
        }
    };

    public void printUsb() {
        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(context);
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        if (usbConnection == null || usbManager == null) {
            new AlertDialog.Builder(context)
                    .setTitle("USB Connection")
                    .setMessage("No USB printer found.")
                    .show();
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                context,
                0,
                new Intent(ACTION_USB_PERMISSION),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
        );
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
    }

    private Object getSystemService(String usbService) {
        return this;
    }


    /*==============================================================================================
    =========================================TCP PART===============================================
    ==============================================================================================*/

    public void printTcp() {
//        final EditText ipAddress = (EditText) this.findViewById(R.id.edittext_tcp_ip);
//        final EditText portAddress = (EditText) this.findViewById(R.id.edittext_tcp_port);

        try {
            BreakIterator portAddress=null;
            BreakIterator ipAddress = null;
            new AsyncTcpEscPosPrint(
                    context,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                        }
                    }
            )
                    .execute(
                            this.getAsyncEscPosPrinter(
                                    new TcpConnection(
                                            ipAddress.getText().toString(),
                                            Integer.parseInt(portAddress.getText().toString())
                                    )
                            )
                    );
        } catch (NumberFormatException e) {
            new AlertDialog.Builder(context)
                    .setTitle("Invalid TCP port address")
                    .setMessage("Port field must be an integer.")
                    .show();
            e.printStackTrace();
        }
    }

    /*==============================================================================================
    ===================================ESC/POS PRINTER PART=========================================
    ==============================================================================================*/

    /**
     * Asynchronous printing
     */
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
//        for(int i=0; i<str.length(); i++) {
//
//        }
        System.out.println(str);
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 200, 44f, 50);
        return printer.addTextToPrint(
//                "[R] Angualr Data cominng" + "\n"+
//                        "[L]\n" +
                "[C]<u><font size='medium'>ORDER</font></u>\n" +
                        "[C]Mode:"+bussname+"\n" +
                        "[C]Invoice No:"+invoice+"\n"+
                        "[L]"+DeliveryMode+"[R]<u type='double'>" + format.format(new Date()) +"</u>\n"+
//                        "[C]<u type='double'>" + format.format(new Date()) + "</u>\n" +
//                        "[C]\n" +
                        "[C]================================\n" +
                        "[L]# Product Quantity Rate Value \n"+
                        "[L] "+str+
                        "[L] Total Item"+itemtotal+"Total"+valuetotal+"\n"+
                        "[C]Thank You Visit Again!!!!"

        );
    }


}
