public class MealPrintItem extends Meal{
    private int ordersCount = 0;
    
    public MealPrintItem(int id, int mealNumber, int shift, String date, String description, String allergens, int ordersCount)
    {
        super(id, mealNumber, shift, date, description, allergens);
        
        this.ordersCount = ordersCount;
    }
    
    public int getOrdersCount()
    {
        return this.ordersCount;
    }
}
