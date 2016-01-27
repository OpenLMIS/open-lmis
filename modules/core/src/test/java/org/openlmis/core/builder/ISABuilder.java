package org.openlmis.core.builder;


import org.openlmis.core.domain.ISA;

public class ISABuilder
{
    public static ISA build()
    {
        return new ISA.Builder().populationSource(1L).adjustmentValue(1).bufferPercentage(2.0).dosesPerYear(3).maximumValue(4)
                .minimumValue(5).wastageFactor(6.0).whoRatio(7.0).build();
    }

    public static boolean defaultIsaEquals(ISA isa)
    {
        return isa.getPopulationSource().equals(1L) &&
                isa.getAdjustmentValue().equals(1) &&
                isa.getBufferPercentage().equals(2.0) &&
                isa.getDosesPerYear().equals(3) &&
                isa.getMaximumValue().equals(4) &&
                isa.getMinimumValue().equals(5) &&
                isa.getWastageFactor().equals(6.0) &&
                isa.getWhoRatio().equals(7.0);
    }

}
