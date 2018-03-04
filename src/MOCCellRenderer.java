
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.JTable;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * MealOClient Cell Renderer
 * 
 * @author Marko Čirič <https://github.com/markocir>
 */
public class MOCCellRenderer extends DefaultTableCellRenderer{
    Color[] colors = new Color[11];
        
    public MOCCellRenderer()
    {
        colors[0] = new Color(255,178,102);
        colors[1] = new Color(255,255,102);
        colors[2] = new Color(178,255,102);
        colors[3] = new Color(102,255,102);
        colors[4] = new Color(102,255,178);
        colors[5] = new Color(102,255,255);
        colors[6] = new Color(102,178,255);
        colors[7] = new Color(102,102,255);
        colors[8] = new Color(255,102,255);
        colors[9] = new Color(255,102,178);
        colors[10] = new Color(255,51,51); // red
    }
    
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); 
        
        char character = table.getModel().getValueAt(row, 2).toString().charAt(0);
        
        this.setBorder(new MatteBorder(new Insets(0,0,2,0), Color.white));
        this.setFont(new Font("Sans-serif", Font.PLAIN, 20));
        
        // Convert first character from description column to a number
        // and set the color of this row corresponding to the result (number - 1).
        try
        {
            int val = Integer.valueOf(String.valueOf(character));
            c.setBackground(colors[val-1]);
        }
        catch (Exception ex)
        {
            // When first character in description column is not a number
            // then the row color is set to red.
            c.setBackground(colors[10]);
        }
        
        
        return this;
    }
}
