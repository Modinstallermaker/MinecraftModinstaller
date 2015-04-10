package installer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Menu extends JFrame implements ActionListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	
	private JList<String> jList1 = new JList<String>();
	private JScrollPane jList1ScrollPane = new JScrollPane(jList1);
	private DefaultListModel<String> jList1Model = new DefaultListModel<String>();
	
	private JList<String> jList1b = new JList<String>();
	private JScrollPane jList1bScrollPane = new JScrollPane(jList1b);
	private DefaultListModel<String> jList1bModel = new DefaultListModel<String>();
	
	private JList<String> jList2 = new JList<String>();	
	private JScrollPane jList2ScrollPane = new JScrollPane(jList2);
	public static DefaultListModel<String> jList2Model = new DefaultListModel<String>();
	
	DefaultListModel<String> jListModel = null;
	JList<String> jList;	
	JTabbedPane tabbedPane = new JTabbedPane();
		
	private JEditorPane pane;  
	private JScrollPane scroller;
	
	private JLabel uberschrift = new JLabel();
	private JLabel versionstext = new JLabel();
	private JLabel modtext = new JLabel();	
	private JLabel[] bew = new JLabel[5];	
	private JLabel banner = new JLabel();
	private JLabel bild = new JLabel();
	private JLabel listright = new JLabel();
	private JLabel pfeilrechts = new JLabel();
	private JLabel pfeillinks = new JLabel();
	private JLabel importbutton = new JLabel();
	private JLabel restore = new JLabel();
	private JLabel hilfe = new JLabel();
	private JLabel link = new JLabel();
	private JLabel quelle = new JLabel();
	private JLabel beenden = new JLabel();
	private JLabel maximize = new JLabel();
	private JLabel minimize = new JLabel();
	public static JLabel weiter = new JLabel();		
	private JComboBox<String> ChVers;
	private JPanel cp;			
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);		
	private String mineord = Start.mineord, stamm = Start.stamm, Version = Start.Version, hyperlink = Read.getTextwith("seite2", "web"), linkquelle=Read.getTextwith("seite2", "web");	
	public static int zahl;
	private Modinfo[] Mod, Downloadlist;
	private double proz=0,  bewertung = 0.;
	private boolean Modloader=true, online = Start.online,  anders=false;	
	private int hoehe =600, breite=1024;
	//private int hoehe =1000, breite=1904;
	private ArrayList<String> Modloaderl = new ArrayList<String>();
	private ArrayList<String> Forgel = new ArrayList<String>();

	public Menu(Modinfo[] Mod, Modinfo[] Downloadlist) 
	{
		this.Mod=Mod;
		this.Downloadlist=Downloadlist;
		
		setUndecorated(true);
		setSize(breite, hoehe);
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		final Point point = new Point(0,0);
		addMouseListener(new MouseAdapter() 
		{  
			 @SuppressWarnings("deprecation")
			public void mousePressed(MouseEvent e) 
			 {  
				 if(!e.isMetaDown())
				 {  
					
					 point.x = e.getX();  
					 point.y = e.getY();  
					 setCursor(Cursor.MOVE_CURSOR);
				 } 
			 } 
			 @SuppressWarnings("deprecation")
			public void mouseReleased(MouseEvent e) 
			 {		
				 setCursor(Cursor.DEFAULT_CURSOR);
			}
		});  
		addMouseMotionListener(new MouseMotionAdapter() 
		{  
			 public void mouseDragged(MouseEvent e) 
			 {  
				 
				 if(!e.isMetaDown())
				 {  
					 
					 Point p = getLocation();  
					 setLocation(p.x + e.getX() - point.x,  
					 p.y + e.getY() - point.y); 					
				 } 				
			 } 
			
		});
		
		cp = new GraphicsPanel(false, "src/bild.png");
		//cp = new JPanel();
		cp.setBackground(Color.decode("#CEE3F6"));
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);
		
		int rand = 20;
		int bildx = 400;
		int bildy = 225;
		int uber = (int)(hoehe*0.09);
		int listeya = rand+uber;
		int listenb = (int)(breite/2-bildx+4*rand);
		int listenh = hoehe-listeya-rand;
		int mittexa= rand+listenb+20;
		int modtexty = rand+uber;	
		int infol = modtexty+2*rand;
		int bildya = infol+(int)(2.5*rand);		
		int textya = bildy+bildya+20;		
		int texth = listeya+listenh-textya;
		int liste2h= (int)(listenh*0.55);

		uberschrift.setBounds(0, 0, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		uberschrift.setText(Read.getTextwith("installer", "name"));
		uberschrift.setHorizontalAlignment(SwingConstants.CENTER);
		uberschrift.setVerticalAlignment(SwingConstants.CENTER);
		uberschrift.setFont(Start.lcd.deriveFont(Font.BOLD,48));
		cp.add(uberschrift);
	
		if(Start.Versionen!=null&&Start.Versionen.length>0)  //MC Version ändern
		{
			ChVers = new JComboBox<String>(Start.Versionen);
			for (int ka =0; ka<Start.Versionen.length; ka++)	
			{
				if(Start.Versionen[ka].equals(Version))
					ChVers.setSelectedIndex(ka);
			}
			ChVers.setBounds(rand+listenb-70, (int)(hoehe*0.05), 70, 25);
			ChVers.addActionListener(this);			
			if(Start.Versionen.length==1) ChVers.setEnabled(false);
			cp.add(ChVers);		
		
			versionstext.setBounds((int)(rand+listenb-70-110-5), (int)(hoehe*0.05), 110, 25);
			versionstext.setText("Minecraft");
			versionstext.setVerticalAlignment(SwingConstants.CENTER);
			versionstext.setHorizontalAlignment(SwingConstants.RIGHT);
			cp.add(versionstext);
		}
		
		jList1Model.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Modloader
		jList1.setModel(jList1Model);		
		jList1.setCellRenderer(new CellRenderer());				
		jList1.addMouseListener(this);
				
		jList1bModel.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Forge
		jList1b.setModel(jList1bModel);
		jList1b.setCellRenderer(new CellRenderer());				
		jList1b.addMouseListener(this);
		
		jList1ScrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		jList1bScrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		
		tabbedPane.addTab( "Modloader", jList1ScrollPane);
		tabbedPane.addTab( "Forge", jList1bScrollPane);
		tabbedPane.setEnabled(false);
		tabbedPane.addChangeListener(new ChangeListener() 
		{
	        public void stateChanged(ChangeEvent e) 
	        {
	        	 if(tabbedPane.isEnabled())
	    		 {
	    			 if(tabbedPane.getSelectedIndex()==0)
	    				ModloaderMode();
	    			 else
	    				ForgeMode();
	    		 }
	        }
	    });
		tabbedPane.setBounds(rand, listeya, listenb, listenh);							
        cp.add(tabbedPane);        
    	
		modtext.setBounds(mittexa, modtexty, bildx, 30);           //Modname
		modtext.setText(Read.getTextwith("seite2", "text7"));		
		modtext.setHorizontalAlignment(SwingConstants.CENTER);
		modtext.setFont(new Font("Dialog", Font.BOLD, 25));
		cp.add(modtext);
		
		link.setBounds(mittexa+bildx-80, infol+5, 40, 40); // Link zu Modinstallerweb	
		link.setIcon(new ImageIcon(this.getClass().getResource("src/infokl.png")));
		link.addMouseListener(this); 
		link.setToolTipText(Read.getTextwith("seite2", "webi"));
		link.setCursor(c);
		cp.add(link);
		
		quelle.setBounds(mittexa+bildx-40, infol+5, 40, 40); // Link zum Entwickler	
		quelle.setIcon(new ImageIcon(this.getClass().getResource("src/quelle.png")));
		quelle.addMouseListener(this); 
		quelle.setToolTipText(Read.getTextwith("seite2", "dev"));
		quelle.setCursor(c);
		cp.add(quelle);
		
		for (int i=0; i<5; i++) //Sterne für Bewertung
		{
			bew[i] = new JLabel();
			bew[i].setBounds(mittexa+10+i*25, infol, 40, 40);
			bew[i].setCursor(c);	
			bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));
			bew[i].addMouseListener(this);
			cp.add(bew[i]);
		}
		
		bild.setBounds(mittexa, bildya, bildx, bildy); 
		bild.setHorizontalAlignment(SwingConstants.CENTER);
    	bild.setVerticalAlignment(SwingConstants.CENTER);   
    	bild.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
    	bild.addMouseListener(this);
    	bild.setCursor(c);    	
		bild.setIcon(new ImageIcon(this.getClass().getResource("src/warten.gif")));	
		bild.setToolTipText(Read.getTextwith("seite2", "pici"));
		cp.add(bild);
		
		HTMLEditorKit kit = new HTMLEditorKit();		
			
		pane = new JEditorPane(); //Beschreibungsfenster		
		pane.setEditable(false);
	    pane.setContentType("text/html");	
	    Document doc = kit.createDefaultDocument();
	    pane.setDocument(doc);	  
	    pane.setText(Read.getTextwith("seite2", "wait"));	  	  
	    
	    StyleSheet ss = kit.getStyleSheet();
		try 
		{
			ss.importStyleSheet(new URL("http://www.minecraft-installer.de/sub/installerstyle.css"));
		} 
		catch (MalformedURLException e1) 
		{			
		}		
		kit.setStyleSheet(ss);
	    
	    scroller = new JScrollPane(pane);
	    scroller.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
	    scroller.setBounds(mittexa, textya, bildx, texth);	
	    cp.add(scroller);
        
		pfeilrechts.setBounds(mittexa+bildx+2*rand, 300, 100, 83); // Pfeil nach rechts		
		pfeilrechts.setIcon(new ImageIcon(this.getClass().getResource("src/hinzufügen.png")));		
		pfeilrechts.setToolTipText(Read.getTextwith("seite2", "text1"));
		pfeilrechts.addMouseListener(this);
		pfeilrechts.setCursor(c);
		cp.add(pfeilrechts);

		pfeillinks.setBounds(mittexa+bildx+rand, 390, 100, 83); // Pfeil nach links		
		pfeillinks.setIcon(new ImageIcon(this.getClass().getResource("src/löschen.png")));	
		pfeillinks.setToolTipText(Read.getTextwith("seite2", "text2"));	
		pfeillinks.addMouseListener(this);
		pfeillinks.setCursor(c);
		cp.add(pfeillinks);	
		
		restore.setBounds(breite-rand-listenb+10, (int)(hoehe*0.15), 180, 40); // Restore druchführen	
		restore.setText(Read.getTextwith("seite2", "text5"));	
		restore.setFont(restore.getFont().deriveFont(Font.BOLD));
		restore.setIcon(new ImageIcon(this.getClass().getResource("src/restore.png")));		
		restore.addMouseListener(this); 
		restore.setCursor(c);			
		File backupfile = new File(stamm +"/Modinstaller/Backup/");
		if (!backupfile.exists()) // überprüfen, ob Restore möglich ist		
		{
			restore.setEnabled(false);
		}
		cp.add(restore);
		
		importbutton.setBounds(breite-rand-listenb+10, (int)(hoehe*0.15)+50, 180, 40); // Mods importieren				
		importbutton.setText(Read.getTextwith("seite2", "text3"));
		importbutton.setFont(importbutton.getFont().deriveFont(Font.BOLD));
		importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/importkl.png")));	
		importbutton.addMouseListener(this); 
		importbutton.setCursor(c);		
		cp.add(importbutton);
		
		listright.setBounds(breite-rand-listenb, (int)(hoehe*0.315), listenb, 20); //Liste2 Überschrift
		listright.setHorizontalAlignment(SwingConstants.CENTER);
		listright.setText(Read.getTextwith("seite2", "modi"));		
		cp.add(listright);
		
		jList2Model.addElement("");    // Liste2
		jList2.setModel(jList2Model);   
		jList2.setCellRenderer(new CellRenderer());  
		jList2.addMouseListener(this);
		jList2ScrollPane.setBounds(breite-rand-listenb,  (int)(hoehe*0.35), listenb, liste2h);
		jList2ScrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.add(jList2ScrollPane);
		
		hilfe.setBounds(2, 5, 50, 50); // FAQ anzeigen			
		hilfe.setIcon(new ImageIcon(this.getClass().getResource("src/help.png")));
		hilfe.setToolTipText(Read.getTextwith("seite2", "text6"));	
		hilfe.addMouseListener(this); 		
		cp.add(hilfe);
	    
		minimize.setBounds(breite-(35+35+35+3+3)-3, 3, 35, 27); //Minimieren		
		minimize.setIcon(new ImageIcon(this.getClass().getResource("src/mini.png")));			
		minimize.addMouseListener(this);
		cp.add(minimize);
		
		maximize.setBounds(breite-(35+35+3)-3, 3, 35, 27); //Maximieren
		maximize.setIcon(new ImageIcon(this.getClass().getResource("src/maxi.png")));			
		maximize.addMouseListener(this);
		maximize.setEnabled(false);
		cp.add(maximize);
		
		beenden.setBounds(breite-35-3, 3, 35, 27); //Beenden		
		beenden.setIcon(new ImageIcon(this.getClass().getResource("src/closeme.png")));			
		beenden.addMouseListener(this);
		cp.add(beenden);

		weiter.setBounds((int)(breite-200-rand), hoehe-70-rand, 200, 70); // Installieren		
		weiter.setText(Read.getTextwith("seite2", "text10"));
		weiter.setFont(weiter.getFont().deriveFont((float) 15));
		weiter.addMouseListener(this);
		weiter.setCursor(c);		
		weiter.setHorizontalTextPosition(SwingConstants.LEFT);
		weiter.setFont(weiter.getFont().deriveFont(Font.BOLD, 20));
		weiter.setHorizontalAlignment(SwingConstants.RIGHT);	
		weiter.setVerticalAlignment(SwingConstants.CENTER);
		weiter.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));	
		weiter.setEnabled(false);
		cp.add(weiter);	
	    
	    setVisible(true);			
	    laden();	 		
	}
	
	public void laden()
	{
		if(online)
		{			
		   SwingUtilities.invokeLater(new Runnable() 
		   {					   
				 public void run() 
				 { 							
					 change();					
				 }
		   });			   
		}
		else
		{
			offline(true);
		}
	}
	
	public void offline(boolean off) //Den Offline Modus vorbereiten
	{
		if(off==true)
		{
			online=false;
			jList1Model.removeAllElements();
			jList1bModel.removeAllElements();
			jList2Model.removeAllElements();		
			for(int i=0; i<5; i++)
				bew[i].setEnabled(false);				
			try
			{
				pane.setText(Read.getTextwith("seite2", "import"));
				pane.setCaretPosition(0);	
				modtext.setText(Read.getTextwith("seite2", "text7"));
			}
			catch (Exception ex)
			{				
			}		
		}
		else
		{
			online=true;	
		}
	}
	
	public void loadText()
	{
		if(jListModel.isEmpty())  
		{
	    	if(!jList2Model.isEmpty())
	    	{
	    		jList2.setSelectedIndex(0);
	    		setInfoText((String)jList2Model.get(0));		    	
	    	}  
		}
    	else   
    	{
    		jList.setSelectedIndex(0);
    		setInfoText((String)jListModel.get(0));	  
    	}
	}
	
	public void ModloaderMode()
	{			
		jListModel = jList1Model;
	    jList = jList1;
	    Modloader = true;
	    jListModel.removeAllElements();
	    jList2Model.removeAllElements();
	    for (String val : Modloaderl)
	     jListModel.addElement(val);	    
	    aussortieren(jListModel);
	    tabbedPane.setSelectedIndex(0);
	    loadText();
	}

	public void ForgeMode() 
	{		
		jListModel = jList1bModel;
	    jList = jList1b;
	    Modloader = false;
	    jListModel.removeAllElements();
	    jList2Model.removeAllElements();
	    for (String val : this.Forgel) 
	     jListModel.addElement(val);
	    aussortieren(this.jListModel);
	    tabbedPane.setSelectedIndex(1);
	    loadText();
	}
	
	public void aussortieren(DefaultListModel<String> List)
	{
	    String Mode = "Modloader";
	    if (!this.Modloader) 
	      Mode = "Forge";
	   
      if ((new OP().optionReader("lastmc").equals(this.Version)) && (new OP().optionReader("lastmode").equals(Mode)))
      {
        String alastm = new OP().optionReader("lastmods");
        String[] lastm = alastm.split(";;");
        int lange = List.getSize();
        for (int r = 0; r < lastm.length; r++) {
          for (int k = 0; k < lange; k++) {
            if (lastm[r].equals(((String)List.getElementAt(k)).toString()))
            {
              jList2Model.addElement(((String)List.getElementAt(k)).toString());
              List.remove(k);
              lange--;
            }
          }
        }
      }
	    
	    this.jList1ScrollPane.getVerticalScrollBar().setValue(0);
	    this.jList1bScrollPane.getVerticalScrollBar().setValue(0);
	}
	
	public void change()
	{	
		bild.setIcon(new ImageIcon(this.getClass().getResource("src/warten.gif")));	
		tabbedPane.setEnabled(false);		
		this.Modloaderl.clear();
	    this.Forgel.clear();	
				
	    try
	    {
	      for (int k = 0; k < this.Downloadlist.length; k++) {
	        if ((this.Downloadlist[k].getMC().equals(this.Version)) && (this.Downloadlist[k].getCat() == 0)) {
	          this.Modloaderl.add(this.Downloadlist[k].getName());
	        } else if ((this.Downloadlist[k].getMC().equals(this.Version)) && (this.Downloadlist[k].getCat() == 3)) {
	          this.Forgel.add(this.Downloadlist[k].getName());
	        }
	      }
	      new OP().del(new File(this.stamm + "/Modinstaller/Import"));
	      new OP().del(new File(this.stamm + "/Modinstaller/zusatz.txt"));
	    }
	    catch (Exception localException) {}
	    if (this.Forgel.size() > 0) {
	      this.tabbedPane.setEnabled(true);
	    }
	    if (this.Forgel.size() >= this.Modloaderl.size()) {
	      ForgeMode();
	    } else {
	      ModloaderMode();
	    }
	  }
	
	public void versioneinstellen() //Version ändern
	{	
		jListModel.addElement(Read.getTextwith("seite2", "wait2"));
		pane.setText(Read.getTextwith("seite2", "wait"));
		Start.Version = Start.Versionen[ChVers.getSelectedIndex()];
		Version = Start.Versionen[ChVers.getSelectedIndex()];
		laden();				
	}	
	
	private void setInfoText(final String modname) //Modbeschreibung anzeigen
	{			
		modtext.setText(modname);	
		bild.setIcon(new ImageIcon(this.getClass().getResource("src/warten.gif")));	
		
		for(int i=0; i<Mod.length; i++)
		{
			if(Mod[i].getName().equals(modname))
			{	
				try
			    {								
				 	String inh = Mod[i].getText();			
				 	linkquelle =  Mod[i].getSource();
					hyperlink = Read.getTextwith("seite2", "web") + "/modinfo.php?modname=" + modname.replace(" ", "+");
					if(!inh.startsWith("<html>"))
					{
						inh="<html><body>"+inh+"</body></html>";
					}				
					pane.setText(inh);							
					pane.setCaretPosition(0);
					anders=false;
					
					for(int j=0; j<Downloadlist.length; j++)	
					{
						if(Downloadlist[j].getName().equals(modname)&&Downloadlist[j].getMC().equals(Version))
						{
							proz = Downloadlist[j].getRating();
							if(proz > 6.5)
							{									
								modtext.setText(modname +" - TOP!");									
							}		
							Sterne(proz, false);	
							break;
						}
					}					
			    }
			    catch(Exception e)
			    {	
			    	 e.getStackTrace();
			    }	
			}
		}
		
		new Thread() 
 	    {
    	  public void run() 
    	  {	 	    		
    		try 
		    {	
    			String url = "http://www.minecraft-installer.de/Dateien/BilderPre/"+modname+".jpg";
    			url = url.replace(" ", "%20");
			 	BufferedImage img = ImageIO.read(new URL(url));			 	
				bild.setIcon((Icon) new ImageIcon(img));	
				img.flush();
				bild.setText("");
			} 
		    catch (Exception e) 
		    {
				bild.setText(Read.getTextwith("seite2", "nopic"));				
				bild.setIcon(null);
			}	
    	 }
	    }.start();
	}
	
	public void ModAuswahl() // Auswählen von Mods
	{	
		String modname = (String)jList.getSelectedValue();
			
		if (searchentry(jListModel, modname)) 
		{
			jList2Model.add(jList2Model.getSize(),modname);
			jListModel.removeElement(modname);
		}			
		weiter.setEnabled(true); // Installieren Knopf freischalten									
	}
	
	public boolean searchentry(DefaultListModel<String> model, String modname) //In ListModel einen Modeintrag finden
	{
		boolean gefunden = false;
		for (int i=0; i < model.getSize(); i++) 
		{
			if (model.getElementAt(i).equals(modname)) // überprüfen ob schon in Liste vorhanden
			{
				gefunden = true;
			}
		}
		return gefunden;
	}
	
	public void ModEntfernen() // Entfernen von Mods
	{
		weiter.setEnabled(true);
		for (int i = 0; i < jList2Model.getSize(); i++) 
		{
			if (jList2.getSelectedIndex() == i) // Ausgew�hlte Stelle suchen
			{
				if (((String) jList2Model.getElementAt(jList2.getSelectedIndex())).substring(0, 1).equals("+")) // wenn ausgew�hltes mod,dann zusatz.txt l�schen
				{
					File zus = new File(stamm +"/Modinstaller/zusatz.txt");
					if(zus.exists())
					{
						String neutext = "";
						boolean test = false;
						try
						{
							String[] zeilenl4 = new OP().Textreader(zus);
							
							for (int m=0; m<zeilenl4.length; m++)
							{							
								File zz = new File(zeilenl4[m]);
								if (!zz.getName().equals(((String) jList2Model.getElementAt(jList2.getSelectedIndex())).substring(2))) 
								{
									neutext += String.valueOf(zeilenl4[m]) +";;";
									test = true;
								} 
							}					
						} 
						catch (Exception e) 
						{					
							new Error(Read.getTextwith("seite2", "error2") + String.valueOf(e)+ "\n\nErrorcode: S2x06");
						}
						
						try //???
						{
							new OP().Textwriter(zus, neutext.split(";;"), false);
						} 
						catch (Exception e) 
						{
							new Error(Read.getTextwith("seite2", "error2") + String.valueOf(e)+ "\n\nErrorcode: S2x07");
						}
								       
						if (test == false) 
						{
							new OP().del(new File(stamm +"/Modinstaller/zusatz.txt"));
							new OP().del(new File(stamm +"/Modinstaller/Import"));
						} 
					}
				} // sonst nach Liste1 kopieren
				else 
				{
					boolean ent = false;
					for (int j = 0; j < jList1Model.getSize(); j++) 
					{
						if (jList2Model.getElementAt(jList2.getSelectedIndex()).toString().equals(jListModel.getElementAt(j).toString())) // Ausgew�hlte Stelle suchen
						{
							ent = true;
						}
					}
					if (ent == false) 
					{
						jList.setEnabled(true);						
						jListModel.addElement(jList2Model.getElementAt(jList2.getSelectedIndex()));
												
						String[] list = new String[jListModel.getSize()];
						for(int i3 = 0; i3 < jListModel.getSize(); i3++) 
						{						  
						    list[i3] = ((String) jListModel.elementAt(i3));						  
						}
						Arrays.sort(list);	
						
						jListModel.removeAllElements();
						for (int h=0; h<list.length; h++)
						{
							jListModel.addElement(list[h]);
						}
					}
				}
				jList2Model.remove(jList2.getSelectedIndex()); // Mod vom Liste2 l�schen
				if (jList2Model.getSize() == 0) 
				{
					weiter.setEnabled(false); // Wenn keine Mods in Liste2 vorhanden Installieren deaktivieren
				}
			}
		}
	}

	public void weiter_ActionPerformed(MouseEvent e2) // Installieren Knopf
	{		
		try 
		{		
			ArrayList<String> chnamen = new ArrayList<String>();
			for(int i=0; i<jList2Model.getSize(); i++)
			{
				String eintrag = jList2Model.getElementAt(i);
				if(eintrag.charAt(0)!='+')
				{
					chnamen.add(eintrag);	
				}
			}				
			String[] namen = chnamen.toArray(new String[chnamen.size()]); 
			
			dispose();
			new Install(namen, Modloader);
		} 
		catch (Exception ex) 
		{	
			new Error(new OP().getStackTrace(ex) + "\n\nErrorcode: S2x09");	
		}		
	}

	public void ModsImportieren() //Mods importieren
	{
		pane.setText(Read.getTextwith("seite2", "import"));	
		pane.setCaretPosition(0);
		modtext.setText(Read.getTextwith("seite2", "text3"));
		hyperlink="http://www.minecraft-installer.de/faq.php";
		Sterne(0, false);		
		new Import(Modloader);			
	}
	
	public void Restore() //Letzte Modinstallation wiederherstellen
	{
		new OP().del(new File(mineord+"/versions/Modinstaller"));
		try 
		{
			new OP().copy(new File(stamm+"/Modinstaller/Backup/Modinstaller.jar"), new File(mineord+"/versions/Modinstaller.jar"));
			new OP().copy(new File(stamm+"/Modinstaller/Backup/Modinstaller.json"), new File(mineord+"/versions/Modinstaller.json"));
			new OP().copy(new File(stamm+"/Modinstaller/Backup/mods"), new File(mineord+"/mods"));
			JOptionPane.showMessageDialog(null,	Read.getTextwith("seite2", "restore"), Read.getTextwith("seite2", "restoreh"), JOptionPane.INFORMATION_MESSAGE);
		} 
		catch (Exception e) 
		{			
			e.printStackTrace();
		} 
		try 
		{
			new OP().optionWriter("lastmods", new OP().optionReader("slastmods"));
			new OP().optionWriter("slastmods", new OP().optionReader("n/a"));
			new OP().optionWriter("lastmc", new OP().optionReader("slastmc"));
			new OP().optionWriter("slastmc", new OP().optionReader("n/a"));
			new OP().optionWriter("lastmode", new OP().optionReader("slastmode"));
			new OP().optionWriter("slastmode", new OP().optionReader("n/a"));
		} 
		catch (Exception e1) 
		{			
			e1.printStackTrace();
		}		
		restore.setEnabled(false);	
	}
	
	private void Sterne(double bewe, boolean anders) //Bewertung grafisch umsetzen
	{
			
		for(int i=0; i<5; i++)
		{
			if(bewe>0.75)
			{
				if(!anders)
					bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star1.png")));	
				else
					bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star1b.png")));	
			}
			else if(bewe>0.25)
			{
				bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star05.png")));	
			}
			else
			{
				bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));	
			}
			
			bewe--;
		}
	}
	
	private static class CellRenderer extends DefaultListCellRenderer //Auswahlliste verschönern
	{   
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	    {  
           super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
           list.setFixedCellHeight(25);     
           setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));           
           if(isSelected) setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));          
           if(cellHasFocus) setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
           return (this);  
        }
    }

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		Object s = e.getSource();
		if(s==banner)
			new Browser(Read.getTextwith("seite2", "web"));
		else if(s==pfeilrechts)		 
			 ModAuswahl();		 
		 else if(s==pfeillinks)
			 ModEntfernen();		 
		 else if(s==importbutton)					
			ModsImportieren();
		 else if(s==restore)
		 {
			 if(restore.isEnabled())
				 Restore();
		 }
		 else if(s==hilfe)
			 new Browser(Read.getTextwith("seite2", "web")+"/faq.php");
		 else if(s==link)
		 {
			 if(link.isEnabled())
			 new Browser(hyperlink);
		 }
		 else if(s==quelle)
		 {
			 if(quelle.isEnabled())
			 new Browser(linkquelle);
		 }
		 else if(s==bild)
			 new Fullscreen(jList, jListModel);
		 else if(s==beenden)
			 System.exit(0);
		 else if(s==minimize)
			 setState(ICONIFIED);
		
		 for(int i=0; i<bew.length; i++)
		 {
			 if(e.getSource()==this.bew[i])
			 {											
				if(bewertung==i)
				{
					Sterne(proz, false);
					anders=false;
					bewertung=-1;
				}
				else
				{
					Sterne(i+1, true);	
					anders=true;
					bewertung=i;
					try
					{
						String body = "Mod=" + jList.getSelectedValue().toString() + "&Version=" + Version +"&Rating=" +  String.valueOf((i+1));
						new Download().post("http://www.minecraft-installer.de/ratemod.php", body);							
					} 
					catch (IOException e1) {}					
				}					
			 }
		 }
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		Object s = e.getSource();		
		
		for(int i=0; i<bew.length; i++)
		{
			 if(s==this.bew[i]&&(!anders))
			 {				
				 Sterne(i+1, true);				
			 }	
		}
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		Object s = e.getSource();		
		
		for(int i=0; i<bew.length; i++)
		{
			 if(s==this.bew[i])
			 {
				 if(anders)
					 Sterne(bewertung+1, true);
				 else						 
					 Sterne(proz, false);
			 }
		}
		
	}
	public void mousePressed(MouseEvent e)
	{
		Object s = e.getSource();
	    if (s == jList)
	    {
	      if ((e.getClickCount() == 2) || (e.getButton() == 3))
	        ModAuswahl();
	      else if (e.getButton() == 1) 
	      {	    	  
	        SwingUtilities.invokeLater(new Runnable()
	        {
	          public void run()
	          {
	            if ((jListModel.getSize() > 0) && (jList.isEnabled()))
	            {
	              String Auswahl = (String)jListModel.getElementAt(jList.getSelectedIndex());
	              if (!modtext.getText().equals(Auswahl)) 
	                setInfoText(Auswahl);
	            }
	          }
	        });
	      }
	    }
	    else if (s == jList2) 
	    {
	      try
	      {
	        if (jList2Model.getSize() <= 0) 
	          return;
	       
	        String Auswahl = (String)jList2Model.getElementAt(this.jList2.getSelectedIndex());
	        if (((String)jList2Model.getElementAt(this.jList2.getSelectedIndex())).substring(0, 1).equals("+"))
	        {
	          pane.setText(Read.getTextwith("seite2", "import"));
	          pane.setCaretPosition(0);
	          modtext.setText(Read.getTextwith("seite2", "text3"));
	          hyperlink = "http://www.minecraft-installer.de/faq.php";
	          Sterne(0.0D, true);
	          if (e.getClickCount() == 2) {
	            new Import(Auswahl.substring(2), mineord, Modloader, stamm);
	          }
	        }
	        else if ((e.getClickCount() == 2) || (e.getButton() == 3))
	        {
	          ModEntfernen();
	        }
	        else if (e.getButton() == 1)
	        {
	          SwingUtilities.invokeLater(new Runnable()
	          {
	            public void run()
	            {
	              if ((Menu.jList2Model.getSize() > 0) && (Menu.this.jList2.isEnabled()))
	              {
	                String Auswahl = (String)Menu.jList2Model.getElementAt(jList2.getSelectedIndex());
	                if (!modtext.getText().equals(Auswahl))
	                 setInfoText(Auswahl);	                
	              }
	            }
	          });
	        }
	      }
	      catch (Exception ex) {}
	    } 
	    else if ((s == weiter) && (weiter.isEnabled())) 
	    {
	      weiter_ActionPerformed(e);
	    }
  }

	public void mouseReleased(MouseEvent arg0) {		
	}

	public void actionPerformed(ActionEvent e) 
	{
		Object s = e.getSource();
		 if (s == ChVers) 
			 versioneinstellen();		 
	} 
}
