package com.mybooks.batch.config.key;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * packageName    : store.mybooks.resource.config
 * fileName       : KeyResponseDto
 * author         : Fiat_lux
 * date           : 2/20/24
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2/20/24        Fiat_lux       최초 생성
 */
@Getter
public class KeyResponseDto {
    private Header header;
    private Body body;

    @Getter
    @NoArgsConstructor
    public static class Body {
        private String secret;
    }

    @Getter
    @NoArgsConstructor
    public static class Header {
        private Integer resultCode;
        private String resultMessage;
        private boolean isSuccessful;
    }
}