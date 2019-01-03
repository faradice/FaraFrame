package com.faradice.faraframe.html;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.faradice.faraUtil.FaraFiles;
import com.faradice.faraframe.properties.BasicPropertyItem;
import com.faradice.faranet.FaraHtml;

public class FaraWeb {
	public static String begin() {
		String hd = "<html><head>";
		hd+= load("Header.html").replace("$(Title)", "Faradice");
		hd+="<style>";
		hd+= load("css/Common.css");
		hd+="</style>";
		hd+= "</head>";
		hd+= "<body>";
		hd+="<div class='page-wrap'>";
	    hd+= load("Top.html").replace("$(Name)", "Faradice");
		return hd;
	}

	public static String end() {
		String end = "</div>";
		end += "<footer class='site-footer'><a href='http://www.faradice.com'>Visit Faradice Home</a></footer>";
		end += "</body></html>";
		return end;
	}
	
	public static String form(String[] cols, Object[] values, int[] fieldSize) {
		BasicPropertyItem bi = new BasicPropertyItem(cols);
		bi.setValues(values);
		bi.setAttribute("Size", fieldSize);
//		bi.setAttribute("Action", action);
		bi.setAttribute("Submit", "OK");
		return form(bi);
	}
	
	public static String form(BasicPropertyItem bpi) {
//		String action = bpi.getAttribute("Action").toString();
		String submitTxt = bpi.getAttribute("Submit").toString();
		int[] length = ((int[])bpi.getAttribute("Size"));
//		String form = "<form action='"+action+"' method='post'>";
		String form = "<form method='post'>";
		int i = 0;
		for (String key : bpi.getAllKeys()) {
			String value = bpi.getString(key);
			Class<?> type = bpi.getType(key);
			if (type.isAssignableFrom(Boolean.class)) {
				form+= FaraHtml.createCheckbox(key, key, value);
			} else {
				form+= FaraHtml.createField(key, length[i]+"", value);
			}
			i++;
			form+="<br>";
		}
		form+=FaraHtml.createButton(submitTxt, submitTxt.toLowerCase());
		form+="</form>";
		return form;
	}
	
	public static String beginCard() {
		StringBuffer sb = new StringBuffer();
		sb.append("<div class='card' style='width:300;margin:10 auto;'>");
		sb.append("<div class='container'>");
		sb.append("<div style='margin:1em;font-size:120%'>Register User</div>");
		sb.append("<div style='margin:1em'>");
		return sb.toString();
	}
	
	public static String table(List<String> csvRows) {
		if (csvRows.size() < 1) {
			return "";
		}
		StringBuffer sb = new StringBuffer();		
		sb.append(FaraHtml.startTable());
		int rowPos = 0;
		String header = csvRows.get(rowPos).trim();
		String[] cols = header.split(",");
		rowPos = 1;
		if (!header.startsWith("#")) {
			rowPos = 0;
			cols = new String[cols.length];
		}
		for (int colNr=0; colNr<cols.length; colNr++) {
			String col = cols[colNr];
			if (col == null) {
				col = "col "+colNr;
			}
			sb.append(FaraHtml.header(col));
		}
		
		for (int rowId=rowPos; rowId < csvRows.size(); rowId++) {
			String row = csvRows.get(rowId).trim();
			cols = row.split(",");
			sb.append(FaraHtml.startRow());
			for (int colNr=0;colNr <cols.length; colNr++) {
				sb.append(FaraHtml.cell(cols[colNr]));
			}
			sb.append(FaraHtml.endRow());
		}		
		sb.append(FaraHtml.endTable());
		return sb.toString();
	}
	
	public static String endCard() {
		StringBuffer sb = new StringBuffer();
		sb.append("</div>");
		sb.append("</div>");
		sb.append("</div>");
		return sb.toString();
	}
	
	public static void saveForm(String data, String fileName) {
		if (data == null || data.length() < 2) {
			return;
		}
		String row = "";
		String[] cols = data.split("&");
		int colNr = 0;
		for (int i=0; i<cols.length-1; i++) {
			String value = cols[i];
			if (value == null || !value.contains("=")) {
				continue;
			}
			if (colNr > 0) {
				row += ", ";
			}
			value = value.substring(value.indexOf("=")+1);
			row += value;
			colNr++;
		}
		FaraFiles.appendToFile(fileName, row);
	}

	public static String load(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			String resource = "/"+fileName;
			InputStream is = FaraWeb.class.getResourceAsStream(resource);
			if (is == null) throw new Exception("Resource not found: "+resource);
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
