package walletmanager.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Currency;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String>
{
    @Override
    public String convertToDatabaseColumn(Currency currency)
    {
        if (currency == null)
        {
            return null;
        }
        return currency.getCurrencyCode();
    }

    @Override
    public Currency convertToEntityAttribute(String code)
    {
        if (code == null)
        {
            return null;
        }
        return Currency.getInstance(code);
    }
}