package com.shop.farmmunity.domain.item.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupService {

    @Value("${custom.groupMaxCount}")
    private int groupMaxCount;

}
