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
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Menu  extends MenuGUI implements ActionListener, MouseListener, ChangeListener
{
	private static final long serialVersionUID = 1L;
	
	private String mineord = Start.mineord, stamm = Start.stamm, Version = Start.Version, hyperlink = Read.getTextwith("seite2", "web"), linkquelle=Read.getTextwith("seite2", "web");	
	public static int zahl;
	private Modinfo[] Mod, Downloadlist;
	private double proz=0,  bewertung = 0.;
	static boolean Modloader=true;
	private static boolean online = Start.online;
	private boolean anders=false;	

	private ArrayList<String> Modloaderl = new ArrayList<String>();
	private ArrayList<String> Forgel = new ArrayList<String>();

	public Menu(Modinfo[] Mod, Modinfo[] Downloadlist) 
	{
		this.Mod=Mod;
		this.Downloadlist=Downloadlist;
		
		GUI();
	    
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
	    if (!Modloader) 
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
	      new OP().del(new File(this.stamm + "Modinstaller/Import"));
	      new OP().del(new File(this.stamm + "Modinstaller/zusatz.txt"));
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
					File zus = new File(stamm +"Modinstaller/zusatz.txt");
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
							new OP().del(new File(stamm +"Modinstaller/zusatz.txt"));
							new OP().del(new File(stamm +"Modinstaller/Import"));
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
		JFileChooser chooser = new JFileChooser();      
        int rueckgabeWert = chooser.showOpenDialog(null);      
        if(rueckgabeWert == JFileChooser.APPROVE_OPTION)
        {
        	new Import(chooser.getSelectedFile());	            
        }		
	}
	
	public void Restore() //Letzte Modinstallation wiederherstellen
	{
		new OP().del(new File(mineord+"versions/Modinstaller"));
		try 
		{
			new OP().copy(new File(stamm+"Modinstaller/Backup/Modinstaller.jar"), new File(mineord+"versions/Modinstaller.jar"));
			new OP().copy(new File(stamm+"Modinstaller/Backup/Modinstaller.json"), new File(mineord+"versions/Modinstaller.json"));
			new OP().copy(new File(stamm+"Modinstaller/Backup/mods"), new File(mineord+"mods"));
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
	            new Import(Auswahl.substring(2));
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
