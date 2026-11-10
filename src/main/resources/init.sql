CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS pergunta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT,
    enunciado TEXT NOT NULL,
    alternativa_a VARCHAR(255) NOT NULL,
    alternativa_b VARCHAR(255) NOT NULL,
    alternativa_c VARCHAR(255) NOT NULL,
    alternativa_d VARCHAR(255) NOT NULL,
    resposta_correta CHAR(1) NOT NULL,
    FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE IF NOT EXISTS historico_resposta (
	partida_id INT NOT NULL,
    jogador_id INT,
    pergunta_id INT,
    acertou BOOLEAN,
    PRIMARY KEY (jogador_id, pergunta_id),
    FOREIGN KEY (pergunta_id) REFERENCES pergunta(id)
);



INSERT INTO  categoria (nome) VALUES ("Ciência");
INSERT INTO  categoria (nome) VALUES ("Geografia");
INSERT INTO  categoria (nome) VALUES ("História");
INSERT INTO  categoria (nome) VALUES ("TI");
INSERT INTO  categoria (nome) VALUES ("Matemática");

INSERT INTO pergunta (categoria_id, enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta) VALUES
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é o processo pelo qual as plantas produzem glicose a partir de luz, água e gás carbônico?', 'Respiração celular', 'Fermentação', 'Fotossíntese', 'Quimiossíntese', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é o símbolo químico do sódio?', 'Na', 'So', 'Sd', 'S', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual organela é responsável pela maior parte da produção de energia (ATP) na célula?', 'Ribossomo', 'Lisossomo', 'Complexo de Golgi', 'Mitocôndria', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Em qual camada da atmosfera ocorrem os fenômenos meteorológicos?', 'Estratosfera', 'Troposfera', 'Mesosfera', 'Termosfera', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é a unidade de medida de força no Sistema Internacional?', 'Joule', 'Watt', 'Newton', 'Pascal', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual gás é o mais abundante na atmosfera terrestre?', 'Nitrogênio', 'Oxigênio', 'Gás carbônico', 'Argônio', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Como se chama a passagem direta do estado sólido para o gasoso?', 'Fusão', 'Vaporização', 'Condensação', 'Sublimação', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Quem formulou a lei da gravitação universal?', 'Albert Einstein', 'Isaac Newton', 'Galileu Galilei', 'Johannes Kepler', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual tipo de ligação química se forma pelo compartilhamento de elétrons entre átomos?', 'Iônica', 'Metálica', 'Covalente', 'Dipolo-dipolo', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é o maior órgão do corpo humano?', 'Pele', 'Fígado', 'Intestino delgado', 'Pulmão', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual vitamina é sintetizada pela pele com a exposição à luz solar?', 'Vitamina C', 'Vitamina A', 'Vitamina K', 'Vitamina D', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é a velocidade aproximada da luz no vácuo?', '150.000 km/s', '300.000 km/s', '30.000 km/s', '3.000.000 km/s', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é o pH de uma solução neutra a 25 °C?', '0', '10', '7', '14', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Quem propôs a teoria da evolução por seleção natural?', 'Charles Darwin', 'Gregor Mendel', 'Louis Pasteur', 'Carl Linnaeus', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual molécula armazena a informação genética na maioria dos seres vivos?', 'RNA mensageiro', 'ATP', 'Hemoglobina', 'DNA', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual célula do sangue é a principal responsável pelo transporte de oxigênio?', 'Leucócito', 'Hemácia', 'Plaqueta', 'Linfócito', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual lei de Newton estabelece que a toda ação corresponde uma reação de mesma intensidade e sentido oposto?', 'Primeira lei', 'Segunda lei', 'Terceira lei', 'Lei da gravitação universal', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual elemento químico tem número atômico 6?', 'Carbono', 'Oxigênio', 'Nitrogênio', 'Boro', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é a principal função dos glóbulos brancos?', 'Transportar oxigênio', 'Coagular o sangue', 'Produzir hormônios', 'Defender o organismo contra agentes infecciosos', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual fenômeno explica a existência das estações do ano?', 'A variação da distância entre a Terra e o Sol', 'A inclinação do eixo de rotação da Terra', 'O movimento de rotação da Terra', 'As fases da Lua', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual é a unidade de medida da corrente elétrica?', 'Ampère', 'Volt', 'Ohm', 'Watt', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Quem descobriu a penicilina?', 'Louis Pasteur', 'Robert Koch', 'Alexander Fleming', 'Edward Jenner', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual estrutura existe na célula vegetal, mas não na célula animal?', 'Membrana plasmática', 'Parede celular', 'Núcleo', 'Mitocôndria', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual tipo de onda se propaga no vácuo, sem precisar de meio material?', 'Som', 'Ondas na água', 'Ondas em uma corda', 'Ondas eletromagnéticas', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Ciência'), 'Qual ramo da biologia estuda a classificação dos seres vivos?', 'Taxonomia', 'Ecologia', 'Citologia', 'Genética', 'A');