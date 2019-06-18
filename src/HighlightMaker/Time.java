package HighlightMaker;


public class Time {
	private int hour;
	private int min;
	private int second;
	private int num;
	
	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public void numAddOne() {
		num++;
	}
	
	public Time(int hour,int min,int second)
	{
		this.hour = hour;
		this.min = min;
		this.second = second;
		num = 1;
	}
	
	public Time(int hour,int min,int second,int num)
	{
		this.hour = hour;
		this.min = min;
		this.second = second;
		this.num = num;
	}
	
	public Time(Time myTime)
	{
		this.hour = myTime.getHour();
		this.min = myTime.getMin();
		this.second = myTime.getSecond();
		this.num = myTime.getNum();
	}
	
	public String toString()
	{
		String a,b,c;
		if(hour < 10)
			a = "0" + Integer.toString(hour);
		else
			a = Integer.toString(hour);
		if(min < 10)
			b = "0" + Integer.toString(min);
		else
			b = Integer.toString(min);
		if(second < 10)
			c = "0" + Integer.toString(second);
		else
			c = Integer.toString(second);
		return a + ":" + b + ":" + c;
	}
}

