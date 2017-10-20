package com.jonfreer.wedding.infrastructure.factories;

import com.jonfreer.wedding.domain.interfaces.unitofwork.IDatabaseUnitOfWork;
import com.jonfreer.wedding.infrastructure.interfaces.factories.IDatabaseUnitOfWorkFactory;
import com.jonfreer.wedding.infrastructure.unitofwork.DatabaseUnitOfWork;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jvnet.hk2.annotations.Service;

import javax.inject.Named;

/**
 * Factory that creates DatabaseUnitOfWork instances.
 */
@Service
@Named
public class DatabaseUnitOfWorkFactory implements IDatabaseUnitOfWorkFactory {

    private String connectionString;
    private String username;
    private String password;

    /**
     * Constructs a DatabaseUnitOfWorkFactory. This constructor looks for a
     * file called 'databaseInfo.properties' in order to load in database
     * configuration information.
     */
    public DatabaseUnitOfWorkFactory() {

        InputStream is = null;

        try {

            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("databaseInfo.properties");
            Properties databaseProperties = new Properties();
            databaseProperties.load(is);
            is.close();

		//default to the values in the property files for backward compatability.
		//override values with environment values.
		String host = databaseProperties.getProperty("host");
		String port = databaseProperties.getProperty("port");
		String databaseName = databaseProperties.getProperty("databaseName");
		String username = databaseProperties.getProperty("username");
		String password = databaseProperties.getProperty("password");
		String jdbcConnectionString = "jdbc:mysql://%s:%s/%s?useLegacyDatetimeCode=false&useSSL=false";

		String hostEnv = System.getenv("MYSQL_HOST");
		if(hostEnv != null){
			System.out.println("Successfully read database host environment variable.");
			host = hostEnv;
		}

		String portEnv = System.getenv("MYSQL_PORT");
		if(portEnv != null){
			System.out.println("Successfully read database port environment variable.");
			port = portEnv;
		}

		String databaseNameEnv = System.getenv("MYSQL_DATABASE");
		if(databaseNameEnv != null){
			System.out.println("Successfully read database name environment variable.");
			databaseName = databaseNameEnv;
		}

		String usernameEnv = System.getenv("MYSQL_USER");
		if(usernameEnv != null){
			System.out.println("Successfully read database user environment variable.");
			username = usernameEnv;
		}

		String passwordEnv = System.getenv("MYSQL_PASSWORD");
		if(passwordEnv != null){
			System.out.println("Successfully read database password environment variable.");
			password = passwordEnv;
		}

		this.connectionString = String.format(jdbcConnectionString, host, port, databaseName);
		System.out.println("Connection string: " + this.connectionString);
		this.username = username;
		this.password = password;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Creates a new instance of DatabaseUnitOfWork.
     *
     * @return The new instance of DatabaseUnitOfWork.
     */
    public IDatabaseUnitOfWork create() {
        try {
            return new DatabaseUnitOfWork(
                    DriverManager.getConnection(this.connectionString, this.username, this.password));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new java.lang.RuntimeException(e);
        }
    }
}
