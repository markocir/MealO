
/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class Meal 
{
    private int id;
    private final int mealNumber;
    private final int shift;
    private final String date;
    private final String description;
    private final String allergens;
    
    public Meal(int id, int mealNumber, int shift, String date, String description, String allergens)
    {
        this.id = id;
        this.mealNumber = mealNumber;
        this.shift = shift;
        this.date = date;
        this.description = description;
        this.allergens = allergens;
    }

    public int getId()
    {
        return this.id;
    }
    
    public int getMealNumber()
    {
        return this.mealNumber;
    }
    
    public int getShift()
    {
        return this.shift;
    }
    
    public String getDate()
    {
        return this.date;
    }
    
    public String getDescription()
    {
        return this.description;
    }
    
    public String getAllergens()
    {
        return this.allergens;
    }
    
    // used when importing meals
    public void setId(int id)
    {
        this.id = id;
    }
    
    @Override
    public String toString()
    {
        return String.format("%d %d %d %s %s %s", this.id, this.mealNumber, this.shift, this.date, this.description, this.allergens);
    }
}
