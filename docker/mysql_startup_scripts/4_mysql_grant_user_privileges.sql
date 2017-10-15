/*
	Grant appropriate privileges for web_app user.
*/
REVOKE ALL PRIVILEGES, GRANT OPTION FROM 'web_app'@'%'; -- since web_app is created using $MYSQL_USER and $MYSQL_PASSWORD, it has GRANT ALL applied which isn't good.
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE on wedding_jonfreer_com.* TO 'web_app'@'%'; -- adding back the privileges that are more restrictive.
GRANT SELECT ON mysql.* TO 'web_app'@'%'; -- needed in order to execute stored procedures.
FLUSH PRIVILEGES;

