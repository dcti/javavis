// Copyright distributed.net 1997-2002 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.awt.*;
import java.awt.event.*;
import java.net.URL;


class AboutDialog extends Dialog
{
    private Image Cow;

    class OKButton extends Button implements ActionListener
    {
        public OKButton()
        {
            super("OK");
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e)
        {
            AboutDialog.this.setVisible(false);
        }
    }

    AboutDialog(Frame parent)
    {
        super(parent, "About this program", true);
        setSize(380,300);
        setLocation(50,50);
        setResizable(false);
        LayoutManager layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets.left = 60;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        add(new Label("distributed.net Log Visualizer v1.6"), gbc);
        add(new Label("programmed by:"), gbc);
        add(new Label("  Jeff \"Bovine\" Lawson <jlawson@bovine.net>"), gbc);
        add(new Label("  William Goo <wgoo@hmc.edu>"), gbc);
        add(new Label("  Yves Hetzer <aetsch@gmx.de>"), gbc);
        add(new Label("  Greg Hewgill <greg@hewgill.com>"), gbc);
        add(new Label("  Jason Townsend <townsend@cs.stanford.edu>"), gbc);
        add(new Label("  Andy Hedges <andy@hedges.net>"), gbc);
        add(new Label("  Stanley Appel <s.appel@bigfoot.com>"), gbc);
        add(new Label(), gbc);
        gbc.insets.left = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(new OKButton(), gbc);

        URL res = getClass().getResource("cowhead.gif");
        if (res != null) {
            Cow = getToolkit().getImage(res);
        }
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        if (Cow != null) {
            g.drawImage(Cow, 15, 32, this);
        }
    }
}
