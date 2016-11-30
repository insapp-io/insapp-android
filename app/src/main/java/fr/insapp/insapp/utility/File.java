package fr.insapp.insapp.utility;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by Antoine on 21/09/2016.
 */
public class File {

    private static final String FILENAME = "auth.dat";

    public static void writeSettings(Context context, String data){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            fOut = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fOut.write(data.getBytes());

            /*osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();*/
        }
        catch (Exception e) {
        }
        finally {
            try {
                //osw.close();
                fOut.close();
            } catch (IOException e) {
            }
        }
    }

    public static String readSettings(Context context){
        /*fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[255];
        String data = null;

        try{

            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
*/
        int value;
        StringBuffer lu = new StringBuffer();
        try {
            FileInputStream fIn = context.openFileInput(FILENAME);
            while((value = fIn.read()) != -1) {
                lu.append((char)value);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
            /*finally {
               try {
                      isr.close();
                      fIn.close();
                      } catch (IOException e) {
                        Toast.makeText(context, "Settings not read",Toast.LENGTH_SHORT).show();
                      }
            } */
        return lu.toString();
    }
}
