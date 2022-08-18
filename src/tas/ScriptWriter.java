package tas;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptWriter {

 private File in;
 private File out;
 private boolean includeEmptyLines;
 
 private static Pattern math = Pattern.compile("(-?[\\.0-9]+) *([+-]) *(-?[\\.0-9]+)");
 private static Pattern variable = Pattern.compile("\\$\\w*");
 
 private TreeMap<String, String> replace = new TreeMap<String, String>();
 
 public ScriptWriter(File in, File out, boolean includeEmptyLines) {
	 this.in = in;
	 this.out = out;
	 this.includeEmptyLines = includeEmptyLines;
 }
 
 public String writeFile() {
	
	long start = System.currentTimeMillis();
	
	File destination = null;
	
	Scanner scan = null;
	try {
		scan = new Scanner(in);
	}
	catch (FileNotFoundException e) {
		return "TSV file not found.";
	}

	PrintWriter print = null;
	try {
		destination = new File(out + ".tmp");
		print = new PrintWriter(destination);
	}
	catch (FileNotFoundException e) {
		scan.close();
		return "Output destination not found.";
	}
	
	int line = 1;
	String first = "";
	while (scan.hasNextLine()) {
		Scanner s = new Scanner(scan.nextLine()).useDelimiter("\t");
		boolean durationWritten = false;
		int duration = 1;
		if (s.hasNextInt()) {
			duration = s.nextInt();
			durationWritten = true;
		}
		else if (s.hasNext()) {
			first = s.next();
			if (first.length() >= 2 && first.substring(0, 2).equals("//")) {
				continue;
			}
			else if (first.length() > 0 && first.charAt(0) == '$' && first.contains("=")) {
				replace.put(first.substring(0, first.indexOf("=")).trim(), prepareToken(first.substring(first.indexOf("=") + 1).trim(), replace));
				continue;
			}
		}
		Line ln = new Line(duration);
		//if there was no duration number we already got the first input and need to add it to the line
		if (!durationWritten) {
			try {
				ln.add(prepareToken(first, replace));
			}
			catch (Exception e) {
				s.close();
				scan.close();
				print.close();
				destination.delete();
				return "Syntax errors in TSV prevented file generation.";
			}
		}
		while (s.hasNext())
			try {
				ln.add(prepareToken(s.next(), replace));
			}
			catch (Exception e) {
				s.close();
				scan.close();
				print.close();
				destination.delete();
				return "Syntax errors in TSV prevented file generation.";
			}
		if (!includeEmptyLines && ln.isEmpty())
			line += duration;
		else
			for (int i = 0; i < duration; i++, line++)
				print.println(line + " " + ln.get(i));
	}
	scan.close();
	print.close();
	
	File existing = out;
	existing.delete();
	destination.renameTo(existing);
	
	DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
	
	long end = System.currentTimeMillis();
	long time = end - start;
	return "File successfully generated in " + time + " ms at " + timeFormat.format(LocalTime.now()) + ".";
 }

	private static String prepareToken(String token, TreeMap<String, String> replace) {
		//variables
		Matcher variableMatcher = variable.matcher(token);
		while (variableMatcher.find()) {
			token = token.replaceFirst("\\" + variableMatcher.group(0), replace.get(variableMatcher.group(0)));
		}
		
		//math
		Matcher mathMatcher;
		while ((mathMatcher = math.matcher(token)).find()) {
			boolean add = mathMatcher.group(2).equals("+");
			double num1 = Double.parseDouble(mathMatcher.group(1));
			double num2 = Double.parseDouble(mathMatcher.group(3));
			double total;
			if (add)
				total = num1 + num2;
			else
				total = num1 - num2;
			if (total == (int) total) //if the total can be written as an integer, write it as such
				token = token.replace(mathMatcher.group(0), Integer.toString((int) total));
			else
				token = token.replace(mathMatcher.group(0), Double.toString(total));
		}
		
		return token.toLowerCase();
	}
}
