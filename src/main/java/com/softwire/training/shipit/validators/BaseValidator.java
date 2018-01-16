package com.softwire.training.shipit.validators;

import com.softwire.training.shipit.exception.ValidationException;
import com.softwire.training.shipit.model.OrderLine;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseValidator<T>
{
    List<String> errors;
    private Class<T> clazz;

    BaseValidator(Class<T> clazz)
    {
        this.clazz = clazz;
        errors = new ArrayList<String>();
    }

    public void validate(Object target) throws ValidationException
    {
        errors = new ArrayList<String>();
        if (!clazz.equals(target.getClass()))
        {
            addError(String.format("Incorrect type, got %s, but expected %s", target.getClass(), clazz));
        }
        else
        {
            doValidation((T) target);
        }

        if (errors.size() > 0)
        {
            StringBuilder builder = new StringBuilder(String.format("Validation of %s failed with errors: \n", target));
            for (String error : errors)
            {
                builder.append(error).append('\n');
            }
            throw new ValidationException(builder.toString());
        }
    }

    protected abstract void doValidation(T target);

    void addError(String error)
    {
        errors.add(error);
    }

    void addErrors(List<String> errors)
    {
        this.errors.addAll(errors);
    }

    /**
     * Object validators
     */

    void assertNotNull(String fieldName, Object value)
    {
        if (value == null)
        {
            addError(String.format("Field %s cannot be null", fieldName));
        }
    }

    /**
     * String validators
     */

    void assertNotBlank(String fieldName, String value)
    {
        if (StringUtils.isBlank(value))
        {
            addError(String.format("Field %s cannot be blank", fieldName));
        }
    }

    void assertNumeric(String fieldName, String value)
    {
        if (!StringUtils.isNumeric(value))
        {
            addError(String.format("Field %s must be numeric", fieldName));
        }
    }

    void assertMaxLength(String fieldName, String value, int maxLength)
    {
        if (value.length() > maxLength)
        {
            addError(String.format("Field %s must be shorter than %s characters", fieldName, maxLength));
        }
    }

    void assertExactLength(String fieldName, String value, int exactLength)
    {
        if (value.length() != exactLength)
        {
            addError(String.format("Field %s must be exactly %s characters", fieldName, exactLength));
        }
    }

    /**
     * Numeric validators
     */

    void assertNonNegative(String fieldName, int value)
    {
        if (value < 0)
        {
            addError(String.format("Field %s must be non-negative", fieldName));
        }
    }

    void assertNonNegative(String fieldName, float value)
    {
        if (value < 0)
        {
            addError(String.format("Field %s must be non-negative", fieldName));
        }
    }

    /**
     * Specific validators
     */

    void validateGtin(String value)
    {
        assertNotBlank("gtin", value);
        assertNumeric("gtin", value);
        assertMaxLength("gtin", value, 13);
    }

    void validateGcp(String value)
    {
        assertNotBlank("gcp", value);
        assertNumeric("gcp", value);
        assertMaxLength("gcp", value, 13);
    }

    void validateWarehouseId(int warehouseId)
    {
        assertNonNegative("warehouseId", warehouseId);
    }

    void validateOrderLines(List<OrderLine> orderLines)
    {
        Set<String> gtins = new HashSet<String>(orderLines.size());
        for (OrderLine orderLine : orderLines)
        {
            OrderLineValidator orderLineValidator = new OrderLineValidator();
            orderLineValidator.doValidation(orderLine);
            addErrors(orderLineValidator.errors);

            if (gtins.contains(orderLine.getGtin()))
            {
                addError(String.format("Order contains duplicate GTINs: %s", orderLine.getGtin()));
            }
            else
            {
                gtins.add(orderLine.getGtin());
            }
        }
    }
}
