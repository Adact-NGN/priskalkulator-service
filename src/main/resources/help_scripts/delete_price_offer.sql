use pk_db_prod;

# Price rows
delete from price_rows WHERE id In (
    select pr.id from price_offers as po
                          left join sales_office so on po.id = so.po_sales_office_list_id
                          left join price_rows pr on so.id = pr.material_list_id
    where po.customer_number = 9999999 and customer_name = '');

# Zones
select z.id from price_offers as po
                     left join sales_office so on po.id = so.po_sales_office_list_id
                     left join zones z on so.id = z.zones_id
where po.customer_number = 9999999 and po.customer_name = '';

delete from zones where id in (select z.id from price_offers as po
                                                    left join sales_office so on po.id = so.po_sales_office_list_id
                                                    left join zones z on so.id = z.zones_id
                               where po.customer_number = 9999999 and po.customer_name = '');

# Zone materials
select pr.id from price_offers as po
                      left join sales_office so on po.id = so.po_sales_office_list_id
                      left join zones z on so.id = z.zones_id
                      left join price_rows pr on z.id = pr.zone_price_row_id
where po.customer_number = 9999999 and po.customer_name = '';

delete from price_rows WHERE id In (select pr.id from price_offers as po
                                                          left join sales_office so on po.id = so.po_sales_office_list_id
                                                          left join zones z on so.id = z.zones_id
                                                          left join price_rows pr on z.id = pr.zone_price_row_id
                                    where po.customer_number = 9999999 and po.customer_name = '');

# Sales office
delete from sales_office where id in (select so.id from price_offers as po
                                                            left join sales_office so on po.id = so.po_sales_office_list_id
                                      where po.customer_number = 9999999 and po.customer_name = '');

# Price offer
delete from price_offers where id in (select po.id from price_offers as po where po.customer_number = 9999999 and po.customer_name = '');
select po.id from price_offers as po where po.customer_number = 9999999 and po.customer_name = '';

