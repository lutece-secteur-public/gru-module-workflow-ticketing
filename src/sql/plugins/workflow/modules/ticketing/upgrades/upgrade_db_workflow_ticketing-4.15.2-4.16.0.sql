-- Ajout variable email pour les erreurs de transfert S3 courrier postal
INSERT INTO core_datastore
(entity_key, entity_value)
VALUES('ticketing.configuration.draft.alert.error.mail', 'DSTISTINBSUNparticipationcitoyenne@paris.fr');