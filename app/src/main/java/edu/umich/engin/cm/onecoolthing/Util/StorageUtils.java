package edu.umich.engin.cm.onecoolthing.Util;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by jawad on 29/09/15.
 */
public class StorageUtils {
    static public File getCacheFolder(Context context) {
        File cacheDir = null;

        // If there's an SD card, try to get the cache folder on that
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheDir = new File(Environment.getExternalStorageDirectory(), "cachefolder");

            // If there is no cache folder on the sd card yet, make one
            if(!cacheDir.isDirectory()) {
                cacheDir.mkdirs();
            }
        }

        // If couldn't get any external cache folders, then use the internal one
        if(cacheDir == null || !cacheDir.isDirectory()) {
            cacheDir = context.getCacheDir(); //get system cache folder
        }

        return cacheDir;
    }

    static public File getAppDataFolder(Context context) {
        File dataDir = null;

        // Try to create get the app data location on the external
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dataDir = new File(Environment.getExternalStorageDirectory(), "myappdata");

            // If the folder doesn't exist, create it
            if(!dataDir.isDirectory()) {
                dataDir.mkdirs();
            }
        }

        // If couldn't get any external app data folders, then use the internal one
        if(dataDir == null || !dataDir.isDirectory()) {
            dataDir = context.getFilesDir();
        }

        return dataDir;
    }

    static public void storeFileFromWeb(File directory, String newFileName, String contentUrl)
    throws Exception {
        // Get the content stream
        URL content = new URL(contentUrl);
        URLConnection connection = content.openConnection();
        InputStream inputStream = new BufferedInputStream(connection.getInputStream(), 10240); // 10k size, just in case

        // Create the file and stream to write to the file
        File newFile = new File(directory, newFileName);
        FileOutputStream outputStream = new FileOutputStream(newFile);

        byte buffer[] = new byte[1024];
        int dataSize;
//        int loadedSize = 0;
        while ((dataSize = inputStream.read(buffer)) != -1) {
/*          // Used to update progress, not using this feature currently
            loadedSize += dataSize;
            publishProgress(loadedSize);*/
            outputStream.write(buffer, 0, dataSize);
        }

        outputStream.close();

    }

    static public void storeFileFromString(File directory, String newFileName, String content)
    throws Exception {
        // Create the file and stream then simply write to it
        File newFile = new File(directory, newFileName);
        FileOutputStream outputStream = new FileOutputStream(newFile);
        outputStream.write(content.getBytes());
    }

    static public String getStringFromFile(File directory, String fileName) throws IOException {
        // Get the inputstream of the file
        File targetFile = new File(directory, fileName);
        InputStream inputStream = new FileInputStream(targetFile);

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ( (receiveString = bufferedReader.readLine()) != null ) {
            stringBuilder.append(receiveString);
        }

        // Finally close the input stream and return the results
        inputStream.close();
        return stringBuilder.toString();
    }

    static public boolean deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDirectory(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    static public boolean checkIfFileExists(File directory, String fileName) {
        File file = new File(directory, fileName);
        return file.exists();
    }

}
