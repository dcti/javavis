// Copyright distributed.net 1997-2001 - All Rights Reserved
// For use in distributed.net projects only.
// Any other distribution or use of this source violates copyright.
//

import java.awt.*;
import java.awt.image.*;


public class leftPanel
extends Panel
{
    private java.awt.Image img;
    private int width=20;
    private int height=200;

    public leftPanel()
    {
        super();
        setBackground(Color.lightGray);
    }

    private void createImage2()
    {
        java.awt.Image img2 = createImage(height,width);
        Graphics g = img2.getGraphics();

        g.setColor(Color.lightGray);
        g.fillRect(0,0,height,width);

        g.setColor(Color.black);
        FontMetrics fm = g.getFontMetrics();
        String str = "Work Unit keyrate (kkeys/sec)";
        int length = fm.stringWidth(str);
        g.drawString(str,(height/2)-length/2,fm.getHeight());
        g.dispose();
        g.finalize();
        img2.flush();


        int[] pixels = new int[height*width];
        PixelGrabber pg = new PixelGrabber(img2, 0, 0, height, width, pixels, 0, height);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
            return;
        }

        /* Rotate the Image */
        int pixels2[] = new int[height*width];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
            {
                int c = pixels[y+(x)*height];

                // Due a bug in the MS-JavaVM the Background for Images are not the same
                // as for Panel's  so. mark it as non-opaque ..
                if (c != 0xff000000)
                {
                    c = 0;
                }
                pixels2[x+(height-y-1)*width] = c;
            }

        img = createImage(new MemoryImageSource(width, height, pixels2, 0, width));
        repaint();
    }

    public Dimension getPreferredSize()
    {
        return new Dimension(width,height);
    }


    public void paint(Graphics g)
    {
        Dimension d = getSize();
        if (img != null)
        {
            int p = (d.height-height)/2;
            g.drawImage(img,0,p,null);
        } else {
            createImage2();
        }
    }
}
