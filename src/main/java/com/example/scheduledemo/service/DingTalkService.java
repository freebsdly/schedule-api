package com.example.scheduledemo.service;

import com.aliyun.dingtalkcalendar_1_0.models.GetEventHeaders;
import com.aliyun.dingtalkcalendar_1_0.models.GetEventRequest;
import com.aliyun.dingtalkcalendar_1_0.models.GetEventResponse;
import com.aliyun.dingtalkconference_1_0.models.*;
import com.aliyun.dingtalkdoc_1_0.models.*;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.example.scheduledemo.entity.DepartmentEntity;
import com.example.scheduledemo.entity.EmployeeEntity;
import com.example.scheduledemo.feignclients.DepartmentResponseDTO;
import com.example.scheduledemo.feignclients.DingTalkFeignClient;
import com.example.scheduledemo.feignclients.EmployeeQuery;
import com.example.scheduledemo.feignclients.EmployeeResponseDTO;
import com.example.scheduledemo.repository.DepartmentRepository;
import com.example.scheduledemo.repository.EmployeeRepository;
import com.example.scheduledemo.service.dto.CreateDocDTO;
import com.example.scheduledemo.service.dto.CreateDocResultDTO;
import com.example.scheduledemo.service.dto.RecordTextResultDTO;
import com.example.scheduledemo.service.dto.ServiceDTOMapper;
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
public class DingTalkService {

    @Autowired
    private com.aliyun.dingtalkcalendar_1_0.Client calendarClient;

    @Autowired
    private com.aliyun.dingtalkconference_1_0.Client conferenceClient;

    @Autowired
    private com.aliyun.dingtalkoauth2_1_0.Client oauth2Client;

    @Autowired
    private com.aliyun.dingtalkdoc_1_0.Client docClient;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DingTalkFeignClient dingTalkFeignClient;

    @Value("${dingtalk.ak}")
    private String appKey;

    @Value("${dingtalk.sk}")
    private String appSecret;

    @Value("${dingtalk.connector.base-url}")
    private String dingTalkConnectorBaseUrl;

    private String accessToken;

    private boolean syncDepartmentRunning = false;
    private boolean syncEmployeeRunning = false;

    @Autowired
    private EmployeeRepository employeeRepository;

    public String getAccessToken() throws Exception {
        if (accessToken != null) {
            return accessToken;
        }
        GetAccessTokenRequest getAccessTokenRequest = new GetAccessTokenRequest()
                .setAppKey(appKey)
                .setAppSecret(appSecret);
        GetAccessTokenResponse resp = oauth2Client.getAccessToken(getAccessTokenRequest);
        accessToken = resp.getBody().getAccessToken();
        return accessToken;
    }

    /**
     * FIXME: 按照token的过期时间刷新token
     */
    void updateAccessToken() {

    }

    @Async
    public synchronized void syncDepartments() throws Exception {
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
            String token = getAccessToken();
            DepartmentResponseDTO info = dingTalkFeignClient.getSubDepartments(token, currentId);
            if (info.getErrorCode() != 0) {
                log.error("get department info return error: {}", info.getErrorMessage());
                break;
            }

            info.getResult().forEach(e -> {
                Optional<DepartmentEntity> exist = departmentRepository.findByDingTalkDepartmentId(e.getDepartmentId());
                if (exist.isPresent()) {
                    DepartmentEntity entity = exist.get();
                    entity.setName(e.getName());
                    entity.setDingTalkParentId(e.getParentId());
                    entity.setDingTalkDepartmentId(e.getDepartmentId());
                    departmentRepository.save(entity);
                } else {
                    log.debug("department (id=1) does not exist, create it");
                    DepartmentEntity entity = ServiceDTOMapper.INSTANCE.toEntity(e);
                    departmentRepository.saveAndFlush(entity);
                }

                departmentStack.push(e.getDepartmentId());
            });
        }
        syncDepartmentRunning = false;
    }

    @Async
    public synchronized void syncEmployees() throws Exception {
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
                    log.error("get department (id:{}) employee error: {}", e.getDingTalkDepartmentId(), dto.getErrorMessage());
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
                        EmployeeEntity entity = ServiceDTOMapper.INSTANCE.toEntity(employee);
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

    public QueryCloudRecordTextResponseBody getCloudRecordText(String unionId, String conferenceId, Long nextToken) throws Exception {
        String token = getAccessToken();
        QueryCloudRecordTextHeaders headers = new QueryCloudRecordTextHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        QueryCloudRecordTextRequest request = new QueryCloudRecordTextRequest();
        request.setUnionId(unionId);
        request.setNextToken(nextToken);
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        QueryCloudRecordTextResponse resp = conferenceClient.queryCloudRecordTextWithOptions(conferenceId, request, headers, runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("get cloud record text failed. code: {}", resp.getStatusCode());
            throw new Exception("Failed to get cloud record text info.");
        }
        return resp.getBody();
    }

    public String getCloudRecordAllText(String unionId, String conferenceId) throws Exception {
        QueryCloudRecordTextResponseBody body = getCloudRecordText(unionId, conferenceId, null);

        //FIXME: 考虑hasMore情况
        List<String> list = body.getParagraphList().stream()
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
    public String filterConferenceIdsByRecordStatus(List<String> ids) throws Exception {
        if (!ids.isEmpty()) {
            return ids.get(0);
        }
        throw new Exception("event have more than 1 conference id");
    }

    public RecordTextResultDTO getEventCloudRecordAllText(String unionId, String calendarId, String eventId) throws Exception {
        String token = getAccessToken();
        RuntimeOptions runtimeOptions = new RuntimeOptions();

        GetEventHeaders getEventHeaders = new GetEventHeaders();
        getEventHeaders.setXAcsDingtalkAccessToken(token);
        GetEventRequest getEventRequest = new GetEventRequest()
                .setMaxAttendees(100L);
        GetEventResponse eventInfo = calendarClient.getEventWithOptions(unionId, calendarId, eventId, getEventRequest, getEventHeaders, runtimeOptions);
        if (eventInfo.getStatusCode() != 200) {
            log.error("Get Event Info error: {}", eventInfo.getStatusCode());
            throw new Exception("Get Event Info error");
        }
        String meetingCode = (String) eventInfo.getBody().getOnlineMeetingInfo().getExtraInfo().get("roomCode");
        String replace = meetingCode.replace(" ", "");

        //一个会议可以被多次关闭和开启，每次开启后关闭都会生成一个新的会议ID
        QueryConferenceInfoByRoomCodeHeaders headers = new QueryConferenceInfoByRoomCodeHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        QueryConferenceInfoByRoomCodeRequest request = new QueryConferenceInfoByRoomCodeRequest();
        QueryConferenceInfoByRoomCodeResponse resp = conferenceClient.queryConferenceInfoByRoomCodeWithOptions(replace, request, headers, runtimeOptions);
        if (resp.getStatusCode() != 200) {
            log.error("get conference id by meeting room failed. code: {}", resp.getStatusCode());
            throw new Exception("Failed to get conference id by meeting room.");
        }
        List<String> list = resp.getBody().getConferenceList().stream().map(p -> p.getConferenceId()).toList();

        String conferenceId = filterConferenceIdsByRecordStatus(list);
        String text = getCloudRecordAllText(unionId, conferenceId);
        String summary = eventInfo.getBody().getSummary();
        String startTime = eventInfo.getBody().getStart().getDateTime();
        return new RecordTextResultDTO(eventId, summary, text, startTime);
    }


    public String pushEventToMultiDimensionalTable(String nodeId, String content) throws Exception {
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
    public CreateDocResultDTO createKBDoc(CreateDocDTO dto) throws Exception {
        String token = getAccessToken();
        CreateWorkspaceDocHeaders headers = new CreateWorkspaceDocHeaders();
        headers.setXAcsDingtalkAccessToken(token);
        CreateWorkspaceDocRequest request = new CreateWorkspaceDocRequest()
                .setName(dto.getDocName())
                .setDocType(dto.getDocType())
                .setOperatorId(dto.getUnionId())
                .setParentNodeId(dto.getParentId());
        RuntimeOptions runtimeOptions = new RuntimeOptions();
        CreateWorkspaceDocResponse docInfo = docClient.createWorkspaceDocWithOptions(dto.getWorkspaceId(), request, headers, runtimeOptions);
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

        DocUpdateContentResponse result = docClient.docUpdateContentWithOptions(docKey, updateRequest, updateHeaders, runtimeOptions);

        String error = null;
        if (result.getStatusCode() != 200) {
            log.error("update doc failed. statusCode: {}", result.getStatusCode());
            error = String.format("update doc failed. statusCode: %s", result.getStatusCode());
        }

        String url = docInfo.getBody().getUrl();
        return new CreateDocResultDTO(url, error);
    }
}
