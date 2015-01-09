/*
 * This file is part of the SimpleRay project
 * 
 * Published as part of the FreeMapper project:
 * http://sourceforge.net/projects/freemapper/
 * 
 * author: Hj. Malthaner <h_malthaner@users.sourceforge.net>
 */

package tracer;

import java.awt.image.BufferedImage;

/**
 * Texture (argb) storage.
 * 
 * @author Hj. Malthaner
 */
public class Texture
{
    
    //float translateX, translateY;
    //float scaleX, scaleY;
    
    private final int width;
    private final int height;
    private final int [] rgb;
    
    public Texture(BufferedImage img)
    {
        width = img.getWidth();
        height = img.getHeight();
        rgb = new int[width * height];
                
        img.getRGB(0, 0, width, height, rgb, 0, width);
    }
    
    
    public int getRGB(int x, int y)
    {
        return rgb[y*width + x];
    }

    public int getRGB(double s, double t)
    {
        return getRGB((int)(s*width), (int)(t*height));
    }

    public final int getWidth()
    {
        return width;
    }

    public final int getHeight()
    {
        return height;
    }
}
