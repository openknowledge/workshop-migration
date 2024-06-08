create function update_order() returns trigger
language plpgsql
as $$
begin
  update public.TAB_ORDER
    set ORD_STATUS = 'OFFERED'
    where ORD_CUS_ID
    in (select CUS_ID from public.TAB_CUSTOMER where CUS_CUSTOMER_NUMBER = NEW.OFF_CUSTOMER_NUMBER);
  return null;
end;
$$;

create trigger update_order
  after insert on checkout.TAB_OFFER
  for each row
  execute function update_order();
