package installer;

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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
 * All actions for Modinstaller main window
 * 
 * @version 5.0
 * @author Dirk Lippke
 */

public class Menu extends MenuGUI implements ActionListener, MouseListener, ChangeListener, KeyListener
{
	private static final long serialVersionUID = 1L;
	public static boolean isModloader=true;
	private String mcVersion = Start.mcVersion;	
	
	private String modx="";
	private boolean manual=false;	
	private boolean importmod=false, searchfocus = false, ist=false;
	private double proz=0.0,  rating = 0.0;		
	private int modID=-1;
	private ArrayList<Modinfo> modlArrL = new ArrayList<Modinfo>();
	private ArrayList<Modinfo> forgeArrL = new ArrayList<Modinfo>();
	private ArrayList<Modinfo> proposals = new ArrayList<Modinfo>();
	private ArrayList<String> offlineList;
	private Modinfo[] modtexts, moddownloads;
	private JList<String> leftList;	
	private DefaultListModel<String> leftListModel;	
	private Thread bart = null, picThread =null;

	public Menu(Modinfo[] modtexts, Modinfo[] moddownloads, ArrayList<String> offlineList) 
	{
		this.modtexts=modtexts;
		this.moddownloads=moddownloads;
		this.offlineList = offlineList;
		GUI();	    
	    setVisible(true);			
	    load();	 
	    if (Start.online)
	    {
	    	new Survey();
	    }
	}
	
	/**
	 * Necessary for the fist start of the menu.
	 * All mods of the selected Minecraft version are dedicated to the modloader and forge ArrayList.
	 * Decides the modloader or forge mods are shown first.
	 */
	private void load()
	{
		if(Start.online)
		{			
			SwingUtilities.invokeLater(new Runnable() 
			{					   
				public void run() 
				{ 							
					picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));	
					tabbedPane.setEnabled(false);		
					
					modlArrL.clear();
					forgeArrL.clear();	
					
					del(new File(Start.sport, "Import"));
					
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
	
	/**
	 * Sets the Modinstaller to offline mode, if no Internet connection can be established
	 * @param off True, activates the offline mode
	 */
	private void setOffline(boolean off)
	{
		if(off==true)
		{
			Start.online=false;
			leftListMModel.removeAllElements();
			leftListFModel.removeAllElements();
			rightListModel.removeAllElements();	
			searchInput.setEnabled(false);
			selectArrow.setEnabled(false);
			setImport(true);				
		}
		else
		{
			Start.online=true;	
			searchInput.setEnabled(true);
			selectArrow.setEnabled(true);
		}
	}
	
	/**
	 * Change Modinstaller to Modinstaller mode. The lists of the tab are changed.
	 */
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

	/**
	 * Change Modinstaller to Forge mode. The lists of the tab are changed.
	 */
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
	
	/**
	 * All mods are unselected
	 */
	private void resetSelection()
	{
		for(Modinfo prop: proposals)
			prop.setSelect(false);
	}
	
	/**
	 * Transfers all mods that are already installed from the left to the right list
	 */
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
	
	/**
	 * Updates all lists and adds entries of the mods with imports.
	 * Shows the probability in a bar if the selected are compatible to each other.
	 */
	public void updateLists()
	{
		nextButton.setEnabled(false);
		if(leftListModel!=null)
			leftListModel.removeAllElements();
		rightListModel.removeAllElements();
		searchInput.reset();	
		if(leftList!=null)
		{
			leftList.setEnabled(true);
			leftList.requestFocusInWindow();
		}
		for(Modinfo prop: proposals)
		{
			if(prop.getSelect())
			{
				rightListModel.addElement(prop.getName());
				nextButton.setEnabled(true);
			}
			else
				leftListModel.addElement(prop.getName());
		}			
		
		//Adds all imported mods
		new Thread()
		{
			public void run()
			{		
				File impf=new File(Start.sport, "Import");
				
				String mode ="Forge";
				if(isModloader)
					mode="Modloader";
				
				//Adds all mods that are imported at the last time
				if(optionReader("lastmc").equals(mcVersion) && optionReader("lastmode").equals(mode))
				{	
					File impo = new File(Start.sport, "Importo");
					if(impo.exists())
					{
						try 
						{
							rename(impo, impf);
						} 
						catch (Exception e) {}	
					}
				}
				
				//Adds all new imported mods
				if(impf.exists())
				{			
					File[] imports = impf.listFiles();
			        for (File modi : imports) 
			        {
			            if (!Menu.isModloader)
			            {
			              if (modi.isFile())
			              {
			                String name = modi.getName().substring(0, modi.getName().lastIndexOf("."));
			                Menu.this.rightListModel.addElement("+ " + name);
			                Menu.this.nextButton.setEnabled(true);
			              }
			            }
			            else if (modi.isDirectory())
			            {
			              String name = modi.getName();
			              Menu.this.rightListModel.addElement("+ " + name);
			              Menu.this.nextButton.setEnabled(true);
			            }
			        }
				}
			}
		}.start();
		
		//Show the compatibility of the mods that have been selected
		if(bart!=null)
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
					res = new Postrequest("http://www.minecraft-installer.de/api/compGet.php", "Mods="+zeile).toString();
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
						ratingBar.setVisible(false);
					}
					else
					{
						ratingBar.setVisible(true);
						ratingBar.setValue(val);
					}
				}
				else
					ratingBar.setVisible(false);
			}
		};
		bart.start();
	}
	
	/**
	 * Selects the first mod of the left or right mod list in order to show any mod text
	 */
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
	
	/**
	 * Changes the Minecraft version
	 */
	public void changeVersion()
	{	
		ist=false;
		resetSelection();
		mcVersLabel.setText("Minecraft ["+Start.mcVersion+"]");
		modDescPane.setText(Read.getTextwith("Menu", "wait"));
		leftListMSP.getVerticalScrollBar().setValue(0);
		leftListFSP.getVerticalScrollBar().setValue(0);
		mcVersion = Start.mcVersion;
		load();				
	}	
	
	/**
	 * Sets the texts, links and pictures of an mods that has been selected by user.
	 * @param modname Name of the selected mod
	 */
	private void setInfoText(final String modname)
	{	
		boolean newt = false;
		if(modname.equals(modNameLabel.getText()))
		{
			newt = true;
		}
		else
		{
			picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));	
		}
		
		setImport(false);
		modNameLabel.setText(modname);	
		
		
		for(Modinfo modt : modtexts)
		{
			if(modt.getName().equals(modname))
			{	
				try
			    {								
				 	String inh = modt.getText();
				 	modID= modt.getID();
				 	website =  modt.getSource();
				 	YouTube = modt.getYouTube();
					hyperlink = Read.getTextwith("installer", "website") + "/modinfo.php?modname=" + modname.replace(" ", "+");					
					String suche = searchInput.getText().replaceAll("\\(", "").replaceAll("\\)", ""); //mark seach text
					if(!suche.equals("")&&suche.length()>1)
					{	
						inh = inh.replaceAll("&auml;", "ä");
						inh = inh.replaceAll("&uuml;", "ü");
						inh = inh.replaceAll("&ouml;", "ö");
						inh = inh.replaceAll("&szlig;", "ß");
						inh = inh.replaceAll("&quot;", "\"");
						inh = inh.replaceAll("<m>", "");
						inh = inh.replaceAll("</m>", "");
						inh = inh.replaceAll("\\(", "");
						inh = inh.replaceAll("\\)", "");
						String rep ="";
						Pattern word = Pattern.compile(suche, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
						Matcher match = word.matcher(inh);
						while (match.find()) {
							rep = inh.substring(match.start(), match.end());
						}
						inh = inh.replaceAll("(?i)" + suche, "<m>" + rep+ "</m>");
						inh = inh.replaceAll("<m>", "<font style='background-color:#9C2717; color:white;'>");
						inh = inh.replaceAll("</m>", "</font>");
					}
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
							//Shows if the mod is popular
							proz = modd.getRating();
							if(proz > 6.5)	
							{
								topIcon.setIcon(new ImageIcon(this.getClass().getResource("src/top.png")));
								topIcon.setVisible(true);	
							}
							else
								topIcon.setVisible(false);
							
							//Shows if the mod is new
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
		//Load picture of mod
		if(!newt)
		{
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
					picture.setText(Read.getTextwith("Menu", "nopic"));				
					picture.setIcon(null);
				}	
	    	 }
		    };
		    picThread.start();
		}
	}

	/**
	 * Install mod: Method detects the selected mods in the left list and moves the selected mods to the right list
	 */
	private void selectMod() // Auswählen von Mods
	{	
		List<String> strlist = new ArrayList<String>();
		strlist = leftList.getSelectedValuesList();
		for (String listitem : strlist)
			for(Modinfo prop : proposals)
				if(prop.getName().equals(listitem))
					prop.setSelect(true);		
		updateLists();
		
		if(searchfocus)
			searchInput.requestFocus();
		searchfocus=false;
	}
	
	/**
	 * Uninstall mod: Method detects the selected mods in the right list and moves the selected mods to the left list
	 */
	private void removeMod() // Entfernen von Mods
	{		
		List<String> strlist = new ArrayList<String>();
		strlist = rightList.getSelectedValuesList();
		for (String listitem : strlist)	
		{
			String name = String.valueOf(listitem);
			if (name.substring(0, 1).equals("+")) // Importierter Mod löschen
			{
				name = name.substring(2);					
				File importf = new File(Start.sport, "Import");
				File importfn = new File(Start.sport, "Importn");
				del(new File(importf, name+".jar"));
				del(new File(importf, name));					
				del(new File(importfn, name));					
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
	}

	/**
	 * Packs all selected mods in the right list into an ArrayList and starts the Install window.
	 */
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

	/**
	 * If the mod import button is pressed ask user for the folder or mod file.
	 */
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
        	{
        		new Import(modfile, this);
        	}
        }
	}
	
	/**
	 * If a imported mod is shown in the menu some special modifications have to be done.
	 * @param yes true, if a imported mod has to be shown
	 */
	public void setImport(boolean yes)
	{
		if(yes)
		{			
	        picture.setIcon(new ImageIcon(this.getClass().getResource("src/importbig.png")));
	         
	        if(isModloader)
	        {
	        	modNameLabel.setText(Read.getTextwith("Menu", "importmh"));
	        	modDescPane.setText(Read.getTextwith("Menu", "importm"));
	        }
	        else
	        {
	        	modNameLabel.setText(Read.getTextwith("Menu", "importfh"));
	        	modDescPane.setText(Read.getTextwith("Menu", "importf"));
	        }	      
		}
		modDescPane.setCaretPosition(0);
		importmod =yes;
		modinstWebLnk.setVisible(!yes);
		modVersionL.setVisible(yes);
		topIcon.setVisible(!yes);
		videoButton.setVisible(!yes);
		for (JLabel ic : ratIcons)
        	ic.setVisible(!yes);
	}
		
	/**
	 * If user presses the restore button the last Minecraft installation is restored
	 */
	private void restore()
	{
		del(new File(Start.mineord, "versions/Modinstaller"));
		try 
		{
			File modsport = new File(Start.mineord, "versions/Modinstaller");
			OP.copy(new File(Start.sport, "Backup/Modinstaller.jar"), new File(modsport, "Modinstaller.jar"));
			OP.copy(new File(Start.sport, "Backup/Modinstaller.json"), new File(modsport, "Modinstaller.json"));
			OP.copy(new File(Start.sport, "Backup/mods"), new File(Start.mineord, "mods"));
			JOptionPane.showMessageDialog(null,	Read.getTextwith("Menu", "restore"), Read.getTextwith("Menu", "restoreh"), JOptionPane.INFORMATION_MESSAGE);
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
	
	/**
	 * Graphical implementation in visual star symbols of an Double rating.
	 * @param stars Number of stars that has to be displayed
	 * @param manual True, if a cursor of the user sets the number of stars
	 */
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
	
	/**
	 * Detects the source of a MouseEvent in the left mod list.
	 * Counts the number of clicks and shows the mod description.
	 * If the user has clicked twice the mods will be selected.
	 * @param e MouseEvent of the left list
	 */
	private void leftListItemSelected(MouseEvent e)
	{
		JList<?> list = (JList<?>) e.getSource();
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
	
	/**
	 * Detects the source of a MouseEvent in the left mod list.
	 * Counts the number of clicks and shows the mod or import description.
	 * If the user has clicked twice the mods will be removed.
	 * @param e MouseEvent of the right list
	 */
	private void rightListItemSelected(MouseEvent e)
	{		
		if (rightListModel.getSize() > 0 && Menu.this.rightList.isEnabled()) 
		{			
			JList<?> list = (JList<?>) e.getSource();
			int index = list.locationToIndex(e.getPoint());
			final String Auswahl = (String)rightListModel.getElementAt(index);
			if ((e.getClickCount() == 2) || (e.getButton() == 3))
			{
				removeMod();
			}	
			else
			{	
				//If the user clicked on a mod with a "+" the imported mod description is shown
				if (Auswahl.substring(0, 1).equals("+"))
				{		
					modNameLabel.setText("Loading Mod...");	
					picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));
					new Thread()
					{
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
	
	/**
	 * Update the left mod list according to the entered text the user typed in
	 * @param e
	 */
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
			
			for(Modinfo prop : proposals) //Filter 1: Starts with the first letter
			{					
				String modname = prop.getName().toLowerCase().replace(" ", "");
				if(modname.startsWith(needle)&&!prop.getSelect())
					leftListModel.addElement(prop.getName());			
			}	
			for(Modinfo prop : proposals) //Filter 2: Contains the whole String
			{					
				String modname = prop.getName().toLowerCase().replace(" ", "");	
				if(modname.contains(needle)&&!modname.startsWith(needle)&&!prop.getSelect())
					leftListModel.addElement(prop.getName());			
			}
			for(Modinfo prop : proposals) //Filter 3: Contains the whole String
			{	
				f1: for(Modinfo info : modtexts)
				{	
					if(info.getName().equals(prop.getName()))
					{
						String modtext = info.getText().toLowerCase().replace(" ", "");
						modtext =modtext.replace("&auml;", "ä");
						modtext =modtext.replace("&uuml;", "ü");
						modtext =modtext.replace("&ouml;", "ö");
						modtext =modtext.replace("&szlig;", "ß");
						modtext =modtext.replace("&quot;", "\"");
						if(modtext.contains(needle)&&!leftListModel.contains(prop.getName()))
						{
							leftListModel.addElement(prop.getName());
							break f1;
						}
					}
				}	
			}	
			/*
			if(needle.length()>2) //Filter 4: Remove the first and the last letter of the String
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
			*/
			if(leftListModel.size()>0)
			{		
				modx=leftListModel.getElementAt(0).toString();					
				setInfoText(modx);
				leftList.setSelectedIndex(0);
				leftListMSP.getVerticalScrollBar().setValue(0); //2x notwendig
				leftListFSP.getVerticalScrollBar().setValue(0);	
				leftList.setEnabled(true);						
			}
			else
			{
				leftListModel.addElement(Read.getTextwith("Menu", "searchn"));
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
			OperatingSystem.openLink(Read.getTextwith("installer", "website")+"/faq.php");
		else if(s== videoButton)
			OperatingSystem.openLink(YouTube);
		else if(s==modinstWebLnk)
		{
			 if(modinstWebLnk.isEnabled())
				 OperatingSystem.openLink(hyperlink);
		}
		else if(s==devWebLnk)
		{
			 if(devWebLnk.isEnabled())
				 OperatingSystem.openLink(website);
		}
		else if(s==picture)
		{
			if(importmod)
				importMod();
			else
			 if(Start.online)
				 new Fullscreen(modtexts, modNameLabel.getText(), proposals);
		}
		else if(s==exitButton)
			 System.exit(0);
		else if(s==minButton)
			 setState(ICONIFIED);	
		else if(s==mcVersLabel)
		{			
			new MCVersions(modtexts, moddownloads, offlineList, this).setVisible(true);
		}		
		else if ((s == nextButton) && (nextButton.isEnabled())) 
			startInstallation();		
		
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
						new Postrequest("http://www.minecraft-installer.de/api/modrating.php", body);							
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
