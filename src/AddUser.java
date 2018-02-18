
import java.util.ArrayList;




class AddUser extends ActionSettings {
    private CustomTableModel tableModel;
    private String firstName;
    private String lastName;
    private boolean superUser;
    private ArrayList<User> userList;
    
  
    public AddUser(Database database, CustomTableModel tableModel, ArrayList<User> userList, String firstName, String lastName, boolean superUser)
    {
        super(database);
        this.tableModel = tableModel;
        this.userList = userList;
        this.firstName = firstName;
        this.lastName = lastName;
        this.superUser = superUser;
    }
  
    public void execute() {
        String query = String.format("INSERT INTO mo_users (firstName, lastName, superUser) VALUES ('%s', '%s', %d)", firstName, lastName, superUser ? 1:0);
        int id = database.executeQuery(query, true);
        tableModel.addRow(new Object[] {id,firstName, lastName, superUser});
        userList.add(new User(id,firstName,lastName,superUser));
    }

}
