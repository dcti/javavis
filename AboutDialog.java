// Copyright distributed.net 1997-1999 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.awt.*;
import java.awt.event.*;


class AboutDialog extends Dialog
{
    class OKButton extends Button implements ActionListener {
        Dialog dlgsave;

        public OKButton(Dialog dlg)
		{
		    super("OK");
		    dlgsave = dlg;
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent e)
		{
		    dlgsave.setVisible(false);
		}
    }

    AboutDialog(Frame parent)
    {
        super(parent, "About this program", true);
        setSize(300,200);
        setLocation(50,50);
        setResizable(false);
        add("North", new Label("distributed.net Log Visualizer v1.0"));
        add("North", new Label("programmed by:"));
        add("North", new Label("Jeff \"Bovine\" Lawson <jlawson@bovine.net>"));
        add("North", new Label("William Goo <wgoo@hmc.edu>"));
        add("North", new Label("Yves Hetzer <aetsch@gmx.de>"));
        add("South", new OKButton(this));
    }
}


