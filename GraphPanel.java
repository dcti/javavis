// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.io.*;
import java.util.*;         // java.util.AbstractList
import java.awt.*;
import javax.swing.*;


public class GraphPanel extends JPanel
{
    // Empty borders
    protected final int topBorder       = 10;
    protected final int bottomBorder    = 45;
    protected final int leftBorder      = 55;
    protected final int rightBorder     = 20;
    protected int width, height;

    // user-interface
    protected Color grayShade = new Color(235, 235, 235);

    // storage variables.
    protected java.util.List logdata = Collections.synchronizedList(new ArrayList());
    protected long mintime, maxtime;
    protected double minrate, maxrate;
    protected double totalkeys;
    protected long rangestart, rangeend;

    // current graphing state.
    protected final int nologloaded     = 0;
    protected final int lognotfound     = 1;
    protected final int loadinprogress  = 2;
    protected final int logloaded       = 3;
    protected int loggerstate;

    // other
    public File currentLogFile;


    // constructor
    public GraphPanel()
    {
        // set the default ranges
        rangestart = -1;
        rangeend = -1;
    
        // set the flags
        loggerstate = nologloaded;
    }


    // public interface methods.
    void getDataRange(long start, long end)
        { start = mintime; end = maxtime; }
    
    // public interface methods.
    void getRange(long start, long end)
        { start = rangestart; end = rangeend; }
    
    // public interface methods.
    void setRange(long start, long end)
        { rangestart = start; rangeend = end; }
    
    // public interface methods.
    boolean isDataAvailable()
        { return (loggerstate == logloaded) &&
            (!logdata.isEmpty()) &&
              (minrate < maxrate) && (mintime < maxtime); }
  
  
    public void paintComponent(Graphics g)
    {
        if (loggerstate == loadinprogress) {
            g.drawString("Please wait, currently reloading log file.",
                leftBorder, topBorder);
            return;
        }
        else if (loggerstate == lognotfound) {
            g.drawString("Could not load any data for graphing.  This may " +
                "indicate that there was a problem opening the log file.",
                leftBorder, topBorder);
            return;
        }
        else if (loggerstate == nologloaded) {
            g.drawString("You must specify a log file to be used for graph visualization.",
                leftBorder, topBorder);
            return;
        }
        else if (logdata.isEmpty() || minrate >= maxrate || mintime >= maxtime) {
            g.drawString("Could not load any data for graphing.  This may " +
                "indicate that there was no graphable data inside of the " +
                "specified log file",
                leftBorder, topBorder);
            return;
        }
        
        
        // Determine dimensions of graph area.
        width = this.getWidth() - (leftBorder + rightBorder);
        height = this.getHeight() - (topBorder + bottomBorder);

        
        // determine the range window that we will draw
        long timelo = (rangestart == -1 ? mintime : rangestart);
        long timehi = (rangeend == -1 ? maxtime : rangeend);
        if (timehi <= timelo) return;
        
        FontMetrics fm = g.getFontMetrics();
        
        // set up the graphing window structure and scaling
        double yinterval = (maxrate - minrate) / height * (fm.getHeight() * 4);
        long xinterval = (timehi - timelo) / width * (fm.charWidth('A') * 10);
        
        // Paint the window background.
        super.paintComponent(g);
        
        // Start with a white graph area.
        g.setColor(Color.white);
        g.fillRect(leftBorder, topBorder, width, height);
        
        // Add gray shading to graph area.
        g.setColor(grayShade);
        for(int i=0 ; i<width ; i+=100) {
            if((i+50)>width) {
                g.fillRect((leftBorder+i), topBorder, (width-i), height);
            } else {
                g.fillRect((leftBorder+i), topBorder, 50, height);
            }
        }
        
        // draw horizontal lines
        g.setColor(Color.black);
        for (double r = minrate + yinterval; r < maxrate; r += yinterval) {
            g.drawLine((int) timelo, (int) r, (int) timehi, (int) r);
        }
        
        // start drawing the points.
        g.setColor(Color.red);
        try {
            ListIterator listiter = logdata.listIterator();
            while (listiter.hasNext())
            {
                GraphEntry ge = (GraphEntry) listiter.next();
// more here.
            }
        }
        catch (NoSuchElementException e) { }
        
                
            


/*
  HPEN graphline = CreatePen(PS_SOLID, 2, RGB(0x99, 0x33, 0x33));
  oldpen = SelectObject(dc, graphline);
  logdata.ForEach(IterDrawFuncRate, (void*)&paintstr);
  SelectObject(dc, oldpen);
  DeleteObject(graphline);
  
        
  // change to a small black pen for drawing the tick marks.
  HGDIOBJ oldbrush = SelectObject(dc, GetSysColorBrush(COLOR_WINDOWTEXT));
  SetBkMode(dc, TRANSPARENT);
  SetTextColor(dc, RGB(0, 0, 0));
  SelectObject(dc, GetStockObject(BLACK_PEN));


  // draw the y-axis labels and ticks
  for (double y = minrate; y <= maxrate; y += yinterval)
  {
    // draw the tick mark
    POINT point = paintstr.PointToClientCoords(0, y);
    MoveToEx(dc, point.x, point.y, NULL);
    LineTo(dc, point.x - 6, point.y);

    // draw the text
    char buffer[30];
    sprintf(buffer, "%.1f", y / 1000.0);
    RECT rect = {0, point.y - tmet.tmHeight,
        point.x - 6, point.y + tmet.tmHeight};
    DrawText(dc, buffer, -1, &rect, DT_RIGHT | DT_VCENTER | DT_SINGLELINE);
  }


  // draw the x-axis labels and tick marks
  for (time_t x = timelo; x <= timehi; x += xinterval)
  {
    // draw the tick mark
    POINT point = paintstr.PointToClientCoords(x, 0);
    MoveToEx(dc, point.x, point.y, NULL);
    LineTo(dc, point.x, point.y + 6);

    // draw the text
    char buffer[30];
    struct tm *gmt = gmtime(&x);
    strftime(buffer, sizeof(buffer), "%b %d\n%H:%M", gmt);
    RECT rect = {point.x - tmet.tmAveCharWidth * 10, point.y + 6,
        point.x + tmet.tmAveCharWidth * 10, point.y + 6 + 3 * tmet.tmHeight};
    DrawText(dc, buffer, -1, &rect, DT_CENTER);
  }
  SelectObject(dc, oldbrush);
  SelectObject(dc, oldfont);


  // display the axes labels
  HFONT unrotatedfont = CreateFont(tmet.tmHeight, 0, 0, 0,
      FW_DONTCARE, false, false, false, DEFAULT_CHARSET,
      OUT_TT_ONLY_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY,
      DEFAULT_PITCH | FF_DONTCARE, "Arial");
  oldfont = SelectObject(dc, unrotatedfont);
  char *xlabel = "Work Unit completion date";
  SIZE xlabelsize;
  GetTextExtentPoint32(dc, xlabel, strlen(xlabel), &xlabelsize);
  TextOut(dc, graphrect.left + (graphrect.right - graphrect.left - xlabelsize.cx) / 2,
      clientrect.bottom - xlabelsize.cy, xlabel, strlen(xlabel));
  SelectObject(dc, oldfont);
  DeleteObject(unrotatedfont);


  // display the rotated y-axis label
  HFONT rotatedfont = CreateFont(tmet.tmHeight, 0, 900, 900,
      FW_DONTCARE, false, false, false, DEFAULT_CHARSET,
      OUT_TT_ONLY_PRECIS, CLIP_DEFAULT_PRECIS, DEFAULT_QUALITY,
      DEFAULT_PITCH | FF_DONTCARE, "Arial");
  oldfont = SelectObject(dc, rotatedfont);
  char *ylabel = "Work Unit keyrate (kkeys/sec)";
  SIZE ylabelsize;
  GetTextExtentPoint32(dc, ylabel, strlen(ylabel), &ylabelsize);
  TextOut(dc, 0, graphrect.top + ylabelsize.cx +
    (graphrect.bottom - graphrect.top - ylabelsize.cx) / 2, ylabel, strlen(ylabel));
  SelectObject(dc, oldfont);
  DeleteObject(rotatedfont);
        
*/    
        
        // Done!
        return;
    }
    
 
    // Load log in a separate thread.
    final class WorkerThread extends Thread
    {
        LogParser parser = null;
        
        WorkerThread(BufferedReader in, java.util.List logdata) {
            parser = new LogParser(in, logdata);
            loggerstate = loadinprogress;
        }

        public void run() {
            parser.run();
            loggerstate = logloaded;
            //repaint();
        }
    }



    void readLogData()
    {
        // reset storage.
        logdata.clear();
        mintime = maxtime = 0;
        minrate = maxrate = 0.0;
        totalkeys = 0.0;
        
        // open up the log.
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(currentLogFile));
        }
        catch( FileNotFoundException e ) {
            System.out.println("File not found: " + e);
            loggerstate = lognotfound;
            return;
        }
        
        WorkerThread p = new WorkerThread(in, logdata);
        p.start();
        
        repaint();
        
    }



/*
public void IterDrawFuncRate(GraphEntry datapoint, void *vptr)
{
  MyPaintStruct *paintstr = (MyPaintStruct*)vptr;
  if ((datapoint.timestamp >= paintstr->mintime) &&
      (datapoint.timestamp <= paintstr->maxtime))
  { 
    POINT point = paintstr->PointToClientCoords(datapoint.timestamp, datapoint.rate);
    if (paintstr->firstpoint)
    {
      // this is the first point being drawn.
      MoveToEx(paintstr->dc, point.x, point.y, NULL);
      paintstr->firstpoint = false;
    }
    else if ((datapoint.timestamp - paintstr->lasttime) > 300 + 1.25 * datapoint.duration)
    {
      // There was a significant lapse in time since the last point,
      // which probably indicates that the client was turned off for
      // awhile, so draw a "drop" in the keyrate graph.
      POINT point1 = paintstr->PointToClientCoords((long)(paintstr->lasttime + 0.25 * datapoint.duration), 0);
      POINT point2 = paintstr->PointToClientCoords((long)(datapoint.timestamp - 0.25 * datapoint.duration), 0);
      LineTo(paintstr->dc, point1.x, point1.y);
      LineTo(paintstr->dc, point2.x, point2.y);
      LineTo(paintstr->dc, point.x, point.y);
    }
    else
    {
      // otherwise just connect the line from the last one.
      LineTo(paintstr->dc, point.x, point.y);
    }
    paintstr->lasttime = datapoint.timestamp;
    paintstr->lastrate = datapoint.rate;
  }
}
*/




}
