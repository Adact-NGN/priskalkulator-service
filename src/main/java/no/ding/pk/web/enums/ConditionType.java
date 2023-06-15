package no.ding.pk.web.enums;

import lombok.Getter;

@Getter
public enum ConditionType {
    ZPTR("ZPTR"),
    ZR05("ZR05"),
    ZR02("ZR02"),
    ZPRK("ZPRK"),
    ZH00("ZH00"),
    ZH02("ZH02"),
    ZH03("ZH03"),
    ZGEB("ZGEB"),
    ZBEH("ZBEH"),
    ZMIL("ZMIL");

    private final String conditionName;

    ConditionType(String conditionName) {
        this.conditionName = conditionName;
    }
}
