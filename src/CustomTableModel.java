import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
 */
public class CustomTableModel extends DefaultTableModel{
    private Class[] columnTypes;
    
    public CustomTableModel(Object[][] data, Class[] columnTypes, Object[] columnNames)
    {
        super(data,columnNames);
        this.columnTypes = columnTypes;
    }
    
    @Override
    public boolean isCellEditable(int row,int column){
        return false;
    };
    
    @Override
    public Class getColumnClass(int columnIndex) {
        
        return columnTypes[columnIndex];
    }
}
