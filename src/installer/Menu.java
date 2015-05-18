package installer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

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

import static installer.OP.*;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Menu extends MenuGUI implements ActionListener, MouseListener, ChangeListener
{
	private static final long serialVersionUID = 1L;	
	private String mineord = Start.mineord, stamm = Start.stamm, mcVersion = Start.mcVersion, hyperlink = Read.getTextwith("seite2", "web"), linkquelle=Read.getTextwith("seite2", "web");	
	private boolean online = Start.online;
	private double proz=0,  rating = 0.;	
	private boolean manual=false;
	private ArrayList<String> Modloaderl = new ArrayList<String>();
	private ArrayList<String> Forgel = new ArrayList<String>();
	private Modinfo[] modtexts, moddownloads;
	private JList<String> leftList;	
	private DefaultListModel<String> leftListModel;
	
	public static boolean Modloader=true;

	public Menu(Modinfo[] modtexts, Modinfo[] moddownloads) 
	{
		this.modtexts=modtexts;
		this.moddownloads=moddownloads;
		GUI();	    
	    setVisible(true);			
	    load();	 		
	}
	
	public void load()
	{
		if(online)
		{			
			SwingUtilities.invokeLater(new Runnable() 
			{					   
				public void run() 
				{ 							
					picture.setIcon(new ImageIcon(this.getClass().getResource("src/warten.gif")));	
					tabbedPane.setEnabled(false);		
					Modloaderl.clear();
					Forgel.clear();	
							
					try
					{
						for (int k = 0; k < moddownloads.length; k++)
						{
							if ((moddownloads[k].getMC().equals(mcVersion)) && (moddownloads[k].getCat() == 0))
							{
								Modloaderl.add(moddownloads[k].getName());
							}
							else if ((moddownloads[k].getMC().equals(mcVersion)) && (moddownloads[k].getCat() == 3))
							{
								Forgel.add(moddownloads[k].getName());
							}
						}
						del(new File(stamm + "Modinstaller/Import"));
						del(new File(stamm + "Modinstaller/zusatz.txt"));
					}
					catch (Exception localException) {}
								    
					if (Forgel.size() > 0) 					
						tabbedPane.setEnabled(true);					
					if (Forgel.size() >= Modloaderl.size()) 					
						ForgeMode();					
					else 					
						ModloaderMode();				
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
			leftListMModel.removeAllElements();
			leftListFModel.removeAllElements();
			rightListModel.removeAllElements();		
			setImport();	
		}
		else
		{
			online=true;	
		}
	}
	
	public void loadText()
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
	
	public void ModloaderMode()
	{			
		leftListModel = leftListMModel;
	    leftList = leftListM;
	    Modloader = true;
	    leftListModel.removeAllElements();
	    rightListModel.removeAllElements();
	    for (String val : Modloaderl)
	     leftListModel.addElement(val);	    
	    sortOut(leftListModel);
	    tabbedPane.setSelectedIndex(0);
	    loadText();
	}

	public void ForgeMode() 
	{		
		leftListModel = leftListFModel;
	    leftList = leftListF;
	    Modloader = false;
	    leftListModel.removeAllElements();
	    rightListModel.removeAllElements();
	    for (String val : Forgel) 
	     leftListModel.addElement(val);
	    sortOut(leftListModel);
	    tabbedPane.setSelectedIndex(1);
	    loadText();
	}
	
	public void sortOut(DefaultListModel<String> List)
	{
		String Mode = "Modloader";
		if (!Modloader) 
			Mode = "Forge";
		
		if ((optionReader("lastmc").equals(this.mcVersion)) && (optionReader("lastmode").equals(Mode)))
		{
			String alastm = optionReader("lastmods");
			String[] lastm = alastm.split(";;");
			int lange = List.getSize();
			for (int r = 0; r < lastm.length; r++) 
			{
				for (int k = 0; k < lange; k++)
				{
					if (lastm[r].equals(((String)List.getElementAt(k)).toString()))
					{
						rightListModel.addElement(((String)List.getElementAt(k)).toString());
						List.remove(k);
						lange--;
					}
				}
			}
		}
		leftListMSP.getVerticalScrollBar().setValue(0);
		leftListFSP.getVerticalScrollBar().setValue(0);
	}
		
	public void changeVersion() //Version ändern
	{	
		pane.setText(Read.getTextwith("seite2", "wait"));
		Start.mcVersion = Start.mcVersionen[ChVers.getSelectedIndex()];
		mcVersion = Start.mcVersionen[ChVers.getSelectedIndex()];
		load();				
	}	
	
	private void setInfoText(final String modname) //Modbeschreibung anzeigen
	{			
		modtext.setText(modname);	
		picture.setIcon(new ImageIcon(this.getClass().getResource("src/warten.gif")));	
		
		for(int i=0; i<modtexts.length; i++)
		{
			if(modtexts[i].getName().equals(modname))
			{	
				try
			    {								
				 	String inh = modtexts[i].getText();			
				 	linkquelle =  modtexts[i].getSource();
					hyperlink = Read.getTextwith("seite2", "web") + "/modinfo.php?modname=" + modname.replace(" ", "+");
					if(!inh.startsWith("<html>"))
					{
						inh="<html><body>"+inh+"</body></html>";
					}				
					pane.setText(inh);							
					pane.setCaretPosition(0);
					manual=false;
					
					for(int j=0; j<moddownloads.length; j++)	
					{
						if(moddownloads[j].getName().equals(modname)&&moddownloads[j].getMC().equals(mcVersion))
						{
							proz = moddownloads[j].getRating();
							if(proz > 6.5)
							{									
								modtext.setText(modname +" - TOP!");									
							}		
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
	
	public void selectMod() // Auswählen von Mods
	{	
		String modname = (String)leftList.getSelectedValue();
			
		if (searchEntry(leftListModel, modname)) 
		{
			rightListModel.add(rightListModel.getSize(),modname);
			leftListModel.removeElement(modname);
		}			
		nextButton.setEnabled(true);								
	}
	
	public boolean searchEntry(DefaultListModel<String> model, String modname) //In ListModel einen Modeintrag finden
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
	
	public void removeMod() // Entfernen von Mods
	{
		nextButton.setEnabled(true);
		for (int i = 0; i < rightListModel.getSize(); i++) 
		{
			if (rightList.getSelectedIndex() == i) // Ausgewählte Stelle suchen
			{
				if (((String) rightListModel.getElementAt(rightList.getSelectedIndex())).substring(0, 1).equals("+")) // Importierter Mod löschen
				{
					String name = rightListModel.getElementAt(rightList.getSelectedIndex()).substring(2);	
					del(new File(stamm+"Modinstaller/Import/"+name+".jar"));
				}  
				else  // sonst nach Liste links kopieren
				{
					boolean ent = false;
					for (int j = 0; j < leftListMModel.getSize(); j++) 
					{
						if (rightListModel.getElementAt(rightList.getSelectedIndex()).toString().equals(leftListModel.getElementAt(j).toString())) // Überprüfen ob Mod in Downloadliste vorhanden
						{
							ent = true;
						}
					}
					if (ent == false) //Mod in linker Liste vorhanden --> Hinzufügen in linke Liste
					{
						leftList.setEnabled(true);						
						leftListModel.addElement(rightListModel.getElementAt(rightList.getSelectedIndex()));
												
						String[] list = new String[leftListModel.getSize()];
						for(int i3 = 0; i3 < leftListModel.getSize(); i3++) 
						{						  
						    list[i3] = ((String) leftListModel.elementAt(i3));						  
						}
						Arrays.sort(list);	
						
						leftListModel.removeAllElements();
						for (int h=0; h<list.length; h++)
						{
							leftListModel.addElement(list[h]);
						}
					}
				}
				rightListModel.remove(rightList.getSelectedIndex()); // Mod vom Liste2 l�schen
				if (rightListModel.getSize() == 0) 
				{
					nextButton.setEnabled(false); // Wenn keine Mods in Liste2 vorhanden Installieren deaktivieren
				}
			}
		}
	}

	public void startInstallation() // Installieren Knopf
	{		
		try 
		{		
			ArrayList<String> chnamen = new ArrayList<String>();
			for(int i=0; i<rightListModel.getSize(); i++)
			{
				String eintrag = rightListModel.getElementAt(i);
				if(eintrag.charAt(0)!='+')
				{
					chnamen.add(eintrag);	
				}
			}				
			String[] modnames = chnamen.toArray(new String[chnamen.size()]); 
			
			dispose();
			new Install(modnames, Modloader);
		} 
		catch (Exception ex) 
		{	
			new Error(getError(ex) + "\n\nErrorcode: S2x09");	
		}		
	}

	public void importMod() //Mods importieren
	{	
		setImport();
		JFileChooser chooser = new JFileChooser();      
        int rueckgabeWert = chooser.showOpenDialog(null);      
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
        {
        	new Import(chooser.getSelectedFile());	            
        }		
	}
	
	public void setImport()
	{
		
        picture.setIcon(new ImageIcon(this.getClass().getResource("src/importbig.png")));
         
        if(Modloader)
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
       
        hyperlink = Read.getTextwith("seite2", "web")+"/faq.php";
        setRating(0.0D, true);
	}
		
	public void restore() //Letzte Modinstallation wiederherstellen
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
	
	public void mouseClicked(MouseEvent e) 
	{
		Object s = e.getSource();
		if(s==selectArrow)		 
			 selectMod();		 
		else if(s==removeArrow)
			 removeMod();		 
		else if(s==importButton)					
			importMod();
		else if(s==restoreButton)
		{
			if(restoreButton.isEnabled())
				 restore();
		}
		else if(s==helpButton)
			 new Browser(Read.getTextwith("seite2", "web")+"/faq.php");
		else if(s==linkButton)
		{
			 if(linkButton.isEnabled())
			 new Browser(hyperlink);
		}
		else if(s==sourceButton)
		{
			 if(sourceButton.isEnabled())
				 new Browser(linkquelle);
		}
		else if(s==picture)
		{
			 if(online)
				 new Fullscreen(leftList, leftListModel);
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

	public void mouseEntered(MouseEvent e) 
	{
		Object s = e.getSource();	
		for(int i=0; i<ratIcons.length; i++)
		{
			 if(s==this.ratIcons[i]&&(!manual))
			 {				
				 setRating(i+1, true);				
			 }	
		}
	}

	public void mouseExited(MouseEvent e) 
	{
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
	
	public void mousePressed(MouseEvent e)
	{
		Object s = e.getSource();
	    if (s == leftList)
	    {
	      if ((e.getClickCount() == 2) || (e.getButton() == 3))
	        selectMod();
	      else if (e.getButton() == 1) 
	      {	    	  
	        SwingUtilities.invokeLater(new Runnable()
	        {
	          public void run()
	          {
	            if ((leftListModel.getSize() > 0) && (leftList.isEnabled()))
	            {
	              String Auswahl = (String)leftListModel.getElementAt(leftList.getSelectedIndex());
	              if (!modtext.getText().equals(Auswahl)) 
	                setInfoText(Auswahl);
	            }
	          }
	        });
	      }
	    }
	    else if (s == rightList) 
	    {
	      try
	      {
	        if (rightListModel.getSize() <= 0) 
	          return;
	       
	        String Auswahl = (String)rightListModel.getElementAt(this.rightList.getSelectedIndex());
	        if (((String)rightListModel.getElementAt(this.rightList.getSelectedIndex())).substring(0, 1).equals("+"))
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
	          SwingUtilities.invokeLater(new Runnable()
	          {
	            public void run()
	            {
	              if ((Menu.rightListModel.getSize() > 0) && (Menu.this.rightList.isEnabled()))
	              {
		              String Auswahl = (String)Menu.rightListModel.getElementAt(rightList.getSelectedIndex());
		              if (!modtext.getText().equals(Auswahl))
		            	  setInfoText(Auswahl);	                
	              }
	            }
	          });
	        }
	      }
	      catch (Exception ex) {}
	    } 
	    else if ((s == nextButton) && (nextButton.isEnabled())) 
	    {
	    	startInstallation();
	    }
  }

	public void mouseReleased(MouseEvent arg0) {		
	}

	public void actionPerformed(ActionEvent e) 
	{
		Object s = e.getSource();
		 if (s == ChVers) 
			 changeVersion();		 
	}

	public void stateChanged(ChangeEvent arg0) {
		 if(tabbedPane.isEnabled())
		 {
			 if(tabbedPane.getSelectedIndex()==0)
				ModloaderMode();
			 else
				ForgeMode();
		 }		
	} 
}
