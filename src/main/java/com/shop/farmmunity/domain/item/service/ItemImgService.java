package com.shop.farmmunity.domain.item.service;

import com.shop.farmmunity.domain.item.entity.ItemImg;
import com.shop.farmmunity.domain.item.repository.ItemImgRepository;
import com.shop.farmmunity.domain.item.util.BuildFileName;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    private final ItemImgRepository itemImgRepository;

    private final S3FileService s3FileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) {
        String oriImgName = itemImgFile.getOriginalFilename();

        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!(oriImgName == null || "".equals(oriImgName))) { // TODO: isEmpty() -> Deprecated
            imgName = BuildFileName.buildFileName(oriImgName);
            imgUrl = s3FileService.uploadFile(itemImgFile, imgName);
        }

        itemImg.updateItemImg(oriImgName, imgName, imgUrl);
        itemImgRepository.save(itemImg);
    }


    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile)
            throws Exception {
        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
                s3FileService.deleteFile(savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = BuildFileName.buildFileName(oriImgName);
            String imgUrl = s3FileService.uploadFile(itemImgFile, imgName);
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
        }
    }

    public void deleteItemImg(Long itemImgId) {
        ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                .orElseThrow(EntityNotFoundException::new);

        if (!StringUtils.isEmpty(savedItemImg.getImgName())) {
            s3FileService.deleteFile(savedItemImg.getImgName());
        }
    }
}
