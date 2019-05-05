package com.example.car.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;

/**
 * @author Kartist 2018/8/15 10:50
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * 全局异常处理
     *
     * @param e
     * @param request
     * @param response
     * @throws IOException
     */
    @ExceptionHandler(Exception.class)
    public void handleViolationException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String requestURI = request.getRequestURI();
        log.error("uri : {} ,handle exception : ", requestURI, e);
            responseWithJson(response, new JsonResult("Fail", e.getMessage()));

    }


    private void responseWithJson(HttpServletResponse response, JsonResult jsonResult) throws IOException {
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(jsonResult));
    }

}
