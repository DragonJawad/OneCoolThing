package edu.umich.engin.cm.onecoolthing.NetworkUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.umich.engin.cm.onecoolthing.R;
import edu.umich.engin.cm.onecoolthing.Util.FileCache;
import edu.umich.engin.cm.onecoolthing.Util.MemoryCache;
import edu.umich.engin.cm.onecoolthing.Util.Utils;

/**
 * Created by jawad on 19/10/14.
 *
 * Used to download image from url and resize downloaded image and make file cache
 *      on sdcard. Lazy load images for listview rows.
 */

public class ImageLoader {
    FileCache fileCache;

    // Initialize MemoryCache
    MemoryCache memoryCache = new MemoryCache();

    // Create Map (collection) to store image and image url in key value pair
    private Map<ImageView, String> imageViews = Collections.synchronizedMap(
            new WeakHashMap<ImageView, String>());
    ExecutorService executorService;

    // Handler to display images in UI thread
    Handler handler = new Handler();

    // If not -1, then determines what scale to use the images at
    int givenScale = -1;

    // Manager to be notified once data is loaded
    LoaderManager manager;

    // Interface so that user can be notified once data has been finally applied
    public interface LoaderManager {
        // Notifies the fragment's manager that the fragment is done being set
        public void notifyDataLoaded();
    };

    public ImageLoader(Context context){

        fileCache = new FileCache(context);

        // Creates a thread pool that reuses a fixed number of
        // threads operating off a shared unbounded queue.
        executorService = Executors.newFixedThreadPool(5);
    }

    // Constructor that sets a manager to notify once data is loaded
    public ImageLoader(Context context, LoaderManager manager){
        this(context);

        // Set the manager to notify once data is applied
        this.manager = manager;
    }

    // default image show in list (Before online image download)
    final int stub_id = R.drawable.ic_launcher;

    // Default, primary method
    public void DisplayImage(String url, ImageView imageView)
    {
        //Store image and url in Map
        imageViews.put(imageView, url);

        // Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(url);

        if(bitmap!=null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);
        }
        else
        {
            //queue Photo to download from url
            queuePhoto(url, imageView);

            //Before downloading image show default image
            imageView.setImageResource(stub_id);
        }
    }

    // Call DisplayImage but specify a scale to use
    public void DisplayImage(String url, ImageView imageview, int scale) {
        givenScale = scale;
        DisplayImage(url, imageview);
    }

    // Display an image with a specific scale, and assign a TextView's text at the same time
    public void DisplayImageAndTextAndSpinner(String url, ImageView imageView, int scale,
                                    TextView textView, String text, ProgressBar spinner) {
        // Set the givenScale
        givenScale = scale;

        // TODO: Rewrite below so not copy+pasting code
        //Store image and url in Map
        imageViews.put(imageView, url);

        // Check image is stored in MemoryCache Map or not (see MemoryCache.java)
        Bitmap bitmap = memoryCache.get(url);

        if(bitmap!=null) {
            // if image is stored in MemoryCache Map then
            // Show image in listview row
            imageView.setImageBitmap(bitmap);

            // Display text now as well
            textView.setText(text);
        }
        else
        {
            //queue Photo to download from url AND let it set the textView's text
            queuePhotoAndTextAndSpinner(url, imageView, textView, text, spinner);
        }
    }

    private void queuePhoto(String url, ImageView imageView)
    {
        // Store image and url in PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView);

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution

        executorService.submit(new PhotosLoader(p));
    }

    // Queue a new photo to load, as well as text to load at the same time
    private void queuePhotoAndTextAndSpinner(String url, ImageView imageView,
                                   TextView textView, String text, ProgressBar spinner) {
        // Store the data in a PhotoToLoad object
        PhotoToLoad p = new PhotoToLoad(url, imageView, textView, text, spinner);

        // pass PhotoToLoad object to PhotosLoader runnable class
        // and submit PhotosLoader runnable to executers to run runnable
        // Submits a PhotosLoader runnable task for execution

        executorService.submit(new PhotosLoader(p));
    }

    //Task for the queue
    private class PhotoToLoad
    {
        // Simple data
        public String url;
        public ImageView imageView;

        // Additional text to load & spinner to manage
        public boolean loadText = false;
        public TextView textView;
        public String text;
        public ProgressBar spinner;

        // Simple constructor
        public PhotoToLoad(String u, ImageView i){
            url=u;
            imageView=i;
        }

        // Constructor, in order to load text
        public PhotoToLoad(String u, ImageView i, TextView textView,
                           String text, ProgressBar spinner){
            url=u;
            imageView=i;

            loadText = true;
            this.textView = textView;
            this.text = text;
            this.spinner = spinner;
        }
    }

    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;

        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }

        @Override
        public void run() {
            try{
                //Check if image already downloaded
                if(imageViewReused(photoToLoad))
                    return;
                // download image from web url
                Bitmap bmp = getBitmap(photoToLoad.url);

                // set image data in Memory Cache
                memoryCache.put(photoToLoad.url, bmp);

                if(imageViewReused(photoToLoad))
                    return;

                // Get bitmap to display
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);

                // Causes the Runnable bd (BitmapDisplayer) to be added to the message queue.
                // The runnable will be run on the thread to which this handler is attached.
                // BitmapDisplayer run method will call
                handler.post(bd);

            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }

    private Bitmap getBitmap(String url)
    {
        File f=fileCache.getFile(url);

        //from SD cache
        //CHECK : if trying to decode file which not exist in cache return null
        Bitmap b = decodeFile(f);
        if(b!=null)
            return b;

        // Download image file from web
        try {
            // TODO: Rewrite to use ServiceHandler
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();

            // Constructs a new FileOutputStream that writes to file
            // if file not exist then it will create file
            OutputStream os = new FileOutputStream(f);

            // See Utils class CopyStream method
            // It will each pixel from input stream and
            // write pixels to output stream (file)
            Utils.CopyStream(is, os);

            os.close();
            conn.disconnect();

            //Now file created and going to resize file with defined height
            // Decodes image and scales it to reduce memory consumption
            bitmap = decodeFile(f);

            return bitmap;

        } catch (Throwable ex){
            ex.printStackTrace();
            if(ex instanceof OutOfMemoryError)
                memoryCache.clear();
            return null;
        }
    }

    //Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){

        try {

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();

            // TODO: Make this resizing aspect optional
            //////////////////////////////////////
            // Set width/height of recreated image
            int scale=1;
            // If no specific scale specified, use default
            if(givenScale == -1) {
                //Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = 85;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }
            }
            // Otherwise, use the specified scale
            else {
                scale = givenScale;
            }

            //decode with current scale values
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;

        } catch (FileNotFoundException e) {
            Log.e("MD/ImageLoader", e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.e("MD/ImageLoader", e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    boolean imageViewReused(PhotoToLoad photoToLoad){

        String tag=imageViews.get(photoToLoad.imageView);

        //Check url is already exist in imageViews MAP
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }

    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;

        // Simple constructor
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){
            bitmap=b;
            photoToLoad=p;
        }

        public void run()
        {
            // Show image if not reusing it
            if(!imageViewReused(photoToLoad)) {
                // Show bitmap on UI
                if (bitmap != null)
                    photoToLoad.imageView.setImageBitmap(bitmap);
                else
                    photoToLoad.imageView.setImageResource(stub_id);
            }

            // Display text and turn off spinner, if necessary
            if(photoToLoad.loadText) {
                photoToLoad.textView.setText(photoToLoad.text);

                // Stop the spinner
                photoToLoad.spinner.setVisibility(View.GONE);
            }

            // Finally, notify the manager that data has been loaded
            if(manager != null) {
                manager.notifyDataLoaded();
            }
        }
    }

    public void clearCache() {
        //Clear cache directory downloaded images and stored data in maps
        memoryCache.clear();
        fileCache.clear();
    }

}
