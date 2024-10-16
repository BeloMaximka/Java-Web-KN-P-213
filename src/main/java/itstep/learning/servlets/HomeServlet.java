package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.AccessLogDao;
import itstep.learning.dal.dao.AuthDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class HomeServlet extends HttpServlet {
    private final AccessLogDao accessLogDao;
    private final AuthDao authDao;

    @Inject
    HomeServlet(AccessLogDao accessLogDao, AuthDao authDao) {
        this.accessLogDao = accessLogDao;
        this.authDao = authDao;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String dbMessage = accessLogDao.install() && authDao.install() ? "AccessLogDao install OK" : "Uninstall FAILED";

        req.setAttribute("hash", dbMessage);
        req.setAttribute("body", "home.jsp");
        req.getRequestDispatcher("WEB-INF/views/_layout.jsp").forward(req, resp);
    }
}

/*
Сервлети - спеціалізовані класи для мережних задач, зокрема,
HttpServlet - для веб-задач, є аналогом контролерів в ASP
 */
