 CREATE UNIQUE INDEX idx_vw_stockouts ON vw_stockouts (uuid);

 CREATE OR REPLACE FUNCTION refresh_weekly_nos_soh()
   RETURNS INT LANGUAGE plpgsql
 AS $$
 BEGIN
   REFRESH MATERIALIZED VIEW CONCURRENTLY vw_weekly_nos_soh;
   RETURN 1;
 END $$;
