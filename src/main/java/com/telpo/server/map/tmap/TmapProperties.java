package com.telpo.server.map.tmap;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SK open API(Tmap) appKey. 프론트에는 절대 노출하지 않고 서버 환경변수로만 주입한다.
 */
@ConfigurationProperties(prefix = "tmap")
public record TmapProperties(String appKey) {
}
