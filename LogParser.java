// Copyright distributed.net 1997-2002 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.lang.*;
import java.text.*;
import java.io.*;
import java.util.*;

class LogParser
{
    private BufferedReader logfile;
    private Vector logdata;


// Past logging formats of the client (older to newer):
//   [03/18/98 19:59:39 GMT] Completed block 00002752:E8100000 (2097152 keys)
//                          00:00:03.75 - [517825.30 keys/sec]
//  ---
//   [May 31 23:24:19 GMT] Completed RC5 block 687C9CC2:40000000 (1073741824 keys)
//                        0.00:19:29.52 - [918103.14 keys/sec]
//  ---
//   [Jul 18 03:00:57 GMT] Completed RC5 block 6DE46FD9:00000000 (2147483648 keys)
//   [Jul 18 03:00:57 GMT] 0.01:59:18.82 - [299,977.15 keys/sec]
//  ---
//   [Dec 16 03:25:59 UTC] Completed CSC packet 00205AE7:80000000 (4*2^28 keys)
//                         0.00:22:46.15 - [786,534.65 keys/sec]
//  ---
//   [Jul 21 10:01:55 UTC] Completed OGR stub 24/2-9-13-29-15 (9,743,881,734 nodes)
//                         0.00:45:22.11 - [3,579,527.43 nodes/sec]
//  ---
//   [Nov 09 03:38:02 UTC] RC5: Completed (1.00 stats units)
//                         0.00:02:43.43 - [176,486 keys/s]
//  ---
//   [Dec 24 02:43:22 UTC] RC5-72: Completed CA:40749399:00000000 (1.00 stats units)
//                         0.02:49:40.68 - [354,351 keys/s]



    public LogParser(BufferedReader br, Vector list)
    {
        logfile = br;
        logdata = list;
    }

    public void run()
    {
        GraphEntry ge = new GraphEntry();
        String s1 = null, s2 = null;

        try {
            s1 = logfile.readLine();
            while(logfile.ready())
            {
                s2 = logfile.readLine();

                if (ParseLogEntry(s1, s2, ge)) {
                    logdata.addElement(ge);
                    ge = new GraphEntry();
                }

                s1 = s2;
            }
        }
        catch (IOException e) {
            System.out.println("LogParser.run(): " + e);
        }
    }

    // Implements a custom decimal string parser for the StringCharacterIterator
    // class, that allows our caller to easily continue parsing after the
    // last character in the parsed string.
    private int ConvertDecimalInteger(StringCharacterIterator sci)
            throws ParseException
        // sci is modified by reference.
    {
        int value = 0;
        char ch = sci.next();
        if (ch < 0x0030 || ch > 0x0039) {
            sci.previous();
            throw new ParseException("invalid digit " + ch, sci.getIndex());
        }
        for (;;) {
            value = (value * 10) + (ch - 0x0030);
            ch = sci.next();
            if (ch == CharacterIterator.DONE) break;
            else if (ch == ',') ch = sci.next();
            if (ch < 0x0030 || ch > 0x0039) { sci.previous(); break; }
        }
        return value;
    }

    // Implements a custom decimal string parser for the StringCharacterIterator
    // class, that allows our caller to easily continue parsing after the
    // last character in the parsed string.
    private float ConvertDecimalFloat(StringCharacterIterator sci)
            throws ParseException, NumberFormatException
        // sci is modified by reference.
    {
        String str = "";
        char ch = sci.next();
        if (ch != '.' && (ch < 0x0030 || ch > 0x0039)) {
            sci.previous();
            throw new ParseException("invalid digit2 " + ch, sci.getIndex());
        }
        for (;;) {
            str += ch;
            ch = sci.next();
            if (ch == CharacterIterator.DONE) break;
            else if (ch == ',') ch = sci.next();
            if (ch != '.' && (ch < 0x0030 || ch > 0x0039)) { sci.previous(); break; }
        }
        return (float) Float.valueOf(str).floatValue();
    }

    // returns the matching month name.  raises exception on error.
    private int ConvertMonthName(String month)
        throws ParseException
    {
        if (month.compareTo("Jan") == 0) return 1;
        if (month.compareTo("Feb") == 0) return 2;
        if (month.compareTo("Mar") == 0) return 3;
        if (month.compareTo("Apr") == 0) return 4;
        if (month.compareTo("May") == 0) return 5;
        if (month.compareTo("Jun") == 0) return 6;
        if (month.compareTo("Jul") == 0) return 7;
        if (month.compareTo("Aug") == 0) return 8;
        if (month.compareTo("Sep") == 0) return 9;
        if (month.compareTo("Oct") == 0) return 10;
        if (month.compareTo("Nov") == 0) return 11;
        if (month.compareTo("Dec") == 0) return 12;
        throw new ParseException("invalid month " + month, 0);
    }

    // returns parsed timestamp.  raises exception on error.
    private long ParseTimestamp(String stamp)
        throws ParseException
    {
        int tm_mon, tm_mday, tm_year, tm_hour, tm_min, tm_sec;
        StringCharacterIterator sci = new StringCharacterIterator(stamp);
        if (Character.isDigit(sci.first()))
        {
            // Parse a timestamp of format "%u/%u/%u %u:%u:%u"
            tm_mon = ConvertDecimalInteger(sci);
            if (sci.next() != '/') throw new ParseException("bad separator", sci.getIndex());
            tm_mday = ConvertDecimalInteger(sci);
            if (sci.next() != '/') throw new ParseException("bad separator", sci.getIndex());
            tm_year = ConvertDecimalInteger(sci);
            if (sci.next() != ' ') throw new ParseException("bad separator", sci.getIndex());
            tm_hour = ConvertDecimalInteger(sci);
            if (sci.next() != ':') throw new ParseException("bad separator", sci.getIndex());
            tm_min = ConvertDecimalInteger(sci);
            if (sci.next() != ':') throw new ParseException("bad separator", sci.getIndex());
            tm_sec = ConvertDecimalInteger(sci);
        }
        else
        {
            // Convert a timestamp of format "%3s %u %u:%u:%u"
            tm_mon = ConvertMonthName(stamp.substring(0, 3));
            if (sci.setIndex(3) != ' ') throw new ParseException("bad separator1", sci.getIndex());
            tm_mday = ConvertDecimalInteger(sci);
            if (sci.next() != ' ') throw new ParseException("bad separator2", sci.getIndex());
            tm_hour = ConvertDecimalInteger(sci);
            if (sci.next() != ':') throw new ParseException("bad separator3", sci.getIndex());
            tm_min = ConvertDecimalInteger(sci);
            if (sci.next() != ':') throw new ParseException("bad separator4", sci.getIndex());
            tm_sec = ConvertDecimalInteger(sci);
            tm_year = 1998;     // hard coded for now.
        }

        // correct the date to a full 4-digit year
        if (tm_year < 0) throw new ParseException("bad year", sci.getIndex());
        else if (tm_year < 70) tm_year += 2000;
        else if (tm_year < 100) tm_year += 1900;

        // validate all fields
        if (tm_mon < 1 || tm_mon > 12 ||
            tm_mday < 1 || tm_mday > 31 ||
            tm_year < 1970 || tm_year >= 2038 ||
            tm_hour < 0 || tm_hour > 23 ||
            tm_min < 0 || tm_min > 59 ||
            tm_sec < 0 || tm_sec > 59)
            throw new ParseException("bad field value", sci.getIndex());

        // Convert to seconds past epoc.
        // This uses a deprecated API, but it is still useful.
        return Date.UTC(tm_year - 1900, tm_mon - 1, tm_mday, tm_hour, tm_min, tm_sec) / 100;
    }

    // returns parsed duration.  throws exception on error.
    public float ParseDuration(StringCharacterIterator sci)
        throws ParseException
    {
        int days, hours, mins;
        float secs;

        days = ConvertDecimalInteger(sci);
        switch (sci.next()) {
            case ':':
                hours = days; days = 0; break;
            case '.':
                hours = ConvertDecimalInteger(sci);
                if (sci.next() != ':')
                    throw new ParseException("bad duration separator", sci.getIndex());
                break;
            default:
                throw new ParseException("bad duration separator", sci.getIndex());
        }
        mins = ConvertDecimalInteger(sci);
        if (sci.next() != ':')
            throw new ParseException("bad duration separator", sci.getIndex());
        secs = ConvertDecimalFloat(sci);

        return (float) ((24.0 * days + hours) * (float) 3600.0 +
            (float) mins * (float) 60.0 + (float) secs);
    }

    public int ParseProject (StringCharacterIterator sci)
        throws ParseException
    {
        String project = "";
        int projectcode;

        projectcode = 0;
        project += sci.next();
        project += sci.next();
        project += sci.next();
        if (project.compareTo("RC5") == 0) projectcode = 1;
        else if (project.compareTo("DES") == 0) projectcode = 2;
        else if (project.compareTo("CSC") == 0) projectcode = 3;
        else if (project.compareTo("OGR") == 0) projectcode = 4;
        else if (project.compareTo("72:") == 0) projectcode = 8;

        return (int) projectcode;
    }


    long lastTimeStamp = 0;
    long addValue = 0;
    // returns true if successfully parsed.
    public boolean ParseLogEntry(String logline1, String logline2, GraphEntry ge)
        // ge is modified, by reference.
    {
        try
        {
            if (!logline1.startsWith("[")) return false;
            int compoffset = logline1.indexOf("Completed ");
            if (compoffset < 0) return false;
            boolean bContestFirst = (logline1.charAt(compoffset - 2) == ':');


            // parse timestamp.
            ge.timestamp = ParseTimestamp(logline1.substring(1))+addValue;
            if ((ge.timestamp-lastTimeStamp) < -8640000)
            {
                // 1 Year
                addValue += 864000*365;
                ge.timestamp += 864000*365;
                //System.out.println("Year Roll-Over Time : "+new Date(ge.timestamp*100)+" "+logdata.size());
                //System.out.println("line " + logline1.substring(1));
            }
            //System.out.println("got timestamp " + ge.timestamp);
            lastTimeStamp = ge.timestamp;

            // parse project.
            if (bContestFirst) {
                // "RC5: Completed"
                ge.project = ParseProject(new StringCharacterIterator(logline1, compoffset-5));
            } else {
                // "Completed RC5"
                ge.project = ParseProject(new StringCharacterIterator(logline1, compoffset+9));
            }
            //System.out.println("got project " + ge.project);

            // parse workunit count (keycount).
            int keyoffset = logline1.indexOf('(');
            if (keyoffset < 0) return false;
            if (bContestFirst) {
                float workfloat = ConvertDecimalFloat(new StringCharacterIterator(logline1, keyoffset));
                if (ge.project == 1 || ge.project == 2 || ge.project == 3) {
                    ge.keycount = (long) ((float) 0x10000000 * workfloat);
                } else {
                    ge.keycount = (long) workfloat;
                }
                //System.out.println("got keycount1 " + ge.keycount);
            } else {
                ge.keycount = (long) ConvertDecimalInteger(new StringCharacterIterator(logline1, keyoffset));
                //System.out.println("got keycount2 " + ge.keycount);
            }

            // parse the keyrate.
            int rateoffset = logline2.lastIndexOf('[');
            if (rateoffset < 0) return false;
            ge.rate = (float) ConvertDecimalFloat(new StringCharacterIterator(logline2, rateoffset));
            //System.out.println("got keyrate " + ge.rate);

            // parse duration.
            int duroffset = logline2.lastIndexOf(' ', rateoffset - 4);
            if (duroffset < 0) return false;
            ge.duration = ParseDuration(new StringCharacterIterator(logline2, duroffset));
            //System.out.println("got duration " + ge.duration);


            // successful parse complete.
            return true;
        }
        catch (Exception E) {
            System.out.println("parse exception " + E);
            return false;
        }
    }

}

