package no.ding.pk.web.enums;

import lombok.Getter;

@Getter
public enum KeyCombinationType {
    A615("A615"),
    A783("A783"),
    A791("A791"),
    A790("A790"),
    A704("A704"),
    A766("A766"),
    A781("A781"),
    A780("A780"),
    A785("A785"),
    A784("A784"),
    A795("A795"),
    A798("A798"),
    A767("A767"),
    A786("A786"),
    A768("A768"),
    A805("A805"),
    A789("A789"),
    A775("A775"),
    A770("A770"),
    A765("A765"),
    A778("A778");

    private final String keyCombinationType;

    KeyCombinationType(String keyCombinationType) {
        this.keyCombinationType = keyCombinationType;
    }
}
