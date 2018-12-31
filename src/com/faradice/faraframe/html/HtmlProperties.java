package com.faradice.faraframe.html;

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
	
/*	
	<form action="/action_page.php">
	  First name:<br>
	  <input type="text" name="firstname" value="Mickey">
	  <br>
	  Last name:<br>
	  <input type="text" name="lastname" value="Mouse">
	  <br><br>
	  <input type="submit" value="Submit">
	</form> 
*/
	public static String htmlForm(BasicPropertyItem bpi) {
		String action = bpi.getAttribute("Action");
		String submitTxt = bpi.getAttribute("Submit"); 
		String form = "<form action='"+action+"' method='post'>";
		for (String key : bpi.getAllKeys()) {
			String value = bpi.getString(key);
			Class<?> type = bpi.getType(key);
			if (type.isAssignableFrom(Boolean.class)) {
				form+= FaraHtml.createCheckbox(key, key, value);
			} else {
				form+=key+":<br>";
				form+= FaraHtml.createField(key, "100", value);
			}
			form+="<br>";
		}
		form+=FaraHtml.createButton(submitTxt, submitTxt.toLowerCase());
		form+="</form>";
		return form;
	}
	
	public static void main(String[] args) {
		test();
	}
	
}
