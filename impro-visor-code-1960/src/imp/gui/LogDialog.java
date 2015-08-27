/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *

 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.gui;

/**
 *
 * @author  keller
 */
public class LogDialog
        extends javax.swing.JDialog
{
/** Creates new form ErrorDialog */
public LogDialog(boolean modal)
  {
  initComponents();
  getRootPane().setDefaultButton(closeLogDialogBtn);
  }


/** This method is called from within the constructor to
 * initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is
 * always regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        logScrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        closeLogDialogBtn = new javax.swing.JButton();
        clearLogDialogBtn = new javax.swing.JButton();

        setTitle("Log");
        getContentPane().setLayout(new java.awt.GridBagLayout());

        logScrollPane.setMinimumSize(new java.awt.Dimension(700, 600));
        logScrollPane.setPreferredSize(new java.awt.Dimension(700, 600));

        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logScrollPane.setViewportView(logTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(logScrollPane, gridBagConstraints);

        closeLogDialogBtn.setBackground(new java.awt.Color(0, 255, 0));
        closeLogDialogBtn.setText("Close");
        closeLogDialogBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        closeLogDialogBtn.setMinimumSize(new java.awt.Dimension(400, 20));
        closeLogDialogBtn.setOpaque(true);
        closeLogDialogBtn.setPreferredSize(new java.awt.Dimension(400, 20));
        closeLogDialogBtn.setSelected(true);
        closeLogDialogBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeLogButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 20, 5);
        getContentPane().add(closeLogDialogBtn, gridBagConstraints);

        clearLogDialogBtn.setBackground(new java.awt.Color(255, 204, 51));
        clearLogDialogBtn.setText("Clear");
        clearLogDialogBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        clearLogDialogBtn.setMinimumSize(new java.awt.Dimension(400, 20));
        clearLogDialogBtn.setOpaque(true);
        clearLogDialogBtn.setPreferredSize(new java.awt.Dimension(400, 20));
        clearLogDialogBtn.setSelected(true);
        clearLogDialogBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearLogDialogBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 20, 5);
        getContentPane().add(clearLogDialogBtn, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeLogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeLogButtonActionPerformed
  setVisible(false);
}//GEN-LAST:event_closeLogButtonActionPerformed

private void clearLogDialogBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearLogDialogBtnActionPerformed
    logTextArea.setText("");
}//GEN-LAST:event_clearLogDialogBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton clearLogDialogBtn;
    private javax.swing.JButton closeLogDialogBtn;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextArea logTextArea;
    // End of variables declaration//GEN-END:variables



public void append(String string)
{
    logTextArea.append(string);
}

/**
 * @param args the command line arguments
 */
public javax.swing.JButton getButton()
  {
  return closeLogDialogBtn;
  }

;
}
