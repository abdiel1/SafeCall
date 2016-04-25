/*
 *  Copyright (c) 2011 by Twilio, Inc., all rights reserved.
 *
 *  Use of this software is subject to the terms and conditions of 
 *  the Twilio Terms of Service located at http://www.twilio.com/legal/tos
 */

package twilio;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpHelper
{
	private static final String TAG = "HttpHelper";

	private static String stringFromInputStream(InputStream is) throws IOException
    {
        char[] buf = new char[1024];
        StringBuilder out = new StringBuilder();

        Reader in = new InputStreamReader(is, "UTF-8");

        int bin;
        while ((bin = in.read(buf, 0, buf.length)) >= 0) {
            out.append(buf, 0, bin);
        }

        return out.toString();
    }

	public static String httpGet(String url) throws Exception {
		
		URL urlObj = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
		
		conn.setConnectTimeout(45000);
		conn.setReadTimeout(30000);	
		conn.setDoInput(true);

		int responseCode = conn.getResponseCode();

		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = conn.getInputStream();
			String capabilityToken = stringFromInputStream(is);
			is.close();
			conn.disconnect();
			return capabilityToken; 
		} else {
			conn.disconnect();
			throw new Exception("Got error code " + responseCode
					+ " from server");
		}
	}

	public static String httpPost(String url, HashMap<String, String> params) {
		String response = "";
		try{
			URL urlObj = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();

			conn.setConnectTimeout(45000);
			conn.setReadTimeout(30000);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			//For the post
			OutputStream out = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

			writer.write(getPostDataString(params));

			writer.flush();
			writer.close();
			out.close();

			int responseCode = conn.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				String line;
				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				while((line=reader.readLine())!= null) {
					response += line;
				}
			} else {
				response = "";
				throw new Exception("Got error code " + responseCode
						+ " from server");
			}
		}
		catch(Exception e)
		{
			Log.e(TAG, e.toString());
		}
		return response;
	}

	private static String getPostDataString(HashMap<String, String> params) {
		StringBuilder results = new StringBuilder();
		try{
			boolean first = true;
			for (Map.Entry<String, String> entry: params.entrySet()){
				if (first){
					first = false;
				}
				else{
					results.append("&");
				}
				results.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				results.append("=");
				results.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}
		}
		catch(UnsupportedEncodingException e){
			Log.e(TAG, e.toString());
		}
		return results.toString();
	}
}
