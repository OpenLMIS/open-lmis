package org.openlmis.web.util;

import org.apache.commons.lang3.ObjectUtils;
import org.openlmis.web.model.ReviewDataColumnOrder;
import org.openlmis.web.model.SynchronizedDistribution;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public final class SynchronizedDistributionComparators {
  private static final Map<String, Comparator<SynchronizedDistribution>> MAP = new HashMap<>();

  static {
    MAP.put("province", SynchronizedDistributionComparator.PROVINCE);
    MAP.put("deliveryZone", SynchronizedDistributionComparator.DELIVERY_ZONE);
    MAP.put("period", SynchronizedDistributionComparator.PERIOD);
    MAP.put("initiated", SynchronizedDistributionComparator.INITIATED);
    MAP.put("synchronized", SynchronizedDistributionComparator.SYNCHRONIZED);
    MAP.put("lastViewed", SynchronizedDistributionComparator.LAST_VIEWED);
    MAP.put("lastEdited", SynchronizedDistributionComparator.LAST_EDITED);
    MAP.put("editedBy", SynchronizedDistributionComparator.EDITED_BY);
  }

  private SynchronizedDistributionComparators() {
    throw new UnsupportedOperationException();
  }

  public static Comparator<SynchronizedDistribution> get(ReviewDataColumnOrder order) {
    Comparator<SynchronizedDistribution> comparator = MAP.get(order.getColumn());
    return order.getDescending() ? Collections.reverseOrder(comparator) : comparator;
  }

  private enum SynchronizedDistributionComparator implements Comparator<SynchronizedDistribution> {
    PROVINCE {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getProvince(), b.getProvince());
      }
    },
    DELIVERY_ZONE {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getDeliveryZone().getName(), b.getDeliveryZone().getName());
      }
    },
    PERIOD {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getPeriod().getName(), b.getPeriod().getName());
      }
    },
    INITIATED {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getInitiated(), b.getInitiated());
      }
    },
    SYNCHRONIZED {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getSync(), b.getSync());
      }
    },
    LAST_VIEWED {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getLastViewed(), b.getLastViewed());
      }
    },
    LAST_EDITED {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getLastEdited(), b.getLastEdited());
      }
    },
    EDITED_BY {
      @Override
      public int compare(SynchronizedDistribution a, SynchronizedDistribution b) {
        return ObjectUtils.compare(a.getEditedBy(), b.getEditedBy());
      }
    };

  }
}
