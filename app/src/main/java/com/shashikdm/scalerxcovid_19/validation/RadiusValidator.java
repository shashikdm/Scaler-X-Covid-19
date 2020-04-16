package com.shashikdm.scalerxcovid_19.validation;

public class RadiusValidator implements Validator {
    @Override
    public Boolean validate(Object obj) {
        try {
            int radius = Integer.parseInt(obj.toString());
            return radius > 0 && radius < 1000000;
        } catch (Exception e) {
            return false;
        }

    }
}
