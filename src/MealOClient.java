
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumnModel;
import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class MealOClient extends javax.swing.JFrame {
    private static final String DATABASE = "//localhost:1527/src/db";
    private static final String ATTRIBUTES = "";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    
    private PreparedStatement getOrderedMealStatement = null;
    private PreparedStatement setOrderClaimStatement = null;
    
    private Connection connection = null;
    private ResultSet resultSet = null;
    
    /**
     * Creates new form MealOClient
     */
    public MealOClient() {
        initComponents();
        // message is displayed when user should be informed about an exception
        messageLabel.setFont(new Font("Sans-serif", Font.BOLD, 16));
        
        // DATE TOP START
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");
        
        todaysDate.setFont(new Font("Sans-serif", Font.PLAIN, 20));
        todaysDate.setText(localDate.format(formatter));
        
        formatter = DateTimeFormatter.ofPattern("yyyy-LL-dd");
        String date = localDate.format(formatter);
        // DATE TOP END
        
        passwordField.requestFocusInWindow();
        passwordField.setText("");
        
        // if server is online get connection and prepare statements
        try 
        {
            checkServerStatus();
            connection = getConnection();
            getOrderedMealStatement = 
                    connection.prepareStatement("SELECT * FROM mo_orders mo "
                            + "INNER JOIN mo_meals mm ON mo.meal_id = mm.meal_id "
                            + "INNER JOIN mo_users mu ON mo.user_id = mu.user_id "
                            + "WHERE mo.user_id = ? AND mm.date = ? AND mm.shift = 1");
            
            setOrderClaimStatement = 
                    connection.prepareStatement("UPDATE mo_orders SET date_claimed = ? WHERE user_id = ? AND meal_id = ?");
            
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(MealOClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // create and prepare table model
        CustomTableModel tableModel = new CustomTableModel(
            null,
            new Class[] {String.class, String.class, String.class},
            new Object[] {"Time", "Name", "Meal"}
        );
        
        table.setModel(tableModel);
        table.setEnabled(false);
        table.setRowHeight(60);
        table.setDefaultRenderer(String.class, new MOCCellRenderer());
        
        TableColumnModel tableColumnModel = table.getColumnModel();
        
        // set table column widths
        tableColumnModel.getColumn(0).setPreferredWidth(80);
        tableColumnModel.getColumn(1).setPreferredWidth(170);
        tableColumnModel.getColumn(2).setPreferredWidth(750);
        
        // When anything is typed in the password field after 200 milliseconds
        // input is stored and password field is cleared. The stored input is
        // then used to fetch data from database.
        DelayedDocumentListener listener = new DelayedDocumentListener(200, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMessageFields();
                
                StringBuffer stringBufferPassword = new StringBuffer();
                stringBufferPassword.append(passwordField.getPassword());
                passwordField.setText("");
                
                Date d = new Date();
                
                // Set data to prepared statement and fetch the first result.
                try {
                    getOrderedMealStatement.setInt(1, Integer.valueOf(stringBufferPassword.toString()));
                    getOrderedMealStatement.setString(2, date);
                    
                    resultSet = getOrderedMealStatement.executeQuery();
                    
                    if(resultSet.next())
                    {
                        // If meal was not claimed yet. Update the order with 
                        // the current timestamp and add a new row to the table model.
                        if(resultSet.getString("date_claimed") == null)
                        {
                            String dateClaimed = String.valueOf(new java.sql.Timestamp(d.getTime()));
                            String[] dateClaimedPart = dateClaimed.split("\\s");
                            setOrderClaimStatement.setString(1, dateClaimed);
                            setOrderClaimStatement.setInt(2, resultSet.getInt("user_id"));
                            setOrderClaimStatement.setInt(3, resultSet.getInt("meal_id"));
                            setOrderClaimStatement.executeUpdate();
                            
                            tableModel.insertRow(0, 
                                    new Object[]{dateClaimedPart[1],
                                        resultSet.getString("firstName").concat(" ").concat(resultSet.getString("lastName")).toUpperCase(), 
                                        resultSet.getString("mealNumber").concat(": ").concat(resultSet.getString("description")).toUpperCase()
                                        }
                            );
                            return;
                        }
                        // If meal was already claimed once. Get the claimed 
                        // timestamp from the database and add and 'already claimed'
                        // row to the time table
                        else
                        {
                            String timestamp = String.valueOf(resultSet.getTimestamp("date_claimed"));
                            tableModel.insertRow(0, 
                                    new Object[] {"", 
                                        resultSet.getString("firstname").concat(" ").concat(resultSet.getString("lastname")), 
                                        "Claimed on ".concat(timestamp), ""}
                            );
                        }
                    }
                    // If meal order for that day and shift was not found
                    // add a 'Meal Not Ordered' row to the table model.
                    else 
                        tableModel.insertRow(0, 
                                new Object[] {
                                    "Meal Not Ordered!", 
                                    "Meal Not Ordered!", 
                                    "Meal Not Ordered!", 
                                    "Meal Not Ordered!"}
                        );
                } 
                catch (NumberFormatException ex) 
                {
                    passwordField.setBackground(Color.red);
                    messageLabel.setForeground(Color.red);
                    messageLabel.setText("The format of ID entered is incorrect.");
                }
                catch(SQLException ex)
                {
                    Logger.getLogger(MealOClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
        }, false);
        
        passwordField.getDocument().addDocumentListener(listener);
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
        tableScrollPanel = new javax.swing.JScrollPane();
        table = new javax.swing.JTable();
        todaysDate = new javax.swing.JLabel();
        messageLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tableScrollPanel.setViewportView(table);

        todaysDate.setText("<<TODAYS_DATE>>");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1217, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(messageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(todaysDate)
                        .addGap(15, 15, 15))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(messageLabel))
                    .addComponent(todaysDate))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tableScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 551, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Resets {@code passwordField} color to white. And {@code messageLabel}
     * color to black and removes text.
     */
    private void resetMessageFields()
    {
        passwordField.setBackground(Color.white);
        messageLabel.setForeground(Color.black);
        messageLabel.setText("");
    }
    
    private static Connection getConnection() throws SQLException
    {
        String URL = "jdbc:derby:"+DATABASE+";"+ATTRIBUTES;
        Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        
        return connection;
    }
    
    /**
     * Checks if server is online.
     * @throws Exception 
     */
    private void checkServerStatus() throws Exception
    {
        // Server instance for testing connection
        org.apache.derby.drda.NetworkServerControl server = new NetworkServerControl();

        // Check if server is online
        for (int i = 0; i < 10; i ++)
        {
                try {
                        Thread.currentThread().sleep(500);
                        server.ping();
                        break;
                }
                catch (Exception e)
                {
                        // Stop trying after 10th try and throw an exception
                        if (i == 9 )
                        {
                                throw e;
                        }
                        
                        Thread.currentThread().sleep(4500);
                }
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.htableModell 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MealOClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MealOClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MealOClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MealOClient.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MealOClient().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel messageLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JTable table;
    private javax.swing.JScrollPane tableScrollPanel;
    private javax.swing.JLabel todaysDate;
    // End of variables declaration//GEN-END:variables
}
