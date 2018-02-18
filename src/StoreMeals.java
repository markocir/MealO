class StoreMeals extends ActionOrder {
    int oldId = -1;
    int newId = -1;
    boolean update = false;
    boolean delete = false;
    
    /**
    * <b>StoreMeals(database, 1, 2, true, false)</b>: updates users meal from id 1 to 2;
    * <b>StoreMeals(database, 1, 2, true/false, true)</b>: removes users ordered meal with id 1;
    * <b>StoreMeals(database, 1, 2, false, false)</b>: inserts new meal with id 1 to ordered meals table
    * 
    * @param database 
    *        Self-explanatory
    * 
    * @param oldId 
    *        The id to be deleted or updated/changed
    * 
    * @param newId 
    *        The id to replace the {@code oldId} value
    * 
    * @param update 
    *        Set to {@code true} updates the database
    * 
    * @param delete 
    *        if set to {@code true} overrides {@code update} parameter 
    *        and deletes {@code oldId} from database
    * 
    */
    public StoreMeals(Database database, int oldId, int newId, boolean update, boolean delete)
    {
        super(database);
        this.oldId = oldId;
        this.newId = newId;
        this.update = update;
        this.delete = delete;
    }
    
    @Override
    public void execute() {
        if(update)
        {
            if(delete)
            {
                String query = String.format("DELETE FROM mo_orders WHERE user_id = %d AND meal_id = %d", database.getUserAccount().getUserId(), oldId);
                database.executeQuery(query, false);
                return;
            }
            String query = String.format("UPDATE mo_orders SET meal_id = %d WHERE user_id = %d AND meal_id = %d", newId, database.getUserAccount().getUserId(), oldId);
            database.executeQuery(query, false);
            
        }
        else
        {
            String query = String.format("INSERT INTO mo_orders (user_id, meal_id) VALUES (%d, %d)", database.getUserAccount().getUserId(), newId);
            database.executeQuery(query, false);
        }
    }

}
