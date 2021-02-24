package com.lunatech.training.quarkus;

import io.quarkus.qute.TemplateData;
import io.quarkus.qute.TemplateExtension;

// @TemplateData(target = String.class)
@TemplateExtension
public class StringExtension {

    public static String shout(String in) {
        return in + "!";
    }

    public static String shout(String in, String append) {
        return in + append;
    }
}
