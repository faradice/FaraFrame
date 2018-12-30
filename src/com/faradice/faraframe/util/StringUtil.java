package com.faradice.faraframe.util;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

/*
 * Copyright (c) 2005 deCODE Genetics Inc.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * deCODE Genetics Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with deCODE.
 */

/**
 * A collection of static string utility routines
 */
public class StringUtil {
    /**
     * Appends white spaces to a string
     * @param str the start string
     * @param length the desired length
     * @return the string padded with spaces upto length long
     */
    public static String appendSpace(String str, int length) {
        StringBuilder s = new StringBuilder(str);
        while (s.length() < length) s.append(' ');
        return s.toString();
    } // leadingZeros
    
    /**
     * Convert the stack trace of the specified exception into a single string object
     * @param ex The exception
     * @return The string representation of the exception 
     */
    public static String stacktrace2String(Throwable ex) {
        final CharArrayWriter stackTrace = new CharArrayWriter();
        printStackTrace(ex, new PrintWriter(stackTrace));
        return new String(stackTrace.toCharArray());
    }
    
    /**
     * Prints the exception stacktrace and its backtrace to the specified print stream.
     *
     * @param exception the exception to list
     * @param s <code>PrintWriter</code> to use for output
     */
    private static void printStackTrace(Throwable exception, PrintWriter s) {
        synchronized (s) {
            s.println(exception.toString());
            StackTraceElement[] trace = exception.getStackTrace();
            for (StackTraceElement element : trace)
                s.println("\tat " + element);

            Throwable ourCause = exception.getCause();
            if (ourCause != null) {
                s.print("Caused by: ");
                printStackTrace(ourCause, s);
            }
        }
    }
    
    /**
     * @param str The string to extract an integer from
     * @return The integer assumed to be at the end of the string.
     * @throws NumberFormatException if no int value is found at the end of the string 
     */
    public static int extractTrailingInt(String str) {
    	int begin = str.length();
    	while (begin > 0 && Character.isDigit(str.charAt(begin-1))) {
    		begin--;
    	}
    	if (begin == str.length()) {
    		throw new NumberFormatException("Excpected a integer at the end of the input string: " + str);
    	}
    	return Integer.parseInt(str.substring(begin));
    }


    /**
     * Convert a number to a string left padded with zeros upto length
     * Example if length is 5 and number 20 the method returns 00020
     * @param number the number to convert to string
     * @param length the length of the resulting string
     * @return the formatted number
     */
    public static String leadingZeros(int number, int length) {
        StringBuffer s = new StringBuffer(String.valueOf(number));
        while (s.length() < length) s.insert(0, '0');
        return s.toString();
    }

    /**
     * Convert a string to a left padded string up to length long
     * @param str the string to convert
     * @param length the desired length
     * @return the formatted string
     */
    public static String leadingZeros(String str, int length) {
        StringBuffer s = new StringBuffer(str);
        while (s.length() < length) s.insert(0, '0');
        return s.toString();
    }

    /**
     * Trim all leading zeros from a string
     * @param s the string to trim
     * @return the string with leading zeros removed
     */
    public static String trimLeadingZeros(String s) {
        if (isValidInt(s)) {
            return Integer.valueOf(s).toString();
        }
        return s;
    }

    /**
     * Trim all zeros from a string, both leading and trailing.
     * @param str the string to remove the zeros from
     * @return the input string with all zeros removed.
     */
    public static String trimZeros(String str) {
        StringBuffer s = new StringBuffer(str);
        int length = s.length() - 1;
        while (length >= 0) {
            if (s.charAt(length) == '0') {
                s.deleteCharAt(length);
            }
            length--;
        }
        return s.toString();
    }

    /**
     * Check if a string holds a valid integer value
     * @param s the string to check
     * @return true if the string can be converted to a int
     */
    public static final boolean isValidInt(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException n) {
            return false;
        }
        return true;
    }

    /**
     * Check if a string contains only digits
     * @param s the string to check
     * @return true if the characters in the string are all digits
     */
    public final static boolean isAllDigit(String s) {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isDigit(s.charAt(i)))
                return false;

        return true;
    }
    
    /**
     * Check if a string contains only letters
     * @param s the string to check
     * @return true if the characters in the string are all letters
     */
    public final static boolean isAllLetter(String s) {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isLetter(s.charAt(i)))
                return false;

        return true;
    }
    
    /**
     * Check if a string contains no numerical characters
     * @param s the string to check
     * @return true if no characters in the string are digits
     */
    public final static boolean hasNoDigits(String s) {
        for (int i = 0; i < s.length(); i++)
            if (Character.isDigit(s.charAt(i)))
                return false;

        return true;
    }
    

    /**
     * Compares two strings, with regards to whether they include numbers.
     * When used instead of s1.compareTo(s2) when ordering strings, "String 9" will precede "String 10".
     * @param s1 First String to compare
     * @param s2 Second String to compare
     * @return A number > 0 if s1 is precedes s2, less than 0 if s2 precedes s1, 0 if they are equal
     */
    public final static int compareNumStrings(String s1, String s2) {
    	int lesserLength = Math.min(s1.length(), s2.length());
		StringBuffer s1Buffer = new StringBuffer("0");
		StringBuffer s2Buffer = new StringBuffer("0");
    	int s1Number = 0;
    	int s2Number = 0;
    	int index = 0;
    	while (index < lesserLength-1 && s1.charAt(index) == s2.charAt(index)) {
    		int s1Index = index+1;
    		while (s1Index < s1.length()-1 && Character.isDigit(s1.charAt(s1Index)) ) {
    			s1Buffer.append(s1.charAt(s1Index++));
    		}
    		int s2Index = index+1;
    		while (s2Index < s2.length()-1 && Character.isDigit(s2.charAt(s2Index))) {
    			s2Buffer.append(s2.charAt(s2Index++));
    		}
    		if (s1Buffer.length() > 1 || s2Buffer.length() > 1) {
    			s1Number = Integer.parseInt(s1Buffer.toString());
    			s2Number = Integer.parseInt(s2Buffer.toString());
    			if (s1Number != s2Number) {
    				return s1Number - s2Number;
    			}
    			s1Buffer = new StringBuffer("0");
    			s2Buffer = new StringBuffer("0");
    			index = Math.max(s1Index, s2Index);
    		}
    		else {
    			index++;
    		}
    	}
    	return s1.compareTo(s2);
    }
    
    /**
     * Converts an int number between 0 and 999 to text representation.
     * Numbers greater than 999 are not converted, only returned as string.
     * @param number The number to display as text
     * @param capitalizeFirst Flag for indicating that first character of the return string should be Capitalized
     * @return Text version of the provided number, e.g. "six hundred sixty-six"
     */
    public static String toText(int number, boolean capitalizeFirst) {
    	String[] min20 = {"no", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
	            		  "eleven","twelve","thirteen","fourteen","fifteen","sixteen","seventeen","eigtheen","nineteen"};    	
    	String[] desi = {"","","twenty","thirty","fourty","fifty","sixty","seventy","eighty","ninety"};
    	String ret = "" + number;
    	if (number < 20) {
    		ret = min20[number];
    	}
    	else if (number < 100) {
    		ret = desi[number / 10] + (number % 10 > 0 ? "-" + min20[number % 10] : ""); 
    	}
    	else if (number < 1000) {
    		ret = min20[number / 100] + " hundred " + 
    		     (number % 100 > 19 ? desi[number % 100 / 10] + (number % 100 % 10 > 0 ? "-" + min20[number % 1000 % 10] : "") : (number % 100 % 20 > 0 ? min20[number % 100 % 20] : ""));    		       
    	}
    	if (capitalizeFirst) {
    		return ret.substring(0, 1).toUpperCase() + ret.substring(1);
    	}
    	return ret;
    }
    
    /**
     * Converts elapsed milliseconds to string of minutes and hours.
     * If time is 'less than one minute' or 'more than 24 hours', those strings are returned
     * @param time
     * @param includeHave Flag for indicating if the verb 'have' is to be added to the time string
     * @return Human readable text: hh hour(s), mm minute(s) [have/has]
     */
    public static String long2minHours(long time, boolean includeHave) {
    	String[] numbers = {"no", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
    			            "11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27",
    			            "28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43",
    			            "44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59"};
    	String singular = " has";
    	String plural   = " have";
    	if (!includeHave) {
    		singular = "";
    		plural = "";
    	}
    	if (time < 60000) {
    		return "less than one minute" + singular;
    	}
    	long totalMinutes = time / 60000;
    	int minutes = (int)totalMinutes % 60;
    	int hours = (int)totalMinutes / 60;
    	String ret = "";
    	if (hours > 24) {    		
    		return "more than 24 hours" + plural;
    	}
    	if (hours > 0) {
    		ret = numbers[hours] + " hour";    		   		
    		if (hours > 1) {
    			ret += "s";
    			if (minutes == 0) {
    				ret += plural;
    			}
    		}
    		else {
    			if (minutes == 0) {
    				ret += singular;
    			}
    		}
    	}
    	if (minutes > 0) {
    		if (hours > 0) {
    			ret += ", ";
    		}    		
    		ret += numbers[minutes] + " minute";
    		if (minutes > 1) {
    			ret += "s" + plural;
    		}
    		else {
    			ret += singular;    		 
    		}
    	}
    	
    	return ret;
    }

    /**
     * Format a timestamp to a date string
     * @param date the time stamp value to convert
     * @return the timestamp formated as dd.mm.yyyy hh:mm
     */
    public static String dateTimeStr(java.sql.Timestamp date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        return formatter.format(date);
    }

    /**
     * Format a java sql date to string
     * @param date the time stamp value to convert
     * @return the timestamp formated as dd.mm.yyyy hh:mm:ss
     */
    public static String dateTimeStr(java.sql.Date date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return formatter.format(date);
    }

    /**
     * Convert a date to a string
     * @param date the date to convert
     * @return the date formatted as dd.mm.yyyy
     */
    public static String dateStr(java.sql.Date date) {
        if (date == null) return "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(date);
    }

    /**
     * Cuts of the beginning of a string if it is longer that a certain length
     * @param s The string
     * @param maxLength
     * @return a new String that is not longer than maxLength
     */
    public static String cutBeginning(String s, int maxLength) {
        if (s.length() < maxLength) {
            return s;
        }
        return s.substring(s.length() - maxLength);
    }

    /**
     * Enumerates an array of strings from 0 to array.length-1
     * @param array
     * @return An map of each string to its index
     */
    public static HashMap<String, Integer> enumerate(String[] array) {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < array.length; i++) {
            map.put(array[i], Integer.valueOf(i));
        }
        return map;
    }

    /**
     * Enumerates the elements in a set from 0 to array.length-1
     * @param <E>
     * @param set a set with numbered object
     * @return the set
     */
    public static <E extends Object> HashMap<E, Integer> enumerate(HashSet<E> set) {
        final HashMap<E, Integer> map = new HashMap<E, Integer>();
        int index = 0;
        for (E entry : set) {
            map.put(entry, index++);
        }
        return map;
    }

    /**
     * Query if the provided string is a valid double number
     * NOTE calling this method frequently is not optimal since it will using Double.parseDouble and catch exception is rather slow
     * @param s The string to check
     * @return True if string is a valid double
     */
    public static final boolean isValidDouble(String s) {
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException n) {
            return false;
        }
        return true;
    }

    /**
     * Query if the provided string is a valid date
     * @param s The string to check
     * @return True if string is a valid date
     */
    public static boolean isValidDate(String s) {
        try {
            return (DateFormat.getDateInstance().parse(s) != null);
        } catch (ParseException p) {
            return false;
        }
    }

    /**
     * Join a collection of strings (or toString() result of objects) using a
     * separator.
     * @param seperator Separator
     * @param items Collection of items.
     * @return joined string.
     */
    public static String join(String seperator, Iterable<?> items) {
        if (items == null) return null;
        StringBuffer result = new StringBuffer();
        join(seperator, items, result);
        return result.toString();
    }

    /**
     * Join a collection of strings (or toString() result of objects) using a
     * seperator.
     *
     * @param seperator  Separator
     * @param items Collection of items.
     * @param result Buffer to add joined string to.
     */
    public static void join(String seperator, Iterable<?> items, Appendable result) {
        try {
            if (items == null)
                return;
            Iterator<?> iter = items.iterator();
            for (int i = 0; iter.hasNext(); i++) {
                Object item = iter.next();
                if (i > 0)
                    result.append(seperator);
                result.append(item.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Join an array of strings (or toString() result of objects) using a
     * separator.
     * @param seperator Separator
     * @param items Array of items.
     * @return joined string.
     */
    public static String join(String seperator, Object... items) {
        if (items == null) return null;
        return join(seperator, Arrays.asList(items));
    }
    
    /**
     * Join an array of strings (or toString() result of objects) using a
     * separator.
     * @param seperator Separator
     * @param items Array of items.
     * @return joined string.
     */
    public static String join(String seperator, String... items) {
        if (items == null) return null;
        return join(seperator, Arrays.asList(items));
    }
    
    /**
     * Split the specified line into substring based on the specified separator
     * @param line The line to split
     * @param sep The separator to use
     * @return ArrayList of the substrings
     */
    public static ArrayList<String> split(String line, char sep) {
    	return split(line, 0, sep);
    }

    /**
     * Split the specified line into substring based on the specified separator
     * @param line The line to split
     * @param startPos The starting position to split from
     * @param sep The separator to use
     * @return ArrayList of the substrings
     */
    public static ArrayList<String> split(String line, int startPos, char sep) {
    	final ArrayList<String> list = new ArrayList<String>();
    	int lastPos = startPos;
    	while (true) {
	        final int pos = line.indexOf(sep, lastPos);
	        if (pos > -1) {
	        	list.add(line.substring(lastPos, pos));
	        	lastPos = pos+1;
	        } else {
	            list.add(line.substring(lastPos));
	            return list;
	        }
    	}
    }    
    
    /**
     * Split the specified line into substring assuming tab char as separator
     * @param line The line to split
     * @return ArrayList of the substrings
     */
    public static ArrayList<String> split(String line) {
    	return split(line, '\t');
    }
    
    /**
     * Split the specified line into substring assuming tab char as separator
     * @param line The line to split
     * @return Array of the substrings
     */
    public static String[] splitToArray(String line) {
    	final ArrayList<String> list = StringUtil.split(line);
    	return list.toArray(new String[list.size()]);
    }
    
    /**
     * Split the specified line into substring assuming tab char as separator
     * @param line The line to split
     * @param sep The separator to use
     * @return Array of the substrings
     */
    public static String[] splitToArray(String line, char sep) {
    	final ArrayList<String> list = StringUtil.split(line, sep);
    	return list.toArray(new String[list.size()]);
    }
    
    
    

    /**
     * Join an array of strings (or toString() result of objects) using a
     * seperator.
     *
     * @param seperator  Separator
     * @param items Array of items.
     * @param result Buffer to add joined string to.
     */
    public static void join(String seperator, Object[] items, Appendable result) {
        if (items == null) return;
        join(seperator, Arrays.asList(items), result);
    }

    /**
     * Parse patterns from string using regular expressions.
     * Example:
     *   StringUtil.parse("File: abc.txt  Size: 300","File:\\s+(\\w+)\\s+Size:\\s+([0-9]+)")
     *   returns string array with {"abc.txt","300"}
     *
     * @param str String to parse from
     * @param pattern Pattern describing string format
     * @return Array of Strings, one for each matched group in pattern.
     */

    public static String[] parse(String str,String pattern) {
        Matcher m = Pattern.compile(pattern).matcher(str);
        int n = 0;
        if (m.matches()) {
            n = m.groupCount();
        }
        String[] result = new String[n];
        for (int i=0;i<n;i++) {
            result[i] = m.group(i+1);
        }
        return result;
    }

    /**
     * Return parent part of a path by removing separator and leaf name.
     * Example: parent("a/b/c","/") returns "a/b".  parent("noslash","/") returns null.
     *
     * @param path Path.
     * @param seperator Path separator
     * @return Parent name or null if no parent.
     */
    public static String parent(String path, String seperator) {
    	if(path == null) return null;
    	int lastPos = path.lastIndexOf(seperator);
    	if(lastPos < 0) {
    		return null;     // A root node
    	}
        return path.substring(0, lastPos);
    }


    /**
     * Return parent part of a dot separated path by removing separator and leaf name.
     * Example: parent("a.b.c") returns "a.b".  parent("nodot") returns null.
     *
     * @param path Path.
     * @return Parent name or null if no parent.
     */
    public static String dotParent(String path) {
        return parent(path,".");
    }


    /**
     * Return last (leaf) part of path.
     * Example: leaf("p1.p2.p3",".") returns "p3".  leaf("noDots",".") returns "noDots".
     * @param path path.
     * @param separator Path separator
     * @return last part of path.
     */
    public static String leaf(String path, String separator) {
    	if(path == null) return null;
    	int lastDot = path.lastIndexOf(separator);
        // Note if dot not found, lastDot will be -1 - full string is returned.
    	return path.substring(lastDot + 1);
    }



    /**
     * Return last (leaf) part of dot separated path.
     * Example: leaf("p1.p2.p3") returns "p3".  dotLeaf("noDots") returns "noDots".
     * @param dotPath dot separated path.
     * @return last part of path.
     */
    public static String dotLeaf(String dotPath) {
        return leaf(dotPath,".");
    }

    /**
     * Return head of a path by removing separator and tail.
     * Example:   head("a/b/c","/") returns "a".  head("noslash","/") returns "noslash";
     * @param path Path
     * @param separator Path separator
     * @return Head or null if path is null.
     */
    public static String head(String path,String separator) {
        if (path == null) return  null;
        int firstDot = path.indexOf(separator);
        if (firstDot >=0) {
            return path.substring(0,firstDot);
        }
        return path;
    }

    /**
     * Return head of a dot separated pathl.
     * Example:   dotHead("a.b.c") returns "a".  head("nodot) returns "nodot";
     * @param path Path
     * @return Head or null if path is null.
     */
    public static String dotHead(String path) {
        return head(path,".");
    }


    /**
     * Return tail of a path.
     * Example:  tail("a/b/c","/") returns "b/c".  tail("noslash","/") returns null;
     * @param path Path
     * @param separator Path separator
     * @return Tail or null if path is null or same as head.
     */
    public static String tail(String path,String separator) {
        if (path == null) return  null;
        int firstDot = path.indexOf(separator);
        if (firstDot >=0) {
            return path.substring(firstDot+1);
        }
        return null;
    }

    /**
     * Return tail of a dot separated pathl.
     * Example:   dotTail("a.b.c") returns "b.c".  diotTail("nodot) returns null;
     * @param path Path
     * @return Tail  or null if path is null or same as head.
     */
    public static String dotTail(String path) {
        return tail(path,".");
    }


    /**
     * Join two strings with dot.
     * Example:  dotJoin("a.b.c","d") returns "a.b.c.d".  dotJoin(null,"leaf") returns "leaf".
     * @param parent Parent path.
     * @param child Child name.
     * @return dot separated path.
     */
    public static String dotJoin(String parent,String child) {
        if (parent == null) return child;
        if (child == null) return parent;
        return join(".",(Object[])new String[] {parent,child});
    }

    /**
     * Find (distictly) all strings in a that are not in b
     * @param a String array
     * @param b String array
     * @return An array with the distinct strings from a not in b
     */
    public static String[] diff(String[] a, String[] b) {
        HashSet<String> diff = new HashSet<String>();
        HashSet<String> bstrings = new HashSet<String>();
        Collections.addAll(bstrings, b);
        for (String s : a) {
            if (!bstrings.contains(s))
                diff.add(s);
        }

        return diff.toArray(new String[diff.size()]);
    }

    
    /**
     * Base64 encode binary data.  Data is encoded using printable ascii characters.
     * @param data Input data
     * @return Base64 encoded string.
     */
/*    
    public static String base64Encode(byte[] data) {
        return new BASE64Encoder().encode(data);
    }
*/
    /**
     * Decode base64 encoded data.
     * @param base64 Base64 encoded string.
     * @return Original data.
     */
    /*    
    public static byte[] base64Decode(String base64) {
        try {
            return new BASE64Decoder().decodeBuffer(base64);
        } catch (IOException e) {
            throw new RuntimeException("Should not happen");
        }
    }
    */

    /**
     * Create a sentence from a 'programmer' string.  This will take strings like "firstValue" or "BASE_QUERY" and
     * return "First Value" and "Base Query" respectively.  Word boundaries are detected on switch from lower to upper
     * case or underscore/whitespace.  All words are capitalized.
     * @param name Camel case or underscored name.
     * @return Sentence
     */
    public static String toSentence(String name) {
        if (name == null) return null;
        name = name.trim();
        StringBuilder result = new StringBuilder();
        boolean cap = true;
        boolean lastUpper = true;
        for (int i=0;i<name.length();i++) {
            char c = name.charAt(i);
            if (c == '_' || c == ' ' || c == '\t' || c == '\n') {
                result.append(' ');
                cap = true;
            } else {
                boolean upper = false;
                if (c == Character.toUpperCase(c)){
                    upper = true;
                }
                if (!lastUpper && upper) {
                    result.append(' ');
                    cap = true;
                }
                if (cap) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
                cap = false;
                lastUpper = upper;
            }
        }

        return result.toString().replaceAll("\\s+"," ");
    }

    /**
     * Create a Camel case name from a sentence.
     * This will take strings like "First value" or " To  Camel case " and
     * return "firstValue" and "toCamelCase".
     * Note that the expression toCamelCase(toSentence(name)) should return the original
     * name if it is in camel case to start with (the reverse is not neccesarily true).
     * @param sentence Sentence
     * @return Camel case name.
     */
    public static String toCamelCase(String sentence) {
        if (sentence == null) return null;
        String n = sentence.trim();
        StringBuilder result = new StringBuilder();
        boolean cap=false;

        for (int i=0;i<n.length();i++) {
            char c = n.charAt(i);
            if (Character.isWhitespace(c)){
                cap = true;
            } else {
                if (cap) {
                    result.append(Character.toUpperCase(c));
                } else {
                    result.append(Character.toLowerCase(c));
                }
                cap = false;
            }
        }
        return result.toString();
    }

    /**
     * Create an identifier from input string.
     * The result will only contain upper-case alphanumerics and underscore ( i.e matching [A-Z0-9_]*).
     *
     * Details:
     * Trims leading/trailing spaces.  Converts other spaces/non-alphanunerical characters to underscores.
     * Underscores are also added on switch from lower to upper case.
     * Result will be all uppercase.
     *
     * Example:  "My String" -> "MY_STRING"
     *           "firstValue" -> "FIRST_VALUE"
     * @param sentence Input
     * @return Identifier.
     */
    public static String toIdentifier(String sentence) {
    	sentence = sentence.trim();
    	sentence = sentence.replaceAll("[^a-zA-Z0-9_]+", "_");
    	StringBuilder result = new StringBuilder();

    	boolean addOnUpper = false;
    	for (int i=0;i<sentence.length();i++) {
    		char c = sentence.charAt(i);
    		if (c == '_') {
    			addOnUpper = false;
    		} else if (addOnUpper && Character.toUpperCase(c) == c) {
    			addOnUpper = false;
    			result.append('_');
    		} else if (!addOnUpper && Character.toLowerCase(c) == c) {
    			addOnUpper = true;
    		}
    		result.append(c);
    	}
    	return result.toString().toUpperCase();
    }

    /**
     * Test if string starts with regex pattern.
     * If string is long, this is more efficient than adding .* to pattern.
     * @param s String to test.
     * @param pattern Regex pattern to match.
     * @return True if string starts with patterns.
     */
    public static boolean startsWithPattern(String s,String pattern) {
        String fixedPattern = pattern;
        if (pattern.length() == 0 || pattern.charAt(0) != '^')  {
            fixedPattern = "^"+fixedPattern;
        }
        Matcher matcher = Pattern.compile(fixedPattern).matcher(s);
        if (matcher.find()) return true;
        return false;
    }

    /**
     * If given null value return empty string, else return the given string
     * @param st The string to work with (can be null)
     * @return The non null string
     */
    public static String toNotNullString(String st){
        return st == null ? "" : st;
    }
    
    /**
     * Removes all whitespace and non-printable characters from the input string
     * @param in The string to prune
     * @return The pruned string
     */
    public static String prune(String in) {
    	StringBuilder sb = new StringBuilder();
    	for (int i = 0; i < in.length(); i++) {
			char c = in.charAt(i);
    		if (!Character.isWhitespace(c)) {
    			sb.append(c);
    		}
		}
    	return sb.toString();
    }
    
    /**
     * Truncate string to max length.  
     * 
     * @param in Input string
     * @param maxLength Maximum length
     * @param trailingDots  If true, resulting string will have "..." at the end if it was truncated.
     * @return A String that is no longer than maxLength
     */
    public static String truncate(String in,int maxLength,boolean trailingDots) {
        if (in == null) return null;
        if (in.length() <= maxLength) return in;
        if (trailingDots) return in.substring(0, maxLength-3)+"...";
        return in.substring(0, maxLength);
    }
    
    
    /**
     * Quote all strings
     * @param in Strings
     * @return Quoted strings
     */
    public static String [] quote(Object... in) {
    	String[] result = new String[in.length];
    	for (int i=0;i<in.length;i++) {
    		result[i]= "\""+in[i]+"\"";
    	}
    	return result;
    }
    
	/**
	 * @param s
	 * @return true for true, yes, y
	 */
	public static boolean isTrue(String s) {
		if (s == null) {
			return false;
		}
		s = s.trim();
		return (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y"));
	}

    /**
     * Encapsulate text with html delimiters and replace newline with html line break.
     * Useful to create multiline tooltip text.
     * 
     * @param s input string
     * @return html string
     */
    public static String toMultilineHtml(String s) {
        return "<html>" + s.replaceAll("[\n\r]+", "<br>") + "</html>";
    }

}
