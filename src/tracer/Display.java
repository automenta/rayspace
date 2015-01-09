/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */
package tracer;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import javax.swing.JPanel;

/**
 * A JPanel based display for the raytraced image. Uses selfmade double
 * buffering.
 *
 * @author Hj. Malthaner
 */
public class Display extends JPanel implements DisplayInterface {

    private BufferedImage bufferImg1;
    private BufferedImage bufferImg2;

    private BufferedImage showBuffer;

    private int bufferW;
    private int bufferH;

    private volatile boolean bufferOk;
    
    boolean filter = true;
    private boolean alphaBlend = true;

    @Override
    public int width() {
        return bufferW;
    }

    @Override
    public int height() {
        return bufferH;        
    }

    
    
    public Display() {
        super(new BorderLayout());
        setOpaque(false);

        
        setDoubleBuffered(false);
        
        bufferOk = false;

        addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
                createBackBuffer();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
            }

            @Override
            public void componentShown(ComponentEvent e) {
                createBackBuffer();
            }

            @Override
            public void componentHidden(ComponentEvent e) {
            }
        });
        
    }

    @Override
    public void paint(Graphics gr) {        
        paintBuffered((Graphics2D)gr);
    }

    private void paintBuffered(Graphics2D gr) {
        if (showBuffer == null) {
            createBackBuffer();
            
            //gr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }
        
        
        if (alphaBlend) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8f);
            gr.setComposite(ac);
        }
        
        
        
        gr.drawImage(showBuffer, 0, 0, width(), height(), null);
    }

    private void createBackBuffer() {
        if (showBuffer == null || bufferW != getWidth() || bufferH != getHeight()) {
            bufferOk = false;

            bufferH = getHeight();
            bufferW = getWidth();

            bufferImg1 = new BufferedImage(Math.max(bufferW, 16), Math.max(bufferH, 16),
                    BufferedImage.TYPE_INT_ARGB);
            bufferImg2 = new BufferedImage(Math.max(bufferW, 16), Math.max(bufferH, 16),
                    BufferedImage.TYPE_INT_ARGB);

            showBuffer = bufferImg1;

            bufferOk = true;
            
            /*if (filter) {

                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                                    RenderingHints.VALUE_RENDER_QUALITY);
                int w = getSize().width;
                int h = getSize().height;

                //  Set the color to black.

                g2.setColor(Color.black);


                //  Create a low-pass filter and a sharpen filter.

                float[][] data = {BLUR3x3, SHARPEN3x3};

                String theDesc[] = { "Convolve LowPass",
                                     "Convolve Sharpen", 
                                     "LookupOp",
                                     "RescaleOp"};            
            }*/

        }

    }

    public void prepareFrame() {
        if (showBuffer == null || bufferW != getWidth() || bufferH != getHeight()) {
            createBackBuffer();
        }
    }

    @Override
    public void setline(int y, int[] linepix) {
        if (!bufferOk) {
            return;
        }
        WritableRaster raster;

        if (showBuffer == bufferImg1) {
            raster = bufferImg2.getRaster();
        } else {
            raster = bufferImg1.getRaster();
        }
        if (raster == null)
            return;

        DataBuffer buffer = raster.getDataBuffer();

        final int off = y * bufferW;

        int[] bufferData = ((DataBufferInt) buffer).getData();
        if (linepix.length + (y*width()) > bufferData.length) {
            return;
        }

        
        //int overflow = ((linepix.length + off) - bufferData.length);
        System.arraycopy(linepix, 0, bufferData, off, linepix.length);

        //else {
            //throw new RuntimeException("Buffer overflow");
          //  System.err.println("raster buffer underflow: " + y + " @ " + off + " with " + linepix.length + " to write");
        //}
    }

    public void switchBuffers() {
        if (showBuffer == bufferImg1) {
            showBuffer = bufferImg2;
        } else {
            showBuffer = bufferImg1;
        }
    }
}
