
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Account {
  private int userId;
  private String firstName;
  private String lastName;
  private boolean superUser;
  private Connection connection;
  private ArrayList<MealOrderItem> orderedMeals = new ArrayList<>(7);

  public Account(Connection connection, int userId, String firstName, String lastName, int superUser) throws SQLException
  {
      this.connection = connection;
      this.userId = userId;
      this.firstName = firstName;
      this.lastName = lastName;
      this.superUser = (superUser > 0) ? true : false;
      
      PreparedStatement statement = connection.prepareStatement("SELECT mm.* FROM mo_orders mo "
              + "INNER JOIN mo_meals mm ON mo.meal_id = mm.meal_id "
              + "WHERE mo.user_id = ?",
              ResultSet.TYPE_SCROLL_INSENSITIVE,
              ResultSet.CONCUR_READ_ONLY);
      statement.setInt(1, this.userId);
      
      ResultSet rs = statement.executeQuery();
      
      while(rs.next())
      {
          this.orderedMeals.add(new MealOrderItem(
                  rs.getInt("meal_id"),
                  rs.getInt("mealNumber"),
                  rs.getInt("shift"),
                  rs.getString("date"),
                  rs.getString("description"),
                  rs.getString("allergens"),
                  true
          ));
      }
  }
  
  public void setFirstName(String firstName)
  {
      this.firstName = firstName;
  }
  
  public void setLastName(String lastName)
  {
      this.lastName = lastName;
  }
  
  public void setSuperUser(int superUser)
  {
      this.superUser = (superUser > 0);
  }
  
  public void setUserId(int userId) 
  {
      this.userId = userId;
  }

  public boolean isSuperUser() {
      return superUser;
  }

  public int getUserId() {
      return userId;
  }

  public String getFirstName() {
      return firstName;
  }

  public String getLastName() {
      return lastName;
  }

  public ArrayList<MealOrderItem> getOrderedMeals() {
      return orderedMeals;
  }
  
  public String toString()
  {
      return String.format("%s %15s %23s%n%06d %7s %s %12s","ID","Full Name","Is Super User?", 
              this.userId, this.firstName, this.lastName, this.superUser);
  }
}
