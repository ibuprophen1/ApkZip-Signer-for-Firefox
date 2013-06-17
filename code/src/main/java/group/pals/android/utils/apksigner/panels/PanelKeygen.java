/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

/*
 * PanelKeygen.java
 *
 * Created on Feb 15, 2012, 5:48:38 PM
 */
package group.pals.android.utils.apksigner.panels;

import group.pals.android.utils.apksigner.MainFrame;
import group.pals.android.utils.apksigner.panels.ui.JEditorPopupMenu;
import group.pals.android.utils.apksigner.utils.Constants;
import group.pals.android.utils.apksigner.utils.Files;
import group.pals.android.utils.apksigner.utils.KeyGen;
import group.pals.android.utils.apksigner.utils.MsgBox;
import group.pals.android.utils.apksigner.utils.UI;
import group.pals.android.utils.apksigner.utils.prefs.Prefs;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import javax.swing.AbstractAction;

/**
 * The keystore-generator panel.
 *
 * @author Hai Bison
 */
public class PanelKeygen extends javax.swing.JPanel {

    private final MainFrame mMainFrame;

    /**
     * Creates new form PanelKeygen.
     *
     * @param mainFrame the main frame.
     */
    public PanelKeygen(MainFrame mainFrame) {
        mMainFrame = mainFrame;

        initComponents();
        mBtnChooseFile.addActionListener(mBtnChooseFileListener);
        mBtnGenFile.setAction(mActionGenKeyfile);
        mTextValidity.setValue(25);

        UI.setEditorPopupMenu(this, new JEditorPopupMenu());
    }//PanelKeygen()

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mTextFile = new javax.swing.JTextField();
        mBtnChooseFile = new javax.swing.JButton();
        mTextPwd = new javax.swing.JPasswordField();
        mTextPwd2 = new javax.swing.JPasswordField();
        mTextAlias = new javax.swing.JTextField();
        mTextAliasPwd = new javax.swing.JPasswordField();
        mTextAliasPwd2 = new javax.swing.JPasswordField();
        mTextValidity = new javax.swing.JSpinner();
        mTextName = new javax.swing.JTextField();
        mTextOrgUnit = new javax.swing.JTextField();
        mTextOrg = new javax.swing.JTextField();
        mTextCity = new javax.swing.JTextField();
        mTextState = new javax.swing.JTextField();
        mTextCountry = new javax.swing.JTextField();
        mBtnGenFile = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        mTextFile.setBorder(javax.swing.BorderFactory.createTitledBorder("Target file:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextFile, gridBagConstraints);

        mBtnChooseFile.setText("Choose...");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mBtnChooseFile, gridBagConstraints);

        mTextPwd.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextPwd.setBorder(javax.swing.BorderFactory.createTitledBorder("Password:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextPwd, gridBagConstraints);

        mTextPwd2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextPwd2.setBorder(javax.swing.BorderFactory.createTitledBorder("Confirm:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextPwd2, gridBagConstraints);

        mTextAlias.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextAlias.setBorder(javax.swing.BorderFactory.createTitledBorder("Alias:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextAlias, gridBagConstraints);

        mTextAliasPwd.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextAliasPwd.setBorder(javax.swing.BorderFactory.createTitledBorder("Alias password:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextAliasPwd, gridBagConstraints);

        mTextAliasPwd2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextAliasPwd2.setBorder(javax.swing.BorderFactory.createTitledBorder("Confirm:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextAliasPwd2, gridBagConstraints);

        mTextValidity.setBorder(javax.swing.BorderFactory.createTitledBorder("Validity (years - 25 is recommended):"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextValidity, gridBagConstraints);

        mTextName.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextName.setBorder(javax.swing.BorderFactory.createTitledBorder("First and last name:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextName, gridBagConstraints);

        mTextOrgUnit.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextOrgUnit.setBorder(javax.swing.BorderFactory.createTitledBorder("Organizational unit:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextOrgUnit, gridBagConstraints);

        mTextOrg.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextOrg.setBorder(javax.swing.BorderFactory.createTitledBorder("Organization:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextOrg, gridBagConstraints);

        mTextCity.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextCity.setBorder(javax.swing.BorderFactory.createTitledBorder("City or locality:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextCity, gridBagConstraints);

        mTextState.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextState.setBorder(javax.swing.BorderFactory.createTitledBorder("State or province:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextState, gridBagConstraints);

        mTextCountry.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        mTextCountry.setBorder(javax.swing.BorderFactory.createTitledBorder("Country code (XX):"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.PAGE_START;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mTextCountry, gridBagConstraints);

        mBtnGenFile.setText("Generate KeyFile");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        add(mBtnGenFile, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton mBtnChooseFile;
    private javax.swing.JButton mBtnGenFile;
    private javax.swing.JTextField mTextAlias;
    private javax.swing.JPasswordField mTextAliasPwd;
    private javax.swing.JPasswordField mTextAliasPwd2;
    private javax.swing.JTextField mTextCity;
    private javax.swing.JTextField mTextCountry;
    private javax.swing.JTextField mTextFile;
    private javax.swing.JTextField mTextName;
    private javax.swing.JTextField mTextOrg;
    private javax.swing.JTextField mTextOrgUnit;
    private javax.swing.JPasswordField mTextPwd;
    private javax.swing.JPasswordField mTextPwd2;
    private javax.swing.JTextField mTextState;
    private javax.swing.JSpinner mTextValidity;
    // End of variables declaration//GEN-END:variables
    /**
     * Preferences
     */
    private final Prefs mPrefs = Prefs.getInstance();
    private static final String KEY_LAST_WORKING_DIR = PanelKeygen.class.getName() + ".last-working-dir";

    /*
     * ACTION LISTENERS
     */
    private final ActionListener mBtnChooseFileListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            File file = Files.chooseFileToSave(
                    new File(mPrefs.get(KEY_LAST_WORKING_DIR, "/")),
                    Constants.FILE_EXT_KEYSTORE, Constants.REGEX_KEYSTORE_FILES,
                    Constants.DESC_KEYSTORE_FILES);
            if (file != null) {
                mTextFile.setText(file.getAbsolutePath());
                mPrefs.set(KEY_LAST_WORKING_DIR, file.getParent());
            }
        }//actionPerformed()
    };//mBtnChooseFileListener
    private final AbstractAction mActionGenKeyfile = new AbstractAction("Generate Keyfile") {
        @Override
        public void actionPerformed(ActionEvent e) {
            File file = new File(mTextFile.getText());
            if (!file.getParentFile().isDirectory()) {
                MsgBox.showErrMsg(null, null, "Target folder does not exist!");
                return;
            }

            char[] pwd = mTextPwd.getPassword();
            if (pwd == null || pwd.length == 0) {
                MsgBox.showErrMsg(null, null, "Password can not be empty!");
                return;
            }
            if (!Arrays.equals(pwd, mTextPwd2.getPassword())) {
                MsgBox.showErrMsg(null, null, "Password does not match!");
                return;
            }

            String alias = mTextAlias.getText().trim();
            if (alias.isEmpty()) {
                MsgBox.showErrMsg(null, null, "Alias is empty!");
                return;
            }

            char[] aliasPwd = mTextAliasPwd.getPassword();
            if (aliasPwd == null || aliasPwd.length == 0) {
                MsgBox.showErrMsg(null, null, "Alias password can not be empty!");
                return;
            }
            if (!Arrays.equals(aliasPwd, mTextAliasPwd2.getPassword())) {
                MsgBox.showErrMsg(null, null, "Alias password does not match!");
                return;
            }

            int validity = (Integer) mTextValidity.getValue();
            if (validity <= 0) {
                MsgBox.showErrMsg(null, null, "Validity must be > 0!");
                return;
            }

            String name = mTextName.getText().trim();
            String orgUnit = mTextOrgUnit.getText().trim();
            String org = mTextOrg.getText().trim();
            String city = mTextCity.getText().trim();
            String state = mTextState.getText().trim();
            String country = mTextCountry.getText().trim();
            String[] ownerProps = new String[]{name, orgUnit, org, city, state, country};

            int validValueCount = 0;
            for (String s : ownerProps) {
                if (!s.isEmpty()) {
                    validValueCount++;
                }
            }
            if (validValueCount < 1) {
                MsgBox.showErrMsg(null, null, "At least one Certificate issuer field is required to be non-empty.");
                return;
            }

            try {
                if (file.exists()) {
                    file.delete();
                }

                KeyGen.genKey(mMainFrame.getJdkDir(), file, pwd, alias, aliasPwd,
                        validity * 365, name, orgUnit, org, city, state, country);
                MsgBox.showInfoMsg(null, null, "Key-file generated successfully");
            } catch (Exception ex) {
                MsgBox.showErrMsg(null, null, "Error while generating key-file. Please try again.\n\nDetails:\n" + ex);
            }
        }//actionPerformed()
    };//mActionGenKeyfile
}