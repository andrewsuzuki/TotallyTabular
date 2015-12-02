package imp.gui;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import imp.util.Gtab;

import javax.swing.JDialog;
import javax.swing.JScrollBar;

public class GtabDialog extends JDialog {
	
	/**
	 * The guitar tab we are displaying
	 */
	private Gtab gtab;
	
	/**
	 * GUI-related instances
	 */
	private javax.swing.JPanel dialogPanel;
	private javax.swing.JLabel mainLabel;
	private javax.swing.JTextPane tabText;
	private javax.swing.JScrollPane scrollPane;
	
	/**
	 * Class constructor
	 * @param parent
	 * @param modality
	 */
    public GtabDialog(java.awt.Frame parent, boolean modality) {
        super(parent, modality);
        initComponents();
    }
    
    /**
     * Change dialog contents to reflect a new guitar tab
     * @param gtab
     */
    public void setGtab(Gtab gtab) {
    	this.gtab = gtab;
    	// TODO handle rendering given width, etc
    	String rendered = gtab.render(10);
    	this.tabText.setText(rendered);
    	this.mainLabel.setText("Six-string guitar tuned in standard with " + this.gtab.getFrets() + " frets");
    }
    
    public void reRender(int width) {
    	int tabWidth = width < 300 ? width / 25 : width / 20;
    	String rendered = gtab.render(tabWidth);
    	this.tabText.setText(rendered);
    }
    
    /**
     * Initialize GUI components
     */
    private void initComponents() {
    	java.awt.GridBagConstraints gridBagConstraints;
    	
    	dialogPanel = new javax.swing.JPanel();
    	mainLabel = new javax.swing.JLabel();
    	tabText = new javax.swing.JTextPane();
    	scrollPane = new javax.swing.JScrollPane(tabText);
    	
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
        
        dialogPanel.addComponentListener(new ComponentListener() {
        	@Override
            public void componentResized(ComponentEvent e) {
            	int width = e.getComponent().getWidth();
            	reRender(width);
            }

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
        });
        
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
        dialogPanel.add(scrollPane, gridBagConstraints);
        
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
