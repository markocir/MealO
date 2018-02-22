import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class MealSettings extends JPanel{
    private Database database = null;
    private CustomJTable table = null;
    private ArrayList<ActionSettings> actionSettings = new ArrayList<>(0);
    private CustomTableModel customTableModel;
    private ArrayList<Meal> mealList;
    private Object[][] mealTable;
    private int shiftNumber = 1;
    private JButton editButton = new JButton("Edit");
    private JButton deleteButton = new JButton("Delete");
    private ArrayList<ShiftButton> shiftGroup = new ArrayList<>(3);
    private MealSettings container;
    
    public MealSettings(Database database)
    {
        this.database = database;
        this.setLayout(new java.awt.BorderLayout());
        initializeTable();
        shiftDisable(shiftNumber);
        container = this;
    }
    
    public void initializeTable()
    {
        // disable buttons on table initialization
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // declare buttons
        JButton importButton = new JButton("Import Meals");
        
        ShiftButton morningShift = new ShiftButton("Morning Shift", 1);
        ShiftButton afternoonShift = new ShiftButton("Afternoon Shift", 2);
        ShiftButton nightShift = new ShiftButton("Night Shift", 3);
        
        // add buttons to group
        shiftGroup.add(morningShift);
        shiftGroup.add(afternoonShift);
        shiftGroup.add(nightShift);
        
        // declare shift name
        JLabel shiftName = new JLabel(morningShift.getText());
        
        // style shift name 
        shiftName.setFont(new Font("Dialog", 1, 18));
         // used as top margin
        shiftName.setBorder(new EmptyBorder(15,0,0,0));
        
        // declare panels
        JPanel panelTop = new JPanel();
        JPanel panelBottom = new JPanel();
        
        // declare table with scrollpane
        table = new CustomJTable();
        JScrollPane tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(table);
        
        // add panels to this panel
        this.add(panelTop, BorderLayout.NORTH);
        this.add(tableScrollPane, BorderLayout.CENTER);
        this.add(panelBottom, BorderLayout.SOUTH);
        
        // declare layouts
        SpringLayout layoutTop = new SpringLayout();
        SpringLayout layoutBottom = new SpringLayout();
        
        // set layouts to panels
        panelTop.setLayout(layoutTop);
        panelBottom.setLayout(layoutBottom);
        
        // add components to top panel
        panelTop.add(shiftName);
        panelTop.add(importButton);
        panelTop.add(editButton);
        panelTop.add(deleteButton);
        
        // set margins to top panel buttons
        editButton.setMargin(new Insets(10,10,10,10));
        deleteButton.setMargin(new Insets(10,10,10,10));
        importButton.setMargin(new Insets(10,10,10,10));        
        
        // add buttons to bottom panel
        panelBottom.add(morningShift);
        panelBottom.add(afternoonShift);
        panelBottom.add(nightShift);
        
        // positionate components in top panel
        layoutTop.putConstraint(SpringLayout.SOUTH, panelTop, 5, SpringLayout.SOUTH, importButton);    
        layoutTop.putConstraint(SpringLayout.EAST, importButton, -5, SpringLayout.WEST, editButton);
        layoutTop.putConstraint(SpringLayout.EAST, editButton, -5, SpringLayout.WEST, deleteButton);
        layoutTop.putConstraint(SpringLayout.EAST, deleteButton, 0, SpringLayout.EAST, panelTop);
        layoutTop.putConstraint(SpringLayout.WEST, shiftName, 0, SpringLayout.WEST, panelTop);
         
        // positionate components in bottom panel
        layoutBottom.putConstraint(SpringLayout.SOUTH, panelBottom, 5, SpringLayout.SOUTH, morningShift);
        layoutBottom.putConstraint(SpringLayout.WEST, morningShift, 0, SpringLayout.WEST, panelBottom);
        layoutBottom.putConstraint(SpringLayout.WEST, afternoonShift, 5, SpringLayout.EAST, morningShift);
        layoutBottom.putConstraint(SpringLayout.WEST, nightShift, 5, SpringLayout.EAST, afternoonShift);
        
        // self-explanatory
        buildTable();
        
        table.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent e) {
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                // do nothing
            }
            
        });
        
        importButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt) {
                actionSettings.clear(); // clear array list and set size to 0
                try 
                {
                    actionSettings.add(new ImportMeals(container, database, (CustomTableModel)table.getModel()));
                    
                    for(ActionSettings as : actionSettings)
                        as.execute();
                } 
                catch (FileNotFoundException ex) 
                {
                    JOptionPane.showConfirmDialog(
                            container,
                            "File should be located in \"import\" map", 
                            "File Not Found", 
                            JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
                catch (IOException ex)
                {
                     Logger.getLogger(MealSettings.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        
        editButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                actionSettings.clear();
                editButtonActionPerformed();
                
                for(ActionSettings as : actionSettings)
                    as.execute();
            }
        });
        
        deleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                actionSettings.clear();
                deleteButtonActionPerformed();
                
                for(ActionSettings as : actionSettings)
                    as.execute();
            }
        });
        
        morningShift.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shiftNumber = 1;
                shiftName.setText(morningShift.getText());
                shiftDisable(shiftNumber);
                buildTable();
            }
        });
        
        afternoonShift.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shiftNumber = 2;
                shiftName.setText(afternoonShift.getText());
                shiftDisable(shiftNumber);
                buildTable();
            }
        });
        
        nightShift.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shiftNumber = 3;
                shiftName.setText(nightShift.getText());
                shiftDisable(shiftNumber);
                nightShift.setEnabled(false);
                buildTable();
            }
        });
        
    }
    
    private void buildTable()
    {
        boolean isEmpty = true; // used to clear table if nothing is fetched from database
        mealList = database.getMealsByShift(shiftNumber);
        mealTable = new Object[mealList.size()][5];
        
        for(int i=0;i<mealList.size();i++)
        {
            Meal m = mealList.get(i);
            if(m.getId() >= 0)
            {
                mealTable[i][0] = m.getId();
                mealTable[i][1] = m.getDate();
                mealTable[i][2] = m.getMealNumber();
                mealTable[i][3] = m.getDescription();
                mealTable[i][4] = m.getAllergens();
                isEmpty = false;
            }
        }
        
        if(isEmpty)
            mealTable = null;
        
        customTableModel = new CustomTableModel(
            mealTable,
            new Class[] { Integer.class, String.class, Integer.class, String.class, String.class },
            new Object [] {
                "ID", "Date", "Meal#", "Description", "Allergens"
            }
        );
        
        table.setModel(customTableModel);
        
        TableColumnModel columnModel = table.getColumnModel();
        
        table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(50);    
        columnModel.getColumn(3).setPreferredWidth(680);
        columnModel.getColumn(4).setPreferredWidth(200);
        
        table.validate();
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>((CustomTableModel)table.getModel());
        
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        
        sorter.setSortKeys(sortKeys);
        table.setRowSorter(sorter);
        
        table.setDefaultRenderer(Object.class, new CustomTableCellRenderer());
    }
    
    private void deleteButtonActionPerformed()
    {
        int option = -1;
        boolean yesToAll = false;
        int[] selectedRows = table.getSelectedRows();
        for(int selectedRow : selectedRows)
        {
            if(!yesToAll)
                {
                    // create a question string
                    String questionString = 
                            String.format("Do you want to delete this meal?%n%nID: %d%nDATE: %s%nMEAL NUMBER: %d%n%nDESCRIPTION:%n%s%n%n%s%n%s", 
                                    table.getValueAt(selectedRow,0), 
                                    table.getValueAt(selectedRow,1),
                                    table.getValueAt(selectedRow,2),
                                    table.getValueAt(selectedRow,3),
                                    "NOTE:",
                                    "If users ordered this meal, their order will be canceled %nand will have to re-order the meal for this day.");
                    // show confirm dialog
                    Object[] options = null;
                    if(selectedRows.length > 1)
                        options = new Object[] {"Cancel", "No", "Yes", "Yes To All"};
                    else
                        options = new Object[] {"Cancel", "No", "Yes"};

                        option = JOptionPane.showOptionDialog(
                                this, questionString, "Delete Meal", JOptionPane.OK_CANCEL_OPTION, 
                                JOptionPane.WARNING_MESSAGE, null, options, options[1]);

                    if(option == 3) // if Yes to all
                        yesToAll = true;
                }
                
                // if Cancel
                if(option == 0)
                {
                    actionSettings.clear();
                    break;
                }
                // if Yes Or Yes to all add
                if(option == 2 || yesToAll)
                {
                    actionSettings.add(new DeleteMeals(database, table, (int)table.getValueAt(selectedRow,0)));
                }
        }
    }
    
    private void editButtonActionPerformed()
    {
        int option = -1;
        int[] selectedRows = table.getSelectedRows();
        for(int selectedRow : selectedRows)
        {
            int id = (int)table.getValueAt(selectedRow, 0);
            String date = table.getValueAt(selectedRow, 1).toString();
            int mealNumber = (int)table.getValueAt(selectedRow, 2);
            String description = table.getValueAt(selectedRow, 3).toString();
            String allergens = table.getValueAt(selectedRow, 4).toString();
            
            EditMealPane editedMeal = new EditMealPane(
                    id,
                    date,
                    mealNumber,
                    description,
                    allergens                    
            );
            
            option = JOptionPane.showConfirmDialog(
                this,editedMeal, "Edit Meal", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
            
            // on OK
            if(option == 0)
            actionSettings.add(
                new EditMeal(
                        this,
                        database, 
                        table, 
                        selectedRow, 
                        shiftNumber,
                        editedMeal.getDate(),
                        editedMeal.getMealNumber(),
                        editedMeal.getDescription(),
                        editedMeal.getAllergens()
                )
            );
        }
    }
        
    public int getShiftNumber()
    {
        return shiftNumber;
    }
    
    private void shiftDisable(int value)
    {
        // when shift is changed disable edit and delete buttons
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        for(ShiftButton button : shiftGroup)
            if(button.getValue() == value)
            {
                button.setEnabled(false);
                button.requestFocus();
            }
            else
                button.setEnabled(true);
    }
}
