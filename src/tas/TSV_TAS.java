package tas;

//Java program to create open or
//save dialog using JFileChooser
import java.io.*;

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
 
 private static String OS = System.getProperty("os.name");

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
     
     f.setVisible(true);
 }
 
 public void actionPerformed(ActionEvent evt)
 {
     // if the user presses the save button show the save dialog
     String com = evt.getActionCommand();

     if (com.equals("run")) {
    	 ScriptWriter scriptWriter = new ScriptWriter(new File(loadPath.getText()), new File(savePath.getText()), blank.isSelected());
    	 message.setText(scriptWriter.writeFile());
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
}