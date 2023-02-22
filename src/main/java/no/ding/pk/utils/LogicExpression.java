package no.ding.pk.utils;

import lombok.Builder;
import lombok.Data;
import no.ding.pk.web.enums.LogicComparator;
import no.ding.pk.web.enums.MaterialField;

@Data
@Builder
public class LogicExpression {
    MaterialField field;
    String value;
    LogicComparator comparator;
}
