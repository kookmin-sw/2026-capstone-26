package backend.capstone.domain.user.mapper;

import backend.capstone.domain.user.entity.ProviderType;
import backend.capstone.domain.user.entity.User;
import backend.capstone.integration.kakao.auth.dto.KakaoUserInfoResponse;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class UserMapper {

    public static User toEntity(KakaoUserInfoResponse kakaoUser) {
        return User.builder()
            .provider(ProviderType.KAKAO)
            .providerId(kakaoUser.id())
            .nickname(kakaoUser.kakao_account().profile().nickname())
            .profileImageUrl(kakaoUser.kakao_account().profile().profile_image_url())
            .build();
    }

}
