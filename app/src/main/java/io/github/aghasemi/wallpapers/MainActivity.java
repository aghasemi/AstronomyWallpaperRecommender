package io.github.aghasemi.wallpapers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class MainActivity extends Activity {
	Timer timer=new Timer(true);
    Bitmap currentBitmap;
    String urlOfCurrentPage;
    String urlOfCurrentImage;
    boolean currentImageHasBeenSelectedAsWallpaper=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ((ImageView) findViewById(R.id.imView)).setImageResource(R.drawable.default_pic);
        todayWallpaperSet(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			System.out.println("Here");
			Log.v("TURNOFF","HERE");
			timer.cancel();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	public void buttonClicked(View view) throws IOException {
		//Log.v("Hey!","HERE");
        if (!currentImageHasBeenSelectedAsWallpaper)
        {
            RESTClient.HttpDecrementIntAsync("https://flickering-inferno-8909.firebaseio.com/stats/wpchanger/images/"+urlOfCurrentImage.replace("/","_").replace(".","")+".json");
            currentImageHasBeenSelectedAsWallpaper=false;

        }

        randomWallpaperFind();
		
		

		
		
	}

    public void automateWallpaperChange(View view)
    {
        Log.v("Tick","Wallpaper automation");
        final CheckBox checkBox = (CheckBox) findViewById(R.id.changeDaily);
        if (checkBox.isChecked()) {
            //checkBox.setChecked(false);
            timer=new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    todayWallpaperSet(true);
                }
            }, 0, 3600000);
        }
        else
        {
            timer.cancel();
        }
    }

    public void changeWallpaper(View view)
    {
        Log.v("Click","Wallpaper changed");
        currentImageHasBeenSelectedAsWallpaper=true;
        try{
            if (currentBitmap!=null)
            {
                WallpaperManager.getInstance(getBaseContext()).setBitmap(currentBitmap);
                //The action associated with the notification
                Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlOfCurrentPage));

                PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                showNotification( "Wallpaper Changed", "Your wallpaper was changed to the image in the following link:\n"+ urlOfCurrentPage, R.mipmap.ic_launcher,currentBitmap,contentIntent);
                RESTClient.HttpIncrementIntAsync("https://flickering-inferno-8909.firebaseio.com/stats/wpchanger/images/"+urlOfCurrentImage.replace("/","_").replace(".","")+".json");

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

	public void randomWallpaperFind()
	{
		Date today=new Date();
		long rangebegin = Timestamp.valueOf("20"+14+"-01-01 00:00:00").getTime();
		long rangeend = today.getTime();
		long diff = rangeend - rangebegin + 1;
		Timestamp rand = new Timestamp(rangebegin + (long)(Math.random() * diff));
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(rand.getTime());
		
		int year=cal.get(Calendar.YEAR)-2000;
		
		int month=cal.get(Calendar.MONTH)+1;
		int day=cal.get(Calendar.DAY_OF_MONTH);
		
		
		
		
		
		String monthStr=month<10 ? "0"+month:Integer.toString(month);
		String dayStr=day<10 ? "0"+day:Integer.toString(day);
		String adr="http://apod.nasa.gov/apod/ap"+year+monthStr+dayStr+".html";//"http://apod.nasa.gov/apod/image/1411/TulipNebulaMetsavainio800.jpg";
		System.out.println("--"+adr);

		downloadWallpaperFromImage(adr, false);
        //urlOfCurrentImage=adr;
	}
	
	public void todayWallpaperSet(boolean set)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(new Date().getTime());
		
		int year=cal.get(Calendar.YEAR)-2000;
		
		int month=cal.get(Calendar.MONTH)+1;
		int day=cal.get(Calendar.DAY_OF_MONTH);
		
		
		
		String monthStr=month<10 ? "0"+month:Integer.toString(month);
		String dayStr=day<10 ? "0"+day:Integer.toString(day);
		String adr="http://apod.nasa.gov/apod/ap"+year+monthStr+dayStr+".html";//"http://apod.nasa.gov/apod/image/1411/TulipNebulaMetsavainio800.jpg";
		System.out.println("+*"+adr);
		downloadWallpaperFromImage(adr, set);
        //urlOfCurrentImage=adr;
	}
	ProgressDialog pd;
	public void downloadWallpaperFromImage(final String adr, final boolean set)
	{
		final ImageView img= (ImageView) findViewById(R.id.imView);
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final Button findRandomButton=(Button)findViewById(R.id.button1);
        final Button setAsWpButton=(Button)findViewById(R.id.setAsWP);



        if (!mWifi.isConnected())
		{
			Log.v("3G", "NOT ON WIFI");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Not on a WiFi Connection", Toast.LENGTH_LONG).show();

                }
            });
            if (set)
                return;
		}
		
		runOnUiThread(new Runnable() {
			
			

			@Override
			public void run() {
				pd=new ProgressDialog(MainActivity.this);
				pd.setMessage("Loading the wallpaper");
				pd.setTitle("");
                findRandomButton.setEnabled(false);
                setAsWpButton.setEnabled(false);
		    	pd.show();
			}
		});
		
		AsyncTask<Object, Integer, Integer> asyncLoad=new ImageDownloader();
		
		asyncLoad.execute(adr,new ImageReceivedListener() {
			
			@Override
			public void imageReceived(final Bitmap bmp,final String imurl) {
				 try{
					Log.v("200","OK");
                     currentBitmap=bmp;

                     if (bmp!=null)
                     {
                         urlOfCurrentPage =adr;
                         if (set)
                         {
                             WallpaperManager.getInstance(getBaseContext()).setBitmap(currentBitmap);
                             //The action associated with the notification
                             Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(adr));

                             PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

                             showNotification( "Wallpaper Changed", "Your wallpaper was changed to the image in the following link:\n"+adr, R.mipmap.ic_launcher,bmp,contentIntent);
                             Context context = getApplicationContext();
                             CharSequence text = "Your wallpaper was successfully changed.";
                             int duration = Toast.LENGTH_SHORT;

                             Toast toast;
                             toast = Toast.makeText(context, text, duration);
                             toast.show();

                         }

                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {

                                 img.setImageBitmap(bmp);
                                 img.setScaleType(ScaleType.FIT_XY);
                                 pd.dismiss();
                             }
                         });
                         urlOfCurrentImage=imurl;
                     }
                     else
                     {
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {

                                 pd.dismiss();
                                 randomWallpaperFind();
                             }
                         });
                     }

                     runOnUiThread(new Runnable() {
                         @Override
                         public void run() {

                         }
                     });
					
				}catch(IOException e)
                 {
                     e.printStackTrace();

                 }


				
			}
		});
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                pd.dismiss();
                findRandomButton.setEnabled(true);
                setAsWpButton.setEnabled(true);
            }
        });
	}
	public  void showNotification ( String title, String text, int icon, Bitmap bmp,PendingIntent pIntent )
    {
        Context ctx=getApplicationContext();

        // Build notification
        Notification notification = new Notification.Builder( ctx)
                .setContentTitle( title )
                .setContentText( text )
                .setSmallIcon(icon)
                .setLargeIcon(bmp)
                .setAutoCancel( true )
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setContentIntent(pIntent)
                
                .build();
        
        

        NotificationManager notificationManager =
                ( NotificationManager ) ctx.getSystemService( ctx.NOTIFICATION_SERVICE );
        notificationManager.notify( 0, notification );
    }
}
