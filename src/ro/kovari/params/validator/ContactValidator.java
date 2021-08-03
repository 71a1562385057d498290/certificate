package ro.kovari.params.validator;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.util.List;
import java.util.Objects;

public class ContactValidator implements IValueValidator<List<String>> {

    @Override
    public void validate(String s, List<String> contacts) throws ParameterException {
        contacts.stream()
                .filter(contact -> Objects.isNull(contact) || contact.isEmpty() || !contact.contains("@"))
                .forEach(contact -> {
                    throw new ParameterException("Invalid contact");
                });
    }
}
