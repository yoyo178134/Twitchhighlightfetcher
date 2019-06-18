package HighlightMaker;


public class HighLightTime {
	private Time startTime;
	private Time endTime;
		
	public HighLightTime(Time myTime, int advance,int delay)
	{
			startTime = new Time(myTime);
			endTime = new Time(myTime);
			setStartTime(advance);
			setEndTime(delay);
	}
	
	public HighLightTime(Time myTime)
	{
		startTime = new Time(myTime);
		endTime = new Time(myTime);
		setStartTime(20);
		setEndTime(20);
	}
	
	public Time getStartTime() {
		return startTime;
	}

	public void setStartTime(Time startTime) {
		this.startTime = startTime;
	}

	public Time getEndTime() {
		return endTime;
	}

	public void setEndTime(Time endTime) {
		this.endTime = endTime;
	}

	public void setStartTime(int advance)
	{
		if(startTime.getSecond()<advance)
		{
			startTime.setMin(startTime.getMin() - 1);
			startTime.setSecond(startTime.getSecond() - advance + 60);
			if(startTime.getMin() < 0)
			{
				startTime.setHour(startTime.getHour() - 1);
				startTime.setMin(startTime.getMin() + 60);
			}
			if(startTime.getHour() < 0)
			{
				startTime.setHour(0);
				startTime.setMin(0);
				startTime.setSecond(0);
			}
		}
		else
			startTime.setSecond(startTime.getSecond() - advance);
	}
	
	public void setEndTime(int delay)
	{
		if(endTime.getSecond() + delay > 60)
		{
			endTime.setSecond(endTime.getSecond() + delay - 60);
			endTime.setMin(endTime.getMin() + 1);
			if(endTime.getMin()>60)
			{
				endTime.setHour(endTime.getHour() + 1);
				endTime.setMin(endTime.getMin() - 60);
			}
		}
		else
			endTime.setSecond(endTime.getSecond() + delay);
	}
	
	public String toString()
	{
		String str;
		str = String.format("%s to %s", startTime, endTime);
		return str;
	}
}