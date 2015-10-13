package imp.gui;

import imp.util.Gtab;

import javax.swing.JDialog;

public class GtabDialog extends JDialog {
	
	private Gtab gtab;
	
	private javax.swing.JPanel dialogPanel;
	private javax.swing.JLabel mainLabel;
	private javax.swing.JTextPane tabText;
	
	/** Creates new form GtabDialog */
    public GtabDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    
    /**
     * Display a gtab
     */
    public void setGtab(Gtab gtab) {
    	this.gtab = gtab;
    	// TODO handle rendering given width, etc
    	String rendered = gtab.render(40);
    	this.tabText.setText(rendered);
    	this.mainLabel.setText("Six-string guitar tuned in standard with " + this.gtab.getFrets() + " frets");
    }
    
    private void initComponents() {
    	java.awt.GridBagConstraints gridBagConstraints;
    	
    	dialogPanel = new javax.swing.JPanel();
    	mainLabel = new javax.swing.JLabel();
    	tabText = new javax.swing.JTextPane();
    	
    	setTitle("Guitar Tab Viewer\n");
        setFocusCycleRoot(false);
        setName("gtabDialog");
        getContentPane().setLayout(new java.awt.GridBagLayout());
    	
    	// Configure dialog panel
    	dialogPanel.setBackground(new java.awt.Color(255, 255, 235));
        dialogPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        dialogPanel.setMinimumSize(new java.awt.Dimension(500, 900));
        dialogPanel.setPreferredSize(new java.awt.Dimension(500, 900));
        dialogPanel.setLayout(new java.awt.GridBagLayout());
        
        // Make label
        mainLabel.setFont(new java.awt.Font("Dialog", 3, 14)); // NOI18N
        mainLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        mainLabel.setMaximumSize(new java.awt.Dimension(400, 100));
        mainLabel.setMinimumSize(new java.awt.Dimension(400, 15));
        mainLabel.setPreferredSize(new java.awt.Dimension(400, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        dialogPanel.add(mainLabel, gridBagConstraints);
    	
        // Configure the text pane (tab view box)
    	tabText.setEditable(false);
        tabText.setBackground(new java.awt.Color(255, 255, 235));
        tabText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tabText.setFont(new java.awt.Font("Courier New", 1, 14));
        tabText.setMinimumSize(new java.awt.Dimension(400, 350));
        tabText.setPreferredSize(new java.awt.Dimension(400, 350));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        dialogPanel.add(tabText, gridBagConstraints);
        
        // Add constraints for actual dialog
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(dialogPanel, gridBagConstraints);
        
        // Finally, pack everything up
        pack();
    }
}