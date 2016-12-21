package se.esss.litterbox.icecube.serialioc;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ItsSolarMeterIoc extends IceCubeSerialIoc
{
	public ItsSolarMeterIoc(String domain, String clientIdBase, String brokerUrl, String brokerKey, String brokerSecret, String serialPortName) throws Exception 
	{
		super(domain, clientIdBase, brokerUrl, brokerKey, brokerSecret, serialPortName);
	}
	@Override
	public byte[] getSerialData() 
	{
		JSONObject outputData = new JSONObject();
		String command = "photoGet";
		readResponseStringFromSerial(command, 10, outputData);
		
		command = "photoAvgGet";
		readResponseStringFromSerial(command, 10, outputData);

		command = "tempGet";
		readResponseStringFromSerial(command, 10, outputData);
		
		return outputData.toJSONString().getBytes();
	}
	@Override
	public void handleIncomingMessage(String topic, byte[] message) 
	{
		if (topic.equals("solarMeter01/set/avgPeriod"))
		{
			try
			{
				JSONParser parser = new JSONParser();		
				JSONObject jsonData = (JSONObject) parser.parse(new String(message));
				String avgPeriodSet = (String) jsonData.get("avgPeriodSet");
				writeReadSerialData("avgPeriodSet " + avgPeriodSet, 10);
			}
			catch (ParseException nfe) {}
		}
		if (topic.equals("solarMeter01/set/samplePeriod"))
		{
			try
			{
				JSONParser parser = new JSONParser();		
				JSONObject jsonData = (JSONObject) parser.parse(new String(message));
				String samplePeriod = (String) jsonData.get("samplePeriodSet");
				writeReadSerialData("samplePeriodSet " + samplePeriod, 10);
			}
			catch (ParseException nfe) {}
		}
	}
	public static void main(String[] args) throws Exception 
	{
		ItsSolarMeterIoc ioc = new ItsSolarMeterIoc("its", "ItsSolarMeter01Ioc", "tcp://broker.shiftr.io:1883", "c8ac7600", "1e45295ac35335a5", "/dev/rfcomm3");
		ioc.setPeriodicPollPeriodmillis(2000);
		ioc.startIoc("solarMeter01/set/#", "solarMeter01/get/cond");
	}
}