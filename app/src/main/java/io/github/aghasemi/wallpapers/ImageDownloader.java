package io.github.aghasemi.wallpapers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.GpsStatus.Listener;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

public class ImageDownloader extends AsyncTask<Object,Integer,Integer>{
	
	protected Integer doInBackground(Object... params) {
        ImageReceivedListener ir=(ImageReceivedListener) params[1];
        try {
			
			String web=params[0].toString();
			Document doc=Jsoup.parse(new URL(web), 3000);

			Elements elements = doc.select("img[src]");
			String imurl = null;//"http://apod.nasa.gov/apod/";

            for( Element element : elements )
			{
			    // Do something with your links here ...
			    System.out.println(element.attr("src"));
			    imurl=element.attr("src");
			}
            if (imurl==null)
			{
				Log.v("BADPAGE", "NO IMGSRC FOUND");
                ir.imageReceived(null,null);

                return 0;
			}
			URL url = new URL("http://apod.nasa.gov/apod/"+imurl);
			
			Bitmap bmp = BitmapFactory.decodeStream(url.openConnection()
			        .getInputStream());
			

			ir.imageReceived(bmp,imurl);

            RESTClient.HttpIncrementIntAsync("https://flickering-inferno-8909.firebaseio.com/stats/wpchanger/visits.json");

            System.out.println("Done Downloading");
			
			//context.setWallpaper(bmp);
			
			
			
			
			
			return 1;
		} catch (IOException e) {
			Log.v("OOPS", e.getMessage());
			System.out.println(params[0]);
            ir.imageReceived(null,null);
            return 0;
		}
		
    }

    protected void onProgressUpdate(Integer... progress) {
        
    }

    protected void onPostExecute(Integer result) {
        System.out.println("PostExecute");
        
    }
    public ImageDownloader() {
    	
    	
	}
    
}
