package installer;

import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;

public class Fullscreen extends JFrame implements ActionListener, KeyListener, MouseWheelListener
{	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel headl = new JLabel();
	private JLabel headl2 = new JLabel();
	private JLabel bild = new JLabel();	
	private JButton vor = new JButton();
	private JButton zurueck = new JButton();
	private JButton exit = new JButton();
	private Cursor curs1 = new Cursor(Cursor.HAND_CURSOR);
	private String url;
	private int x = 0;
	private int y = 0, i=0;	
	private String modname;
	private DefaultListModel<String> model;
	private boolean geladen = false, neu=true;
	
	
	public Fullscreen(JList<String> list, DefaultListModel<String> model) 
	{ 	   
		super(Read.getTextwith("installer", "name"));			
	    this.model=model;
		addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent event) {
		    	Fullscreen.this.dispose();
		    }
		});		
		
		modname = (String) model.getElementAt(list.getSelectedIndex());
		i = list.getSelectedIndex();
		
		setUndecorated(true);
		setResizable(false);
		setBackground(Color.BLACK);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		gd.setFullScreenWindow(this);		
		
	    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
	    x = d.width;
	    y = d.height;
	  
	    Container cp = getContentPane();
	    cp.setLayout(null);	 
	    cp.setBackground(Color.BLACK);	    
	    cp.addKeyListener(this);
	    
	    headl.setBounds(0, 25, x, 50); 	   	 
	    headl.setBackground(Color.decode("#FFF9E9"));	  
	    headl.setHorizontalAlignment(SwingConstants.CENTER); 
	    headl.setFont(Start.lcd.deriveFont(Font.BOLD,40));
	 
	    cp.add(headl);	
	    
	    headl2.setBounds(3, 27, x-3, 50); 	   	 
	    headl2.setBackground(null);
	    headl2.setForeground(Color.WHITE);
	    headl2.setHorizontalAlignment(SwingConstants.CENTER); 
	    headl2.setFont(Start.lcd.deriveFont(Font.BOLD,40));
	 
	    cp.add(headl2);	
	    
	    int khoehe = (int)(y*0.9);
	    int kbreite = (int)(x*0.4);
	    vor.setBounds(-5, (y/2)-(khoehe/2), kbreite, khoehe);
	    vor.setText("<<");	   
	    vor.setCursor(curs1);	
	    vor.addMouseWheelListener(this);
	    vor.addKeyListener(this);
	    vor.setHorizontalAlignment(SwingConstants.LEFT);
	    vor.setContentAreaFilled(false);
	    vor.setFont(new Font("Arial", Font.BOLD, 40));
	    vor.addActionListener(this);	    
	    cp.add(vor);
	    
	    zurueck.setBounds(x-kbreite+5, (y/2)-(khoehe/2), kbreite, khoehe);
	    zurueck.setText(">>");	  
	    zurueck.addMouseWheelListener(this);
	    zurueck.setCursor(curs1);
	    zurueck.setContentAreaFilled(false);
	    zurueck.addKeyListener(this);
	    zurueck.setHorizontalAlignment(SwingConstants.RIGHT);
	    zurueck.setFont(new Font("Arial", Font.BOLD, 40));
	    zurueck.addActionListener(this);	   
	    cp.add(zurueck);
	    
	    exit.setBounds(x-40, 0, 40, 40);
	    exit.setIcon(new ImageIcon(this.getClass().getResource("src/power.png")));
	    exit.addMouseWheelListener(this);
	    exit.setContentAreaFilled(false);
	    exit.setCursor(curs1);		  
	    exit.addKeyListener(this);
	    exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();							
			}
		});
	    cp.add(exit);
	   
	    int hoehe = y;
	    int breite = x;	    
	    int abstandoben = 0;
	    int abstandlinks = 0;
	  
    	bild.addKeyListener(this);
    	bild.addMouseWheelListener(this);
    	bild.setHorizontalAlignment(SwingConstants.CENTER);
    	bild.setVerticalAlignment(SwingConstants.CENTER);    	
    	bild.setBounds(abstandlinks, abstandoben, breite, hoehe);        	    	 
    	
    	 laden();
    	 cp.add(bild);
    	 setVisible(true);	     	    
	}	
	
	public void laden()
	{
		bild.setText("");
		geladen=false;
		neu=true;
	    headl.setText(modname);	
	    headl2.setText(modname);	    
	    url = "http://www.minecraft-installer.de/Dateien/BilderHQ/"+modname+".jpg";		 
	    url = url.replace(" ", "%20");
		 	
		 try 
	 	    {
	 			bild.setIcon((Icon) new ImageIcon(this.getClass().getResource("src/warten.gif")));
	 		} 
	 	    catch (Exception e) 
	 	    {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		 Thread t1 = new Thread() 
	 	    {
	 	    	  @Override public void run() 
	 	    	  {	 	    		
					 try 
					    {						 
						 	BufferedImage img = ImageIO.read(new URL(url));
						 	int dx = img.getWidth();
						 	int dy = img.getHeight();
						 	double verh = (double)dy/(double)dx;						 	
						 	if(dx>x)
						 	{
						 		dx=x;
						 		dy=(int)((double)x*verh);
						 	}						 	
						 	if(dy>y)
						 	{
						 		dx=(int)((double)y/verh);
						 		dy=y;
						 	}						 	
						 	img = new ImageScaler().scaleImage(img, new Dimension(dx, dy));	
							bild.setIcon((Icon) new ImageIcon(img));	
							img.flush();
						} 
					    catch (Exception e) 
					    {
							bild.setText("Kein Bild verfügbar...");
							bild.setIcon(null);
						}					 
					 geladen = true;
					 if(!neu) laden();
	 	    	 }
		    };
		    t1.start();
	}
	
	 public void actionPerformed (ActionEvent ae)
	 {
	        if(ae.getSource() == this.vor)
	        {
	        	vor();           
	        }	      
	        if(ae.getSource() == this.zurueck)
	        {
	        	zurueck();	           
	        }
	 }

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		int keyCode = arg0.getKeyCode();
	    switch( keyCode ) 
	    { 
	    	case KeyEvent.VK_ESCAPE:
	    		Fullscreen.this.dispose();
	            break;
	        case KeyEvent.VK_UP:
	        	vor();
	            break;
	        case KeyEvent.VK_DOWN:
	        	zurueck();
	            break;
	        case KeyEvent.VK_LEFT:
	        	vor();
	            break;
	        case KeyEvent.VK_RIGHT :
	        	zurueck();
	            break;	       
	     }
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void vor()
	{
		i--;			
		if(i<0)
			i=model.getSize()-1;	
		modname = (String) model.getElementAt(i);	
		headl.setText(modname);	
		   headl2.setText(modname);	    
		if(geladen)
			laden();
		else neu=false;
	
	}
	public void zurueck()
	{
		i++;	
		if(i>model.getSize()-1)		
			i=0;	
		modname = (String) model.getElementAt(i);	
		headl.setText(modname);	
		   headl2.setText(modname);	    
		if(geladen)
			laden();
		else
			neu=false;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) 
	{
		  int notches = arg0.getWheelRotation();
	       if (notches < 0) 
	       {
	           vor();	                      
	       }
	       else 
	       {
	          zurueck();
	       }	
	}
	
}
