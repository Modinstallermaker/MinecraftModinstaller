package installer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;

class DragDropListener implements DropTargetListener 
{	
	MenuGUI men;
	public DragDropListener(MenuGUI men)
	{
		this.men=men;
	}
		@Override
	    public void drop(DropTargetDropEvent event) {	
	      event.acceptDrop(DnDConstants.ACTION_COPY);	
	      Transferable transferable = event.getTransferable();
	      DataFlavor[] flavors = transferable.getTransferDataFlavors();	    
	      for (DataFlavor flavor : flavors) 
	      {		        
	        try 
	        {		         
	          if (flavor.isFlavorJavaFileListType()) 
	          {  
				@SuppressWarnings("unchecked")
				List<File>  files = (List<File>) transferable.getTransferData(flavor);
		            
			       for (final File filex : files) 
			       {	
			    	   men.modNameLabel.setText("Loading Mod...");
			    	   men.modDescPane.setText("Loading mod file \""+filex.getName()+"\"...");
			    	   men.picture.setIcon(new ImageIcon(this.getClass().getResource("src/wait.gif")));
						new Thread(){
							public void run()
							{
								  new Import(filex, men);									
							}
						}.start();					
		           }		            
	          }	          
	        } 
	        catch (Exception e) 
	        {		          
	        	e.printStackTrace();		          
	        }
	      }		      
	    	 event.dropComplete(true);			    	
	    }
	    
	    @Override
	    public void dragEnter(DropTargetDragEvent event) {
	    }
	    
	    @Override
	    public void dragExit(DropTargetEvent event) {
	    }
	    
	    @Override
	    public void dragOver(DropTargetDragEvent event) {
	    }
	    
	    @Override
	    public void dropActionChanged(DropTargetDragEvent event) {
	    }
	    
} 