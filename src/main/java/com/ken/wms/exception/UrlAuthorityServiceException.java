package com.ken.wms.exception;

/**
 * UrlAuthorityServiceException 异常
 * @author 302294马丽丽
 * @since 2017/8/22 19:29
 */

public class UrlAuthorityServiceException extends BusinessException {
    public static final String ENTITY_IS_NULL = "urlAuthorityDTOIsNull";
    public UrlAuthorityServiceException(Exception e) {
        super(e);
    }

    public UrlAuthorityServiceException(Exception e, String exceptionDesc) {
        super(e, exceptionDesc);
    }

    public UrlAuthorityServiceException(String exceptionDesc) {
        super(exceptionDesc);
    }

    public UrlAuthorityServiceException() {
        super();
    }
}
