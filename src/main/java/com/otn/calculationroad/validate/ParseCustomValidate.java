package com.otn.calculationroad.validate;

import com.otn.calculationroad.anno.ValidateVariable;
import com.otn.calculationroad.en.StateCode;
import com.otn.calculationroad.exception.ParamterException;
import com.otn.calculationroad.vo.CacuParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @BelongsProject: calculationroad
 * @Author: zf
 * @CreateTime: 2019-12-09 17:07
 * @Description: 自定义注解校验
 */
@Slf4j
public class ParseCustomValidate implements ConstraintValidator<ValidateVariable, Object> {

    public static final String IS_NULL = "is null";

    private String filed;
    private String message;

    @Override
    public void initialize(ValidateVariable validateVariable) {
        log.info("field is {}", validateVariable.field());
        this.filed = validateVariable.field();
        this.message = validateVariable.message();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (o instanceof String) {
            String stringValue = String.valueOf(o);
            ParseNull(stringValue);
        } else if (o instanceof Double) {
            Double doubleValue = Double.valueOf(String.valueOf(o));
            ParseNull(doubleValue);
        } else if (o instanceof Boolean) {
            Boolean booleanValue = Boolean.valueOf(String.valueOf(o));
            ParseNull(booleanValue);
        }else{
            ParseNull(null);
        }
        return true;
    }

    /**
     * 判空
     * @param value
     */
    private void ParseNull(Object value) {
        if(ObjectUtils.isEmpty(value)){
            //抛错
            String errorMsg = filed + IS_NULL;
            StateCode.INVAILD.setMsg(errorMsg);
            throw new ParamterException(StateCode.INVAILD.getCode(),StateCode.INVAILD.getMsg());
        }
    }

/*    public static void main(String[] args) {
        try{
            throw new ParamterException(400,"is null");
        }catch (ParamterException e){
            System.out.println(e.getErrorCode());
            System.out.println(e.getMessage());
        }
    }*/
}