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

public class WebAppInterface {
    Context context;
    String data1,data2,orderid,kotorderid,invoicenum;
    private String DeliveryMode,bussname;

    int itemtotal;
    int valuetotal;
    StringBuilder str = new StringBuilder();

    public WebAppInterface(Context c){
        context=c;
    }

    @JavascriptInterface
    public void printtext(String cartdata,String orderdata,String billingtype) {

        int t= Integer.parseInt(billingtype);
        orderid = orderdata;
        browseBluetoothDevice();
        if(t==1)
        {
        data1 = cartdata;
        jsonparse1();
        printBluetoothkot();
         }
          else if(t==2){
              data2=cartdata;
             jsonparse2();
              printBluetoothbill();
          }

    }


    public void jsonparse1(){
        try {
            str.setLength(0);
            JSONObject idobj = new JSONObject(String.valueOf(orderid));
            String num1= idobj.getString("orderid");
            String num2= idobj.getString("invoiceno");
            String busname=idobj.getString("bussname");
            kotorderid=num1;
            invoicenum=num2;
            bussname=busname;
            JSONObject obj = new JSONObject(String.valueOf(data1));
            JSONArray cartitem = obj.getJSONArray("orderItems");
            String Mode= obj.getString("deliveryMode");
            DeliveryMode=Mode;
            int length = obj .length();
            for(int i=0; i<length; i++) {
                JSONObject jsonObj = cartitem.getJSONObject(i);

                int quantity = jsonObj.getInt("quantity");
                String name = jsonObj.getString("name");

                str.append((String.format("%s [C] %s", name,quantity)+"\n"));

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void jsonparse2(){

        try {
            str.setLength(0);
            JSONObject idobj = new JSONObject(String.valueOf(orderid));
            String num1= idobj.getString("orderid");
            String num2= idobj.getString("invoiceno");
            String busname=idobj.getString("bussname");
            kotorderid=num1;
            invoicenum=num2;
            bussname=busname;
            JSONObject obj = new JSONObject(String.valueOf(data2));
            JSONArray cartitem = obj.getJSONArray("orderItems");
            String Mode= obj.getString("deliveryMode");
            int grosstot=obj.getInt("grossTotal");
            int itmtot=obj.getInt("itemCount");
            itemtotal=itmtot;
            valuetotal=grosstot;
            DeliveryMode=Mode;
            int length = obj .length();

            for(int i=0; i<length; i++) {
                JSONObject jsonObj = cartitem.getJSONObject(i);
//                Toast.makeText(context, jsonObj.getString("name"), Toast.LENGTH_LONG).show();

                // getting inner array Ingredients
                int quantity = jsonObj.getInt("quantity");
                String name = jsonObj.getString("name");
                int price=jsonObj.getInt("price");
                int value=quantity * price;
                str.append((String.format("%s[R]%s[C]%s[L]%s", name,quantity,price,value)+"\n"));
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
                    this.printBluetoothkot();
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

                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
//            alert.show();

        }
    }

    public void printBluetoothkot() {
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
                    .execute(this.getAsyncEscPosPrinterkot(selectedDevice));
        }
    }
    public void printBluetoothbill() {
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
                    .execute(this.getAsyncEscPosPrinterbillprint(selectedDevice));
        }
    }


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
                                    .execute(getAsyncEscPosPrinterkot(new UsbConnection(usbManager, usbDevice)));
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
    public void printTcp() {


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
                            this.getAsyncEscPosPrinterkot(
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

    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinterkot(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat(" yyyy-MM-dd 'at' HH:mm:ss");

        System.out.println(str);
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 320, 80, 50);
        return printer.addTextToPrint(

                        "[C]<u><font size='medium'>ORDER:"+kotorderid+"</font></u>\n" +
                        "[C]Mode:"+DeliveryMode+"\n" +
                        "[C]<u type='double'>" + format.format(new Date()) + "</u>\n" +
//                        "[C]\n" +
                        "[C]================================\n" +
                        "[L] "+str

        );

    }
    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinterbillprint(DeviceConnection printerConnection) {
        SimpleDateFormat format = new SimpleDateFormat(" yyyy-MM-dd 'at' HH:mm");


        System.out.println(str);
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 320, 80, 50);
        return printer.addTextToPrint(
                "[C]<u><font size='big'>"+bussname+"</font></u>\n" +

                        "[C]Invoice No:"+invoicenum+"\n"+
                        "[C]"+DeliveryMode+"<u type='double'>" + format.format(new Date()) +"</u>\n"+

                        "================================================\n" +
                        "[C]<b>Product [R]Quantity [C]Rate [L]Value </b>\n"+
                        "------------------------------------------------\n" +
                        "[L]"+str+
                        "------------------------------------------------\n" +
                        "[L]<b>Total Item"+"[R]"+itemtotal+"[C]"+"Total"+"[L]"+valuetotal+"</b>\n"+
                        "------------------------------------------------\n" +
                        "[C]<u>Thank You Visit Again</u>!!!!"
        );

    }
        public void clearitem(){
    str.setLength(0);
        }

}
