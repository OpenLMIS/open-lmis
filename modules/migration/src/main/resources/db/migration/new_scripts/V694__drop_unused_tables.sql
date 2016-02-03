DROP TABLE IF EXISTS budgets;
DROP TABLE IF EXISTS distribution_types;
DROP TABLE IF EXISTS vaccine_targets;
DROP TABLE IF EXISTS vaccine_quantifications;
DROP TABLE IF EXISTS vaccine_storage;
DROP TABLE IF EXISTS storage_types;
DROP TABLE IF EXISTS temperature;
DROP TABLE IF EXISTS vaccine_storage;
DROP TABLE IF EXISTS vaccine_distribution_batches;

--Questionable
DROP TABLE IF EXISTS vaccine_report_target_line_items;
DROP TABLE IF EXISTS vaccine_facility_targets;

DROP TABLE IF EXISTS odk_account CASCADE;
DROP TABLE IF EXISTS odk_proof_of_delivery_submission_data CASCADE;
DROP TABLE IF EXISTS odk_stock_status_submission CASCADE;
DROP TABLE IF EXISTS odk_proof_of_delivery_xform CASCADE;
DROP TABLE IF EXISTS odk_submission_data CASCADE;
DROP TABLE IF EXISTS odk_submission CASCADE;
DROP TABLE IF EXISTS odk_xform_survey_type CASCADE;
DROP TABLE IF EXISTS odk_xform CASCADE;
DROP TABLE IF EXISTS odk_znz_survey_lmis_submission CASCADE;
DROP TABLE IF EXISTS odk_znz_survey_randr_submission CASCADE;
DROP TABLE IF EXISTS odk_znz_survey_storage_submission CASCADE;
