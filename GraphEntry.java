// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

public class GraphEntry
{
	long timestamp;
	float rate;
	float duration;
	long keycount;
	
	public static boolean equals(GraphEntry a, GraphEntry b)
	{
		return (a.timestamp == b.timestamp) && (a.rate == b.rate) &&
		    (a.duration == b.duration) && (a.keycount == b.keycount);
	}
}


