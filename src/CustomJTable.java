import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
public class CustomJTable extends JTable{
    
    public CustomJTable()
    {
        super.setRowHeight(40);
        super.setSelectionBackground(new Color(102,178,255));
        super.getTableHeader().setPreferredSize(new java.awt.Dimension(0,40));
        // set checkbox cell background to transparent
        ((javax.swing.JComponent) super.getDefaultRenderer(Boolean.class)).setOpaque(true);
    }
    
    public void setCellRenderer(Object column, DefaultTableCellRenderer renderer)
    {
        this.getColumn(column).setCellRenderer(renderer);
    }
    
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        
        Component c = super.prepareRenderer(renderer, row, column);
        if(row%2 == 1 && !isCellSelected(row,column))
            c.setBackground(new Color(240,240,240));
        else
            c.setBackground(Color.WHITE);
        
        if(isCellSelected(row,column))
            c.setBackground(new Color(102,178,255));
           
        return c;
    }
    public Component prepareEditor(TableCellEditor editor, int row, int column) {
        Component c = super.prepareEditor(editor, row, column);
        
        if(editor.getCellEditorValue() != null && editor.getCellEditorValue().getClass() == Boolean.class)
            c.setBackground(new Color(102,178,255));
        else
            c.setBackground(Color.WHITE);

        return c;
    }
}
