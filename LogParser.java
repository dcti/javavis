// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.lang.*;
import java.text.*;
import java.io.*;
import java.util.Date;
import java.util.List;


public class LogParser
{
    private BufferedReader logfile;
    private List logdata;


//   [Dec 16 03:25:59 UTC] Completed CSC packet 00205AE7:80000000 (4*2^28 keys)
//                         0.00:22:46.15 - [786,534.65 keys/sec]
//   [03/18/98 19:59:39 GMT] Completed block 00002752:E8100000 (2097152 keys)
//                          00:00:03.75 - [517825.30 keys/sec]
//   [May 31 23:24:19 GMT] Completed RC5 block 687C9CC2:40000000 (1073741824 keys)
//                        0.00:19:29.52 - [918103.14 keys/sec]
//   [Jul 18 03:00:57 GMT] Completed RC5 block 6DE46FD9:00000000 (2147483648 keys)
//   [Jul 18 03:00:57 GMT] 0.01:59:18.82 - [299,977.15 keys/sec]


    public LogParser(BufferedReader br, List list)
    {
        logfile = br;
        logdata = list;
    }

    public void run()
    {
        GraphEntry ge = new GraphEntry();
        String s1 = null, s2 = null;

        try {
            while(logfile.ready())
            {
                s2 = logfile.readLine();

                if (ParseLogEntry(s1, s2, ge)) {
                    logdata.add(ge);
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
        // sci is modified, by reference.
    {
        int value = 0;
        char ch = sci.current();
        if (ch < 0x0030 || ch > 0x0039)
            throw new ParseException("invalid digit", sci.getIndex());
        for (;;) {
            value = (value * 10) + (ch - 0x0030);
            ch = sci.next();
            if (ch == CharacterIterator.DONE ||
                ch < 0x0030 || ch > 0x0039) break;
        }
        return value;
    }

    // returns the matching month name.  raises exception on error.
    private int ConvertMonthName(String month)
        throws ParseException
    {
        if (month == "Jan") return 1;
        if (month == "Feb") return 2;
        if (month == "Mar") return 3;
        if (month == "Apr") return 4;
        if (month == "May") return 5;
        if (month == "Jun") return 6;
        if (month == "Jul") return 7;
        if (month == "Aug") return 8;
        if (month == "Sep") return 9;
        if (month == "Oct") return 10;
        if (month == "Nov") return 11;
        if (month == "Dec") return 12;
        throw new ParseException("invalid month", 0);
    }

    // returns true if successfully parsed.
    private boolean ParseTimestamp(String stamp, GraphEntry ge)
        // ge is modified, by reference.
    {
        int tm_mon, tm_mday, tm_year, tm_hour, tm_min, tm_sec;
        try {
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
                if (sci.setIndex(3) != ' ') throw new ParseException("bad separator", sci.getIndex());
                tm_mday = ConvertDecimalInteger(sci);
                if (sci.next() != ' ') throw new ParseException("bad separator", sci.getIndex());
                tm_hour = ConvertDecimalInteger(sci);
                if (sci.next() != ':') throw new ParseException("bad separator", sci.getIndex());
                tm_min = ConvertDecimalInteger(sci);
                if (sci.next() != ':') throw new ParseException("bad separator", sci.getIndex());
                tm_sec = ConvertDecimalInteger(sci);
                tm_year = 1998;     // hard coded for now.
            }

            // correct the date to a full 4-digit year
            if (tm_year < 0) return false;
            else if (tm_year < 70) tm_year += 2000;
            else if (tm_year < 100) tm_year += 1900;

            // validate all fields
            if (tm_mon < 1 || tm_mon > 12 ||
              tm_mday < 1 || tm_mday > 31 ||
              tm_year < 1970 || tm_year >= 2038 ||
              tm_hour < 0 || tm_hour > 23 ||
              tm_min < 0 || tm_min > 59 ||
              tm_sec < 0 || tm_sec > 59) return false;

        }
        catch (Exception E)
        {
            return false;
        }

        // Convert to seconds past epoc.
        // This uses a deprecated API, but it is still useful.
        ge.timestamp = Date.UTC(tm_year - 1900, tm_mon - 1,
                tm_mday, tm_hour, tm_min, tm_sec) / 100;
        return true;
    }

    // returns true if successfully parsed.
        // ge is modified, by reference.
    public boolean ParseDuration(String stamp, GraphEntry ge)
    {
        int days, hours, mins;
        float secs;
        try {
            StringCharacterIterator sci = new StringCharacterIterator(stamp);
            days = ConvertDecimalInteger(sci);
            switch (sci.next()) {
                case ':':
                    hours = days; days = 0; break;
                case '.':
                    hours = ConvertDecimalInteger(sci);
                    if (sci.next() != ':') throw new ParseException("", sci.getIndex());
                    break;
                default:
                    throw new ParseException("", sci.getIndex());
            }
            mins = ConvertDecimalInteger(sci);
            if (sci.next() != ':') throw new ParseException("", sci.getIndex());
            secs = (float) Float.valueOf(stamp.substring(sci.getIndex())).floatValue();
        }
        catch (Exception E)
        {
            return false;
        }
        ge.duration = (float) (24.0 * days + hours) * (float) 3600.0 +
            (float) mins * (float) 60.0 + (float) secs;
        return true;
    }

    // returns true if successfully parsed.
    public boolean ParseLogEntry(String logline1, String logline2, GraphEntry ge)
        // ge is modified, by reference.
    {
        try
        {
            if (!logline1.startsWith("[") || logline1.indexOf("] Completed ") < 0)
                return false;

            // parse timestamp.
            if (!ParseTimestamp(logline1.substring(1), ge))
                return false;

            // parse keycount.
            int keyoffset = logline1.indexOf('(');
            if (keyoffset < 0) return false;
            ge.keycount = Long.parseLong(logline1.substring(keyoffset + 1));

            // parse the keyrate.
            int rateoffset = logline2.lastIndexOf('[');
            if (rateoffset < 0) return false;
            String ratestr = logline2.substring(rateoffset + 1);
            while ((rateoffset = ratestr.indexOf(',')) >= 0) // get rid of commas
            {
                ratestr = ratestr.substring(0, rateoffset) +
                    ratestr.substring(rateoffset + 1);
            }
            ge.rate = (float) Float.valueOf(ratestr).floatValue();

            // parse duration.
            int duroffset = logline2.lastIndexOf(' ', rateoffset - 4);
            if (duroffset < 0) return false;
            if (!ParseDuration(logline2.substring(duroffset + 1), ge))
                return false;

            // successful parse complete.
            return true;
        }
        catch (Exception E) {
            return false;
        }
    }

}

