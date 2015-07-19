package installer;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

public class HintTextField extends JTextField implements FocusListener 
{

	  /**
	 * 
	 */
	private static final long serialVersionUID = 4677918701886147670L;
	private final String hint;
	private boolean showingHint;
	private Color stdc;

	  public HintTextField(final String hint) {
	    super(hint);
	    this.hint = hint;
	    stdc = super.getForeground();
	    super.setForeground(Color.GRAY);
	    this.showingHint = true;
	    super.addFocusListener(this);
	  }

	  @Override
	  public void focusGained(FocusEvent e) {
	    if(this.getText().isEmpty()) {	    	
	      super.setText("");
	      super.setForeground(stdc);
	      showingHint = false;
	    }
	  }
	  @Override
	  public void focusLost(FocusEvent e) {
	    if(this.getText().isEmpty()) {
	    	reset();
	    }
	  }
	  
	  public void reset()
	  {
		  super.setText(hint);
	      super.setForeground(Color.GRAY);
	      showingHint = true;
	  }

	  @Override
	  public String getText() {
	    return showingHint ? "" : super.getText();
	  }
}
