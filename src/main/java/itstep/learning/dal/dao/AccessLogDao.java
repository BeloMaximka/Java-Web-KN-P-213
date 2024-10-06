package itstep.learning.dal.dao;

import com.google.inject.Inject;
import itstep.learning.services.db.DbService;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

public class AccessLogDao {
    private final DbService dbService;
    private final Logger logger;

    @Inject
    public AccessLogDao(DbService dbService, Logger logger) {
        this.dbService = dbService;
        this.logger = logger;
    }

    public boolean install() {
        try {
            CreateAccessLogsTable();
            return true;
        } catch (SQLException e) {
            logger.warning(e.getMessage());
            return false;
        }
    }

    private void CreateAccessLogsTable() throws SQLException {
        String sql = "CREATE TABLE  IF NOT EXISTS `accessLogs` (" +
                "`user_id`   CHAR(36)      PRIMARY KEY  DEFAULT( UUID() )," +
                "`page_path` VARCHAR(2000) NOT NULL," +
                "`timestamp` DATETIME      NOT NULL" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        Statement stmt = dbService.getConnection().createStatement();
        stmt.executeUpdate(sql);
    }
}
