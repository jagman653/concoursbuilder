/* 
 * Copyright (C) 2017 Edward F Sowell
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package JCNAConcours;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;

/**
 *
 * @author Ed Sowell
 */
public class RemoveCustomJudgeTeamDialog extends javax.swing.JDialog {
    Concours theConcours;
    //ConcoursPersonnel theConcoursePersonnel;
    //Judges theConcoursJudges;
    List<Judge> theJudgeList;
    ArrayList<ConcoursClass> theClassesWithPreassignedJudges;
    boolean systemexitwhenclosed;
    Connection theDBConnection;
    LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase;

    /**
     * Creates new form RemoveCustomJudgeTeamDialog
     */
    public RemoveCustomJudgeTeamDialog(java.awt.Frame parent, boolean modal, Concours aConcours) {
        super(parent, modal);
        initComponents();
        theConcours = aConcours;
        theDBConnection = aConcours.GetConnection();
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();
        
        Statement stat = null;
        try {
            stat = theDBConnection.createStatement();
        } catch (SQLException ex) {
            Logger.getLogger(EditCustomJudgeTeamDialog.class.getName()).log(Level.SEVERE, null, ex);
        }

        theClassesWithPreassignedJudges = getClassesWithPreassignedJudges(theConcours);
        cboClasses.setModel(new javax.swing.DefaultComboBoxModel(theClassesWithPreassignedJudges.toArray()));
        
        // Populate the Judge checkbox list with the first Concours class preassigns
        
        // Note: Menu item is not enabled unless one or more CncoursClasses have preassigned judges
       ConcoursClass cc = (ConcoursClass)cboClasses.getSelectedItem();
        PopulateTextArea(cc);
        
        try {
            stat.close();
        } catch (SQLException ex) {
            Logger.getLogger(EditCustomJudgeTeamDialog.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<ConcoursClass> getClassesWithPreassignedJudges(Concours aConcours){
        ArrayList<ConcoursClass> classesWithPreassignedJudges = new ArrayList<>();
        for(ConcoursClass cc : aConcours.GetConcoursClasses()){
            if(!cc.GetClassPreassignedJudgeNameList().isEmpty()){
                classesWithPreassignedJudges.add(cc);
            }
        }
        return classesWithPreassignedJudges;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlContent = new javax.swing.JPanel();
        cboClasses = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea = new javax.swing.JTextArea();
        btnSave = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Remove custom judging team");

        cboClasses.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboClasses.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboClassesItemStateChanged(evt);
            }
        });

        jLabel1.setText("Select class");

        txtArea.setColumns(20);
        txtArea.setRows(5);
        jScrollPane1.setViewportView(txtArea);

        javax.swing.GroupLayout pnlContentLayout = new javax.swing.GroupLayout(pnlContent);
        pnlContent.setLayout(pnlContentLayout);
        pnlContentLayout.setHorizontalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlContentLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(46, Short.MAX_VALUE))
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(jLabel1)
                .addGap(29, 29, 29)
                .addComponent(cboClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlContentLayout.setVerticalGroup(
            pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlContentLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(pnlContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );

        btnSave.setText("Remove team");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished");
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnSave)
                        .addGap(27, 27, 27)
                        .addComponent(btnFinished)
                        .addGap(119, 119, 119)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(pnlContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSave)
                    .addComponent(btnFinished))
                .addGap(87, 87, 87))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboClassesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboClassesItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){
           ConcoursClass selectedconcoursclass = (ConcoursClass) cboClasses.getSelectedItem(); 
           PopulateTextArea(selectedconcoursclass);
        }
    }//GEN-LAST:event_cboClassesItemStateChanged
    private void PopulateTextArea(ConcoursClass selectedconcoursclass){
           String report = "Preassigned Judges for Concours Class " + selectedconcoursclass.GetClassName()+ ":\n";
           for(String j : selectedconcoursclass.GetClassPreassignedJudgeNameList()){
               report = report  + j + "\n";
           }
           txtArea.setText(report);
    }
    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // Removed the preassigned judges for selected Concours Class from memory & database
        ConcoursClass cc = (ConcoursClass) cboClasses.getSelectedItem();
        clearClassPreassignedJugdes(cc);        
    }//GEN-LAST:event_btnSaveActionPerformed

    /*
       Helper function so it can be used in when a Judge who is on a preassigned team is removed
    */
    public void clearClassPreassignedJugdes(ConcoursClass aConcoursClass){
        aConcoursClass.RemoveAllPreassignedJudges(theDBConnection); // Clear out from memory & database
        theConcours.SetJudgeAssignmentCurrent(false); 
        try { 
            loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
        } catch (SQLException ex) {
            String msg = "SQLException in clearClassPreassignedJugdes";
            okDialog(msg);
           theConcours.GetLogger().log(Level.SEVERE, msg, ex);
        }
        //theConcours.GetLogger().info("Calling ClearJudgeAssignmentsTables() in RemoveCustomJudgingTeamDialog");
        //loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
        //String report = "All preassigned Judges for Concours Class " + aConcoursClass.GetClassName() + " have been removed.\n";
        //theConcours.GetLogger().info(report);
        //okDialog(report);
    }
    
    private void btnFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishedActionPerformed
        theConcours.SetPreassignedJudgesFlag(theConcours.GetConcoursClassesObject().preassignedJudgeListsExists());
        this.setVisible(false);
        this.dispose();
         // This is not wanted when the dialog is invoked by ConcourseGUI, but must be done when run by main()
         // or the process will continu to run!
        if(systemexitwhenclosed){
            System.exit(0);
        }
    }//GEN-LAST:event_btnFinishedActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
    /*    try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RemoveCustomJudgeTeamDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RemoveCustomJudgeTeamDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RemoveCustomJudgeTeamDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RemoveCustomJudgeTeamDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //Create and display the dialog 
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RemoveCustomJudgeTeamDialog dialog = new RemoveCustomJudgeTeamDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
            */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnFinished;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox cboClasses;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JTextArea txtArea;
    // End of variables declaration//GEN-END:variables
}
