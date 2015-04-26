package installer;

import static argo.jdom.JsonNodeFactories.field;
import static argo.jdom.JsonNodeFactories.object;
import static argo.jdom.JsonNodeFactories.string;

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

import javax.swing.ImageIcon;

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

public class Install extends InstallGUI
{
	private static final long serialVersionUID = 1L;
	
	private double value = 0.00;
	private Thread t1;
	private Download dow, dowf;
	private String webplace = Start.webplace, mineord = Start.mineord, stamm = Start.stamm, Version = Start.Version;	
	private String modsport = mineord + "versions/Modinstaller/";
	private String sport = stamm + "/Modinstaller/";
	private boolean online = Start.online;
	private String[] namen;
	private boolean Modloader;
	
	public static String Fehler="";
	
	public Install(final String[] namen, final boolean Modloader) 
	{
		this.namen=namen;
		this.Modloader=Modloader;
		GUI();
		installation();
	}
	
	public void installation()
	{
		t1 = new Thread() 
		{			
			private JsonRootNode versionData;
			@Override
			public void run() 
			{
				try 
				{
					status(value += 1); //1
					
					stat.setText(Read.getTextwith("seite3", "prog3"));                              //Wiederherstellungspunkt
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/restore2.png")));		
					new OP().del(new File(sport + "Backup"));		
					new OP().copy(new File(modsport), new File(sport + "Backup"));	
					new OP().copy(new File(mineord +"mods"), new File(sport + "Backup/mods"));		
					
					status(value += 5); //6
					
					stat.setText(Read.getTextwith("seite3", "prog1"));	//Löschen								
					new OP().del(new File(sport + "Result"));
					new OP().del(new File(sport + "Original"));					
					new OP().del(new File(mineord +"mods"));
					new OP().del(new File(mineord +"coremods"));
					new OP().del(new File(mineord +"config"));	
					new OP().del(new File(modsport));
					
					stat.setText(Read.getTextwith("seite3", "prog2"));            //Anlegen
					new OP().makedirs(new File(sport + "Result"));
					new OP().makedirs(new File(sport + "Backup"));	
					new OP().makedirs(new File(modsport));
					
					new OP().copy(new File(mineord + "versions/"+Version), new File(modsport)); //von Versions Ordner in Modinstaller Ordner kopieren
					new OP().rename(new File(modsport + Version+".jar"), new File(modsport + "Modinstaller.jar")); //Umbenennen in Modinstaller
					new OP().rename(new File(modsport + Version+".json"), new File(modsport + "Modinstaller.json"));
					
					status(value += 2); //8
					
					if(Modloader)  // Entpacken	
					{
						stat.setText(Read.getTextwith("seite3", "extra"));
						iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
						new Extract(new File(modsport+"Modinstaller.jar"), new File(sport + "Result/"));  
					}
					
					try
					{
						String mode="Forge";
						if(Modloader==true) mode="Modloader";
					
						new OP().optionWriter("slastmc", new OP().optionReader("lastmc"));
						new OP().optionWriter("lastmc", Version);					
						new OP().optionWriter("slastmode", new OP().optionReader("lastmode"));
						new OP().optionWriter("lastmode", mode);	
											
						if(namen.length!=0)
						{
							new OP().optionWriter("slastmods", new OP().optionReader("lastmods"));
							String modn="";
							for (int e=0; e<namen.length; e++)
							{
								modn+=namen[e]+";;";
							}
							if(modn.endsWith(";;"))
								modn = modn.substring(0, modn.length()-2);
							new OP().optionWriter("lastmods", modn);
						}
						else
						{
							new OP().optionWriter("slastmods", "n/a");
							new OP().optionWriter("lastmods", "n/a");
						}						
					}
					catch (Exception e)	{}
					
					status(value += 2);	//10	
				} 
				catch (Exception ex) 
				{
					stat.setText("Errorcode: S3x01: " + String.valueOf(ex));
					Fehler += new OP().getStackTrace(ex) + " Errorcode: S3x01\n\n";
				}
				
				if (online==true)																//Dateien herunterladen
				{			
					try 
					{	
						new OP().makedirs(new File(sport + "Mods")); // Ordner anlegen	
						
						double hinzu = 10;
						try
						{
							if(namen.length>0)
							{
								hinzu = 75/namen.length;	
								if(!Modloader)
								{
									hinzu = 85/(namen.length+1);
								}
							}
							else
							{								
								if(Modloader)
								{
									status(value += 70);	
								}
								else
								{
									status(value += 80);	
								}								
							}
						}
						catch (Exception e)
						{							
						}
						
						for (int k = 0; k < namen.length; k++) 
						{
							String statt = Read.getTextwith("seite3", "prog8a") + namen[k] + "</b>"+Read.getTextwith("seite3", "prog8b");
							stat.setText(statt);
							iconf.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
							
							int art =3;
							if(Modloader) art=0;
							String Downloadort = "http://www.minecraft-installer.de//api/download.php?MC="+Version+"&Mod="+namen[k]+"&Art="+art; //Downloadlink für ZIP Datei				
							File Temporar= new File(sport + "temp.zip");
							File Zeilverzeichnis = new File(sport + "Mods/"+ namen[k]+"/");
															
							try
							{	
								dow = new Download();
								
								Thread t = new Thread(new Downloadstate(dow, Temporar, stat, statt, hinzu, value)); //Prozent berechnen und anzeigen
								t.start();
								
								dow.downloadFile(Downloadort, new FileOutputStream(Temporar));	//ZIP Datei herunterladen
								
								t.interrupt();	//Downloadgrößen-Thread beenden
								status(value+= hinzu*0.75);
								
								stat.setText(Read.getTextwith("seite3", "extra2")+namen[k]+"...");
								iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));	
								new Extract(Temporar, Zeilverzeichnis); //Heruntergeladene ZIP Datei entpacken
								
								if(Modloader) //Modloader
								{
									new OP().copy(Zeilverzeichnis, new File(sport + "Result")); //in JAR Kompressionsordner
								}
								else  //Forge
								{
									new OP().copy(Zeilverzeichnis, new File(mineord)); //in .mincraft Ordner
								}
							}
							catch (Exception ex)
							{
								stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
								Fehler += "Mod: "+namen[k] + " " + Version +"\nSource: "+Downloadort+"\nFrom: "+Temporar.toString()+"\nTo: "+Zeilverzeichnis.toString()+"\nException:\n"+ new OP().getStackTrace(ex) + "\nErrorcode: S3x04\n\n";
							}	
						} 
						status(value += hinzu*0.25);
						
						if (!Modloader) //Forge Modus
						{	
							String text = Read.getTextwith("seite3", "forge");
							stat.setText(text);  
							iconf.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));	
							File libr = new File(sport + "forge_"+Version+".zip");                //Downloadort Forge
							dowf = new Download();	
							String forgeort = webplace + Version +"/"+ "forge2.zip";
														
							if(!dowf.ident(forgeort, libr))  //Minecraft Forge herunterladen
							{
								Thread t2 = new Thread(new Downloadstate(dowf, libr, stat, text, hinzu, value)); //Prozent berechnen und anzeigen
								t2.start();					
								try
								{
									dowf.downloadFile(forgeort, new FileOutputStream(libr));	//ZIP Datei herunterladen
								}
								catch (Exception ex)
								{
									stat.setText("Errorocde: S3x04a: " + String.valueOf(ex));
									Fehler += "Forge "+Version +"\nTo: "+libr+"\nException:\n"+ new OP().getStackTrace(ex) + "\nErrorcode: S3x04d\n\n";
								}								
								t2.interrupt();
							}							
							status(value += hinzu);	
							
							iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
							try
							{
								new Extract(libr, new File(mineord));
							}
							catch(Exception ex)
							{								
								Fehler += "Forge "+Version +"\nSource: "+forgeort+"\nFrom: "+libr.toString()+"\nTo: "+mineord.toString()+"\nException:\n"+ new OP().getStackTrace(ex) + "\nErrorcode: S3x04e\n\n";
							}							
						}					
					}					 
					catch (Exception ex) 
					{
						stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
						Fehler += new OP().getStackTrace(ex) + " Errorcode: S3x04\n\n";
					}
			    }
				
				iconf.setIcon(new ImageIcon(this.getClass().getResource("src/import.png")));  // Importiertes kopieren
				File importord = new File(stamm+"Modinstaller/Import/");
				if(Modloader)
				{
					try {
						new OP().copy(importord, new File(sport + "Result/"));
					} catch (FileNotFoundException e) {	
						e.printStackTrace();
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
				else
				{
					try {
						new OP().copy(importord, new File(mineord + "mods/"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				iconf.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
				
				if(Modloader)     //Dateien in Minecraft JAR bei Modloader Modus komprimieren
				{					
					stat.setText(Read.getTextwith("seite3", "prog12"));	
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/Komprimieren.png")));
					status(value += 5);
					new Compress(new File(sport + "Result/"), new File(modsport +"Modinstaller.jar"));  // Komprimieren
					status(value += 5);
				}			
				
				try
				{
					File json = new File(modsport+"Modinstaller.json");
					if(json.exists())
					{					
						String[] lines = new OP().Textreader(json);
						for (int i=0; i<lines.length; i++)
						{												
							lines[i] = lines[i].replaceAll("\"id\": \""+Version+"\",", "\"id\": \"Modinstaller\",");  // z.B. 1.7.4 in JSON Datei durch 1.7.10_Mods ersetzen									
						}
						new OP().Textwriter(json, lines, false);
					}
				}
				catch (Exception e)
				{					
				}
				
				File profiles = new File(mineord + "launcher_profiles.json");  //Minecraft Launcher: JSON Datei präparieren: Profil Modinstaller einstellen				
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
				
				File sound = new File(mineord + "assets/indexes/"+Version+".json");    //Sounddateien kopieren
				File soundc = new File(mineord + "assets/indexes/Modinstaller.json");
				if(sound.exists())
				{
					try 
					{
						new OP().copy(sound, soundc);
					} 
					catch (Exception e) 
					{						
					}
				}

				start.setEnabled(true);
				back.setEnabled(false);
				bar.setValue(100);
				
				if (!Fehler.equals("")) // alle Fehler anzeigen
				{
					new Error(Fehler);
					stat.setText(Read.getTextwith("seite3", "error2"));				
				} 
				else 
				{
					bar.setVisible(false);
					banner.setVisible(false);
					social[0].setVisible(true);
					social[1].setVisible(true);
					social[2].setVisible(true);
					iconf.setVisible(false);
					iconf.setIcon(new ImageIcon(this.getClass().getResource("src/play.png")));
					stat.setText("");	
					info.setText(Read.getTextwith("seite3", "prog13"));					
				}
			}
		};
		t1.start();
	}

	public static void status(double zahl) // Statusbar einstellen
	{
		bar.setValue((int) zahl);
	}
}
