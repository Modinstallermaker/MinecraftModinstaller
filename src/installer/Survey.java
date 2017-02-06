package installer;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Survey  extends JFrame implements ActionListener
{
  private static final long serialVersionUID = 1L;
  JButton yesb = new JButton(Read.getTextwith("MOL", "compj"));
  JButton db = new JButton(Read.getTextwith("MOL", "compk"));
  JButton nob = new JButton(Read.getTextwith("MOL", "compn"));
  JButton errorb = new JButton(Read.getTextwith("MOL", "compe"));
  private Cursor c = new Cursor(12);
  int sel = -1;
  
  public Survey()
  {
    setTitle("Minecraft Modinstaller - Mods ok?");
    if ((!new File(Start.sport + "Importo").exists()) && 
      (!OP.optionReader("lastmods").equals(OP.optionReader("slastmods"))) && 
      (OP.optionReader("changed").equals("true")))
    {
      OP.optionWriter("changed", "false");
      
      JPanel pane = new JPanel();
      pane.setLayout(new BorderLayout());
      
      JLabel question = new JLabel(Read.getTextwith("MOL", "compt"));
      question.setHorizontalAlignment(0);
      question.setFont(new Font("Dialog", 1, 14));
      question.setCursor(this.c);
      question.setPreferredSize(new Dimension(600, 80));
      pane.add(question, "First");
      
      this.yesb.addActionListener(this);
      this.yesb.setCursor(this.c);
      this.yesb.setPreferredSize(new Dimension(200, 100));
      pane.add(this.yesb, "Before");
      
      this.db.addActionListener(this);
      this.db.setCursor(this.c);
      this.db.setPreferredSize(new Dimension(200, 100));
      pane.add(this.db, "Center");
      
      this.nob.addActionListener(this);
      this.nob.setCursor(this.c);
      this.nob.setPreferredSize(new Dimension(200, 100));
      pane.add(this.nob, "After");
      
      this.errorb.addActionListener(this);
      this.errorb.setCursor(this.c);
      this.errorb.setPreferredSize(new Dimension(600, 60));
      pane.add(this.errorb, "Last");
      
      add(pane);
      setIconImage(new ImageIcon(getClass().getResource("src/icon.png")).getImage());
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
    }
  }
  
  private void send()
  {
    String body = "Mods=" + OP.optionReader("lastmods") + "&" + "Rate=" + this.sel+"&Vers="+Start.modinstallerVersion;
    try
    {
      new Postrequest("http://www.minecraft-installer.de/api/compSet.php", body);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    dispose();
  }
  
  public void actionPerformed(ActionEvent e)
  {
    Object s = e.getSource();
    if (s == this.yesb)
    {
      this.sel = 3;
      send();
    }
    else if (s == this.db)
    {
      this.sel = 2;
      send();
    }
    else if (s == this.nob)
    {
      this.sel = 1;
      send();
    }
    else if (s == this.errorb)
    {
      this.sel = 0;
      send();
    }
  }
}
