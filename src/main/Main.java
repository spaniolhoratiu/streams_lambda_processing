package main;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

public class Main
{	
	private static List<MonitoredData> monitoredData;
	
	private static FileWriter fileWriter;
    private static PrintWriter printWriter;
	
	public static void main(String[] args) throws IOException
	{	
		try 
		{
			initialize();
			countDays();
			countActivities();	
			countActivitiesPerDay();
			activityDurationPerLine();
			activityDurationPerTotal2();
			filterActivitiesOver90PercentUnder5Minutes();
			JOptionPane.showMessageDialog(null, "Ran succesfully!", "That's what she said", JOptionPane.INFORMATION_MESSAGE);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}	
	
	private static void initialize() throws IOException
	{
		monitoredData = new ArrayList<MonitoredData>();
		fileWriter = new FileWriter("log.txt");
		printWriter = new PrintWriter(fileWriter);

		String fileName = "Activities.txt";
		List<String> monitoredDataStrings = null;

		//read file into stream, try-with-resources
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) 
		{	
			monitoredDataStrings = stream.collect(Collectors.toList());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		monitoredDataStrings.forEach(s -> {
			String[] splitList = s.split("\t\t");
			String startTime = splitList[0];
			String endTime = splitList[1];
			String activity = splitList[2].trim();
			MonitoredData md = new MonitoredData(startTime, endTime, activity);
			monitoredData.add(md);
		 	});
	}
	
	private static void countDays() // Count how many days of monitored data appears in the log.
	{
		int differentDays = monitoredData.stream().distinct().collect(Collectors.groupingBy(MonitoredData::getDay)).size();
		printWriter.println("1. Count how many days of monitored data appears in the log.");
		printWriter.println("Different days: " + differentDays + "\n");
	}
	
	private static void countActivities()
	{
		Map<String, Long> map = monitoredData.stream().collect(Collectors.groupingBy(MonitoredData::getActivity,Collectors.counting()));
		
		printWriter.println("2.	Determine a map of type <String, Integer> that maps to each distinct action type the number of occurrences in the log. Write the resulting map into a text file");
		map.keySet().forEach(string -> printWriter.println(string + "\t\t" + map.get(string)));
	}
	
	private static void countActivitiesPerDay()
	{
		Map<String, Map<String, Long>> activityCountForEachDayMap = monitoredData.stream()
																						.collect(Collectors
																								.groupingBy(MonitoredData::getDay,Collectors
																										.groupingBy(MonitoredData::getActivity,
																												Collectors.counting())));
		
		printWriter.println("\n3. Generates a data structure of type Map<Integer, Map<String, Integer>> that contains the activity count for each day of the log (task number 2 applied for each day of the log)and writes the result in a text file");
		activityCountForEachDayMap.forEach((activity, map) -> {
			printWriter.println("\n" + activity);
			map.keySet().forEach(string -> printWriter.println(string + "\t\t" + map.get(string)));	
		});
		
	}
	
	private static void activityDurationPerLine()
	{
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		monitoredData.forEach(md -> {
			System.out.print(md.getActivity() + "\t");
			try
			{
				System.out.println((dataFormat.parse(md.getEndTime()).getTime() - dataFormat.parse(md.getStartTime()).getTime()) / 1000);
			}
			catch (ParseException e)
			{
				e.printStackTrace();
			}
		});
	}
	
	private static void activityDurationPerTotal() throws IOException
	{
		HashMap<String, Long> map = new HashMap<String, Long>();
		
		HashSet<String> differentActivities = new HashSet<String>();
		monitoredData.forEach(md -> differentActivities.add(md.getActivity()));
		differentActivities.forEach(string -> map.put(string, (long) 0));
		
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		monitoredData.forEach(md -> {
			Long durationOfActivity = map.get(md.getActivity());
			Long newDurationOfActivity = (long) 0;
			try
			{
				newDurationOfActivity = durationOfActivity + (dataFormat.parse(md.getEndTime()).getTime() - dataFormat.parse(md.getStartTime()).getTime()) / 1000;
				map.put(md.getActivity(), newDurationOfActivity);
			} 
			catch (ParseException e)
			{
				e.printStackTrace();
			}	
			});
		
		printWriter.println("\n4. Determine a data structure of the form Map<String, DateTime> that maps for each activity the total duration computed over the monitoring period. Filter the activities with total duration larger than 10 hours. Write the result in a text file.");
		map.forEach((activity, duration) -> {
			printWriter.print(activity + "\t" + duration + " seconds.");
			if(duration > 3600)
			{
				printWriter.println(" Takes more than 10 hours(3600 seconds).");
			}
			else
			{
				printWriter.println();
			}
		});
		
	}
	
	private static void activityDurationPerTotal2() throws IOException
	{
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Map<String, Long> map = monitoredData.stream().distinct().collect(Collectors.groupingBy(MonitoredData::getActivity, Collectors.summingLong(md -> {
			try
			{
				return (dataFormat.parse(md.getEndTime()).getTime() - dataFormat.parse(md.getStartTime()).getTime()) / 1000;
			} 
			catch (ParseException e)
			{
				e.printStackTrace();
			}
			return 0;
		})));
		
		
		printWriter.println("\n4. Determine a data structure of the form Map<String, DateTime> that maps for each activity the total duration computed over the monitoring period. Filter the activities with total duration larger than 10 hours. Write the result in a text file.");
		map.forEach((activity, duration) -> {
			printWriter.print(activity + "\t" + duration + " seconds.");
			if(duration > 3600)
			{
				printWriter.println(" Takes more than 10 hours(3600 seconds).");
			}
			else
			{
				printWriter.println();
			}
		});
		
		
	}
	
	private static void filterActivitiesOver90PercentUnder5Minutes() throws IOException
	{
		HashMap<String, Float> under5Map = new HashMap<String, Float>(); // map will contain: activity	
		HashMap<String, Float> over5Map = new HashMap<String, Float>();
		
		monitoredData.forEach(md -> under5Map.put(md.getActivity(), (float) 0));
		monitoredData.forEach(md -> over5Map.put(md.getActivity(), (float) 0));
		SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		monitoredData.forEach(md -> {
			try
			{
				Float numberOfTimes = null;
				if(((dataFormat.parse(md.getEndTime()).getTime() - dataFormat.parse(md.getStartTime()).getTime()) / 1000) > 300)
				{
					numberOfTimes = over5Map.get(md.getActivity());
					numberOfTimes++;
					over5Map.put(md.getActivity(), numberOfTimes);
				}
				else
				{
					numberOfTimes = under5Map.get(md.getActivity());
					numberOfTimes = numberOfTimes + 1;
					under5Map.put(md.getActivity(), numberOfTimes);
				}
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
		});
		
		
		printWriter.println("\n5. Filter the activities that have 90% of the monitoring samples with duration less than 5 minutes, collect the results in a List<String> containing only the distinct activity names and write the result in a text file.");
	    under5Map.forEach((activity, times) -> {
			float percentage = times / (times + over5Map.get(activity));
			if(percentage > 0.9)
			{
				printWriter.println(activity + ":\nPercentage of < 5 mins: " + percentage*100 + "%");
			}
		});
	    printWriter.close();
	}	
}
