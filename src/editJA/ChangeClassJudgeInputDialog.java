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
package editJA;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursClass;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.TimeslotAssignment;


/**
 *
 * @author Ed Sowell
 */
public class ChangeClassJudgeInputDialog extends javax.swing.JDialog {
    
    Concours theConcours;

   // private ArrayList<TimeslotAssignment> lstSelectedClassEntriesTimeslots;
    /**
     * Creates new form ChangeClassJudgeInputDialog
     */
    
    public ChangeClassJudgeInputDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }
    // Custom constructor to pass theConcours to this dialog
    public ChangeClassJudgeInputDialog(java.awt.Frame parent, boolean modal, Concours aConcours) {
        super(parent, modal);
        //initComponents();
        myInitComponents();
        theConcours = aConcours;
        cboReplacementJudge.setEnabled(false);

  //      lstSelectedClassEntriesTimeslots = new ArrayList<>(); //Loaded when user selects a Class
        
        cboClasses.setModel(new javax.swing.DefaultComboBoxModel(getClasses(aConcours).toArray()));
        cboClasses.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboClassesItemStateChanged(evt);
            }
        });
        cboClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboClassesActionPerformed(evt);
            }
        });
     //    cboReplacementJudge.setModel(new javax.swing.DefaultComboBoxModel(getJudges(aConcours).toArray()));
        cboJudge.setEnabled(true);
        UpdateCboJudge();
        UpdateCboAvailableJudges();

   }

    private ArrayList<ConcoursClass> getClasses(Concours aConcours){
        return aConcours.GetConcoursClasses();
        
    }
    private int getJudgeNode(Concours theConcours, int i){
        return JudgeAssignGUI.theConcours.GetConcoursJudges().get(i).GetNode();
    }
    private List<Judge> getJudges(Concours theConcours){
        return JudgeAssignGUI.theConcours.GetConcoursJudges();
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        cboClasses = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboJudge = new javax.swing.JComboBox();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboReplacementJudge = new javax.swing.JComboBox();

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        cboClasses.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboClasses.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboClassesItemStateChanged(evt);
            }
        });
        cboClasses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboClassesActionPerformed(evt);
            }
        });

        jLabel1.setText("Class");

        jLabel2.setText("Judge to be changed");

        cboJudge.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJudge.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJudgeItemStateChanged(evt);
            }
        });
        cboJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJudgeActionPerformed(evt);
            }
        });

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel3.setText("Replace with judge");

        cboReplacementJudge.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cboClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOK)
                        .addGap(64, 64, 64)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(cboReplacementJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboJudge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboReplacementJudge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void myInitComponents() {

        //jComboBox1 = new javax.swing.JComboBox();
        cboClasses = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboJudge = new javax.swing.JComboBox();
        btnOK = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        cboReplacementJudge = new javax.swing.JComboBox();

        //jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);


        jLabel1.setText("Class");

        jLabel2.setText("Judge to be changed");

        /*cboJudge.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboJudge.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJudgeItemStateChanged(evt);
            }
        });
        cboJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJudgeActionPerformed(evt);
            }
        });
        */

        btnOK.setText("OK");
        btnOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOKActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel3.setText("Replace with judge");

 //       cboReplacementJudge.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cboClasses, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(47, 47, 47)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnOK)
                        .addGap(64, 64, 64)
                        .addComponent(btnCancel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cboJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jLabel3)
                            .addComponent(cboReplacementJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(68, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboClasses, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboJudge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboReplacementJudge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 173, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnOK)
                    .addComponent(btnCancel))
                .addGap(28, 28, 28))
        );

        pack();
    }// </editor-fold>                        
    
    
    
    private void cboClassesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboClassesActionPerformed
    /*               ConcoursClass selectedConcoursClass;
	selectedConcoursClass = (ConcoursClass) ChangeClassJudgeInputDialog.this.cboClasses.getSelectedItem();
        cboJudge.setModel(new javax.swing.DefaultComboBoxModel(selectedConcoursClass.GetClassJudgeObjects().toArray()));   
     */   
    }//GEN-LAST:event_cboClassesActionPerformed

    private void cboJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboJudgeActionPerformed
       /* if (evt.getStateChange() == 1){
            
        }
        */
    }//GEN-LAST:event_cboJudgeActionPerformed

    private void btnOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOKActionPerformed
        
        String res;
        ConcoursClass theClass;
        Judge theCurrentJudge;
        Judge theNewJudge;
        theClass = (ConcoursClass)cboClasses.getSelectedItem();
        //System.out.println("Class: " + theClass + " Number of Class Judges: " + theClass.GetClassJudgeIndices().size());
        //theCurrentJudge =(Judge)cboJudge.getSelectedItem();
        theCurrentJudge = theConcours.GetConcoursJudge((String)cboJudge.getSelectedItem());
        
        //theNewJudge =(Judge)this.cboReplacementJudge.getSelectedItem();
        theNewJudge = theConcours.GetConcoursJudge((String)cboReplacementJudge.getSelectedItem());
        
        // Change TimeSlotAssignemnts, ConcoursClass, and database accordingly
//        res = JudgeAssignGUI.theConcours.ChangeClassJudge(JudgeAssignGUI.theConcours.GetTimeslotAssignments(),  theClass, theCurrentJudge, theNewJudge);
        res = JudgeAssignGUI.theConcours.ChangeClassJudge(theClass, theCurrentJudge, theNewJudge);
        if("".equals(res)){
            JudgeAssignGUI.theConcours.GetLogger().info("ERROR occured in ChangeClassJudge() to ChangeClassJudgeInputDialog");
            okDialog("ERROR: Failed ChangeClassJudge() to ChangeClassJudgeInputDialog");
            System.exit(-1);
        } else{
            okDialog(res);
            JudgeAssignGUI.theConcours.GetLogger().info(res);        
        }
        JudgeAssignGUI.textarea.append(res);
        JudgeAssignGUI.theConcours.GetLogger().info(res);
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnOKActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        JudgeAssignGUI.theConcours.GetLogger().info("User cancelled Change Class Judge");
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cboClassesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboClassesItemStateChanged
         if (evt.getStateChange() == ItemEvent.SELECTED) {
             UpdateCboJudge();
         }
        
                }//GEN-LAST:event_cboClassesItemStateChanged

    private void UpdateCboJudge(){
        ConcoursClass theSelectedClass;
        theSelectedClass =(ConcoursClass) cboClasses.getSelectedItem();
        List<String> judgeUniqueNameList = theSelectedClass.GetClassJudgeUniqueNames();
        cboJudge.setModel(new javax.swing.DefaultComboBoxModel(judgeUniqueNameList.toArray()));
        cboJudge.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboJudgeItemStateChanged(evt);
            }
        });
        cboJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboJudgeActionPerformed(evt);
            }
        });
        
        cboJudge.setEnabled(true);
        UpdateCboAvailableJudges();
    }
    

    private void cboJudgeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboJudgeItemStateChanged
         Judge  theSelectedJudge;
         if (evt.getStateChange() == ItemEvent.SELECTED) {
           theSelectedJudge = theConcours.GetConcoursJudge((String)cboJudge.getSelectedItem());
           //System.out.println("The selected Judge: " + theSelectedJudge );
           //cboReplacementJudge.setEnabled(true);
             
           UpdateCboAvailableJudges()             ;
         }
         
    }//GEN-LAST:event_cboJudgeItemStateChanged

    private void UpdateCboAvailableJudges(){
            ConcoursClass  theSelectedClass;
            List<Judge> ClassJudgeObjects;
            List<Judge> JudgesAvailableInThisClassTimeslots;
            //List<TimeslotAssignment> classTimeslotsUsed; 
            List<Entry> ClassEntryObjects;
            //List<Integer> ClassEntryIndicies;
            Integer classIndex; 
            Integer firstClassEntryIndex;
            List<Judge> JudgesAvailableInAllClassTimeslots = new ArrayList<>(JudgeAssignGUI.theConcours.GetConcoursJudges()); //  local COPY ofJudge Objects
            //List<Judge> JudgesAvailableInAllClassTimeslots = new ArrayList<>();
           // if (evt.getStateChange() == 1) {
            theSelectedClass = (ConcoursClass)cboClasses.getSelectedItem();
            classIndex = theSelectedClass.GetClassNode();
            //System.out.println("The selected Class: " + theSelectedClass );

            firstClassEntryIndex = theSelectedClass.GetClassEntryIndices().get(0);// same for all entries in Class so just use the 0th one.
            ClassEntryObjects = theSelectedClass.GetClassEntryObjects();
            ClassJudgeObjects = new ArrayList<>(JudgeAssignGUI.theConcours.GetJudgeObjectsForEntry(firstClassEntryIndex));
            /*for(Judge jo : ClassJudgeObjects){
                System.out.println("Class index: " + classIndex + " Judge index: " + jo.GetNode() );
            }
            */
            for(Entry eo : ClassEntryObjects){
                if(!eo.GetClassName().equals("DISP")){
                Integer tsID = eo.GetTimeslotIndex();
                    TimeslotAssignment ts = JudgeAssignGUI.theConcours.GetTimeslotObject(tsID);
                    JudgesAvailableInThisClassTimeslots = ts.GetAvailableJudgeObjects();
                    JudgesAvailableInAllClassTimeslots.retainAll(JudgesAvailableInThisClassTimeslots);
                }
            }
            theConcours.GetLogger().info("Judges available in timeslot:\n");
            for(Judge j : JudgesAvailableInAllClassTimeslots){
                theConcours.GetLogger().info("Judge:" + j);    
            }
            

            //cboJudge.setModel(new javax.swing.DefaultComboBoxModel(ClassJudgeObjects.toArray()));
            cboReplacementJudge.setEnabled(true);
            List<String> replacementJudgeUniqueNameList = new ArrayList<>();
            for(Judge j : JudgesAvailableInAllClassTimeslots){
                replacementJudgeUniqueNameList.add(j.getUniqueName());
            }
            
            cboReplacementJudge.setModel(new javax.swing.DefaultComboBoxModel(replacementJudgeUniqueNameList.toArray()));

      
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ChangeClassJudgeInputDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ChangeClassJudgeInputDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ChangeClassJudgeInputDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ChangeClassJudgeInputDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ChangeClassJudgeInputDialog dialog = new ChangeClassJudgeInputDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setEnabled(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOK;
    private javax.swing.JComboBox cboClasses;
    private javax.swing.JComboBox cboJudge;
    private javax.swing.JComboBox cboReplacementJudge;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
