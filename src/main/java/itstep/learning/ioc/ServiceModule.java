package itstep.learning.ioc;

import com.google.inject.AbstractModule;
import itstep.learning.dal.dao.AuthDao;
import itstep.learning.services.db.DbService;
import itstep.learning.services.db.MySqlDbService;
import itstep.learning.services.filenames.FileNameService;
import itstep.learning.services.filenames.RandomFileNameService;
import itstep.learning.services.hash.HashService;
import itstep.learning.services.hash.Md5HashService;
import itstep.learning.services.kdf.KdfService;
import itstep.learning.services.kdf.PbKdf1Service;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(HashService.class).to(Md5HashService.class);
        bind(KdfService.class).to(PbKdf1Service.class);
        bind(FileNameService.class).to(RandomFileNameService.class);
        bind(DbService.class).to(MySqlDbService.class);
    }
}
