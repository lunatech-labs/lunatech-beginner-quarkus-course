package com.lunatech.training.quarkus;

import io.quarkus.qute.TemplateExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

@TemplateExtension
public class MoneyExtensions {

    static String money(BigDecimal value) {
        return "€ " + value.setScale(2, RoundingMode.HALF_EVEN);
    }

}
