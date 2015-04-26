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

class DragDropListener implements DropTargetListener 
{	
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
		            
			       for (File filex : files) 
			       {				    	   
			    	   new Import(filex);
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