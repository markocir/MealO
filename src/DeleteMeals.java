
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class DeleteMeals extends ActionSettings {

    private JTable table;
    private final int idToRemove;
    
    
    public DeleteMeals(Database database, JTable table, int idToRemove) {
        super(database);
        this.database = database;
        this.table = table;
        this.idToRemove = idToRemove;
    }

    @Override
    public void execute() {
        database.executeQuery(String.format("DELETE FROM mo_orders WHERE meal_id = %d", idToRemove), false); // remove meal from orders
        database.executeQuery(String.format("DELETE FROM mo_meals WHERE meal_id = %d", idToRemove), false); // remove meal from meals

        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        // find and remove row from model
        int rowToRemove = getRowById(tableModel, idToRemove, 0, table.getRowCount()-1);
        tableModel.removeRow(rowToRemove);

    }
}
