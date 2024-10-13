package itstep.learning.storage;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.services.filenames.FileNameService;
import org.apache.commons.fileupload.FileItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Singleton
public class LocalStorageService implements StorageService {
    private final static String storagePath = "D:/storage";
    private final FileNameService fileNameService;
    private final int bufferSize = 1024;

    @Inject
    public LocalStorageService(FileNameService fileNameService) {
        this.fileNameService = fileNameService;
    }

    @Override
    public File getFile(String fileName) {
       return  new File(storagePath, fileName);
    }

    @Override
    public String saveFile(FileItem fileItem) throws IOException {
        String extension = getString(fileItem);

        String savedName;
        File file;
        do {
            savedName = fileNameService.generateFilename(32) + extension;
            file = new File(storagePath, savedName);
        } while (file.exists());

        long size = fileItem.getSize();
        if( size > bufferSize ) {
            size = bufferSize;
        }
        byte[] buffer = new byte[(int)size];
        int len;
        try(FileOutputStream fos = new FileOutputStream(file)) {
            InputStream in = fileItem.getInputStream();
            while ((len = in.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        }

        return savedName;
    }

    private static String getString(FileItem fileItem) throws IOException {
        if( fileItem == null ) {
            throw new IOException("File is null");
        }
        if(fileItem.getSize() == 0) {
            throw new IOException("File is empty");
        }

        String fileName = fileItem.getName();
        if( fileName == null ) {
            throw new IOException("File name is null");
        }

        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new IOException("File name contains no extension");
        }
        String extension = fileName.substring(dotIndex);
        if(".".equals(extension)) {
            throw new IOException("File extension is empty");
        }
        return extension;
    }
}
