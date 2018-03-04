
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Starts a {@code NetworkServerUtility} and offers database manipulation methods 
 * needed by Meal Ordering Application
 * @author Marko Čirič <https://github.com/markocir>
 */
class Database{
    private boolean isUserFound = false;
    
    private static final String DATABASE = "src/db";
    private static final String ATTRIBUTES = null;
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";
    
    private String DEFAULT_QUERY = "SELECT * FROM mo_users WHERE user_id = ?";
    private Connection connection;
    private Account account;
    
    private final int numberOfMealsPerDay = 6;
    
    private final int numberOfWorkdays = 6;
    
    private final NetworkServerUtility NSU;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;
    
    public Database()
    {
        NSU = new NetworkServerUtility(DATABASE, ATTRIBUTES, USERNAME, PASSWORD);
        try 
        {
            NSU.startNetworkServer();
            connection = NSU.getEmbeddedConnection();
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Retrieves data from database 
     * @param accountNumber 
     */
    public void retrieve(int accountNumber)
    {
        try {
            
            connection = NSU.getEmbeddedConnection();
                    
            preparedStatement = connection.prepareStatement(DEFAULT_QUERY,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            preparedStatement.setInt(1, accountNumber);
            resultSet = preparedStatement.executeQuery();
            
            if(!resultSet.next())
            {
                disconnect();
                return;
            }
            
            account = new Account(
                    connection, 
                    resultSet.getInt("user_id"), 
                    resultSet.getString("firstName"), 
                    resultSet.getString("lastName"), 
                    resultSet.getInt("superUser"));
                        
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }

    }
    
    /**
     * Checks if user has been found
     * @return a boolean value of user found
     */
    public boolean isUserFound()
    {
        return account != null;
    }
    
    /**
     * @return
     *        {@code Account} object
     */
    public Account getUserAccount()
    {
        return account;
    }
    
    /**
     * @return 
     *        full name in format: firstName lastName
     */
    public String getFullName() 
    {
        return String.format("%s %s", account.getFirstName(), account.getLastName());
    }
    
    /**
     * Closes forwarded resources
     * @param ps
     *          {@code PreparedStatement} to be closed
     * @param rs 
     *          {@code ResultSet} to be closed
     */
    private void closeResources()
    {
        try {
            if (preparedStatement != null) 
            {
                preparedStatement.close();
            }
            
            if(statement != null)
            {
                statement.close();
            }
            
            if (resultSet != null) 
            {
                resultSet.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns an array of users with details
     * @return 
     *        {@code ArrayList<User>} of all users in a database
     */
    public ArrayList<User> getUsers()
    {
        ArrayList<User> userList = new ArrayList<>(1);
        
        try {
            preparedStatement = connection.prepareStatement("SELECT * FROM mo_users ORDER BY user_id ASC");
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next())
            {
                userList.add(new User(
                        resultSet.getInt("user_id"), 
                        resultSet.getString("firstName"), 
                        resultSet.getString("lastName"), 
                        (resultSet.getInt("superUser") == 0) ? false : true));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return userList;
    }
    
    /**
     * Returns a {@code String} of valid dates. 
     * @return 
     *        Returns a {@code String} of valid dates.
     * @see 
     *        Calendar#DAY_OF_WEEK
     */
    public String getWorkdays()
    {
        /**
        *   1       2        3         4         5        6        7
        * Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday.
        */
        final String databaseEnabledDays = "2 3 4 5 6 7"; 
        
        return databaseEnabledDays;
    }
    
    /**
     * Returns dates specified by {@link Database#getWorkdays} method.
     * @param   offset 
     *          Can be a negative or a positive number, 0 represents a present day.
     * @param   numberOfDates 
     *          Determines how many dates should be returned.
     * @return 
     *          Returns a {@code String[numberOfDates]} object.
     *          String format example: Sunday 2018-02-25
     * @see 
     *          Database#getWorkdays
     * 
     */
    public String[] getUpcommingDates(int offset, int numberOfDates)
    {
        String workDays = getWorkdays();
        
        String[] dates = new String[numberOfDates];
        
        String dayNames[] = new DateFormatSymbols().getWeekdays();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, offset);
        for(int d = 0; d<numberOfDates;)
        {
            calendar.add(Calendar.DATE, 1);
            if(workDays.contains(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK))))
            {
                dates[d++] = String.format("%s %d-%02d-%02d", 
                        dayNames[calendar.get(Calendar.DAY_OF_WEEK)],
                        calendar.get(Calendar.YEAR), 
                        calendar.get(Calendar.MONTH)+1, 
                        calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
        
        return dates;
    }
    
    /**
     * Method splits the provided date, sets the calendar to that date and 
     * returns the days name.
     * 
     * @param   date
     *          String format: YYYY-MM-DD
     * @return 
     *          Day name of that date
     * 
     * @see Calendar#set(int, int) 
     * @see Calendar#get(int) 
     * @see Calendar#DAY_OF_WEEK
     */
    public String getDayName(String date)
    {
        String[] dayNames = new DateFormatSymbols().getWeekdays();
        Calendar calendar = Calendar.getInstance();
        String[] datePart = date.split("-");
        calendar.set(Integer.valueOf(datePart[0]), Integer.valueOf(datePart[1])-1, Integer.valueOf(datePart[2]));
        
        return dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
    }
    
    /**
     * Check and return true if meal {@ id} is ordered.
     * @param   id
     *          meal id
     * @return 
     *          true if meal is ordered else return false.
     */
    public boolean isOrdered(int id)
    {
        return (account.getOrderedMeals().stream().filter(orderedItem -> orderedItem.getId() == id).count() == 1);
    }

    /**
     * Populates a prepared statement with data provided and fetches the meals
     * from the database.
     * @param   date
     *          date of the day we want to get meals. Date format: YYYY-MM-DD
     * @param shiftNumber
     *          shift of which we want to get the meals
     * @return 
     *          {@code ArrayList<MealOrderItem>} 
     */
    public ArrayList<MealOrderItem> getMealsByDate(String date, int shiftNumber)
    {
        ArrayList<MealOrderItem> dailyMeals = null;
        
        try
        {
            dailyMeals = new ArrayList<>(getNumberOfMealsPerDay());
            preparedStatement = connection.prepareStatement("SELECT * FROM mo_meals WHERE date = ? AND shift = ? ORDER BY mealNumber ASC");
            preparedStatement.setString(1, date);
            preparedStatement.setInt(2, shiftNumber);
            
            resultSet = preparedStatement.executeQuery();
            
            while(resultSet.next())
            {
                MealOrderItem meal = new MealOrderItem(
                        resultSet.getInt("meal_id"), 
                        resultSet.getInt("mealNumber"), 
                        resultSet.getInt("shift"), 
                        resultSet.getString("date"), 
                        resultSet.getString("description"), 
                        resultSet.getString("allergens"), 
                        isOrdered(resultSet.getInt("meal_id")));
                dailyMeals.add(meal);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeResources();
        }
        
        return dailyMeals;
    }
    
    /**
     * Populates a prepared statement with data provided and fetches the meals
     * from the database.
     * @param   shiftNumber
     *          
     * @return 
     */
    public ArrayList<Meal> getMealsByShift(int shiftNumber)
    {
        ArrayList<Meal> list = null;
        
        try
        {
            preparedStatement = connection.prepareStatement("SELECT * FROM mo_meals WHERE shift = ? ORDER BY meal_id ASC");
            preparedStatement.setInt(1, shiftNumber);
                        
            resultSet = preparedStatement.executeQuery();
            
            list = new ArrayList<>();
            
            while(resultSet.next())
            {
                Meal meal = new Meal(
                        resultSet.getInt("meal_id"), 
                        resultSet.getInt("mealNumber"), 
                        resultSet.getInt("shift"), 
                        resultSet.getString("date"), 
                        resultSet.getString("description"), 
                        resultSet.getString("allergens"));
                list.add(meal);
            }
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeResources();
        }
        
        return list;
    }
    
    /**
     * Used for SELECT statement.
     * @param   query
     *          query to be executed
     * @return 
     *          row count
     */
    public int executeQueryGetRowCount(String query)
    {
        int result = -1;
        
        try 
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            resultSet = statement.executeQuery(query);
            
            resultSet.last();
            
            result = resultSet.getRow();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return result;
    }
    
    /**
     * Used for importing meals into the database.
     * Sets IDs to the meals imported and returns the list.
     * @param mealList 
     * @return 
     *        A list of meals imported to database
     */
    public ArrayList<Meal> importMeals(ArrayList<Meal> mealList) throws SQLException
    {
        try
        {  
            preparedStatement = connection.prepareStatement(
                    "INSERT INTO mo_meals (mealNumber, shift, date, description, allergens) "
                            + "VALUES(?,?,?,?,?)", 
                    Statement.RETURN_GENERATED_KEYS);
            
            for(int i = 0; i < mealList.size(); i++)
            {
                Meal meal = mealList.get(i);

                preparedStatement.setInt(1, meal.getMealNumber());
                preparedStatement.setInt(2, meal.getShift());
                preparedStatement.setString(3, meal.getDate());
                preparedStatement.setString(4, meal.getDescription());
                preparedStatement.setString(5, meal.getAllergens());
                preparedStatement.executeUpdate();
                
                resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                mealList.get(i).setId(resultSet.getInt(1));
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return mealList;
    }
    
    /**
     * Used for INSERT, UPDATE and DELETE statements.
     * Return generated key or number of affected rows by the statement.
     * @param query
     * @param getGeneratedKey 
     *        {@code true} generated key is returned.
     *        {@code false} affected row count is returned.
     * @return 
     */
    public int executeQuery(String query, boolean getGeneratedKey)
    {
        int result = -1;
        
        try
        {
            statement = connection.createStatement();
            
            if(getGeneratedKey)
            {
                statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                resultSet = statement.getGeneratedKeys();
                result = resultSet.getInt(1);
            }
            else
            {
                result = statement.executeUpdate(query);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeResources();
        }
        
        return result;
    }
    
    /**
     * Extract grouped dates from database meal table.
     * @return String array of each date only once.
     */
    public String[] getGroupedMealDates()
    {
        String[] dates = null;
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            resultSet = statement.executeQuery("SELECT date FROM mo_meals GROUP BY date ORDER BY date ASC");
            
            resultSet.last(); // move to last row
            dates = new String[resultSet.getRow()]; // set array fields number
            
            resultSet.beforeFirst(); // move before first row
            
            int i = 0;
            
            while(resultSet.next())
            {
                dates[i++] = resultSet.getString("date");
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return dates;
    }
    
    /**
     * Counts ordered meals by given date, shift and meal number.
     * Used to fill the ordered meal statistic table.
     * 
     * @param date
     * @param shiftNumber
     * @param mealNumber
     * @return 
     */
    public int getCountOfMealOrders(String date, int shiftNumber, int mealNumber)
    {
        int count = 0;
        
        try
        {
            statement = connection.createStatement();
            String query = String.format("SELECT count(mo.user_id) countOrders, mo.meal_id, mm.date, mm.mealNumber "
                    + "FROM mo_orders mo "
                    + "INNER JOIN mo_meals mm ON mo.meal_id = mm.meal_id "
                    + "WHERE mm.date = '%s' AND mm.shift = %d AND mm.mealNumber = %d "
                    + "GROUP BY mo.meal_id, mm.date, mm.mealNumber ", date, shiftNumber, mealNumber);

            resultSet = statement.executeQuery(query);

            if(resultSet.next())
                count = resultSet.getInt("countOrders");
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return count;
    }
    
    /**
     * Counts ordered meals by given date and shift.
     * Used when printing item.
     * @param date
     * @param shiftNumber
     * @return 
     */
    public ArrayList<MealPrintItem> getCountOfMeals(String date, int shiftNumber)
    {
        ArrayList<MealPrintItem> mealList = new ArrayList<>(getNumberOfMealsPerDay());
        
        try
        {
            statement = connection.createStatement();
            String query = String.format("SELECT count(mo.user_id) countOrders, mo.meal_id, mm.mealNumber, mm.description, mm.allergens "
                + "FROM mo_orders mo "
                + "INNER JOIN mo_meals mm ON mo.meal_id = mm.meal_id "
                + "WHERE mm.date = '%s' AND mm.shift = %d "
                + "GROUP BY mo.meal_id, mm.mealNumber, mm.description, mm.allergens "
                + "ORDER BY mm.mealNumber ASC", date, shiftNumber);

            resultSet = statement.executeQuery(query);

            while(resultSet.next())
                mealList.add(new MealPrintItem(
                        resultSet.getInt("meal_id"), 
                        resultSet.getInt("mealNumber"), 
                        shiftNumber, 
                        date, 
                        resultSet.getString("description"),
                        resultSet.getString("allergens"), 
                        resultSet.getInt("countOrders")));
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return mealList;
    }
    
    /**
     * @return array of past print dates
     */
    public String[] executeQueryPastPrints(String date, int shift)
    {
        String[] results = null;
        
        try
        {
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = String.format("SELECT * FROM mo_prints WHERE shift = %d AND date < '%s' ORDER BY print_id ASC", shift, date);
            resultSet = statement.executeQuery(query);
            
            resultSet.last();
            results = new String[resultSet.getRow()];
            
            resultSet.beforeFirst();
            
            int i = 0;
            while(resultSet.next())
                results[i++] = resultSet.getString("date");
                
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return results;
    }
    
    /**
     * Populates a prepared statement with data provided and fetches the meals IDs.
     * 
     * @param   date
     *          Filter meals by this date.
     * @param   shift
     *          Filter meals by this shift.
     * @return 
     *          Array of ints found.
     */
    public int[] executeQueryGetMealId(String date, int shift)
    {
        int[] results = new int[0];
        
        try
        {
            preparedStatement = connection.prepareStatement(
                    "SELECT * FROM mo_meals WHERE shift = ? AND date = ?", 
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY
            );
            
            preparedStatement.setInt(1, shift);
            preparedStatement.setString(2, date);
            
            resultSet = preparedStatement.executeQuery();
            
            resultSet.last();
            results = new int[resultSet.getRow()];
            
            resultSet.beforeFirst();
            
            int i = 0;
            while(resultSet.next())
                results[i++] = resultSet.getInt("meal_id");
                
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources();
        }
        
        return results;
    }
    
    public int getNumberOfMealsPerDay() 
    {
        return numberOfMealsPerDay;
    }
    
    public int getNumberOfWorkdays()
    {
        return numberOfWorkdays;
    }
    
    public void disconnect()
    {
        try 
        {
            account = null;
            connection.close();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void printUserData()
    {
        System.out.println(account.toString());
        
        System.out.printf("%nOrdered meals:%n");
        account.getOrderedMeals()
                .forEach(e -> System.out.printf("%d %d %d %s %s %s%n", e.getId(), e.getMealNumber(), e.getShift(), e.getDate(), e.getDescription(), e.getAllergens()));
        System.out.println();
    }
}
