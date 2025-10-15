# 异常定义

> 通常在 `-domain` 中定义异常类。

## 基类选择

- 异常类可继承自：`io.r2mo.spring.common.exception.SpringException`（Runtime）
- 定义则使用包域的：`io.r2mo.spring.common.exception.SpringE` 统一管理

---

## 编写异常

推荐命名：`_{code}Exception{httpState}Xxx`

- code: 五位可管理的异常码（或超过5位）
- httpState：可选则的状态码，对应 Http 状态返回
- Xxx：异常描述术语

```java
package io.r2mo.spring.app.takeout.exception;

import io.r2mo.spring.common.exception.SpringE;
import org.springframework.http.HttpStatus;

interface ERR {
    // 内部异常，无资源绑定文件
    SpringE _11202 = SpringE.of(-11202, "用户 {} 不存在").state(HttpStatus.NOT_FOUND);
    // 可绑定资源文件的异常（国际化）
    SpringE _11203 = SpringE.of(-11203).state(HttpStatus.NOT_FOUND);
}
```

上述接口 `ERR` 提供的是包域内的异常常量，两种定义方法

- [x] 内部异常直接输出，无需额外的资源文件定义。
- [x] 资源文件绑定的异常，需在 `resources` 下提供资源文件信息。

有了上述定义后，两个异常的写法一模一样，参数为动态参数。

```java
package com.formaltech.apps.takeout.exception;

import io.r2mo.spring.common.exception.SpringException;

public class _11202Exception404UserNotFound extends SpringException {

    public _11202Exception404UserNotFound(final String username) {
        super(ERR._11202, username);            // 此处 super 第二参开始为动态参数
    }
}
```

资源文件绑定（ `src/main/resources` 之下 )

```bash
# IDE 中会呈现：Resource Bundle 'MessageFail' 和 Resource Bundle 'MessageInfo'
# - MessageFail: 系统异常消息定义, e.printStackTrace() 时使用
# - MessageInfo: 返回提示信息 -> 通常会返回客户端
MessageFail_zh_CN.properties
MessageFail_en_US.properties

MessageInfo_zh_CN.properties
MessageInfo_en_US.properties
```

属性文件格式，一个 `E前缀`，一个 `I前缀`，占位符 `{0}, {1}, ...` 代表动态参数，也可直接使用 `{}, {}, ...` 格式。

```properties
# MessageFail_zh_CN.properties
E10005=(V) - 动态键"{0}"在统一扩展配置数据{1}中缺失
E40020=Zero系统找不到键 = {0}的配置，请检查您的配置
E40049=Zero工作方法签名冲突，方法 = {0}，类 = {1}
# MessageInfo_zh_CN.properties
I10005=对不起，您的键 {} 在扩展配置 {} 中缺失，请联系管理员
```

---

## 响应格式

响应的状态码和异常中定义的 HttpState 一致！

```json
{
    "code": -11203,
    "message": "Stack 系统信息",
    "info": "用户 test 不存在"
}
```