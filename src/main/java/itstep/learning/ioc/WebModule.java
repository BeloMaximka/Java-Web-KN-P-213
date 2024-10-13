package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.filters.CharsetFilter;
import itstep.learning.filters.SecurityFilter;
import itstep.learning.servlets.*;

public class WebModule  extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/shop").through(CharsetFilter.class);
        filter("/*").through(SecurityFilter.class);

        serve("/").with(HomeServlet.class);
        serve( "/auth"    ).with( AuthServlet.class   );
        serve("/web-xml").with(WebXmlServlet.class);
        serve("/shop").with(ShopServlet.class);
        serve("/storage/*").with(StorageServlet.class);
    }
}
