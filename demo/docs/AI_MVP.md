# Demo 模块 AI MVP 说明

## 一期闭环

1. **智能分析**：前端在图表页提交表名、双 Y 列、时间范围与 `num`，后端计算统计特征（均值/方差/阶跃/3σ 粗检），可选调用 OpenAI 兼容接口生成结构化 JSON 结论。
2. **RAG 引用**：`demo-biz/src/main/resources/kb/` 下 Markdown 按 `##` 分块，关键词检索 Top-K，响应中带 `citations` 供前端展示。
3. **离线评测**：`demo/scripts/eval_ai_cases.json` + `eval_ai.py` 批量调用 `/demo/demo/ai/analyze`（需网关与 Token）。

## 配置

在 Nacos 或本地 `application.yml` 增加：

```yaml
demo:
  ai:
    enabled: true
    base-url: https://api.openai.com/v1
    api-key: ${DEMO_AI_API_KEY:}
    model: gpt-4o-mini
    prompt-version: v1
```

未配置 `api-key` 或 `base-url` 时自动走**离线规则摘要**，仍返回知识库引用。

### 国内可访问的 OpenAI 兼容接口（任选其一，改 base-url + api-key + model）

本服务使用标准 **`POST {base-url}/chat/completions`**，与国内多数「OpenAI 兼容」网关一致，无需改代码。

| 厂商 | base-url 示例 | 说明 |
|------|----------------|------|
| DeepSeek | `https://api.deepseek.com/v1` | 文档见官网，模型如 `deepseek-chat` |
| 阿里云通义 | DashScope 兼容模式地址（控制台查看） | 模型名按控制台填写 |
| 智谱 GLM | `https://open.bigmodel.cn/api/paas/v4` | 模型如 `glm-4-flash` |
| 硅基流动等聚合 | 平台提供的 `/v1` 根地址 | 选带 Chat Completions 的模型 |

将 `demo.ai.base-url` 设为上述 **v1 根路径**（不要多写 `/chat/completions`），`api-key` 填对应平台密钥，`model` 与平台文档一致即可。

**「完全免费」**：长期完全免费的大模型 API 很少，多为**新用户赠送额度**或**按量计费**；不配 key 时仍可先用本项目的**离线规则 + RAG** 演示链路。

## API（经网关典型路径）

AI 接口挂在 **`DemoController`**（`/demo`）下，路径为 `/demo/ai/...`，经网关 strip 第一段后与现有 `/demo/demo/column` 等形式一致：

- `POST /demo/demo/ai/analyze` — 请求体见 `AiAnalysisRequest`
- `GET /demo/demo/ai/metrics` — 内存聚合指标
