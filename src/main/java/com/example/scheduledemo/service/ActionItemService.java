package com.example.scheduledemo.service;

import com.example.scheduledemo.repository.ActionItemRepository;
import com.example.scheduledemo.repository.entity.ActionItemEntity;
import com.example.scheduledemo.service.dto.ActionItemDTO;
import com.example.scheduledemo.service.dto.DTOMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActionItemService
{

    @Autowired
    ActionItemRepository actionItemRepository;


    public List<ActionItemDTO> getActionItems()
    {
        List<ActionItemEntity> items = actionItemRepository.findAll();
        return items.stream().map(DTOMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    public ActionItemDTO getActionItemById(Long id)
    {
        ActionItemEntity actionItemNotFound = actionItemRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Action item not found")
                                                                                           );
        return DTOMapper.INSTANCE.toDto(actionItemNotFound);
    }
}
