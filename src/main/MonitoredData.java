package main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MonitoredData
{
	private String startTime;
	private String endTime;
	private String activity;
	
	MonitoredData(String startTime, String endTime, String activity)
	{
		this.startTime = startTime;
		this.endTime = endTime;
		this.activity = activity;
	}
	
	public String getStartTime()
	{
		return startTime;
	}
	
	public void setStartTime(String startTime)
	{
		this.startTime = startTime;
	}
	
	public String getEndTime()
	{
		return endTime;
	}
	
	public void setEndTime(String endTime)
	{
		this.endTime = endTime;
	}
	
	public String getActivity()
	{
		return activity;
	}
	
	public Date getStartTimeInDateFormat() throws ParseException
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.parse(this.getStartTime());
	}
 	
	public String getDay()
	{
		return startTime.split(" ")[0];
	}
	
	
	
}
