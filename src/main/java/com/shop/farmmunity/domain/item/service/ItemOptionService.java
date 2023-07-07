package com.shop.farmmunity.domain.item.service;

import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.ItemOption;
import com.shop.farmmunity.domain.item.repository.ItemOptionRepository;
import com.shop.farmmunity.domain.item.repository.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemOptionService {

    private final ItemOptionRepository itemOptionRepository;

    private final ItemRepository itemRepository;

    public void saveItemOption(List<String> optionNameList, List<Integer> extraAmountList, Item item) {
        for (int i = 0; i < optionNameList.size(); i++) {
            ItemOption itemOption = new ItemOption();
            itemOption.setItem(item);

            if (!(optionNameList.get(i).equals(""))) {
                String optName = optionNameList.get(i);
                int extraAmount = extraAmountList.get(i);

                itemOption.updateItemOption(optName, extraAmount);
                itemOptionRepository.save(itemOption);
            }
        }
    }

    public void updateItemOption(Item item, List<String> optionNameList, List<Integer> extraAmountList) {
        // 옵션 개수와 추가 가격 개수가 일치하는지 확인
        if (optionNameList.size() != extraAmountList.size()) {
            throw new IllegalArgumentException("옵션 개수와 추가 가격 개수가 일치하지 않습니다.");
        }

        List<ItemOption> itemOptionList = item.getItemOptionList();
        int diff = optionNameList.size() - itemOptionList.size();

        if (diff > 0) {
            createExtraItemOption(itemOptionList.size(), optionNameList.size() + diff - 1, item, optionNameList, extraAmountList);
        }

        for (int i = 0; i < itemOptionList.size(); i++) {
            itemOptionList.get(i).updateItemOption(optionNameList.get(i), extraAmountList.get(i));
        }
    }

    public void createExtraItemOption(int start, int end, Item item, List<String> optionNameList, List<Integer> extraAmountList) {

        for (int i = start; i < end; i++) {
            ItemOption itemOption = new ItemOption();
            itemOption.setItem(item);

            if (!(optionNameList.get(i).equals(""))) {
                String optName = optionNameList.get(i);
                int extraAmount = extraAmountList.get(i);

                itemOption.updateItemOption(optName, extraAmount);
                itemOptionRepository.save(itemOption);
            }
        }
    }


    public ItemOption findByItemOptionId(Long itemOptionId) {
        return itemOptionRepository.findById(itemOptionId).orElse(null);
    }

    public void deleteItemOption(Long optionId) {
        itemOptionRepository.deleteById(optionId);
    }
}
