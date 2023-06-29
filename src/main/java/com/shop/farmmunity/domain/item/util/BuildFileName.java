package com.shop.farmmunity.domain.item.util;

import java.util.UUID;

public class BuildFileName {

    public static String buildFileName(String originalFileName) {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName
                .lastIndexOf("."));
        // UUID로 받은 값과 원래 파일의 이름의 확장자를 조합해서 저장될 파일 이름을 만듭니다.
        return uuid + extension;
    }
}
