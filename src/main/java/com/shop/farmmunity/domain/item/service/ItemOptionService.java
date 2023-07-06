package com.shop.farmmunity.domain.item.service;

import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.ItemOption;
import com.shop.farmmunity.domain.item.repository.ItemOptionRepository;
import com.shop.farmmunity.domain.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemOptionService {

    private final ItemOptionRepository itemOptionRepository;

    private final ItemRepository itemRepository;

    public void saveItemOption(ItemOption itemOption) {
        itemOptionRepository.save(itemOption);
    }

    public void updateItemOption(Item item, List<String> optionNameList, List<Integer> extraAmountList) {
        // 옵션 개수와 추가 가격 개수가 일치하는지 확인
        if (optionNameList.size() != extraAmountList.size()) {
            throw new IllegalArgumentException("옵션 개수와 추가 가격 개수가 일치하지 않습니다.");
        }

        // 아이템 엔티티에 연결된 옵션 삭제
        item.getItemOptionList().clear();

        // 옵션 엔티티의 원래 아이템에 해당하는 옵션 삭제
        itemOptionRepository.deleteAllByItemId(item.getId());

        // 새로운 옵션 추가
        for (int i = 0; i < optionNameList.size(); i++) {
            String optionName = optionNameList.get(i);
            Integer extraAmount = extraAmountList.get(i);

            ItemOption option = new ItemOption();
            option.setItem(item);
            option.updateItemOption(optionName, extraAmount);
            saveItemOption(option);
        }
    }

//    public void deleteItemOption()
}
