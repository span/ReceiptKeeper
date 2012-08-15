DROP TABLE IF EXISTS "accounts";
CREATE TABLE "accounts" ("row" TEXT NOT NULL ,"name" TEXT NOT NULL ,"id" INTEGER PRIMARY KEY NOT NULL );
INSERT INTO "accounts" VALUES('B1','immaterial_assets',1000);
INSERT INTO "accounts" VALUES('B2','buildings_and_property',1110);
INSERT INTO "accounts" VALUES('B3','property_and_other_assets_non_taxrefund',1130);
INSERT INTO "accounts" VALUES('B4','machines_and_inventory',1220);
INSERT INTO "accounts" VALUES('B5','other_property_assets',1300);
INSERT INTO "accounts" VALUES('B6','merchandise_store',1400);
INSERT INTO "accounts" VALUES('B7','customer_billlings ',1500);
INSERT INTO "accounts" VALUES('B8','other_billlings',1600);
INSERT INTO "accounts" VALUES('B9','cash_and_bank',1920);
INSERT INTO "accounts" VALUES('B10','own_capital',2010);
INSERT INTO "accounts" VALUES('U2','expansion_fund',2050);
INSERT INTO "accounts" VALUES('U3','replacement_fund',2060);
INSERT INTO "accounts" VALUES('U4','forest_account',2070);
INSERT INTO "accounts" VALUES('U1','periodisation_fund',2080);
INSERT INTO "accounts" VALUES('B13','lend_debt ',2330);
INSERT INTO "accounts" VALUES('B15','supplier_debt',2440);
INSERT INTO "accounts" VALUES('B14','tax_debt ',2610);
INSERT INTO "accounts" VALUES('B16','other_debt',2900);
INSERT INTO "accounts" VALUES('R1','sale_work_taxable_income',3000);
INSERT INTO "accounts" VALUES('R2','non_taxable_income',3100);
INSERT INTO "accounts" VALUES('R3','car_and_house_privilige',3200);
INSERT INTO "accounts" VALUES('R5','merchandise_material_and_services',4000);
INSERT INTO "accounts" VALUES('R6','other_external_costs',6900);
INSERT INTO "accounts" VALUES('R7','hired_staff',7000);
INSERT INTO "accounts" VALUES('R9','depreciation_and_impairments_of_property',7820);
INSERT INTO "accounts" VALUES('R10','depreciation_and_impairments_of_inventory_and_immaterial_assets',7830);
INSERT INTO "accounts" VALUES('R4','interest_income',8310);
INSERT INTO "accounts" VALUES('R8','interest_cost',8410);