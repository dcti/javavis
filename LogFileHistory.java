// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.io.*;

public class LogFileHistory implements Serializable
{
    private static final int size = 4;
    private File[] files = new File[size];
    private static File logHistoryFile = new File("LogFileHistory.bin");

    public LogFileHistory() {

    }

    protected boolean addFile(File f) {
        if (!exists(f)) {
            for (int i = files.length-1; i > 0; i--) {
                files[i] = files[i-1];
            }
            files[0] = f;
            return true;
        } else {
            return false;
        }
    }

    protected boolean exists(File f) {
        for (int i = 0; i < files.length; i++) {
            if (files[i] != null && files[i].equals(f)) {
                return true;
            }
        }
        return false;
    }

    protected File[] getFiles() {
        return files;
    }

    protected void save() {
        try {
            FileOutputStream out = new FileOutputStream(logHistoryFile);
            ObjectOutputStream s = new ObjectOutputStream(out);
            s.writeObject(this);
            s.flush();
        } catch(IOException ioe) {
            System.out.println(ioe.toString());
        }
    }

    protected static LogFileHistory open()
    {
        LogFileHistory lfh = null;
        try {
            FileInputStream in = new FileInputStream(logHistoryFile);
            ObjectInputStream s = new ObjectInputStream(in);
            lfh = (LogFileHistory)s.readObject();
        } catch (IOException ioe) {

        } catch (ClassNotFoundException cnfe) {

        } finally {
            if (lfh == null) {
                return new LogFileHistory();
            } else {
                return lfh;
            }
        }
    }

}