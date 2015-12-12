package installer;

import static installer.OP.copy;
import static installer.OP.del;
import static installer.OP.getError;
import static installer.OP.optionReader;
import static installer.OP.optionWriter;
import static installer.OP.rename;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Menu extends MenuGUI implements ActionListener, MouseListener, ChangeListener, KeyListener
{
	private static final long serialVersionUID = 1L;	
	public String mineord = Start.mineord, stamm = Start.stamm, mcVersion = Start.mcVersion;	
	private boolean online = Start.online;
	private double proz=0.0,  rating = 0.0;	
	private boolean manual=false;
	private ArrayList<Modinfo> modlArrL = new ArrayList<Modinfo>();
	private ArrayList<Modinfo> forgeArrL = new ArrayList<Modinfo>();
	private ArrayList<Modinfo> proposals = new ArrayList<Modinfo>();
	private Modinfo[] modtexts, moddownloads;
	private JList leftList;	
	private DefaultListModel leftListModel;
	
	private int modID=-1;
	private String YT = "";
	private boolean importmod=false, searchfocus = false, ist=false;
	private String modx="";
	private File impo=new File(stamm+"Modinstaller/Importo");
	private Thread bart = null, picThread =null;
	
	public static boolean isModloader=true;

	public Menu(Modinfo[] modtexts, Modinfo[] moddownloads) 
	{
		this.modtexts=modtexts;
		this.moddownloads=moddownloads;
		GUI();	    
	    setVisible(true);			
	    load();	 		
	}
	
	private void load()
	{
		if(online)
		{			
			SwingUtilities.invokeLater(new Runnable() 
			{					   
				public void run() 
				{ 							
					picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));	
					tabbedPane.setEnabled(false);		
					
					modlArrL.clear();
					forgeArrL.clear();	
					
					del(new File(stamm + "Modinstaller/Import"));
					
					try
					{
						for (int k = 0; k < moddownloads.length; k++)
						{
							if ((moddownloads[k].getMC().equals(mcVersion)) && (moddownloads[k].getCat() == 0))
							{
								modlArrL.add(moddownloads[k]);
							}
							else if ((moddownloads[k].getMC().equals(mcVersion)) && (moddownloads[k].getCat() == 3))
							{
								forgeArrL.add(moddownloads[k]);
							}
						}						
					}
					catch (Exception ex) {
						new Error(getError(ex));	
					}
								    
					if (forgeArrL.size() > 0) 					
						tabbedPane.setEnabled(true);					
					if (forgeArrL.size() >= modlArrL.size()) 					
						setForge();					
					else 					
						setModloader();				
				}
			});			   
		}
		else
		{
			setOffline(true);
		}
	}
	
	private void setOffline(boolean off) //Den Offline Modus vorbereiten
	{
		if(off==true)
		{
			online=false;
			leftListMModel.removeAllElements();
			leftListFModel.removeAllElements();
			rightListModel.removeAllElements();	
			searchInput.setEnabled(false);
			selectArrow.setEnabled(false);
			setImport(true);				
		}
		else
		{
			online=true;	
			searchInput.setEnabled(true);
			selectArrow.setEnabled(true);
		}
	}
	
	
	private void setModloader()
	{		
		resetSelection();
		leftListModel = leftListMModel;
	    leftList = leftListM;
	    isModloader = true;	   
	    proposals = modlArrL;
	    tabbedPane.setSelectedIndex(0);
	    sortOutInstalledMods();
	    ist=true;
	}

	private void setForge() 
	{		
		resetSelection();
		leftListModel = leftListFModel;
	    leftList = leftListF;
	    isModloader = false;	  
	    proposals = forgeArrL;
	    tabbedPane.setSelectedIndex(1);	    
	    sortOutInstalledMods();	 
	    ist=true;
	}
	private void resetSelection()
	{
		for(Modinfo prop: proposals)
			prop.setSelect(false);
	}
	
	private void sortOutInstalledMods()
	{	
		String Mode = "Modloader";
		if (!isModloader) 
			Mode = "Forge";
		if ((optionReader("lastmc").equals(mcVersion)) && (optionReader("lastmode").equals(Mode)))
		{
			try
			{
				String alastm = optionReader("lastmods");
				for (String laste : alastm.split(";;")) 
					for (Modinfo prop: proposals)
						if(laste.matches("[0-9]+"))
							if (Integer.parseInt(laste)==prop.getID())
								prop.setSelect(true);
			}
			catch (Exception e){	
				new Error(getError(e));	
			}
		}
		updateLists();			
		loadTexts();
	}
	
	public void updateLists()
	{
		leftListModel.removeAllElements();
		rightListModel.removeAllElements();
		searchInput.reset();		
		leftList.setEnabled(true);
		leftList.requestFocusInWindow();
		for(Modinfo prop: proposals){
			if(prop.getSelect())
				rightListModel.addElement(prop.getName());
			else
				leftListModel.addElement(prop.getName());
		}
		
		new Thread() //Importiere Mods adden
		{
			public void run()
			{		
				File impf=new File(stamm+"Modinstaller/Import");
				
				String mode ="Forge";
				if(isModloader)
					mode="Modloader";
				
				if(optionReader("lastmc").equals(mcVersion) && optionReader("lastmode").equals(mode))
				{	
					if(impo.exists())
					{
						try {
							rename(impo, impf);
						} catch (Exception e) {
						}	
					}
				}
				
				if(impf.exists())
				{			
					File[] imports = impf.listFiles();
					for(File modi : imports)
					{				
						String name =modi.getName().substring(0, modi.getName().lastIndexOf("."));
						rightListModel.addElement("+ "+name);
					}
				}
			}
		}.start();
		
		if(bart!=null) //Kompatibilität der Mods anzeigen
			bart.interrupt();
		bart = new Thread()
		{
			public void run()
			{
				
				String zeile = "";
				for(Modinfo prop : proposals)
					if(prop.getSelect())
						zeile+=String.valueOf(prop.getID())+";;";	
				if(zeile.length()>1)
					zeile= zeile.substring(0, zeile.length()-2);
				String res ="";
				try {
					res = new Download().post("http://www.minecraft-installer.de/api/compGet.php", "Mods="+zeile);
				} 
				catch (IOException e) {
				}
				if(!res.equals(""))
				{
					int val =-1;
					try
					{
						val = (int)Double.parseDouble(res);
					}
					catch (Exception e){
						val=-1;
					}
					if(val==-1)
					{
						bar.setVisible(false);
					}
					else
					{
						bar.setVisible(true);
						bar.setValue(val);
					}
				}
				else
					bar.setVisible(false);
			}
		};
		bart.start();
	}
	
	private void loadTexts()
	{
		if(leftListModel.isEmpty())  
		{
	    	if(!rightListModel.isEmpty())
	    	{
	    		rightList.setSelectedIndex(0);
	    		setInfoText((String)rightListModel.get(0));		    	
	    	}  
		}
    	else   
    	{
    		leftList.setSelectedIndex(0);
    		setInfoText((String)leftListModel.get(0));	  
    	}
	}
		
	private void changeVersion() //Version ändern
	{	
		ist=false;
		resetSelection();
		modDescPane.setText(Read.getTextwith("seite2", "wait"));
		Start.mcVersion = Start.mcVersionen[mcVersDrop.getSelectedIndex()];
		mcVersion = Start.mcVersionen[mcVersDrop.getSelectedIndex()];
		load();				
	}	
	
	private void setInfoText(final String modname) //Modbeschreibung anzeigen
	{	
		setImport(false);
		modNameLabel.setText(modname);	
		picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));	
		
		for(Modinfo modt : modtexts)
		{
			if(modt.getName().equals(modname))
			{	
				try
			    {								
				 	String inh = modt.getText();
				 	modID= modt.getID();
				 	website =  modt.getSource();
					hyperlink = Read.getTextwith("installer", "website") + "/modinfo.php?modname=" + modname.replace(" ", "+");
					if(!inh.startsWith("<html>"))
					{
						inh="<html><body>"+inh+"</body></html>";
					}				
					modDescPane.setText(inh);							
					modDescPane.setCaretPosition(0);
					manual=false;
					
					for(Modinfo modd : moddownloads)	
					{
						if(modd.getName().equals(modname)&&modd.getMC().equals(mcVersion))
						{
							proz = modd.getRating();
							if(proz > 6.5)	
							{
								topIcon.setIcon(new ImageIcon(this.getClass().getResource("src/top.png")));
								topIcon.setVisible(true);	
							}
							else
								topIcon.setVisible(false);
							
							if(modd.getDate()!=null)								
							{
								long DAY_IN_MS = 1000 * 60 * 60 * 24;								
								Timestamp sevendago = new Timestamp(new Date(System.currentTimeMillis() - (7 * DAY_IN_MS)).getTime());
								Timestamp modst = modd.getDate();
								if(modst.after(sevendago))
								{
									topIcon.setIcon(new ImageIcon(this.getClass().getResource("src/new.png")));
									topIcon.setVisible(true);
								}
							}
							else
								topIcon.setVisible(false);
							
							sizeLabel.setText(new OP().getSizeAsString(modd.getSize()));							
							setRating(proz, false);	
							break;
						}
					}					
			    }
			    catch(Exception e) {
			    	new Error(getError(e));
			    }	
			}
		}
		if(picThread!=null)
			picThread.interrupt();
		picThread = new Thread() 
 	    {
    	  public void run() 
    	  {	 	    		
    		try 
		    {	
    			String url = "http://www.minecraft-installer.de/Dateien/BilderPre/"+modname+".jpg";
    			url = url.replace(" ", "%20");
			 	BufferedImage img = ImageIO.read(new URL(url));			 	
				picture.setIcon((Icon) new ImageIcon(img));	
				img.flush();
				picture.setText("");
			} 
		    catch (Exception e) 
		    {
				picture.setText(Read.getTextwith("seite2", "nopic"));				
				picture.setIcon(null);
			}	
    	 }
	    };
	    picThread.start();
	}
	
	private void selectMod() // Auswählen von Mods
	{	
		Object[] listitems =  leftList.getSelectedValues();
		for (Object listitem : listitems)
			for(Modinfo prop : proposals)
				if(prop.getName().equals(listitem))
					prop.setSelect(true);		
		updateLists();
		
		nextButton.setEnabled(true);	
		if(searchfocus)
			searchInput.requestFocus();
		searchfocus=false;
	}
		
	private void removeMod() // Entfernen von Mods
	{
		if(rightList.isFocusOwner())
		{
			Object[] listitems = rightList.getSelectedValues();
			for (Object listitem : listitems)	
			{
				String name = String.valueOf(listitem);
				if (name.substring(0, 1).equals("+")) // Importierter Mod löschen
				{
					name = name.substring(2);
					del(new File(stamm+"Modinstaller/Import/"+name));
					del(new File(stamm+"Modinstaller/Import/"+name+".jar"));
					del(new File(stamm+"Modinstaller/Importn/"+name+"/"));
				}  
				else  // sonst nach Liste links kopieren
				{			
					leftList.setEnabled(true);
					for(Modinfo prop : proposals)
						if(prop.getName().equals(listitem))
							prop.setSelect(false);	
				}	
			}
			updateLists();
			nextButton.setEnabled(true);
			if (rightListModel.getSize() == 0) 
			{
				nextButton.setEnabled(false); // Wenn keine Mods in Liste2 vorhanden Installieren deaktivieren
			}	
		}
	}

	private void startInstallation() // Installieren Knopf
	{		
		try 
		{	
			ArrayList<Modinfo> selected = new ArrayList<Modinfo>();
			for(Modinfo prop : proposals)
				if(prop.getSelect())
					selected.add(prop);
			dispose();
			new Install(selected, isModloader);
		} 
		catch (Exception ex) 
		{	
			new Error(getError(ex) + "\n\nErrorcode: S2x09");	
		}	
	}

	private void importMod() //Mods importieren
	{			
		setImport(true);
		JFileChooser chooser = new JFileChooser();
		chooser.setMultiSelectionEnabled(true);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Mods (.jar, .zip)", "jar", "zip");
		chooser.setFileFilter(filter);
		int rueckgabeWert = chooser.showOpenDialog(this);      
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
        {
        	for(File modfile : chooser.getSelectedFiles())
        		new Import(modfile, this);	            
        }
	}
	
	public void setImport(boolean yes)
	{
		if(yes)
		{			
	        picture.setIcon(new ImageIcon(this.getClass().getResource("src/importbig.png")));
	         
	        if(isModloader)
	        {
	        	modNameLabel.setText(Read.getTextwith("seite2", "importmh"));
	        	modDescPane.setText(Read.getTextwith("seite2", "importm"));
	        }
	        else
	        {
	        	modNameLabel.setText(Read.getTextwith("seite2", "importfh"));
	        	modDescPane.setText(Read.getTextwith("seite2", "importf"));
	        }	      
		}
		modDescPane.setCaretPosition(0);
		importmod =yes;
		modinstWebLnk.setVisible(!yes);
		modVersionL.setVisible(yes);
		topIcon.setVisible(!yes);
		for (JLabel ic : ratIcons)
        	ic.setVisible(!yes);
	}
		
	private void restore() //Letzte Modinstallation wiederherstellen
	{
		del(new File(mineord+"versions/Modinstaller"));
		try 
		{
			copy(new File(stamm+"Modinstaller/Backup/Modinstaller.jar"), new File(mineord+"versions/Modinstaller.jar"));
			copy(new File(stamm+"Modinstaller/Backup/Modinstaller.json"), new File(mineord+"versions/Modinstaller.json"));
			copy(new File(stamm+"Modinstaller/Backup/mods"), new File(mineord+"mods"));
			JOptionPane.showMessageDialog(null,	Read.getTextwith("seite2", "restore"), Read.getTextwith("seite2", "restoreh"), JOptionPane.INFORMATION_MESSAGE);
		} 
		catch (Exception e) 
		{			
			e.printStackTrace();
		} 
		
		optionWriter("lastmods", optionReader("slastmods"));
		optionWriter("slastmods", optionReader("n/a"));
		optionWriter("lastmc", optionReader("slastmc"));
		optionWriter("slastmc", optionReader("n/a"));
		optionWriter("lastmode", optionReader("slastmode"));
		optionWriter("slastmode", optionReader("n/a"));
			
		restoreButton.setEnabled(false);	
	}
	
	private void setRating(double stars, boolean manual) //Bewertung grafisch umsetzen
	{		
		for (JLabel s : ratIcons)
		{
			if(stars>0.75)
			{
				if(manual)
					s.setIcon(new ImageIcon(this.getClass().getResource("src/star1b.png")));
				else					
					s.setIcon(new ImageIcon(this.getClass().getResource("src/star1.png")));	
			}
			else if(stars>0.25)			
				s.setIcon(new ImageIcon(this.getClass().getResource("src/star05.png")));			
			else		
				s.setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));	
			
			stars--;
		}
	}
	
	private void leftListItemSelected(MouseEvent e)
	{
		JList list = (JList)e.getSource();
		int index = list.locationToIndex(e.getPoint());
		if ((e.getClickCount() == 2) || (e.getButton() == 3))
			selectMod();
		else if (e.getButton() == 1) 
		{	
			if (leftListModel.getSize() > 0 && leftList.isEnabled())
			{
				String Auswahl = (String)leftListModel.getElementAt(index);
				if (!modNameLabel.getText().equals(Auswahl)) 
					setInfoText(Auswahl);
			}
		}
	}
	
	private void rightListItemSelected(MouseEvent e)
	{
		if (rightListModel.getSize() > 0 && Menu.this.rightList.isEnabled()) 
		{			
			JList list = (JList)e.getSource();
			int index = list.locationToIndex(e.getPoint());
			final String Auswahl = (String)rightListModel.getElementAt(index);
			if ((e.getClickCount() == 2) || (e.getButton() == 3))
			{
				removeMod();
			}	
			else if (e.getButton() == 1)
			{		
				if (Auswahl.substring(0, 1).equals("+"))
				{		
					modNameLabel.setText("Loading Mod...");	
					picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));
					new Thread(){
						public void run()
						{
							new Import(Auswahl.substring(2), Menu.this);							
						}
					}.start();					
				}
				else if (!modNameLabel.getText().equals(Auswahl))
				{
					setInfoText(Auswahl);
				}
			}	
		}	
	}
	
	private void enterSearchText(KeyEvent e)
	{
		int si = leftList.getSelectedIndex();
		if(e.getKeyCode()==KeyEvent.VK_DOWN)
		{
			leftList.setSelectedIndex(si+1);
			setInfoText(leftList.getSelectedValue().toString());
		}
		else if (e.getKeyCode()==KeyEvent.VK_UP)
		{
			if(si!=0)
			{
				leftList.setSelectedIndex(si-1);
				setInfoText(leftList.getSelectedValue().toString());	
			}
		}
		else if (e.getKeyCode()==KeyEvent.VK_ENTER || e.getKeyCode()==KeyEvent.VK_RIGHT) //Enter oder rechte Pfeiltaste = Mod auswählen
		{
			searchfocus = true;
			selectMod();
		}	
		else
		{
			leftListModel.removeAllElements();	
			
			String needle = searchInput.getText().toLowerCase().replace(" ", "");	
			for(Modinfo prop : proposals) //Filter 1: Startet mit erstem Buchstaben
			{					
				String modname = prop.getName().toLowerCase().replace(" ", "");			
				if(modname.startsWith(needle)&&!prop.getSelect())
					leftListModel.addElement(prop.getName());			
			}	
			for(Modinfo prop : proposals) //Filter 2: Enthält die Zeichenkette
			{					
				String modname = prop.getName().toLowerCase().replace(" ", "");			
				if(modname.contains(needle)&&!modname.startsWith(needle)&&!prop.getSelect())
					leftListModel.addElement(prop.getName());			
			}
			if(needle.length()>2) //Filter 3: Anfang und Ende des Strings entfernen und suchen
			{
				String needle2 = needle.substring(0, needle.length()-1);
				if(needle2.length()>2)
					needle2 = needle.substring(1, needle2.length());
				for(Modinfo prop : proposals)
				{					
					String modname = prop.getName().toLowerCase().replace(" ", "");			
					if(!modname.contains(needle)&&!modname.startsWith(needle)&&modname.contains(needle2)&&!prop.getSelect())
						leftListModel.addElement(prop.getName());			
				}
			}
		
			if(leftListModel.size()>0)
			{	
				if(!modx.equals(leftListModel.getElementAt(0)))
				{
					modx=leftListModel.getElementAt(0).toString();
					setInfoText(modx);			
				}
				leftList.setSelectedIndex(0);
				leftListMSP.getVerticalScrollBar().setValue(0); //2x notwendig
				leftListFSP.getVerticalScrollBar().setValue(0);	
				leftList.setEnabled(true);
						
			}
			else
			{
				leftListModel.addElement(Read.getTextwith("seite2", "searchn"));
				leftList.setEnabled(false);
			}
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		Object s = e.getSource();
		if(s==selectArrow)	
			selectMod();
		else if (s==removeArrow)	
			removeMod();
		else if(s==importButton)					
			importMod();
		else if(s==restoreButton)
		{
			if(restoreButton.isEnabled())
				 restore();
		}
		else if(s==helpButton)
			new Browser(Read.getTextwith("installer", "website")+"/faq.php");
		else if(s== videoButton)
			new Browser(YT);
		else if(s==modinstWebLnk)
		{
			 if(modinstWebLnk.isEnabled())
				 new Browser(hyperlink);
		}
		else if(s==devWebLnk)
		{
			 if(devWebLnk.isEnabled())
				 new Browser(website);
		}
		else if(s==picture)
		{
			if(importmod)
				importMod();
			else
			 if(online)
				 new Fullscreen(modtexts, modNameLabel.getText(), proposals);
		}
		else if(s==exitButton)
			 System.exit(0);
		else if(s==minButton)
			 setState(ICONIFIED);
		
		for(int i=0; i<ratIcons.length; i++)
		{
			 if(e.getSource()==this.ratIcons[i])
			 {											
				if(rating==i)
				{
					setRating(proz, false);
					manual=false;
					rating=-1;
				}
				else
				{
					setRating(i+1, true);	
					manual=true;
					rating=i;
					try
					{
						String body = "modID=" + modID + "&rating="+(rating+1);
						new Download().post("http://www.minecraft-installer.de/api/modrating.php", body);							
					} 
					catch (Exception er) {
						new Error(getError(er));
					}					
				}					
			 }
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) 	{
		Object s = e.getSource();	
		for(int i=0; i<ratIcons.length; i++)
		{
			 if(s==this.ratIcons[i]&&(!manual))
			 {				
				 setRating(i+1, true);				
			 }	
		}
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		Object s = e.getSource();
		for(int i=0; i<ratIcons.length; i++)
		{
			 if(s==this.ratIcons[i])
			 {
				 if(manual)
					 setRating(rating+1, true);
				 else						 
					 setRating(proz, false);
			 }
		}		
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Object s = e.getSource();
	    if (s == leftList)
	    	leftListItemSelected(e);
	    else if (s == rightList) 
	    	rightListItemSelected(e);
	    else if ((s == nextButton) && (nextButton.isEnabled())) 
	    	startInstallation();
	}
	
	@Override
	public void mouseReleased(MouseEvent arg0) {		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		Object s = e.getSource();
		 if (s == mcVersDrop) 
			 changeVersion();		 
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		 if(tabbedPane.isEnabled() && arg0.getSource() == tabbedPane &&ist)
		 {
			 if(tabbedPane.getSelectedIndex()==0)
				setModloader();
			 else
				setForge();
		 }		
	} 
	
	@Override
	public void keyTyped(KeyEvent e) {				
	}
	
	@Override
	public void keyPressed(KeyEvent e) {			
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		enterSearchText(e);				
	}
}
