ALTER TABLE em DROP PRIMARY KEY;
ALTER TABLE em ADD COLUMN employee_id int not null auto_increment primary key;
ALTER TABLE em ADD INDEX (name);