package com.faradice.faraframe.html;

import com.faradice.faraUtil.FaraFiles;
import com.faradice.faraframe.properties.BasicPropertyItem;
import com.faradice.faranet.FaraHtml;

public class HtmlProperties {

	public static void test() {
		BasicPropertyItem bpi = new BasicPropertyItem();
		bpi.setColumns("Name", "GSM", "RFID", "Email", "ID");
		bpi.set("Name", "Ragnar");
		bpi.set("GSM", "8967869");
		bpi.set("RFID", "RFID-2020");
		bpi.set("Email", "ragnar@faraadice.com");
		bpi.set("ID", "TP-ZYX");
		System.out.println(bpi.columnsString());
		System.out.println(bpi.valuesString());
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
	
	public static String form(String[] cols, Object[] values, int[] fieldSize) {
		BasicPropertyItem bi = new BasicPropertyItem(cols);
		bi.setValues(values);
		bi.setAttribute("Size", fieldSize);
//		bi.setAttribute("Action", action);
		bi.setAttribute("Submit", "OK");
		return form(bi);
	}
	
	public static void save(String data, String fileName) {
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
	
	public static void main(String[] args) {
		test();
	}
}
