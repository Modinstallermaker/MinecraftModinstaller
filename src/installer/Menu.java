package installer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.html.HTMLEditorKit;

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
	
	private JList jList1 = new JList();
	private JScrollPane jList1ScrollPane = new JScrollPane(jList1);
	private DefaultListModel jList1Model = new DefaultListModel();
	
	private JList jList1b = new JList();
	private JScrollPane jList1bScrollPane = new JScrollPane(jList1b);
	private DefaultListModel jList1bModel = new DefaultListModel();
	
	private JList jList2 = new JList();	
	private JScrollPane jList2ScrollPane = new JScrollPane(jList2);
	public static DefaultListModel jList2Model = new DefaultListModel();
	
	DefaultListModel jListModel;
	JList jList;
	Modinfo[] Info;
	
	JTabbedPane tabbedPane = new JTabbedPane();
		
	public static JButton weiter = new JButton();
	
	private JEditorPane pane;  
	private JScrollPane scroller;
	
	private JLabel uberschrift = new JLabel();
	private JLabel versionstext = new JLabel();
	private JLabel modtext = new JLabel();
	private JLabel popu = new JLabel();
	private JLabel[] bew = new JLabel[5];	
	private JLabel banner = new JLabel();
	private JLabel listleft = new JLabel();
	private JLabel listright = new JLabel();
	private JLabel pfeilrechts = new JLabel();
	private JLabel pfeillinks = new JLabel();
	private JLabel importbutton = new JLabel();
	private JLabel restore = new JLabel();
	private JLabel hilfe = new JLabel();
	private JLabel link = new JLabel();
	private JLabel beenden = new JLabel();
	
	private JCheckBox check = new JCheckBox();
	private JCheckBox check2 = new JCheckBox();	
	
	private JComboBox ChVers;

	private Method shapeMethod, transparencyMethod;
	private Class<?> utils;	
	private JPanel cp;		
	
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);
	private boolean aktual = true, Modloader=true;		
		
	private String webplace = Start.webplace, mineord = Start.mineord, stamm = Start.stamm, Version = Start.Version;	
	private boolean online = Start.online;
	private String hyperlink = Read.getTextwith("seite2", "web");
	public static int zahl;
	private Modinfo[] ModloaderInfo, ForgeInfo;
	private String[] ModloaderList, ForgeList, ModList;	
	private double proz=0;
	private boolean anders=false;
	private double bewertung=0;
	
	private int hoehe =700, breite=1000;
	
	public Menu() 
	{
		try 
		{
			if(new OP().optionReader("design").equals("default"))
			{
				setUndecorated(true);
				setSize(breite, hoehe);				
				try 
				{
					utils = Class.forName("com.sun.awt.AWTUtilities");
					shapeMethod = utils.getMethod("setWindowShape", Window.class,Shape.class);
					shapeMethod.invoke(null, this, new RoundRectangle2D.Double(0, 0,breite, hoehe, 20, 20));
					transparencyMethod = utils.getMethod("setWindowOpacity",Window.class, float.class);
					transparencyMethod.invoke(null, this, .95f);
				} 
				catch (Exception ex) 
				{
					ex.printStackTrace();
				}
				cp = new GraphicsPanel(false, "src/page-bg.jpg");
				cp.setBackground(Color.decode("#b0b4b7"));
			}
			else
			{
				setSize(breite+5, hoehe+30);		
				cp = new JPanel();				
				cp.setBackground(Color.decode("#b0b4b7"));
			}
		} 
		catch (Exception e1) 
		{			
			e1.printStackTrace();
		} 
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);
		setResizable(false);
		
		cp.setLayout(null);
		add(cp);

		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());

		uberschrift.setBounds(0, 0, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		uberschrift.setText("Minecraft Modinstaller");
		uberschrift.setHorizontalAlignment(SwingConstants.CENTER);
		uberschrift.setVerticalAlignment(SwingConstants.CENTER);
		uberschrift.setFont(Start.lcd.deriveFont(Font.PLAIN,40));
		cp.add(uberschrift);
	
		if(Start.Versionen!=null&&Start.Versionen.length>0)  //MC Version ändern
		{
			ChVers = new JComboBox(Start.Versionen);
			for (int ka =0; ka<Start.Versionen.length; ka++)	
			{
				if(Start.Versionen[ka].equals(Version))
					ChVers.setSelectedIndex(ka);
			}
			ChVers.setBounds((int)(breite-80), (int)(hoehe*0.02), 67, 25);
			ChVers.addActionListener(this);			
			if(Start.Versionen.length==1) ChVers.setEnabled(false);
			cp.add(ChVers);		
		
			versionstext.setBounds((int)(breite-135), (int)(hoehe*0.02), 110, 25);
			versionstext.setText("Minecraft");
			versionstext.setVerticalAlignment(SwingConstants.CENTER);
			versionstext.setHorizontalAlignment(SwingConstants.LEFT);
			cp.add(versionstext);
		}
		
		int rand = 20;
		int uber = (int)(hoehe*0.09);
		int listenb = (int)(breite*0.2);
		int listenh = (int)(hoehe*0.7);
		int textb = (int)(breite*0.4);
		int texth = (int)(hoehe*0.5);
				
		
		listleft.setBounds(rand, rand+uber, listenb, 20);  //Liste1 Überschrift
		listleft.setHorizontalAlignment(SwingConstants.CENTER);
		listleft.setText(Read.getTextwith("seite2", "modv"));		
		cp.add(listleft);
		
		jList1Model.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Modloader
		jList1.setModel(jList1Model);
		jList1.setCellRenderer(new CellRenderer());				
		jList1.addMouseListener(this);
		
		jList1bModel.addElement(Read.getTextwith("seite2", "wait2")); //Liste1 Forge
		jList1b.setModel(jList1bModel);
		jList1b.setCellRenderer(new CellRenderer());				
		jList1b.addMouseListener(this);
		
		tabbedPane.addTab( "Modloader", jList1ScrollPane);
		tabbedPane.addTab( "Forge", jList1bScrollPane);
		tabbedPane.setEnabled(false);
		tabbedPane.addMouseListener(this);
		tabbedPane.setBounds(rand, rand+uber+20, listenb, listenh);
		cp.add(tabbedPane);
		
		listright.setBounds(breite-rand-listenb, (int)(hoehe*0.32), listenb, 20); //Liste2 Überschrift
		listright.setHorizontalAlignment(SwingConstants.CENTER);
		listright.setText(Read.getTextwith("seite2", "modi"));		
		cp.add(listright);
		
		jList2Model.addElement("");    // Liste2
		jList2.setModel(jList2Model);   
		jList2.setCellRenderer(new CellRenderer());  
		jList2.addMouseListener(this);
		jList2ScrollPane.setBounds(breite-rand-listenb,  (int)(hoehe*0.35), listenb, (int)(hoehe*0.5));
		cp.add(jList2ScrollPane);
		
		pfeilrechts.setBounds(rand+listenb+10+textb+20, 300, 100, 83); // Pfeil nach rechts		
		pfeilrechts.setIcon(new ImageIcon(this.getClass().getResource("src/pfeil_rechts.png")));		
		pfeilrechts.setToolTipText(Read.getTextwith("seite2", "text1"));
		pfeilrechts.addMouseListener(this);
		pfeilrechts.setCursor(c);
		cp.add(pfeilrechts);

		pfeillinks.setBounds(rand+listenb+10+textb+20, 390, 100, 83); // Pfeil nach links		
		pfeillinks.setIcon(new ImageIcon(this.getClass().getResource("src/pfeil_links.png")));	
		pfeillinks.setToolTipText(Read.getTextwith("seite2", "text2"));	
		pfeillinks.addMouseListener(this);
		pfeillinks.setCursor(c);
		cp.add(pfeillinks);	
		
		importbutton.setBounds(breite-rand-listenb+10, (int)(hoehe*0.25), 180, 40); // Mods importieren				
		importbutton.setText(Read.getTextwith("seite2", "text3"));
		importbutton.setFont(importbutton.getFont().deriveFont(Font.BOLD));
		importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/import.png")));	
		importbutton.addMouseListener(this); 
		importbutton.setCursor(c);		
		cp.add(importbutton);
		
		restore.setBounds(breite-rand-listenb+10, (int)(hoehe*0.17), 180, 40); // Restore druchführen	
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
		
		hilfe.setBounds(10, 5, 50, 50); // FAQ anzeigen			
		hilfe.setIcon(new ImageIcon(this.getClass().getResource("src/hilfe_n.png")));
		hilfe.setToolTipText(Read.getTextwith("seite2", "text6"));	
		hilfe.addMouseListener(this); 		
		cp.add(hilfe);
			
		modtext.setBounds(rand+listenb+10, (int)(hoehe*0.2), textb, 30);           //Modname
		modtext.setText(Read.getTextwith("seite2", "text7"));		
		modtext.setHorizontalAlignment(SwingConstants.CENTER);
		modtext.setFont(new Font("Dialog", Font.BOLD, 25));
		cp.add(modtext);
		
		link.setBounds(rand+listenb+280, (int)(hoehe*0.28), 160, 40); // Link zur Modwebseiten		
		link.setFont(link.getFont().deriveFont(Font.BOLD));
		link.setIcon(new ImageIcon(this.getClass().getResource("src/link_n.png")));
		link.setText(Read.getTextwith("seite2", "text8"));	
		link.addMouseListener(this); 
		link.setCursor(c);
		cp.add(link);
		
		for (int i=0; i<5; i++) //Sterne für Bewertung
		{
			bew[i] = new JLabel();
			bew[i].setBounds(rand+listenb+20+i*25, (int)(hoehe*0.275), 40, 40);
			bew[i].setCursor(c);	
			bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));
			bew[i].addMouseListener(this);
			cp.add(bew[i]);
		}
		
		popu.setBounds(rand+listenb+130, (int)(hoehe*0.31), 100, 40);                                //Prozent der Moddownloads
		popu.setText(Read.getTextwith("seite2", "wait2"));
		popu.setHorizontalAlignment(SwingConstants.LEFT);
		popu.setFont(new Font("Dialog", Font.PLAIN, 10));
		cp.add(popu);
		
		HTMLEditorKit kit = new HTMLEditorKit();
		try 
		{				
			
				
		} 
		
		catch (Exception e) {}		
		
		pane = new JTextPane(); //Beschreibungsfenster
		pane.setEditable(false);
	    pane.setContentType("text/html");
	    pane.setEditorKit(kit);  	    
	    pane.setText(Read.getTextwith("seite2", "wait"));	  
	    
	    scroller = new JScrollPane(pane);
	    scroller.setBounds(rand+listenb+15, (int)(hoehe*0.35), textb, texth);
	    cp.add(scroller);
	    
	    check.setBounds(rand+listenb+40, (int)(hoehe*0.36) +texth, 150, 25);                        //Texte laden?
		check.setText(Read.getTextwith("seite2", "text11"));
		check.setOpaque(false);
		check.setSelected(true);
	
		try 
		{
			if(new OP().optionReader("loadtexts").equals("false"))			
			{
				aktual = false;
				link.setEnabled(false);
				check.setSelected(false);
				for(int i=0; i<5; i++)
					bew[i].setEnabled(false);
				popu.setEnabled(false);
			}
		} 
		catch (Exception e2) 
		{			
			e2.printStackTrace();
		}	
		
		check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				check_ItemStateChanged(evt);
			}
		});
		if(!online) check.setEnabled(false);
		cp.add(check);		
		
		check2.setBounds(rand+listenb+200, (int)(hoehe*0.36) +texth, 200, 25);                             //Design?
		check2.setText(Read.getTextwith("seite2", "text11a"));
		check2.setOpaque(false);
		check2.setSelected(true);		
		try 
		{
			if(new OP().optionReader("design").equals("simple"))			
				check2.setSelected(false);
		} 
		catch (Exception e1) 
		{
			
			e1.printStackTrace();
		}
		check2.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				check2_ItemStateChanged(evt);
			}
		});				
		cp.add(check2); 
		
		beenden.setBounds(rand, (int)(hoehe*0.92), 150, 40); //Beenden	
		beenden.setText(Read.getTextwith("seite2", "text9"));		
		beenden.setIcon(new ImageIcon(this.getClass().getResource("src/exit.png")));	
		beenden.setForeground(Color.DARK_GRAY);
		beenden.setFont(beenden.getFont().deriveFont(Font.BOLD));	
		beenden.addMouseListener(this);
		beenden.setCursor(c);		
		cp.add(beenden);

		weiter.setBounds((int)(breite-200), (int)(hoehe*0.92), 180, 40); // Installieren		
		weiter.setText(Read.getTextwith("seite2", "text10"));
		weiter.setFont(weiter.getFont().deriveFont((float) 15));
		weiter.setMargin(new Insets(2, 2, 2, 2));
		weiter.addActionListener(this);
		weiter.setCursor(c);
		weiter.setEnabled(false);
		cp.add(weiter);			
	    
	    setVisible(true);	
		
	    laden();	 		
	}
	
	public void laden()
	{
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setEnabled(false);
		
		if(online)
		{			
		   SwingUtilities.invokeLater(new Runnable() 
		   {				      
				 public void run() 
				 { 	
					 Download(webplace + Version + "/quellen.txt", new File(stamm +"/Modinstaller/Modloader.txt"));		
							
					 
					 try 
					 {
						if(new Download().size(webplace + Version + "/Forge_Mods/quellen.txt")!=0)
						{		
							Download(webplace + Version + "/Forge_Mods/quellen.txt", new File(stamm +"/Modinstaller/Forge.txt"));	
							tabbedPane.setEnabled(true);		
							tabbedPane.setSelectedIndex(1);
							ForgeMode();	
						}	
						else
						{
							ModloaderMode();
						}
					} 
					catch (Exception e) 
					{
						 ModloaderMode();	
					}
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
			jList2Model.removeAllElements();		
			for(int i=0; i<5; i++)
				bew[i].setEnabled(false);
			popu.setEnabled(false);
			importbutton.setEnabled(true);
			try
			{
				pane.setText(Read.getTextwith("seite2", "offline"));
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
	    		setInfoText((String)jList2Model.get(jList2.getSelectedIndex()));		    	
	    	}
    	}
    	else
    	{	 
    		if(!jListModel.isEmpty())
	    	{	    		
	    		setInfoText((String)jListModel.get(jList.getSelectedIndex()));	  		  	
	    	}    		
    	}
	}
	
	public void ModloaderMode()
	{			
		jListModel = jList1Model;
		jList = jList1;
		Info = ModloaderInfo;			
		ModList = ModloaderList;
		Modloader=true;
		change();
	}

	public void ForgeMode() 
	{		
		jListModel = jList1bModel;
		jList = jList1b;
		Info = ForgeInfo;
		ModList = ForgeList;
		Modloader=false;	
		change();		
	}
	
	public void change()
	{	
		File datei;
		if(Modloader)
			datei = new File(stamm +"/Modinstaller/Modloader.txt");
		else
			datei = new File(stamm +"/Modinstaller/Forge.txt");
		
		jList1Model.removeAllElements();
		jList1bModel.removeAllElements();
		jList2Model.removeAllElements();		
		
		if(online)
			try
			{
				ModList = new OP().Textreader(datei);				
				Info = new Modinfo[ModList.length];
				String[] modnamen = new String[ModList.length];
				for (int k=0; k<ModList.length; k++)
				{		
					modnamen[k]= ModList[k].split(";")[0];
				}
				Arrays.sort(modnamen);
				for (int l=0; l<ModList.length; l++)
				{		
					jListModel.addElement(modnamen[l]);
					if(aktual)				
						Info[l] = new Modinfo(modnamen[l]);					
				}
				
				if(aktual)
					new Thread()  //Bewertungen
					{			
						public void run() 
						{							
							try 
							{
								File rating = new File(stamm +"/Modinstaller/bewertungen.txt");
								Download("http://minecraft-installer.de/proz3.php?MC="+Version, rating);
								String[] Bew = new OP().Textreaders(rating).split(";");
								
								
								for (int k=0; k<ModList.length; k++)	
								{
									boolean gefunden =false;
									loop1 : for(int i=0; i<Bew.length; i++)
										if(Bew[i].split(":")[0].equals(Info[k].getModname()))
										{
											Info[k].setRating(Bew[i].split(":")[1]);	
											gefunden=true;
											continue loop1;
										}
									if(!gefunden)
										Info[k].setRating("error");
								}
								
							} 
							catch (IOException e) 
							{
								for (int k=0; k<ModList.length; k++)
								{
									if(Info[k].getRating()==0)
										Info[k].setRating("error");
								}
							}						
						}
					}.start();
				
				new OP().del(new File(stamm +"/Modinstaller/Import"));
				new OP().del(new File(stamm +"/Modinstaller/zusatz.txt"));
				jList.setSelectedIndex(0);
			}
			catch (Exception ex)
			{			
			}			
		loadText();
	}
	
	public void Download(String URL, File target)
	{			
		try 
		{			
			new Download().downloadFile(URL, new FileOutputStream(target));				
		} 
		catch (Exception ex) 
		{
			offline(true);
		}		
	}
	
	public void versioneinstellen() //Version ändern
	{			
		Modloader = true;	
		
		Start.Version = Start.Versionen[ChVers.getSelectedIndex()];
		Version = Start.Versionen[ChVers.getSelectedIndex()];
		SwingUtilities.invokeLater(new Runnable() 
		 {				      
			 public void run() 
			 {
				 try 
				 {
						String[] vers = Version.split("\\.");
						boolean vorhanden=false;
						if(Integer.parseInt(vers[0])>0)
						{	
							if(Integer.parseInt(vers[0])==1&&Integer.parseInt(vers[1])>3)
							{
								vorhanden=true;
							}
							if(Integer.parseInt(vers[0])>1)
							{
								vorhanden=true;
							}
						}
						if(vorhanden)
						{			
							laden();			
						}
						else
						{
							JOptionPane.showMessageDialog(null, Read.getTextwith("seite1", "inco"), Read.getTextwith("seite1", "incoh"), JOptionPane.INFORMATION_MESSAGE); //�ndern					
							online = false;					
							new OP().del(new File(stamm + "/Modinstaller/modlist.txt"));					
						}
						
				 } 
				 catch (Exception ex) 
				 {
					new Error(Read.getTextwith("seite2", "error1")+ String.valueOf(ex)+ "\n\nErrorcode: S2x03", Version);	
					new Browser("http://www.minecraft-installer.de/verbindung.htm");
				 }
				 
				 try 												// Wenn Minecraft aktueller
					{ 
						String lastmc = new OP().optionReader("lastmc");
						
						if (!lastmc.equals("n/a")&&!lastmc.equals(Version))
						{	
							new OP().del(new File(stamm + "/Modinstaller/Mods"));				
							new OP().del(new File(stamm + "/Modinstaller/Original"));			
							new OP().del(new File(stamm + "/Modinstaller/Mods/forge.zip"));	
							new OP().del(new File(stamm + "/Modinstaller/Mods/Forge"));	
						}			
					} 
					catch (Exception ex) 
					{
						new Error(String.valueOf(ex) +"\n\nErrorcode: S2xak", Version);
					}	
			 }
		 });	
	}	
	
	private void setInfoText(String modname) //Modbeschreibung anzeigen
	{			
		modtext.setText(modname);
					
		popu.setText(Read.getTextwith("seite2", "wait2"));
		Sterne(0, false);	
		anders=false;
		
		if(aktual)
		{			
			for(int i=0; i<Info.length; i++)
			{
				if(Info[i].getModname().equals(modname))
				{	
					zahl=i;
					new Thread() 
					{
						public void run()
						{
						  int i= Menu.zahl;		
						  boolean ende=false;
						  do
						  {
							  if(Info[i]!=null&&Info[i].fertig())
							  {	
							    ende=true;    							   
							  }								  
						  }
						  while(!ende);	
						  if(ende)
						  {	
							 try
						     {								
							 	String inh = Info[i].getDescription();
								hyperlink = Info[i].getHyperlink();
							    
								if(!inh.startsWith("<html>"))
								{
									inh="<html><body>"+inh+"</body></html>";
								}
								pane.setText(inh);
								pane.setCaretPosition(0);
								interrupt();
						     }
						     catch(Exception e){}	
						  }
						}
					}.start();						
					
					new Thread() 
					{
						public void run()
						{
						  int i= Menu.zahl;		
						  boolean ende=false;
						  do
						  {
							  if(Info!=null&&Info.length>i)
							  {
								  try
								  {
									  if(Info[i].fertig2())
										  ende=true;  
									  if(!ende)
									  try {
											Thread.sleep(100);
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
								  }
								  catch (Exception ex)
								  {
									  
								  }
							  }							 
						  }
						  while(!ende);	
						  if(ende)
						  {	
							 try
						     {
					        	proz = Info[i].getRating();        	
								popu.setText(String.valueOf(proz)+"%");
								if(proz==-1)
									popu.setText("Not available");
								Sterne(proz, false);
								interrupt();
						     }
						     catch(Exception e){}	
						  }
						}
					}.start();		
				}
			}	
		}
		else
		{
			pane.setText(Read.getTextwith("seite2", "text12"));
		}
	}
	
	public void ModAuswahl() // Auswählen von Mods
	{	
		String modname = (String)jList.getSelectedValue();
			
		if (searchentry(jListModel, modname)) 
		{
			jList2Model.add(jList2Model.getSize(),modname);
			jListModel.removeElement(modname);
			
			
			for (int i=0; i<Info.length; i++)
			{	
				if(Info[i]!=null&&Info[i].getModname().equals(modname))
				{
					String comp = Info[i].getCompatibleWith();					
					if(!comp.equals("error"))
					{
						String[] spl = comp.split(";");
						for (int j=0; j<spl.length; j++)
						{
							if (searchentry(jListModel, spl[j])) 
							{
								jList2Model.add(jList2Model.getSize(),spl[j]);
								jListModel.removeElement(spl[j]);
							}
						}
					}
					if(Info[i]!=null&Info[i].getIncompatibleWith().equals("allother")) //Alle verbieten mit allother
					{
						jList.setEnabled(false);
					}
				}
			}
		}			
		weiter.setEnabled(true); // Installieren Knopf freischalten									
	}
	
	public boolean searchentry(DefaultListModel model, String modname) //In ListModel einen Modeintrag finden
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
							new Error(Read.getTextwith("seite2", "error2") + String.valueOf(e)+ "\n\nErrorcode: S2x06", Version);
						}
						
						try 
						{
							new OP().Textwriter(zus, neutext.split(";;"), false);
						} 
						catch (Exception e) 
						{
							new Error(Read.getTextwith("seite2", "error2") + String.valueOf(e)+ "\n\nErrorcode: S2x07", Version);
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

	public void weiter_ActionPerformed(ActionEvent evt) // Installieren Knopf
	{		
		
		String[] zeilen = new String[jList2Model.getSize()];
		String[] namen = new String[jList2Model.getSize()];	
		int[] anzahl = new int[jList2Model.getSize()];	
		
		String h="";
		try 
		{			
			if(online==true)
			{
				File modli;
				if(Modloader)	
					modli = new File(stamm +"/Modinstaller/Modloader.txt");
				else
					modli = new File(stamm +"/Modinstaller/Forge.txt");
				
				if(modli.exists())
				{
					String[] lines = new OP().Textreader(modli);
					for (int i =0; i<lines.length; i++)
					{
						String[] spl = lines[i].split(";");
						for (int j = 0; j < jList2Model.getSize(); j++) 
						{							
							if (spl[0].equals(jList2Model.getElementAt(j))) // Wenn Name des Mods in Liste2 identisch
							{
								namen[j] = spl[0];
								zeilen[j] = spl[1];                           // Speicherort in zeilen speichern
								anzahl[j] = Integer.parseInt(spl[2]);
								h+=spl[0]+";;;";						// Anzahl der Dateien pro Mod in auswahlzahl speichern
								
								try 
								{
									String body = "Minecraft=" + URLEncoder.encode(Version, "UTF-8" ) + "&" + "Mod=" + URLEncoder.encode(spl[0], "UTF-8" ) + "&" + "OP=" + URLEncoder.encode(System.getProperty("os.name").toString() + "; " + System.getProperty("os.version").toString() + "; " + System.getProperty("os.arch").toString(), "UTF-8" ) + "&" + "InstallerVers=" + URLEncoder.encode(Read.getTextwith("installer", "version"), "UTF-8");
									new Download().post("http://www.minecraft-installer.de/modstat.php", body);
								} 
								catch (Exception e) 
								{										
								}
							}
						}
					}
					if(h.length()>2) 
					{
						h.substring(h.length()-3);						
					}
				}			
			}
			dispose();			
			new Installieren(namen, zeilen, anzahl, Modloader);
		} 
		catch (Exception ex) 
		{	
			new Error(new OP().getStackTrace(ex) + "\n\nErrorcode: S2x09", Version);	
		}		
	}

	public void ModsImportieren() //Mods importieren
	{
		pane.setText(Read.getTextwith("seite2", "import"));	
		pane.setCaretPosition(0);
		modtext.setText(Read.getTextwith("seite2", "text3"));
		hyperlink="http://www.minecraft-installer.de/faq.php";
		Sterne(0, false);
		popu.setText("n/a");
		new Modimport(Modloader);			
	}
	
	public void Restore() //Letzte Modinstallation wiederherstellen
	{
		new OP().del(new File(mineord+"/versions/Modinstaller"));
		try 
		{
			new OP().copy(new File(stamm+"/Modinstaller/Backup"), new File(mineord+"/versions/Modinstaller"));
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
	
	
	
	public void check_ItemStateChanged(ItemEvent evt) //Modtexte mit Bewertung laden
	{		
		if (evt.getStateChange() == 1) 
		{
			SwingUtilities.invokeLater(new Runnable() 
			 {				      
				 public void run() 
				 { 
					 try 
					 {
						 new OP().optionWriter("loadtexts", "true");
					 } 
					 catch (Exception e) 
					 {							
						 e.printStackTrace();
					 }
					aktual = true;
					link.setEnabled(true);											
					for(int i=0; i<5; i++)
						bew[i].setEnabled(true);
					popu.setEnabled(true);
					change();
				 }
			 });				
		} 
		else 
		{
			try {
				new OP().optionWriter("loadtexts", "false");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i=0; i<5; i++)
				bew[i].setEnabled(false);
			popu.setEnabled(false);
			aktual = false;
			link.setEnabled(false);
		}	
	}
	
	public void check2_ItemStateChanged(ItemEvent evt) //Design einstellen
	{		
		if (evt.getStateChange() == 1) 
		{	
			try {
				new OP().optionWriter("design", "default");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispose();
			new Menu();
		} 
		else 
		{			
			try {
				new OP().optionWriter("design", "simple");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispose();
			new Menu();			
		}		
	}
	
	private void Sterne(double bewe, boolean anders) //Bewertung grafisch umsetzen
	{
		bewe*=2;		
		bewe = Math.round(bewe);
		bewe/=2;		
		for(int i=0; i<5; i++)
		{
			if(bewe>=1)
			{
				if(!anders)
					bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star1.png")));	
				else
					bew[i].setIcon(new ImageIcon(this.getClass().getResource("src/star1b.png")));	
			}
			else if(bewe==0.5)
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
	    /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) 
	    {  
           super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );  
           list.setFixedCellHeight(25);     
           setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
           
           if(isSelected) 
           {
        	   setBackground(new Color(0xebebeb));   
               setForeground(Color.black);            
               setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
           }
           
           if(cellHasFocus) 
           {
        	   setBackground(new Color(0xe0e0e0));   
               setForeground(Color.black);            
               setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
           }
        
           
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
			 Restore();
		 else if(s==hilfe)
			 new Browser(Read.getTextwith("seite2", "web")+"/faq.php");
		 else if(s==link)
			 new Browser(hyperlink);	
		 else if(s==beenden)
			 System.exit(0);
		 else if(s==tabbedPane)
		 {
			 if(tabbedPane.getSelectedIndex()==0)
				ModloaderMode();
			 else
				ForgeMode();
		 }
		
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
					}					
				 }
			}
	}

	@Override
	public void mouseEntered(MouseEvent e) 
	{
		Object s = e.getSource();
		if(s==pfeilrechts)
			pfeilrechts.setIcon(new ImageIcon(this.getClass().getResource("src/pfeil_rechts_over.png")));
		else if(s==pfeillinks)
			pfeillinks.setIcon(new ImageIcon(this.getClass().getResource("src/pfeil_links_over.png")));
		else if(s==importbutton)
		{
			importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/import_hover.png")));
			importbutton.setForeground(Color.decode("#3333ff"));
		}
		else if(s==restore)
		{
			 if(restore.isEnabled())
	    	  {
		    	  restore.setIcon(new ImageIcon(this.getClass().getResource("src/restore_hover.png")));
		    	  restore.setForeground(Color.decode("#006000"));
	    	  }
		}
		else if (s==beenden)
		{
			 beenden.setIcon(new ImageIcon(this.getClass().getResource("src/exit_hover.png")));
	    	 beenden.setForeground(Color.RED);
		}
		
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
		if(s==pfeilrechts)
			pfeilrechts.setIcon(new ImageIcon(this.getClass().getResource("src/pfeil_rechts.png")));
		else if(s==pfeillinks)
			pfeillinks.setIcon(new ImageIcon(this.getClass().getResource("src/pfeil_links.png")));
		else if(s==importbutton)
		{
			importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/import.png")));
			importbutton.setForeground(Color.BLACK);
		}
		else if(s==restore)
		{
			if(restore.isEnabled())
	    	  {
		    	  restore.setIcon(new ImageIcon(this.getClass().getResource("src/restore.png")));
		    	  restore.setForeground(Color.BLACK);
	    	  }
		}
		else if(s==beenden)
		{
			 beenden.setIcon(new ImageIcon(this.getClass().getResource("src/exit.png")));
			 beenden.setForeground(Color.DARK_GRAY);
		}
		
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

	@Override
	public void mousePressed(MouseEvent e) 
	{
		Object s = e.getSource();
		 if (s == jList1) 
		 {
			 if(e.getClickCount()==2||e.getButton()==3)
			 {
				ModAuswahl();
			 }
			 else
			 {
				 if(e.getButton()==1)
				SwingUtilities.invokeLater(new Runnable() 
				{				      
				       public void run() 
				       { 
				    	   	if (jList1Model.getSize()>0 && jList1.isEnabled() && online==true) 
							{
							    String Auswahl = (String) jList1Model.getElementAt(jList1.getSelectedIndex());
							    if(!modtext.getText().equals(Auswahl))
							    {								    	
						           setInfoText(Auswahl);						           
							    }
							}				    	  
				       }
				 });
			 }
		 }
		 else if (s == jList1b) 
		 {
			 if(e.getClickCount()==2||e.getButton()==3)
			 {
				ModAuswahl();
			 }
			 else
			 {
				 if(e.getButton()==1)
					 SwingUtilities.invokeLater(new Runnable() 
					 {				      
				       public void run() 
				       { 				    	   
				    	   	if (jList1bModel.getSize()>0 && jList1b.isEnabled() && online==true) 
							{
							    String Auswahl = (String) jList1bModel.getElementAt(jList1b.getSelectedIndex());
							    if(!modtext.getText().equals(Auswahl))
							    {								    	
						           setInfoText(Auswahl);						           
							    }
							}
				       }
					 });
			 }
		 }
		 else if(s == jList2)
		 {
			 try 
				{	
					if (jList2Model.getSize()>0)
					{
					    String Auswahl = (String) jList2Model.getElementAt(jList2.getSelectedIndex());     
											
						if(((String) jList2Model.getElementAt(jList2.getSelectedIndex())).substring(0, 1).equals("+"))
						{		
							pane.setText(Read.getTextwith("seite2", "import"));	
							pane.setCaretPosition(0);
							modtext.setText(Read.getTextwith("seite2", "text3"));
							hyperlink="http://www.minecraft-installer.de/faq.php";
							Sterne(0, true);
							popu.setText("n/a");
							if(e.getClickCount()==2)
							{
								new Modimport(Auswahl.substring(2), mineord, Modloader, stamm);
							}						
						}
						else
						{	
							if(e.getClickCount()==2||e.getButton()==3)
							{
								ModEntfernen();
							}	
							else
							{
							 if(!modtext.getText().equals(Auswahl)&&online==true&&e.getButton()==3)
							 {
								 SwingUtilities.invokeLater(new Runnable() 
								 {				      
									 public void run() 
									 { 
										String Auswahl = (String) jList2Model.getElementAt(jList2.getSelectedIndex());   										
										pane.setText(Read.getTextwith("seite2", "import"));	
										modtext.setText(Read.getTextwith("seite2", "text3")); setInfoText(Auswahl);										
									 }
								 });
							 }
							}
						}
					}
				}
	    	   catch(Exception ex)
	    	   {	    		  
	    	   }	
		 }
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object s = e.getSource();
		 if (s == ChVers) 		
		 {
			 jList1Model.removeAllElements();
			 jList2Model.removeAllElements();
			 jList1Model.addElement(Read.getTextwith("seite2", "wait2"));
			 pane.setText(Read.getTextwith("seite2", "wait"));
			 versioneinstellen();
		 }
		 
		 else if(s==weiter)
			 weiter_ActionPerformed(e);		 
	}  
}
