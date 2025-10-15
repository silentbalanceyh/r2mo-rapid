# DBE

## 基本接口

基本接口直接参考：[DBE](https://gitee.com/silentbalanceyh/r2mo-rapid/blob/master/r2mo-dbe/src/main/java/io/r2mo/dbe/common/DBE.java)

## 查询引擎语法

### 1. 请求格式

请求可直接使用 `QQuery.of()` 的方式构造对象

```json
{
    "criteria": "可直接使用 QTree.of() 的方式构造对象",
    "pager": {
        "page": "从 1 开始",
        "size": "每页大小"
    },
    "sorter": [
        "field1,ASC",
        "field2,DESC"
    ],
    "projection": [
        "列过滤，不配置则表示全列输出",
        "配置 field1,field2,... 则表示只输出这些列"
    ]
}
```

> 注意 `DBE` 的查询 API 中参数的说明，`QTree/QQuery` 格式会有所不同。

四个属性含义如下

|        属性名 | 类型      | 备注   |
|-----------:|---------|------|
|   criteria | JObject | 查询条件 |
|      pager | JObject | 分页参数 |
|     sorter | JArray  | 排序参数 |
| projection | JArray  | 列过滤  |

### 2. 基本语法

查询语法本身是 Json 格式，支持嵌套，整个查询树的节点有三种

- **直接节点**：`column,op:value`
- **嵌套节点**: `column: {}`
- **连接节点**: `"": true|false`

直接节点基本语法如：

```bash
    "field,op": "value"
```

- field：属性名
- op：操作符
- value：查询的属性值

---

以下部分使用如下示例数据说明

| 属性名      | 类型      | 列名       |
|----------|---------|----------|
| name     | String  | NAME     |
| email    | String  | EMAIL    |
| password | String  | PASSWORD |
| age      | int     | AGE      |
| active   | boolean | ACTIVE   |

---

### 3. 操作符列表

| 操作符  | 格式                               | 含义      | 等价SQL                   |
|------|----------------------------------|---------|-------------------------|
| `<`  | "age,<": 20                      | 小余某个值   | `AGE < 20`              |
| `<=` | "age,<=": 20                     | 小余等于某个值 | `AGE <= 20`             |
| `>`  | "age,>": 20                      | 大余某个值   | `AGE > 20`              |
| `>=` | "age,>=": 20                     | 大余等于某个值 | `AGE >= 20`             |
| `=`  | "name,=": "张三" 或 "name":"张三"（默认） | 等于某个值   | `NAME = '张三'`           |
| `<>` | "name,<>" : "张三"                 | 不等于某个值  | `NAME <> '张三'`          |
| `!n` | "name,!n": "任意"                  | 不为空     | `NAME IS NOT NULL`      |
| `n`  | "name,n": "任意"                   | 为空      | `NAME IS NULL`          |
| `i`  | "name,i": ["A", "B"]             | 在某些值内   | `NAME IN ('A','B')`     |
| `!i` | "name,!i": ["A", "B"]            | 不在某些值内  | `NAME NOT IN ('A','B')` |
| `s`  | "name,s": "张"                    | 以某个值开始  | `NAME LIKE '张%'`        |
| `e`  | "name,e": "三"                    | 以某个值结束  | `NAME LIKE '%三'`        |
| `c`  | "name,c": "三"                    | 包含某个值   | `NAME LIKE '%三%'`       |

### 4. 连接符

语法中的 `op` 是直接节点专用的操作符语法，而连接节点则使用**空键**：`""` 来表示，使用它的目的：

- 开发人员方便记忆
- 空键不具有任何业务意义，作为占位符方便描述

| 值       | 连接符       |
|---------|-----------|
| `true`  | `AND`（默认） |
| `false` | `OR`      |

---

## 示例说明

### 示例 1

```json
{
    "name": "Lang",
    "email,s": "lang.yu"
}
```

等价

```sql
NAME = 'Lang' AND EMAIL LIKE 'lang.yu%'
```

### 示例 2

```json
{
    "": false,
    "name": "Lang",
    "email,s": "lang.yu"
}
```

等价

```sql
NAME = 'Lang' OR EMAIL LIKE 'lang.yu%'
```

### 示例 3（嵌套）

```json
{
    "": false,
    "name": "Lang",
    "$1": {
        "": true,
        "email,s": "lang.yu",
        "age,>=": 18,
        "age,<=": 60
    }
}
```

等价

```sql
NAME = 'Lang' OR (EMAIL LIKE 'lang.yu%' AND AGE >= 18 AND AGE <= 60)
```

### 示例 4（同字段）

```json
{
    "name,c": "lang",
    "": false,
    "$0": {
        "name,c": "yu"
    }
}
```

等价

```sql
NAME LIKE '%lang%' OR NAME LIKE '%yu%'
```