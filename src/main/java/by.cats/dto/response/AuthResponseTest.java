package by.cats.dto.response;

import lombok.Data;

@Data
public class AuthResponseTest {
    private String token;
    private Integer userId;
    private String nickname;
    private String avatarUrl;
}
