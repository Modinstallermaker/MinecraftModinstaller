package installer;

import static installer.OP.copy;
import static installer.OP.del;
import static installer.OP.getError;
import static installer.OP.optionReader;
import static installer.OP.optionWriter;

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
import java.util.ArrayList;

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
	private String mineord = Start.mineord, stamm = Start.stamm, mcVersion = Start.mcVersion, hyperlink = Read.getTextwith("installer", "website"), 
			website=Read.getTextwith("installer", "website");	
	private boolean online = Start.online;
	private double proz=0,  rating = 0.;	
	private boolean manual=false;
	private ArrayList<Modinfo> modlArrL = new ArrayList<Modinfo>();
	private ArrayList<Modinfo> forgeArrL = new ArrayList<Modinfo>();
	private ArrayList<Modinfo> proposals = new ArrayList<Modinfo>();
	private Modinfo[] modtexts, moddownloads;
	private JList<String> leftList;	
	private DefaultListModel<String> leftListModel;
	
	private boolean importmod=false, sear = false;
	private String modx="";
	
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
					catch (Exception localException) {}
								    
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
			search.setEnabled(false);
			selectArrow.setEnabled(false);
			setImport();				
		}
		else
		{
			online=true;	
			search.setEnabled(true);
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
				String[] lastm = alastm.split(";;");
				for (int r = 0; r < lastm.length; r++) 
					for (Modinfo prop: proposals)
						if (Integer.parseInt(lastm[r])==prop.getID())
							prop.setSelect(true);
			}
			catch (Exception e){				
			}
		}
		updateLists();		
		loadTexts();
	}
	
	public void updateLists()
	{
		leftListModel.removeAllElements();
		rightListModel.removeAllElements();
		search.reset();		
		leftList.setEnabled(true);
		leftList.requestFocusInWindow();
		for(Modinfo prop: proposals){
			if(prop.getSelect())
				rightListModel.addElement(prop.getName());
			else
				leftListModel.addElement(prop.getName());
		}
		
		File impo=new File(stamm+"Modinstaller/Import");
		if(impo.exists())
		{
			File[] imports = impo.listFiles();
			for(File modi : imports)
			{				
				String name =modi.getName().substring(0, modi.getName().lastIndexOf("."));
				rightListModel.addElement("+ "+name);
			}
		}
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
		resetSelection();
		pane.setText(Read.getTextwith("seite2", "wait"));
		Start.mcVersion = Start.mcVersionen[ChVers.getSelectedIndex()];
		mcVersion = Start.mcVersionen[ChVers.getSelectedIndex()];
		load();				
	}	
	
	private void setInfoText(final String modname) //Modbeschreibung anzeigen
	{	
		importmod=false;
		modtext.setText(modname);	
		picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));	
		
		for(Modinfo modt : modtexts)
		{
			if(modt.getName().equals(modname))
			{	
				try
			    {								
				 	String inh = modt.getText();			
				 	website =  modt.getSource();
					hyperlink = Read.getTextwith("installer", "website") + "/modinfo.php?modname=" + modname.replace(" ", "+");
					if(!inh.startsWith("<html>"))
					{
						inh="<html><body>"+inh+"</body></html>";
					}				
					pane.setText(inh);							
					pane.setCaretPosition(0);
					manual=false;
					
					for(Modinfo modd : moddownloads)	
					{
						if(modd.getName().equals(modname)&&modd.getMC().equals(mcVersion))
						{
							proz = modd.getRating();
							if(proz > 6.5)	
							{
								specImg.setIcon(new ImageIcon(this.getClass().getResource("src/top.png")));
								specImg.setVisible(true);	
							}
							else
								specImg.setVisible(false);
							if(proz==100)
							{
								specImg.setIcon(new ImageIcon(this.getClass().getResource("src/new.png")));
								specImg.setVisible(true);
							}
							
							double size = modd.getSize();
							String unit = "Byte";
							if(size>1024)
							{
								size/=1024.;
								unit="KB";
							}
							if(size>1024)
							{
								size/=1024.;
								unit="MB";
							}
							if(size>1024)
							{
								size/=1024.;
								unit="GB";
							}
							size = Math.round(size*10.)/10.;
							sizeLabel.setText(String.valueOf(size)+" "+unit);							
							setRating(proz, false);	
							break;
						}
					}					
			    }
			    catch(Exception e) {}	
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
	    }.start();
	}
	
	private void selectMod() // Auswählen von Mods
	{	
		String listitem = (String)leftList.getSelectedValue();
		for(Modinfo prop : proposals)
			if(prop.getName().equals(listitem))
				prop.setSelect(true);		
		updateLists();		
		nextButton.setEnabled(true);	
		if(sear)
			search.requestFocus();
		sear=false;
	}
		
	private void removeMod() // Entfernen von Mods
	{
		if(rightList.isFocusOwner())
		{
			String listitem = (String)rightList.getSelectedValue();			
			if (listitem.substring(0, 1).equals("+")) // Importierter Mod löschen
			{
				String name = listitem.substring(2);
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
		setImport();
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
	
	private void setImport()
	{
		importmod =true;
        picture.setIcon(new ImageIcon(this.getClass().getResource("src/importbig.png")));
         
        if(isModloader)
        {
        	modtext.setText(Read.getTextwith("seite2", "importmh"));
        	pane.setText(Read.getTextwith("seite2", "importm"));
        }
        else
        {
        	modtext.setText(Read.getTextwith("seite2", "importfh"));
        	pane.setText(Read.getTextwith("seite2", "importf"));
        }
        pane.setCaretPosition(0);	         
       
        hyperlink = Read.getTextwith("installer", "website")+"/faq.php";
        setRating(0.0D, true);
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
	
	private void setRating(double bewe, boolean anders) //Bewertung grafisch umsetzen
	{			
		for (JLabel s : ratIcons)
		{
			if(bewe>0.75)
			{
				if(!anders)
					s.setIcon(new ImageIcon(this.getClass().getResource("src/star1.png")));	
				else
					s.setIcon(new ImageIcon(this.getClass().getResource("src/star1b.png")));	
			}
			else if(bewe>0.25)			
				s.setIcon(new ImageIcon(this.getClass().getResource("src/star05.png")));			
			else		
				s.setIcon(new ImageIcon(this.getClass().getResource("src/star0.png")));	
			
			bewe--;
		}
	}
	
	private void leftListItemSelected(MouseEvent e)
	{
		if ((e.getClickCount() == 2) || (e.getButton() == 3))
			selectMod();
		else if (e.getButton() == 1) 
		{	
			if (leftListModel.getSize() > 0 && leftList.isEnabled())
			{
				String Auswahl = (String)leftListModel.getElementAt(leftList.getSelectedIndex());
				if (!modtext.getText().equals(Auswahl)) 
					setInfoText(Auswahl);
			}
		}
	}
	
	private void rightListItemSelected(MouseEvent e)
	{
		if (rightListModel.getSize() > 0 && Menu.this.rightList.isEnabled()) 
		{			   
			String Auswahl = (String)rightListModel.getElementAt(this.rightList.getSelectedIndex());
			if (Auswahl.substring(0, 1).equals("+"))
			{	
				setImport();
				if (e.getClickCount() == 2)
					new Import(Auswahl.substring(2));
			}
			else if ((e.getClickCount() == 2) || (e.getButton() == 3))
			{
				removeMod();
			}
			else if (e.getButton() == 1)
			{					
				if (!modtext.getText().equals(Auswahl))
					setInfoText(Auswahl);
			}
		}	
	}
	
	private void enterSearchText(KeyEvent e)
	{
		leftListModel.removeAllElements();	
		
		String needle = search.getText().toLowerCase().replace(" ", "");	
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
				modx=leftListModel.getElementAt(0);
				setInfoText(modx);			
			}
			leftList.setSelectedIndex(0);
			leftListMSP.getVerticalScrollBar().setValue(0); //2x notwendig
			leftListFSP.getVerticalScrollBar().setValue(0);	
			leftList.setEnabled(true);
			if(e.getKeyCode()==KeyEvent.VK_ENTER) //Enter
			{
				sear = true;
				selectMod();
			}
		}
		else
		{
			leftListModel.addElement(Read.getTextwith("seite2", "searchn"));
			leftList.setEnabled(false);
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
		else if(s==linkButton)
		{
			 if(linkButton.isEnabled())
			 new Browser(hyperlink);
		}
		else if(s==sourceButton)
		{
			 if(sourceButton.isEnabled())
				 new Browser(website);
		}
		else if(s==picture)
		{
			if(importmod)
				importMod();
			else
			 if(online)
				 new Fullscreen(modtexts, modtext.getText(), proposals);
		}
		else if(s==exitButton)
			 System.exit(0);
		else if(s==minimizeButton)
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
						String body = "Mod=" + leftList.getSelectedValue().toString() + "&Version=" + mcVersion +"&Rating=" +  String.valueOf((i+1));
						new Download().post("http://www.minecraft-installer.de/ratemod.php", body);							
					} 
					catch (IOException e1) {}					
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
		 if (s == ChVers) 
			 changeVersion();		 
	}
	
	@Override
	public void stateChanged(ChangeEvent arg0) {
		 if(tabbedPane.isEnabled())
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
