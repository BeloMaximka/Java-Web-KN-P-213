package itstep.learning.services.form;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class MixedFormParseService implements FormParseService {
    private final static int memoryLimit = 3 * 1024 * 1024;
    private final static int maxSingleFile = 2 * 1024 * 1024;
    private final static int maxFormFile = 5 * 1024 * 1024;
    private final ServletFileUpload servletFileUpload;

    @Inject
    public MixedFormParseService() {
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(memoryLimit);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        servletFileUpload = new ServletFileUpload(factory);
        servletFileUpload.setFileSizeMax(maxSingleFile);
        servletFileUpload.setSizeMax(maxFormFile);
    }

    @Override
    public FormParseResult parse(HttpServletRequest request) {
        final Map<String, String> formFields = new HashMap<>();
        final Map<String, FileItem> formFiles = new HashMap<>();

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            try {
                for (FileItem fileItem : servletFileUpload.parseRequest(request)) {
                    if (fileItem.isFormField()) {
                        formFields.put(fileItem.getFieldName(), fileItem.getString());
                    }
                    else {
                        formFiles.put(fileItem.getFieldName(), fileItem);
                    }
                }
            }
             catch (FileUploadException e) {
             }
        }
        else {
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                formFields.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        return new FormParseResult() {
            @Override public Map<String, String> getFields() { return formFields; }
            @Override public Map<String, FileItem> getFiles() { return formFiles; }
        };
    }

}
