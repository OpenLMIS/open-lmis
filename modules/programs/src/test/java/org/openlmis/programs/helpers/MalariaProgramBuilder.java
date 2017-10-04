package org.openlmis.programs.helpers;

import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.joda.time.DateTime.now;

public class MalariaProgramBuilder {
    private String username = randomAlphanumeric(10);
    private Date reportedDate = now().toDate();
    private Date periodStartDate = now().minusDays(nextInt(10)).toDate();
    private Date periodEndDate = now().minusDays(nextInt(10)).toDate();
    private List<Implementation> implementations = newArrayList(ImplementationBuilder.fresh().build(),
            ImplementationBuilder.fresh().build());

    private static MalariaProgramBuilder malariaProgramBuilder;

    public static MalariaProgramBuilder fresh() {
        malariaProgramBuilder = new MalariaProgramBuilder();
        return malariaProgramBuilder;
    }

    public MalariaProgramBuilder with(Consumer<MalariaProgramBuilder> consumer) {
        consumer.accept(malariaProgramBuilder);
        return malariaProgramBuilder;
    }

    public MalariaProgram build() {
        return new MalariaProgram(username, reportedDate, periodStartDate, periodEndDate, implementations);
    }
}
