package com.faradice.faraframe.html;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import com.faradice.faraUtil.FaraFiles;
import com.faradice.faraframe.properties.BasicPropertyItem;
import com.faradice.faranet.FaraHtml;

public class FaraWeb {
	final String htmlPath;

	public FaraWeb() {
		this("");
	}
	public FaraWeb(String htmlpath) {
		this.htmlPath = htmlpath;
	}
	
	public String pageFrame(String title, String styles, String jspTop, String jspReady, String content) {
		String header = loadFromCP(htmlPath+"Header.html");
		String page = loadFromCP(htmlPath+"PageFramework.html");
		String mainStyles = loadFromCP(htmlPath+"MainStyles.css");
		page = page.replace("$(MainStyles)", mainStyles);
		page = page.replace("$(Title)", title);
		page = page.replace("$(Styles)", styles);
		page = page.replace("$(JspTop)", jspTop);
		page = page.replace("$(JspReady)", jspReady);
		page = page.replace("$(Header)", header);
		page = page.replace("$(Content)", content);
		return page;
	}

	
	public String pageFrame(String title, String content) {
		return pageFrame(title, "", "", "", content);
	}

	public String pageFrame(String title, String styles, String content) {
		return pageFrame(title, styles, "", "", content);
	}

	
	public String end() {
		return  loadFromCP(htmlPath+"Footer.html");
	}
	
	public static String lcr(String s) {
		return s+"\n";
	}	

	public static String headerBody() {
		String header = loadFromCP("HeaderBody.html");
		return header;
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
			form+=lcr("<br>");
		}
		form+=lcr(FaraHtml.createButton(submitTxt, submitTxt.toLowerCase()));
		form+=lcr("</form>");
		return form;
	}
	
	public static String beginCard(int width) {
		StringBuffer sb = new StringBuffer();
		sb.append(lcr("<div class='card' style='width:"+width+";margin:10 auto;'>"));
		sb.append(lcr("<div class='container'>"));
		sb.append(lcr("<div style='margin:1em;font-size:120%'>Register User</div>"));
		sb.append(lcr("<div style='margin:1em'>"));
		return sb.toString();
	}
	
	public static String plainHeaderHead() {
		String tableControl = loadFromCP("StandaloanHeaderHead.html");
		String mainStyles = loadFromCP("MainStyles.css");
		tableControl = tableControl.replace("$(MainStyles)", mainStyles);
		return tableControl;
	}

	public static String plainHeaderBody() {
		String tableControl = loadFromCP("StandaloanHeader.html");
		return tableControl;
	}

	public static String tableControl(String prefix) {
		String tableControl = loadFromCP(prefix+"TableControl.html");
		return tableControl;
	}

	public static String testCheckBoxPage() {
		String page = loadFromCP("ext/CheckBoxDemoOrg.html");
		return page;
	}

	public static String testCheckBox2Page() {
		String page = loadFromURL("www.faradice.com/resource/ext/CheckBoxDemoOrg.html");
		return page;
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
		sb.append(lcr("<thead>"));
		sb.append(FaraHtml.startTableRow());
		for (int colNr=0; colNr<cols.length; colNr++) {
			String col = cols[colNr];
			if (col == null) {
				col = "col "+colNr;
			}
			sb.append(FaraHtml.tableHeader(col));
		}
		sb.append(FaraHtml.endTableRow());
		sb.append(lcr("</thead>"));
		sb.append(lcr("<tbody>"));
		for (int rowId=rowPos; rowId < csvRows.size(); rowId++) {
			String row = csvRows.get(rowId).trim();
			cols = row.split(",");
			sb.append(FaraHtml.startTableRow());
			for (int colNr=0;colNr <cols.length; colNr++) {
				sb.append(FaraHtml.cell(cols[colNr]));
			}
			sb.append(FaraHtml.endTableRow());
		}		
		sb.append(lcr("</tbody>"));
		sb.append(FaraHtml.endTable());
		return sb.toString();
	}
	
	public static String endCard() {
		StringBuffer sb = new StringBuffer();
		sb.append(lcr("</div>"));
		sb.append(lcr("</div>"));
		sb.append(lcr("</div>"));
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
		FaraFiles.appendRowToCSVFile(fileName, row);
	}

	public static String loadFromCP(String fileName) {
		StringBuilder sb = new StringBuilder();
		try {
			String resource = "/"+fileName;
			InputStream is = FaraWeb.class.getResourceAsStream(resource);
			if (is == null) throw new Exception("Resource not found: "+resource);
			BufferedReader br = new BufferedReader(new InputStreamReader(is,"UTF-8"));
			String res = null;
			while ((res = br.readLine()) != null) {
				sb.append(lcr(res));
			}
		} catch (Exception e) {
			sb.append("\n"+e.getMessage());
			e.printStackTrace();
		} 
		return sb.toString();
	}

	public static String loadFromURL(String resource) {
		StringBuilder sb = new StringBuilder();
		if (!resource.startsWith("http://")) {
			resource = "http://"+ resource;
		}
		BufferedReader br = null;
		try  {
			URI uri = new URI(resource);
			br = new BufferedReader(new InputStreamReader(uri.toURL().openStream(),"UTF-8"));
			String res = null;
			while ((res = br.readLine()) != null) {
				sb.append(lcr(res));
			}
			if (br != null) br.close();
		} catch (Exception e) {
			sb.append("\n"+e.getMessage());
			e.printStackTrace();
		}
		return sb.toString();		
	}
	
	public static String loginPage(String rootPath, String backgroundURL) {
		FaraHtml fh = new FaraHtml();
		StringBuilder content = new StringBuilder();
        content.append("<div class='container-fluid backImg'>");
		content.append("<p style='margin:12em'>");
		content.append("<div class='col-sm-4 col-sm-offset-4'>'");		
		content.append("<form method=\"post\">");
		content.append(FaraHtml.passwordField("AÐGANGSORÐ", "200"));
		content.append(FaraHtml.primaryButton("Innskrá", "login", true));
		content.append("</form>");
		content.append("</div>");
	 	content.append("</div>");
		String url = backgroundURL;
		StringBuilder sb = new StringBuilder();
		sb.append(".backImg {");
	    sb.append("background: url("+url+") center center;");
	    sb.append("background-size: cover;");
	    sb.append("background-position: 50% 30%;");
	    sb.append("height: 36em");
		sb.append("}");
		String page = new FaraWeb(rootPath).pageFrame("INNSKRÁNING", sb.toString(), content.toString());
		return page;
	}
			
}
