package io.r2mo.typed.service;

/**
 * @author lang : 2025-08-28
 */
public enum ActState {
    // 1xx Informational - 信息性状态码
    INFO_100_CONTINUE,                     // 100 Continue
    INFO_101_SWITCHING_PROTOCOLS,          // 101 Switching Protocols
    INFO_102_PROCESSING,                   // 102 Processing (WebDAV)
    INFO_103_EARLY_HINTS,                  // 103 Early Hints

    // 2xx Success - 成功状态码
    SUCCESS,                               // 200 OK (默认成功)
    SUCCESS_201_CREATED,                   // 201 Created
    SUCCESS_202_ACCEPTED,                  // 202 Accepted
    SUCCESS_204_NO_DATA,                   // 204 No Content
    SUCCESS_205_RESET_CONTENT,             // 205 Reset Content
    SUCCESS_206_PARTIAL_CONTENT,           // 206 Partial Content
    SUCCESS_207_MULTI_STATUS,              // 207 Multi-Status (WebDAV)
    SUCCESS_208_ALREADY_REPORTED,          // 208 Already Reported (WebDAV)
    SUCCESS_226_IM_USED,                   // 226 IM Used

    // 3xx Redirection - 重定向状态码
    REDIRECT_300_MULTIPLE_CHOICES,         // 300 Multiple Choices
    REDIRECT_301_MOVED_PERMANENTLY,        // 301 Moved Permanently
    REDIRECT_302_FOUND,                    // 302 Found (临时重定向)
    REDIRECT_303_SEE_OTHER,                // 303 See Other
    REDIRECT_304_NOT_MODIFIED,             // 304 Not Modified
    REDIRECT_307_TEMPORARY_REDIRECT,       // 307 Temporary Redirect
    REDIRECT_308_PERMANENT_REDIRECT,       // 308 Permanent Redirect

    // 错误状态
    FAILURE                                // 默认失败状态
}