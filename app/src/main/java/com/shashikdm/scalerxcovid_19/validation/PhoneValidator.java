package com.shashikdm.scalerxcovid_19.validation;

import java.util.regex.Pattern;

public class PhoneValidator implements Validator {

    @Override
    public Boolean validate(Object obj) {
        String input = obj.toString();
        Pattern pattern = Pattern.compile("^[6-9]\\d{9}$");
        return pattern.matcher(input).matches();
    }
}
