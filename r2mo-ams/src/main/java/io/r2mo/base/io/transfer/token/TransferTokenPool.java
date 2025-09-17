package io.r2mo.base.io.transfer.token;

/**
 * 存储传输令牌的接口，用于定义令牌的存储和管理方法
 *
 * @author lang : 2025-09-16
 */
public interface TransferTokenPool {

    boolean runSave(TransferToken token, long expiredAt);

    boolean runExtend(String token, long expiredAt);

    boolean runDelete(String token);

    int runClean(boolean expiredOnly);

    TransferToken findBy(String token);

    boolean isExists(String token);

    long getExpired(String token);
}
