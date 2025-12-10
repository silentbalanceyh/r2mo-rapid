package io.r2mo.base.io.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * URI Scheme 枚举类
 * 包含常见的 URI 协议方案
 */
public enum UriScheme {

    // Web 协议
    HTTP("http", "超文本传输协议", true),
    HTTPS("https", "安全超文本传输协议", true),
    FTP("ftp", "文件传输协议", true),
    FTPS("ftps", "安全文件传输协议", true),
    SFTP("sftp", "SSH文件传输协议", true),

    // 邮件协议
    MAILTO("mailto", "邮件地址协议", true),
    SMTP("smtp", "简单邮件传输协议", true),
    SMTPS("smtps", "安全简单邮件传输协议", true),
    POP("pop", "邮局协议版本3", true),
    POPS("pop3s", "安全邮局协议版本3", true),
    IMAP("imap", "Internet消息访问协议", true),
    IMAPS("imaps", "安全Internet消息访问协议", true),

    // 数据协议
    DATA("data", "数据URI方案", false),
    FILE("file", "本地文件URI", false),
    JAR("jar", "Java归档文件协议", false),
    WAR("war", "Web应用归档协议", false),
    EAR("ear", "企业应用归档协议", false),

    // 数据库协议
    JDBC("jdbc", "Java数据库连接协议", false),
    MONGODB("mongodb", "MongoDB协议", true),
    MONGODB_SRV("mongodb+srv", "MongoDB SRV记录协议", true),
    REDIS("redis", "Redis协议", true),
    REDIS_SSL("rediss", "安全Redis协议", true),
    POSTGRES("postgresql", "PostgreSQL协议", true),
    MYSQL("mysql", "MySQL协议", true),
    ORACLE("oracle", "Oracle数据库协议", true),
    SQLSERVER("sqlserver", "Microsoft SQL Server协议", true),

    // 版本控制协议
    GIT("git", "Git版本控制协议", true),
    SSH("ssh", "安全外壳协议", true),
    SVN("svn", "Subversion协议", true),
    SVN_SSH("svn+ssh", "通过SSH的Subversion协议", true),

    // 消息队列协议
    AMQP("amqp", "高级消息队列协议", true),
    AMQPS("amqps", "安全高级消息队列协议", true),
    MQTT("mqtt", "消息队列遥测传输协议", true),
    MQTTS("mqtts", "安全消息队列遥测传输协议", true),
    KAFKA("kafka", "Apache Kafka协议", true),
    RABBITMQ("amqp", "RabbitMQ协议", true),

    // 云服务协议
    S3("s3", "Amazon S3协议", true),
    S3N("s3n", "Amazon S3 Native协议", true),
    S3A("s3a", "Amazon S3A协议", true),
    AZURE("wasb", "Azure Blob存储协议", true),
    AZURE_SECURE("wasbs", "安全Azure Blob存储协议", true),
    GS("gs", "Google Cloud Storage协议", true),
    OSS("oss", "阿里云对象存储协议", true),
    COS("cos", "腾讯云对象存储协议", true),

    // 实时通信协议
    RTMP("rtmp", "实时消息协议", true),
    RTMPS("rtmps", "安全实时消息协议", true),
    RTMPT("rtmpt", "通过HTTP的实时消息协议", true),
    WEBSOCKET("ws", "WebSocket协议", true),
    WEBSOCKET_SECURE("wss", "安全WebSocket协议", true),
    SIP("sip", "会话初始协议", true),
    SIPS("sips", "安全会话初始协议", true),

    // 网络协议
    TCP("tcp", "传输控制协议", true),
    UDP("udp", "用户数据报协议", true),
    TLS("tls", "传输层安全协议", true),
    SSL("ssl", "安全套接字层协议", true),

    // 应用协议
    TELNET("telnet", "远程登录协议", true),
    LDAP("ldap", "轻量级目录访问协议", true),
    LDAPS("ldaps", "安全轻量级目录访问协议", true),
    SNMP("snmp", "简单网络管理协议", true),
    SNMPV3("snmpv3", "SNMP版本3协议", true),
    TFTP("tftp", "简单文件传输协议", true),
    NFS("nfs", "网络文件系统协议", true),
    SMB("smb", "服务器消息块协议", true),
    CIFS("cifs", "通用Internet文件系统协议", true),

    // 移动协议
    TEL("tel", "电话号码协议", true),
    SMS("sms", "短消息服务协议", true),
    MMS("mms", "多媒体消息服务协议", true),
    NFC("nfc", "近场通信协议", false),
    BT("bt", "蓝牙协议", false),
    BTP("btp", "蓝牙传输协议", false),

    // 新兴协议
    IPFS("ipfs", "星际文件系统协议", true),
    DAT("dat", "分布式档案传输协议", true),
    MAGNET("magnet", "磁力链接协议", true),
    TORRENT("torrent", "BitTorrent协议", false),
    IRC("irc", "Internet中继聊天协议", true),
    IRC6("irc6", "IPv6 IRC协议", true),
    IRCS("ircs", "安全IRC协议", true),

    // 开发相关协议
    CLASSPATH("classpath", "Java类路径协议", false),
    MAVEN("mvn", "Maven仓库协议", true),
    DOCKER("docker", "Docker镜像协议", true),
    NPM("npm", "Node包管理协议", true),
    GEM("gem", "Ruby Gem协议", true),
    PIP("pip", "Python包安装协议", true),

    // 系统协议
    CRAM_MD5("cram-md5", "CRAM-MD5认证协议", false),
    DIGEST("digest", "摘要认证协议", false),
    NTLM("ntlm", "NT LAN Manager协议", false),

    // 自定义协议
    CUSTOM("custom", "自定义协议", false);

    private final String scheme;
    private final String description;
    @Getter
    private final boolean networkBased;

    UriScheme(final String scheme, final String description, final boolean networkBased) {
        this.scheme = scheme;
        this.description = description;
        this.networkBased = networkBased;
    }

    /**
     * 根据scheme字符串获取对应的枚举值
     *
     * @param scheme scheme字符串
     *
     * @return 对应的UriScheme枚举值，如果未找到则返回CUSTOM
     */
    public static UriScheme fromScheme(final String scheme) {
        if (scheme == null || scheme.isEmpty()) {
            return CUSTOM;
        }

        for (final UriScheme uriScheme : UriScheme.values()) {
            if (uriScheme.value().equalsIgnoreCase(scheme)) {
                return uriScheme;
            }
        }

        return CUSTOM;
    }

    /**
     * 判断是否为网络协议
     *
     * @param scheme scheme字符串
     *
     * @return 是否为网络协议
     */
    public static boolean isNetworkScheme(final String scheme) {
        final UriScheme uriScheme = fromScheme(scheme);
        return uriScheme.isNetworkBased();
    }

    /**
     * 获取所有网络协议
     *
     * @return 网络协议列表
     */
    public static List<UriScheme> getNetworkSchemes() {
        final List<UriScheme> networkSchemes = new ArrayList<>();
        for (final UriScheme scheme : values()) {
            if (scheme.isNetworkBased()) {
                networkSchemes.add(scheme);
            }
        }
        return networkSchemes;
    }

    /**
     * 获取所有本地协议
     *
     * @return 本地协议列表
     */
    public static List<UriScheme> getLocalSchemes() {
        final List<UriScheme> localSchemes = new ArrayList<>();
        for (final UriScheme scheme : values()) {
            if (!scheme.isNetworkBased()) {
                localSchemes.add(scheme);
            }
        }
        return localSchemes;
    }

    public String value() {
        return this.scheme;
    }

    public String description() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.scheme + " - " + this.description + " (" + (this.networkBased ? "网络" : "本地") + ")";
    }
}
