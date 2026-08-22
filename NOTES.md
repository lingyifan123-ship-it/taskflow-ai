# 重建验收说明

- DeepSeek Key: 仅从 `AI_API_KEY` 读取，当前留空
- 无 Key: Mock 模式可编译、可启动、可测试
- 结构化输出: Spring AI `BeanOutputConverter` 生成并校验 JSON Schema
- Tool Calling: 用户确认后调用 TaskFlow `POST /api/tasks`
