delete from rights where name = 'VIEW_PIPELINE_EXPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_PIPELINE_EXPORT','REPORT','Permission to view Pipeline export Report');
