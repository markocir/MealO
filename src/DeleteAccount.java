import javax.swing.JTable;

class DeleteAccount extends ActionSettings {
  
    private final JTable table;
    private final int idToRemove;
    
    public DeleteAccount(Database database, JTable table, int idToRemove)
    {
        super(database);
        this.table = table;
        this.idToRemove = idToRemove;
    }
    
    public void execute() {
        database.executeQuery(String.format("DELETE FROM mo_orders WHERE user_id = %d", idToRemove), false);
        database.executeQuery(String.format("DELETE FROM mo_users WHERE user_id = %d", idToRemove), false);

        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        
        // recursive method: find a row by id
        int row = getRowById(tableModel, idToRemove, 0, tableModel.getRowCount()-1);
        tableModel.removeRow(row);
    }
}
