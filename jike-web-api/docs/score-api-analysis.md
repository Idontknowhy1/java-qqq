# 项目积分接口分析

## 1. 分析范围

本次梳理覆盖以下模块中的积分相关代码：

- jike-web-api（用户侧接口、内部接口、积分核心服务）
- jike-admin-api（管理侧接口）
- jikeing-web（用户侧与管理侧前端调用）
- jike-admin-web（管理侧前端调用）
- java-jike-common（积分配置/积分变更类型枚举）
- java-jjs-common（统一返回结构与状态码）

## 2. 积分接口总览

### 2.1 用户侧 API（jike-web-api）

统一前缀：`/userscore/v1`

| 接口 | 方法 | 认证 | 主要参数 | 说明 |
|---|---|---|---|---|
| `/daily-resign` | GET | 需要 `token` + `userId` | Header 注入 `AppRequestHeader` | 每日签到，发放签到积分 |
| `/init` | GET | 需要 `token` + `userId` | Header 注入 `AppRequestHeader` | 初始化积分（固定 +300） |
| `/get-score-config` | GET | 不需要登录 | 无 | 获取积分配置 |
| `/balance-info` | GET | 需要 `userId` | Header 注入 `AppRequestHeader` | 查询当前用户积分余额和今日签到状态 |

### 2.2 管理侧 API（jike-admin-api）

统一前缀：`/userscore/v1`

| 接口 | 方法 | 主要参数 | 说明 |
|---|---|---|---|
| `/add-score` | GET | `toUserId`(String), `score`(int) | 管理员给用户加/减永久积分（实际转发到内部接口） |
| `/get-user-score` | GET | `uuid`(String) | 按用户 uuid 查询积分信息 |
| `/get-score-config` | GET | 无 | 获取积分配置 |

### 2.3 内部接口（jike-web-api）

统一前缀：`/inner`

| 接口 | 方法 | 入参 | 说明 |
|---|---|---|---|
| `/addUserScore` | POST | `InnerAddUserScoreRequest` | 管理侧加积分的真实执行接口 |

`InnerAddUserScoreRequest` 字段：

- `userId`：String，必填（`@NotBlank`）
- `score`：long，可正可负（代码未限制）

## 3. 关键返回结构

### 3.1 通用返回壳

统一使用 `ApiResponse`：

- `code`：状态码（成功常见值为 `10000`）
- `msg`：提示信息
- `data`：业务数据

常见状态码（`ApiResponseEnum`）：

- `10000`：成功
- `99999`：系统错误
- `10003`：认证失败
- `10014`：积分不足

### 3.2 积分余额对象（UserScoreVO）

主要字段：

- `forScore`：永久积分
- `vipScore`：会员积分
- `userId`：用户ID
- `id`：记录ID
- `todayResigned`：当天是否已签到（仅 `balance-info` 场景会设置）

### 3.3 积分流水对象（UserScoreRecordEntity）

主要字段：

- `createTime`
- `userId`
- `type`（变更类型）
- `memo`（变更说明）
- `vipScore`（本次变更的会员积分）
- `forScore`（本次变更的永久积分）
- `vipBalanceScore`（变更后会员积分余额）
- `forBalanceScore`（变更后永久积分余额）

## 4. 积分变更类型（UserScoreUpdateEnum）

- `CONSUME`：消费
- `DAILY_SIGN`：每日签到
- `BUY_SCORE`：购买积分
- `RESET`：积分重置
- `INIT`：初始化积分
- `BUY_VIP`：购买会员
- `ADMIN_ADD_SCORE`：管理员添加积分
- `ADMIN_ADD_VIP`：管理员添加会员积分
- `CLEAR`：系统清零
- `VIP_EXPIRED`：会员过期

## 5. 核心业务逻辑说明

### 5.1 每日签到（`/userscore/v1/daily-resign`）

- 登录态校验：`token` 与 `userId` 不能为空
- 幂等控制：Redis key `user:dailyresign:{userId}:{yyyy-MM-dd}`
- 发放积分：读取 `scoreconfig.dailySignReward`
- 更新方式：`UserScoreService.update(..., DAILY_SIGN)`

### 5.2 余额查询（`/userscore/v1/balance-info`）

- 按 `userId` 查 `tb_user_score`
- 若无记录，返回空 `UserScoreVO`
- 附带读取今日签到标记，写入 `todayResigned`

### 5.3 管理员加积分

调用链：

1. 管理前端调用 `jike-admin-api`：`/userscore/v1/add-score`
2. `jike-admin-api` 通过 `InnerApi.sendPost` 转发到 `jike-web-api`：`/inner/addUserScore`
3. `jike-web-api` 执行 `userScoreService.update(score, userId, ADMIN_ADD_SCORE)`
4. 写入积分余额表与积分流水表

## 6. 数据与配置依赖

### 6.1 表

- `tb_user_score`：用户当前积分余额
- `tb_user_score_records`：积分变更流水

### 6.2 Redis

- `scoreconfig`：积分配置 JSON（反序列化为 `ScoreConfig`）
- `user:dailyresign:{userId}:{date}`：每日签到标记

### 6.3 积分配置（ScoreConfig）

- `dailySignReward`：签到奖励积分
- `aiGenPrice`：AI生成消耗
- `imageSplitPrice`：图片处理消耗

## 7. 前端调用映射

### 7.1 jikeing-web

- 用户端：
  - `getUserScoreBalance` -> `/userscore/v1/balance-info`
  - `resign` -> `/userscore/v1/daily-resign`
  - `getScoreConfig` -> `/userscore/v1/get-score-config`
- 管理端：
  - `addScore` -> 管理后端 `/userscore/v1/add-score`
  - `getUserScore` -> 管理后端 `/userscore/v1/get-user-score`

### 7.2 jike-admin-web

- 用户积分页调用：
  - `/userscore/v1/get-user-score`
  - `/userscore/v1/add-score`

## 8. 现状观察与风险点

- `/userscore/v1/init` 为 GET 且固定发放 300 积分，当前代码未看到幂等限制，建议确认是否仅用于一次性初始化。
- 管理加分接口使用 GET（`/add-score`），从语义上更适合 POST（涉及状态变更）。
- `InnerAddUserScoreRequest.score` 未做范围校验，虽然支持负值可用于扣分，但建议增加明确规则与审计控制。

## 9. 代码定位清单

- 用户侧 Controller：`jike-web-api/src/main/java/com/jike/controller/score/UserScoreController.java`
- 内部 Controller：`jike-web-api/src/main/java/com/jike/controller/inner/InnerController.java`
- 积分 Service：`jike-web-api/src/main/java/com/jike/service/score/UserScoreService.java`
- 管理侧 Controller：`jike-admin-api/src/main/java/com/jike/controller/score/UserScoreController.java`
- 积分配置模型：`java-jike-common/src/main/java/com/jike/common/model/score/ScoreConfig.java`
- 积分类型枚举：`java-jike-common/src/main/java/com/jike/common/model/score/UserScoreUpdateEnum.java`
- 前端调用（用户）：`jikeing-web/src/api/home/score.ts`
- 前端调用（管理）：`jikeing-web/src/api/manager/score.ts`
- 前端调用（管理后台）：`jike-admin-web/src/pages/user/UserScore.tsx`
