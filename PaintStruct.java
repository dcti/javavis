

public class PaintElement {
	boolean firstpoint;
	double lastrate;
	double lasttime;
	
	double minrate, maxrate;
	double mintime, maxtime;
	//RECT *window;
	
	POINT PointToClientCoords(time_t time, double rate) const;
	
	POINT MyPaintStruct::PointToClientCoords(time_t time, double rate) const
	{
		double x = (double) (time - mintime) / (double) (maxtime - mintime);
		double y = (double) (rate - minrate) / (double) (maxrate - minrate);
		
		// force to within range
		if (x < 0) x = 0;
		else if (x > 1) x = 1;
		if (y < 0) y = 0;
		else if (y > 1) y = 1;
		
		// convert to screen coords
		POINT tmp;
		tmp.x = window->left + (int) ((window->right - window->left) * x);
		tmp.y = window->bottom - (int) ((window->bottom - window->top) * y);
		return tmp;
	}
}
