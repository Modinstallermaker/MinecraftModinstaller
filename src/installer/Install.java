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

import static installer.OP.*;
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
	private String webplace = Start.webplace, mineord = Start.mineord, stamm = Start.stamm, Version = Start.mcVersion;	
	private boolean online = Start.online;
	private double value = 0.00;	
	private Download dow, dowf;	
	private String modsport = mineord + "versions/Modinstaller/";
	private String sport = stamm + "Modinstaller/";	
	private String[] modnames;
	private boolean Modloader;
	
	public static String Fehler="";
	
	public Install(final String[] modnames, final boolean Modloader) 
	{
		this.modnames=modnames;
		this.Modloader=Modloader;
		GUI();
		installation();
	}
	
	public void installation()
	{
		new Thread() 
		{			
			private JsonRootNode versionData;
			@Override
			public void run() 
			{
				try 
				{
					status(value += 1); //1
					
					stat.setText(Read.getTextwith("seite3", "prog3"));                              //Wiederherstellungspunkt
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/restore2.png")));		
					del(new File(sport + "Backup"));		
					copy(new File(modsport), new File(sport + "Backup"));	
					copy(new File(mineord +"mods"), new File(sport + "Backup/mods"));		
					
					status(value += 5); //6
					
					stat.setText(Read.getTextwith("seite3", "prog1"));	//Löschen								
					del(new File(sport + "Result"));
					del(new File(sport + "Original"));					
					del(new File(mineord +"mods"));
					del(new File(mineord +"coremods"));
					del(new File(mineord +"config"));	
					del(new File(modsport));
					
					stat.setText(Read.getTextwith("seite3", "prog2"));            //Anlegen
					makedirs(new File(sport + "Result"));
					makedirs(new File(sport + "Backup"));	
					makedirs(new File(modsport));
					
					copy(new File(mineord + "versions/"+Version), new File(modsport)); //von Versions Ordner in Modinstaller Ordner kopieren
					rename(new File(modsport + Version+".jar"), new File(modsport + "Modinstaller.jar")); //Umbenennen in Modinstaller
					rename(new File(modsport + Version+".json"), new File(modsport + "Modinstaller.json"));
					
					status(value += 2); //8
					
					if(Modloader)  // Entpacken	
					{
						stat.setText(Read.getTextwith("seite3", "extra"));
						stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
						new Extract(new File(modsport+"Modinstaller.jar"), new File(sport + "Result/"));  
					}
					
					try
					{
						String mode="Forge";
						if(Modloader==true) mode="Modloader";
					
						optionWriter("slastmc", optionReader("lastmc"));
						optionWriter("lastmc", Version);					
						optionWriter("slastmode", optionReader("lastmode"));
						optionWriter("lastmode", mode);	
											
						if(modnames.length!=0)
						{
							optionWriter("slastmods", optionReader("lastmods"));
							String modn="";
							for (int e=0; e<modnames.length; e++)
							{
								modn+=modnames[e]+";;";
							}
							if(modn.endsWith(";;"))
								modn = modn.substring(0, modn.length()-2);
							optionWriter("lastmods", modn);
						}
						else
						{
							optionWriter("slastmods", "n/a");
							optionWriter("lastmods", "n/a");
						}						
					}
					catch (Exception e)	{}
					
					status(value += 2);	//10	
				} 
				catch (Exception ex) 
				{
					stat.setText("Errorcode: S3x01: " + String.valueOf(ex));
					Fehler += getError(ex) + " Errorcode: S3x01\n\n";
				}
				
				if (online==true)																//Dateien herunterladen
				{			
					try 
					{	
						makedirs(new File(sport + "Mods")); // Ordner anlegen	
						
						double hinzu = 10;
						try
						{
							if(modnames.length>0)
							{
								hinzu = 75/modnames.length;	
								if(!Modloader)
								{
									hinzu = 85/(modnames.length+1);
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
						
						for (int k = 0; k < modnames.length; k++) 
						{
							String statt = Read.getTextwith("seite3", "prog8a") + modnames[k] + "</b>"+Read.getTextwith("seite3", "prog8b");
							stat.setText(statt);
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));
							
							int art =3;
							if(Modloader) art=0;
							String Downloadort = "http://www.minecraft-installer.de//api/download.php?MC="+Version+"&Mod="+modnames[k]+"&Art="+art; //Downloadlink für ZIP Datei				
							File Temporar= new File(sport + "temp.zip");
							File Zeilverzeichnis = new File(sport + "Mods/"+ modnames[k]+"/");
															
							try
							{	
								dow = new Download();
								
								Thread t = new Thread(new Downloadstate(dow, Temporar, stat, statt, hinzu, value)); //Prozent berechnen und anzeigen
								t.start();
								
								dow.downloadFile(Downloadort, new FileOutputStream(Temporar));	//ZIP Datei herunterladen
								
								t.interrupt();	//Downloadgrößen-Thread beenden
								status(value+= hinzu*0.75);
								
								stat.setText(Read.getTextwith("seite3", "extra2")+modnames[k]+"...");
								stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));	
								new Extract(Temporar, Zeilverzeichnis); //Heruntergeladene ZIP Datei entpacken
								
								if(Modloader) //Modloader
								{
									copy(Zeilverzeichnis, new File(sport + "Result")); //in JAR Kompressionsordner
								}
								else  //Forge
								{
									copy(Zeilverzeichnis, new File(mineord)); //in .mincraft Ordner
								}
							}
							catch (Exception ex)
							{
								stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
								Fehler += "Mod: "+modnames[k] + " " + Version +"\nSource: "+Downloadort+"\nFrom: "+Temporar.toString()+"\nTo: "+Zeilverzeichnis.toString()+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04\n\n";
							}	
						} 
						status(value += hinzu*0.25);
						
						if (!Modloader) //Forge Modus
						{	
							String text = Read.getTextwith("seite3", "forge");
							stat.setText(text);  
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/download.png")));	
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
									Fehler += "Forge "+Version +"\nTo: "+libr+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04d\n\n";
								}								
								t2.interrupt();
							}							
							status(value += hinzu);	
							
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
							try
							{
								new Extract(libr, new File(mineord));
							}
							catch(Exception ex)
							{								
								Fehler += "Forge "+Version +"\nSource: "+forgeort+"\nFrom: "+libr.toString()+"\nTo: "+mineord.toString()+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04e\n\n";
							}							
						}					
					}					 
					catch (Exception ex) 
					{
						stat.setText("Errorocde: S3x04: " + String.valueOf(ex));
						Fehler += getError(ex) + " Errorcode: S3x04\n\n";
					}
			    }
				
				stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/import.png")));  // Importiertes kopieren
				File importord = new File(stamm+"Modinstaller/Import/");
				if(Modloader)
				{
					try {
						copy(importord, new File(sport + "Result/"));
					} catch (FileNotFoundException e) {	
						e.printStackTrace();
					} catch (IOException e) {						
						e.printStackTrace();
					}
				}
				else
				{
					try {
						copy(importord, new File(mineord + "mods/"));
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/install.png")));
				
				if(Modloader)     //Dateien in Minecraft JAR bei Modloader Modus komprimieren
				{					
					stat.setText(Read.getTextwith("seite3", "prog12"));	
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Komprimieren.png")));
					status(value += 5);
					new Compress(new File(sport + "Result/"), new File(modsport +"Modinstaller.jar"));  // Komprimieren
					status(value += 5);
				}			
				
				try
				{
					File json = new File(modsport+"Modinstaller.json");
					if(json.exists())
					{					
						String[] lines = Textreader(json);
						for (int i=0; i<lines.length; i++)
						{												
							lines[i] = lines[i].replaceAll("\"id\": \""+Version+"\",", "\"id\": \"Modinstaller\",");  // z.B. 1.7.4 in JSON Datei durch 1.7.10_Mods ersetzen									
						}
						Textwriter(json, lines, false);
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
						Fehler += getError(e1) + " Errorcode: S3xpr1\n\n";
					}
				    JdomParser parser = new JdomParser();
				    try
				    {
				    	versionData = parser.parse(new InputStreamReader(installProfile));
				    	installProfile.close();
				    }
				    catch (Exception e)
				    {
				    	Fehler += getError(e) + " Errorcode: S3xpr2\n\n";
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
			    		Fehler += getError(e) + " Errorcode: S3xpr3\n\n";
					}					    
				}
				
				File sound = new File(mineord + "assets/indexes/"+Version+".json");    //Sounddateien kopieren
				File soundc = new File(mineord + "assets/indexes/Modinstaller.json");
				if(sound.exists())
				{
					try 
					{
						copy(sound, soundc);
					} 
					catch (Exception e) 
					{						
					}
				}

				startMCButton.setEnabled(true);				
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
					socialIcons[0].setVisible(true);
					socialIcons[1].setVisible(true);
					socialIcons[2].setVisible(true);
					stateIcon.setVisible(false);
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/play.png")));
					stat.setText("");	
					info.setText(Read.getTextwith("seite3", "prog13"));					
				}
			}
		}.start();
	}

	public static void status(double zahl) // Statusbar einstellen
	{
		bar.setValue((int) zahl);
	}
}
