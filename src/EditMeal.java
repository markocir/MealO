
import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
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
public class EditMeal extends ActionSettings{

    private final int id;
    private final String date;
    private final int mealNumber;
    private final String description;
    private final String allergens;
    private final JTable table;
    private final Container container;
    private final int shiftNumber;
    
    public EditMeal(
            Container container, Database database, JTable table, int selectedRow,
            int shiftNumber, String date, int mealNumber, String description, String allergens)
    {
        super(database);
        this.container = container;
        this.table = table;
        this.id = (int)table.getValueAt(selectedRow, 0);
        this.shiftNumber = shiftNumber;
        this.date = date;
        this.mealNumber = mealNumber;
        this.description = description;
        this.allergens = allergens;
    }

    @Override
    public void execute() {
        CustomTableModel tableModel = (CustomTableModel) table.getModel();
        String query = String.format("SELECT * FROM mo_meals WHERE date = '%s' AND shift = %d AND mealNumber = %d AND meal_id != %d", date, shiftNumber, mealNumber, id);
        
        if(database.executeQueryGetRowCount(query) > 0)
        {
            JOptionPane.showConfirmDialog(container, "Meal number already exists. Reversing changes for meal id "+id+".", "Duplicate Entry Found", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            return; // stop executing the process
        }
        query = String.format("UPDATE mo_meals SET date = '%s', mealNumber = %d, description = '%s', allergens = '%s' WHERE meal_id = %d",
                date,
                mealNumber,
                description,
                allergens,
                id);
        
        database.executeQuery(query, false);
        
        // find row from table model
        int row = getRowById(tableModel, id, 0, table.getRowCount()-1);
        
        // store values to table model
        tableModel.setValueAt(date, row, 1);
        tableModel.setValueAt(mealNumber, row, 2);
        tableModel.setValueAt(description, row, 3);
        tableModel.setValueAt(allergens, row, 4);
    }
}
