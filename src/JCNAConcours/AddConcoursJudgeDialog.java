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
import static JCNAConcours.AddConcoursEntryDialog.yesNoDialog;
import static JCNAConcours.ConcoursGUI.theConcours;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.sql.Statement;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import us.efsowell.concours.lib.Concours;
import us.efsowell.concours.lib.ConcoursPerson;
import us.efsowell.concours.lib.ConcoursPersonnel;
import us.efsowell.concours.lib.Entry;
import us.efsowell.concours.lib.JCNAClass;
import us.efsowell.concours.lib.JCNAClasses;
import us.efsowell.concours.lib.Judge;
import us.efsowell.concours.lib.Judges;
import us.efsowell.concours.lib.MasterPersonExt;
import us.efsowell.concours.lib.LoadSQLiteConcoursDatabase;
import us.efsowell.concours.lib.Owner;
import us.efsowell.concours.lib.SchedulingInterfaceJava;


/**
 *
 * @author Ed Sowell
 */
public class AddConcoursJudgeDialog extends javax.swing.JDialog {

    JPanel pnlJCNAClassGroups;
    JPanel pnlJCNAClasses;
    JScrollPane scrollPaneClasses;
    
    JCNAClass[] classMasterListArray;
    Concours theConcours;
    ConcoursPersonnel theConcoursePersonnel;
    Judges theConcoursJudges;
    List<JCNAClass> theJCNAclassList;
    JCNAClassesGroups theJCNAClassGroups;
    boolean systemexitwhenclosed;
    
    boolean changedSelections;
    boolean addedJudge;
    Connection theDBConnection;
    //Vector<Component> traversalOrder;
    //FocusTraversalPolicyConcoursBuilder ftp;


    private static class MemberInfoFormat extends Format {

        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo,
                FieldPosition pos) {
            if (obj != null) {
                toAppendTo.append(((MasterPersonExt) obj).getUniqueName());
            }
            return toAppendTo;
        }

        @Override
        public Object parseObject(String source, ParsePosition pos) {
            return MasterListRepository.getInstance().getMemberInfo(
                    source.substring(pos.getIndex()));
        }
    }

    /**
     * Creates new form AddConcoursJudgeDialog
     */
    public AddConcoursJudgeDialog(java.awt.Frame parent, boolean modal,  Connection aConnection, Concours aConcours, ConcoursPersonnel aConcoursPersonnel, MasterListRepository aRepository, JCNAClass[] aClassMasterList, boolean aSystemexitwhenclosed, boolean aAddJudge) {
        super(parent, modal);
        this.setTitle("Add Concours Judges");
        systemexitwhenclosed = aSystemexitwhenclosed;
        
        theJCNAClassGroups = new JCNAClassesGroups();
        classMasterListArray = aClassMasterList;
        theDBConnection =  aConnection;
        
        addedJudge = false;
        changedSelections = true;

        
        initComponents();
        //myInitComponents();
        theConcours = aConcours;
        theConcours.GetLogger().info("Starting AddConcoursJudge");

        PopulatePrefPanelAddConcoursJudgeDialog(theConcours);
        
        this.setSize(1600,800); /// 5/19/2017
        // custom filterator
        TextFilterator<MasterPersonExt> textFilterator = GlazedLists.textFilterator(
                MasterPersonExt.class, "uniqueName");

        theConcoursJudges = theConcours.GetConcoursJudgesObject();
        /*
         * install auto-completion
         */
        theConcoursePersonnel = aConcoursPersonnel;
        MasterPersonExt[] allMembers;
        allMembers = aRepository.getAllMembers();
        AutoCompleteSupport support = AutoCompleteSupport.install(
                this.cboUniqueName, GlazedLists.eventListOf(allMembers),
                textFilterator, new MemberInfoFormat());
        // and set to strict mode
        support.setStrict(true);

        /*
         * Based on the selected MasterPerson in  cboUniqueName, fill in the member attribute fields
         * in the dialog and get the jaguar stable for this member so it can be used to populate cboJaguars
         */
        UpdateMasterPersonAttributes();

    }
    
    //
    // PopulatePrefPanelAddConcoursJudgeDialog constructs the Judge preference panel.  Couldn't see a way to do this with the NetBeans GUI designer
    //
private void PopulatePrefPanelAddConcoursJudgeDialog(Concours aConcours)   {
        ArrayList<JCNAClass> jcnalasses = new ArrayList<>();
        String judgeassigngroupname;
        String classname;
        JCNAClassesGroup cg;
        
        /* 
         * iterate the list of JCNA Classes to set up the checkbox lists for Class judge assignment Groups and then 
         * for all classes. 
         *  
         * All of this is set into 2 panels in the dialog
         */
        
        // Set up Class Groups panel
        pnlJCNAClassGroups = new javax.swing.JPanel();
        pnlJCNAClassGroups.setBorder(javax.swing.BorderFactory.createTitledBorder("Select JCNA classes by groups"));
        javax.swing.BoxLayout pnlJCNAClassGroupsLayout;
        pnlJCNAClassGroupsLayout = new javax.swing.BoxLayout(pnlJCNAClassGroups, BoxLayout.PAGE_AXIS);
        pnlJCNAClassGroups.setLayout(pnlJCNAClassGroupsLayout);
        //pnlJCNAClassGroups.createToolTip().setTipText("Check groups of JCNA classes this Judge can Judge. Note: Each checked group the corresponding classe will be checked in panel to the right.");
        
        jcnalasses = aConcours.GetJCNAClasses().GetJCNAClasses();
        for (JCNAClass c : jcnalasses) {
            classname = c.getName();
            judgeassigngroupname = c.getJudgeAssignGroup();
            cg = theJCNAClassGroups.GetJCNAClassesGroup(judgeassigngroupname);
            if (cg == null) {
                cg = new JCNAClassesGroup(judgeassigngroupname);
                theJCNAClassGroups.AddClassesGroup(cg);
            }
            cg.AddClassName(classname);
        }
        // now populate the Class group scroll panel
        JCheckBox cb;
        //List<JCheckBox> cbList = new ArrayList<>();
       // pnlJCNAClassGroups.removeAll();  // clearing the checkboxes inserted with the GUI designer
        for (JCNAClassesGroup jcnaclassgroup : theJCNAClassGroups.GetClassesGroupList()) {
            cb = new JCheckBox(new JCNAClassesGroup.JCNAClassesGroupAction(jcnaclassgroup));
            cb.setName(jcnaclassgroup.GetGroupName());
            cb.setSelected(true); // Added 7/11/2017 so by default ALL class Groups are selected.
        //    cbList.add(cb);
            cb.addItemListener(new ItemListener() {
                /*
                 *  The following itemStateChanged listener transfers the Group selections to the list of all classes.
                 */
                @Override
                public void itemStateChanged(ItemEvent e) {
                    //String cbText;
                    String classname;
                    String cbName;
                    String[] groupclassnames;
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        JCheckBox cbe = (JCheckBox) e.getItem();
                        String groupname = cbe.getName();
                        groupclassnames = theJCNAClassGroups.GetJCNAClassesGroup(groupname).GetClassNames();
                        for (String groupclassname : groupclassnames) {
                            classname = groupclassname;
                            JPanel p = pnlJCNAClasses;
                            int count = p.getComponentCount();
                            JCheckBox cb;
                            // iterate over the JCNA Classes check boxes in pnlJCNAClasses
                            // and select those  that have been selected in group i
                           for (int j = 0; j < count; j++) {
                                //p.getClass();
                                cb = (JCheckBox) p.getComponent(j);
                                cbName = cb.getName();
                                if (cbName.equals(classname)) {
                                    //cbText = cb.getText();
                                    cb.setSelected(true);
                                }
                            }
                            
                        }
                    } else {
                        if (e.getStateChange() == ItemEvent.DESELECTED) {
                            JCheckBox cbe = (JCheckBox) e.getItem();
                            String groupname = cbe.getName();
                            groupclassnames = theJCNAClassGroups.GetJCNAClassesGroup(groupname).GetClassNames();
                            for (String groupclassname : groupclassnames) {
                                classname = groupclassname;
                                JPanel p = pnlJCNAClasses;
                                int count = p.getComponentCount();
                                JCheckBox cb;
                            // iterate over the JCNA Classes check boxes in pnlJCNAClasses
                                // and deselect those  that have been deselected in group i
                                for (int j = 0; j < count; j++) {
                                   cb = (JCheckBox) p.getComponent(j);
                                   if (cb.getName().equals(classname)) {
                                        //cbText = cb.getText();
                                        cb.setSelected(false);
                                    }
                                }
                            }
                        }
                    }
                }
            });
            pnlJCNAClassGroups.add(cb);
        }
        pnlJCNAClassGroups.revalidate();
        pnlJCNAClassGroups.repaint();
        
        // Set up Classes panel

        pnlJCNAClasses = new javax.swing.JPanel();
        pnlJCNAClasses.setBorder(javax.swing.BorderFactory.createTitledBorder("Select JCNA classes individually"));
        pnlJCNAClasses.setPreferredSize(new Dimension(600, 900));
        //
        //  Set up scrolling for the Class panel
        //
        scrollPaneClasses = new JScrollPane(pnlJCNAClasses, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //scrollPaneClasses.setMinimumSize(new Dimension(400, 300));
        //scrollPaneClasses.setMaximumSize(new Dimension(800, 400));
        scrollPaneClasses.setPreferredSize(new Dimension(600, 900));
        scrollPaneClasses.revalidate();
        scrollPaneClasses.repaint();
       
        javax.swing.BoxLayout pnlJCNAClassesLayout;
        pnlJCNAClassesLayout = new javax.swing.BoxLayout(pnlJCNAClasses, BoxLayout.PAGE_AXIS);
        pnlJCNAClasses.setLayout(pnlJCNAClassesLayout);
        pnlJCNAClasses.revalidate();
        pnlJCNAClasses.repaint();
        
        
        pnlJCNAClassGroups.setLayout(new BoxLayout(pnlJCNAClassGroups, BoxLayout.PAGE_AXIS)); 
        //pnlJCNAClassGroups.setVisible(true);
        
//       pnlPreferenceContent.add(pnlJCNAClassGroups, java.awt.BorderLayout.WEST);

        for (JCNAClass c : classMasterListArray) {
            classname = c.getName();
            cb = new JCheckBox(new JCNAClass.JCNAClassAction(c));
            cb.setName(classname);
            cb.setSelected(true); // Added 7/11/2017 so by default ALL classes are selected.
            pnlJCNAClasses.add(cb);
        }
       // pnlJCNAClasses.createToolTip().setTipText("");
        pnlJCNAClasses.revalidate();
        pnlJCNAClasses.repaint();
        


       pnlPreferenceContent.setPreferredSize(new Dimension(900, 320));
              
//        pnlPreferenceContent.setLayout(new java.awt.BorderLayout());
        javax.swing.BoxLayout pnlPreferenceContentLayout;
        pnlPreferenceContentLayout = new javax.swing.BoxLayout(pnlPreferenceContent, BoxLayout.LINE_AXIS);
        pnlPreferenceContent.setLayout(pnlPreferenceContentLayout);
        pnlPreferenceContent.add(pnlJCNAClassGroups);
        pnlPreferenceContent.add(scrollPaneClasses);
        pnlPreferenceContent.revalidate();
        pnlPreferenceContent.repaint();
        
        pnlPreferenceContent.setVisible(true);                                          

}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnAddJudge = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();
        pnlPreferenceContent = new javax.swing.JPanel();
        pnlPersonalInfoContent = new javax.swing.JPanel();
        txtLast = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPostalCode = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtCertYear = new javax.swing.JTextField();
        txtJCNA = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtPhoneCell = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPhoneHome = new javax.swing.JTextField();
        txtJudgeStatus = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtFirst = new javax.swing.JTextField();
        txtStreetAddress = new javax.swing.JTextField();
        txtState = new javax.swing.JTextField();
        txtCountry = new javax.swing.JTextField();
        txtPhoneWork = new javax.swing.JTextField();
        cboUniqueName = new javax.swing.JComboBox();
        txtClub = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBounds(new java.awt.Rectangle(0, 0, 1200, 540));
        setPreferredSize(new java.awt.Dimension(1136, 800));

        btnAddJudge.setText("Add");
        btnAddJudge.setToolTipText("Click to add the selected person to the list of Judges for this concours.\n Can be used repetedly, followed by clicking the Finished button.");
        btnAddJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddJudgeActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished");
        btnFinished.setToolTipText("Click after adding one or more Judges");
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
            }
        });

        pnlPreferenceContent.setBorder(javax.swing.BorderFactory.createTitledBorder("Select preferred judging classes"));
        pnlPreferenceContent.setToolTipText("Note that only the right panel selections count. The left panel is simply a quicker way of making right panel selections.");
        pnlPreferenceContent.setPreferredSize(new java.awt.Dimension(1500, 350));
        pnlPreferenceContent.setLayout(new javax.swing.BoxLayout(pnlPreferenceContent, javax.swing.BoxLayout.LINE_AXIS));

        pnlPersonalInfoContent.setBorder(javax.swing.BorderFactory.createTitledBorder("Judge Personal Data"));
        pnlPersonalInfoContent.setToolTipText("Data fields are for information only. Data can only be changed by editing the Master Person.");

        txtLast.setEditable(false);
        txtLast.setText("last");
        txtLast.setEnabled(false);
        txtLast.setFocusable(false);

        jLabel16.setText("Work phone");

        jLabel5.setText("Street address");

        txtPostalCode.setEditable(false);
        txtPostalCode.setText("unknown");
        txtPostalCode.setEnabled(false);
        txtPostalCode.setFocusable(false);

        jLabel14.setText("Year certified");

        txtCertYear.setEditable(false);
        txtCertYear.setText("unknown");
        txtCertYear.setEnabled(false);
        txtCertYear.setFocusable(false);

        txtJCNA.setEditable(false);
        txtJCNA.setText("unknown");
        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);

        jLabel6.setText("City");

        jLabel15.setText("Home phone");

        jLabel8.setText("Postal code");

        txtEmail.setEditable(false);
        txtEmail.setText("unknown");
        txtEmail.setEnabled(false);
        txtEmail.setFocusable(false);

        txtPhoneCell.setEditable(false);
        txtPhoneCell.setText("unknown");
        txtPhoneCell.setEnabled(false);
        txtPhoneCell.setFocusable(false);
        txtPhoneCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneCellActionPerformed(evt);
            }
        });

        jLabel2.setText("First");

        jLabel3.setText("Judge status");

        txtPhoneHome.setEditable(false);
        txtPhoneHome.setText("unknown");
        txtPhoneHome.setEnabled(false);
        txtPhoneHome.setFocusable(false);
        txtPhoneHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneHomeActionPerformed(evt);
            }
        });

        txtJudgeStatus.setEditable(false);
        txtJudgeStatus.setText("unknown");
        txtJudgeStatus.setEnabled(false);
        txtJudgeStatus.setFocusable(false);

        jLabel17.setText("Cell phone");

        jLabel4.setText("Last");

        txtCity.setEditable(false);
        txtCity.setText("unknown");
        txtCity.setEnabled(false);
        txtCity.setFocusable(false);

        jLabel7.setText("State");

        jLabel1.setText("Judge unique name");

        jLabel10.setText("JCNA #");

        jLabel9.setText("Country");

        txtFirst.setEditable(false);
        txtFirst.setText("first");
        txtFirst.setEnabled(false);
        txtFirst.setFocusable(false);

        txtStreetAddress.setEditable(false);
        txtStreetAddress.setText("unknown");
        txtStreetAddress.setEnabled(false);
        txtStreetAddress.setFocusable(false);

        txtState.setEditable(false);
        txtState.setText("unknown");
        txtState.setEnabled(false);
        txtState.setFocusable(false);

        txtCountry.setEditable(false);
        txtCountry.setText("unknown");
        txtCountry.setEnabled(false);
        txtCountry.setFocusable(false);

        txtPhoneWork.setEditable(false);
        txtPhoneWork.setText("unknown");
        txtPhoneWork.setEnabled(false);
        txtPhoneWork.setFocusable(false);
        txtPhoneWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneWorkActionPerformed(evt);
            }
        });

        cboUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cboUniqueName.setToolTipText("Start typing Last name for autocompletion.");
        cboUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboUniqueNameItemStateChanged(evt);
            }
        });
        cboUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUniqueNameActionPerformed(evt);
            }
        });

        txtClub.setEditable(false);
        txtClub.setText("unknown");
        txtClub.setEnabled(false);
        txtClub.setFocusable(false);

        jLabel11.setText("Club");

        jLabel18.setText("Email");

        javax.swing.GroupLayout pnlPersonalInfoContentLayout = new javax.swing.GroupLayout(pnlPersonalInfoContent);
        pnlPersonalInfoContent.setLayout(pnlPersonalInfoContentLayout);
        pnlPersonalInfoContentLayout.setHorizontalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(70, 70, 70)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPostalCode)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))))
                .addContainerGap(791, Short.MAX_VALUE))
        );
        pnlPersonalInfoContentLayout.setVerticalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPostalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(3, 3, 3)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(3, 3, 3)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnlPersonalInfoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(275, 275, 275))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(446, 446, 446)
                        .addComponent(btnAddJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFinished))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(pnlPreferenceContent, javax.swing.GroupLayout.PREFERRED_SIZE, 1463, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(pnlPersonalInfoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(pnlPreferenceContent, javax.swing.GroupLayout.PREFERRED_SIZE, 334, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddJudge)
                    .addComponent(btnFinished))
                .addContainerGap(37, Short.MAX_VALUE))
        );

        pnlPreferenceContent.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cboUniqueNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cboUniqueNameItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) { // Without this check the action will take place when item is selected and unselected... twice
            this.changedSelections = true;
            this.addedJudge = false;
            UpdateMasterPersonAttributes();
            this.btnAddJudge.setEnabled(true);
        }

    }//GEN-LAST:event_cboUniqueNameItemStateChanged

    private void UpdateMasterPersonAttributes() {
        // System.out.println("cboUniqueName itemStateChange");
        Integer intCertYear;
        String fn;
        String ln;
        MasterPersonExt selectedMasterPerson;
       // System.out.println("cboUniqueName ItemEvent.SELECTED ");
        selectedMasterPerson = (MasterPersonExt) AddConcoursJudgeDialog.this.cboUniqueName.getSelectedItem();

        if (selectedMasterPerson != null) {
                    //System.out.println("Selected '" + selectedMasterPerson.getFirstName() + " " + selectedMasterPerson.getLastName() + "'");
            // strTemp = selectedMasterPerson.getJcna().toString();
            txtJCNA.setText(selectedMasterPerson.getJcna().toString());
            txtClub.setText(selectedMasterPerson.getClub());
            fn = selectedMasterPerson.getFirstName();
            txtFirst.setText(fn);
            ln = selectedMasterPerson.getLastName();
            txtLast.setText(ln);

            txtJudgeStatus.setText(selectedMasterPerson.getJudgeStatus());
            intCertYear = selectedMasterPerson.getCertYear();

            txtCertYear.setText(intCertYear.toString());

            txtStreetAddress.setText(selectedMasterPerson.getAddressSreet());
            txtCity.setText(selectedMasterPerson.getCity());
            txtState.setText(selectedMasterPerson.getState());
            txtCountry.setText(selectedMasterPerson.getCountry());
            txtPostalCode.setText(selectedMasterPerson.getPostalCode());

            txtPhoneWork.setText(selectedMasterPerson.getPhoneWork());
            txtPhoneHome.setText(selectedMasterPerson.getPhoneHome());
            txtPhoneCell.setText(selectedMasterPerson.getPhoneCell());
            txtEmail.setText(selectedMasterPerson.getEmail());

        }

    }
    
    private void myInitComponents(){
        btnAddJudge = new javax.swing.JButton();
        btnFinished = new javax.swing.JButton();
        pnlPreferenceContent = new javax.swing.JPanel();
        pnlPreferenceContent.setToolTipText("Note that only the right panel selections count. The left panel is simply a quicker way of making right panel selections.");

        pnlPersonalInfoContent = new javax.swing.JPanel();
        pnlPersonalInfoContent.setToolTipText("To change data in this panel you must Edit the Master Person.");
        txtLast = new javax.swing.JTextField();
        txtLast.setEnabled(false);
        txtLast.setFocusable(false);
        
        jLabel16 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtPostalCode = new javax.swing.JTextField();
        txtPostalCode.setEnabled(false);
        txtPostalCode.setFocusable(false);
        
        jLabel14 = new javax.swing.JLabel();
        txtCertYear = new javax.swing.JTextField();
        txtCertYear.setEnabled(false);
        txtCertYear.setFocusable(false);
        txtJCNA = new javax.swing.JTextField();
        txtJCNA.setEnabled(false);
        txtJCNA.setFocusable(false);
        jLabel6 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        txtEmail.setEnabled(false);
        txtEmail.setFocusable(false);
        
        txtPhoneCell = new javax.swing.JTextField();
        txtPhoneCell.setEnabled(false);
        txtPhoneCell.setFocusable(false);
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        txtPhoneHome = new javax.swing.JTextField();
        txtPhoneHome.setEnabled(false);
        txtPhoneHome.setFocusable(false);
        
        txtJudgeStatus = new javax.swing.JTextField();
        txtJudgeStatus.setEnabled(false);
        txtJudgeStatus.setFocusable(false);
        jLabel17 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtCity = new javax.swing.JTextField();
        txtCity.setEnabled(false);
        txtCity.setFocusable(false);
        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtFirst = new javax.swing.JTextField();
        txtFirst.setEnabled(false);
        txtFirst.setFocusable(false);
        txtStreetAddress = new javax.swing.JTextField();
        txtStreetAddress.setEnabled(false);
        txtStreetAddress.setFocusable(false);
        txtState = new javax.swing.JTextField();
        txtState.setEnabled(false);
        txtState.setFocusable(false);
        txtCountry = new javax.swing.JTextField();
        txtCountry.setEnabled(false);
        txtCountry.setFocusable(false);
        txtPhoneWork = new javax.swing.JTextField();
        txtPhoneWork.setEnabled(false);
        txtPhoneWork.setFocusable(false);
        cboUniqueName = new javax.swing.JComboBox();
        cboUniqueName.setToolTipText("Start typing Last name for autocompletion.");

        txtClub = new javax.swing.JTextField();
        txtClub.setEnabled(false);
        txtClub.setFocusable(false);
        jLabel11 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        
        setMaximumSize(new java.awt.Dimension(1200, 800)); // the dialog
        setPreferredSize(new java.awt.Dimension(1200, 800)); // the dialog

        btnAddJudge.setText("Add");
        btnAddJudge.setToolTipText("Click to add selected Judge.");
        btnAddJudge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddJudgeActionPerformed(evt);
            }
        });

        btnFinished.setText("Finished");
        btnFinished.setToolTipText("Click after adding one or more Judges. You can add more later.");
        btnFinished.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFinishedActionPerformed(evt);
            }
        });

        pnlPreferenceContent.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Judge Class Preferences"));
        // Group layout might not be the best for pnlPreferenceContent

       /* javax.swing.GroupLayout pnlPreferenceContentLayout = new javax.swing.GroupLayout(pnlPreferenceContent);
        pnlPreferenceContent.setLayout(pnlPreferenceContentLayout);
        pnlPreferenceContentLayout.setHorizontalGroup(
            pnlPreferenceContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        pnlPreferenceContentLayout.setVerticalGroup(
            pnlPreferenceContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 333, Short.MAX_VALUE)
        );
        */
        java.awt.FlowLayout pnlPreferenceContentLayout = new java.awt.FlowLayout();
        pnlPreferenceContent.setLayout(pnlPreferenceContentLayout);
        pnlPreferenceContent.setSize(new Dimension(800, 400));
        
        pnlPersonalInfoContent.setBorder(javax.swing.BorderFactory.createTitledBorder("Judge Personal Data"));

        txtLast.setEditable(false);
        txtLast.setText("last");

        jLabel16.setText("Work phone");

        jLabel5.setText("Street address");

        txtPostalCode.setEditable(false);
        txtPostalCode.setText("unknown");

        jLabel14.setText("Year certified");

        txtCertYear.setEditable(false);
        txtCertYear.setText("unknown");

        txtJCNA.setEditable(false);
        txtJCNA.setText("unknown");

        jLabel6.setText("City");

        jLabel15.setText("Home phone");

        jLabel8.setText("Postal code");

        txtEmail.setEditable(false);
        txtEmail.setText("unknown");

        txtPhoneCell.setEditable(false);
        txtPhoneCell.setText("unknown");
        // is this needed?
        txtPhoneCell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneCellActionPerformed(evt);
            }
        });

        jLabel2.setText("First");

        jLabel3.setText("Judge status");

        txtPhoneHome.setEditable(false);
        txtPhoneHome.setText("unknown");
        // is this needed?
        txtPhoneHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneHomeActionPerformed(evt);
            }
        });

        txtJudgeStatus.setEditable(false);
        txtJudgeStatus.setText("unknown");

        jLabel17.setText("Cell phone");

        jLabel4.setText("Last");

        txtCity.setEditable(false);
        txtCity.setText("unknown");

        jLabel7.setText("State");

        jLabel1.setText("Judge unique name");

        jLabel10.setText("JCNA #");

        jLabel9.setText("Country");

        txtFirst.setEditable(false);
        txtFirst.setText("first");

        txtStreetAddress.setEditable(false);
        txtStreetAddress.setText("unknown");

        txtState.setEditable(false);
        txtState.setText("unknown");

        txtCountry.setEditable(false);
        txtCountry.setText("unknown");

        txtPhoneWork.setEditable(false);
        txtPhoneWork.setText("unknown");
        // is this needed?
        txtPhoneWork.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPhoneWorkActionPerformed(evt);
            }
        });
        //  Maybe this isn't necessary.... should put the real one in here?
        cboUniqueName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Name 1", "Name 2", "Name 3", "Name 4" }));
        cboUniqueName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboUniqueNameItemStateChanged(evt);
            }
        });
        cboUniqueName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboUniqueNameActionPerformed(evt);
            }
        });

        txtClub.setEditable(false);
        txtClub.setText("unknown");

        jLabel11.setText("Club");

        jLabel18.setText("Email");

        javax.swing.GroupLayout pnlPersonalInfoContentLayout = new javax.swing.GroupLayout(pnlPersonalInfoContent);
        pnlPersonalInfoContent.setLayout(pnlPersonalInfoContentLayout);
        pnlPersonalInfoContentLayout.setHorizontalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(34, 34, 34)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(70, 70, 70)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGap(103, 103, 103)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtPostalCode)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(39, 39, 39)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))))
                .addContainerGap())
        );
        pnlPersonalInfoContentLayout.setVerticalGroup(
            pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtJCNA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboUniqueName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtClub, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtJudgeStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                            .addComponent(jLabel14)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txtCertYear, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFirst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtLast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtStreetAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPostalCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCountry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addGap(3, 3, 3)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneHome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneWork, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(pnlPersonalInfoContentLayout.createSequentialGroup()
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18))
                        .addGap(3, 3, 3)
                        .addGroup(pnlPersonalInfoContentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtPhoneCell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(39, Short.MAX_VALUE))
        );
 

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlPersonalInfoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pnlPreferenceContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(449, 449, 449)
                        .addComponent(btnAddJudge, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnFinished)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(pnlPersonalInfoContent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(pnlPreferenceContent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddJudge)
                    .addComponent(btnFinished))
                .addGap(69, 69, 69))
        );

        getContentPane().setLayout(layout);

        pack();
        
    }

    private void ClearSelectedClassList(ArrayList<JCNAClass> aSelectedClasses) {
        aSelectedClasses.clear();
    }

    private void cboUniqueNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboUniqueNameActionPerformed
        
    }//GEN-LAST:event_cboUniqueNameActionPerformed

    private void txtPhoneHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneHomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneHomeActionPerformed

    private void txtPhoneCellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneCellActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneCellActionPerformed

    private void txtPhoneWorkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPhoneWorkActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPhoneWorkActionPerformed

    private void btnAddJudgeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddJudgeActionPerformed
        // TODO add your handling code here:
        Long mpid;
        Integer concoursPersonnelNode;
        Integer ownerNode;

        String person_unique_name;
        String OwnerFirst;
        String OwnerLast;
        Integer JCNA;
        Integer Year;
        String Club;
        String ID;
        String judgeStatus = ""; 
        Integer concoursJudgeNode;
        LoadSQLiteConcoursDatabase  loadSQLiteConcoursDatabase;
        loadSQLiteConcoursDatabase = new LoadSQLiteConcoursDatabase();  // for function access only
        
        ConcoursPerson theConcoursPerson;
        int judgeLoad = 0; // gets incremented during assignments
       // long cpid = 0;  // gets set after new judge is inserted in the Concours Personnel DB table
        this.addedJudge = true;
        Judge theNewJudge;
        ArrayList<String> rejectedClasses;
        //ArrayList<String>  selfEntryClasses;

        //Long personAlreadyInConcoursPersonnel;
       // Long personAlreadyInConcoursJudges;
        MasterPersonExt selectedMasterPerson = (MasterPersonExt) AddConcoursJudgeDialog.this.cboUniqueName.getSelectedItem();

        mpid = selectedMasterPerson.getMasterPersonID();
        person_unique_name = selectedMasterPerson.getUniqueName();
        OwnerFirst = selectedMasterPerson.getFirstName();
        OwnerLast = selectedMasterPerson.getLastName();
        JCNA = selectedMasterPerson.getJcna();
        Year = selectedMasterPerson.getCertYear();
        Club = selectedMasterPerson.getClub();
        ID = theConcoursJudges.GetNextJudgeID();
        concoursPersonnelNode = theConcoursePersonnel.PersonInPersonnelList(person_unique_name); // returns personnel node
        concoursJudgeNode = theConcoursJudges.PersonInJudgeList(person_unique_name); // returns judge personnel node
        ownerNode = theConcours.GetConcoursOwnersObject().GetOwnerNode(person_unique_name);

        
       if (concoursJudgeNode == 0) { 
            // Not in the Judges list
            if(concoursPersonnelNode == 0){
               // Add the person  to ConcoursPersonnel 
               concoursPersonnelNode = theConcoursePersonnel.NextNode();
               // Judge node has to be same as Person node
               concoursJudgeNode = concoursPersonnelNode;
               //Long aMasterpersonnel_id,  String aUnique_name, int aStatus_o,  int aStatus_j, int aConcourspersonnel_node
               theConcoursPerson = new ConcoursPerson(mpid, person_unique_name, 0, 1, concoursPersonnelNode);
               theConcoursePersonnel.AddPerson(theConcoursPerson); 
               // Now update ConcoursPersonnel database table:
               loadSQLiteConcoursDatabase.UpdateAddConcoursPersonnelDBTable(theDBConnection, theConcours.GetLogger(), theConcoursPerson);
            } 
            
            rejectedClasses = CreateRejectedClassesList(); // All the classes that were NOT selected
            concoursJudgeNode = concoursPersonnelNode; // since the new judge is in ConcoursPersonnel
            theNewJudge = new Judge(OwnerLast, OwnerFirst, person_unique_name, JCNA, Year, Club, ID,  rejectedClasses, judgeStatus, concoursJudgeNode,  judgeLoad);
            if(ownerNode != 0 ){
                // Already an Owner so must add Owner' Entries to the new Judge's self entry list
               for(Integer node : theConcours.GetConcoursOwnersObject().GetOwner(person_unique_name).GetEntryList()) {
                   Entry entry = theConcours.GetEntries().getEntry(node);
                   String aClassName = entry.GetClassName();
                   theNewJudge.AddSelfEntry(aClassName);
                   loadSQLiteConcoursDatabase.UpdateAddConcoursJudgeSelfEntryTable(theDBConnection,  theNewJudge,  aClassName);
               }
            }
           theConcours.AddConcoursJudge(theNewJudge);
           theConcours.GetConcoursPersonnelObject().SetPersonJudgeStatus(person_unique_name, 1);
           
               // Now update the affected database tables:
           loadSQLiteConcoursDatabase.UpdateAddConcoursJudgesDBTable(theDBConnection, theNewJudge);
           loadSQLiteConcoursDatabase.UpdateSetstatus_jConcoursPersonnelDBTable(theDBConnection, mpid,  1);
           loadSQLiteConcoursDatabase.UpdateJudgeClassRejectDBTable(theDBConnection, theNewJudge);
           // ++++++++++++
           // Changed 7/14/2016 to remove the no longer valid Judge Assignment/schedule tables from the database
           // the previouse Judge Assignment is now invalid so manual editing is disabled.
            // Also, might as well clear the JudgeAssignments Table and EntryJudgesTable
            theConcours.SetJudgeAssignmentCurrent(false); 
            try { 
                loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ;
            } catch (SQLException ex) {
                //Logger.getLogger(AddConcoursJudgeDialog.class.getName()).log(Level.SEVERE, null, ex);
                String msg = "SQLException in AddConcoursJudgeDialog call to SetSettingsTableJAState";
                okDialog(msg);
                theConcours.GetLogger( ).log(Level.SEVERE, msg, ex);
            }
            // Trying again... 7/14/2016   WORKED.  No locked tables.
            // 1/16/2017 Failed with foreign key issue so commented this out. Shouldn't be necessary anyway.
            // 3/29/2017 Put this back in after fixing ClearJudgeAssignmentsTables()
            // 8/1/2017 Getting locked tables in LoadSQLiteConcoursDatabase.JudgeAssignmentsMemToDB so once again disable this... unnecessary
           //loadSQLiteConcoursDatabase.ClearJudgeAssignmentsTables(theDBConnection);
           // ++++++++
           
           //------------
           // the previouse Judge Assignment is now invalid so manual editing is disabled.
           // theConcours.SetJudgeAssignmentCurrent(false); 
           // loadSQLiteConcoursDatabase.SetSettingsTableJAState(theDBConnection, false) ; 
            // ----------
            okDialog("Person " + person_unique_name + " added to concours Judges");
            theConcours.GetLogger().info("Person " + person_unique_name + " added to concours Judges");

        } else{
            JOptionPane.showMessageDialog(null, "Person " + person_unique_name + " is already in Concourse Personnel as a Judge. No action taken.");
        }
        
       this.btnAddJudge.setEnabled(false);  // to prevent accidental re-adding a Judge 

    }//GEN-LAST:event_btnAddJudgeActionPerformed

private ArrayList<String> CreateRejectedClassesList(){
    ArrayList<String> list = new ArrayList<>();
    Component [] components = pnlJCNAClasses.getComponents();
   for( Component c : components){
       JCheckBox cb = (JCheckBox) c;
       if(!cb.isSelected() ){
           list.add(cb.getName() );
       }
        
   }
   return list;
}

    private void btnFinishedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFinishedActionPerformed
        int response;
        if(this.changedSelections && !this.addedJudge) {
            response = yesNoDialog("You have not clicked Add for the current selections. Are you sure you want to leave the Add Judge dialog?");
            if(response != JOptionPane.YES_OPTION) {
                    return;
            }
        }
        theConcours.GetLogger().info("Finished adding Concours Judges");
        this.setVisible(false);
        
        if(systemexitwhenclosed){
            System.exit(0);
        }
        else{
            this.dispose();
        }
        
    }//GEN-LAST:event_btnFinishedActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

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
            java.util.logging.Logger.getLogger(AddConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AddConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AddConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AddConcoursJudgeDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Connection conn;
                String strConn;
                String strDBName = "SDJC2014.db";
                String strPath = "C:\\Users\\Ed Sowell\\Documents\\JOCBusiness\\Concours" + "\\" + strDBName;
                //String strPath= "C:\\Users\\jag_m_000\\Documents\\Concours" + "\\" + strDBName;
                conn = null;
                try {
                    Class.forName("org.sqlite.JDBC");
                    strConn = "jdbc:sqlite:" + strPath;
                    conn = DriverManager.getConnection(strConn);
                    System.out.println("Opened database " + strConn + " successfully");
                } catch (ClassNotFoundException | SQLException e) {
                    System.err.println(e.getClass().getName() + ": " + e.getMessage());
                    System.exit(0);
                }
                Logger logger = Logger.getLogger("ConcoursBuilderLog");  
                FileHandler fh;  
                try {  
                    fh = new FileHandler(strPath + "ConcoursBuilder.log");  // The log file will be in the strPath
                    logger.addHandler(fh);
                    SimpleFormatter formatter = new SimpleFormatter();  
                    fh.setFormatter(formatter);  
                    logger.info("ConcoursBuilder started");  
                } catch (SecurityException e) {  
                    e.printStackTrace();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  

                System.out.println("Opened database " + strPath + " successfully");
                Concours theConcours = new Concours(logger, 3);
                theConcours.GetJCNAClasses().LoadJCNAClassesDB(conn, "JCNAClasses", logger);
                theConcours.LoadMasterPersonnelDB(conn, logger);
                theConcours.LoadConcoursPersonnelDB(conn, logger);
                theConcours.LoadMasterJaguarDB(conn, logger);
                theConcours.LoadEntriesDB(conn, logger); // TimeslotIndex in Entries gets set when TimeslotAssignment is being updated
                theConcours.LoadJudgesDB(conn, logger); // Judge loads in Judges gets set when TimeslotAssignment is being updated
                theConcours.LoadOwnersDB(conn, logger);

                //MasterListRepository masterList = new MasterListRepository(conn);
                boolean loadRepositoryFromMemory = true;
                MasterListRepository masterList = new MasterListRepository(theConcours, loadRepositoryFromMemory);
                ConcoursPersonnel theConcoursPersonnel = theConcours.GetConcoursPersonnelObject();
                ArrayList<JCNAClass> JCNAClassesList = theConcours.GetJCNAClasses().GetJCNAClasses();
                JCNAClass[] classMasterArray = JCNAClassesList.toArray(new JCNAClass[JCNAClassesList.size()]);
                AddConcoursJudgeDialog theDialog = new AddConcoursJudgeDialog(new javax.swing.JFrame(), true, conn, theConcours, theConcoursPersonnel, masterList, classMasterArray, true, true);

                theDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                theDialog.pack();
                theDialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddJudge;
    private javax.swing.JButton btnFinished;
    private javax.swing.JComboBox cboUniqueName;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel pnlPersonalInfoContent;
    private javax.swing.JPanel pnlPreferenceContent;
    private javax.swing.JTextField txtCertYear;
    private javax.swing.JTextField txtCity;
    private javax.swing.JTextField txtClub;
    private javax.swing.JTextField txtCountry;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFirst;
    private javax.swing.JTextField txtJCNA;
    private javax.swing.JTextField txtJudgeStatus;
    private javax.swing.JTextField txtLast;
    private javax.swing.JTextField txtPhoneCell;
    private javax.swing.JTextField txtPhoneHome;
    private javax.swing.JTextField txtPhoneWork;
    private javax.swing.JTextField txtPostalCode;
    private javax.swing.JTextField txtState;
    private javax.swing.JTextField txtStreetAddress;
    // End of variables declaration//GEN-END:variables
}
