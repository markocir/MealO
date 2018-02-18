
import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class OrdersListTableCellRenderer extends DefaultTableCellRenderer{
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
        
        // day name
        if(column == 0) 
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        
        // meals columns
        if(column > 2 && Integer.valueOf(String.valueOf(value)) > 0) 
            c.setFont(c.getFont().deriveFont(Font.BOLD));
        return this;
    }
}
        