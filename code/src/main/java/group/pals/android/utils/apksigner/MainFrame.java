/*
 *    Copyright (C) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

/*
 * MainFrame.java
 *
 * Created on Feb 15, 2012, 5:42:54 PM
 */
package group.pals.android.utils.apksigner;

import de.muntjak.tinylookandfeel.Theme;
import de.muntjak.tinylookandfeel.ThemeDescription;
import de.muntjak.tinylookandfeel.TinyLookAndFeel;
import group.pals.android.utils.apksigner.panels.PanelKeygen;
import group.pals.android.utils.apksigner.panels.PanelKeytools;
import group.pals.android.utils.apksigner.panels.PanelSigner;
import group.pals.android.utils.apksigner.panels.ui.JEditorPopupMenu;
import group.pals.android.utils.apksigner.utils.Files;
import group.pals.android.utils.apksigner.utils.MsgBox;
import group.pals.android.utils.apksigner.utils.UI;
import group.pals.android.utils.apksigner.utils.prefs.Prefs;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main frame.
 *
 * @author Hai Bison
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Preferences
     */
    private final Prefs mPrefs = Prefs.getInstance();
    private static final String KEY_LAST_WORKING_DIR = MainFrame.class.getName() + ".last-working-dir";
    private static final String KEY_JDK_PATH = MainFrame.class.getName() + ".jdk-path";

    /**
     * Initializes UI.
     */
    private void initUi() {
        try {
            ThemeDescription[] availableThemes = Theme.getAvailableThemes();
            for (ThemeDescription td : availableThemes) {
                if (td.getName().matches("(?i).*nightly.*")) {
                    Theme.loadTheme(td);
                    UIManager.setLookAndFeel(new TinyLookAndFeel());
                    SwingUtilities.updateComponentTreeUI(getRootPane());
                    break;
                }
            }
        } catch (UnsupportedLookAndFeelException ex) {
            //Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//initUi()

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initUi();
        initComponents();

        UI.setEditorPopupMenu(getContentPane(), new JEditorPopupMenu());
        btnChooseJdkPath.setAction(mActionChooseJdkPath);
        miExit.setAction(mActionExit);
        miAbout.setAction(mActionAbout);

        if (mPrefs.get(KEY_JDK_PATH) != null) {
            File f = new File(mPrefs.get(KEY_JDK_PATH));
            if (f.isDirectory()) {
                setJdkDir(f);
            }
        }

        tabbedPane.add("Key Generator", new PanelKeygen(this));
        tabbedPane.add("APK Signer", new PanelSigner(this));
        tabbedPane.add("Key Tools", new PanelKeytools(this));
    }//MainFrame()

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        txtJdkPath = new javax.swing.JTextField();
        btnChooseJdkPath = new javax.swing.JButton();
        tabbedPane = new javax.swing.JTabbedPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        miExit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        miAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        txtJdkPath.setEditable(false);
        txtJdkPath.setBorder(javax.swing.BorderFactory.createTitledBorder("(For Windows users) JDK Path:"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(txtJdkPath, gridBagConstraints);

        btnChooseJdkPath.setText("Choose...");
        jPanel1.add(btnChooseJdkPath, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(jPanel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(tabbedPane, gridBagConstraints);

        jMenu1.setText("File");

        miExit.setText("jMenuItem1");
        jMenu1.add(miExit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Help");

        miAbout.setText("jMenuItem1");
        jMenu2.add(miAbout);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame m = new MainFrame();

                UI.setWindowCenterScreen(m, 59);

                Package pkg = MainFrame.class.getPackage();
                m.setTitle(String.format("%s %s",
                        pkg != null ? pkg.getImplementationTitle() : "apk-signer",
                        pkg != null ? pkg.getImplementationVersion() : "[unknown]"));

                m.setVisible(true);
            }//run()
        });
    }//main()
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChooseJdkPath;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuItem miAbout;
    private javax.swing.JMenuItem miExit;
    private javax.swing.JTabbedPane tabbedPane;
    private javax.swing.JTextField txtJdkPath;
    // End of variables declaration//GEN-END:variables
    private File mJdkDir;

    /**
     * Gets JDK directory.
     *
     * @return the JDK directory.
     */
    public File getJdkDir() {
        return mJdkDir;
    }//getJdkDir()

    /**
     * Sets the JDK directory.
     *
     * @param dir the JDK directory to set.
     */
    public void setJdkDir(File dir) {
        this.mJdkDir = dir;
        txtJdkPath.setText(dir != null ? dir.getAbsolutePath() : null);
        mPrefs.set(KEY_JDK_PATH, dir != null ? dir.getAbsolutePath() : null);
    }//setJdkDir()
    /*
     * ACTION LISTENERS
     */
    private final AbstractAction mActionChooseJdkPath = new AbstractAction("Choose...") {
        @Override
        public void actionPerformed(ActionEvent e) {
            File f = Files.chooseDir(new File(mPrefs.get(KEY_LAST_WORKING_DIR, "/")));
            setJdkDir(f);
            if (f != null) {
                mPrefs.set(KEY_LAST_WORKING_DIR, f.getParent());
            }
        }//actionPerformed()
    };//mActionChooseJdkPath
    private final AbstractAction mActionExit = new AbstractAction("Exit") {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }//actionPerformed()
    };//mActionExit
    private final AbstractAction mActionAbout = new AbstractAction("About...") {
        @Override
        public void actionPerformed(ActionEvent e) {
            Package pkg = MainFrame.class.getPackage();
            String msg = String.format("%s %s\n\n"
                    + "...by Hai Bison\n\n"
                    + " - License: MIT License\n"
                    + " - Code page: https://code.google.com/p/apk-signer/\n"
                    + " - Official site: http://www.haibison.com\n"
                    + "\n"
                    + "Special thanks to Hans Bickel for TinyLaF library:\n"
                    + " - http://www.muntjak.de/hans/java/tinylaf/index.html\n"
                    + " - License: GNU Lesser General Public License\n"
                    + "\n"
                    + "And thanks to our friends who have been contributing to this project:\n"
                    + " - Leo Chien (https://plus.google.com/118055781130476825691?prsrc=2)",
                    pkg != null ? pkg.getImplementationTitle() : "apk-signer",
                    pkg != null ? pkg.getImplementationVersion() : "[unknown]");
            MsgBox.showHugeInfoMsg(null, null, msg, 630, 270);
        }//actionPerformed()
    };//mActionAbout
}
