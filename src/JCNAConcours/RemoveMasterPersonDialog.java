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

import java.util.Comparator;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;


import ca.odell.glazedlists.swing.DefaultEventListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import ca.odell.glazedlists.swing.DefaultEventComboBoxModel;

import static JCNAConcours.AddConcoursEntryDialog.okDialog;
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.event.ItemEvent;
import java.sql.Connection;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.JOptionPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.MasterJaguar;
import us.efsowell.concours.lib.MasterJaguars;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.MasterPersonnel;
import us.efsowell.concours.lib.MyJavaUtils;

/**
 *
 * @author Ed Sowell
 */
public class RemoveMasterPersonDialog extends javax.swing.JDialog {

    private static class MemberInfoFormat extends Format {
		@Override
		public StringBuffer format(Object obj, StringBuffer toAppendTo,
				FieldPosition pos) {
			if (obj != null)
				toAppendTo.append(((MasterPersonExt) obj).getUniqueName());
			return toAppendTo;
		}

		@Override
		public Object parseObject(String source, ParsePosition pos) {
			return MasterListRepository.getInstance().getMemberInfo(
					source.substring(pos.getIndex()));
		}
	}
    
        LoadSQLiteConcoursDatabase loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only

        Concours theConcours;
        boolean systemexitwhenclosed;
        Connection theDBConnection;
        boolean boolAddedMPToBeRemoved;
        List<MasterPersonExt> masterPersonsToBeRemovedList;
        List<MasterPersonExt> nonConcoursMasterPersonsList;
        List<MasterPersonExt> concoursMasterPersonsList;
        MyJavaUtils utils = new MyJavaUtils();
        

//<editor-fold defaultstate="collapsed" desc="comment">
        
        MasterPersonnel theMasterPersonnel;
        int MAX_FIRST_NAME_EXTENSION = 3; // used in construction og unique name NOW IN CONCOURS
        
        /**
         * Constructor
         * Creates new form RemovetMasterPersonDialog
         */
        
//</editor-fold>
public RemoveMasterPersonDialog(java.awt.Frame parent, boolean modal, Connection aConnection, Concours aConcours, boolean aSystemexitwhenclosed) {
        super(parent, modal);
        theConcours = aConcours;
        theDBConnection = theConcours.GetConnection();
        theConcours.GetLogger().info("Starting Remove Master Person");
        theMasterPersonnel = theConcours.GetMasterPersonnelObject();
        List<MasterPersonExt> allMasterPersonnel;
        // work with a copy so as not to alter the real one
        nonConcoursMasterPersonsList = new ArrayList<MasterPersonExt>(aConcours.GetMasterPersonnelObject().GetMasterPersonnelList()); // temporialy it's all of them
        concoursMasterPersonsList  = new ArrayList<>();
        for(ConcoursPerson cp : aConcours.GetConcoursPersonnelObject().GetConcoursPersonnelList()){
            String un = cp.GetUniqueName();
            MasterPersonExt mp = aConcours.GetConcoursMasterPersonnelObject().GetMasterPerson(un);
            concoursMasterPersonsList.add(mp);
        }
        nonConcoursMasterPersonsList.removeAll(concoursMasterPersonsList);// Now it's just those not involved as Judge or Owner in current Concours
         
        initComponents();
    }

    
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        // Custom code to help GUI Builder do the GlazedList stuff
        // Note that GUIBuilder insists on putting in the "cboAvailableMPs ="
        cboAvailableMPs = new javax.swing.JComboBox();// GUI builder will insert the preceding left side
        // Initialize eventListOfAvaiableMPs to nonConcoursMasterPersonsList from repository
        eventListOfAvaiableMPs = GlazedLists.eventList(nonConcoursMasterPersonsList);
        // Note that sortedAvailableMPs will automatically update when the source, eventListOfAvaiableMPs, is changed
        EventList<MasterPersonExt> sortedAvailableMPs = new SortedList<>(eventListOfAvaiableMPs, MasterPersonExt.Comparators.UNIQUENAME);

        // custom filterator
        TextFilterator<MasterPersonExt> textFilterator = GlazedLists.textFilterator(MasterPersonExt.class, "uniqueName");
        // set Autocompletion support
        AutoCompleteSupport support = AutoCompleteSupport.install(this.cboAvailableMPs, sortedAvailableMPs, textFilterator, new MemberInfoFormat());
        support.setStrict(true);

        ;
        btnAddToRemoveList = new javax.swing.JButton();
        btnRemoveAll = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstBoxRemoveList = new javax.swing.JList();
        jLabel12 = new javax.swing.JLabel();
        btnAddBack = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Remove Master Persons");

        jLabel4.setText("Master Person list");

        cboAvailableMPs.setEditable(true);
        cboAvailableMPs.setToolTipText("Select Master Person to be removed from Master Personnel list. Note: Master Persons who are active in th current Concours have been omitted since they cannot be removed.");
        cboAvailableMPs.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboAvailableMPsItemStateChanged(evt);
            }
        });
        cboAvailableMPs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboAvailableMPsActionPerformed(evt);
            }
        });

        btnAddToRemoveList.setText("Add to remove list");
        btnAddToRemoveList.setToolTipText("Puts selected Master Person  into the Remove list");
        btnAddToRemoveList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddToRemoveListActionPerformed(evt);
            }
        });

        btnRemoveAll.setText("Remove all");
        btnRemoveAll.setToolTipText("Removes all Master Persons in Remove list & retuns to the main ConcoursBuilder dialog.");
        btnRemoveAll.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                btnRemoveAllMouseDragged(evt);
            }
        });
        btnRemoveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveAllActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.setToolTipText("Returns to ConcoursBuilder main dialog without  removal of any Master Persons");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        // Initialize eventListOfToBeRemovedMPs as an empty BasicEventList of MasterPersonExt
        eventListOfToBeRemovedMPs = new BasicEventList<>();
        // Set the Model for  JList lstBoxSelected
        DefaultEventListModel lstBoxRemoveListModel = new DefaultEventListModel(eventListOfToBeRemovedMPs);
        lstBoxRemoveList.setModel(lstBoxRemoveListModel);

        jScrollPane1.setViewportView(lstBoxRemoveList);

        jLabel12.setText("Remove list");

        btnAddBack.setText("Add back");
        btnAddBack.setToolTipText("Moves selection  on Remove list back to Master Persons list.");
        btnAddBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddBackActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(cboAvailableMPs, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAddToRemoveList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAddBack, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(108, 108, 108)
                        .addComponent(btnRemoveAll)
                        .addGap(98, 98, 98)
                        .addComponent(btnCancel)))
                .addContainerGap(58, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboAvailableMPs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAddToRemoveList))
                .addGap(19, 19, 19)
                .addComponent(jLabel12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(59, 59, 59)
                        .addComponent(btnAddBack)))
                .addGap(53, 53, 53)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnRemoveAll))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboAvailableMPsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboAvailableMPsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cboAvailableMPsActionPerformed

    private void btnAddToRemoveListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddToRemoveListActionPerformed
        //
        // Here we just add the selected Master Person to masterPersonsToBeRemovedList and
        // remove it from the cbo
        //
        MasterPersonExt mp = (MasterPersonExt) cboAvailableMPs.getSelectedItem();
        if(mp != null) {
            eventListOfAvaiableMPs.remove(mp);
            eventListOfToBeRemovedMPs.add(mp);
            //btnAdd.setEnabled(false);
        }

    }//GEN-LAST:event_btnAddToRemoveListActionPerformed

    private void btnRemoveAllMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnRemoveAllMouseDragged
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoveAllMouseDragged

    private void btnRemoveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveAllActionPerformed
        int response;
        if(eventListOfToBeRemovedMPs.isEmpty()){
            //okDialog("The list of Master Person to be removed is empty.");
            response = yesNoDialog("The list of Master Person to be removed is empty. Are you sure you want to return to the main dialog?");
            if(response == JOptionPane.YES_OPTION) {
                    return; // go back to main dialog
            } else {
                // return to RemoveMasterPersonDialog
            }
        } else {
            response = yesNoDialog("Are you sure you want to remove these " + eventListOfToBeRemovedMPs.size() + " persons from th Master Persons list?");
            if(response == JOptionPane.NO_OPTION) {
                    // return to  RemoveMasterPersonDialog
            } else{
                // Do the removes
                List<MasterPersonExt> toBeRemoved = new ArrayList<>();
                for (Iterator<MasterPersonExt> it = eventListOfToBeRemovedMPs.iterator(); it.hasNext();) {
                    MasterPersonExt mp = it.next();
                    toBeRemoved.add(mp);
                }
                theMasterPersonnel.RemoveFromMasterPersonnel(toBeRemoved);
                // have to remove from database too
                int numRemoved = loadSQLiteConcoursDatabase.UpdateRemoveMasterPersonList(theDBConnection, toBeRemoved);
                String msg = "Note: " + numRemoved + " Master Persons have been removed from the Master Personnel table in the database for the current Concours.\n";
                msg = msg + "If you want these removals to be reflected in later concourses you must execute the Save Active Base Database command on the File menu.";
                okDialog(msg);
                if(!systemexitwhenclosed){
                    this.setVisible(false);
                    this.dispose();
                } else {
                    System.exit(0);
                }
            }
        }
        
    }//GEN-LAST:event_btnRemoveAllActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        this.setVisible(false);
        this.dispose();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void cboAvailableMPsItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboAvailableMPsItemStateChanged
        if(evt.getStateChange() == ItemEvent.SELECTED){ // Without this check the action will take place when item is selected and unselected... twice
            this.btnAddToRemoveList.setEnabled(true);
        }
    }//GEN-LAST:event_cboAvailableMPsItemStateChanged

    private void btnAddBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddBackActionPerformed
        MasterPersonExt mp =  (MasterPersonExt)lstBoxRemoveList.getSelectedValue();
        if(mp != null) {
            eventListOfToBeRemovedMPs.remove(mp);
            eventListOfAvaiableMPs.add(mp);
            //btnRemove.setEnabled(false);
        }
    }//GEN-LAST:event_btnAddBackActionPerformed


    
    private boolean masterJagListContains(List<MasterJaguar> aList, MasterJaguar aMasterJaguar){
        boolean result = false;
        for(MasterJaguar mj : aList){
            if(mj.getUniqueDesc().equals(aMasterJaguar.getUniqueDesc())){
                result = true;
                break;
            }
        }
        return result;
    }
    
    /*
 
    */
   
    private boolean masterJagListRemove(List<MasterJaguar> aList, MasterJaguar aMasterJaguar) {
        boolean result = false;
        Iterator<MasterJaguar> mjIt = aList.iterator();
        while (mjIt.hasNext()) {
            MasterJaguar mj = mjIt.next();
            if (mj.getUniqueDesc().equals(aMasterJaguar.getUniqueDesc())) {
                mjIt.remove();
                result = true;
             }
        }   
        return result;
    }   
    private void clearAllFields(){
    
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
            java.util.logging.Logger.getLogger(RemoveMasterPersonDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RemoveMasterPersonDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RemoveMasterPersonDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RemoveMasterPersonDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RemoveMasterPersonDialog2 dialog = new RemoveMasterPersonDialog2(new javax.swing.JFrame(), true, aConnection, aConcours,  aSystemexitwhenclosed);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
        //</editor-fold>

        /* Create and display the dialog */
        /*
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                RemoveMasterPersonDialog2 dialog = new RemoveMasterPersonDialog2(new javax.swing.JFrame(), true, aConnection, aConcours,  aSystemexitwhenclosed);
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
    private javax.swing.JButton btnAddBack;
    private javax.swing.JButton btnAddToRemoveList;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnRemoveAll;
    // Custom code... pre-declaration section
    //private EventList<MasterPersonExt> allMasterPersons; // repository of MasterPersons
    private EventList<MasterPersonExt> eventListOfAvaiableMPs;
    private EventList<MasterPersonExt> eventListOfToBeRemovedMPs; // EventList to be associated with JList lstBoxRemoveList
    // End Custom  pre-declaration section
    private javax.swing.JComboBox cboAvailableMPs;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList lstBoxRemoveList;
    // End of variables declaration//GEN-END:variables
}
