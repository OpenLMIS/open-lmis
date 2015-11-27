/*
 *
 * Electronic Logistics Management Information System (eLMIS) is a supply chain management system for health commodities in a developing country setting.
 *
 *  Copyright (C) 2015  John Snow, Inc (JSI). This program was produced for the U.S. Agency for International Development (USAID). It was prepared under the USAID | DELIVER PROJECT, Task Order 4.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 *
 *    You should have received a copy of the GNU Affero General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openlmis.vaccine.utils;/*
 * This program was produced for the U.S. Agency for International Development. It was prepared by the USAID | DELIVER PROJECT, Task Order 4. It is part of a project which utilizes code originally licensed under the terms of the Mozilla Public License (MPL) v2 and therefore is licensed under MPL v2 or later.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the Mozilla Public License as published by the Mozilla Foundation, either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Mozilla Public License for more details.
 *
 * You should have received a copy of the Mozilla Public License along with this program. If not, see http://www.mozilla.org/MPL/
 */

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CustomPeriodDateUtil {
    public static final int LAST_THREE_MONTHS=1;
    public static final int LAST_SIX_MONTHS=2;
    public static final int LAST_ONE_YEAR=3;

public static String getEndDate(){
    String endDate="";
    SimpleDateFormat dateFormat= new SimpleDateFormat("YYYY-MM-dd");
    Calendar calendar= Calendar.getInstance();
    endDate=dateFormat.format(calendar.getTime());
    return endDate;
}
    public static String getStartDate(int choise){
        String startDate="";
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat dateFormat= new SimpleDateFormat("YYYY-MM-dd");
        switch (choise){
            case LAST_THREE_MONTHS :
                calendar.add(Calendar.MONTH,-3);
                break;
            case LAST_SIX_MONTHS :
                calendar.add(Calendar.MONTH,-3);
                break;
            case LAST_ONE_YEAR :
                calendar.add(Calendar.YEAR,-1);
                break;
        }

        startDate=dateFormat.format(calendar.getTime());
        return startDate;

    }
    public static void main(String[] a){
        System.out.println(" date is "+ getStartDate(1));
    }

}
