package installer;

import java.io.File;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

class SimpleGraphicFileFilter extends javax.swing.filechooser.FileFilter 
{ 
    public String getDescription() 
    { 
       return "JAR Archive (*.jar)"; 
    } 

    public boolean accept(File file) 
    { 
       if(file.isDirectory()) 
          return true; 
       else if(file.getName().endsWith(".jar")) 
          return true;  
       else 
          return false; 
    } 
 }
