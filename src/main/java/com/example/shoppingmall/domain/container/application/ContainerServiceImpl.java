package com.example.shoppingmall.domain.container.application;

import com.example.shoppingmall.domain.container.dto.ContainerItemDTO;
import com.example.shoppingmall.domain.container.repository.ContainerItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ContainerServiceImpl implements ContainerService {
    private final ContainerItemRepository containerItemRepository;

    @Override
    public Page<ContainerItemDTO> getSellPlaceList(Pageable pageable) {
        return containerItemRepository.findAllPage(pageable)
                .map(ContainerItemDTO::changeDTO);
    }
}
