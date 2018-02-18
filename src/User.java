public class User {
    
    private final int userId;
    private String firstName;
    private String lastName;
    private Boolean superUser;
    
    public User(int userId, String firstName, String lastName, Boolean superUser)
    {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.superUser = superUser;
    }
    
    public int getUserId()
    {
        return userId;
    }
    
    public String getFirstName()
    {
        return firstName;
    }
    
    public String getLastName()
    {
        return lastName;
    }
    
    public Boolean getSuperUser()
    {
        return superUser;
    }
    
    public String getColumn(int value)
    {
        switch(value)
        {
            case 0:
                return String.valueOf(userId);
            case 1:
                return firstName;
            case 2:
                return lastName;
            case 3:
                return String.valueOf(superUser);
            default:
                return null;
        }
    }
    
    public void setColumn(int column, Object value)
    {
        switch(column)
        {
            case 1:
                firstName = (String)value;
                break;
            case 2:
                lastName = (String)value;
                break;
            case 3:
                superUser = (value.toString().compareTo("true") == 0)? true : false;
                break;
            default:
                System.out.println("User.java: Failed to update user column("+column+").");
                break;
        }
    }
}
