package de.dfki.irl.darby.prediction.helper;

import java.util.Date;
import java.util.GregorianCalendar;

public class SAPTimestamp {
	int year, month, day, hours, minutes, seconds;
	Date date;

	public SAPTimestamp() {

	}

	public SAPTimestamp(int year, int month, int day, int hours, int minutes,
			int seconds) {
		super();
		this.year = year;
		this.month = month;
		this.day = day;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;

		calculateDate(year, month, day, hours, minutes, seconds);
	}
	

	@SuppressWarnings("deprecation")
	private void calculateDate(int year, int month, int day, int hours,
			int minutes, int seconds) {
		date = new Date(year - 1900, month - 1, day, hours, minutes, seconds);
		//GregorianCalendar date = new GregorianCalendar(year + 1900, month, day, hours, minutes, seconds);
	}

	public static SAPTimestamp fromString(String in) {

		if (in.length() != 14) {
			System.err.println("Invalid SAPTimestamp:" + in);
			return null;
		}
		try {
			int year = Integer.parseInt(in.substring(0, 4));
			int month = Integer.parseInt(in.substring(4, 6));
			int day = Integer.parseInt(in.substring(6, 8));
			int hours = Integer.parseInt(in.substring(8, 10));
			int minutes = Integer.parseInt(in.substring(10, 12));
			int secs = Integer.parseInt(in.substring(12, 14));
			return new SAPTimestamp(year, month, day, hours, minutes, secs);
		} catch (NumberFormatException nf) {
			System.err.println("Invalid SAPTimestamp:" + in);
			nf.printStackTrace();
			return null;
		}
	}

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
		calculateDate(year, month, day, hours, minutes, seconds);
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
		calculateDate(year, month, day, hours, minutes, seconds);
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
		calculateDate(year, month, day, hours, minutes, seconds);
	}

	public static SAPTimestamp fromLong(long in) {
		return SAPTimestamp.fromString(Long.toString(in));
	}

	public long toEpoch() {
		return date.getTime();
	}

	@SuppressWarnings("deprecation")
	public static SAPTimestamp fromEpoch(long epoch) {
		Date date = new Date(epoch);
		SAPTimestamp ret = new SAPTimestamp(date.getYear() + 1900,
				date.getMonth() + 1, date.getDay(), date.getHours(),
				date.getMinutes(), date.getSeconds());
		return ret;
	}

	@Override
	public String toString() {
		return Integer.toString(year) + "-" + Integer.toString(month) + "-"
				+ Integer.toString(day) + " ; " + Integer.toString(hours) + ":"
				+ Integer.toString(minutes) + "." + Integer.toString(seconds);
	}

	@Override
	public SAPTimestamp clone() {
		return new SAPTimestamp(year, month, day, hours, minutes, seconds);
	}

	public String toSAPString() {
		return Integer.toString(year) + twoString(month) + twoString(day)
				+ twoString(hours) + twoString(minutes) + twoString(seconds);
	}

	private String twoString(int zahl) {
		String ret = Integer.toString(zahl);
		if (ret.length() == 1) {
			ret = "0" + ret;
		}
		return ret;
	}
}
