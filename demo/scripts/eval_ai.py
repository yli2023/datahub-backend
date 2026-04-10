#!/usr/bin/env python3
"""
离线批量调用 AI 分析接口，用于面试材料中的延迟与成功率粗测。

环境变量:
  AI_BASE_URL  网关根，如 http://127.0.0.1:9999
  AI_TOKEN     Bearer Token（与前端登录后一致）
  AI_CASES     用例 JSON 路径，默认本目录 eval_ai_cases.json

请求路径默认: {AI_BASE_URL}/demo/demo/ai/analyze
"""
from __future__ import annotations

import json
import os
import sys
import time
import urllib.error
import urllib.request
from pathlib import Path


def main() -> int:
    base = os.environ.get("AI_BASE_URL", "http://127.0.0.1:9999").rstrip("/")
    token = os.environ.get("AI_TOKEN", "")
    cases_path = Path(__file__).resolve().parent / os.environ.get("AI_CASES", "eval_ai_cases.json")
    url = f"{base}/demo/demo/ai/analyze"

    if not token:
        print("WARN: AI_TOKEN empty — expect 401 from secured gateway", file=sys.stderr)

    raw = cases_path.read_text(encoding="utf-8")
    cases = json.loads(raw)
    latencies = []
    oks = 0

    for c in cases:
        name = c.get("name", "?")
        payload = json.dumps(c["payload"], ensure_ascii=False).encode("utf-8")
        req = urllib.request.Request(url, data=payload, method="POST")
        req.add_header("Content-Type", "application/json")
        if token:
            req.add_header("Authorization", f"Bearer {token}")

        t0 = time.perf_counter()
        try:
            with urllib.request.urlopen(req, timeout=120) as resp:
                body = resp.read().decode("utf-8", errors="replace")
                ms = (time.perf_counter() - t0) * 1000
                latencies.append(ms)
                data = json.loads(body)
                code_ok = data.get("code") == 0 if isinstance(data, dict) else False
                if code_ok:
                    oks += 1
                print(f"[{name}] http={resp.status} latency_ms={ms:.0f} code={data.get('code')}")
        except urllib.error.HTTPError as e:
            ms = (time.perf_counter() - t0) * 1000
            print(f"[{name}] HTTPError {e.code} latency_ms={ms:.0f}", file=sys.stderr)
        except Exception as e:
            print(f"[{name}] ERROR {e}", file=sys.stderr)

    if latencies:
        avg = sum(latencies) / len(latencies)
        print(f"\nSummary: ok={oks}/{len(cases)} avg_latency_ms={avg:.0f}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
