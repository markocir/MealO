import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
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
public class OrdersSettings extends JPanel{
    private Database database = null;
    private CustomJTable table = null;
    private ArrayList<ActionSettings> actionSettings = new ArrayList<>(0);
    private CustomTableModel customTableModel;
    private ArrayList<Orders> ordersList = new ArrayList<>(3);
    private Object[][] ordersTable = null;
    private int shiftNumber = 1;
    ArrayList<ShiftButton> shiftGroup = new ArrayList<>(3);
    
    public OrdersSettings(Database database)
    {
        this.database = database;
        this.setLayout(new java.awt.BorderLayout());
        initializeTable();
        shiftDisable(shiftNumber);
    }
    
    public void initializeTable()
    {        
        // declare buttons
        JButton printTomorrowsOrdersButton = new JButton("Print Tomorrows Orders");
        
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
        panelTop.add(printTomorrowsOrdersButton);
        
        printTomorrowsOrdersButton.setMargin(new Insets(10,10,10,10));        
        
        // add buttons to bottom panel
        panelBottom.add(morningShift);
        panelBottom.add(afternoonShift);
        panelBottom.add(nightShift);
        
        // positionate components in top panel
        layoutTop.putConstraint(SpringLayout.SOUTH, panelTop, 5, SpringLayout.SOUTH, printTomorrowsOrdersButton);    
        layoutTop.putConstraint(SpringLayout.EAST, printTomorrowsOrdersButton, 0, SpringLayout.EAST, panelTop);
        layoutTop.putConstraint(SpringLayout.WEST, shiftName, 0, SpringLayout.WEST, panelTop);
         
        // positionate components in bottom panel
        layoutBottom.putConstraint(SpringLayout.SOUTH, panelBottom, 5, SpringLayout.SOUTH, morningShift);
        layoutBottom.putConstraint(SpringLayout.WEST, morningShift, 0, SpringLayout.WEST, panelBottom);
        layoutBottom.putConstraint(SpringLayout.WEST, afternoonShift, 5, SpringLayout.EAST, morningShift);
        layoutBottom.putConstraint(SpringLayout.WEST, nightShift, 5, SpringLayout.EAST, afternoonShift);
        
        // self-explanatory
        buildTable();
        
        printTomorrowsOrdersButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt) {
                actionSettings.clear(); // clear array list and set size to 0
                printTomorrowsOrdersButtonActionPerformed();
                
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
        ordersTable = null;
        ordersList = new ArrayList<>(3);
        // create an empty table of columns: Day name, date, shift and a column for each meal number
        Object[] tableColumns = new Object[3 + database.getNumberOfMealsPerDay()];
        Class[] tableClasses = new Class[3 + database.getNumberOfMealsPerDay()];
        
        tableColumns[0] = "Day";
        tableColumns[1] = "Date";
        tableColumns[2] = "Shift";
        
        tableClasses[0] = String.class;
        tableClasses[1] = String.class;
        tableClasses[2] = Integer.class;
        
        for(int i = 0; i<tableColumns.length-3; i++)
        {
            tableColumns[i+3] = "Meal "+(i+1);
            tableClasses[i+3] = Integer.class;
        }
        
        String[] dates = database.getGroupedMealDates();
        String[] todaysDate = database.getUpcommingDates(0, 1);
        String[] datePart = todaysDate[0].split("\\s"); 
        
        boolean dateFound = false;
              
        for(String date : dates)
        {
            if(date.equals(datePart[1]))
                dateFound = true;
            
            // for each date that is >= todays date add row to ordered meals list
            if(dateFound)
            {

                Orders ordersRow = new Orders(database.getDayName(date), date, shiftNumber, database.getNumberOfMealsPerDay());

                for(int mealNumber = 1; mealNumber<=database.getNumberOfMealsPerDay(); mealNumber++)
                {
                    int count = database.getCountOfMeals(date, shiftNumber, mealNumber);
                    ordersRow.setMealCount(mealNumber, count);
                }

                ordersList.add(ordersRow);
            }
        }
        
        ordersTable = new Object[ordersList.size()][tableColumns.length];
        
        // fill the table from ordered meals list
        for(int i=0;i<ordersList.size();i++)
        {
            Orders o = ordersList.get(i);
            ordersTable[i][0] = o.getDayName();
            ordersTable[i][1] = o.getDate();
            ordersTable[i][2] = o.getShiftNumber();
            for(int mealNumber=1; mealNumber<=database.getNumberOfMealsPerDay(); mealNumber++)
            {
                ordersTable[i][3 + mealNumber-1] = o.getMealCount(mealNumber);
            }
        }
        
        // remove all elements from the list
        ordersList.clear();
        
        // fill the table model with table data
        customTableModel = new CustomTableModel(
            ordersTable,
            tableClasses,
            tableColumns
        );
        
        table.setModel(customTableModel);
        table.validate();
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>((CustomTableModel)table.getModel());
        table.setRowSorter(sorter);
        
        table.setDefaultRenderer(Object.class, new OrdersListTableCellRenderer());
    }
   
    private void printTomorrowsOrdersButtonActionPerformed()
    {
        String[] dates = database.getUpcommingDates(0, database.getNumberOfWorkdays());
        String[] date = dates[0].split("\\s");
        
        Print printer = new Print(database, date[1], shiftNumber);
        printer.execute();
    }
    
    public int getShiftNumber()
    {
        return shiftNumber;
    }
    
    private void shiftDisable(int value)
    {        
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
