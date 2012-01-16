--
-- WORKFLOWNOTIFYDIRECTORY-18 : Adapt the notification for multiple files as attachments 
--
ALTER TABLE task_notify_directory_ef DROP PRIMARY KEY, 
ADD PRIMARY KEY (id_task, position_directory_entry_file);
