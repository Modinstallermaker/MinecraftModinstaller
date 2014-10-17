package installer;

import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import argo.format.PrettyJsonFormatter;
import argo.jdom.JdomParser;
import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Installieren extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JButton back = new JButton();
	private JButton exit = new JButton();
	private JButton start = new JButton();
	private JLabel banner = new JLabel();
	private JLabel uberschrift = new JLabel();
	private JLabel info = new JLabel();
	private JLabel iconf = new JLabel();
	private JProgressBar bar = new JProgressBar();
	private JLabel stat = new JLabel();	
	private double value = 0.00;
	private String quelle;
	private JPanel cp;
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);	
	private Thread t1, t2, t3;
	private Download dow, dowf;
	private int breite = 600, hoehe=350;
	
	private String webplace = Start.webplace, mineord = Start.mineord, stamm = Start.stamm, Version = Start.Version;	
	private boolean online = Start.online;
	
	public static String Fehler="";
	
	public Installieren(final String[] namen, final String[] downloadlist, final int[] anzahl, final boolean Modloader) 
	{
		
		setUndecorated(true);			
		setSize(breite, hoehe);
		cp = new GraphicsPanel(false, "src/bild.png");
		cp.setBorder(BorderFactory.createLineBorder(Color.decode("#9C2717")));

		setTitle(Read.getTextwith("installer", "name"));		
		setLocationRelativeTo(null);	
		setResizable(false);
		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());
		
		cp.setLayout(null);
		add(cp);
		
		uberschrift.setBounds(0, 20, (int)(breite), (int)(hoehe*0.1));                              //Überschrift
		uberschrift.setText(Read.getTextwith("installer", "name"));
		uberschrift.setHorizontalAlignment(SwingConstants.CENTER);
		uberschrift.setVerticalAlignment(SwingConstants.CENTER);
		uberschrift.setFont(Start.lcd.deriveFont(Font.PLAIN,40));
		cp.add(uberschrift);
		
		banner.setBackground(null);
		banner.setForeground(null);
		banner.setIcon(new ImageIcon(this.getClass().getResource("src/banner_gross.png")));
		banner.setBounds(0, (int)(hoehe*0.12), (int)(breite), (int)(hoehe*0.3));
		banner.setCursor(c);
		banner.setHorizontalAlignment(SwingConstants.CENTER);
		banner.addMouseListener(new MouseListener() { // Internetlink
			public void mouseClicked(MouseEvent e) 
			{
				new Browser("http://server.nitrado.net/deu/gameserver-mieten?pk_campaign=MinecraftInstaller");
			}

			public void mouseExited(MouseEvent e) {			
			}

			public void mouseEntered(MouseEvent e) {			
			}

			public void mouseReleased(MouseEvent e) {				
			}

			public void mousePressed(MouseEvent e) {				
			}
		});
		cp.add(banner);
		
		iconf.setBackground(null);
		iconf.setForeground(null);
		iconf.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
		iconf.setBounds(15, 150, 80, 80);
		cp.add(iconf);		

		bar.setBounds(100, 180, 425, 33);
		cp.add(bar);
		
		stat.setBounds(105, 160, 425, 17);
		cp.add(stat);
		
		info.setBounds(105, 210, (int)(breite)-100, (int)(hoehe*0.2));                             //Info
		cp.add(info);
		
		back.setBounds(20, 300, 110, 35);  //Zurück
		back.setBackground(null);
		back.setText(Read.getTextwith("seite3", "text1"));
		back.setMargin(new Insets(2, 2, 2, 2));
		back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				back_ActionPerformed(evt);
			}
		});
		back.setCursor(c);
		cp.add(back);
		
		exit.setBounds((int)(breite)-130, 300, 110, 35); //Programm beenden
		exit.setBackground(null);
		exit.setText(Read.getTextwith("seite3", "text2"));
		exit.setMargin(new Insets(2, 2, 2, 2));
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});		
		exit.setCursor(c);
		cp.add(exit);
		
		start.setBounds((int)(breite)-130-130, 300, 120, 35); //Minecraft starten
		start.setBackground(null);
		start.setText(Read.getTextwith("seite3", "text3"));
		start.setMargin(new Insets(2, 2, 2, 2));
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				new startLauncher(webplace, mineord, online, stamm);	
			}
		});
		start.setCursor(c);
		start.setEnabled(false);
		cp.add(start);
		
		setVisible(true);		

		t1 = new Thread() {			
			private JsonRootNode versionData;
			@Override
			public void run() 
			{
				try 
				{
					status(value += 1); //1
					
					stat.setText(Read.getTextwith("seite3", "prog1") + " (Backup)");	//Löschen		
					new OP().del(new File(stamm + "/Modinstaller/Backup"));
					stat.setText(Read.getTextwith("seite3", "prog1") + " (Result)");
					File Reso = new File(stamm + "/Modinstaller/Result");
					if (Reso.exists()) new OP().del(Reso);		//Fehler???
					new OP().del(new File(mineord +"/mods"));
					new OP().del(new File(mineord +"/coremods"));
					new OP().del(new File(mineord +"/config"));

					status(value += 2); //3
					
					stat.setText(Read.getTextwith("seite3", "prog2"));            //Anlegen
					new OP().makedirs(new File(stamm + "/Modinstaller/Result"));
					new OP().makedirs(new File(stamm + "/Modinstaller/Backup"));					
					new OP().makedirs(new File(stamm + "/Modinstaller/Original"));
					
					status(value += 1); //4
					
					stat.setText(Read.getTextwith("seite3", "prog3"));                              //Wiederherstellungspunkt
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/restore2.png")));
					File vmod = new File(mineord + "/versions/Modinstaller");
					new OP().copy(vmod, new File(stamm + "/Modinstaller/Backup"));
					new OP().del(new File(mineord + "/versions/Modinstaller"));
					new OP().copy(new File(mineord + "/versions/"+Version), new File(mineord + "/versions/Modinstaller")); //von Versions Ordner in Modinstaller Ordner kopieren
					new OP().rename(new File(mineord + "/versions/Modinstaller/"+Version+".jar"), new File(mineord + "/versions/Modinstaller/Modinstaller.jar")); //Umbenennen in Modinstaller
					new OP().rename(new File(mineord + "/versions/Modinstaller/"+Version+".json"), new File(mineord + "/versions/Modinstaller/Modinstaller.json"));
					
					status(value += 3); //7
					
					String mode="Forge";
					if(Modloader==true) mode="Modloader";
					
					new OP().optionWriter("slastmc", new OP().optionReader("lastmc"));
					new OP().optionWriter("lastmc", Version);					
					new OP().optionWriter("slastmode", new OP().optionReader("lastmode"));
					new OP().optionWriter("lastmode", mode);					
					new OP().optionWriter("slastmods", new OP().optionReader("lastmods"));
					String modn="";
					for (int e=0; e<namen.length; e++)
					{
						modn+=namen[e]+";;";
					}
					new OP().optionWriter("lastmods", modn.substring(0, modn.length()-2));
					    
					status(value += 3);	//10	
					
					File json = new File(mineord + "/versions/Modinstaller/Modinstaller.json");
					if(json.exists())
					{
						String[] lines = new OP().Textreader(json);
						for (int i=0; i<lines.length; i++)
						{
							lines[i] = lines[i].replaceAll("\"id\": \""+Version, "\"id\": \"Modinstaller");  // z.B. 1.7.4 in JSON Datei durch Modinstaller ersetzen
						}
						new OP().Textwriter(json, lines, false);
					}
								
					if (Modloader==true) //Modloader Modus
					{
						new OP().copy(new File(mineord + "/versions/"+Version+"/"+Version+".jar"), new File(stamm + "/Modinstaller/minecraft.jar"));
						
						
						
						stat.setText(Read.getTextwith("seite3", "extra"));
						iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
						new Extrahieren(new File(stamm + "/Modinstaller/minecraft.jar"), new File(stamm + "/Modinstaller/Original/"));    // Entpacken						
					}					
					else //Forge Modus
					{						
						new OP().copy(new File(mineord + "/versions/"+Version+"/"+Version+".jar"), new File(stamm + "/Modinstaller/Modinstaller.jar"));
					}					
					new OP().copy(new File(stamm + "/Modinstaller/Original"),new File(stamm + "/Modinstaller/Result")); // Orginal in Ergebnisordner
					
					status(value += 5);
				} 
				catch (Exception ex) 
				{
					stat.setText("Errorcode: S3x01: " + String.valueOf(ex));
					Fehler += new OP().getStackTrace(ex) + " Errorcode: S3x01\n\n";
				}
				status(value += 5);	//15			
				
				if (online==true)																//Dateien herunterladen
				{			
					try 
					{						
						new OP().makedirs(new File(stamm + "/Modinstaller/Mods")); // Ordner anlegen	
						final double hinzu = 70/downloadlist.length;
						
						for (int k = 0; k < downloadlist.length; k++) 
						{
							quelle = String.valueOf(downloadlist[k]); // Downloadpfad für genauere Downloadliste speichern
							if(!quelle.equals("null")) 
							{	
								final String statt = Read.getTextwith("seite3", "prog8a") + namen[k] + "</b>"+Read.getTextwith("seite3", "prog8b");
								stat.setText(statt);
								iconf.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
								
								String Ort = quelle.substring(webplace.length(), quelle.length()-1);
								Ort = Ort.replace("/", "::");	
								String Downloadort = "http://files.minecraft-mods.de/installer/zipper.php?path="+Ort; //Downloadlink für ZIP Datei
								 
								final File Temporar= new File(stamm+"/Modinstaller/temp.zip");
								File Zeilverzeichnis = new File(stamm+"/Modinstaller/Mods/"+ namen[k]+"/");
								
								dow = new Download();
								
								t2 = new Thread()
								{			
									public void run() 
									{	
										double ges=value;										
										while(!isInterrupted())
										{	
											try 
											{
												int ist = dow.groesse(Temporar);												
												if(ist>1)
												{
													int soll = dow.getGroesse();													
													if(soll>1)
													{	
														double proz = Math.round(((double)ist/(double)soll)*1000.)/10.;
														stat.setText(statt+" - "+String.valueOf(proz)+"%");			       //Downloadstatus anzeigen	
														status(ges + hinzu*0.75*((double)ist/(double)soll));
													}																					
												}
												
												Thread.sleep(50);
											} 
											catch (Exception e) 
											{
												interrupt();
											} 
										}
									}
								};								
								t2.start();
								
								dow.downloadFile(Downloadort, new FileOutputStream(Temporar));	//ZIP Datei herunterladen
								
								t2.interrupt();	//Downloadgrößen-Thread beenden
								status(value+= hinzu*0.75);
								
								stat.setText(Read.getTextwith("seite3", "extra2")+namen[k]+"...");
								iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
								
								try
								{
									new Extrahieren(Temporar, Zeilverzeichnis); //Heruntergeladene ZIP Datei entpacken
								}
								catch(Exception ex)
								{
									stat.setText(Read.getTextwith("seite3", "prog9") + "<br><br>"+namen[k]);
								}
								
								if(Modloader) //Modloader
								{
									new OP().copy(Zeilverzeichnis, new File(stamm + "/Modinstaller/Result")); //in JAR Kompressionsordner
								}
								else  //Forge
								{
									new OP().copy(Zeilverzeichnis, new File(mineord)); //in .mincraft Ordner
								}
								
								
								stat.setText(Read.getTextwith("seite3", "prog8"));	 //nicht für Modloader oder Forge vorgesehene Dateien kopieren							
								try		
								{
									File extradatei = new File(stamm + "/Modinstaller/Mods/"+ namen[k] + "/extra.txt");
									
									if(extradatei.exists())
									{
										iconf.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
										String[] extrai = new OP().Textreader(extradatei);									
										for (int r=0; r<extrai.length; r++)
										{	
											String[]spl3 = extrai[r].split(";;");
											File g = new File(stamm+"/"+ spl3[1]);									
											new OP().makedirs(g.getParentFile());
											new Download().downloadFile(quelle+spl3[0],new FileOutputStream(g));
										}		
									}
								}
								catch(Exception ex)
								{
									stat.setText(Read.getTextwith("seite3", "prog9") + "/n/n"+namen[k]);
								}
								status(value += hinzu*0.25);
							} 							 
						}
						if (Modloader==false) //Forge Modus
						{												
							stat.setText(Read.getTextwith("seite3", "prog10"));  //Minecraft Forge herunterladen
							iconf.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));	
							final File libr = new File(stamm+"/Modinstaller/forge_"+Version+".zip");
							
							dowf = new Download();	
							
							if(!dowf.ident(webplace + Version +"/"+ "forge2.zip", libr))
							{
								t3 = new Thread()
								{			
									public void run() 
									{																	
										while(!isInterrupted())
										{	
											try 
											{
												int ist = dowf.groesse(libr);												
												if(ist>1)
												{
													int soll = dowf.getGroesse();													
													if(soll>1)
													{	
														double proz2 = Math.round(((double)ist/(double)soll)*1000.)/10.;
														stat.setText(Read.getTextwith("seite3", "forge") +" - "+String.valueOf(proz2)+"%");		       //Downloadstatus anzeigen															
													}																					
												}												
												Thread.sleep(50);
											} 
											catch (Exception e) 
											{
												interrupt();
											} 
										}
									}
								};								
								t3.start();								
								dowf.downloadFile(webplace + Version +"/"+ "forge2.zip", new FileOutputStream(libr));	//ZIP Datei herunterladen
								t3.interrupt();
							}
							
							status(value += 4);	
							iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
							new Extrahieren(libr, new File(mineord));
						}					
					}					 
					catch (Exception ex) 
					{
						stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
						Fehler += new OP().getStackTrace(ex) + " Errorcode: S3x04\n\n";
					}
			    }
				
				File zusatz = new File(stamm + "/Modinstaller/zusatz.txt");  // Importiertes kopieren
				if (zusatz.exists())  
				{
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/importieren.png")));
					try 
					{
						String[] read1 = new OP().Textreader(zusatz);
						for(int w=0; w<read1.length; w++)
						{
							stat.setText(Read.getTextwith("seite3", "prog11a") + read1[w] + Read.getTextwith("seite3", "prog11b"));
							File neu = new File(stamm + "/Modinstaller/Import/"+read1[w]+".txt");
							if(neu.exists())
							{
								String read2[] = new OP().Textreader(neu);
								for (int x=0; x<read2.length; x++)
								{
									String[] spl = read2[x].split(";;");
									if(spl.length==1)
									{	
										if(new File(spl[0]).isDirectory())
										{
											new OP().copy(new File(spl[0]), new File(stamm + "/Modinstaller/Result/"));
										}
										else
										{
											String name = new File(spl[0]).getName().toString().replace("\\", "/");
											new OP().copy(new File(spl[0]), new File(stamm + "/Modinstaller/Result/"+name));
										}									
									}
									else
									{									
										String von = spl[0];
										String nach = spl[1]+new File(spl[0]).getName().toString().replace("\\", "/");
										
										new OP().copy(new File(von), new File(nach));
									}
								}
							}
						}
					} 
					catch (IOException e) 
					{
						stat.setText("Errorcode: S3x05" + String.valueOf(e));
						Fehler += new OP().getStackTrace(e) + " Errorcode: S3x05\n\n";
					}
				}
				
				iconf.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
				if(Modloader==true)
				{					
					stat.setText(Read.getTextwith("seite3", "prog12"));	
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Komprimieren.png")));
					status(value += 5);
					new Komprimieren(new File(stamm + "/Modinstaller/Result/"), new File(mineord + "/versions/Modinstaller/Modinstaller.jar"));  // Komprimieren
					status(value += 10);
				}
				
				File profiles = new File(mineord + "/launcher_profiles.json");  //Minecraft Launcher: JSON Datei präparieren: Profil Modinstaller einstellen				
				if(profiles.exists())
				{				
					FileInputStream installProfile = null;
					try 
					{
						installProfile = new FileInputStream(profiles);
					} 
					catch (FileNotFoundException e1) 
					{						
						Fehler += new OP().getStackTrace(e1) + " Errorcode: S3xpr1\n\n";
					}
				    JdomParser parser = new JdomParser();
				    try
				    {
				    	versionData = parser.parse(new InputStreamReader(installProfile));
				    	installProfile.close();
				    }
				    catch (Exception e)
				    {
				    	Fehler += new OP().getStackTrace(e) + " Errorcode: S3xpr2\n\n";
				    }				 
				    try 
			    	{					    					    	
				        JsonField[] fields = {field("name", string("Modinstaller")), field("lastVersionId", string("Modinstaller")) };
				       

				        Map<JsonStringNode, JsonNode> profileCopy = new HashMap<JsonStringNode, JsonNode>(versionData.getNode(new Object[] { "profiles" }).getFields());
				        profileCopy.remove(string("Modinstaller"));
				        profileCopy.put(string("Modinstaller"), object(fields));				       
				        JsonRootNode profileJsonCopy = object(profileCopy);
				        
				        Map<JsonStringNode, JsonNode> rootCopy = new HashMap<JsonStringNode, JsonNode>(versionData.getFields());	    
				        rootCopy.put(string("profiles"), profileJsonCopy);
				        rootCopy.put(string("selectedProfile"), string("Modinstaller"));
				     //   rootCopy.remove(string("authenticationDatabase"));			        

				        JsonRootNode jsonProfileData = object(rootCopy); 
				    	
				        BufferedWriter newWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(profiles)));;
				        PrettyJsonFormatter.fieldOrderPreservingPrettyJsonFormatter().format(jsonProfileData, newWriter);
				        newWriter.close();				        									
					}			    	
			    	catch (Exception e) 
			    	{						
			    		Fehler += new OP().getStackTrace(e) + " Errorcode: S3xpr3\n\n";
					}					    
				}
				
				File sound = new File(mineord + "/assets/indexes/"+Version+".json");    //Sounddateien kopieren
				File soundc = new File(mineord + "/assets/indexes/Modinstaller.json");
				if(sound.exists())
				{
					try {
						new OP().copy(sound, soundc);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				start.setEnabled(true);
				back.setEnabled(false);
				bar.setValue(100);
				

				if (!Fehler.equals("")) // alle Fehler anzeigen
				{
					new Error(Fehler, Version);
					stat.setText(Read.getTextwith("seite3", "error2"));				
				} 
				else 
				{
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/play.png")));
					stat.setText(Read.getTextwith("seite3", "prog13"));	
					info.setText(Read.getTextwith("seite3", "prog14"));
					
				}
			}
		};
		t1.start();
	}

	public void status(double zahl) // Statusbar einstellen
	{
		bar.setValue((int) zahl);
	}

	@SuppressWarnings("deprecation")
	public void back_ActionPerformed(ActionEvent evt) // Vorgang abbrechen
	{
		if (JOptionPane.showConfirmDialog(this,	Read.getTextwith("seite3", "cancel"), Read.getTextwith("seite3", "cancelh"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
		{
			try 
			{	
				t1.stop();
				t2.stop();
				t3.stop();
				dow.exit();	
				dowf.exit();
				
			} 
			catch (Exception ex) 
			{
			}
			try
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
				
				new OP().del(new File(stamm+"/Modinstaller/Backup"));
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
			}
			catch (Exception ex)
			{				
			}
			dispose();			
			new Menu(); // beenden
		}
	}
}
