abstract class ActionSettings {
    protected Database database;

    public ActionSettings(Database database)
    {
        this.database = database;
    }
    
    public abstract void execute();
    
    // recursive method
    protected int getRowById(CustomTableModel model, int id, int lower, int upper)
    {
        if((int) model.getValueAt(lower, 0) == id)
        {

            return lower;
        }
        else if((int) model.getValueAt(upper, 0) == id)
        {
            return upper;
        }


        if((int) model.getValueAt((lower+upper)/2, 0) > id)
        {
            // recursive call
            return getRowById(model, id, ++lower, (lower+upper)/2);
        }
        else 
        {
            // recursive call
            return getRowById(model, id, (lower+upper)/2, --upper);
        }
    }
    
    
}
