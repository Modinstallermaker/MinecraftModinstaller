package installer;

import static installer.OP.Textreader;
import static installer.OP.Textreaders;
import static installer.OP.Textwriter;
import static installer.OP.Textwriters;
import static installer.OP.copy;
import static installer.OP.del;
import static installer.OP.getError;
import static installer.OP.makedirs;
import static installer.OP.optionReader;
import static installer.OP.optionWriter;
import static installer.OP.rename;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
	
	public Install(final String[] modnames, final boolean Modloader, final int downloadsize) 
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
			@Override
			public void run() 
			{
				try 
				{
					status(value += 5);
					
					stat.setText(Read.getTextwith("seite3", "prog3"));                              //Wiederherstellungspunkt
					stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/restore2.png")));		
					del(new File(sport + "Backup"));		
					copy(new File(modsport), new File(sport + "Backup"));	
					copy(new File(mineord +"mods"), new File(sport + "Backup/mods"));		
					
					status(value += 5);
					
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
					
					status(value += 5);
					
					copy(new File(mineord + "versions/"+Version), new File(modsport)); //von Versions Ordner in Modinstaller Ordner kopieren
					rename(new File(modsport + Version+".jar"), new File(modsport + "Modinstaller.jar")); //Umbenennen in Modinstaller
					rename(new File(modsport + Version+".json"), new File(modsport + "Modinstaller.json"));
					
					status(value += 5);
					
					if(Modloader)  // Entpacken	
					{
						stat.setText(Read.getTextwith("seite3", "extra"));
						stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
						new Extract(new File(modsport+"Modinstaller.jar"), new File(sport + "Result/"));  
					}
					status(value += 5);
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
					
					status(value += 5);		
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
								
								Thread t = new Thread(new Downloadstate(dow, Temporar, stat, statt)); //Prozent berechnen und anzeigen
								t.start();
								
								dow.downloadFile(Downloadort, new FileOutputStream(Temporar));	//ZIP Datei herunterladen
								
								t.interrupt();	//Downloadgrößen-Thread beenden
								status(value=0);
								
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
								Fehler += "Mod: "+modnames[k] + " " + Version +"\nSource: "+Downloadort+"\nFrom: "+Temporar.toString()+
										"\nTo: "+Zeilverzeichnis.toString()+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04\n\n";
							}	
						} 						
						status(value=0);
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
								Thread t2 = new Thread(new Downloadstate(dowf, libr, stat, text)); //Prozent berechnen und anzeigen
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
							status(value = 5);	
							
							stateIcon.setIcon(new ImageIcon(this.getClass().getResource("src/Extrahieren.png")));
							try
							{
								new Extract(libr, new File(mineord));
							}
							catch(Exception ex)
							{								
								Fehler += "Forge "+Version +"\nSource: "+forgeort+"\nFrom: "+libr.toString()+
										"\nTo: "+mineord.toString()+"\nException:\n"+ getError(ex) + "\nErrorcode: S3x04e\n\n";
							}
							status(value = 15);
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
							lines[i] = lines[i].replaceAll("\"id\": \""+Version+"\",", "\"id\": \"Modinstaller\",");  
							// z.B. 1.7.4 in JSON Datei durch 1.7.10_Mods ersetzen									
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
					Gson gson = new Gson(); 
			        try 
			        {          
			            String jsontext = Textreaders(profiles);
			            JsonObject jfile = gson.fromJson(jsontext, JsonObject.class); 
			            jfile.remove("selectedProfile");
			            jfile.addProperty("selectedProfile", "Modinstaller");
				            JsonObject jprofiles = jfile.get("profiles").getAsJsonObject();
					        jprofiles.remove("Modinstaller");
					        	JsonObject sub = new JsonObject();
					            sub.addProperty("name", "Modinstaller");
					            sub.addProperty("lastVersionId", "Modinstaller");	
				            jprofiles.add("Modinstaller", sub);
			            jfile.remove("profiles");
			            jfile.add("profiles", jprofiles);			            
			            Textwriters(profiles, gson.toJson(jfile), false);			                   
			        }
			        catch (Exception e)
			        {
			        	Fehler += getError(e) + " Errorcode: Pro\n\n";
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
