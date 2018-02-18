
import java.sql.Connection;
import java.sql.DriverManager;
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




class Database {
    private boolean isUserFound = false;
    
    private String DEFAULT_QUERY = "SELECT * FROM mo_users WHERE user_id = ?";
    private Connection connection;
    private String database_url = "", username = "", password = "";
    private Account account;
    private final int numberOfMealsPerDay = 6;
    
    private final int numberOfWorkdays = 6; 
    
    public Database(String database_url, String username, String password, int accountNumber)
    {
        try {
            this.database_url = database_url;
            this.username = username;
            this.password = password;

            connection = DriverManager.getConnection(database_url, username, password);
            PreparedStatement findUserStatement = connection.prepareStatement(DEFAULT_QUERY,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            findUserStatement.setInt(1, accountNumber);
            ResultSet rs = findUserStatement.executeQuery();
            
            if(!rs.next())
            {
                disconnect();
                return;
            }
            else
                isUserFound = true;
            
            account = new Account(connection, rs.getInt("user_id"), rs.getString("firstName"), rs.getString("lastName"), rs.getInt("superUser"));
            
            rs.close();
            findUserStatement.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void retrieve() throws SQLException
    {
            connection = DriverManager.getConnection(database_url, username, password);
            PreparedStatement findUserStatement = connection.prepareStatement(DEFAULT_QUERY,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            findUserStatement.setInt(1, account.getUserId());
            ResultSet rs = findUserStatement.executeQuery();

            if(!rs.next())
            {
                connection.close();
                return;
            }
            else
                isUserFound = true;

            account = new Account(connection, rs.getInt("user_id"), rs.getString("firstName"), rs.getString("lastName"), rs.getInt("superUser"));

    }
    
    public boolean isUserFound()
    {
        return isUserFound;
    }
    
    public Account getUserAccount()
    {
        return account;
    }
    
    public String getFullName() 
    {
        return String.format("%s %s", account.getFirstName(), account.getLastName());
    }

    private void closeResources(Statement s, ResultSet rs)
    {
        try {
            if (s != null) {
                s.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void closeResources(PreparedStatement ps, ResultSet rs)
    {
        try {
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public ArrayList<User> getUsers()
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<User> userList = new ArrayList<>(1);
        
        try {
            ps = connection.prepareStatement("SELECT * FROM mo_users ORDER BY user_id ASC");
            rs = ps.executeQuery();
            
            while(rs.next())
            {
                userList.add(new User(rs.getInt("user_id"), rs.getString("firstName"), rs.getString("lastName"), (rs.getInt("superUser") == 0) ? false : true));
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(ps, rs);
        }
        
        return userList;
    }
    
    /**
     * 
     * @return 
     *        Returns a {@code String} of valid dates.
     */
    public String getWorkDays()
    {
        /**
        *   1       2        3         4         5        6        7
        * Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday.
        */
        final String databaseEnabledDays = "2 3 4 5 6 7"; 
        
        return databaseEnabledDays;
    }
    
    /**
     * Returns dates specified by {@link Database#getWorkDays} method.
     * @param offset 
     *        Can be a negative or a positive number, 0 represents present day.
     * @param numberOfDates 
     *        Determines how many dates should be returned.
     * @return 
     *        Returns a {@codeString[numberOfDates]} array.
     *        String format example: Sunday 2018-02-25
     * 
     */
    public String[] getUpcommingDates(int offset, int numberOfDates)
    {
        String workDays = getWorkDays();
        
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
     * Method returns the day name of the date provided.
     * 
     * @param date String format: YYYY-MM-DD
     * @return Day name of that date
     */
    public String getDayName(String date)
    {
        String[] dayNames = new DateFormatSymbols().getWeekdays();
        Calendar calendar = Calendar.getInstance();
        String[] datePart = date.split("-");
        calendar.set(Integer.valueOf(datePart[0]), Integer.valueOf(datePart[1])-1, Integer.valueOf(datePart[2]));
        
        return dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
    }
    
    public boolean isOrdered(int id)
    {
        return (account.getOrderedMeals().stream().filter(p -> p.getId() == id).count() == 1);
    }

    public ArrayList<MealOrderItem> getMealsByDate(String date, int shiftNumber)
    {
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<MealOrderItem> dailyMeals = new ArrayList<>(5);
        
        try
        {
            ps = connection.prepareStatement("SELECT * FROM mo_meals WHERE date = ? AND shift = ? ORDER BY mealNumber ASC", 
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, date);
            ps.setInt(2, shiftNumber);
            
            rs = ps.executeQuery();
            rs.last();
            
            dailyMeals = new ArrayList<>(rs.getRow());
            
            rs.beforeFirst();
            while(rs.next())
            {
                MealOrderItem meal = new MealOrderItem(rs.getInt("meal_id"), rs.getInt("mealNumber"), rs.getInt("shift"), rs.getString("date"), rs.getString("description"), rs.getString("allergens"), isOrdered(rs.getInt("meal_id")));
                dailyMeals.add(meal);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeResources(ps, rs);
        }
        
        return dailyMeals;
    }
    
    public ArrayList<Meal> getMealsByShift(int shiftNumber)
    {
        PreparedStatement ps = null;
        ResultSet rs = null;
        ArrayList<Meal> list = null;
        
        try
        {
            ps = connection.prepareStatement("SELECT * FROM mo_meals WHERE shift = ? ORDER BY meal_id ASC",
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setInt(1, shiftNumber);
                        
            rs = ps.executeQuery();
            
            rs.last();
            list = new ArrayList<>(rs.getRow());
            
            rs.beforeFirst();
            while(rs.next())
            {
                Meal meal = new Meal(rs.getInt("meal_id"), rs.getInt("mealNumber"), rs.getInt("shift"), rs.getString("date"), rs.getString("description"), rs.getString("allergens"));
                list.add(meal);
            }
            
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeResources(ps, rs);
        }
        
        return list;
    }
    
    /**
     * Used for SELECT statement
     * @param query
     * @return row count
     */
    public int executeQueryGetRowCount(String query)
    {
        int result = -1;
        Statement s = null;
        ResultSet rs = null;
        
        try 
        {
            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            rs = s.executeQuery(query);
            
            rs.last();
            
            result = rs.getRow();
        } 
        catch (SQLException ex) 
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(s, rs);
        }
        
        return result;
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
        ResultSet rs = null;
        Statement s = null;
        
        try
        {
            s = connection.createStatement();
            
            if(getGeneratedKey)
            {
                s.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                rs = s.getGeneratedKeys();
            }
            else
            {
                result = s.executeUpdate(query);
            }
            
            
            if(rs != null && rs.next())
            {
                result = rs.getInt(1);
            }
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            closeResources(s, rs);
        }
        
        return result;
    }
    
    /**
     * Extract grouped dates from database meal table.
     * @return String array of each date only once.
     */
    public String[] getGroupedMealDates()
    {
        ResultSet rs = null;
        Statement s = null;
        String[] dates = null;
        try
        {
            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            
            rs = s.executeQuery("SELECT date FROM mo_meals GROUP BY date ORDER BY date ASC");
            
            rs.last(); // move to last row
            dates = new String[rs.getRow()]; // set array fields number
            
            rs.beforeFirst(); // move before first row
            
            int i = 0;
            
            while(rs.next())
            {
                dates[i++] = rs.getString("date");
            }
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(s, rs);
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
    public int getCountOfMeals(String date, int shiftNumber, int mealNumber)
    {
        ResultSet rs = null;
        Statement s = null;
        int count = 0;
        
        try
        {
            s = connection.createStatement();
            String query = String.format("SELECT count(mo.user_id) countOrders, mo.meal_id, mm.date, mm.mealNumber "
                    + "FROM mo_orders mo "
                    + "INNER JOIN mo_meals mm ON mo.meal_id = mm.meal_id "
                    + "WHERE mm.date = '%s' AND mm.shift = %d AND mm.mealNumber = %d "
                    + "GROUP BY mo.meal_id, mm.date, mm.mealNumber ", date, shiftNumber, mealNumber);

            rs = s.executeQuery(query);

            if(rs.next())
                count = rs.getInt("countOrders");
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(s, rs);
        }
        
        return count;
    }
    
    /**
     * Counts ordered meals by given date and shift.
     * 
     * @param date
     * @param shiftNumber
     * @return 
     */
    public ArrayList<MealPrintItem> getCountOfMeals(String date, int shiftNumber)
    {
        ResultSet rs = null;
        Statement s = null;
        ArrayList<MealPrintItem> mealList = new ArrayList<>(getNumberOfMealsPerDay());
        
        try
        {
            s = connection.createStatement();
            String query = String.format("SELECT count(mo.user_id) countOrders, mo.meal_id, mm.mealNumber, mm.description, mm.allergens "
                + "FROM mo_orders mo "
                + "INNER JOIN mo_meals mm ON mo.meal_id = mm.meal_id "
                + "WHERE mm.date = '%s' AND mm.shift = %d "
                + "GROUP BY mo.meal_id, mm.mealNumber, mm.description, mm.allergens "
                + "ORDER BY mm.mealNumber ASC", date, shiftNumber);

            rs = s.executeQuery(query);

            while(rs.next())
                mealList.add(new MealPrintItem(rs.getInt("meal_id"), rs.getInt("mealNumber"), shiftNumber, date, rs.getString("description"), rs.getString("allergens"), rs.getInt("countOrders")));
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(s, rs);
        }
        
        return mealList;
    }
    
    /**
     * @return array of past print dates
     */
    public String[] executeQueryPastPrints(String date, int shift)
    {
        ResultSet rs = null;
        Statement s = null;
        String[] results = new String[0];
        
        try
        {
            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = String.format("SELECT * FROM mo_prints WHERE shift = %d AND date < '%s' ORDER BY print_id ASC", shift, date);
            rs = s.executeQuery(query);
            
            rs.last();
            results = new String[rs.getRow()];
            
            rs.beforeFirst();
            
            int i = 0;
            while(rs.next())
                results[i++] = rs.getString("date");
                
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(s, rs);
        }
        
        return results;
    }
    
    public int[] executeQueryGetMealId(String date, int shift)
    {
        ResultSet rs = null;
        Statement s = null;
        int[] results = new int[0];
        
        try
        {
            s = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String query = String.format("SELECT * FROM mo_meals WHERE shift = %d AND date = '%s'", shift, date);
            rs = s.executeQuery(query);
            
            rs.last();
            results = new int[rs.getRow()];
            
            rs.beforeFirst();
            
            int i = 0;
            while(rs.next())
                results[i++] = rs.getInt("meal_id");
                
        }
        catch(SQLException ex)
        {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally
        {
            closeResources(s, rs);
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
