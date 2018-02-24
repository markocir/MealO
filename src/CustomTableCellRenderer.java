
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer{
    String filterText = null;
    
    public CustomTableCellRenderer()
    {
        
    }
    
    public CustomTableCellRenderer(String filterText)
    {
        this.filterText = filterText;
    }
    
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        setBorder(noFocusBorder);
        this.setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
        
        TextHighlightner string = new TextHighlightner();
        value = string.setTextToHighlight(value.toString(), filterText);
        
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
        return this;
    }
}
