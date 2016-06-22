package org.openlmis.distribution.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_EMPTY;

@EqualsAndHashCode(callSuper = false)
@JsonSerialize(include = NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EditedItemUI {

    @Getter
    @Setter
    private List<String> translate = new ArrayList<>();

    @Getter
    @Setter
    private List<String> noTranslate = new ArrayList<>();

    EditedItemUI() {
    }

    void addTransalte(String value) {
        this.translate.add(value);
    }

    void addNoTransalte(String value) {
        this.translate.add(value);
    }

}
