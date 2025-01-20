package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.AuthAuthorityJson;
import guru.qa.niffler.model.Authority;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthAuthorityEntity implements Serializable {
    private UUID id;
    private Authority authority;
    private AuthUserEntity user;

    public static AuthAuthorityEntity fromJson(AuthAuthorityJson authorityJson) {
        AuthAuthorityEntity authorityEntity = new AuthAuthorityEntity();
        authorityEntity.setId(authorityJson.id());
        authorityEntity.setUser(AuthUserEntity.fromJson(authorityJson.userJson()));
        authorityEntity.setAuthority(authorityJson.authority());
        return authorityEntity;
    }
}
