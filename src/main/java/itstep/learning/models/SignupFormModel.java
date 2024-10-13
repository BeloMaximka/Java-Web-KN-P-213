package itstep.learning.models;

import org.apache.commons.fileupload.FileItem;

import java.util.Date;

public class SignupFormModel {
    private String name;
    private String email;
    private String phone;
    private String login;
    private String password;
    private String repeat;
    private Date birthdate;
    private FileItem avatar;
}
