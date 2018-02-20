
/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class MealOrderItem extends Meal{
    boolean isOrdered = false;
    
    public MealOrderItem(int mealNumber)
    {
        super(-1, mealNumber, -1, null, null, " ");
        this.isOrdered = false;
    }
    
    public MealOrderItem(int id, int mealNumber, int shift, String date, String description, String allergens, boolean isOrdered)
    {
        super(id, mealNumber, shift, date, description, allergens);
        this.isOrdered = isOrdered;
    }
    
    public void setOrder(boolean value)
    {
        isOrdered = value;
    }
    
    public boolean isOrdered()
    {
        return isOrdered;
    }
    
    @Override
    public String toString()
    {
        // DO NOT CHANGE. this gets extracted and filled in meal list cells
        return String.format("%d<break/>%s<break/>%s<break/>%s", getMealNumber(), getDescription(), getAllergens(), isOrdered);
    }
}
