# TaskFlow AI

这个服务把自然语言需求拆成结构化任务，先让用户确认，再调用 TaskFlow 创建任务。没有 DeepSeek Key 时自动走 Mock，所以拿到代码后也能启动和测试。

技术栈：Java 26、Spring Boot 4.1、Spring AI 2.0、DeepSeek API、JSON Schema、Tool Calling、JUnit、Docker。

```bash
git clone https://github.com/lingyifan123-ship-it/taskflow-ai.git
cd taskflow-ai
# 可选：设置 AI_API_KEY；留空就是 Mock
docker compose up --build -d
curl -X POST localhost:8081/api/ai/analyze -H "Content-Type: application/json" -d '{"requirement":"下周前完成登录接口和测试"}'
```

确认后调用 `POST /api/ai/{id}/confirm`，请求体提供 `projectId` 和 `taskflowToken`。服务只有在确认后才写入 TaskFlow。

测试：`mvn test`。固定样例覆盖正常、模糊、超长、无 Key/失败降级以及缺字段 JSON。

已知问题：真实 DeepSeek 延迟和成功率必须由使用者填入 Key 后重新测量；Token 当前随确认请求传入，生产环境应改成服务间凭证。

重建说明：原本地工程因系统恢复丢失，本仓库依据留存说明重建，提交时间代表本次恢复过程。
