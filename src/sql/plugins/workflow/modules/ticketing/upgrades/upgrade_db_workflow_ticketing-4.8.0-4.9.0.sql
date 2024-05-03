-- insertion du guid de Francoise Signol comme defaut pour paris famille
INSERT INTO core_datastore (entity_key, entity_value) VALUES
('ticketing.configuration.agent.paris.famille.defaut', 'C3CAA162EC6B11E6A6EDF5019677183C00000000');

-- nettoytage tables inutiles car suppression numero facilfamilles
DROP TABLE IF EXISTS workflow_task_ticketing_automatic_assignment_config;
DROP TABLE IF EXISTS workflow_task_ticketing_facilfamilles_config;
DROP TABLE IF EXISTS workflow_task_ticketing_facilfamilles_history;