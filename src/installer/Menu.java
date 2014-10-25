package installer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
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
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
		
	private JEditorPane pane;  
	private JScrollPane scroller;
	
	private JLabel uberschrift = new JLabel();
	private JLabel versionstext = new JLabel();
	private JLabel modtext = new JLabel();	
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
	private JLabel web = new JLabel();
	private JLabel beenden = new JLabel();
	public static JLabel weiter = new JLabel();	
	
	private JComboBox ChVers;

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
		setUndecorated(true);
		setSize(breite, hoehe);
		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);
		setResizable(false);
		
		cp = new GraphicsPanel(false, "src/bild.png");
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));
		cp.setLayout(null);
		add(cp);

		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());

		uberschrift.setBounds(0, 0, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		uberschrift.setText(Read.getTextwith("installer", "name"));
		uberschrift.setHorizontalAlignment(SwingConstants.CENTER);
		uberschrift.setVerticalAlignment(SwingConstants.CENTER);
		uberschrift.setFont(Start.lcd.deriveFont(Font.BOLD,48));
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
		int texth = (int)(hoehe*0.5-2);
				
		
		listleft.setBounds(rand, rand+uber-5, listenb, 20);  //Liste1 Überschrift
		listleft.setHorizontalAlignment(SwingConstants.CENTER);
		listleft.setFont(importbutton.getFont().deriveFont(Font.BOLD,14));
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
		
		jList1ScrollPane.setBorder(BorderFactory.createEmptyBorder());
		jList1bScrollPane.setBorder(BorderFactory.createEmptyBorder());
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
		tabbedPane.setBounds(rand, rand+uber+20, listenb, listenh);
							
        cp.add(tabbedPane);
		
		listright.setBounds(breite-rand-listenb, (int)(hoehe*0.315), listenb, 20); //Liste2 Überschrift
		listright.setHorizontalAlignment(SwingConstants.CENTER);
		listright.setText(Read.getTextwith("seite2", "modi"));		
		cp.add(listright);
		
		jList2Model.addElement("");    // Liste2
		jList2.setModel(jList2Model);   
		jList2.setCellRenderer(new CellRenderer());  
		jList2.addMouseListener(this);
		jList2ScrollPane.setBounds(breite-rand-listenb,  (int)(hoehe*0.35), listenb, (int)(hoehe*0.5));
		jList2ScrollPane.setBorder(BorderFactory.createEmptyBorder());
		cp.add(jList2ScrollPane);
		
		pfeilrechts.setBounds(rand+listenb+10+textb+20+25, 300, 100, 83); // Pfeil nach rechts		
		pfeilrechts.setIcon(new ImageIcon(this.getClass().getResource("src/hinzufügen.png")));		
		pfeilrechts.setToolTipText(Read.getTextwith("seite2", "text1"));
		pfeilrechts.addMouseListener(this);
		pfeilrechts.setCursor(c);
		cp.add(pfeilrechts);

		pfeillinks.setBounds(rand+listenb+10+textb+20-5, 390, 100, 83); // Pfeil nach links		
		pfeillinks.setIcon(new ImageIcon(this.getClass().getResource("src/löschen.png")));	
		pfeillinks.setToolTipText(Read.getTextwith("seite2", "text2"));	
		pfeillinks.addMouseListener(this);
		pfeillinks.setCursor(c);
		cp.add(pfeillinks);	
		
		importbutton.setBounds(breite-rand-listenb+10, (int)(hoehe*0.23), 180, 40); // Mods importieren				
		importbutton.setText(Read.getTextwith("seite2", "text3"));
		importbutton.setFont(importbutton.getFont().deriveFont(Font.BOLD));
		importbutton.setIcon(new ImageIcon(this.getClass().getResource("src/importkl.png")));	
		importbutton.addMouseListener(this); 
		importbutton.setCursor(c);		
		cp.add(importbutton);
		
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
		
		hilfe.setBounds(2, 5, 50, 50); // FAQ anzeigen			
		hilfe.setIcon(new ImageIcon(this.getClass().getResource("src/help.png")));
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
		link.setIcon(new ImageIcon(this.getClass().getResource("src/infokl.png")));
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
		
		
		HTMLEditorKit kit = new HTMLEditorKit();
	
		pane = new JTextPane(); //Beschreibungsfenster
		pane.setEditable(false);
	    pane.setContentType("text/html");
	    pane.setEditorKit(kit);  	    
	    pane.setText(Read.getTextwith("seite2", "wait"));	  
	
	    
	    scroller = new JScrollPane(pane);
	    scroller.setBounds(rand+listenb+15, (int)(hoehe*0.35), textb, texth);
	    scroller.setBorder(BorderFactory.createEmptyBorder());
	    cp.add(scroller);
	    
	    web.setBounds((int)(breite/2-180), (int)(hoehe*0.94), 300, 20); //Beenden	
	    web.setText(Read.getTextwith("seite2", "web2"));
	    web.setFont(web.getFont().deriveFont(Font.PLAIN, 16));	
	    web.addMouseListener(this);
	    web.setHorizontalAlignment(SwingConstants.CENTER);	 
	    web.setCursor(c);		
		cp.add(web);
	    
		beenden.setBounds(rand, hoehe-40-rand, 150, 40); //Beenden	
		beenden.setText(Read.getTextwith("seite2", "text9"));		
		beenden.setIcon(new ImageIcon(this.getClass().getResource("src/power.png")));			
		beenden.setFont(beenden.getFont().deriveFont(Font.BOLD, 16));	
		beenden.addMouseListener(this);
		beenden.setCursor(c);		
		cp.add(beenden);

		weiter.setBounds((int)(breite-220-rand+5), hoehe-70-rand+10, 220, 70); // Installieren		
		weiter.setText(Read.getTextwith("seite2", "text10"));
		weiter.setFont(weiter.getFont().deriveFont((float) 15));
		weiter.addMouseListener(this);
		weiter.setCursor(c);		
		weiter.setHorizontalTextPosition(SwingConstants.LEFT);
		weiter.setFont(weiter.getFont().deriveFont(Font.BOLD, 22));
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
		tabbedPane.setSelectedIndex(0);
		tabbedPane.setEnabled(false);
		
		if(online)
		{			
		   SwingUtilities.invokeLater(new Runnable() 
		   {				      
				 public void run() 
				 { 	
					 File Modloaderlist = new File(stamm +"/Modinstaller/Modloader.txt");
					 File ForgeList = new File(stamm +"/Modinstaller/Forge.txt");
					 
					 Download(webplace + Version + "/quellen.txt", Modloaderlist);									
					 Download(webplace + Version + "/Forge_Mods/quellen.txt", ForgeList);	
					
					 if(ForgeList.length()!=0) //keine Forge Mods vorhanden
					 {
							tabbedPane.setEnabled(true);	
							
							if(Modloaderlist.length()<ForgeList.length()) //die größerere Modl
							{
								tabbedPane.setSelectedIndex(1);
								ForgeMode();	
							}
							else							
								ModloaderMode();							
					 }
					 else
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
					boolean gefunden =false;
					
					String Mode = "Modloader"; //bereits installierte Mods anzeigen
					if(!Modloader) Mode = "Forge";
					if(new OP().optionReader("lastmc").equals(Version)&&new OP().optionReader("lastmode").equals(Mode))
					{
						String alastm = new OP().optionReader("lastmods");
						String[] lastm = alastm.split(";;");					
						for (int r=0; r<lastm.length; r++)
						{
							if(lastm[r].equals(modnamen[l]))
							{
								jList2Model.addElement(modnamen[l]);
								gefunden=true;
							}
						}
					}
					
					if(!gefunden)
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
									loop1 : for(int i=0; i<Bew.length; i++)	
									{
										if(Info[k]!=null&&Bew[i]!=null)
										{
											String downloadmodname = Bew[i].split(":")[0];
											String listenmodname = Info[k].getModname();
											if(downloadmodname.equals(listenmodname))
											{
												Info[k].setRating(Bew[i].split(":")[1]);													
												continue loop1;
											}
										}										
									}									
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
						}
						
				 } 
				 catch (Exception ex) 
				 {
					new Error(Read.getTextwith("seite2", "error1")+ String.valueOf(ex)+ "\n\nErrorcode: S2x03");	
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
						new Error(String.valueOf(ex) +"\n\nErrorcode: S2xak");
					}	
			 }
		 });	
	}	
	
	private void setInfoText(String modname) //Modbeschreibung anzeigen
	{			
		modtext.setText(modname);
		
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
							  try
							  {
							  if(Info[i]!=null&&Info[i].fertig())
							  {	
							    ende=true;    							   
							  }	
							  }
							  catch (Exception e){}
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
			new Install(namen, zeilen, anzahl, Modloader);
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
        	          
               setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
           }
           
           if(cellHasFocus) 
           {
        	             
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
		 else if(s==web)
		 {			
			 new Browser(Read.getTextwith("seite2", "web"));
		 }
		 else if(s==beenden)
			 System.exit(0);
		 
		
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
							if(e.getClickCount()==2)
							{
								new Import(Auswahl.substring(2), mineord, Modloader, stamm);
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
		 else if(s==weiter)
			 if(weiter.isEnabled())
				 weiter_ActionPerformed(e);	
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
	} 
}
