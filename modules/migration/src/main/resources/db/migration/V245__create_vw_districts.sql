CREATE VIEW vw_districts
AS
select d.id district_id, d.name district_name, r.id region_id, r.name region_name, z.id zone_id, z.name zone_name, z.parentId parent   from geographic_zones d
	join geographic_zones r on d.parentId = r.id
	join geographic_zones z on z.id = r.parentId;
