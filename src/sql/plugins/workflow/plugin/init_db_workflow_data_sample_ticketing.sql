DELETE FROM workflow_workflow WHERE id_workflow >= 300 AND id_workflow < 400; 
INSERT INTO workflow_workflow (id_workflow, name, description, creation_date, is_enabled, workgroup_key) 
	VALUES	(301,'Workflow GRU','Workflow GRU','2016-01-13 08:36:34',1,'all');

DELETE FROM workflow_state WHERE id_workflow >= 300 AND id_workflow < 400;
INSERT INTO workflow_state (id_state, name, description, id_workflow, is_initial_state, is_required_workgroup_assigned, id_icon, display_order) 
	VALUES	(301,'Nouveau','Nouveau',301,1,0,NULL,1),
			(302,'A qualifier','A qualifier',301,0,0,NULL,2),
			(303,'A traiter','A traiter',301,0,0,NULL,3),
			(304,'En attente d\'informations de l\'usager','En attente d\'informations de l\'usager',301,0,0,NULL,4),
			(305,'Clos','Clos',301,0,0,NULL,5);

DELETE FROM workflow_action WHERE id_workflow >= 300 AND id_workflow < 400;
INSERT INTO workflow_action (id_action, name, description, id_workflow, id_state_before, id_state_after, id_icon, is_automatic, is_mass_action, display_order, is_automatic_reflexive_action) 
	VALUES 	(301,'Initialisation','Initialisation de la sollicitation',301,301,302,1,1,0,1,0),
			(302,'Qualifier','Qualifier',301,302,303,1,0,0,2,0),
			(303,'Requalifier','Requalifier',301,303,303,1,0,0,3,0),
            (304,'Escalader','Escalader',301,303,303,1,0,0,7,0),
			(305,'Assigner à une autre entité','Assigner une sollicitation à une autre entité',301,303,303,1,0,0,6,0),
            (306,'Assigner à un autre agent','Assigner une sollicitation à un autre agent',301,303,303,1,0,0,5,0),
			(307,'Me l\'assigner','M\'assigner une sollicitation',301,303,303,1,0,0,4,0),
			(308,'Demander des informations complémentaires à l\'usager','Demander des informations complémentaires à l\'usager',301,303,304,1,0,0,8,0),
			(309,'Répondre','Action de répondre pour l\'usager',301,304,303,1,0,0,9,0),
            (310,'Répondre','Action de répondre pour l\'agent',301,303,305,1,0,0,10,0),            
			(311, 'Ré-ouvrir la sollicitation', 'Réouverture d\'une sollicitation close', 301, 305, 303, 1, 0, 0, 11, 0),
            (312, 'Répondre à l\'escalade', 'Répondre à l\'escalade', 301, 303, 303, 1, 0, 0, 12, 0);
		
DELETE FROM workflow_task WHERE id_action >= 300 AND id_action < 450;
INSERT INTO workflow_task (id_task, task_type_key, id_action, display_order) 
	VALUES 	(301,'taskTicketingGenerateTicketReference',301,1),
			(302,'taskTicketingAssignUnitLinkedToCategory',301,2),
			-- 303 is reserved by module ticketing gru 	(303,'taskTicketingCreateCustomer',301,3)
            (321,'taskTicketingQualifyTicket',302,1),
            (322,'taskTicketingAssignTicketToUnit',302,2),
			(323,'taskTicketingAssignTicketToUser',302,3),
            (324,'taskTicketingModifyTicketCategory',302,4),
			(325, 'taskTypeComment', 302,5), 
            (341,'taskTicketingQualifyTicket',303,1),
            (342,'taskTicketingModifyTicketCategory',303,2),
            (343, 'taskTypeComment', 303,3),
            (351,'taskTicketingAssignTicketToUnit',305,1),
            (352, 'taskTypeComment', 305,2),
            (361,'taskTicketingAssignTicketToUser',306,1),
            (362, 'taskTypeComment', 306,2),
            (371,'taskTicketingAssignTicketToMe',307,1),
            (372, 'taskTypeComment', 307,2),
			(381,'taskTicketingAssignUpTicket',304,1),
			(382, 'taskTypeComment', 304,2),
            (390, 'taskTicketingEditTicket', 308,1), -- Ask for user information
            (400, 'taskTicketingEditTicket', 309,1), -- Reply to agent
			(420, 'taskTicketingReply', 310,1), -- Reply to user
			(431, 'taskTicketingQualifyTicket', 311, 1), 
			(432, 'taskTicketingAssignTicketToUnit', 311, 2), 
			(433, 'taskTicketingModifyTicketCategory', 311, 3), 
			(434, 'taskTypeComment', 311, 4),
            (441, 'taskTicketingReplyAssignUpTicket',312,1),
            (442, 'taskTypeComment', 312,2);


DELETE FROM workflow_task_comment_config WHERE id_task >= 300 AND id_task < 450;			
INSERT INTO workflow_task_comment_config (id_task, title, is_mandatory) 
	VALUES	(325, 'Commentaire', 0),
			(343, 'Commentaire', 0),
			(352, 'Commentaire', 0),
			(362, 'Commentaire', 0),
			(372, 'Commentaire', 0),
			(382, 'Commentaire', 0),
			(434, 'Motif de ré-ouverture', 1),
            (442, 'Commentaire', 0)
;

DELETE FROM workflow_task_ticketing_edit_ticket_config;
INSERT INTO workflow_task_ticketing_edit_ticket_config (id_task, message_direction, id_user_edition_action, default_message) 
    VALUES  (390, 1, 309, "Bonjour,
Merci de compléter les champs suivants.
En vous remerciant par avance,
Mairie de Paris"),  -- Ask for user information
            (400, 0, 309, "") -- Reply to agent
;
			
DELETE FROM workflow_task_ticketing_reply_config;
INSERT INTO workflow_task_ticketing_reply_config (id_task, message_direction) 
    VALUES  (420, 1) -- Reply to user
;
	
