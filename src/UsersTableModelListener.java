
import java.util.ArrayList;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class UsersTableModelListener implements TableModelListener {
    private JTable table;
    private Database database;
    private ArrayList<User> userList;
    private int editedRow, editedColumn;
    
    public UsersTableModelListener(Database database, ArrayList<User> userList, JTable table )
    {
        this.database = database;
        this.userList = userList;
        this.table = table;
    }
    
    @Override
    public void tableChanged(TableModelEvent e) 
    {
        if(table.isEditing() && e.getColumn() != 0)
        {
            String[] columnNames = {"user_id", "firstName", "lastName", "superUser"};

            editedRow = table.getSelectedRow();
            editedColumn = table.getSelectedColumn();

            if(userList.get(editedRow).getColumn(editedColumn).compareTo(table.getValueAt(editedRow,editedColumn).toString())  != 0)
            {
                String column = columnNames[editedColumn];
                int userId = (int) table.getValueAt(editedRow, 0);
                Object value = table.getValueAt(editedRow, editedColumn);
                
                userList.get(editedRow).setColumn(editedColumn, table.getValueAt(editedRow,editedColumn));
                
                String query = String.format("UPDATE mo_users SET %s = %s WHERE user_id = %d",column, prepareObject(value), userId);
                database.executeQuery(query, false);
            }
        }
    }
    
    public Object prepareObject(Object value)
    {
        if(value.getClass() == Boolean.class)
            return (value.toString().compareTo("true") == 0) ? 1 : 0 ;
        else if(value.getClass() == String.class)
            return "'" + value + "'";
        else
            return value;
    }
    
}
