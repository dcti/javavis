// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.io.File;
import java.awt.*;
import java.awt.event.*;

// Main Frame
public class RC5graph extends Frame
{
    GraphPanel graphPanel;

    // Constructor
    public RC5graph(String title)
    {
        // Parent Constructor
        super(title);

        // Create Menu
        MenuBar menuBar;
        Menu menu;
        MenuItem menuItem;

        menuBar = new MenuBar();
        this.setMenuBar(menuBar);

        menu = new Menu("File");
        menu.setShortcut(new MenuShortcut(KeyEvent.VK_F));
        menuBar.add(menu);

        menuItem = new MenuItem("Open log file...");
        final FileDialog fileDialog = new FileDialog(this);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a file chooser
                fileDialog.setMode(FileDialog.LOAD);
                fileDialog.show();

                // In response to a button click:
                String filename = fileDialog.getFile();
                if (filename != null) {
                    File file = new File(fileDialog.getDirectory(), filename);
                    if (file.exists())
                    {
                        graphPanel.currentLogFile = file;
                        graphPanel.readLogData();
                    }
                }

            }
        });
//        menuItem.setMnemonic(KeyEvent.VK_O);
        menu.add(menuItem);

        menuItem = new MenuItem("Exit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuItem.setShortcut(new MenuShortcut(KeyEvent.VK_X));
        menu.add(menuItem);

        menu = new Menu("Help");
        menu.setShortcut(new MenuShortcut(KeyEvent.VK_H));
        menuBar.add(menu);

        menuItem = new MenuItem("About");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Display about dialog box.
            }
        });
        menuItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
        menu.add(menuItem);
    }

    public Component createComponents()
    {
        // Create an internal panel to hold the graph
        graphPanel = new GraphPanel()
        {
            public Dimension getPreferredSize()
            {
                return new Dimension(620,320);
            }
        };

        return graphPanel;
    }

    public static void main(String[] args)
    {
        // Create the top-level container and add contents to it.
        RC5graph app = new RC5graph("distributed.net Logfile Visualizer");
        Component contents = app.createComponents();
//        app.getContentPane().add(contents, BorderLayout.CENTER);
        app.setBackground(Color.lightGray);
        app.add(contents, BorderLayout.CENTER);
        app.add("West",new leftPanel());
        app.add("South",new Label("Work Unit completion date",Label.CENTER));

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
