package com.shop.farmmunity.domain.item.dto;

import com.shop.farmmunity.domain.item.constant.ItemClassifyStatus;
import com.shop.farmmunity.domain.item.constant.ItemSellStatus;
import com.shop.farmmunity.domain.item.entity.GroupBuying;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.itemTag.entity.ItemTag;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Getter
@Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stockNumber;

    @NotBlank(message = "상세 설명은 필수 입력 값입니다.")
    private String itemDetail;

    private ItemSellStatus itemSellStatus;

    private ItemClassifyStatus itemClassifyStatus;

    private Long postKeywordId;

    @NotBlank(message = "태그는 필수 입력 값입니다.")
    private String itemTagContents;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.EXTRA)
    Set<ItemTag> itemTags = new LinkedHashSet<>();

    private List<String> optionNameList = new ArrayList<>(); // 옵션 이름

    private List<Integer> extraAmountList = new ArrayList<>(); // 옵션 가격

    private List<Integer> gbPriceList = new ArrayList<>(); // 옵션 공동구매 가격

    private List<Integer> quantityList = new ArrayList<>(); // 옵션 수량

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>(); //수정 시, 상품 이미지 정보 저장하는 리스트

    private List<ItemOptionDto> itemOptionDtoList = new ArrayList<>(); // 아이템 옵션 정보를 저장

    private List<Long> itemImgIds = new ArrayList<>(); //수정 시, 상품의 이미지 아이디를 저장하는 리스트

    private int discount; // 공동구매 할인률

    private boolean active;

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem() {
        return modelMapper.map(this, Item.class);
    }

    public GroupBuying createGroupBuying() {
        return modelMapper.map(this, GroupBuying.class);
    }

    public static ItemFormDto of(Item item) {
        return modelMapper.map(item, ItemFormDto.class);
    }

    //수정을 위해
    public String getExtra_inputValue_itemTagContents() {
        return itemTags
                .stream()
                .map(itemTag -> "#" + itemTag.getItemKeyword().getContent())
                .sorted()
                .collect(Collectors.joining(" "));
    }

    public String getExtra_itemTagLinks() {
        return itemTags
                .stream()
                .map(itemTag -> {
                    String text = "#" + itemTag.getItemKeyword().getContent();

                    return """
                            <div class='badge badge-neutral'><a href="%s" class="text-link">%s</a></div>
                            """
                            .stripIndent()
                            .formatted(itemTag.getItemKeyword().getListUrl(), text);
                })
                .sorted()
                .collect(Collectors.joining(" "));
    }
}
