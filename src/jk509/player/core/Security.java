package jk509.player.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Scanner;

import jk509.player.Constants;
import jk509.player.logging.Logger;
import jk509.player.logging.Logger.LogType;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.EnumVariant;
import com.jacob.com.Variant;

public class Security {
	// Get the serial number of the machine's mother board. Supports 3 major OS's
	public static final String getSerialNumber(boolean withUsername) {

		if(Constants.ALT_SERIALNO_CODE){
			String m = getMobo();
			if(m == null || m.equals("") || m.length() < 4 || m.equals("System"))
				return getMobo2();
			else
				return m;
		}
		
		String details = "";
		if(withUsername){
			String compname = System.getenv("COMPUTERNAME");
			if (compname == null)
				try {
					compname = InetAddress.getLocalHost().getHostName();
				} catch (UnknownHostException e) {
					compname = "NONAME";
				}
			String username = System.getenv("USERNAME");
			if (username == null)
				username = System.getProperty("user.name");
	
			details = compname + username;
		}else{
			details = "";
		}

		OsCheck.OSType ostype = OsCheck.getOperatingSystemType();
		switch (ostype) {
		case Windows:
			return details + getSerialNumberWin();
		case MacOS:
			return details + getSerialNumberMac();
		case Linux:
			return details + getSerialNumberNix();
		case Other:
			return details + getSerialNumberWin();
		}
		return null;
	}

	private static final String getMobo(){
		String output = "";
		ComThread.InitMTA();
	    try {
	        ActiveXComponent wmi = new ActiveXComponent("winmgmts:\\\\.");
	        Variant instances = wmi.invoke("InstancesOf", "Win32_BaseBoard");
	        Enumeration<Variant> en = new EnumVariant(instances.getDispatch());
	        while (en.hasMoreElements())
	        {
	            ActiveXComponent bb = new ActiveXComponent(en.nextElement().getDispatch());
	            output = bb.getPropertyAsString("SerialNumber");
	            break;
	        }
	    } finally {
	        ComThread.Release();
	    }
	    return output;
	}
	
	public static String getMobo2() {
		String result = "";
	    try {
	      File file = File.createTempFile("realhowto",".vbs");
	      file.deleteOnExit();
	      FileWriter fw = new java.io.FileWriter(file);

	      String vbs =
	         "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
	        + "Set colItems = objWMIService.ExecQuery _ \n"
	        + "   (\"Select * from Win32_BaseBoard\") \n"
	        + "For Each objItem in colItems \n"
	        + "    Wscript.Echo objItem.SerialNumber \n"
	        + "    exit for  ' do the first cpu only! \n"
	        + "Next \n";

	      fw.write(vbs);
	      fw.close();
	      Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
	      BufferedReader input =
	        new BufferedReader
	          (new InputStreamReader(p.getInputStream()));
	      String line;
	      while ((line = input.readLine()) != null) {
	         result += line;
	      }
	      input.close();
	    }
	    catch(Exception e){
	        Logger.log(e, LogType.ERROR_LOG);
	    }
	    return result.trim();
    }
	
	// Get MB serial on Windows
	private static final String getSerialNumberWin() {

		String sn = null;

		OutputStream os = null;
		InputStream is = null;

		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(new String[] { "wmic", "bios", "get", "serialnumber" });
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		os = process.getOutputStream();
		is = process.getInputStream();

		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Scanner sc = new Scanner(is);
		try {
			while (sc.hasNext()) {
				String next = sc.next();
				if ("SerialNumber".equals(next)) {
					sn = sc.next().trim();
					break;
				}
			}
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (sn == null) {
			throw new RuntimeException("Cannot find computer SN");
		}

		return sn;
	}

	// Get MB serial no. on Mac
	private static final String getSerialNumberMac() {

		String sn = null;

		OutputStream os = null;
		InputStream is = null;

		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(new String[] { "/usr/sbin/system_profiler", "SPHardwareDataType" });
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		os = process.getOutputStream();
		is = process.getInputStream();

		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String marker = "Serial Number:";
		try {
			while ((line = br.readLine()) != null) {
				if (line.indexOf(marker) != -1) {
					sn = line.split(marker)[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (sn == null) {
			throw new RuntimeException("Cannot find computer SN");
		}

		return sn;
	}

	// Get MB serial no. in Linux (and Unix?)
	private static final String getSerialNumberNix() {

		String sn = null;

		OutputStream os = null;
		InputStream is = null;

		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(new String[] { "dmidecode", "-t", "system" });
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		os = process.getOutputStream();
		is = process.getInputStream();

		try {
			os.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		String marker = "Serial Number:";
		try {
			while ((line = br.readLine()) != null) {
				if (line.indexOf(marker) != -1) {
					sn = line.split(marker)[1].trim();
					break;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (sn == null) {
			throw new RuntimeException("Cannot find computer SN");
		}

		return sn;
	}

	// Determine OS type
	private static final class OsCheck {
		/**
		 * types of Operating Systems
		 */
		public enum OSType {
			Windows, MacOS, Linux, Other
		};

		protected static OSType detectedOS;

		/**
		 * detected the operating system from the os.name System property and cache the result
		 * 
		 * @returns - the operating system detected
		 */
		public static OSType getOperatingSystemType() {
			if (detectedOS == null) {
				String OS = System.getProperty("os.name", "generic").toLowerCase();
				if (OS.indexOf("win") >= 0) {
					detectedOS = OSType.Windows;
				} else if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
					detectedOS = OSType.MacOS;
				} else if (OS.indexOf("nux") >= 0) {
					detectedOS = OSType.Linux;
				} else {
					detectedOS = OSType.Other;
				}
			}

			return detectedOS;
		}
	}
}
