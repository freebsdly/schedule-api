package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.*;
import com.example.scheduledemo.service.MeetingMinutesService;
import com.example.scheduledemo.service.ToolService;
import com.example.scheduledemo.service.dto.MeetingMinutesDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-minutes")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class MeetingMinutesApi implements MeetingMinutesDoc {

    private final MeetingMinutesService meetingMinutesService;

    private final ToolService toolService;

    @GetMapping(value = "")
    @Override
    public APIResultVO<List<MeetingMinutesVO>> getMeetingMinutes() {
        List<MeetingMinutesDTO> list = meetingMinutesService.getMeetingMinutes();
        return APIResultVO.success(list.stream().map(VOMapper.INSTANCE::toVO).toList());
    }

    @PostMapping(value = "", consumes = {"application/json"})
    @Override
    public APIResultVO<MeetingMinutesVO> createMeetingMinutes(@RequestBody MeetingMinutesAddVO vo) {
        MeetingMinutesDTO dto = VOMapper.INSTANCE.toDTO(vo);
        MeetingMinutesDTO result = meetingMinutesService.createMeetingMinutes(dto);
        return APIResultVO.success(VOMapper.INSTANCE.toVO(result));
    }

    @PutMapping(value = "", consumes = {"application/json"})
    @Override
    public APIResultVO<MeetingMinutesVO> updateMeetingMinutes(@RequestBody MeetingMinutesUpdateVO vo) {
        MeetingMinutesDTO dto = VOMapper.INSTANCE.toDTO(vo);
        MeetingMinutesDTO result = meetingMinutesService.updateMeetingMinutes(dto);
        return APIResultVO.success(VOMapper.INSTANCE.toVO(dto));
    }

    @DeleteMapping("/{id}")
    @Override
    public APIResultVO<Long> deleteMeetingMinutes(@PathVariable Long id) {
        meetingMinutesService.deleteMeetingMinutes(id);
        return APIResultVO.success(id);
    }

}
