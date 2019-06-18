package HighlightMaker;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.*;

public class TwitchChatFetcher {
	private ArrayList<Integer> arrayListForTime;//record time
	private ArrayList<String> arrayListForComments;//record comments
	private String vodId;//user input
	private String formatVodURL;//use to fetch data
	private int commentCount;
	
	//constructor
	public TwitchChatFetcher(String id) {
		vodId= id;
		formatVodURL="https://api.twitch.tv/kraken/videos/"+id+"/comments/?cursor";
		arrayListForTime=new ArrayList<Integer>();
		arrayListForComments=new ArrayList<String>();
	}
	
	//connect and fetch JSON stirng
    public static String getJSON(String url, int timeout) throws IOException {
        URL u = new URL(url);
        HttpURLConnection c = (HttpURLConnection) u.openConnection();
        c.setRequestMethod("GET");
        c.setUseCaches(false);
        c.setAllowUserInteraction(false);
        c.setConnectTimeout(timeout);   
        c.setReadTimeout(timeout);     
        c.setRequestProperty("Accept","application/vnd.twitchtv.v5+json");
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
	
    
	public void chatDataFetch()throws IOException {
		int content_offset_seconds;//aka time
    	JSONObject body;//aka comments
        
    	//first connection
    	String s = getJSON(formatVodURL,9000);//get data as JSON string
        JSONObject jsonObj = new JSONObject(s);//convert to JSON Object for parse time and comments
    	
    	//fetch first block of comments
        JSONArray arrComments = jsonObj.getJSONArray("comments");
        for (int i = 0; i < arrComments.length(); i++) {
        	content_offset_seconds=(int)((double)(arrComments.getJSONObject(i).getDouble("content_offset_seconds")));
            arrayListForTime.add(content_offset_seconds);
        }
        for (int i = 0; i < arrComments.length(); i++) {
            body=arrComments.getJSONObject(i).getJSONObject("message");
            arrayListForComments.add(body.getString("body"));
        }      	       
        String nextPart; //used to fetch next block of comments 
        
        //fetch comments to the end
    	while(jsonObj.has("_next")) {//while the block have the next part
    		nextPart=jsonObj.get("_next").toString();
    		String newUrl=formatVodURL+"="+nextPart;//the url for the next part
    		s = getJSON(newUrl,9000);
    		jsonObj=new JSONObject(s);
    		//fetch
    		arrComments = jsonObj.getJSONArray("comments");
	        for (int i = 0; i < arrComments.length(); i++) {
	        	content_offset_seconds=(int)((double)(arrComments.getJSONObject(i).getDouble("content_offset_seconds")));
	            arrayListForTime.add(content_offset_seconds);
	        }
	        for (int i = 0; i < arrComments.length(); i++) {
	        	body=arrComments.getJSONObject(i).getJSONObject("message");
	            arrayListForComments.add(body.getString("body"));
	        }
	        //todo progressgbar
    	}
    	//count comments
    	commentCount=arrayListForComments.size();

	}
	//print data
	public void printData() {
        System.out.println("Comments count:"+commentCount);      
        System.out.println("Time    Comments");
        for(int i=0;i<arrayListForComments.size();i++) {
        	System.out.printf("%-7d %s%n",arrayListForTime.get(i),arrayListForComments.get(i));
        }
	}
	
	public ArrayList<Integer> getArrayListForTime()
	{
		return arrayListForTime;
	}
	
	public ArrayList<String> getArrayListForComments()
	{
		return arrayListForComments;
	}
}

