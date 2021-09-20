package tas;

//Java program to create open or
//save dialog using JFileChooser
import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import javax.swing.filechooser.*;
class TSV_TAS extends JFrame implements ActionListener {

 static JTextField savePath;
 static JTextField loadPath;
 static JCheckBox blank;
 static JLabel message;
 
 private static File file;
 private static File destFile;
 
 private static String OS = System.getProperty("os.name");

 // a default constructor
 TSV_TAS()
 {
 }

 public static void main(String args[])
 {
	 
     // frame to contains GUI elements
     JFrame f = new JFrame("TSV-TAS");

     // set the size of the frame
     f.setSize(500, 237);

     // set the frame's visibility
     f.setVisible(true);
     
     f.setResizable(false);

     f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

     JButton save = new JButton("Browse");
     JButton load = new JButton("Browse");
     JButton txt = new JButton("Generate .txt");
     txt.setActionCommand("run");
     save.setActionCommand("save");
     
     savePath = new JTextField(20);
     loadPath = new JTextField(20);
     
     message = new JLabel(" ");
     
     blank = new JCheckBox("Include Empty Lines");

     TSV_TAS f1 = new TSV_TAS();

     save.addActionListener(f1);
     load.addActionListener(f1);
     txt.addActionListener(f1);

     JPanel all = new JPanel();
     all.setLayout(new BoxLayout(all, BoxLayout.Y_AXIS));
     
     // make a panel to add the buttons and labels
     JPanel input = new JPanel();
     input.setBorder(new EmptyBorder(new Insets(20, 14, 10, 20)));
     input.setLayout(new BoxLayout(input, BoxLayout.X_AXIS));
     JLabel inPath = new JLabel("Input Path: ", JLabel.RIGHT);
     if (OS.indexOf("Mac") >= 0)
    	 inPath.setPreferredSize(new Dimension(90, 20));
     else
         inPath.setPreferredSize(new Dimension(80, 20));
     input.add(inPath);
     input.add(loadPath);
     input.add(load);
     
     JPanel output = new JPanel();
     output.setBorder(new EmptyBorder(new Insets(20, 14, 20, 20)));
     output.setLayout(new BoxLayout(output, BoxLayout.X_AXIS));
     JLabel outPath = new JLabel("Output Path: ", JLabel.RIGHT);
     if (OS.indexOf("Mac") >= 0)
    	 outPath.setPreferredSize(new Dimension(90, 20));
     else
         outPath.setPreferredSize(new Dimension(80, 20));
     output.add(outPath);
     output.add(savePath);
     output.add(save);
     
     JPanel generate = new JPanel();
     generate.add(txt);
     generate.add(blank);
     
     JPanel text = new JPanel();
     text.add(message);
     
     all.add(input);
     all.add(output);
     all.add(generate);
     all.add(Box.createRigidArea(new Dimension(0,5)));
     all.add(text);
     all.add(Box.createRigidArea(new Dimension(0,20)));

     f.add(all);
     
     f.show();
 }
 
 public void actionPerformed(ActionEvent evt)
 {
     // if the user presses the save button show the save dialog
     String com = evt.getActionCommand();

     if (com.equals("run")) {
    	 writeFile();
     }
     
     else if (com.equals("save")) {
         // create an object of JFileChooser class
         JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
         j.setDialogTitle("Choose Output Location");
         
         if (savePath.getText().length() > 0)
  			j.setSelectedFile(new File(savePath.getText()));

         // invoke the showsSaveDialog function to show the save dialog
         int r = j.showSaveDialog(null);

         // if the user selects a file
         if (r == JFileChooser.APPROVE_OPTION)
         {
             savePath.setText(j.getSelectedFile().getAbsolutePath());
         }
     }

     // if the user presses the open dialog show the open dialog
     else {
         // create an object of JFileChooser class
         JFileChooser j = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
         
        j.setDialogTitle("Choose TSV File");
 		FileNameExtensionFilter filter = new FileNameExtensionFilter(".tsv files", "tsv");
 		j.setFileFilter(filter);
 		
 		if (loadPath.getText().length() > 0)
 			j.setSelectedFile(new File(loadPath.getText()));

         // invoke the showsOpenDialog function to show the save dialog
         int r = j.showOpenDialog(null);

         // if the user selects a file
         if (r == JFileChooser.APPROVE_OPTION)
         {
             // set the label to the path of the selected file
        	 String filepath = j.getSelectedFile().getAbsolutePath();
             loadPath.setText(filepath);
             String savepath = filepath.substring(0, filepath.indexOf(".tsv")) + ".txt";
             savePath.setText(savepath);
         }
     }
 }
 
 private static void writeFile() {
	
	long start = System.currentTimeMillis();
	
	File destination = null;
	
	Scanner scan = null;
	try {
		scan = 	new Scanner(new File(loadPath.getText()));
	}
	catch (FileNotFoundException e) {
		message.setText("TSV file not found.");
		return;
	}
 	
 	PrintWriter print = null;
 	try {
 		destination = new File(savePath.getText() + "-temp");
		print = new PrintWriter(destination);
 	}
 	catch (FileNotFoundException e) {
 		message.setText("Output destination not found.");
 		scan.close();
 		return;
 	}
	int line = 1;
	String first = "";
 	while (scan.hasNextLine()) {
 		Scanner s = new Scanner(scan.nextLine()).useDelimiter("\t");
 		int duration = 1;
 		if (s.hasNextInt())
 			duration = s.nextInt();
 		else if (s.hasNext()) {
 			first = s.next();
 			if (first.equals("//"))
 				duration = 0;			
 		}
 		Line ln = new Line(duration);
 		//if there was no duration number we already got the first input and need to add it to the line
 		if (duration == 1)
 			try {
 				ln.add(first.toLowerCase());
 			}
 			catch (Exception e) {
 				message.setText("Syntax errors in TSV prevented file generation.");
 				s.close();
 				scan.close();
 				print.close();
 				destination.delete();
 				return;
 			}
 		if (duration > 0) {
	 		while (s.hasNext())
	 			try {
	 				ln.add(s.next().toLowerCase());
	 			}
	 			catch (Exception e) {
					message.setText("Syntax errors in TSV prevented file generation.");
					s.close();
					scan.close();
					print.close();
					destination.delete();
					return;
				}
	 		if (!blank.isSelected() && ln.isEmpty())
	 			line += duration;
	 		else
	 			for (int i = 0; i < duration; i++, line++)
	 				print.println(line + " " + ln.get(i));
 		}
 	}
 	scan.close();
 	print.close();
 	
 	File existing = new File(savePath.getText());
 	existing.delete();
 	destination.renameTo(existing);
 	
 	DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
 	
 	long end = System.currentTimeMillis();
 	long time = end - start;
 	message.setText("File successfully generated in " + time + " ms at " + timeFormat.format(LocalTime.now()) + ".");
 }
}
