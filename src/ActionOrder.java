
import java.util.ArrayList;

abstract class ActionOrder {
    
    protected Database database;
    
    private int shiftNumber;
    private MealsPanel panel;

    public ActionOrder(Database database)
    {
        this.database = database;
    }
    
    public ActionOrder(Database database, int shiftNumber, MealsPanel panel)
    {
        this.panel = panel;
        this.database = database;
        this.shiftNumber = shiftNumber;
    }
    
    public int getShiftNumber()
    {
        return this.shiftNumber;
    }

    public String[] getUpcommingDates()
    {
        return database.getUpcommingDates(0, database.getNumberOfWorkdays());
    }
    
    public ArrayList<MealOrderItem> getMealsByDate(String date, int shift) {

        return database.getMealsByDate(date, shift);
    }
    
    public boolean isOrdered(int id)
    {
        return database.isOrdered(id);
    }
    
    public ArrayList<MealOrderItem> getOrderedMeals()
    {
        return database.getUserAccount().getOrderedMeals();
    }
    
    public MealsPanel getMealsPanel()
    {
        return panel;
    }
    
    abstract public void execute();
            

}
