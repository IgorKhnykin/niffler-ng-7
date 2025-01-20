package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    public static AuthUserEntity fromJson(AuthUserJson authUser) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setId(authUser.id());
        authUserEntity.setUsername(authUser.username());
        authUserEntity.setPassword(authUser.password());
        authUserEntity.setEnabled(authUser.enabled());
        authUserEntity.setAccountNonExpired(authUser.accountNonExpired());
        authUserEntity.setAccountNonLocked(authUser.accountNonLocked());
        authUserEntity.setCredentialsNonExpired(authUser.credentialsNonExpired());
        return authUserEntity;
    }
}
