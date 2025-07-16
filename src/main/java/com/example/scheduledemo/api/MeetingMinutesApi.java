package com.example.scheduledemo.api;

import com.example.scheduledemo.api.vo.*;
import com.example.scheduledemo.entity.MeetingMinutesDTO;
import com.example.scheduledemo.service.MeetingMinutesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meeting-minutes")
public class MeetingMinutesApi implements MeetingMinutesDoc {

    @Autowired
    private MeetingMinutesService meetingMinutesService;

    @GetMapping("/{id}")
    @Override
    public APIResultVO<MeetingMinutesVO> getMeetingMinutesById(@PathVariable Long id) {
        MeetingMinutesDTO dto = meetingMinutesService.getMeetingMinutesById(id);
        return APIResultVO.success(VOMapper.INSTANCE.toVO(dto));
    }

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

    @Override
    @PostMapping(value = "/generate")
    public APIResultVO<String> generateMeetingMinutes(@ModelAttribute GenerateMeetingMinutesVO vo) throws Exception {
        String meetingMinutes = meetingMinutesService.generateMeetingMinutes(vo.getUnionId(), vo.getCalendarId(), vo.getEventId());
        return APIResultVO.success(meetingMinutes);
    }

}
