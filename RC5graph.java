// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.io.File;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// Main Frame
public class RC5graph extends JFrame
{
    GraphPanel graphPanel;

    // Constructor
    public RC5graph(String title)
    {
        // Parent Constructor
        super(title);
        
        // Create Menu
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;
        
        menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("Open log file...");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                final JFileChooser fc = new JFileChooser();
                // In response to a button click:
                int returnVal = fc.showOpenDialog(RC5graph.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    graphPanel.currentLogFile = file;
                    graphPanel.readLogData();
                }
                
            }
        });
        menuItem.setMnemonic(KeyEvent.VK_O);
        menu.add(menuItem);
        
        menuItem = new JMenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuItem.setMnemonic(KeyEvent.VK_X);
        menu.add(menuItem);
        
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu);
        
        menuItem = new JMenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display about dialog box.
            }
        });
        menuItem.setMnemonic(KeyEvent.VK_A);
        menu.add(menuItem);
    }

    public Component createComponents()
    {
        // Create an internal panel to hold the graph
        graphPanel = new GraphPanel();
        Dimension myPreferredSize = new Dimension( 620, 320 );
        graphPanel.setPreferredSize(myPreferredSize);    
        graphPanel.setBackground(Color.lightGray);
//        graphPanel.setLayout(new GridLayout(0, 1));

        return graphPanel;
    }
    
    public static void main(String[] args)
    {
        // Set the style.
        try {
            UIManager.setLookAndFeel(
//                UIManager.getCrossPlatformLookAndFeelClassName()
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { }

        // Create the top-level container and add contents to it.
        RC5graph app = new RC5graph("RC5 logfile visualizer");
        Component contents = app.createComponents();
        app.getContentPane().add(contents, BorderLayout.CENTER);
        
        // Finish setting up the frame, and show it.
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        app.pack();
        app.setVisible(true);
    }
}
