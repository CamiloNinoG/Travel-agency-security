package cng.ms_security.Models;
import java.util.Date;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
// import cng.ms_security.Models.User;

@Data
@Document
public class Session {

    @Id
    private String _id;

    private String token;
    private String code2FA;
    private Date expiration;
    @DBRef
    private User user;

    public Session(String token, String code2FA, Date expiration) {
        this.token = token;
        this.code2FA = code2FA;
        this.expiration = expiration;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode2FA() {
        return code2FA;
    }

    public void setCode2FA(String code2FA) {
        this.code2FA = code2FA;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
