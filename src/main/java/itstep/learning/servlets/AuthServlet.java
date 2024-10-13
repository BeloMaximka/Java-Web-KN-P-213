package itstep.learning.servlets;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import itstep.learning.dal.dao.AuthDao;
import itstep.learning.dal.dto.User;
import itstep.learning.rest.RestMetaData;
import itstep.learning.rest.RestServlet;
import itstep.learning.services.form.FormParseResult;
import itstep.learning.services.form.FormParseService;
import itstep.learning.storage.StorageService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;

@Singleton
public class AuthServlet extends RestServlet {
    private final AuthDao authDao;
    private final FormParseService formParseService;
    private final StorageService storageService;

    @Inject
    public AuthServlet(AuthDao authDao, FormParseService formParseService, StorageService storageService) {
        this.authDao = authDao;
        this.formParseService = formParseService;
        this.storageService = storageService;
    }

    @Override
    protected  void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.restResponse.setMeta(new RestMetaData()
                .setUri("/auth")
                .setMethod(req.getMethod())
                .setName("KN-P-213 Authentication API")
                .setServerTime( new Date())
                .setAllowedMethods(new String[]{"GET", "POST"})
        );
        super.service(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        FormParseResult formParseResult = formParseService.parse(req);
        String savedName = storageService.saveFile(formParseResult.getFiles().get("signup-avatar"));
        super.sendResponse("files: " + formParseResult.getFiles().size() + ", fields: " + formParseResult.getFields().size() + ", name:" + savedName);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        /*
            The 'Basic' HTTP Authentication Scheme
            https://datatracker.ietf.org/doc/html/rfc7617
         */
        // Вилучаємо заголовок Authorization
        // Перевіряємо, що схема Basic
        // Виділяємо дані автентифікації (credentials)
        // Декодуємо їх за Base64
        // Розділяємо за першим символом ':'
        // запитуємо автентифікацію в DAO
        try {
            // Вилучаємо заголовок Authorization
            String authHeader = req.getHeader( "Authorization" );
            if( authHeader == null ) {
                throw new ParseException( "Authorization header not found", 401 );
            }
            // Перевіряємо, що схема Basic
            String authScheme = "Basic ";  // trailing space - the part of standard
            if( ! authHeader.startsWith( authScheme ) ) {
                throw new ParseException( "Invalid Authorization scheme. Required " + authScheme, 400 );
            }
            // Виділяємо дані автентифікації (credentials)
            String credentials = authHeader.substring( authScheme.length() );
            // Декодуємо їх за Base64
            String decodedCredentials;
            try {
                decodedCredentials = new String(
                        Base64.getUrlDecoder().decode( credentials.getBytes( StandardCharsets.UTF_8 ) ),
                        StandardCharsets.UTF_8
                );
            }
            catch( IllegalArgumentException ignored ) {
                throw new ParseException( "Invalid credentials format", 400 );
            }
            // Розділяємо за першим символом ':'
            String[] parts = decodedCredentials.split( ":", 2 );
            if( parts.length != 2 ) {
                throw new ParseException( "Invalid credentials composition", 400 );
            }

            User user = authDao.authenticate( parts[0], parts[1] );
            if( user == null ) {
                throw new ParseException( "Credentials rejected", 401 );
            }

            restResponse.setData( user);
            sendResponse(200);
        }
        catch( ParseException ex ) {
            restResponse.setData( ex.getMessage());
            sendResponse(ex.getErrorOffset());
        }
    }


}
/*
Д.З. Створити сторінку для автоматизованого тестування АРІ
У кодах сторінки надсилаються різні запити на /auth
 як правильні, так і такі, що містять помилки
 і виводяться відповіді на них

[Auth]
Without 'Authorization' header: {code: 401, status: 'error', data: 'Authorization header not found'}
With non-Basic scheme: {...}
Correct with login '234' and password '123': {code: 200, status: 'success', data: '234:123'}

** Відповіді, що відповідають очікуванням, позначати зеленим кольором, інші - червоним

Встановити Oracle XE
https://www.oracle.com/database/technologies/appdev/xe/quickstart.html
 */