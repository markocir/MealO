import java.awt.Container;
import javax.swing.JOptionPane;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class WelcomePanel extends javax.swing.JPanel {
    private Database database = null;
    private Container rootPane = null;
    private MealsPanel orderPanel = null;
    private SettingsPanel settingsPanel = null;
    
    public WelcomePanel()
    {
        initComponents();
    }
    
    public WelcomePanel(Container rp, MealsPanel op) {
        rootPane = rp;
        orderPanel = op;
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

        passwordField = new javax.swing.JPasswordField();

        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1450, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(705, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    public javax.swing.JPasswordField getField()
    {
        return passwordField;
    }
    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        if(passwordField.getPassword().length != 0)
        {
            char[] passwordArray = passwordField.getPassword();
            StringBuilder password = new StringBuilder();
            password.append(passwordArray);

                database = new Database(Integer.parseInt(password.toString()));
                if(!database.isUserFound())
                {
                    JOptionPane.showConfirmDialog(this, "Entered key does not exist.", "User Not Found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
                else
                {
                    this.setVisible(false);
                    if(database.getUserAccount().isSuperUser())
                    {
                        settingsPanel = new SettingsPanel(database, rootPane, this);
                        rootPane.add(settingsPanel);
                        settingsPanel.setVisible(true);
                    }
                    else
                    {
                        orderPanel = new MealsPanel(database, rootPane, this);
                        rootPane.add(orderPanel);
                        orderPanel.setVisible(true);
                    }
                }
        }
    }//GEN-LAST:event_passwordFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPasswordField passwordField;
    // End of variables declaration//GEN-END:variables
}
