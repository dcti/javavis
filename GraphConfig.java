public class GraphConfig
{
	// data storage.
	protected int userstart = 0;
	protected int userend = 0;
	
	// event handlers
	//protected static boolean CALLBACK DialogProc(HWND, UINT, WPARAM, LPARAM);
	//protected int OnCommand(HWND hwndDlg, WORD wNotifyCode, WORD wID, HWND hWndControl);
	//protected int OnInitDialog(HWND hwndDlg, HWND hWndFocus, LPARAM lParam);
	//protected boolean OnOK(HWND hwndDlg);

	// public method to invoke blocking dialog execution.
	//public int DoModal(HWND parent);
	
	// Data storage for currently selected date ranges.
	// Modify these values before calling DoModal(), and read
	// from them after regaining control.
	public int starttime = 0;
	public int endtime = 0;
	public int datastart = 0;
	public int dataend = 0;
}