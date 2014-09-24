package mcupdater.download;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class DLAuthenticator extends Authenticator {

    private String userName;
    private char[] password;

    public DLAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password.toCharArray();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}
