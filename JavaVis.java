// Copyright distributed.net 1997-2002- All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.

import java.io.File;
import java.awt.*;
import java.awt.event.*;

//import com.apple.mrj.*; // MRJToolkitStubs.zip provides empty declarations to link against

/* 
class MacOSMRJToolkitFrame extends JavaVis implements MRJAboutHandler, MRJOpenDocumentHandler, MRJQuitHandler
{
    public MacOSMRJToolkitFrame(String title)
    {
        // Parent Constructor
        super(title);
        
       // Register ourselves with MacOS Runtime for Java (MRJ) system event handlers
        MRJApplicationUtils.registerAboutHandler(this);
        MRJApplicationUtils.registerQuitHandler(this);
        MRJApplicationUtils.registerOpenDocumentHandler(this);   
    }
}
*/

// Main Frame
public class JavaVis extends Frame
{
    GraphPanel graphPanel;
    final AboutDialog aboutDialog = new AboutDialog(this);
    final LogFileHistory lfh;
    MenuItem refreshItem;
    static String[] arguments;

    // Constructor
    public JavaVis(String title)
    {
        // Parent Constructor
        super(title);

        lfh = LogFileHistory.open();
        //for (int i = 0; i < lfh.getFiles().length; i++) {
        //  System.out.println(lfh.getFiles()[i]);
        //}

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
                    lfh.addFile(file);
                    handleOpenFile(file);
                }

            }
        });
        menuItem.setShortcut(new MenuShortcut(KeyEvent.VK_O));
        menu.add(menuItem);

        refreshItem = new MenuItem("Refresh");
        refreshItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                graphPanel.readLogData();
            }
        });
        refreshItem.setShortcut(new MenuShortcut(KeyEvent.VK_R));
        refreshItem.setEnabled(false);
        menu.add(refreshItem);
        menu.addSeparator();

        //add history files (if any)
        File[] files = lfh.getFiles();
        for(int i = 0; i< files.length; i++){
            if(files[i] != null){
                menuItem = new MenuItem(files[i].toString());
                menu.add(menuItem);
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                            handleOpenFile(new File(((MenuItem)e.getSource()).getLabel()));
                    }
                });
            }
        }

        menuItem = new MenuItem("Quit");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleQuit();
            }
        });
        menuItem.setShortcut(new MenuShortcut(KeyEvent.VK_Q));
        menu.add(menuItem);

//        if (MRJApplicationUtils.isMRJToolkitAvailable() && System.getProperty("os.name").equals("Mac OS")) {
            // Under "Mac OS" (!="Mac OS X") the "About box" is provided by the MRJApplicationUtils
//        } else {
            menu = new Menu("Help");

            menuItem = new MenuItem("About JavaVis...");
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handleAbout();
                }
            });
            menuItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
            menu.add(menuItem);
            try {
                menuBar.setHelpMenu(menu);
                // avoid the double Help menu problem on Mac OS 8 and later
            } catch (Throwable thrown) {
                // in case we are on an older JDK which doesn't support this function
                // fall back on old strategy
                menuBar.add(menu);
            }
//        }
        
        Component contents = createComponents();
        //getContentPane().add(contents, BorderLayout.CENTER);
        setBackground(Color.lightGray);
        add(contents, BorderLayout.CENTER);
        add("West",new leftPanel());
        add("South",new Label("Work Unit completion date",Label.CENTER));

        // Finish setting up the frame, and show it.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                handleQuit();
            }
        });
        pack();
        setVisible(true);

        if (arguments.length >= 1) {
            handleOpenFile(new File(arguments[0]));
        }
        
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
        arguments = args;
        
//        if (MRJApplicationUtils.isMRJToolkitAvailable()) {
//            // Create the top-level container and add contents to it.
//            final MacOSMRJToolkitFrame app = new MacOSMRJToolkitFrame("distributed.net Logfile Visualizer");
//		  }
//		  else {
            // Create the top-level container and add contents to it.
            final JavaVis app = new JavaVis("distributed.net Logfile Visualizer");
//        }
    }

    public void handleOpenFile(File file) {
        if (file.exists()) {
            graphPanel.currentLogFile = file;
            graphPanel.readLogData();
            refreshItem.setEnabled(true);
        }
    }

    public void handleAbout() {
        aboutDialog.show();
    }

    public void handleQuit() {
        lfh.save();
        System.exit(0);
    }
}
