
INSERT INTO equipment_types
          ( code       , name                     , displayOrder)
  VALUES  ( 'LAB'      , 'Analyzer, Laboratory'   , 1),
          ( 'ANS'      , 'Anesthesia Unit'        , 2);


INSERT INTO equipment_operational_status
          ( name                      , displayOrder)
  VALUES  (  'Fully Operational'      , 1),
          (  'Partially Operational'  , 2 ),
          (  'Not Operational'        , 3 );