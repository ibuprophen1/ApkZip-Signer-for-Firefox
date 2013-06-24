/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package group.pals.desktop.app.apksigner;

import group.pals.desktop.app.apksigner.i18n.Messages;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * Panel to sign APK files.
 * 
 * @author Hai Bison
 * @since v1.6 beta
 */
public class PanelApkSigner extends JPanel {

    /**
     * Auto-generated by Eclipse.
     */
    private static final long serialVersionUID = -874904794558103202L;

    /**
     * The class name.
     */
    private static final String CLASSNAME = PanelApkSigner.class.getName();

    /**
     * This key holds the last working directory.
     */
    private static final String PKEY_LAST_WORKING_DIR = CLASSNAME
            + ".last_working_dir";
    private JPasswordField passwordField;
    private JTextField textField;
    private JPasswordField passwordField_1;

    /**
     * Create the panel.
     */
    public PanelApkSigner() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        setLayout(gridBagLayout);

        JButton btnNewButton = new JButton(
                Messages.getString("desc_load_key_file"));
        GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
        gbc_btnNewButton.insets = new Insets(10, 3, 3, 3);
        gbc_btnNewButton.gridx = 0;
        gbc_btnNewButton.gridy = 0;
        add(btnNewButton, gbc_btnNewButton);

        passwordField = new JPasswordField();
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setBorder(new TitledBorder(null, Messages
                .getString("password"), TitledBorder.LEADING, TitledBorder.TOP,
                null, null));
        GridBagConstraints gbc_passwordField = new GridBagConstraints();
        gbc_passwordField.insets = new Insets(3, 3, 3, 3);
        gbc_passwordField.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordField.gridx = 0;
        gbc_passwordField.gridy = 1;
        add(passwordField, gbc_passwordField);

        textField = new JTextField();
        textField.setHorizontalAlignment(SwingConstants.CENTER);
        textField.setBorder(new TitledBorder(null, Messages.getString("alias"),
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.insets = new Insets(3, 3, 3, 3);
        gbc_textField.fill = GridBagConstraints.HORIZONTAL;
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 2;
        add(textField, gbc_textField);
        textField.setColumns(10);

        passwordField_1 = new JPasswordField();
        passwordField_1.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField_1.setBorder(new TitledBorder(null, Messages
                .getString("alias_password"), TitledBorder.LEADING,
                TitledBorder.TOP, null, null));
        GridBagConstraints gbc_passwordField_1 = new GridBagConstraints();
        gbc_passwordField_1.insets = new Insets(3, 3, 3, 3);
        gbc_passwordField_1.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordField_1.gridx = 0;
        gbc_passwordField_1.gridy = 3;
        add(passwordField_1, gbc_passwordField_1);

        JButton btnNewButton_1 = new JButton(
                Messages.getString("desc_load_apk_file"));
        GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
        gbc_btnNewButton_1.insets = new Insets(3, 3, 3, 3);
        gbc_btnNewButton_1.gridx = 0;
        gbc_btnNewButton_1.gridy = 4;
        add(btnNewButton_1, gbc_btnNewButton_1);

        JButton btnNewButton_2 = new JButton(Messages.getString("sign"));
        GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
        gbc_btnNewButton_2.insets = new Insets(10, 10, 10, 10);
        gbc_btnNewButton_2.gridx = 0;
        gbc_btnNewButton_2.gridy = 5;
        add(btnNewButton_2, gbc_btnNewButton_2);
    }// PanelApkSigner()

}