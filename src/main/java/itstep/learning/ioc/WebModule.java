package itstep.learning.ioc;

import com.google.inject.servlet.ServletModule;
import itstep.learning.filters.CharsetFilter;
import itstep.learning.filters.SecurityFilter;
import itstep.learning.servlets.HomeServlet;
import itstep.learning.servlets.WebXmlServlet;

public class WebModule  extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(CharsetFilter.class);
        filter("/*").through(SecurityFilter.class);

        serve("/").with(HomeServlet.class);
        serve("/web-xml").with(WebXmlServlet.class);
    }
}
