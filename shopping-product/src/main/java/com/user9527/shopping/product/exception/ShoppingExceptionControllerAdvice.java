package com.user9527.shopping.product.exception;

import com.user9527.common.exception.BizCodeEnum;
import com.user9527.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yangtao
 * @version 1.0
 * @date 2021/3/27 19:48
 */
@Slf4j
//@ResponseBody
//@ControllerAdvice(basePackages = "com.user9527.shopping.product.controller")
@RestControllerAdvice(basePackages = "com.user9527.shopping.product.controller")
public class ShoppingExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}，异常类型{}",e.getLocalizedMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach(item ->{
            errorMap.put(item.getField(),item.getDefaultMessage());
        });
        return R.error(BizCodeEnum.VAILD_EXCEPTION.getCode(), BizCodeEnum.VAILD_EXCEPTION.getMsg()).put("data", errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误：{}",throwable);
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());
    }
}
