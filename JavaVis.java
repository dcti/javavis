// Copyright distributed.net 1997-2001 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//




import java.io.File;
import java.awt.*;
import java.awt.event.*;
//import com.apple.mrj.*;                 // MacOS MRJ

// Main Frame
class JavaVis extends Frame
//implements MRJAboutHandler, MRJOpenDocumentHandler, MRJQuitHandler      // MacOS MRJ
{
    GraphPanel graphPanel;
    final AboutDialog aboutDialog = new AboutDialog(this);
    final LogFileHistory lfh;
    MenuItem refreshItem;

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
        final JavaVis app = new JavaVis("distributed.net Logfile Visualizer");
        Component contents = app.createComponents();
        //app.getContentPane().add(contents, BorderLayout.CENTER);
        app.setBackground(Color.lightGray);
        app.add(contents, BorderLayout.CENTER);
        app.add("West",new leftPanel());
        app.add("South",new Label("Work Unit completion date",Label.CENTER));

        // Finish setting up the frame, and show it.
        app.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                app.handleQuit();
            }
        });
        app.pack();
        app.setVisible(true);
        //MRJApplicationUtils.registerAboutHandler(app);  // MacOS MRJ
        //MRJApplicationUtils.registerQuitHandler(app);           // MacOS MRJ
        //MRJApplicationUtils.registerOpenDocumentHandler(app);           // MacOS MRJ
        if (args.length >= 1) {
            app.handleOpenFile(new File(args[0]));
        }
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
