package com.dantsu.thermalprinter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.dantsu.thermalprinter.async.AsyncEscPosPrinter;

import java.io.InputStream;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Printingmethodhelper {
    String v;
    private String DeliveryMode;
    private Context context;
    DeviceConnection printerConnection;
    StringBuilder str;
    invoicehelper inhelp;
    String SHARED_PREF="sharedprefer";
    String SETTING="setting";
    Bitmap bitmap;
//    SharedPreferences mSharedPreference=context.getSharedPreferences(SHARED_PREF,context.MODE_PRIVATE);
//    int i=mSharedPreference.getInt(SETTING,1);
    public AsyncEscPosPrinter getAsyncEscPosPrinterbillprint(DeviceConnection printerConnection, StringBuilder str, invoicehelper inhelp,int i) {
try{
    String ul="https://hmsdocuments.s3.us-east-2.amazonaws.com/qr.PNG2022-09-16T06:31:31";
    URL connect=new URL(ul);
    InputStream in=connect.openStream();
    bitmap= BitmapFactory.decodeStream(in);
}
catch (Exception e){

}

        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
//        // current time
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd MMM yyyy HH:mm a");
        Format f = new SimpleDateFormat("HH:mm");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 220, 75, 47);
        String s = null;
       switch (i){
           case 1:
              s="[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,bitmap)+"</img>\n" +
                      "[C]<u><font size='big'>" + inhelp.bussname + "</font></u>\n" +
                       "[C]<b>"+inhelp.bussadd + "\n" +

                       "[C]<b>Tel:" + inhelp.bussphone + "\n" +
                       "[L]<b>Bill:" + inhelp.invoicenum + "" + "[R]" + "Date:" + "[L]" + format.format(new Date()) + "\n" +
                       "[L]<b>Mode:" + inhelp.DeliveryMode + "<u type='double'>" + " [R]Time:" + "[L]" + f.format(new Date()) + "\n" +

                       "-----------------------------------------\n" +
                       "[C]<b>Product [R]Quantity [C]Rate [L]Value </b>\n" +
                       "-----------------------------------------\n" +
                       "[L]" + str +
                       "----------------------------------------\n" +
                       "[L]<b >Total Item" + "[R]" + inhelp.itemtotal + "[C]" + "Total" + "[L]" + inhelp.valuetotal + "</b>\n" +
                       "----------------------------------------\n" +
                       "[C]<u>Thank You Visit Again</u>!!!!" ;
               break;
               case 2:
               s= "[C]<b>" + "Invoice" + "</u>\n" +
                       "[L]<b>"+"Bill:" + inhelp.invoicenum + "\n" +
                       "[L]<b>" + "Date:" + format.format(new Date()) +"Time:"+f.format(new Date())+"\n"+
                       "[L]<b>Delivery Mode:" + inhelp.DeliveryMode + " <u type='double'>\n" +
                       "[L]<b>Name: " + inhelp.bussname + "</font></u>\n" +
                       "[L]<b>Address: " + inhelp.bussadd + "\n" +
                       "[L]<b>city: " + inhelp.busscity + "\n" + "[L]<b>state: " + inhelp.bussstate + "\n" +
                       "[L]<b>GSTIN NO: " + inhelp.bussgst + "\n" +
                       "-----------------------------------------\n" +
                       "[C]<b>Product [R]Quantity [C]Rate [L]Value </b>\n" +
                       "-----------------------------------------\n" +
                       "[L]" + str +
                       "----------------------------------------\n" +
                       "[L]<b >Total Item" + "[R]" + inhelp.itemtotal + "[C]" + "Total" + "[L]" + inhelp.valuetotal + "</b>\n" +
                       "----------------------------------------\n" +
                       "                        [R]<b>" + "CGST %:" + "[L]" + "[L]" + inhelp.cgst + "\n" +
                       "                        [R]<b>" + "SGST %:" + "[L]" + "[L]" + inhelp.sgst + "\n" +
                       "----------------------------------------\n" +
                       "                 [R]<b>" + "Grand Total :" + "[R]" + "[L]" + inhelp.gsttotal + "  \n" +
                       "----------------------------------------\n" +
                       "[C]<u>Thank You Visit Again</u>!!!!";
               break;
               case 3:
               s="[C]<u><font size='big'>" + inhelp.bussname + "</font></u>\n" +
               "[C]<b>"+ inhelp.bussadd + "\n" +

                       "[C]<b>Tel:" + inhelp.bussphone + "\n" +
                       "-----------------------------------------\n" +
                       "[L]Date:" + format.format(new Date())+"@"+f.format(new Date())+"[L]Bill:" + inhelp.invoicenum + "\n"+

                       "-----------------------------------------\n" +
                       "[C]<b>Product [R]Quantity [C]Rate [L]Value </b>\n" +
                       "-----------------------------------------\n" +
                       "[L]" + str +
                       "----------------------------------------\n" +
                       "[L]<b >Total Item" + "[R]" + inhelp.itemtotal + "[C]" + "Total" + "[L]" + inhelp.valuetotal + "</b>\n" +
                       "----------------------------------------\n" +
                       "[C]<u>Thank You Visit Again</u>!!!!" ;
               break;
               case 4:
               s= "[C]<b>Name: "+inhelp.bussname+"</font></u>\n" +
                       "[C]<b>Bill:"+inhelp.invoicenum+"\n"+
                       "[C]<b>" + "Date:" + format.format(new Date()) +"[C]Time:"+f.format(new Date())+"\n"+
                       "-----------------------------------------\n" +
                       "[C]<b>Product [C]Quantity [C]Rate [L]Value </b>\n"+
                       "-----------------------------------------\n" +
                       "[L]"+str+
                       "----------------------------------------\n" +
                       "[L]<b >Total Item"+"[R]"+inhelp.itemtotal+"[C]"+"Total"+"[L]"+inhelp.valuetotal+"</b>\n"+
                       "----------------------------------------\n" +
                           "                    [R]<b>"+"CGST %:"+"[L]"+"[L]"+inhelp.cgst+"\n"+
                           "                    [R]<b>"+"SGST %:"+"[L]"+"[L]"+inhelp.sgst+"\n"+
                       "----------------------------------------\n" +
                           "            [R]<b>"+"Grand Total :"+"[R]"+"[L]"+inhelp.gsttotal+"  \n"+
                       "----------------------------------------\n" +
                        "[C]<u>Thank You Visit Again</u>!!!!" ;
               break;
               case 5:
               s=      "[C]<b>Name: "+inhelp.bussname+"</font></u>\n" +
                       "[C]<b>"+ inhelp.bussadd +"\n"+
                       "[C]Tel:"+inhelp.bussphone+"\n"+
                       "[C]<b>"+"Date:"+simpleformat.format(new Date())+"\n"+
                       "[C]<b>Mode:" + inhelp.DeliveryMode + " <u type='double'>\n" +
                       "-----------------------------------------------\n" +
                       "[L]<b>"+"Tableids:Tab30 "+"[R]"+"User:"+"[L]"+inhelp.bussname+"</font></u>\n" +
                       "-----------------------------------------------\n" +
                       "[C]<b>Product [C]Quantity [C]Rate [L]Value </b>\n"+
                       "---------------------------------------\n" +
                       "[L]"+str+
                       "----------------------------------------\n" +
                       "[L]<b >Total Item"+"[R]"+inhelp.itemtotal+"[C]"+"Total"+"[L]"+inhelp.valuetotal+"</b>\n"+
                       "----------------------------------------\n" +
                       "[C]<qrcode size='20'>https://hmsdocuments.s3.us-east-2.amazonaws.com/qr.PNG2022-09-16T06:31:31</qrcode>\n"+
                       "[C]<u>Thank You Visit Again</u>!!!!";
               break;

       }
        return printer.addTextToPrint(s);
    }

    public AsyncEscPosPrinter getAsyncEscPosPrinterbillprint2(DeviceConnection printerConnection, StringBuilder str, invoicehelper inhelp) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        Format f = new SimpleDateFormat("HH:mm");
        String strResult = f.format(new Date());
        System.out.println("Time = " + strResult);
        System.out.println(str);
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 220, 70, 47);
        return printer.addTextToPrint(
                "[C]<u><font size='big'>" + inhelp.bussname + "</font></u>\n" +
                        "[C]" + inhelp.bussadd + "\n" +

                        "[C]Tel:" + inhelp.bussphone + "\n" +
                        "[L]Invoice No:" + inhelp.invoicenum + "" + "[R]" + "Date:" + "[L]" + format.format(new Date()) + "\n" +
                        "[L]Mode:" + inhelp.DeliveryMode + "<u type='double'>" + " [R]Time:" + "[L]" + f.format(new Date()) + "\n" +

                        "-----------------------------------------\n" +
                        "[C]<b>Product [R]Quantity [C]Rate [L]Value </b>\n" +
                        "-----------------------------------------\n" +
                        "[L]" + str +
                        "----------------------------------------\n" +
                        "[L]<b >Total Item" + "[R]" + inhelp.itemtotal + "[C]" + "Total" + "[L]" + inhelp.valuetotal + "</b>\n" +
                        "----------------------------------------\n" +
                        "                        [R]<b>" + "CGST %:" + "[L]" + "[L]" + inhelp.cgst + "\n" +
                        "                        [R]<b>" + "SGST %:" + "[L]" + "[L]" + inhelp.sgst + "\n" +
                        "----------------------------------------\n" +
                        "                 [R]<b>" + "Grand Total :" + "[R]" + "[L]" + inhelp.gsttotal + "  \n" +
                        "----------------------------------------\n" +
                        "[C]<u>Thank You Visit Again</u>!!!!"
        );

    }

    public AsyncEscPosPrinter getAsyncEscPosPrinterbillprint3(DeviceConnection printerConnection, StringBuilder str, invoicehelper inhelp) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        Format f = new SimpleDateFormat("HH:mm");
        String strResult = f.format(new Date());
        System.out.println("Time = " + strResult);
        System.out.println(str);
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 220, 70, 47);
        return printer.addTextToPrint(
                "[C]<b>" + "Invoice" + "</u>\n" +
                        "[L]<b>Invoice No:" + inhelp.invoicenum + "\n" +
                        "[L]<b>" + "Date:" + format.format(new Date()) + "\n" +
                        "[L]<b>Delivery Mode:" + inhelp.DeliveryMode + " <u type='double'>\n" +
                        "[L]<b>Name: " + inhelp.bussname + "</font></u>\n" +
                        "[L]<b>Address: " + inhelp.bussadd + "\n" +
                        "[L]<b>city: " + inhelp.busscity + "\n" + "[L]<b>state: " + inhelp.bussstate + "\n" +
                        "[L]<b>GSTIN NO: " + inhelp.bussgst + "\n" +
                        "-----------------------------------------\n" +
                        "[C]<b>Product [R]Quantity [C]Rate [L]Value </b>\n" +
                        "-----------------------------------------\n" +
                        "[L]" + str +
                        "----------------------------------------\n" +
                        "[L]<b >Total Item" + "[R]" + inhelp.itemtotal + "[C]" + "Total" + "[L]" + inhelp.valuetotal + "</b>\n" +
                        "----------------------------------------\n" +
                        "                        [R]<b>" + "CGST %:" + "[L]" + "[L]" + inhelp.cgst + "\n" +
                        "                        [R]<b>" + "SGST %:" + "[L]" + "[L]" + inhelp.sgst + "\n" +
                        "----------------------------------------\n" +
                        "                 [R]<b>" + "Grand Total :" + "[R]" + "[L]" + inhelp.gsttotal + "  \n" +
                        "----------------------------------------\n" +
                        "[C]<u>Thank You Visit Again</u>!!!!"
        );

    }
    public AsyncEscPosPrinter getAsyncEscPosPrinterbillprint4(DeviceConnection printerConnection, StringBuilder str, invoicehelper inhelp) {
        SimpleDateFormat simpleformat = new SimpleDateFormat("dd MMM yyyy HH:mm a");
        System.out.println(str);
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 70, 45);

        return printer.addTextToPrint(
                        "[C]<b>Invoice No:"+inhelp.invoicenum+"\n"+
                        "[C]<b>Name: "+inhelp.bussname+"</font></u>\n" +
                        "[C]<b>"+inhelp.bussadd +"\n"+
                        "[C]Tel:"+inhelp.bussphone+"\n"+
                        "[C]<b>"+"Date:"+simpleformat.format(new Date())+"\n"+
                        "[C]<b>"+"Draft Bill"+"\n"+
                        "[C]<b>"+"Dine In"+"\n"+
                        "-----------------------------------------------\n" +
                        "[L]<b>"+"Tableids:Tab30 "+"[R]"+"User:"+"[L]"+inhelp.bussname+"</font></u>\n" +
                        "-----------------------------------------------\n" +
                        "[C]<b>Product [C]Quantity [C]Rate [L]Value </b>\n"+
                        "---------------------------------------\n" +
                        "[L]"+str+
                        "--------------------------------------\n" +
                        "[L]<b >Total Item"+"[R]"+inhelp.itemtotal+"[C]"+"Total"+"[L]"+inhelp.valuetotal+"</b>\n"+
                        "--------------------------------------\n" +
                        " [L]<b>"+"Grand Total :[C]"+"[L]"+inhelp.valuetotal+"  \n"+
                        "--------------------------------------\n" +
                        "[C]<u>Thank You Visit Again</u>!!!!"


        );

    }

}
