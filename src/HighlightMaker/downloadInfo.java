package HighlightMaker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.*;
import java.util.*;

public class downloadInfo {
	private  String token;
	private  String sig;
	private  String m3u8;
	private  String formatM3u8;



	private ArrayList<String> resolutionURL = new ArrayList<String>();


	public downloadInfo(String id) throws IOException{
		//get token and sig
		String urlString="https://api.twitch.tv/api/vods/"+id+"/access_token";
		String jsonString=getJSON(urlString,9000);
		JSONObject obj=new JSONObject(jsonString);
		token=obj.get("token").toString();
		sig=obj.get("sig").toString();
		//m3u8 url
		String urlForm3u8="https://usher.ttvnw.net/vod/"+id+".m3u8?player=twitchweb\\&nauthsig="+sig+"&nauth="+token+"&allow_audio_only=true&allow_source=true\\&type=any&p=123" ;
		m3u8=getJSON(urlForm3u8,9000);
		//format m3u8
		String s=m3u8;
		String guess="#";
		int temp[]=new int[100];
		int n=0;
		for (int index = s.indexOf(guess);index >= 0; index = s.indexOf(guess, index + 1))
			temp[n++]= index;
		String tempString="";
		for(int i=0;i<n;i++) {
			if(i!=n-1)
				tempString=tempString+s.substring(temp[i],temp[i+1])+"\n";
			else {
				tempString=tempString+s.substring(temp[i],s.length());
			}
		}
		formatM3u8=tempString;

		ArrayList<String> a = new ArrayList<String>();
		a=extractUrls(getformatM3u8());
		String [] b = new String [10];

		for(int i=0;i<a.size();i++){
			b = a.get(i) .split("/");
			//System.out.println("print b" + b[4] + a.get(i));
			if(b[4].equals("audio_only") ) 
				resolutionURL.add(a.get(i));
			else if (b[4].equals("chunked")) 
				resolutionURL.add(a.get(i));
			else if (b[4].equals("480p30"))
				resolutionURL.add(a.get(i));
			else if (b[4].equals("160p30")) 
				resolutionURL.add(a.get(i));
		}
		
		for(String i:resolutionURL) {
			System.out.println(i);
		}
		

	}
	public String getResolutionURL(int i) {
		try {
			//System.out.printf("resolutionURL: , %s%n", resolutionURL.get(i));
			return resolutionURL.get(i);
		} catch(NullPointerException e){
			//System.out.println("size�d�� " + e);
			return resolutionURL.get(1);
		}
	}

	//connection
	public static String getJSON(String url, int timeout) throws IOException {
		URL u = new URL(url);
		HttpURLConnection c = (HttpURLConnection) u.openConnection();
		c.setRequestMethod("GET");
		c.setUseCaches(false);
		c.setAllowUserInteraction(false);
		c.setConnectTimeout(timeout);
		c.setReadTimeout(timeout);
		//c.setRequestProperty("Accept","application/vnd.twitchtv.v5+json");
		c.setRequestProperty("Client-ID","ildytfqanhzvdaprp96m5rkylap16k");
		c.connect();
		int status = c.getResponseCode();

		switch (status) {
			case 200:
			case 201:
				BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream(),"utf-8"));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				return sb.toString();

		}
		return null;
	}

	//get m3u8 string
	public String getm3u8() {
		return m3u8;
	}
	//get the m3u8 have \n
	public String getformatM3u8() {
		return formatM3u8;
	}

	public static ArrayList<String> extractUrls(String text){
		ArrayList<String> containedUrls = new ArrayList<String>();
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(text);

		while (urlMatcher.find())
		{
			containedUrls.add(text.substring(urlMatcher.start(0),
					urlMatcher.end(0)));
		}

		return containedUrls;
	}
}
