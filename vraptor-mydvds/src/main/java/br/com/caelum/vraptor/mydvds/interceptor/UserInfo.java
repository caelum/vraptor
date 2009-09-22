package br.com.caelum.vraptor.mydvds.interceptor;

import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.caelum.vraptor.mydvds.model.User;

/**
 * Represents the user logged in the system.
 * @author Caio Filipini
 */
@Component
@SessionScoped
public class UserInfo {

    public static final String CURRENT_USER = "currentUser";
    private final HttpSession session;
    private User user;

    public UserInfo(HttpSession session) {
        this.session = session;
        refreshUser();
    }

    public User getUser() {
        return user;
    }

    public void login(User user) {
        this.user = user;
        session.setAttribute(CURRENT_USER, this.user);
    }

    private void refreshUser() {
        this.user = (User) session.getAttribute(CURRENT_USER);
    }

    public void logout() {
        session.setAttribute(CURRENT_USER, null);
        this.user = null;
    }

}
