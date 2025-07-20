package com.example.scheduledemo.service;

import com.aliyun.dingtalkcalendar_1_0.models.*;
import com.aliyun.dingtalkconference_1_0.models.*;
import com.aliyun.dingtalkdoc_1_0.models.*;
import com.aliyun.dingtalkim_1_0.models.*;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkrobot_1_0.models.*;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.request.OapiV2DepartmentListsubRequest;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.example.scheduledemo.feignclients.DingTalkFeignClient;
import com.example.scheduledemo.feignclients.EmployeeQuery;
import com.example.scheduledemo.feignclients.EmployeeResponseDTO;
import com.example.scheduledemo.repository.DepartmentRepository;
import com.example.scheduledemo.repository.EmployeeRepository;
import com.example.scheduledemo.repository.entity.DepartmentEntity;
import com.example.scheduledemo.repository.entity.EmployeeEntity;
import com.example.scheduledemo.service.dto.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DingTalkService
{

    @Autowired
    private com.aliyun.dingtalkcalendar_1_0.Client calendarClient;

    @Autowired
    private com.aliyun.dingtalkconference_1_0.Client conferenceClient;

    @Autowired
    private com.aliyun.dingtalkoauth2_1_0.Client oauth2Client;

    @Autowired
    private com.aliyun.dingtalkdoc_1_0.Client docClient;

    @Autowired
    private com.aliyun.dingtalkrobot_1_0.Client robotClient;

    @Autowired
    private com.aliyun.dingtalkim_1_0.Client imClient;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DingTalkFeignClient dingTalkFeignClient;

    @Getter
    @Value("${dingtalk.ak}")
    private String appKey;

    @Value("${dingtalk.sk}")
    private String appSecret;

    @Value("${dingtalk.robot.code}")
    private String robotCode;

    @Value("${dingtalk.connector.base-url}")
    private String dingTalkConnectorBaseUrl;

    private String accessToken;

    private boolean syncDepartmentRunning = false;
    private boolean syncEmployeeRunning = false;

    @Autowired
    private EmployeeRepository employeeRepository;

    public String getAccessToken() throws Exception
    {
        if (accessToken != null) {
            return accessToken;
        }
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest().setAppKey(appKey).setAppSecret(
                appSecret);
        GetAccessTokenResponse resp = oauth2Client.getAccessToken(getAccessTokenRequest);
        accessToken = resp.getBody().getAccessToken();
        return accessToken;
    }

    /**
     * FIXME: 按照token的过期时间刷新token
     */
    void updateAccessToken()
    {

    }

    @Async
    public synchronized void syncDepartments() throws Exception
    {
        if (syncDepartmentRunning) {
            throw new Exception("sync department task already running");
        }
        syncDepartmentRunning = true;
        Long startId = 1L;
        if (!departmentRepository.existsById(startId)) {
            log.debug("current department (id={}) does not exist, create it", startId);
            DepartmentEntity root = new DepartmentEntity(null, "root", startId, null);
            departmentRepository.saveAndFlush(root);
        }

        Deque<Long> departmentStack = new ArrayDeque<>();
        departmentStack.push(startId);

        while (!departmentStack.isEmpty()) {
            Long currentId = departmentStack.pop();
            List<OapiV2DepartmentListsubResponse.DeptBaseResponse> subDepartments = getSubDepartments(currentId);

            subDepartments.forEach(e -> {
                Optional<DepartmentEntity> exist = departmentRepository.findByDingTalkDepartmentId(e.getDeptId());
                if (exist.isPresent()) {
                    DepartmentEntity entity = exist.get();
                    entity.setName(e.getName());
                    entity.setDingTalkParentId(e.getParentId());
                    entity.setDingTalkDepartmentId(e.getDeptId());
                    departmentRepository.save(entity);
                } else {
                    log.debug("department (id=1) does not exist, create it");
                    DepartmentEntity entity = DTOMapper.INSTANCE.toEntity(e);
                    departmentRepository.saveAndFlush(entity);
                }

                departmentStack.push(e.getDeptId());
            });
        }
        syncDepartmentRunning = false;
    }

    @Async
    public synchronized void syncEmployees() throws Exception
    {
        if (syncEmployeeRunning) {
            throw new Exception("sync department task already running");
        }
        syncEmployeeRunning = true;
        String token = getAccessToken();
        departmentRepository.findAll().forEach(e -> {
            boolean haveNext = true;
            EmployeeQuery query = new EmployeeQuery(e.getDingTalkDepartmentId(), 0L, 100);
            while (haveNext) {
                EmployeeResponseDTO dto = dingTalkFeignClient.getDepartmentEmployees(token, query);
                if (dto.getErrorCode() != 0) {
                    log.error(
                            "get department (id:{}) employee error: {}",
                            e.getDingTalkDepartmentId(),
                            dto.getErrorMessage()
                             );
                    return;
                }
                dto.getResult().getEmployees().forEach(employee -> {
                    Optional<EmployeeEntity> exist = employeeRepository.findByUnionId(employee.getUnionId());

                    if (exist.isPresent()) {
                        // TODO: 使用mapstruct更新
                        EmployeeEntity entity = exist.get();
                        entity.setName(employee.getName());
                        entity.setEmail(employee.getEmail());
                        entity.setDepartment(e);
                        employeeRepository.saveAndFlush(entity);
                    } else {
                        EmployeeEntity entity = DTOMapper.INSTANCE.toEntity(employee);
                        entity.setDepartment(e);
                        employeeRepository.saveAndFlush(entity);
                    }

                });
                haveNext = dto.getResult().isHasMore();
                query.setCursor(dto.getResult().getNextCursor());
            }
        });
        syncDepartmentRunning = false;
    }

    public QueryCloudRecordTextResponseBody getCloudRecordText(String unionId, String conferenceId, Long nextToken)
            throws Exception
    {
        String token = getAccessToken();
        QueryCloudRecordTextHeaders headers = new QueryCloudRecordTextHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        QueryCloudRecordTextRequest request = new QueryCloudRecordTextRequest();
        request.setUnionId(unionId);
        request.setNextToken(nextToken);
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        QueryCloudRecordTextResponse resp = conferenceClient.queryCloudRecordTextWithOptions(
                conferenceId,
                request,
                headers,
                runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("get cloud record text failed. code: {}", resp.getStatusCode());
            throw new Exception("Failed to get cloud record text info.");
        }
        return resp.getBody();
    }

    public String getCloudRecordAllText(String unionId, String conferenceId) throws Exception
    {
        QueryCloudRecordTextResponseBody body = getCloudRecordText(unionId, conferenceId, null);

        //FIXME: 考虑hasMore情况
        List<String> list = body.getParagraphList()
                .stream()
                .map(QueryCloudRecordTextResponseBody.QueryCloudRecordTextResponseBodyParagraphList::getParagraph)
                .toList();

        return String.join("\n", list);
    }

    /**
     * TODO: 按照会议ID获取会议详情，并根据由于云录制状态过滤
     *
     * @param ids
     * @return
     * @throws Exception
     */
    public String filterConferenceIdsByRecordStatus(List<String> ids) throws Exception
    {
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        throw new Exception("event have more than 1 conference id");
    }

    public RecordTextResultDTO getEventCloudRecordAllText(String unionId, String calendarId, String eventId)
            throws Exception
    {
        String token = getAccessToken();
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        GetEventHeaders getEventHeaders = new GetEventHeaders();
        getEventHeaders.setXAcsDingtalkAccessToken(token);
        GetEventRequest getEventRequest = new GetEventRequest().setMaxAttendees(100L);
        GetEventResponse eventInfo = calendarClient.getEventWithOptions(
                unionId,
                calendarId,
                eventId,
                getEventRequest,
                getEventHeaders,
                runtimeOptions
                                                                       );
        if (eventInfo.getStatusCode() != 200) {
            log.error("Get Event Info error: {}", eventInfo.getStatusCode());
            throw new Exception("Get Event Info error");
        }
        if (eventInfo.getBody().getOnlineMeetingInfo() == null) {
            throw new Exception("Get Event Meeting Info error, maybe event have not online meeting");
        }
        String meetingCode = (String) eventInfo.getBody().getOnlineMeetingInfo().getExtraInfo().get("roomCode");
        String replace = meetingCode.replace(" ", "");

        //一个会议可以被多次关闭和开启，每次开启后关闭都会生成一个新的会议ID
        QueryConferenceInfoByRoomCodeHeaders headers = new QueryConferenceInfoByRoomCodeHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        QueryConferenceInfoByRoomCodeRequest request = new QueryConferenceInfoByRoomCodeRequest();
        QueryConferenceInfoByRoomCodeResponse resp = conferenceClient.queryConferenceInfoByRoomCodeWithOptions(
                replace,
                request,
                headers,
                runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("get conference id by meeting room failed. code: {}", resp.getStatusCode());
            throw new Exception("Failed to get conference id by meeting room.");
        }
        List<String> list = resp.getBody().getConferenceList().stream().map(p -> p.getConferenceId()).toList();

        String conferenceId = filterConferenceIdsByRecordStatus(list);
        String text = getCloudRecordAllText(unionId, conferenceId);
        String summary = eventInfo.getBody().getSummary();
        String startTime = eventInfo.getBody().getStart().getDateTime();
        return new RecordTextResultDTO(eventId, summary, text, startTime, eventInfo.getBody());
    }


    public String pushEventToMultiDimensionalTable(String nodeId, String content) throws Exception
    {
        String url = String.format("%s/%s", dingTalkConnectorBaseUrl, nodeId);
        WebClient webClient = WebClient.create();
        String result = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(content)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        log.debug("push finished. result: {}", result);
        return result;
    }

    /**
     * TODO: 根据模板创建文档
     *
     * @throws Exception
     * @url https://open.dingtalk.com/document/orgapp/create-team-space-document
     */
    public CreateDocResultDTO createKBDoc(CreateDocDTO dto) throws Exception
    {
        String token = getAccessToken();
        CreateWorkspaceDocHeaders headers = new CreateWorkspaceDocHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        CreateWorkspaceDocRequest request = new CreateWorkspaceDocRequest()
                .setName(dto.getDocName())
                .setDocType(dto.getDocType())
                .setOperatorId(dto.getUnionId())
                .setParentNodeId(dto.getParentId());
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        CreateWorkspaceDocResponse docInfo = docClient.createWorkspaceDocWithOptions(
                dto.getWorkspaceId(),
                request,
                headers,
                runtimeOptions);
        if (docInfo.getStatusCode() != 200) {
            log.error("create workspace doc failed. statusCode: {}", docInfo.getStatusCode());
            throw new Exception("create workspace doc failed.");
        }
        String docKey = docInfo.getBody().getDocKey();

        // 更新文档内容
        DocUpdateContentHeaders updateHeaders = new DocUpdateContentHeaders();
        updateHeaders.setXAcsDingtalkAccessToken(accessToken);
        DocUpdateContentRequest updateRequest = new DocUpdateContentRequest();
        updateRequest.setContent(dto.getContent());
        updateRequest.setOperatorId(dto.getUnionId());
        updateRequest.setDataType(dto.getContentType());

        DocUpdateContentResponse result = docClient.docUpdateContentWithOptions(
                docKey,
                updateRequest,
                updateHeaders,
                runtimeOptions);

        String error = null;
        if (result.getStatusCode() != 200) {
            log.error("update doc failed. statusCode: {}", result.getStatusCode());
            error = String.format("update doc failed. statusCode: %s", result.getStatusCode());
        }

        String url = docInfo.getBody().getUrl();
        return new CreateDocResultDTO(url, error);
    }

    public void robotBatchSendMessage(List<String> userIds, String msgKey, String message) throws Exception
    {
        String token = getAccessToken();
        BatchSendOTOHeaders headers = new BatchSendOTOHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        BatchSendOTORequest request = new BatchSendOTORequest();
        request.setRobotCode(robotCode);
        request.setUserIds(userIds);
        request.setMsgKey(msgKey);
        request.setMsgParam(message);
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        robotClient.batchSendOTOWithOptions(request, headers, runtimeOptions);
    }

    public List<OapiV2DepartmentListsubResponse.DeptBaseResponse> getSubDepartments(Long id) throws Exception
    {
        DefaultDingTalkClient client = new DefaultDingTalkClient(
                "https://oapi.dingtalk.com/topapi/v2/department/listsub");
        OapiV2DepartmentListsubRequest req = new OapiV2DepartmentListsubRequest();
        req.setDeptId(id);
        OapiV2DepartmentListsubResponse resp = client.execute(req, "");
        if (resp.getErrcode() != 0) {
            throw new Exception(resp.getErrmsg());
        }
        return resp.getResult();
    }

    public void sendInteractiveCard(SendRobotInteractiveCardRequest request) throws Exception
    {
        String token = getAccessToken();
        SendRobotInteractiveCardHeaders headers = new SendRobotInteractiveCardHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        SendRobotInteractiveCardResponse resp = imClient.sendRobotInteractiveCardWithOptions(
                request,
                headers,
                runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("send interactive card failed. code: {}", resp.getStatusCode());
            throw new RuntimeException("send interactive card failed.");
        }
    }

    public void updateInteractiveCard(UpdateRobotInteractiveCardRequest request) throws Exception
    {
        String token = getAccessToken();
        UpdateRobotInteractiveCardHeaders headers = new UpdateRobotInteractiveCardHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        UpdateRobotInteractiveCardResponse resp = imClient.updateRobotInteractiveCardWithOptions(
                request,
                headers,
                runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("update interactive card failed. code: {}", resp.getStatusCode());
            throw new RuntimeException("update interactive card failed.");
        }
    }

    public String getAudioMessageDownloadUrl(String downloadCode) throws Exception
    {
        String token = getAccessToken();
        RobotMessageFileDownloadHeaders headers = new RobotMessageFileDownloadHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        RobotMessageFileDownloadRequest request = new RobotMessageFileDownloadRequest()
                .setDownloadCode(downloadCode)
                .setRobotCode(appKey);
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        RobotMessageFileDownloadResponse resp = robotClient.robotMessageFileDownloadWithOptions(
                request,
                headers,
                runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("download audio message file failed. code: {}", resp.getStatusCode());
            throw new RuntimeException("download audio message file failed.");
        }
        return resp.getBody().getDownloadUrl();
    }

    public void createCalendarEvent(ScheduleEventDTO dto) throws Exception
    {
        CreateEventHeaders headers = new CreateEventHeaders().setXAcsDingtalkAccessToken(getAccessToken());
        CreateEventRequest.CreateEventRequestOnlineMeetingInfo onlineMeetingInfo = new CreateEventRequest.CreateEventRequestOnlineMeetingInfo()
                .setType("dingtalk");
        CreateEventRequest.CreateEventRequestLocation location = new CreateEventRequest.CreateEventRequestLocation().setDisplayName(
                dto.getLocation());

        List<CreateEventRequest.CreateEventRequestAttendees> attendees = new java.util.ArrayList<>();
        if (dto.getAttendees() != null) {
            for (EventAttendeeDTO attendee : dto.getAttendees()) {
                CreateEventRequest.CreateEventRequestAttendees attendee0 = new CreateEventRequest.CreateEventRequestAttendees()
                        .setId(attendee.getEmployeeUnionId());
                attendees.add(attendee0);
            }
        }
        CreateEventRequest.CreateEventRequestEnd end = new CreateEventRequest.CreateEventRequestEnd()
                .setDateTime(dto.getEndTime().toString())
                .setTimeZone("Asia/Shanghai");
        CreateEventRequest.CreateEventRequestStart start = new CreateEventRequest.CreateEventRequestStart()
                .setDateTime(dto.getEndTime().toString())
                .setTimeZone("Asia/Shanghai");
        CreateEventRequest createEventRequest = new CreateEventRequest()
                .setSummary(dto.getSummary())
                .setDescription(dto.getDescription())
                .setStart(start)
                .setEnd(end)
                .setIsAllDay(false)
                .setAttendees(attendees)
                .setLocation(location)
                .setOnlineMeetingInfo(onlineMeetingInfo);

        RuntimeOptions runtimeOptions = new RuntimeOptions();
        calendarClient.createEventWithOptions(
                dto.getOrganizer().getUnionId(),
                dto.getCalendarId(),
                createEventRequest,
                headers,
                runtimeOptions);
    }

    public void updateCalendarEvent(ScheduleEventDTO dto) throws Exception
    {
        PatchEventHeaders headers = new PatchEventHeaders().setXAcsDingtalkAccessToken(getAccessToken());
        PatchEventRequest.PatchEventRequestOnlineMeetingInfo onlineMeetingInfo = new PatchEventRequest.PatchEventRequestOnlineMeetingInfo()
                .setType("dingtalk");
        PatchEventRequest.PatchEventRequestLocation location = new PatchEventRequest.PatchEventRequestLocation()
                .setDisplayName(dto.getLocation());
        List<PatchEventRequest.PatchEventRequestAttendees> attendees = new java.util.ArrayList<>();
        if (dto.getAttendees() != null) {
            for (EventAttendeeDTO attendee : dto.getAttendees()) {
                PatchEventRequest.PatchEventRequestAttendees attendee0 = new PatchEventRequest.PatchEventRequestAttendees()
                        .setId(attendee.getEmployeeUnionId());
                attendees.add(attendee0);
            }
        }
        PatchEventRequest.PatchEventRequestStart start = new PatchEventRequest.PatchEventRequestStart()
                .setDateTime(dto.getEndTime().toString())
                .setTimeZone("Asia/Shanghai");
        PatchEventRequest.PatchEventRequestEnd end = new PatchEventRequest.PatchEventRequestEnd()
                .setDateTime(dto.getEndTime().toString())
                .setTimeZone("Asia/Shanghai");
        PatchEventRequest request = new PatchEventRequest()
                .setSummary(dto.getSummary())
                .setDescription(dto.getDescription())
                .setStart(start)
                .setEnd(end)
                .setIsAllDay(false)
                .setAttendees(attendees)
                .setLocation(location)
                .setOnlineMeetingInfo(onlineMeetingInfo);

        RuntimeOptions runtimeOptions = new RuntimeOptions();
        calendarClient.patchEventWithOptions(
                dto.getOrganizer().getUnionId(),
                dto.getCalendarId(),
                dto.getDingtalkEventId(),
                request,
                headers,
                runtimeOptions);
    }

    public void deleteCalendarEvent(ScheduleEventDTO dto) throws Exception
    {
        DeleteEventHeaders headers = new DeleteEventHeaders().setXAcsDingtalkAccessToken(getAccessToken());
        DeleteEventRequest request = new DeleteEventRequest();
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        calendarClient.deleteEventWithOptions(
                dto.getOrganizer().getUnionId(),
                dto.getCalendarId(),
                dto.getDingtalkEventId(),
                request,
                headers,
                runtimeOptions);
    }

    public ListEventsViewResponseBody getCalendarEvents(ScheduleEventDTO dto, String nextToken) throws Exception
    {
        ListEventsViewHeaders headers = new ListEventsViewHeaders().setXAcsDingtalkAccessToken(getAccessToken());
        ListEventsViewRequest request = new ListEventsViewRequest()
                .setNextToken(nextToken)
                .setTimeMin(dto.getStartTime().toString())
                .setTimeMax(dto.getEndTime().toString());

        RuntimeOptions runtimeOptions = new RuntimeOptions();
        ListEventsViewResponse response = calendarClient.listEventsViewWithOptions(
                dto.getOrganizer().getUnionId(),
                dto.getCalendarId(),
                request,
                headers,
                runtimeOptions);

        if (response.getStatusCode() != 200) {
            throw new RuntimeException("get calendar events failed.");
        }

        return response.getBody();
    }
}
