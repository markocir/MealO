/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author markoc
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
