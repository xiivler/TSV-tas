package tas;

import java.util.ArrayList;
import java.util.HashMap;

public class Line {

	int[] ls_x = {0};
	int[] ls_y = {0};
	int[] rs_x = {0};
	int[] rs_y = {0};
	
	int duration;
	
	private static final boolean LS = true;
	private static final boolean RS = false;
	
	private boolean empty = true;
	
	private static HashMap<String, String> encode = new HashMap<String, String>();
	
	static {
		encode.put("a", "KEY_A");
		encode.put("b", "KEY_B");

		encode.put("x", "KEY_X");
		encode.put("y", "KEY_Y");

		encode.put("zr", "KEY_ZR");
		encode.put("zl", "KEY_ZL");

		encode.put("r", "KEY_R");
		encode.put("l", "KEY_L");

		encode.put("plus", "KEY_PLUS");
		encode.put("+", "KEY_PLUS");
		encode.put("minus", "KEY_MINUS");
		encode.put("-", "KEY_MINUS");

		encode.put("dp-l", "KEY_DLEFT");
		encode.put("dp-r", "KEY_DRIGHT");
		encode.put("dp-u", "KEY_DUP");
		encode.put("dp-d", "KEY_DDOWN");
		
		encode.put("ls", "KEY_LSTICK");
		encode.put("rs", "KEY_RSTICK");
	}
	
	ArrayList<String[]> inputs = new ArrayList<String[]>();
	
	public Line(int duration) {
		this.duration = duration;
	}
	
	public void add(String s) {	
		//interpolation
		if (s.contains("->")) {
			if (s.contains("ls("))
				addInterpolatedStick(LS, s);
			if (s.contains("rs("))
				addInterpolatedStick(RS, s);
		}
		
		//loops
		else if (s.contains("/")){
			empty = false;
			if (s.endsWith("/"))
				s += " ";
			String[] val = s.split("/");
			if (s.contains("ls(")) {
				ls_x = new int[val.length];
				ls_y = new int[val.length];
			}
			if (s.contains("rs(")) {
				rs_x = new int[val.length];
				rs_y = new int[val.length];
			}
			for (int i = 0; i < val.length; i++) {
				String input = val[i];
				if (input.contains("ls("))
					addStick(LS, i, input);
				else if (input.contains("rs("))
					addStick(RS, i, input);
				val[i] = encode.get(input);
			}
			inputs.add(val);
		}
		
		else {
			if (s.contains("ls("))
				addStick(LS, 0, s);
			else if (s.contains("rs("))
				addStick(RS, 0, s);
			else {
				String str = encode.get(s);
				if (s != null) {
					String[] val = {str};
					inputs.add(val);
				}
			}
		}	
	}
	
	private void addStick(boolean stick, int step, String s) {
		//s = s.substring(3, s.length()-1);
		s = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
		double r = 1;
		double theta = 0;
		int x = 0;
		int y = 0;
		
		//Cartesian coordinates
		if (s.contains(",")) {
			x = Integer.parseInt(s.substring(0, s.indexOf(',')).trim());
			y = Integer.parseInt(s.substring(s.indexOf(',') + 1).trim());
		}
		
		//Polar coordinates
		else {
			if (s.contains(";")) {
				r = Double.parseDouble(s.substring(0, s.indexOf(';')).trim());
				theta = Math.toRadians(Double.parseDouble(s.substring(s.indexOf(';') + 1).trim()));
			}
			else
				theta = Math.toRadians(Double.parseDouble(s));
			x = (int) (32767 * r * Math.cos(theta));
			y = (int) (32767 * r * Math.sin(theta));
		}
		
		if (stick == LS) {
				ls_x[step] = x;
				ls_y[step] = y;
			}
		else {
			rs_x[step] = x;
			rs_y[step] = y;
		}
	}
	
	private void addInterpolatedStick(boolean stick, String s) {
		String angle1 = s.substring(0, s.indexOf(">") - 1).trim();
		String angle2 = s.substring(s.indexOf(">") + 1).trim();
		angle1 = angle1.substring(angle1.indexOf("(") + 1, angle1.indexOf(")"));
		angle2 = angle2.substring(angle2.indexOf("(") + 1, angle2.indexOf(")"));
		
		double r1 = 1;
		double r2 = 1;
		double theta1 = 0;
		double theta2 = 0;
		int x1 = 0;
		int x2 = 0;
		int y1 = 0;
		int y2 = 0;
		double dx, dy, dr, dtheta;
		int[] x = new int[duration];
		int[] y = new int[duration];
		
		//Cartesian coordinates
		if (s.contains(",")) {
			x1 = Integer.parseInt(angle1.substring(0, angle1.indexOf(',')).trim());
			x2 = Integer.parseInt(angle2.substring(0, angle2.indexOf(',')).trim());
			y1 = Integer.parseInt(angle1.substring(angle1.indexOf(',') + 1).trim());
			y2 = Integer.parseInt(angle2.substring(angle2.indexOf(',') + 1).trim());
			double x_current = x1;
			double y_current = y1;
			dx = ((double) x2 - x1)/(duration - 1);
			dy = ((double) y2 - y1)/(duration - 1);
			
			for (int i = 0; i < duration - 1; i++) {
				x[i] = (int) x_current;
				x_current += dx;
			}
			x[duration - 1] = x2;
			
			for (int i = 0; i < duration - 1; i++) {
				y[i] = (int) y_current;
				y_current += dy;
			}
			y[duration - 1] = y2;
		}
		
		//Polar coordinates
		else {
			if (angle1.contains(";")) {
				r1 = Double.parseDouble(angle1.substring(0, angle1.indexOf(';')).trim());
				theta1 = Math.toRadians(Double.parseDouble(angle1.substring(angle1.indexOf(';') + 1).trim()));
			}
			else
				theta1 = Math.toRadians(Double.parseDouble(angle1));
			
			if (angle2.contains(";")) {
				r2 = Double.parseDouble(angle2.substring(0, angle2.indexOf(';')).trim());
				theta2 = Math.toRadians(Double.parseDouble(angle2.substring(angle2.indexOf(';') + 1).trim()));
			}
			else
				theta2 = Math.toRadians(Double.parseDouble(angle2));
			
			dr = ((double) r2 - r1)/(duration - 1);
			dtheta = ((double) theta2 - theta1)/(duration - 1);
			
			for (int i = 0; i < duration - 1; i++) {
				x[i] = (int) (32767 * r1 * Math.cos(theta1));
				y[i] = (int) (32767 * r1 * Math.sin(theta1));
				r1 += dr;
				theta1 += dtheta;
			}
			x[duration - 1] = (int) (32767 * r2 * Math.cos(theta2));
			y[duration - 1] = (int) (32767 * r2 * Math.sin(theta2));	
		}
		
		if (stick == LS) {
			ls_x = x;
			ls_y = y;
		}
		
		else {
			rs_x = x;
			rs_y = y;
		}
	}
	
	public String get(int n) {
		String line = "";
		for (String[] input : inputs) {
			String s = input[n % input.length];
			if (s != null)
				line += input[n % input.length] + ";";
		}
		if (line.equals(""))
			line += "NONE ";
		else
			line = line.substring(0, line.length() - 1) + " ";
		line += ls_x[n % ls_x.length] + ";" + ls_y[n % ls_y.length] + " ";
		line += rs_x[n % rs_x.length] + ";" + rs_y[n % rs_y.length];
		return line;
	}
	
	public boolean isEmpty() {
		return get(0).equals("NONE 0;0 0;0");
	}
}
