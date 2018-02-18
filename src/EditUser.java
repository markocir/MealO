
import javax.swing.JTable;

public class EditUser extends ActionSettings{

    private final String firstName;
    private final String lastName;
    private final int userId;
    private final boolean superUser;
    private final JTable table;
    
    public EditUser(Database database, JTable table, int userId,
            String firstName, String lastName, boolean superUser)
    {
        super(database);
        this.table = table;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.superUser = superUser;
    }

    @Override
    public void execute() {
        int superUser = this.superUser ? 1 : 0;
        String query = String.format("UPDATE mo_users SET firstName = '%s', lastName = '%s', superUser = %d WHERE user_id = %d", firstName, lastName, superUser, userId);
        database.executeQuery(query, false);
        
        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        
        int row = getRowById(tableModel, userId, 0, table.getRowCount()-1);
        
        // store values to table model
        tableModel.setValueAt(firstName, row, 1);
        tableModel.setValueAt(lastName, row, 2);
        tableModel.setValueAt(superUser == 1 ? true : false, row, 3);
    }
}
