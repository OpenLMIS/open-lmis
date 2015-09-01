delete from rights where name = 'VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY','REPORT','Permission to View Replacement Plan Summary Report');
