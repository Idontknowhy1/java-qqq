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

| 接口 | 方法 | 认证 | 入参 | 出参 | 说明 |
|---|---|---|---|---|---|
| `/daily-resign` | GET | 需要 `token` + `userId` | Header: `AppRequestHeader` | `ApiResponse<UserScoreRecordEntity>` | 每日签到，发放签到积分 |
| `/init` | GET | 需要 `token` + `userId` | Header: `AppRequestHeader` | `ApiResponse<UserScoreRecordEntity>` | 初始化积分（固定 +300） |
| `/get-score-config` | GET | 不需要登录 | 无 | `ApiResponse<ScoreConfig>` | 获取积分配置 |
| `/balance-info` | GET | 需要 `userId` | Header: `AppRequestHeader` | `ApiResponse<UserScoreVO>` | 查询当前用户积分余额和今日签到状态 |

### 2.2 管理侧 API（jike-admin-api）

统一前缀：`/userscore/v1`

| 接口 | 方法 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `/add-score` | GET | `toUserId`(String), `score`(int) | `ApiResponse<?>` | 管理员给用户加/减永久积分（实际转发到内部接口） |
| `/get-user-score` | GET | `uuid`(String) | `ApiResponse<UserScoreVO>` | 按用户 uuid 查询积分信息 |
| `/get-score-config` | GET | 无 | `ApiResponse<ScoreConfig>` | 获取积分配置 |

### 2.3 内部接口（jike-web-api）

统一前缀：`/inner`

| 接口 | 方法 | 入参 | 出参 | 说明 |
|---|---|---|---|---|
| `/addUserScore` | POST | `InnerAddUserScoreRequest` | `ApiResponse<UserScoreRecordEntity>` | 管理侧加积分的真实执行接口 |

## 3. 数据类型定义

### 3.1 入参类型

#### AppRequestHeader（请求头参数）
```java
// 位置：com.jike.request.AppRequestHeader
// 说明：用户侧接口通过 Header 注入认证信息
- token: String      // 用户登录令牌
- userId: String     // 用户ID
```

#### InnerAddUserScoreRequest（内部添加积分请求）
```java
// 位置：com.jike.controller.inner.request.InnerAddUserScoreRequest
- userId: String     // 用户ID，必填（@NotBlank）
- score: long       // 积分变更值，可正可负
```

### 3.2 出参类型

#### ApiResponse（通用返回壳）
```java
// 位置：com.jjs.response.ApiResponse
- code: Integer      // 状态码
- msg: String        // 提示信息
- data: Object       // 业务数据
```

#### ScoreConfig（积分配置）
```java
// 位置：com.jike.common.model.score.ScoreConfig
- dailySignReward: int      // 签到奖励积分
- aiGenPrice: int          // AI生成消耗
- imageSplitPrice: int     // 图片处理消耗
```

#### UserScoreVO（积分余额视图对象）
```java
// 位置：com.jike.model.score.UserScoreVO
- forScore: long           // 永久积分
- vipScore: long          // 会员积分
- userId: long             // 用户ID
- id: int                 // 记录ID
- todayResigned: boolean  // 当天是否已签到
```

#### UserScoreEntity（积分余额实体）
```java
// 位置：com.jike.model.score.UserScoreEntity
// 表名：tb_user_score
- forScore: long           // 永久积分
- vipScore: long          // 会员积分
- userId: long             // 用户ID
- id: int                 // 记录ID（自增）
```

#### UserScoreRecordEntity（积分流水记录）
```java
// 位置：com.jike.model.score.UserScoreRecordEntity
// 表名：tb_user_score_records
- id: int                 // 记录ID（自增）
- userId: long            // 用户ID
- type: String            // 变更类型（对应 UserScoreUpdateEnum）
- memo: String            // 变更说明
- vipScore: long          // 本次变更的会员积分
- forScore: long          // 本次变更的永久积分
- vipBalanceScore: long   // 变更后会员积分余额
- forBalanceScore: long   // 变更后永久积分余额
- createTime: long        // 创建时间戳（秒）
```

#### UserScoreUpdateResult（积分更新结果）
```java
// 位置：com.jike.model.score.UserScoreUpdateResult
- record: UserScoreRecordEntity   // 更新记录
```

### 3.3 通用返回壳与状态码

#### ApiResponse（通用返回壳）
```java
// 位置：com.jjs.response.ApiResponse
- code: Integer      // 状态码
- msg: String        // 提示信息
- data: Object       // 业务数据
```

#### ApiResponseEnum（状态码枚举）
```java
// 位置：com.jjs.response.ApiResponseEnum
- SUCCESS(10000, "成功")
- FAIL(99999, "系统错误")
- LACK_PERMISSION(403, "权限不足")
- PARAM_MISSING(10001, "参数缺失")
- PARAM_ERROR(10001, "参数错误")
- BUSY(10002, "服务器开小差，请稍后再试")
- AUTH_FAIL(10003, "认证失败")
- METHOD_NOT_ALLOWED(10004, "该方法不被允许")
- FREQUENCY_TOO_HIGH(10005, "操作频率过高，请稍后再试")
- USER_NO_IVALID(10013, "用户Id无效")
- COIN_NOT_ENOUGHT(10014, "积分不足")
```

## 4. 积分变更类型（UserScoreUpdateEnum）
```java
// 位置：com.jike.common.model.score.UserScoreUpdateEnum
- CONSUME             // 消费
- DAILY_SIGN          // 每日签到
- BUY_SCORE           // 购买积分
- RESET               // 积分重置
- INIT                // 初始化积分
- BUY_VIP             // 购买会员
- ADMIN_ADD_SCORE     // 管理员添加积分
- ADMIN_ADD_VIP       // 管理员添加会员积分
- CLEAR               // 系统清零
- VIP_EXPIRED         // 会员过期
```

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

**入参：** `AppRequestHeader`（Header 注入）
**出参：** `ApiResponse<UserScoreRecordEntity>`

- 登录态校验：`token` 与 `userId` 不能为空
- 幂等控制：Redis key `user:dailyresign:{userId}:{yyyy-MM-dd}`
- 发放积分：读取 `scoreconfig.dailySignReward`
- 更新方式：`UserScoreService.update(..., DAILY_SIGN)`

### 5.2 初始化积分（`/userscore/v1/init`）

**入参：** `AppRequestHeader`（Header 注入）
**出参：** `ApiResponse<UserScoreRecordEntity>`

- 登录态校验：`token` 与 `userId` 不能为空
- 固定发放 300 积分：`UserScoreService.update(300, userId, INIT)`
- ⚠️ 风险点：当前代码未看到幂等限制，建议确认是否仅用于一次性初始化

### 5.3 余额查询（`/userscore/v1/balance-info`）

**入参：** `AppRequestHeader`（Header 注入）
**出参：** `ApiResponse<UserScoreVO>`

- 按 `userId` 查 `tb_user_score`
- 若无记录，返回空 `UserScoreVO`
- 附带读取今日签到标记，写入 `todayResigned`

### 5.4 获取积分配置（`/userscore/v1/get-score-config`）

**入参：** 无
**出参：** `ApiResponse<ScoreConfig>`

### 5.5 管理员加积分

**入参（jike-admin-api）：** `toUserId`(String), `score`(int) - GET
**入参（内部接口）：** `InnerAddUserScoreRequest`
**出参：** `ApiResponse<UserScoreRecordEntity>`

调用链：

1. 管理前端调用 `jike-admin-api`：`/userscore/v1/add-score`
2. `jike-admin-api` 通过 `InnerApi.sendPost` 转发到 `jike-web-api`：`/inner/addUserScore`
3. `jike-web-api` 执行 `userScoreService.update(score, userId, ADMIN_ADD_SCORE)`
4. 写入积分余额表与积分流水表

⚠️ 风险点：`InnerAddUserScoreRequest.score` 未做范围校验，虽然支持负值可用于扣分，但建议增加明确规则与审计控制

### 5.6 管理员查询用户积分（`/userscore/v1/get-user-score`）

**入参：** `uuid`(String) - 用户 UUID
**出参：** `ApiResponse<UserScoreVO>`

- 先通过 `uuid` 查 `UserEntity` 获取用户 `id`
- 再通过 `userId` 查 `tb_user_score`

## 6. 数据与配置依赖

### 6.1 表

| 表名 | 说明 |
|---|---|
| `tb_user_score` | 用户当前积分余额 |
| `tb_user_score_records` | 积分变更流水 |

### 6.2 Redis

| Key | 类型 | 说明 |
|---|---|---|
| `scoreconfig` | String (JSON) | 积分配置 JSON（反序列化为 `ScoreConfig`） |
| `user:dailyresign:{userId}:{date}` | String | 每日签到标记 |

## 7. 前端调用映射

### 7.1 jikeing-web

| 场景 | 前端方法 | 调用接口 |
|---|---|---|
| 用户-查询余额 | `getUserScoreBalance` | `GET /userscore/v1/balance-info` |
| 用户-签到 | `resign` | `GET /userscore/v1/daily-resign` |
| 用户-获取配置 | `getScoreConfig` | `GET /userscore/v1/get-score-config` |
| 管理-加积分 | `addScore` | `GET /userscore/v1/add-score` |
| 管理-查询积分 | `getUserScore` | `GET /userscore/v1/get-user-score` |

### 7.2 jike-admin-web

| 页面 | 调用接口 |
|---|---|
| 用户积分页 | `GET /userscore/v1/get-user-score` |
| 用户积分页 | `GET /userscore/v1/add-score` |

## 8. 现状观察与风险点

| 风险点 | 描述 | 建议 |
|---|---|---|
| `/init` 无幂等控制 | GET 且固定发放 300 积分，可重复调用 | 确认是否仅用于一次性初始化，考虑增加幂等控制 |
| 管理加分接口使用 GET | `/add-score` 从语义上更适合 POST | 建议改为 POST 方法 |
| `score` 未做范围校验 | 支持负值但缺乏审计控制 | 增加明确规则与审计日志 |

## 9. 代码定位清单

| 分类 | 文件路径 |
|---|---|
| 用户侧 Controller | `jike-web-api/src/main/java/com/jike/controller/score/UserScoreController.java` |
| 内部 Controller | `jike-web-api/src/main/java/com/jike/controller/inner/InnerController.java` |
| 积分 Service | `jike-web-api/src/main/java/com/jike/service/score/UserScoreService.java` |
| 管理侧 Controller | `jike-admin-api/src/main/java/com/jike/controller/score/UserScoreController.java` |
| 积分配置模型 | `java-jike-common/src/main/java/com/jike/common/model/score/ScoreConfig.java` |
| 积分类型枚举 | `java-jike-common/src/main/java/com/jike/common/model/score/UserScoreUpdateEnum.java` |
| 通用返回结构 | `java-jjs-common/src/main/java/com/jjs/response/ApiResponse.java` |
| 状态码枚举 | `java-jjs-common/src/main/java/com/jjs/response/ApiResponseEnum.java` |
| 前端调用（用户） | `jikeing-web/src/api/home/score.ts` |
| 前端调用（管理） | `jikeing-web/src/api/manager/score.ts` |
| 前端调用（管理后台） | `jike-admin-web/src/pages/user/UserScore.tsx` |
