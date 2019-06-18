package HighlightMaker;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.lang.*;

public class ChatToTime {

	private static Scanner input;
	private static ArrayList<HighLightTime> highLight;
	private static ArrayList<Time> timeArray;

	public static ArrayList<Time> searchByString(ArrayList<Integer> arrayListForTime,ArrayList<String> arrayListForComments, String myString, int timerange,int minTimes) {
		Time compare = new Time(0,0,0,0);
		ArrayList<Time> times = new ArrayList<Time>();
		ArrayList<Time> myTimes = new ArrayList<Time>();
		times.add(compare);
		for(int i = 0;i < arrayListForComments.size();i++)
		{
			
			if(arrayListForComments.get(i).indexOf(myString) > -1)
			{
				addTime(arrayListForTime,times,compare,timerange,i);
				//System.out.printf("numbers = %d%n", times.get(times.size()-1).getNum());
			}
			
		}
		for(int i = 0; i < times.size(); i++)
		{
			System.out.printf("%d times %n", times.get(i).getNum());
			if(times.get(i).getNum()>minTimes)
				myTimes.add(times.get(i));
		}
		return myTimes;
	}
	
	public static ArrayList<Time> searchByFrequency(ArrayList<Integer> arrayListForTime,ArrayList<String> arrayListForComments, int timerange, int minTimes){
		Time compare = new Time(0,0,0,0);
		ArrayList<Time> times = new ArrayList<Time>();
		ArrayList<Time> myTimes = new ArrayList<Time>();
		times.add(compare);
		
		for(int i = 0;i < arrayListForTime.size();i++)
		{
			addTime(arrayListForTime,times,compare,timerange,i);
		}
		
		for(int i = 0; i < times.size(); i++)
		{
			if(times.get(i).getNum()>minTimes)
				myTimes.add(times.get(i));
		}
		return myTimes;
	}
	
	public static ArrayList<Time> searchBySD(ArrayList<Integer> arrayListForTime,ArrayList<String> arrayListForComments)
	{
		Time compare = new Time(0,0,0,0);
		int mesnum = 0;
		ArrayList<Time> times = new ArrayList<Time>();
		ArrayList<Time> myTimes = new ArrayList<Time>();
		ArrayList<Integer> mesnumbers = new ArrayList<Integer>();
		times.add(compare);
		
		for(int i = 0;i < arrayListForTime.size();i++)
		{
			addTime(arrayListForTime,times,compare,15,i);
			mesnum++;
		}
		int alltime, avg, sum = 0, SD;
		Double x;
		alltime = times.get(times.size()-1).getHour() * 3600
				+ times.get(times.size()-1).getMin() * 60
				+ times.get(times.size()-1).getSecond();
		alltime = alltime / 15;
		avg = mesnum / alltime;
		for(int i = 0;i < mesnumbers.size();i++)
			sum = sum + (mesnumbers.get(i) - avg) * (mesnumbers.get(i) - avg);
		sum = sum / alltime * 15;
		x = Math.sqrt(sum);
		SD = x.intValue();
		for(int i = 0; i < times.size(); i++)
		{
			if(times.get(i).getNum() > avg + SD)
				myTimes.add(times.get(i));
		}
		return myTimes;
	}
	
	public static void addTime(ArrayList<Integer> arrayListForTime, ArrayList<Time> times, Time compare, int timerange, int i)
	{
		int seconds , hour, min ,second;
		seconds = arrayListForTime.get(i);
		second = seconds % 60;
		seconds /= 60;
		min = seconds % 60;
		seconds /= 60;
		hour = seconds;
		if(compare.getSecond() == (second / timerange) * timerange)
		{
			times.get(times.size()-1).numAddOne();
		}
		else
		{
			Time tmp = new Time( hour, min, (second / timerange + 1) * timerange);
			times.add(tmp);
			compare = new Time(hour, min, (second / timerange) * timerange);
		}
	}
}