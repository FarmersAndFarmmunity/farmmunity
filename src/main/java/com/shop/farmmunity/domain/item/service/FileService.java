package com.shop.farmmunity.domain.item.service;

import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@Log // 자동으로 로깅 코드를 생성해주는 기능을 제공
public class FileService {

    public String uploadFile(String uploadPath, String originalFileName,
                             byte[] fileData) throws Exception{
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName
                .lastIndexOf("."));
        // UUID로 받은 값과 원래 파일의 이름의 확장자를 조합해서 저장될 파일 이름을 만듭니다.
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData); // 매개변수로 입력받은 내용을 파일에 씀
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception { // 삭제할 파일 경로를 매개변수로 받음
        File deleteFile = new File(filePath); //파일이 저장된 경로를 이용하여 파일 객체를 생성

        if (deleteFile.exists()) {
            deleteFile.delete(); // 삭제할 파일이 존재하면 파일을 삭제
            log.info("파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
