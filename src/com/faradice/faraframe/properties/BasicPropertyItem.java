package com.faradice.faraframe.properties;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import com.faradice.faraframe.util.ColorProvider;

/**
 * A general and basic implementation of the property item
 * contains the most common property management and edition
 *
 * @author ragnar.valdimarsson
 *
 */
public class BasicPropertyItem implements IPropertyItem, Serializable {
	private static final long serialVersionUID = 1L;

	/***/
	public final static Class<?>[] TYPES = {String.class, Number.class, Integer.class, Boolean.class, Color.class};
	
	private List<String> fields = new ArrayList<String>();
	private List<String> editableFields = new ArrayList<String>();
	private HashMap<String, Object> properties = new LinkedHashMap<>();
	private HashMap<String, Class<?>> types = new HashMap<>();
	private HashMap<String, ConstraintKey[]> constraintKeys = new HashMap<>();
	private HashMap<String, String> attributes = new HashMap<>();
	private int idIndex = 0;
	
	private boolean eventOn = true;
	private Object[] event = new Object[3];
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>(); 
	private ArrayList<Object> sources = new ArrayList<Object>(); 

	/***/
	public BasicPropertyItem() {
		super();
	}

	/**
	 * @param item
	 */
	public BasicPropertyItem(BasicPropertyItem item) {
		super();
		fields = new ArrayList<String>(Arrays.asList(item.getItems()));
		editableFields = new ArrayList<String>(item.getEditableFields());
		properties.putAll(item.getProperties());
		types.putAll(item.getTypes());
		constraintKeys = item.getConstraintKeys();
		idIndex = item.getIdIndex();
	}

	public BasicPropertyItem(String[] fields, int idIndex) {
		this(Arrays.asList(fields), idIndex);
	}

	public BasicPropertyItem(List<String> fields, int idIndex) {
		this.fields = new ArrayList<String>(fields);
		this.idIndex = idIndex;
	}
	
	public void setIdIndex(int index) {
		this.idIndex = index;
	}

	public void setColumns(List<String> fields) {
		this.fields = fields;
	}

	public void setColumns(String... fields) {
		this.fields = Arrays.asList(fields);
	}

	public Object convertFieldToValue(String key, Object fieldValue) {
		return fieldValue;
	}

	public Object get(String key) {
		Object value = properties.get(key);
		return value;
	}

	public String getString(String key) {
		String result = "";
		Object value = get(key);
		if (value != null) {
			if (Color.class.isAssignableFrom(value.getClass())) {
				result = (value != null) ? ColorProvider.toString((Color)value) : "";
			} else {
				result = value.toString().trim();
			}
		}
		return result;
	}

	public Number getNumber(String key) {
		return (Number)properties.get(key);
	}

	public Image getImage(String key) {
		return (Image)properties.get(key);
	}

	public Boolean getBoolean(String key) {
		return (Boolean)properties.get(key);
	}

	public Date getDate(String key) {
		return (Date)properties.get(key);
	}

	public Color getColor(String key) {
		return (Color)properties.get(key);
	}

	public Font getFont(String key) {
		return (Font)properties.get(key);
	}

	public File getFile(String key) {
		return (File)properties.get(key);
	}

	public Timestamp getTimestamp(String key) {
		return (Timestamp)properties.get(key);
	}

	public void remove(String key) {
		properties.remove(key);
	}

	public boolean isEmpty(String key) {
		return empty(get(key));
	}

	public static boolean empty(Object s) {
		return (s == null || s.toString().trim().length() == 0 || s.toString().trim().equalsIgnoreCase("null"));
	}

	public boolean hasValue(String key) {
		return !isEmpty(key);
	}

	public String toString() {
		return valuesString();
	}
	
	public String valuesString() {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (String key : properties.keySet()) {
			Object value = properties.get(key);
			if (value != null && typeSupported(value.getClass())) {
				if (i>0) {
					sb.append(", ");
				}
				sb.append(getString(key));
				i++;
			}
		}
		return sb.toString();
	}

	public String columnsString() {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for (String key : properties.keySet()) {
			Object value = properties.get(key);
			if (value != null && typeSupported(value.getClass())) {
				if (i>0) {
					sb.append(", ");
				}
				sb.append(key);
				i++;
			}
		}
		return sb.toString();
	}

	
	public String[] getConstaints(String key) {
		String[] selection = new String[0];
		ConstraintKey[] constraints = constraintKeys.get(key);
		if (constraints != null) {
			selection = new String[constraints.length];
			for (int i=0; i < constraints.length; i++) {
				selection[i] =  constraints[i].description;
			}
		}
		return selection;
	}

	public void setAttribute(String key, String value) {
		attributes.put(key, value);
	}
	
	public String getAttribute(String key) {
		return attributes.get(key);
	}
	
	public void setEditable(String ... fields) {
		this.editableFields = Arrays.asList(fields);
	}

	public String[] getEditableItems() {
		if (editableFields.isEmpty()) {
			return getItems();
		} else {
			return editableFields.toArray(new String[0]);
		}
	}

	public String[] getItems() {
		if (fields.size() < 1) {
			createFieldsFromKeys();
		}
		return fields.toArray(new String[0]);
	}

	public void createFieldsFromKeys() {
		fields = new ArrayList<String>(properties.keySet());
		for (String field : fields) {
			Object value = get(field);
			if (value != null) {
				if (Date.class.isAssignableFrom(value.getClass())) {
					setType(field, Date.class);
				} else if (Color.class.isAssignableFrom(value.getClass())) {
					setType(field, Color.class);
				} else if (Font.class.isAssignableFrom(value.getClass())) {
					setType(field, Font.class);
				}
			}
		}
	}

	public Object getFieldValue(String key) {
		Object value = get(key);
		ConstraintKey[] constraints = constraintKeys.get(key);
		if (constraints != null && value != null) {
			Object constraintDesc = null;
			boolean found = false;
			for (int i=0; i < constraints.length && !found; i++) {
				if (value.equals(constraints[i].value)) {
					found = true;
					constraintDesc = constraints[i].description;
				}
			}
			if (found) value = constraintDesc;
		}
		return value;
	}

	public Object getId() {
		return get(fields.get(idIndex));
	}

	public List<Object> getAllValues() {
		return new ArrayList<Object>(properties.values());
	}

	public List<String> getAllKeys() {
		return new ArrayList<String>(properties.keySet());
	}

	public ArrayList<String> getAsText() {
		ArrayList<String> sa = new ArrayList<String>();
		for (String key : properties.keySet()) {
			Object value = properties.get(key);
			if (value != null && typeSupported(value.getClass())) {
				sa.add(key + "=" + getString(key));
			}
		}
 		return sa;
	}

	
	public Set<Object> getValueSet() {
		return new HashSet<Object>(properties.values());
	}

	public Set<String> getKeySet() {
		return properties.keySet();
	}

	public Class<?> getType(String key) {
		Class<?> type = types.get(key);
		if (type == null) type = String.class;
		return type;
	}

	public void setType(String key, Class<?> type) {
		types.put(key, type);
	}

	public void setConstraints(String type, String[] constraints) {
		setConstraints(type, constraints, constraints);
	}

	public void setConstraints(String type, List<String> constraints) {
		String[] cnstr = constraints.toArray(new String[0]);
		setConstraints(type, cnstr, cnstr);
	}

	public void setConstraints(String type, String[] description, Object[] values) {
		ConstraintKey[] constraintKeys = new ConstraintKey[description.length];
		for (int i=0; i<constraintKeys.length; i++) {
			constraintKeys[i] = new ConstraintKey(values[i], description[i]);
		}
		this.constraintKeys.put(type, constraintKeys);
	}

	public void set(String key, Object value) {
		ConstraintKey[] constraints = constraintKeys.get(key);
		if (constraints != null && value != null) {
			Object constraintValue = null;
			boolean found = false;
			for (int i=0; i < constraints.length && !found; i++) {
				if (value.equals(constraints[i].description)) {
					constraintValue = constraints[i].value;
					found = true;
				}
			}
			if (found) value = constraintValue;
		}

		Class<?> type = getType(key);
		if (type.isAssignableFrom(Number.class) &&  value instanceof String) {
			try {
				value = Integer.parseInt(value.toString());
			} catch (Exception ex) {
				value = Double.parseDouble(value.toString());  //TODO this is a workaround hack.  Needs refactoring
			}
		} else if (type.isAssignableFrom(Boolean.class) &&  value instanceof String) {
			value = Boolean.parseBoolean((String)value);
		} else if (type.isAssignableFrom(Color.class) &&  value instanceof String) {
			value = ColorProvider.getColor((String)value);
		}
		properties.put(key, value);
		fireSetEvent(key);
	}

	private void fireSetEvent(Object key) {
		int i=0;
		if (eventOn) {
			for (ActionListener l : listeners) {
				event[0] = key;
				event[1] = this;
				event[2] = sources.get(i++);
				l.actionPerformed(new ActionEvent(event, 0, null));
			}
		}
	}
	
	public void addListener(Object source, ActionListener al) {
		sources.add(source);
		listeners.add(al);
	}
	
	public void removeListener(ActionListener al) {
		int index = listeners.indexOf(al);
		if (index >= 0) {
			sources.remove(index);
			listeners.remove(index);
		}
	}

	public void setEventOn(boolean on) {
		eventOn = on;
	}

	
	public void setId(Object value) {
		set(fields.get(idIndex), value);
	}

	static class ConstraintKey {
		Object value;
		String description;
		public ConstraintKey(Object value, String desc) {
			this.value = value;
			this.description = desc;
		}
	}

	//
	//   ITableRow interface for JTable support
	//

	public String getColumnName(int column) {
		return fields.get(column);
	}

	public Object getValueAt(int column) {
		String key = getColumnName(column);
		return getFieldValue(key);
	}

	public int getColumnCount() {
		return fields.size();
	}

	// private state getters
	private HashMap<String, Object> getProperties() {
		return properties;
	}

	private HashMap<String, Class<?>> getTypes() {
		return types;
	}

	private HashMap<String, ConstraintKey[]> getConstraintKeys() {
		return constraintKeys;
	}

	private List<String> getEditableFields() {
		return editableFields;
	}

	public int getIdIndex() {
		return idIndex;
	}
	
	/**
	 * @param file Properies file name
	 * @throws IOException
	 */
	public void loadFromFile(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		Properties props = new Properties();
		FileReader reader = new FileReader(file);
		props.load(reader);
		reader.close();
		Set<Object> keys = props.keySet();
		for (Object k : keys) {
			properties.put((String)k, props.get(k));
		}
		reader.close();
	}

	/**
	 * @param file
	 * @throws IOException
	 */
	public void saveToFile(File file) throws IOException {
		Properties props = new Properties();
		for (Object key : properties.keySet()) {
			Object value = properties.get(key);
			if (value != null && typeSupported(value.getClass())) {
				props.put(key, value.toString());
			}
		}
		FileOutputStream out = new FileOutputStream(file);
		props.store(out, "Properties");
		out.close();
	}
	
	public void saveAsUserPref(String prefix) {
		removeFromUserPref(prefix);
		Preferences pref = Preferences.userRoot();
		pref.put(prefix, String.valueOf(System.currentTimeMillis()));
		for (Object key : properties.keySet()) {
			Object value = properties.get(key);
			if (value != null && typeSupported(value.getClass())) {
				pref.put(prefix+key, value.toString());
			}
		}
	}

	public void removeFromUserPref(String prefix) {
		Preferences pref = Preferences.userRoot();
		pref.remove(prefix);
		for (String key : properties.keySet()) {
			pref.remove(prefix+key);
		}
	}
	
	public boolean loadFromUserProps(String prefix) {
		boolean loaded = false;
		Preferences pref = Preferences.userRoot();
		String value = pref.get(prefix, null);
		if (value != null) {
			loaded = true;
			for (String key : properties.keySet()) {
				Class<?> type = types.get(key);
				if  (typeSupported(type)) {
					set(key, pref.get(prefix+key, null));
				}
			}
		} 
		return loaded;
	}

	public static boolean typeSupported(Class<?> c) {
		if (c == null) {
			return true;
		}
		for (Class<?>  t : TYPES) {
			if (t.isAssignableFrom(c)) {
				return true;
			}
		}
		return false;
	}

	public static void main(String[] args) {
		BasicPropertyItem bi = new BasicPropertyItem();
		
		System.out.println(bi.typeSupported(Integer.class));
		System.out.println(bi.typeSupported(String.class));
		System.out.println(bi.typeSupported(Boolean.class));
		System.out.println(bi.typeSupported(Number.class));
		System.out.println(bi.typeSupported(Double.class));
		System.out.println(bi.typeSupported(BasicPropertyItem.class));
		
		File f = new File("c:/data/genes.gor");
		String fileName = "AS_"+f.getName().toLowerCase();
		bi.loadFromUserProps(fileName);
		List<Object> vals = bi.getAllValues();
		for (Object v: vals) {
			System.out.println(v);
		}
	}
	
}
