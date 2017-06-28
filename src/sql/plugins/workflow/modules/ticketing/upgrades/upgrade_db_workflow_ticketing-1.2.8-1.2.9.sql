ALTER TABLE workflow_task_ticketing_reply_config ADD close_ticket int(1) NOT NULL default '0';
UPDATE workflow_task_ticketing_reply_config SET close_ticket = 1;
