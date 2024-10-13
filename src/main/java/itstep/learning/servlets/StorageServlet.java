package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.storage.StorageService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

@Singleton
public class StorageServlet extends HttpServlet {
    private final StorageService storageService;

    @Inject
    public StorageServlet(StorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if(pathInfo == null || pathInfo.isEmpty() || pathInfo.equals("/")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = this.storageService.getFile(pathInfo.substring(1));
        if(file == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        resp.setContentType(ResolveContentType(file.getName()));
        long size= file.length();
        if(size > 4096) {
            size = 4096;
        }
        byte[] buffer = new byte[(int)size];
        int len;
        try (FileInputStream fix = new FileInputStream(file); OutputStream out = resp.getOutputStream();) {
            while ((len = fix.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        }
    }

    private String ResolveContentType(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        String extension = dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
        switch (extension) {
            case "jpg": extension = "jpeg"; break;
            case "jpeg":
            case "png":
            case "bmp":
            case "gif":
                return "image/" + extension;
        }
        return extension;
    }
}
