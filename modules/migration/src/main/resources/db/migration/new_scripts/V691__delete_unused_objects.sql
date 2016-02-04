
-- DHIS2 export related objects
DROP VIEW IF EXISTS vw_dhis2_indicators;
DROP TABLE IF EXISTS interface_facility_mappings;
DROP TABLE IF EXISTS interface_product_data_elements;
DROP TABLE IF EXISTS interface_product_mappings;
DROP TABLE IF EXISTS interface_data_elements;
DROP TABLE IF EXISTS interface_logs;
DROP TABLE IF EXISTS interface_partners;
DROP TABLE IF EXISTS interface_dhis2_facilities;

-- inventory module mockup
DROP TABLE IF EXISTS on_hand;
DROP TABLE IF EXISTS inventory_batches;
DROP TABLE IF EXISTS inventory_transactions;
DROP TABLE IF EXISTS received_status;
DROP TABLE IF EXISTS transaction_types;
DROP TABLE IF EXISTS countries;

-- other objects
--functions
drop function if exists fn_current_pd(integer, integer, character varying);
drop function if exists fn_delete_period(integer);
drop function if exists fn_e2e_pipeline(integer, integer, integer);
drop function if exists fn_e2e_vaccine(integer, integer, integer, character varying);
drop function if exists fn_get_vaccine_export_query(integer, integer, integer);
drop function if exists fn_get_vaccine_facility_targets(integer, integer);
drop function if exists fn_import_scmgr_rnr(character varying);
drop function if exists fn_ivd_test_data(integer, integer, character varying, boolean);
drop function if exists fn_populate_dhis2_mapping();
drop function if exists fn_previous_period(v_program_id integer, v_facility_id integer, v_period_id integer, v_productcode character varying);
drop function if exists fn_save_user_preference2(in_userid integer, in_programid integer, in_facilityid integer, in_productid character varying);
drop function if exists fn_vaccine_test_data(integer, integer, character varying, boolean);

--- views
drop view if exists vw_dhis2_stock_status;
drop view if exists vw_district_consumption_summary;
drop view if exists vw_e2e_pipeline;
drop view if exists vw_equipment_operational_status;
drop view if exists vw_number_rnr_created_by_facility;
drop view if exists vw_order_fill_rate_details;
drop view if exists vw_period_factype_line_items;
drop view if exists vw_regimen_summary;
drop view if exists vw_rg_period_factype_line_items;
drop view if exists vw_user_geo_facilities;
drop view if exists vw_user_program_facilities;
drop view if exists vw_user_role_program_rg;

-- tables
DROP TABLE IF EXISTS dhis2_products;
drop table if exists product_code_change_log;
