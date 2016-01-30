package installer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;

public class GraphicsPanel extends JPanel
{
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  private MediaTracker tracker = new MediaTracker(this);
  private BufferedImage texture;
  private boolean singlePaint;
  
  public GraphicsPanel(boolean sp)
  {
    singlePaint = sp;
    texture = loadTexture();
  }
  
  public void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D)g;
    if (texture == null)
    {
      return;
    }
    if (singlePaint)
    {
      g2.drawImage(texture,null, 100,100);
    }
    else
    {
      Rectangle r = new Rectangle(-1, -1, texture.getWidth(), texture.getHeight());
      TexturePaint paint = new TexturePaint(texture, r);
      Dimension dim = getSize();
      g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g2.setPaint(paint);
      g2.fillRect(0, 0, dim.width, dim.height);
    }
  }
  
  private BufferedImage loadTexture()
  {
    Image image = null;
    BufferedImage bufferedImage = null;  
   
    File backgr = new File(Start.stamm+"Modinstaller/modinstallerbg.png");    
    if (!backgr.exists()||backgr.length()<10)
    	return null;
    
    image = Toolkit.getDefaultToolkit().getImage(backgr.getAbsolutePath());
    if(image != null)
    {
      tracker.addImage(image, 0);
      try
      {
        tracker.waitForAll();
      }
      catch (InterruptedException e)
      {
        tracker.removeImage(image);
        image = null;
      }
      finally
      {
        
        if(image!=null)
        tracker.removeImage(image);
        
        if(tracker.isErrorAny())
        image = null;
        
        if(image!=null)
        {
          if(image.getWidth(null)<0 || image.getHeight(null)<0)
          image = null;
        }
      }
    }
    
    if(image!=null)
    {
      bufferedImage = new BufferedImage(image.getWidth(null),
      image.getHeight(null),
      BufferedImage.TYPE_INT_RGB);
      Graphics2D g = bufferedImage.createGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();
    }
    return bufferedImage;
  }
}
