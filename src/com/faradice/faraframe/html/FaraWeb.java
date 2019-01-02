package com.faradice.faraframe.html;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FaraWeb {
	public static String begin() {
		String hd = "<html><head>";
		hd+= load("Header.html").replace("$(Title)", "Faradice");
		hd+="<style>";
		hd+= load("css/Common.css");
		hd+="</style>";
		hd+= "</head>";
		hd+= "<body>";
	    hd+= load("Top.html").replace("$(Name)", "Faradice");
		return hd;
	}

	public static String end() {
		String end = "<div class='Footer'><a href='http://www.faradice.com'>Visit Faradice Home</a></div>";
		end += "</body></html>";
		return end;
	}
	
	public static String load(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			String resource = "/"+fileName;
			InputStream is = FaraWeb.class.getResourceAsStream(resource);
			if (is == null) throw new Exception("Resorce not found: "+resource);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String res = null;
			while ((res = br.readLine()) != null) {
				sb.append(res+"\n");
			}
		} catch (Exception e) {
			sb.append("\n"+e.getMessage());
			e.printStackTrace();
		} 
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(begin());
	}
	
}
