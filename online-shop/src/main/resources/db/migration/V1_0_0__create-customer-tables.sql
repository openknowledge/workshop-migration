create sequence SEQ_ADDRESS start with 1 increment by 50;
create sequence SEQ_CUSTOMER start with 1 increment by 50;
create table TAB_ADDRESS (ADR_CUS_ID bigint not null, ADR_ID bigint not null, ADR_CITY varchar(255), ADR_HOUSE_NUMBER varchar(255), ADR_STREET varchar(255), ADR_ZIP_CODE varchar(255), primary key (ADR_ID));
create table TAB_CUSTOMER (CUS_DEFAULT_BILLING_ADR_ID bigint unique, CUS_DEFAULT_DELIVERY_ADR_ID bigint unique, CUS_ID bigint not null, CUS_CUSTOMER_NUMBER varchar(255), primary key (CUS_ID));
alter table if exists TAB_ADDRESS add constraint FK_ADR_CUS_ID foreign key (ADR_CUS_ID) references TAB_CUSTOMER;
alter table if exists TAB_CUSTOMER add constraint FK_CUS_DEFAULT_BILLING_ADR_ID foreign key (CUS_DEFAULT_BILLING_ADR_ID) references TAB_ADDRESS;
alter table if exists TAB_CUSTOMER add constraint FK_CUS_DEFAULT_DELIVERY_ADR_ID foreign key (CUS_DEFAULT_DELIVERY_ADR_ID) references TAB_ADDRESS;
