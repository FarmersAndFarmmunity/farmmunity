package com.shop.farmmunity.domain.item.service;

import com.shop.farmmunity.domain.cart.service.CartService;
import com.shop.farmmunity.domain.item.constant.GroupBuyStatus;
import com.shop.farmmunity.domain.item.dto.*;
import com.shop.farmmunity.domain.item.entity.*;
import com.shop.farmmunity.domain.item.repository.*;
import com.shop.farmmunity.domain.itemTag.dto.ItemTagDto;
import com.shop.farmmunity.domain.itemTag.entity.ItemTag;
import com.shop.farmmunity.domain.itemTag.service.ItemTagService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemTagService itemTagService;
    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final ItemOptionService itemOptionService;
    private final GroupBuyingRepository groupBuyingRepository;
    private final GroupRepository groupRepository;

    // 등록
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList, String itemTagContents) throws Exception {
        GroupBuying groupBuying = itemFormDto.createGroupBuying();
        Item item = itemFormDto.createItem();

        groupBuyingRepository.save(groupBuying);
        item.setGroupBuying(groupBuying);

        itemRepository.save(item);
        itemTagService.applyItemTags(item, itemTagContents);
      
        // 아이템 옵션
        if (!CollectionUtils.isEmpty(itemFormDto.getOptionNameList()) || !CollectionUtils.isEmpty(itemFormDto.getExtraAmountList())) {
            itemOptionService.saveItemOption(itemFormDto.getOptionNameList(), itemFormDto.getExtraAmountList(), itemFormDto.getQuantityList(), itemFormDto.getGbPriceList(), item);
        }

        // 아이템 이미지
        for (int i = 0; i < itemImgFileList.size(); i++) { // itemImgFileList를 for문을 이용해 순회하여 처리
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0) { // 첫번 째 이미지면 "Y"를 아니라면 "N"를 부여
                itemImg.setRepImgYn("Y");
            } else {
                itemImg.setRepImgYn("N");
            }
            // 상품의 이미지 정보를 저장
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));
        }

        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        List<ItemOption> itemOptionList = itemOptionRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemOptionDto> itemOptionDtoList = new ArrayList<>();

        for(ItemOption itemOption : itemOptionList) {
            ItemOptionDto itemOptionDto = ItemOptionDto.of(itemOption);
            itemOptionDtoList.add(itemOptionDto);
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        itemFormDto.setItemOptionDtoList(itemOptionDtoList);
        itemFormDto.setDiscount(item.getGroupBuying().getDiscount());
        return itemFormDto;
    }

    // 수정
    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList, String itemTagContents) throws Exception {
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        if (!CollectionUtils.isEmpty(itemFormDto.getOptionNameList()) || !CollectionUtils.isEmpty(itemFormDto.getExtraAmountList())) {
            itemOptionService.updateItemOption(item, itemFormDto.getOptionNameList(), itemFormDto.getExtraAmountList(), itemFormDto.getQuantityList(), itemFormDto.getGbPriceList());
        }

        List<Long> itemImgIds = itemFormDto.getItemImgIds();

        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
        }

        itemTagService.applyItemTags(item, itemTagContents);

        return item.getId();
    }

    // 삭제
    public void deleteItem(Long itemId) throws Exception {
        List<ItemImg> itemImgs = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        for (int i = 0; i < itemImgs.size(); i++) {
            itemImgService.deleteItemImg(itemImgs.get(i).getId());
        }

        itemRepository.deleteById(itemId);
        itemImgRepository.deleteAllByItemId(itemId);
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> getMyItemPage(ItemSearchDto itemSearchDto, Pageable pageable, String email) {
        return itemRepository.getMyItemPage(itemSearchDto, pageable, email);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemClassifyDto itemClassifyDto, ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.getMainItemPage(itemClassifyDto, itemSearchDto, pageable);
    }

    public Optional<Item> findById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    public GroupBuyDto getGroupBuyInfo(Long itemId) {
        GroupBuyDto groupBuyDto = new GroupBuyDto();
        Group group = groupRepository.findByItemIdAndStatus(itemId, GroupBuyStatus.WAIT);
        groupBuyDto.setCount(groupRepository.countByItemIdAndStatus(itemId, GroupBuyStatus.SUCCESS));
        if (group != null) {
            groupBuyDto.setUsername(group.getMember().getUsername());
            groupBuyDto.setMatchEndTime(group.getGroupBuyEndTime().toString().substring(0,19).replace("T", " "));
        }
        return groupBuyDto;
    }

    public List<GroupBuyDto> getGroupBuyList(Long itemId) {
        List<GroupBuyDto> groupBuyDtos = new ArrayList<>();
        List<Group> groups = groupRepository.findByItemIdAndStatusAndIsHost(itemId, GroupBuyStatus.SUCCESS, true);
        for(Group group : groups){
            GroupBuyDto groupBuyDto = new GroupBuyDto();
            groupBuyDto.setUsername(group.getMember().getUsername());
            groupBuyDto.setPartnerUsername(group.getPartnerMember().getUsername());
            groupBuyDto.setMatchedTime(group.getGroupBuyMatchedTime().toString().substring(0, 19).replace("T", " "));

            groupBuyDtos.add(groupBuyDto);
        }
        return groupBuyDtos;
    }

    public List<ItemTagDto> getItemTags(String itemTagContent) {
        List<ItemTag> itemTags = itemTagService.getItemTags(itemTagContent);

        return itemTags.stream()
                .map(itemTag -> {
                    ItemImg repImgYn = itemImgRepository.findByItemIdAndRepImgYn(itemTag.getItem().getId(), "Y");
                    String imgUrl = repImgYn.getImgUrl();
                    ItemTagDto itemTagDto = ItemTagDto.of(itemTag.getItem());
                    itemTagDto.setImgUrl(imgUrl);
                    return itemTagDto;
                })
                .collect(Collectors.toList());
    }

}

