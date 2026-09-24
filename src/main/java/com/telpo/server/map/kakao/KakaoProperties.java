package com.telpo.server.map.kakao;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 카카오 REST API 키. 프론트에는 절대 노출하지 않고 서버 환경변수로만 주입한다.
 */
@ConfigurationProperties(prefix = "kakao")
public record KakaoProperties(String restApiKey) {
}
