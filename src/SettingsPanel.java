import java.awt.Container;
import javax.swing.JPanel;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class SettingsPanel extends javax.swing.JPanel {
    private JPanel tablePanel;
    private Database database;
    private Container container;
    private WelcomePanel welcomePanel;
    /**
     * Creates new form SettingsPanel
     */
    public SettingsPanel() {
        initComponents();
    }

    public SettingsPanel(Database database, Container container, WelcomePanel welcomePanel)
    {
        initComponents();
        
        this.database = database;
        this.container = container;
        this.welcomePanel = welcomePanel;
        fullName.setText(database.getFullName()+"'s");
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fullName = new javax.swing.JLabel();
        exitButton = new javax.swing.JButton();
        mealsButton = new javax.swing.JButton();
        usersButton = new javax.swing.JButton();
        adminPanelText = new javax.swing.JLabel();
        sidePanel = new javax.swing.JPanel();
        listOrders = new javax.swing.JButton();

        fullName.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        fullName.setText("<<Full Name>>");

        exitButton.setText("Confirm & Exit");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        mealsButton.setText("Meals");
        mealsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mealsButtonActionPerformed(evt);
            }
        });

        usersButton.setText("Users");
        usersButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usersButtonActionPerformed(evt);
            }
        });

        adminPanelText.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        adminPanelText.setText("Administration Panel");

        sidePanel.setLayout(new java.awt.BorderLayout());

        listOrders.setText("List Orders");
        listOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listOrdersActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fullName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(adminPanelText))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(listOrders, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(exitButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(usersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mealsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(219, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fullName)
                    .addComponent(adminPanelText))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(usersButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mealsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(listOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 319, Short.MAX_VALUE))
                    .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void usersButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usersButtonActionPerformed
        if(tablePanel != null)
            sidePanel.remove(tablePanel);
        tablePanel = new UserSettings(database);
        
        sidePanel.add(tablePanel);
        sidePanel.validate();
    }//GEN-LAST:event_usersButtonActionPerformed
   
    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        database.disconnect();
        this.setVisible(false);
        welcomePanel.setVisible(true);
        welcomePanel.getPasswordField().setText("");
        welcomePanel.getPasswordField().requestFocusInWindow();
        container.validate();
    }//GEN-LAST:event_exitButtonActionPerformed

    private void mealsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mealsButtonActionPerformed
        if(tablePanel != null)
            sidePanel.remove(tablePanel);
        tablePanel = new MealSettings(database);
        sidePanel.add(tablePanel);
        sidePanel.validate();
    }//GEN-LAST:event_mealsButtonActionPerformed

    private void listOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listOrdersActionPerformed
        if(tablePanel != null)
            sidePanel.remove(tablePanel);
        tablePanel = new OrdersSettings(database);
        sidePanel.add(tablePanel);
        sidePanel.validate();
    }//GEN-LAST:event_listOrdersActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel adminPanelText;
    private javax.swing.JButton exitButton;
    private javax.swing.JLabel fullName;
    private javax.swing.JButton listOrders;
    private javax.swing.JButton mealsButton;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JButton usersButton;
    // End of variables declaration//GEN-END:variables
}
