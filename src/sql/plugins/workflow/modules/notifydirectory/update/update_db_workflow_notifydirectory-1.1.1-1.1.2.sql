ALTER TABLE task_notify_directory_cf ADD COLUMN position_directory_entry_user_guid INT DEFAULT -1 AFTER position_directory_entry_sms;
ALTER TABLE task_notify_directory_cf ADD COLUMN is_notify_by_user_guid SMALLINT(6) NOT NULL DEFAULT 0 AFTER is_notify_by_sms;
