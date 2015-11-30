DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_ESS_MEDS';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values 
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_ESS_MEDS',
'Requisition authorization mail template for via',
'Notification - Email',
'Requisition authorization mail templatefor via',
'<h2><strong>Dados de requisição Via Classica anexados </strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisição Via Classica para o SIMAM.</p></div>', 'HTML', 40);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MMIA';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values 
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MMIA',
'Requisition authorization mail  template for mmia',
'Notification - Email',
'Requisition authorization mail template for mmia',
'<h2><strong>Relatorio de MMIA anexado</strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos anexados, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do  SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da requisição Via Classica para o SIMAM.</p></div>', 'HTML', 40);