
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


