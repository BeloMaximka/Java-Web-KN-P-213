package itstep.learning.dal.dao;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dto.Token;
import itstep.learning.dal.dto.User;
import itstep.learning.services.db.DbService;
import itstep.learning.services.kdf.KdfService;

import java.sql.*;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

@Singleton
public class AuthDao {
    private final DbService dbService;
    private final Logger logger;
    private final KdfService kdfService;

    @Inject
    public AuthDao(DbService dbService, Logger logger, KdfService kdfService) {
        this.dbService = dbService;
        this.logger = logger;
        this.kdfService = kdfService;
    }

    public User authenticate( String login, String password ) {
        String sql = "SELECT * FROM users_access a " +
                " JOIN users u ON a.user_id = u.user_id " +
                " JOIN users_roles r ON a.role_id = r.role_id " +
                " LEFT JOIN tokens t ON u.user_id = t.user_id " +
                " WHERE a.login = ? ";
        try( PreparedStatement prep = dbService.getConnection().prepareStatement( sql ) ) {
            prep.setString( 1, login );
            ResultSet rs = prep.executeQuery();
            if( rs.next() ) {  // є такий login
                String salt = rs.getString( "salt" );
                String dk   = rs.getString( "dk"   );
                // повторюємо процедуру DK і перевіряємо чи збігаються результати перетворень
                if( kdfService.dk( password, salt ).equals( dk ) ) {
                    User user = new User( rs );
                    Token token ;
                    try {
                        token = new Token( rs );
                    }
                    catch( SQLException ignored ) {
                        token = null;
                    }
                    if( token == null ) {
                        token = this.createToken( user );
                    }
                    user.setToken( token );
                    return user;
                }
            }
        }
        catch( SQLException ex ) {
            logger.warning( ex.getMessage() + " -- " + sql );
        }
        return null;
    }

    public Token createToken( User user ) {
        Token token = new Token();
        token.setTokenId( UUID.randomUUID() );
        token.setUserId( user.getUserId() );
        token.setIat( new Date( System.currentTimeMillis() ) );
        token.setExp( new Date( System.currentTimeMillis() + 60000 ) );

        String sql = "INSERT INTO tokens ( token_id, user_id, iat, exp ) " +
                " VALUES ( ?, ?, ?, ? )";
        try( PreparedStatement prep = dbService.getConnection().prepareStatement( sql ) ) {
            prep.setString( 1, token.getTokenId().toString() );
            prep.setString( 2, token.getUserId().toString() );
            prep.setTimestamp( 3, new Timestamp( token.getIat().getTime() ) );
            prep.setTimestamp( 4, new Timestamp( token.getExp().getTime() ) );
            prep.executeUpdate();
            return token;
        }
        catch( SQLException ex ) {
            logger.warning( ex.getMessage() + " -- " + sql );
        }
        return null;
    }

    public boolean install() {
        try {
            CreateUsersTable();
            SeedUsersTable();
            CreateUsersAccessTable();
            SeedUsersAccessTable();
            CreateUserRolesTable();
            SeedUserRolesTable();
            CreateTokensTable();
            return true;
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return false;
        }
    }

    private void CreateUsersTable() throws SQLException {
        String sql = "CREATE TABLE  IF NOT EXISTS `users` (" +
                "`user_id`     CHAR(36)     PRIMARY KEY  DEFAULT( UUID() )," +
                "`user_name`   VARCHAR(64)  NOT NULL," +
                "`email`       VARCHAR(128) NOT NULL," +
                "`phone`       VARCHAR(16)      NULL," +
                "`avatar_url`  VARCHAR(16)      NULL," +
                "`birthdate`   DATETIME     NOT NULL," +
                "`delete_dt`   DATETIME         NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        executeStatement(sql);
    }

    private void SeedUsersTable() throws SQLException {
        String sql = "INSERT INTO `users`(`user_id`,`user_name`,`email`,`birthdate`,`delete_dt`) " +
                "VALUES ('7dd7d8a9-815e-11ef-bb48-fcfbf6dd7098','Administrator','admin@change.me','1970-01-01',NULL) " +
                "ON DUPLICATE KEY UPDATE " +
                "`user_name` = 'Administrator', " +
                "`email` = 'admin@change.me', " +
                "`birthdate` = '1970-01-01'," +
                "`delete_dt` = NULL";

        executeStatement(sql);
    }

    private void CreateUsersAccessTable() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `users_access` (" +
                "`access_id` CHAR(36) PRIMARY KEY DEFAULT (UUID())," +
                "`user_id`   CHAR(36) NOT NULL," +
                "`login`     VARCHAR(32) NOT NULL," +
                "`salt`      CHAR(16) NOT NULL," +
                "`dk`        CHAR(20) NOT NULL," +
                "`role_id`   CHAR(36) NULL," +
                "`is_active` TINYINT NULL" +
                ")ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        executeStatement(sql);
    }

    private void SeedUsersAccessTable() throws SQLException {
        String salt = "bb48fc1bf6dd70.4";
        String password = "root";
        String dk = kdfService.dk(password, salt);

        String sql = "INSERT INTO `users_access`(`access_id`,`user_id`,`role_id`,`login`,`salt`,`dk`,`is_active`) " +
                "VALUES ('5392cdda-815f-11ef-bb48-fcfbf6dd7098'," +
                "'7dd7d8a9-815e-11ef-bb48-fcfbf6dd7098', " +
                "'81661d9f-815d-11ef-bb48-fcfbf6dd7098'," +
                "'admin', ?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE " +
                "`user_id` = '7dd7d8a9-815e-11ef-bb48-fcfbf6dd7098', " +
                "`role_id` = '81661d9f-815d-11ef-bb48-fcfbf6dd7098', " +
                "`login` = 'admin'," +
                "`salt` = ?," +
                "`dk` = ?," +
                "`is_active` = 1";

        PreparedStatement prep = dbService.getConnection().prepareStatement(sql);
        prep.setString(1, salt);   // JDBC - відлік параметрів з 1
        prep.setString(2, dk);
        prep.setString(3, salt);
        prep.setString(4, dk);
        prep.executeUpdate();

    }

    private void CreateUserRolesTable() throws SQLException {
        String sql = "CREATE TABLE  IF NOT EXISTS `users_roles` (" +
                "`role_id`    CHAR(36)     PRIMARY KEY  DEFAULT( UUID() )," +
                "`role_name`  VARCHAR(36)  NOT NULL," +
                "`can_create` TINYINT      DEFAULT 0," +
                "`can_read`   TINYINT      DEFAULT 1," +
                "`can_update` TINYINT      DEFAULT 0," +
                "`can_delete` TINYINT      DEFAULT 0" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        executeStatement(sql);
    }

    private void SeedUserRolesTable() throws SQLException {
        String sql = "INSERT INTO `users_roles`(`role_id`,`role_name`,`can_create`,`can_read`,`can_update`,`can_delete`) " +
                "VALUES ('81661d9f-815d-11ef-bb48-fcfbf6dd7098','Administrator',1,1,1,1) " +
                "ON DUPLICATE KEY UPDATE " +
                "`role_name` = 'Administrator', " +
                "`can_create` = 1, `can_read` = 1,`can_update` = 1,`can_delete` = 1";

        executeStatement(sql);
    }

    private void CreateTokensTable() throws SQLException {
        String sql = "CREATE TABLE  IF NOT EXISTS `tokens` (" +
                "`token_id` CHAR(36)   PRIMARY KEY  DEFAULT( UUID() )," +
                "`user_id`  CHAR(36)   NOT NULL," +
                "`iat`      DATETIME   DEFAULT CURRENT_TIMESTAMP," +
                "`exp`      DATETIME   NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        executeStatement(sql);
    }

    private void executeStatement(String sqlStatement) throws SQLException {
        Statement stmt = dbService.getConnection().createStatement();
        stmt.executeUpdate(sqlStatement);
    }
}
