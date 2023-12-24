package com.yana.yanagen4.Libs;




import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class AppVariables
{
    public static HashMap<String,String> dtclist = new HashMap<String,String>();
    public static ArrayList emsrp = new ArrayList();
    public static String StoreOTP ="";
    public static String BluetoothDeviceName ="MIC43_FLASHER_2";
    public static String Email = "jaks978@gmail.com";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static String SAVEDFBLFILENAME = "" ;
    public static String FLASHEDFBLFILENAME = "" ;

    public static String Currentvci = "" ;
    public static List vcino = null;
    public static int pwd = 0;
    public static String prop_data = "ABCDEFGHIJKLMNOPQRSTUVWX" ;
    public static String ref_data = "ABCDEFGHIJKLMNOP" ;
    public static String Serialnumber = "" ;
    public static int VINway =0;
    public static ArrayList NRC = new ArrayList();
    public static int badgeems =0,badgeabs =0,badgecluster =0;




    public static boolean isaBoolean() {
        return aBoolean;
    }

    public static void setaBoolean(boolean aBoolean) {
        AppVariables.aBoolean = aBoolean;
    }

    public static boolean aBoolean = false;

    /* Link for Download User Details From cloud*/
    public static String Link_User_Details = "http://www.pickhr.com/i_connect_api/get_device/?product_id=2224";

    /* Link for Validate Email_id */
    public static String Link_Email_Valid = "https://realtimemonitroingsystem.000webhostapp.com/SendMail.php";

    public static byte[] currentcmd;
    public static int OTP()
    {
        Random lRand= new Random();
        int Low = 100000;
        int High = 999999;

        int lRandNum = (lRand.nextInt(High-Low) + Low);//(short) 0xA5A5;//
        return lRandNum;
    }
    public static  void Storepwd(Context context, int password)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putInt("pwd", password);
        editor.apply();
    }
    public static int Retpwd(Context context)
    {
        int i =0;
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        int restoredText = prefs.getInt("pwd", 0);
        if (restoredText != 0)
        {
            i = prefs.getInt("pwd", 0); //0 is the default value.
            // String[] GPXFILES1 = myset.toArray(new String[myset.size()]);
        }
        return i;
    }
    /* public void Req_TimeOut(Context context)
     {
         final Dialog dialog = new Dialog(context);
         dialog.setContentView(R.layout.dialog_req_timeout);
         Button b1 = (Button) dialog.findViewById(R.id.Dismiss);
         try
         {
             b1.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     dialog.dismiss();
                 }
             });
         }
         catch (Exception e)
         {

         }
         dialog.show();
     }*/
    public static  void StoreSet(Context context, Set arr)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE).edit();
        editor.putStringSet("vci", arr);
        editor.apply();
    }
    public static Set RetSet(Context context)
    {
        Set set = new HashSet();
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        set = prefs.getStringSet("vci", null);
        if (set!=null)
        {
            set = prefs.getStringSet("vci",null); //0 is the default value.
        }
        return set;
    }
    public static String GenLogLine(String str)
    {
        String logline = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();
        String strdate = sdf.format(date);
        logline = strdate+"  :  "+str;
        return logline;
    }

    public static String FormCmd(String siddid, String payload)
    {
        String cmdlen = null;
        int strlen=0;
        if(payload.equals(""))
        {
            strlen =0;
        }
        else
        {
            strlen = payload.length();//length of enter string
        }
        int len = strlen+4+4;//packetlen =4 cksum =4 pyload =?
        String finallen =  String.format("%X", len);
        if(len <16)
        {
            cmdlen = ("000"+finallen);
        }
        else
        {
            cmdlen = ("00"+finallen);
        }
        String Strcmd = cmdlen+siddid+payload;
        short temp = DataConversion.GetCheckSum(Strcmd.getBytes(),Strcmd.length());
        byte[] cksmbytes = DataConversion._HWordToPAN(temp);
        Strcmd = ":"+Strcmd+new String(cksmbytes)+"\r";
        return Strcmd;
    }
    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHexstr(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    public static  void Readfile(Context context)
    {

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open("dtcshrtdesc.csv")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null)
            {
                getShortDescription(mLine);

            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
    }



    //read dtcs with short description
    public static void getShortDescription(String dtcsnumber)
    {


        String line = dtcsnumber;
        String dtcdata[] = line.split(",");
        dtclist.put(dtcdata[0],dtcdata[1]);
    }
    public static String ParseDTCs(String str, String sentcmd)
    {
        str = str.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","");
        int pos = str.indexOf(sentcmd);
        if(pos<0)
        {
            pos = str.indexOf("59027F");
            sentcmd = "59027F";
        }
        String strlen = str.substring(pos-4,pos);
        int len = Integer.parseInt(strlen,16)*2;
        str = str.substring(pos,len+4);
        str = str.replace(sentcmd,"");
        return str;
    }

    public static String parsecmd(String strdata, String cmd)
    {
        String strlen = null;
        String finaldata = null;
        String str = strdata;
        str = str.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","");
        if(str.substring(0,2).equals("7F")&& str.length()<14)
        {
            str = str.substring(6,str.length());
        }
        else
        if(str.substring(0,2).equals("7F")&& str.length()>14)
        {
            int position = str.indexOf("62"+cmd);
            if(position>8)
            {
                strlen = str.substring(position-4,position);
                int len = (Integer.parseInt(strlen,16))*2;
                str = str.substring(position,len+position);
                str = str.replace("62"+cmd,"");
                finaldata = str;
            }
            else
            {
                str = str.substring(position,str.length());

                str = str.replace("62"+cmd,"");
                finaldata = str;
            }

        }
        else
        if(str.length()>14)
        {
            int position = str.indexOf("62"+cmd);
            strlen = str.substring(position-4,position);
            int len = (Integer.parseInt(strlen,16))*2;
            str = str.substring(position,len+position);
            str = str.replace("62"+cmd,"");
            finaldata = str;
        }
        else
        {
            int position = str.indexOf("62"+cmd);
            str = str.substring(position,str.length());
            str = str.replace("62"+cmd,"");
            finaldata = str;
        }


        return finaldata;
    }

    public static String parsecmd2(String strdata, String cmd)
    {
        String strlen = null;
        String finaldata = null;
        String str = strdata;
        str = str.replace(" ","").replace("0:","").replace("1:","").replace("2:","").replace("3:","").replace("4:","").replace("5:","").replace("6:","").replace("7:","").replace("8:","").replace("9:","").replace("A:","").replace("B:","").replace("C:","").replace("D:","").replace("E:","").replace("F:","");

        int position = str.indexOf(cmd);
        strlen = str.substring(position - 4, position);
        int len = (Integer.parseInt(strlen, 16)) * 2;
        str = str.substring(position, len + position);
        str = str.replace(cmd, "");
        finaldata = str;
        return finaldata;
    }

/*


    public void DtcDialog(Context context)
    {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.write_did_dialog);
        Button b1,b2;
        b1 = dialog.findViewById(R.id.button1);
        b2 = dialog.findViewById(R.id.button2);
        try
        {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            b2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void DTCSnapshotRecord(Context context)
    {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dtcdialog);
    }
*/

    //commands used to communicate with VCI
      /*---read reference data*/
    public static String PING = "4100";//SIDDID

    /*---read nonce data*/
    public static String NONCE = "4D00";//SIDDID


    /*---read reference data*/
    public static String READ_REFERENCE_DATA = "2250";//SIDDID

    /*---read proprietary data*/
    public static String READ_PROPRIETARY_DATA = "2251";//SIDDID

    /*---read keylearnt status */
    public static String READ_KEY_LEARN_STATUS = "2252";//SIDDID

    /*---read serial number */
    public static String READ_SERIAL_NUMBER = "2253";//SIDDID

    /*---read aes key data*/
    public static String READ_AES_KEY = "0000";//SIDDID

    /*---read MAC data*/
    public static String READ_MAC_ADDRESS = "6C00";//

    /*---write reference data*/
    public static String WRITE_REFERENCE_DATA = "2E50";//SIDDID

    /*---write proprietary data*/
    public static String WRITE_PROPRIETARY_DATA = "2E51";//SIDDID

    /*---write btname data*/
    public static String WRITE_BT_NAME = "0000";//SIDDID

    /*---write  data*/
    public static String WRITE_SERIAL_NUMBER = "0000";//SIDDID

    /*---write proprietary data*/
    public static String WRITE_ENCRYPTED_DATA = "2E37";//SIDDID

    public static String NegRes(String str)
    {
        String data = null;
        if(str.equals("78"))
        {

        }
        else
        {
            for(int i=0;i<NRC.size();i++)
            {
                if(NRC.get(i).toString().contains(str))
                {
                    String arr[] = NRC.get(i).toString().split(",");
                    data = arr[1]+","+arr[2];
                }
            }
        }
        return  data;
    }


}
