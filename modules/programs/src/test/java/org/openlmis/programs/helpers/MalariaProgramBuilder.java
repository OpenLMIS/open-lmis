package org.openlmis.programs.helpers;

import com.natpryce.makeiteasy.Instantiator;
import com.natpryce.makeiteasy.Property;
import com.natpryce.makeiteasy.PropertyLookup;
import org.joda.time.DateTime;
import org.openlmis.programs.domain.malaria.Implementation;
import org.openlmis.programs.domain.malaria.MalariaProgram;

import java.util.Date;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.natpryce.makeiteasy.MakeItEasy.a;
import static com.natpryce.makeiteasy.MakeItEasy.make;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.joda.time.DateTime.now;
import static org.openlmis.programs.helpers.ImplementationBuilder.randomImplementation;


public class MalariaProgramBuilder {
    public static final Property<MalariaProgram, String> username = new Property<>();
    public static final Property<MalariaProgram, Date> reportedDate = new Property<>();
    public static final Property<MalariaProgram, Date> periodStartDate = new Property<>();
    public static final Property<MalariaProgram, Date> periodEndDate = new Property<>();
    public static final Property<MalariaProgram, List<Implementation>> implementations = new Property<>();

    public static final Instantiator<MalariaProgram> randomMalariaProgram = new Instantiator<MalariaProgram>() {
        @Override
        public MalariaProgram instantiate(PropertyLookup<MalariaProgram> lookup) {
            DateTime referenceDate = now();
            MalariaProgram malariaProgram = new MalariaProgram(
                    lookup.valueOf(username, randomAlphabetic(10)),
                    lookup.valueOf(reportedDate, referenceDate.toDate()),
                    lookup.valueOf(periodStartDate, referenceDate.minusDays(nextInt(10)).toDate()),
                    lookup.valueOf(periodEndDate, referenceDate.plusDays(nextInt(10)).toDate()),
                    lookup.valueOf(implementations, newArrayList(make(a(randomImplementation)), make(a(randomImplementation)))));
            return malariaProgram;
        }
    };
}
