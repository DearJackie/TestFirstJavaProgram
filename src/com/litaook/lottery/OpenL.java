package com.litaook.lottery;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.litaook.lottery.OpenL;

 public class OpenL implements ActionListener {
    private JPanel theParent = null;
    private JTextField filename = new JTextField(), dir = new JTextField();
    public int selected = 0;
//    filename.setText("");
//    dir.setText("");

    public void actionPerformed(ActionEvent e) {
      JFileChooser c = new JFileChooser();
      // Demonstrate "Open" dialog:
      int rVal = c.showOpenDialog(theParent);
      if (rVal == JFileChooser.APPROVE_OPTION) {
        filename.setText(c.getSelectedFile().getName());
        dir.setText(c.getCurrentDirectory().toString());
	selected = 1;
      }
      if (rVal == JFileChooser.CANCEL_OPTION) {
        filename.setText("You pressed cancel");
        dir.setText("");
      }
    }

	// Constructor
	public OpenL(JPanel parent){
		theParent = parent;
	}

	public String getFileName() {
		return filename.getText();
	}
	
	public String getDir() {
		return dir.getText();
	}	
  }

