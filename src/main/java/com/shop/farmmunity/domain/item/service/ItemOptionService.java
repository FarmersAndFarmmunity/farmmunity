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

    public void saveItemOption(List<String> optionNameList, List<Integer> extraAmountList, List<Integer> quantityList, List<Integer> gbPriceList, Item item) {
        // 옵션이 정확히 기입되었는지 확인
        validateInputOption(optionNameList, extraAmountList, quantityList, gbPriceList);

        for (int i = 0; i < optionNameList.size(); i++) {
            ItemOption itemOption = new ItemOption();
            itemOption.setItem(item);

            if (!(optionNameList.get(i).equals(""))) {
                itemOption.updateItemOption(optionNameList.get(i), extraAmountList.get(i), quantityList.get(i), gbPriceList.get(i));
                itemOptionRepository.save(itemOption);
            }
        }
    }

    public void updateItemOption(Item item, List<String> optionNameList, List<Integer> extraAmountList, List<Integer> quantityList, List<Integer> gbPriceList) {
        // 옵션이 정확히 기입되었는지 확인
        validateInputOption(optionNameList, extraAmountList, quantityList, gbPriceList);

        List<ItemOption> itemOptionList = item.getItemOptionList();
        int diff = optionNameList.size() - itemOptionList.size();

        if (diff > 0) {
            createExtraItemOption(itemOptionList.size(), itemOptionList.size() + diff, item, optionNameList, extraAmountList, quantityList, gbPriceList);
        }

        for (int i = 0; i < itemOptionList.size(); i++) {
            itemOptionList.get(i).updateItemOption(optionNameList.get(i), extraAmountList.get(i), quantityList.get(i), gbPriceList.get(i));
        }
    }

    public void createExtraItemOption(int start, int end, Item item, List<String> optionNameList, List<Integer> extraAmountList, List<Integer> quantityList, List<Integer> gbPriceList) {

        for (int i = start; i < end; i++) {
            ItemOption itemOption = new ItemOption();
            itemOption.setItem(item);

            if (!(optionNameList.get(i).equals(""))) {
                itemOption.updateItemOption(optionNameList.get(i), extraAmountList.get(i), quantityList.get(i), gbPriceList.get(i));
                itemOptionRepository.save(itemOption);
            }
        }
    }

    public void validateInputOption(List<String> optionNameList, List<Integer> extraAmountList, List<Integer> quantityList, List<Integer> gbPriceList) {

        if (optionNameList.size() - extraAmountList.size() - gbPriceList.size() != extraAmountList.size() - quantityList.size() - gbPriceList.size()) {
            throw new IllegalArgumentException("옵션을 정확히 기입해주세요.");
        }
    }

    public ItemOption findByItemOptionId(Long itemOptionId) {
        return itemOptionRepository.findById(itemOptionId).orElse(null);
    }

    public void deleteItemOption(Long optionId) {
        itemOptionRepository.deleteById(optionId);
    }
}
