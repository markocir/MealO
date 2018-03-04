
import java.sql.Connection;
import java.sql.DriverManager;
import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author Marko Čirič <https://github.com/markocir>
 */
public class NetworkServerUtility {
    private final String DATABASE;
    private final String ATTRIBUTES;
    private final String USERNAME;
    private final String PASSWORD;
    
    public NetworkServerUtility(String database, String attributes, String username, String password)
    {
        DATABASE = database;
        ATTRIBUTES = (attributes == null || attributes.length() == 0) ? "" : attributes;
        USERNAME = username;
        PASSWORD = password;
    }
    
    public void startNetworkServer() throws Exception
    {
        // Start the Network Server using the property
        // and then wait for the server to start by testing a connection
        startWithProperty();
        waitForStart();
    }
    
    private void startWithProperty() throws Exception
    {
        // Start network server
        System.setProperty("derby.drda.startNetworkServer","true");

        // Booting Derby
        Class<?> clazz = Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        clazz.getConstructor().newInstance();
    }
    
    private void waitForStart() throws Exception
    {

        // Server instance for testing connection
        org.apache.derby.drda.NetworkServerControl server = null;

        // Use NetworkServerControl.ping() to wait for the
        // Network Server to come up.
        server = new NetworkServerControl();

        // Try to connect for 50 seconds
        for (int i = 0; i < 10; i ++)
        {
                try {
                        Thread.currentThread().sleep(500);
                        server.ping();
                        break;
                }
                catch (Exception e)
                {
                        // After 50 seconds still isn not possible throw Exception
                        if (i == 9 )
                        {
                                throw e;
                        }
                        
                        Thread.currentThread().sleep(4500);
                }
        }

    }
    
    public Connection getEmbeddedConnection()
        throws Exception
    {
        String dbUrl = "jdbc:derby:"+DATABASE+";"+ATTRIBUTES;
        
        Connection conn = DriverManager.getConnection(dbUrl, USERNAME, PASSWORD);
        return conn;
    }
    
}
