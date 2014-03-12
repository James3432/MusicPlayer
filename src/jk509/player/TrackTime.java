package jk509.player;

public class TrackTime implements Comparable<TrackTime> {
	int hours;
	int minutes;
	int seconds;
	int tot_s;

	public TrackTime(int s) {
		tot_s = s;
		hours = s / 3600;
		minutes = (s % 3600) / 60;
		seconds = (s % 3600) % 60;
	}

	public TrackTime(int h, int m, int s) {
		tot_s = s + 60 * m + 3600 * h;
		hours = h;
		minutes = m;
		seconds = s;
	}

	public String toString() {
		String h = Integer.toString(hours);
		String m = Integer.toString(minutes);
		String s = Integer.toString(seconds);
		if (s.length() == 0)
			s = "00";
		if (s.length() == 1)
			s = "0" + s;
		if (hours > 0) {
			if (m.length() == 0)
				m = "00";
			if (m.length() == 1)
				m = "0" + m;
			if (h.startsWith("0"))
				h = h.substring(1);
		} else {
			if (m.startsWith("0") && m.length() > 1)
				m = m.substring(1);
		}

		if (hours == 0)
			return m + ":" + s;
		else
			return h + ":" + m + ":" + s;
	}

	public String SecondsToString(int length) {
		String s = Integer.toString(length % 60);
		if (s.length() == 0)
			s = "00";
		if (s.length() == 1)
			s = "0" + s;
		if (s.length() > 2)
			s = s.substring(0, 2);
		return (length / 60 + ":" + s);
	}

	@Override
	public int compareTo(TrackTime o) {
		return this.tot_s - o.tot_s;
	}
}
