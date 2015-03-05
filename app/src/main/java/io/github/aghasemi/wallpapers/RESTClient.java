package io.github.aghasemi.wallpapers;



import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.net.wifi.p2p.WifiP2pManager.ActionListener;

public class RESTClient {

    private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }



    public static JSONObject HttpGetJSON(String url)
    {

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);

                // A Simple JSONObject Creation
                JSONObject json=new JSONObject(result);

                instream.close();
                return json;

            }
            else
                return null;


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String HttpGetString(String url)
    {

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);



                instream.close();
                return result;

            }
            else
                return null;


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }



    public static String HttpPutString(String url,String data)
    {

        HttpClient httpclient = new DefaultHttpClient();

        // Prepare a request object
        HttpPut httpput = new HttpPut(url);
        // Execute the request
        HttpResponse response;
        try {
            httpput.setEntity(new StringEntity(data));
            response = httpclient.execute(httpput);
            HttpEntity entity = response.getEntity();


            if (entity != null) {

                InputStream instream = entity.getContent();
                String result= convertStreamToString(instream);

                instream.close();
                return result;

            }
            else
                return null;


        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static void HttpIncrementIntAsync(String url)
    {

        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                String url = params[0];
                String currValObj = HttpGetString(url).trim();
                System.out.println(currValObj);
                int currVal;
                try {
                    currVal = Integer.parseInt(currValObj, 10);
                } catch (NumberFormatException error) {
                    System.out.println("No Number");
                    currVal = 0;
                }
                String res= HttpPutString(url, Integer.toString(currVal + 1));
                System.out.println("INCRESULT "+res+" "+url);
                return res;
            }
        }.execute(url);
    }

    public static void HttpDecrementIntAsync(String url)
    {

        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                String url=params[0];
                String currValObj=HttpGetString(url).trim();
                System.out.println(currValObj);
                int currVal;
                try{
                    currVal=Integer.parseInt(currValObj,10);
                }catch (NumberFormatException error)
                {
                    System.out.println("No Number");
                    currVal=0;
                }
                String res= HttpPutString(url, Integer.toString(currVal - 1));
                System.out.println("DECRESULT "+res+" "+url);
                return res;
            }
        }.execute(url);
    }

    public static void HttpIncrementInt(final String url)
    {
        String currValObj=HttpGetString(url).trim();
        System.out.println(currValObj);
        int currVal;
        try{
            currVal=Integer.parseInt(currValObj,10);
        }catch (NumberFormatException error)
        {
            System.out.println("No Number");
            currVal=0;
        }
        HttpPutString(url, Integer.toString(currVal+1));



    }

    public static void HttpDecrementInt(String url)
    {
        String currValObj=HttpGetString(url).trim();
        System.out.println(currValObj);
        int currVal;
        try{
            currVal=Integer.parseInt(currValObj,10);
        }catch (NumberFormatException error)
        {
            System.out.println("No Number");
            currVal=0;
        }
        HttpPutString(url, Integer.toString(currVal-1));
    }

    public static void main(String[] args) throws JSONException {
        JSONObject js = HttpGetJSON("http://en.wikipedia.org/w/api.php?action=parse&page=Shiraz&format=json&prop=text&section=0");
        String resp = HttpPutString("https://flickering-inferno-8909.firebaseio.com/dummy.json", "12");
        System.out.println(js);
        System.out.println(resp);
        HttpIncrementInt("https://flickering-inferno-8909.firebaseio.com/dummy.json");
    }

}