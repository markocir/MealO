
import java.util.ArrayList;

class FetchMeals extends ActionOrder {
   
    public FetchMeals(Database database, int shiftNumber, MealsPanel panel)
    {
        super(database, shiftNumber, panel);
    }
    
    public void execute()
    {
        
        getMealsPanel().setVisible(false); // used to make changes visible
        getMealsPanel().getMealsListPanel().removeAll(); // remove all meal elements
        int numberOfMealsPerDay = database.getNumberOfMealsPerDay();
        // run through upcomming number of dates
        for(String date : getUpcommingDates())
        {            
            MealOrderItem selectedMeal = new MealOrderItem(-1);
            
            // split string of date
            String[] datePart = date.split("\\s");
            
            String query = String.format("SELECT * FROM mo_prints WHERE date = '%s' AND shift = %d", datePart[1], super.getShiftNumber());
            boolean isPrinted = (database.executeQueryGetRowCount(query) != 0) ? true : false;
            
            // get meals by date and shift number
            ArrayList<MealOrderItem> mealList = getMealsByDate(datePart[1], getShiftNumber());
            // define empty list model
            CustomListModel<MealOrderItem> listModel = new CustomListModel<>();
            // define empty list of disabled cells
            ArrayList<Integer> listOfDisabledCells = new ArrayList<>(0);
            
            // if day has NO meals
            if(mealList.size() == 0)
            {
                while(listModel.size() < numberOfMealsPerDay)
                {
                    // add an empty placeholder cell to the list
                    listModel.addElement(new MealOrderItem(listModel.size()+1));
                    // add the cell number to the list of disabled cells
                    listOfDisabledCells.add(listModel.size()-1);
                }
            }
            // if day HAS meals
            else
            {
                // run through meals in a list
                for(int index=0, incrementalMealNumber = index+1, difference = 0;
                        incrementalMealNumber+difference <= numberOfMealsPerDay;
                        index++, incrementalMealNumber = index+1)
                {
                    // while meals are missing in the MIDDLE of a list
                    while(incrementalMealNumber+difference < mealList.get(index).getMealNumber())
                    {
                        if(listModel.size() >= numberOfMealsPerDay)
                            break;
                        // add an empty placeholder cell to the list
                        listModel.addElement(new MealOrderItem(incrementalMealNumber+difference++));
                        // add the cell number to the list of disabled cells
                        listOfDisabledCells.add(listModel.size()-1);
                    }
                    
                    
                    if(incrementalMealNumber+difference <= numberOfMealsPerDay)
                    // add next meal to the list
                    listModel.addElement(mealList.get(index));
                    
                    // while meals are missing at the END of a list
                    while(mealList.size()-1 == index
                            && incrementalMealNumber+difference < numberOfMealsPerDay)
                    {
                        if(listModel.size() >= numberOfMealsPerDay)
                            break;
                        // add an empty placeholder cell to the list
                        listModel.addElement(new MealOrderItem(incrementalMealNumber+(++difference)));
                        // add the cell number to the list of disabled cells
                        listOfDisabledCells.add(listModel.size()-1);
                    }
                    
                    // store selected meal
                    if(selectedMeal.getId() == -1)
                        selectedMeal = (mealList.get(index).isOrdered()) ? mealList.get(index) : selectedMeal ;
                }
            }
            
            // fill and add a custom cell to the JList
            getMealsPanel().addDay(new CustomJList(datePart[0], datePart[1], listModel, listOfDisabledCells, selectedMeal, isPrinted));
        }
        
        // show changes
        getMealsPanel().setVisible(true); // used to make changes visible
    }
}
