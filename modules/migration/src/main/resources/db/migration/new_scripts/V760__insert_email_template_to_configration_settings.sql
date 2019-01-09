DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MALARIA';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_MALARIA',
'Requisition authorization mail template for al',
'Notification - Email',
'Requisition authorization mail templatefor al',
'<h2><strong>Dados de Mapa Consumo de AL anexados</strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados da Mapa consumo de AL para o SIMAM.</p></div>', 'HTML', 40);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_PTV';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_PTV',
'Requisition authorization mail template for ptv',
'Notification - Email',
'Requisition authorization mail template for ptv',
'<h2><strong>Dados de PTV anexados </strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados do PTV para o SIMAM.</p></div>', 'HTML', 40);

DELETE FROM configuration_settings where key = 'EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_TEST_KIT';
insert into  configuration_settings
(key, name, groupname, description, value, valueType,displayOrder)
values
('EMAIL_TEMPLATE_FOR_REQUISITION_ATTACHMENT_TEST_KIT',
'Requisition authorization mail template for rapid test',
'Notification - Email',
'Requisition authorization mail template for rapid test',
'<h2><strong>Dados de Mapa Mensal de Informação de Testes Rápidos (MMIT) anexados </strong></h2><p style="font-size: 14pt">Consulte o nome do arquivo anexado para identificação da US</p><div style="font-size: 11pt"><p>Nos arquivos em anexo, você encontrará:<ul><li>2 arquivos em Excel intitulados: Requisição e Regime.</li></ul></p><p>Se você teve uma formação sobre o uso do SIMAM, por favor importar os arquivos no formato Excel anexados.</p><p>Se você não tiver recebido nenhuma formação ou se estiver com problemas para importar os dados para SIMAM, por favor insira manualmente os dados do MMIT para o SIMAM.</p></div>', 'HTML', 40);