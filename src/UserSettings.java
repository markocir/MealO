import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpringLayout;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

public class UserSettings extends JPanel{
    private Database database;
    private CustomJTable table;
    private ArrayList<User> userList;
    private Object[][] userTable;
    private CustomTableModel customTableModel;
    private ArrayList<ActionSettings> actionSettings = new ArrayList<>(0);
        
    public UserSettings(Database database) 
    {
        this.database = database;
        this.setLayout(new java.awt.BorderLayout());
        
        initializeTable();
    }
    
    private void initializeTable()
    {
        JButton editButton = new JButton("Edit user");
        JButton deleteButton = new JButton("Delete User");
        JButton addButton = new JButton("Add User");
        
        deleteButton.setEnabled(false);
        editButton.setEnabled(false);
        
        // declare filter
        JLabel filterLabel = new JLabel("Filter: ");
        JTextField filterTextField = new JTextField();
        
        JPanel panelTop = new JPanel();
        
        table = new CustomJTable();
        JScrollPane tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        
        this.add(panelTop, java.awt.BorderLayout.PAGE_START);
        this.add(tableScrollPane, java.awt.BorderLayout.CENTER);
        
        // top panel 
        SpringLayout layout = new SpringLayout();
        panelTop.setLayout(layout);
        
        panelTop.add(filterTextField);
        panelTop.add(filterLabel);
        panelTop.add(addButton);
        panelTop.add(editButton);
        panelTop.add(deleteButton);
        
        layout.putConstraint(SpringLayout.EAST, filterLabel, -5, SpringLayout.WEST, filterTextField);
        layout.putConstraint(SpringLayout.EAST, filterTextField, -5, SpringLayout.WEST, addButton);
        layout.putConstraint(SpringLayout.EAST, addButton, -5, SpringLayout.WEST, editButton);
        layout.putConstraint(SpringLayout.EAST, editButton, -5, SpringLayout.WEST, deleteButton);
        layout.putConstraint(SpringLayout.EAST, deleteButton, 0, SpringLayout.EAST, panelTop);
        layout.putConstraint(SpringLayout.SOUTH, panelTop, 5, SpringLayout.SOUTH, addButton);    
        layout.putConstraint(SpringLayout.SOUTH, filterTextField, -5, SpringLayout.SOUTH, panelTop);
        layout.putConstraint(SpringLayout.SOUTH, filterLabel, -10, SpringLayout.SOUTH, panelTop);
        
        int defaultTextFieldHeight = (int)filterTextField.getPreferredSize().getHeight();
        filterTextField.setPreferredSize(new Dimension(200, defaultTextFieldHeight));
        addButton.setMargin(new Insets(10,10,10,10));
        editButton.setMargin(new Insets(10,10,10,10));
        deleteButton.setMargin(new Insets(10,10,10,10));
        // end top panel
        
        userList = database.getUsers();
        userTable = new Object[userList.size()][5];
        
        for(int i=0;i<userList.size();i++)
        {
            User u = userList.get(i);
            userTable[i][0] = u.getUserId();
            userTable[i][1] = u.getFirstName();
            userTable[i][2] = u.getLastName();
            userTable[i][3] = u.getSuperUser();
        }
        
        customTableModel = new CustomTableModel(
            userTable,
            new Class[] { Integer.class, String.class, String.class, Boolean.class },
            new Object [] {
                "ID", "First Name", "Last Name", "Super User"
            }
        );
        
        table.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                // do nothing
            }
            
        });
        
        table.setModel(customTableModel);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>((CustomTableModel)table.getModel());
        table.setRowSorter(sorter);
        
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
        
        filterTextField.addKeyListener(new KeyListener(){
            @Override
            public void keyTyped(KeyEvent e) {
                // do nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // do nothing
            }

            @Override
            public void keyReleased(KeyEvent e) {
                TableRowSorter trs = (TableRowSorter)table.getRowSorter();
                String filterText = filterTextField.getText();
                if(filterText.trim().length() == 0)
                    trs.setRowFilter(null);
                else
                    trs.setRowFilter(RowFilter.regexFilter("(?i)" + filterText.trim()));
                
                table.setDefaultRenderer(Object.class, new CustomTableCellRenderer(filterText));
            }
            
        });
        
        addButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                actionSettings.clear(); // clear array list and set size to 0
                addButtonActionPerformed(evt);
                if(!actionSettings.isEmpty())
                    actionSettings.get(0).execute();
            }
        });
        
        editButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionSettings.clear(); // clear array list and set size to 0
                editButtonActionPerformed(evt);
                // execute all deletions
                for(ActionSettings as : actionSettings)
                    as.execute();
            }
        });
        
        deleteButton.addActionListener(new java.awt.event.ActionListener(){
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                actionSettings.clear(); // clear array list and set size to 0
                deleteButtonActionPerformed(evt);
                // execute all deletions
                for(ActionSettings as : actionSettings)
                    as.execute();
            }
        });
    }
    
    private void editButtonActionPerformed(ActionEvent evt)
    {
        int option = -1;
        int[] selectedRows = table.getSelectedRows();
        for(int selectedRow : selectedRows)
        {
            int userId = (int)table.getValueAt(selectedRow, 0);
            String firstName = table.getValueAt(selectedRow, 1).toString();
            String lastName = table.getValueAt(selectedRow, 2).toString();
            Boolean superUser = (boolean)table.getValueAt(selectedRow, 3);
            
            EditUserPane editedUser = new EditUserPane(
                    userId,
                    firstName,
                    lastName,
                    superUser,
                    (userId != database.getUserAccount().getUserId()) ? true : false
            );
            
            option = JOptionPane.showConfirmDialog(
                this,editedUser, "Edit User", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
            if(option == 0)
                actionSettings.add(
                    new EditUser(
                            database, 
                            table, 
                            (int)table.getValueAt(selectedRow, 0), 
                            editedUser.getFirstName(),
                            editedUser.getLastName(),
                            editedUser.getSuperUser()
                    )
                );
        }
    }
    
    private void deleteButtonActionPerformed(ActionEvent evt)
    {
        boolean yesToAllButton = false;
        
        int[] selectedRows = table.getSelectedRows();
        for(int selectedRow : selectedRows)
        {
            int option = -1;
            // if we are not deleting current user
            if((int)table.getValueAt(selectedRow, 0) != database.getUserAccount().getUserId())
            {
                if(!yesToAllButton)
                {
                    // create a question string
                    String questionString = 
                            String.format("Do you want to delete %s%n%s %s with ID %s", 
                                    ((boolean)table.getValueAt(selectedRow, 3) ? "a super user" : ""), 
                                    table.getValueAt(selectedRow,1),
                                    table.getValueAt(selectedRow,2),
                                    table.getValueAt(selectedRow,0));

                    // show confirm dialog
                    Object[] options = null;
                    if(selectedRows.length > 1)
                        options = new Object[] {"Cancel", "No", "Yes", "Yes To All"};
                    else
                        options = new Object[] {"Cancel", "No", "Yes"};

                        option = JOptionPane.showOptionDialog(
                                this, questionString, "Delete user", JOptionPane.OK_CANCEL_OPTION, 
                                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

                    if(option == 3) // if Yes to all button is pressed
                        yesToAllButton = true;
                }
                
                // if Cancel
                if(option == 0)
                {
                    actionSettings.clear();
                    break;
                }
                // if Yes Or Yes to all add
                if(option == 2 || yesToAllButton)
                {
                    actionSettings.add(new DeleteAccount(database, table, (int) table.getValueAt(selectedRow, 0)));
                }
            }
            // pervent current user deletion
            else
                JOptionPane.showConfirmDialog(
                        this,"You can not delete yourself.", "Self deletion detected", 
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt)
    {
        AddUserPane addUserPane = new AddUserPane();
        int option = JOptionPane.showConfirmDialog(
                this,addUserPane, "Add new user", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
	if(option == JOptionPane.OK_OPTION)
	{
            actionSettings.add(
                    new AddUser(database, (CustomTableModel)table.getModel(), 
                            userList, addUserPane.getFirstName(), addUserPane.getLastName(), 
                            addUserPane.getSuperUser()));
	}
    } 
}
