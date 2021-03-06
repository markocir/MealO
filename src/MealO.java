
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markocir <https://github.com/markocir>
 */
public class MealO extends JFrame {
    
    /**
     * Creates new form MealO
     */
    public MealO() {
        initComponents();
        
        welcomePanel.startDatabase();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        welcomePanel = new WelcomePanel(getContentPane(), orderPanel);
        orderPanel = new MealsPanel();
        settingsPanel = new SettingsPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1600, 920));
        setPreferredSize(new java.awt.Dimension(1600, 920));
        getContentPane().setLayout(new java.awt.CardLayout());
        getContentPane().add(welcomePanel, "card3");
        getContentPane().add(orderPanel, "card4");
        getContentPane().add(settingsPanel, "card4");

        pack();
    }// </editor-fold>//GEN-END:initComponents

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
            java.util.logging.Logger.getLogger(MealO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MealO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MealO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MealO.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                   new MealO().setVisible(true);
            }
        });
        
        // On exit application shutdown the database connection
        // fixes autoincrement by 100 to 1
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try 
                {
                    if(DriverManager.getDrivers().hasMoreElements())
                        DriverManager.getConnection("jdbc:derby:;shutdown=true");
                } 
                catch (SQLException ex) 
                {
                    Logger.getLogger(MealO.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private MealsPanel orderPanel;
    private SettingsPanel settingsPanel;
    private WelcomePanel welcomePanel;
    // End of variables declaration//GEN-END:variables
}
