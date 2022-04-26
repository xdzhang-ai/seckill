package com.chffy.seckill.validator;

import com.chffy.seckill.utils.ValidatorUtil;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (required)
            return ValidatorUtil.isMobile(value);
        if (StringUtils.isEmpty(value))
            return true;
        return ValidatorUtil.isMobile(value);
    }
}
