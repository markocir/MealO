
public class Orders {
    private String dayName = null;
    private String date = null;
    private int shiftNumber = -1;
    private int[] mealCountArray = null;
    
    public Orders(String dayName, String date, int shiftNumber, int numberOfMeals)
    {
        this.dayName = dayName;
        this.date = date;
        this.shiftNumber = shiftNumber;
        this.mealCountArray = new int[numberOfMeals];
        initializeMealCountArray();
    }
    
    public void setMealCount(int mealNumber, int value)
    {
        this.mealCountArray[mealNumber-1] = value;
    }
    
    public String getDayName()
    {
        return dayName;
    }
    
    public String getDate()
    {
        return date;
    }
    
    public int getShiftNumber()
    {
        return shiftNumber;
    }
    
    public int getMealCount(int mealNumber)
    {
        return mealCountArray[mealNumber-1];
    }
    
    private void initializeMealCountArray()
    {
        for(int i=0; i<mealCountArray.length; i++)
            mealCountArray[i] = 0;
    }
}
