UPDATE task_notify_directory_cf tndcf SET id_directory_entry_email =(select entry_position from directory_entry entry where entry.id_entry=tndcf.id_directory_entry_email );
UPDATE task_notify_directory_cf tndcf SET id_directory_entry_sms =(select entry_position from directory_entry entry where entry.id_entry=tndcf.id_directory_entry_sms );

ALTER TABLE task_notify_directory_cf CHANGE COLUMN id_directory_entry_email position_directory_entry_email INT DEFAULT NULL;
ALTER TABLE task_notify_directory_cf CHANGE COLUMN id_directory_entry_sms position_directory_entry_sms INT DEFAULT NULL;