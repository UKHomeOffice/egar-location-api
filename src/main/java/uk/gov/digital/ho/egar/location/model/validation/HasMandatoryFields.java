package uk.gov.digital.ho.egar.location.model.validation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;


import javax.validation.Constraint;
import javax.validation.Payload;


@Documented
@Constraint(validatedBy = { LocationValidator.class } )
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HasMandatoryFields {
    String message() default "{location.model.validation.error.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}