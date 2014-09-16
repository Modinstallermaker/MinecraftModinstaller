package installer;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

/**
 * 
 * Beschreibung
 * 
 * @version 2.1 vom 14.04.2013
 * @author Dirk Lippke
 */

public class Lizenz extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private JButton zur = new JButton();
	private JButton wei = new JButton();
	private JCheckBox check = new JCheckBox();
	private JLabel head = new JLabel();	
	private Method shapeMethod, transparencyMethod;
	private Class<?> utils;
	private Cursor c = new Cursor(Cursor.HAND_CURSOR);
	private String Lizenztext = "";
	private JEditorPane tp = new JEditorPane();
	private Scanner scan;

	public Lizenz() 
	{
		setUndecorated(true);

		try 
		{
			utils = Class.forName("com.sun.awt.AWTUtilities");
			shapeMethod = utils.getMethod("setWindowShape", Window.class,Shape.class);
			shapeMethod.invoke(null, this, new RoundRectangle2D.Double(0, 0,550, 336, 20, 20));
			transparencyMethod = utils.getMethod("setWindowOpacity",Window.class, float.class);
			transparencyMethod.invoke(null, this, .95f);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
		}
		setTitle(Read.getTextwith("installer", "name"));
		int frameWidth = 560;
		int frameHeight = 375;
		setSize(frameWidth, frameHeight);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width - getSize().width) / 2;
		int y = (d.height - getSize().height) / 2;
		setLocation(x, y);

		setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png"))	.getImage());

		JPanel cp = new GraphicsPanel(false, "src/page-bg.jpg");
		cp.setLayout(null);
		add(cp);
		
		String lang ="en";		
		
		try {
			lang = new OP().optionReader("language");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		scan = new Scanner(getClass().getResourceAsStream("src/lizenz_"+lang+".txt"), "ISO-8859-15");

		while (scan.hasNextLine()) 
		{
			Lizenztext += scan.nextLine();
		}
		
		tp.setEditable(false);
		tp.setContentType("text/html");
		tp.setEditorKit(new HTMLEditorKit()); 
		tp.setText("<html><body>" +Lizenztext	+ "</body></html>");
	
		JScrollPane ScrollPane2 = new JScrollPane(tp);
		ScrollPane2.setBounds(25, 50, 500, 200);
		cp.add(ScrollPane2);

		tp.setCaretPosition(0);

		zur.setCursor(c);
		zur.setBackground(null);
		zur.setBounds(10, 295, 110, 35);
		zur.setText(Read.getTextwith("lizenz", "text1"));
		zur.setMargin(new Insets(2, 2, 2, 2));
		zur.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				System.exit(0);
			}
		});
		cp.add(zur);
		wei.setCursor(c);
		wei.setBackground(null);
		wei.setBounds(410, 295, 130, 35);
		wei.setText(Read.getTextwith("lizenz", "text2"));
		wei.setMargin(new Insets(2, 2, 2, 2));
		wei.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				wei_ActionPerformed(evt);
			}
		});
		wei.setEnabled(false);
		cp.add(wei);
		check.setBounds(55, 260, 400, 25);
		check.setText(Read.getTextwith("lizenz", "text3"));
		check.setOpaque(false);
		check.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent evt) {
				check_ItemStateChanged(evt);
			}
		});
		cp.add(check);
		head.setBounds(24, 15, 243, 25);
		head.setFont(new Font("Dialog", Font.BOLD, 16));
		head.setText(Read.getTextwith("lizenz", "text4"));
		cp.add(head);

		setVisible(true);
	}

	// Anfang Methoden
	

	public void wei_ActionPerformed(ActionEvent evt) 
	{
		Date zeitstempel = new Date();
		
		try {
			new OP().optionWriter("lizenz", String.valueOf(zeitstempel));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		dispose();
		new Menu();
	}

	public void check_ItemStateChanged(ItemEvent evt) 
	{
		if (evt.getStateChange() == 1) 
		{
			wei.setEnabled(true);
		} 
		else 
		{
			wei.setEnabled(false);
		}
	}
}