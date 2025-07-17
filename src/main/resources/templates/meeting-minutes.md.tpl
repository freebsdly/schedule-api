
[# th:if="${hasError}"]
在线会议可能没有开启云录制，无法总结会议纪要。
[/]

# 会议议程

本次会议议程如下：
[# th:each="item : ${agenda}"]
   - [(${item})]
[/]


# 会议决议
本次会议决议如下：
[# th:each="item : ${meetingResolution}"]
   - [(${item})]
[/]


# 行动项
|**行动项名称**| **执行人** | **截止时间** |
|---|---|---|[# th:each="item : ${actionItems}"]
| [(${item.item})] | [(${item.executor})]  | [(${item.dueDate})]  |
[/]

# 其他主题
[# th:if="${#lists.size(otherTopics) == 0}"]
无
[/]
[# th:each="item : ${otherTopics}"]
   - [(${item})]
[/]