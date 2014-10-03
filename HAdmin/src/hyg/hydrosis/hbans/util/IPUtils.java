package hyg.hydrosis.hbans.util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class IPUtils {
	static HashMap<String, JSONObject> ipStorage = new HashMap<String, JSONObject>();

	public static String ipToTime(String ip) {
		int offset = 0;
		if (ipStorage.containsKey(ip)) {
			offset = Integer.parseInt((String) ipStorage.get(ip)
					.get("timeZone"));
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="
					+ ip + "&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			String timezone = (String) object.get("timeZone");
			if (timezone != null && timezone.length() > 3) {
				offset = Integer.parseInt(timezone.substring(0,
						timezone.length() - 3));
				ipStorage.put(ip, object);
			} else {
				return "Error: Cannot parse time";
			}
		}
		Calendar time = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		time.add(Calendar.HOUR_OF_DAY, offset);
		DateFormat formatter = new SimpleDateFormat("EEEEEE hh:mm");
		formatter.setCalendar(time);
		String date = formatter.format(time.getTime());
		DateFormat formatter2 = new SimpleDateFormat("aa");
		formatter2.setCalendar(time);
		date += formatter2.format(time.getTime()).toLowerCase();
		return date;
	}
	
	public static long timezoneOffset(String ip)
	{
		if (ipStorage.containsKey(ip))
		{
			String[] str = ((String)(ipStorage.get(ip).get("timeZone"))).split(":");
			long hours = Long.parseLong(str[0])*60*60*1000;
			long minutes = Long.parseLong(str[1])*60*1000;
			if(hours<0)
				return hours+minutes;
			else
				return hours-minutes;
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip="
					+ ip + "&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			String timezone = (String) object.get("timeZone");
			if (timezone != null && timezone.length() > 3) {
				String str[] = timezone.split(":");
				long hours = Long.parseLong(str[0])*60*60*1000;
				long minutes = Long.parseLong(str[1])*60*1000;
				ipStorage.put(ip, object);
				if(hours>0)
					return hours+minutes;
				else
					return hours-minutes;
			} else {
				return 0;
			}
		}
	}
	public static String getCityName(String ip) {
		JSONObject obj = null;
		if (ipStorage.containsKey(ip)) {
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip=[/url]"
					+ ip + "&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip, object);
		}
		return (String) obj.get("cityName");
	}

	public static String getStateName(String ip) {
		JSONObject obj = null;
		if (ipStorage.containsKey(ip)) {
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip=[/url]"
					+ ip + "&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip, object);
		}
		return (String) obj.get("regionName");
	}

	public static String getCountryName(String ip) {
		JSONObject obj = null;
		if (ipStorage.containsKey(ip)) {
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip=[/url]"
					+ ip + "&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip, object);
		}
		String country = (String) obj.get("countryName");
		if (country.contains(",")) {
			country = country.split(",")[0];
		}
		return country;
	}

	public static String getCountryCode(String ip) {
		JSONObject obj = null;
		if (ipStorage.containsKey(ip)) {
			obj = ipStorage.get(ip);
		} else {
			String url = "http://api.ipinfodb.com/v3/ip-city/?key=d7859a91e5346872d0378a2674821fbd60bc07ed63684c3286c083198f024138&ip=[/url]"
					+ ip + "&format=json";
			JSONObject object = stringToJSON(getUrlSource(url));
			obj = object;
			ipStorage.put(ip, object);
		}
		String country = (String) obj.get("countryCode");
		return country;
	}

	public static JSONObject stringToJSON(String json) {
		return (JSONObject) JSONValue.parse(json);
	}

	private static String getUrlSource(String url) {
		URL url2 = null;
		try {
			url2 = new URL(url);
		} catch (MalformedURLException e) {
		}
		URLConnection yc = null;
		try {
			yc = url2.openConnection();
		} catch (IOException e) {
		}
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(yc.getInputStream(),
					"UTF-8"));
		} catch (IOException e) {
		}
		String inputLine;
		StringBuilder a = new StringBuilder();
		try {
			while ((inputLine = in.readLine()) != null)
				a.append(inputLine);
		} catch (IOException e) {
		}
		try {
			in.close();
		} catch (IOException e) {
		}

		return a.toString();
	}
}