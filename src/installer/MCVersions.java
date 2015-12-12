package installer;

import javax.swing.ImageIcon;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Dirk
 */
public class MCVersions extends javax.swing.JFrame {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * Creates new form NewJFrame
     */
    public MCVersions() {
        initComponents();
    }
                     
    private void initComponents() {

        panel_top = new javax.swing.JPanel();
        question_t = new javax.swing.JLabel();
        pannel_bottom = new javax.swing.JPanel();
        forge_only = new javax.swing.JCheckBox();
        panel_middle = new javax.swing.JPanel();
        panel_rightb = new javax.swing.JPanel();
        text_rightb = new javax.swing.JLabel();
        panel_leftb = new javax.swing.JPanel();
        text_leftb = new javax.swing.JLabel();
        panel_centermenu = new javax.swing.JPanel();
        panel_left = new javax.swing.JPanel();
        text_left = new javax.swing.JLabel();
        panel_right = new javax.swing.JPanel();
        text_right = new javax.swing.JLabel();
        panel_center = new javax.swing.JPanel();
        text_center = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(550, 290));
        setIconImage(new ImageIcon(this.getClass().getResource("src/icon.png")).getImage());

        question_t.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        question_t.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        question_t.setText("Welche Minecaft Version möchtest Du modifizieren?");
        question_t.setToolTipText("");

        javax.swing.GroupLayout panel_topLayout = new javax.swing.GroupLayout(panel_top);
        panel_top.setLayout(panel_topLayout);
        panel_topLayout.setHorizontalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(question_t, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panel_topLayout.setVerticalGroup(
            panel_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(question_t, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
        );

        forge_only.setSelected(true);
        forge_only.setText("Nur MC Versionen anzeigen, für die Forge Mods verfügbar sind");
        forge_only.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        forge_only.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                forge_onlyStateChanged(evt);
            }
        });

        javax.swing.GroupLayout pannel_bottomLayout = new javax.swing.GroupLayout(pannel_bottom);
        pannel_bottom.setLayout(pannel_bottomLayout);
        pannel_bottomLayout.setHorizontalGroup(
            pannel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(forge_only, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pannel_bottomLayout.setVerticalGroup(
            pannel_bottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(forge_only, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
        );

        panel_middle.setLayout(new java.awt.BorderLayout());

        text_rightb.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        text_rightb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_rightb.setText(">>");
        text_rightb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_rightbMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_rightbMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_rightbLayout = new javax.swing.GroupLayout(panel_rightb);
        panel_rightb.setLayout(panel_rightbLayout);
        panel_rightbLayout.setHorizontalGroup(
            panel_rightbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_rightb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_rightbLayout.setVerticalGroup(
            panel_rightbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_rightb, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_middle.add(panel_rightb, java.awt.BorderLayout.LINE_END);

        text_leftb.setFont(new java.awt.Font("Tahoma", 1, 30)); // NOI18N
        text_leftb.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_leftb.setText("<<");
        text_leftb.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_leftbMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_leftbMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_leftbLayout = new javax.swing.GroupLayout(panel_leftb);
        panel_leftb.setLayout(panel_leftbLayout);
        panel_leftbLayout.setHorizontalGroup(
            panel_leftbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_leftb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_leftbLayout.setVerticalGroup(
            panel_leftbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_leftb, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_middle.add(panel_leftb, java.awt.BorderLayout.LINE_START);

        panel_centermenu.setLayout(new java.awt.BorderLayout());

        text_left.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_left.setText("<html><center><b><span style='font-size:16px'>1.7.2</span></b><br> <br> <b><span style='font-size:12px'>180</b></b><br> verfügbare Mods<br> <br><i>bereits installiert</i></center></html>");
        text_left.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_leftMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_leftMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_leftLayout = new javax.swing.GroupLayout(panel_left);
        panel_left.setLayout(panel_leftLayout);
        panel_leftLayout.setHorizontalGroup(
            panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_left, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_leftLayout.setVerticalGroup(
            panel_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_left, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_centermenu.add(panel_left, java.awt.BorderLayout.LINE_START);

        text_right.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_right.setText("<html><center><b><span style='font-size:16px'>1.8</span></b><br> <br> <b><span style='font-size:12px'>160</b></b><br> verfügbare Mods<br> <br><i>bereits installiert</i></center></html>");
        text_right.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_rightMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_rightMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_rightLayout = new javax.swing.GroupLayout(panel_right);
        panel_right.setLayout(panel_rightLayout);
        panel_rightLayout.setHorizontalGroup(
            panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_right, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
        );
        panel_rightLayout.setVerticalGroup(
            panel_rightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_right, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_centermenu.add(panel_right, java.awt.BorderLayout.LINE_END);

        text_center.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        text_center.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        text_center.setText("<html><center><b><span style='font-size:22px'>1.7.10</span></b><br><br><b><span style='font-size:18px'>280</span></b><br>verfügbare Mods<br><br><i><span style='font-size:10px'>bereits installiert</span></i></center></html>");
        text_center.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                text_centerMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                text_centerMouseEntered(evt);
            }
        });

        javax.swing.GroupLayout panel_centerLayout = new javax.swing.GroupLayout(panel_center);
        panel_center.setLayout(panel_centerLayout);
        panel_centerLayout.setHorizontalGroup(
            panel_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_center, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE)
        );
        panel_centerLayout.setVerticalGroup(
            panel_centerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(text_center, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
        );

        panel_centermenu.add(panel_center, java.awt.BorderLayout.CENTER);

        panel_middle.add(panel_centermenu, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pannel_bottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panel_middle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel_middle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pannel_bottom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>                        

    private void text_centerMouseEntered(java.awt.event.MouseEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void text_leftMouseEntered(java.awt.event.MouseEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void text_leftbMouseEntered(java.awt.event.MouseEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void text_leftbMouseClicked(java.awt.event.MouseEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void text_leftMouseClicked(java.awt.event.MouseEvent evt) {                                       
        // TODO add your handling code here:
    }                                      

    private void text_centerMouseClicked(java.awt.event.MouseEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void text_rightMouseClicked(java.awt.event.MouseEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void text_rightMouseEntered(java.awt.event.MouseEvent evt) {                                        
        // TODO add your handling code here:
    }                                       

    private void text_rightbMouseEntered(java.awt.event.MouseEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void text_rightbMouseClicked(java.awt.event.MouseEvent evt) {                                         
        // TODO add your handling code here:
    }                                        

    private void forge_onlyStateChanged(javax.swing.event.ChangeEvent evt) {                                        
        // TODO add your handling code here:
    }                                      

 

    // Variables declaration - do not modify                     
    private javax.swing.JCheckBox forge_only;
    private javax.swing.JPanel panel_center;
    private javax.swing.JPanel panel_centermenu;
    private javax.swing.JPanel panel_left;
    private javax.swing.JPanel panel_leftb;
    private javax.swing.JPanel panel_middle;
    private javax.swing.JPanel panel_right;
    private javax.swing.JPanel panel_rightb;
    private javax.swing.JPanel panel_top;
    private javax.swing.JPanel pannel_bottom;
    private javax.swing.JLabel question_t;
    private javax.swing.JLabel text_center;
    private javax.swing.JLabel text_left;
    private javax.swing.JLabel text_leftb;
    private javax.swing.JLabel text_right;
    private javax.swing.JLabel text_rightb;
    // End of variables declaration                   
}
