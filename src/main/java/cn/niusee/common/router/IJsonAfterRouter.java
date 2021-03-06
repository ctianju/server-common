/*
 * Niusee server-common
 *
 * Copyright 2015-2017 by Niusee.inc. All rights reserved.
 */
package cn.niusee.common.router;

import static spark.Spark.after;
import static spark.Spark.exception;

/**
 * 返回JSON数据的全局After控制的Router
 *
 * @author Qianliang Zhang
 */
public interface IJsonAfterRouter extends IRouter {
    /**
     * 全局服务的后处理
     */
    default void afterHandler() {
        // 错误处理
        exception(Exception.class, (exception, request, response) -> {
            response.type("application/json");
            if (exception instanceof RouteException) {
                RouteException routeException = (RouteException) exception;
                response.status(routeException.getResponseCode());
                response.body(combineErrorMsg(routeException.getErrorCode(), routeException.getErrorMessage()));
            } else {
                response.status(400);
                response.body(combineErrorMsg(UNKNOWN_ERROR_CODE, UNKNOWN_ERROR_MSG + exception.getMessage()));
            }
        });

        // 全局后处理
        after((request, response) -> response.type("application/json"));
    }

    /**
     * 组合错误消息
     *
     * @param errorCode 错误代码
     * @param errorMsg  错误信息
     * @return 组合后的错误消息
     */
    default String combineErrorMsg(int errorCode, String errorMsg) {
        return "{\"error_code\":" + errorCode + ",\"error_msg\":\"" + errorMsg + "\"}";
    }
}
