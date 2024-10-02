package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.filenames.FileNameService;
import itstep.learning.services.hash.HashService;
import itstep.learning.services.kdf.KdfService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Singleton
public class HomeServlet extends HttpServlet {
    private final HashService hashService;
    private final KdfService kdfService;
    private final FileNameService fileNameService;

    @Inject
    HomeServlet(HashService hashService, KdfService kdfService, FileNameService fileNameService) {
        this.hashService = hashService;
        this.kdfService = kdfService;
        this.fileNameService = fileNameService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] filenames = new String[10];
        filenames[0] = fileNameService.generateFilename(null);
        for (int i = 1; i < 10; i++) {
            filenames[i] = fileNameService.generateFilename(10 + i);
        }
        req.setAttribute("filenames", filenames);

        req.setAttribute("hash", hashService.hash("123") + " " + kdfService.dk("password", "salt.4") + " " + kdfService.hashCode());
        req.setAttribute("body", "home.jsp");
        req.getRequestDispatcher("WEB-INF/views/_layout.jsp").forward(req, resp);
    }
}

/*
Сервлети - спеціалізовані класи для мережних задач, зокрема,
HttpServlet - для веб-задач, є аналогом контролерів в ASP
 */
