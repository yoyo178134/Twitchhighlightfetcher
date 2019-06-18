package HighlightMaker;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;

public class FFMpegUtils {
    /**
     * @param videoInputPath 
     * @param videoOutPath   
     * @throws Exception
     */
	
	public static String combine(List<String> sourcesPath, String FileName) throws IOException{
		System.out.println("Enter combine");
		String finish = "Combine complete!!!";
		
		Scanner scanner = new Scanner(System.in);
		int length = sourcesPath.size();
		
		System.out.printf("FileName: %s%n", FileName);
		System.out.printf("sourcesPath_Size: %d%n", length);
		
		for(String i: sourcesPath) {
			System.out.println("sourcePath: " + i);
		}
		
		List<String> command = new ArrayList<String>();
		command.add("ffmpeg");
		for(int listIdx=0; listIdx<length; listIdx++){
			command.add("-i");
			command.add(sourcesPath.get(listIdx));
		}
		command.add("-filter_complex");
		command.add("concat=n=" + length + ":v=1:a=1");
		command.add("-y");		
		command.add("output/" + FileName + ".mp4");
		
		ProcessBuilder builder = new ProcessBuilder(command);
        final Process p = builder.start();
       
        Thread combineThread = new Thread(new Runnable() {
            public void run() {

              Scanner sc = new Scanner(p.getErrorStream());

              // Find duration
              Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
              
              String dur;
              
      /*        if (dur == null)
                throw new RuntimeException("Could not parse duration.");*/
              double totalSecs = 0;
              while(null != (dur = sc.findWithinHorizon(durPattern, 0))) {
            	  String[] hms = dur.split(":");
            	  totalSecs += Integer.parseInt(hms[0]) * 3600
                               + Integer.parseInt(hms[1]) *   60
                               + Double.parseDouble(hms[2]);
              }
              System.out.println("Total duration: " + totalSecs + " seconds.");

              // Find time as long as possible.
              Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
              String match;
              while (null != (match = sc.findWithinHorizon(timePattern, 0))) {
            	  System.out.printf("match: %s%n", match);
        		  String[] now = match.split(":");
        		  double nowsec = Integer.parseInt(now[0]) * 3600
                                 + Integer.parseInt(now[1]) *   60
                                 + Double.parseDouble(now[2]);
                  double progress = nowsec / totalSecs;
                  System.out.printf("Progress: %.2f%%%n", progress * 100);
                  }
            }
          });
          
          combineThread.start();
          
          try {
        	  combineThread.join();
        	  
          } catch(InterruptedException e) {
        	  e.printStackTrace();
          }
          
          return finish + System.lineSeparator();
        }

	public static void download(String URL, String start, String end, String fileName) throws IOException{		
		System.out.printf("start:%s, end:%s%n", start, end);
		
		String[] startTime =start.split(":");
		String[] endTime = end.split(":");
		
		List<String> command = new ArrayList<String>();
		command.add("ffmpeg");
		command.add("-protocol_whitelist");
		command.add("\"file,http,https,tcp,tls\"");
		command.add("-i");
		command.add("\"" + URL + "\"");
		command.add("-y");
		command.add("-ss");
		command.add(start);
		command.add("-to");
		command.add(end);
		command.add("-c");
		command.add("copy");
		//command.add("\"" + fileName + startTime[0] + startTime[1] + startTime[2] + "TO"+ endTime[0] + endTime[1] + endTime[2] + ".mp4\"");
		command.add("\"output/" + fileName + startTime[0] + startTime[1] + startTime[2] + "TO"+ endTime[0] + endTime[1] + endTime[2] + ".mp4\"");
		
		/*
		for(String i : command) {
			System.out.print(i + " ");
		}
		System.out.println("");
		
		System.out.printf("URL: \"%s\"%n", URL);
		System.out.printf("output path: \"output/" + fileName + startTime[0] + startTime[1] + startTime[2] + "TO"+ endTime[0] + endTime[1] + endTime[2] + ".mp4\"");
		*/
		ProcessBuilder builder = new ProcessBuilder(command);
        Process process = null;
        try {
            process = builder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null) {
        }
        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }
     }
}

