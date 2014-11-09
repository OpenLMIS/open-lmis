/*
 * This program is part of the OpenLMIS logistics management information system platform software.
 * Copyright © 2013 VillageReach
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with this program.  If not, see http://www.gnu.org/licenses.  For additional information contact info@OpenLMIS.org. 
 */

//Add the key constants for localStorage('key','value') pairs in the application
var localStorageKeys = { RIGHT:"RIGHTS", CURRENCY:"CURRENCY",USERNAME:"USERNAME",USER_ID:"USER_ID", REPORTS:{STOCK_IMBALANCE:"REPORT_STOCK_IMBALANCE",SUPPLY_STATUS:"REPORT_SUPPLY_STATUS"},
    PREFERENCE : {
        DEFAULT_PROGRAM: "DEFAULT_PROGRAM",
        DEFAULT_SCHEDULE: "DEFAULT_SCHEDULE",
        DEFAULT_PERIOD: "DEFAULT_PERIOD",
        DEFAULT_SUPERVISORY_NODE: "DEFAULT_SUPERVISORY_NODE",
        DEFAULT_GEOGRAPHIC_ZONE: "DEFAULT_GEOGRAPHIC_ZONE",
        DEFAULT_PRODUCT: "DEFAULT_PRODUCT",
        DEFAULT_PRODUCTS: "DEFAULT_PRODUCTS",
        DEFAULT_FACILITY: "DEFAULT_FACILITY",
        ALERT_SMS_NOTIFICATION_OVERDUE_REQUISITION: "ALERT_SMS_NOTIFICATION_OVERDUE_REQUISITION",
        ALERT_EMAIL_OVER_DUE_REQUISITION: "ALERT_EMAIL_OVER_DUE_REQUISITION"
    },
    DASHBOARD_FILTERS : {
        "SUMMARY": "DASHBOARD_SUMMARY_PAGE",
        "STOCK": "DASHBOARD_STOCK_EFFICIENCY_PAGE",
        "STOCK-OUT": "DASHBOARD_STOCKED_OUT_PAGE",
        "DISTRICT-STOCK-OUT": "DASHBOARD_STOCKED_OUT_BY_DISTRICT_PAGE",
        "DISTRICT-STOCK-OUT-DETAIL": "DASHBOARD_STOCKED_OUT_DETAIL_PAGE",
        "ORDER": "DASHBOARD_ORDER_TURNAROUND_PAGE",
        "NOTIFICATION" : "DASHBOARD_NOTIFICATION_PAGE",
        "RNR-STATUS-SUMMARY" : "DASHBOARD_RNR_STATUS_SUMMARY_PAGE"
    }
};
