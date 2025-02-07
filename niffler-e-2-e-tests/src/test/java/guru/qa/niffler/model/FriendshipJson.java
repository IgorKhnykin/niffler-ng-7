package guru.qa.niffler.model;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;

import java.nio.charset.StandardCharsets;
import java.util.Date;

public record FriendshipJson(
        UserJson requester,
        UserJson addressee,
        Date createdDate,
        FriendshipStatus status) {

    public static FriendshipJson fromEntity(FriendshipEntity fsEntity) {
        return new FriendshipJson(
                new UserJson(
                        fsEntity.getAddressee().getId(),
                        fsEntity.getAddressee().getUsername(),
                        fsEntity.getAddressee().getFirstname(),
                        fsEntity.getAddressee().getSurname(),
                        fsEntity.getAddressee().getFullname(),
                        fsEntity.getAddressee().getCurrency(),
                        fsEntity.getAddressee().getPhoto() != null && fsEntity.getAddressee().getPhoto().length > 0 ? new String(fsEntity.getAddressee().getPhoto(), StandardCharsets.UTF_8) : null,
                        fsEntity.getAddressee().getPhotoSmall() != null && fsEntity.getAddressee().getPhotoSmall().length > 0 ? new String(fsEntity.getAddressee().getPhotoSmall(), StandardCharsets.UTF_8) : null,
                        null),
                new UserJson(
                        fsEntity.getRequester().getId(),
                        fsEntity.getRequester().getUsername(),
                        fsEntity.getRequester().getFirstname(),
                        fsEntity.getRequester().getSurname(),
                        fsEntity.getRequester().getFullname(),
                        fsEntity.getRequester().getCurrency(),
                        fsEntity.getRequester().getPhoto() != null && fsEntity.getRequester().getPhoto().length > 0 ? new String(fsEntity.getRequester().getPhoto(), StandardCharsets.UTF_8) : null,
                        fsEntity.getRequester().getPhotoSmall() != null && fsEntity.getRequester().getPhotoSmall().length > 0 ? new String(fsEntity.getRequester().getPhotoSmall(), StandardCharsets.UTF_8) : null,
                        null
                ),
                fsEntity.getCreatedDate(),
                fsEntity.getStatus()
        );
    }
}
