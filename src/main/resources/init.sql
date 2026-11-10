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


INSERT INTO categoria (nome) VALUES ('Ciência');
INSERT INTO categoria (nome) VALUES ('Geografia');
INSERT INTO categoria (nome) VALUES ('História');
INSERT INTO categoria (nome) VALUES ('TI');
INSERT INTO categoria (nome) VALUES ('Matemática');


-- Insert perguntas categória ciência. 
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

-- Insert perguntas categória matemática 
INSERT INTO pergunta (categoria_id, enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta) VALUES
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quantos graus mede a soma dos ângulos internos de um triângulo?', '180°', '90°', '270°', '360°', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o dobro de 3/4?', '1/2', '6/8', '3/8', '3/2', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual destes números é primo?', '21', '31', '27', '39', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quantos lados tem um octógono?', '6', '7', '8', '10', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o resultado de (−3) × (−4)?', '12', '−12', '−7', '7', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é a raiz quadrada de 81?', '8', '9', '7', '10', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quanto é 25% de 80?', '25', '15', '30', '20', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quanto é 8 × 7?', '56', '54', '48', '63', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o valor de 2⁵?', '10', '25', '32', '16', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual destes números é divisível por 3?', '124', '312', '235', '401', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quanto é 1/2 + 1/4?', '2/6', '1/6', '1/4', '3/4', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o perímetro de um quadrado com lado de 5 cm?', '20 cm', '25 cm', '10 cm', '15 cm', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quantos centímetros há em 2,5 metros?', '25 cm', '2.500 cm', '250 cm', '52 cm', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o próximo número da sequência 2, 4, 8, 16, ...?', '24', '32', '30', '20', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o próximo número da sequência 1, 4, 9, 16, 25, ...?', '30', '34', '35', '36', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quanto é 3/5 de 100?', '60', '35', '50', '53', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o valor de x em x + 15 = 40?', '55', '20', '25', '35', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é a área de um quadrado com lado de 6 cm?', '24 cm²', '36 cm²', '12 cm²', '42 cm²', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Se 3 canetas custam R$ 12, quanto custam 5 canetas?', 'R$ 15', 'R$ 24', 'R$ 18', 'R$ 20', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o resultado de 10 − 3 × 2?', '4', '14', '7', '20', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual destas frações representa o maior valor?', '1/2', '2/3', '3/4', '3/5', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quantos minutos há em 2 horas e meia?', '120', '150', '130', '180', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Qual é o mínimo múltiplo comum (MMC) de 3 e 4?', '7', '1', '24', '12', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Quantas diagonais tem um quadrado?', '2', '4', '1', '3', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Matemática'), 'Ao lançar um dado comum, qual é a probabilidade de sair um número par?', '1/6', '1/3', '1/2', '2/3', 'C');

-- Insert perguntas categória geografia
INSERT INTO pergunta (categoria_id, enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta) VALUES
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é o maior país do mundo em extensão territorial?', 'China', 'Canadá', 'Estados Unidos', 'Rússia', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é o rio mais caudaloso do mundo?', 'São Francisco', 'Amazonas', 'Paraná', 'Tocantins', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é a capital da Austrália?', 'Camberra', 'Sydney', 'Melbourne', 'Perth', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é o maior oceano do planeta?', 'Atlântico', 'Índico', 'Pacífico', 'Ártico', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é a montanha mais alta do mundo acima do nível do mar?', 'K2', 'Kilimanjaro', 'Aconcágua', 'Monte Everest', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual estado brasileiro tem a maior área territorial?', 'Pará', 'Amazonas', 'Mato Grosso', 'Minas Gerais', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual continente tem o maior número de países?', 'África', 'Ásia', 'Europa', 'América', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual linha imaginária divide a Terra em Hemisfério Norte e Hemisfério Sul?', 'Trópico de Câncer', 'Meridiano de Greenwich', 'Linha do Equador', 'Trópico de Capricórnio', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é a capital do Canadá?', 'Toronto', 'Vancouver', 'Montreal', 'Ottawa', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é o maior deserto quente do mundo?', 'Gobi', 'Saara', 'Kalahari', 'Atacama', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual país tem a maior população da África?', 'Nigéria', 'Etiópia', 'Egito', 'África do Sul', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual bioma predomina no interior do Nordeste brasileiro?', 'Cerrado', 'Pampa', 'Caatinga', 'Pantanal', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual cordilheira se estende ao longo da costa oeste da América do Sul?', 'Himalaia', 'Alpes', 'Montanhas Rochosas', 'Andes', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual destes países sul-americanos não tem saída para o mar?', 'Peru', 'Bolívia', 'Uruguai', 'Venezuela', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é o maior lago de água doce do mundo em área superficial?', 'Lago Superior', 'Lago Vitória', 'Lago Baikal', 'Lago Titicaca', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual mar separa a Europa da África?', 'Mar Negro', 'Mar Vermelho', 'Mar Mediterrâneo', 'Mar Cáspio', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é a capital da Turquia?', 'Istambul', 'Izmir', 'Antália', 'Ancara', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Como se chama o fenômeno de aquecimento anormal das águas superficiais do Oceano Pacífico equatorial?', 'La Niña', 'El Niño', 'Monção', 'Ciclone', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual canal artificial liga os oceanos Atlântico e Pacífico?', 'Canal do Panamá', 'Canal de Suez', 'Canal da Mancha', 'Canal de Kiel', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual país tem o formato de uma bota?', 'Espanha', 'Grécia', 'Itália', 'Portugal', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é a região mais populosa do Brasil?', 'Nordeste', 'Sudeste', 'Sul', 'Centro-Oeste', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual rio atravessa a cidade de Londres?', 'Sena', 'Danúbio', 'Reno', 'Tâmisa', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Em qual continente fica o deserto do Atacama?', 'África', 'Ásia', 'América do Sul', 'Oceania', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual é a capital do estado do Pará?', 'Belém', 'Manaus', 'Macapá', 'São Luís', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'Geografia'), 'Qual estreito separa a Ásia da América do Norte?', 'Estreito de Gibraltar', 'Estreito de Bering', 'Estreito de Magalhães', 'Estreito de Ormuz', 'B');


-- Insert perguntas categória História
INSERT INTO pergunta (categoria_id, enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta) VALUES
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Em que ano o Brasil declarou independência de Portugal?', '1822', '1500', '1889', '1808', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Quem foi o primeiro presidente do Brasil?', 'Getúlio Vargas', 'Floriano Peixoto', 'Deodoro da Fonseca', 'Dom Pedro II', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual acontecimento é tradicionalmente usado para marcar o fim da Idade Média?', 'A queda do Império Romano do Ocidente', 'A tomada de Constantinopla pelos turcos otomanos (1453)', 'A Revolução Industrial', 'A independência dos Estados Unidos', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual conflito mundial ocorreu entre 1939 e 1945?', 'Primeira Guerra Mundial', 'Guerra Fria', 'Guerra do Vietnã', 'Segunda Guerra Mundial', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Quem assinou a Lei Áurea, em 1888?', 'Princesa Isabel', 'Princesa Leopoldina', 'Carlota Joaquina', 'Rainha Maria I', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual civilização construiu as pirâmides de Gizé?', 'Maia', 'Mesopotâmica', 'Egípcia', 'Romana', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Em que ano caiu o Muro de Berlim?', '1961', '1989', '1975', '1991', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Quem liderou a Revolução Russa de 1917?', 'Josef Stálin', 'Leon Tolstói', 'Nicolau II', 'Vladimir Lênin', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual movimento de 1789 buscava a independência de Minas Gerais?', 'Inconfidência Mineira', 'Conjuração Baiana', 'Cabanagem', 'Revolução Farroupilha', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual navegador português chegou à Índia em 1498, contornando a África?', 'Pedro Álvares Cabral', 'Bartolomeu Dias', 'Vasco da Gama', 'Fernão de Magalhães', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual foi a primeira capital do Brasil colonial?', 'Rio de Janeiro', 'Salvador', 'São Paulo', 'Recife', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual revolução, iniciada em 1789, tinha o lema Liberdade, Igualdade e Fraternidade?', 'Revolução Americana', 'Revolução Industrial', 'Revolução Gloriosa', 'Revolução Francesa', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual regime político vigorou no Brasil entre 1964 e 1985?', 'Ditadura militar', 'Monarquia', 'República Velha', 'Estado Novo', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual imperador francês foi derrotado na Batalha de Waterloo, em 1815?', 'Luís XIV', 'Carlos Magno', 'Napoleão Bonaparte', 'Luís XVI', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Como ficou conhecida a disputa de influência entre Estados Unidos e União Soviética, sem confronto militar direto entre eles?', 'Guerra dos Cem Anos', 'Guerra Fria', 'Guerra do Paraguai', 'Guerra dos Trinta Anos', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual grupo indígena predominava no litoral brasileiro quando os portugueses chegaram?', 'Incas', 'Astecas', 'Maias', 'Tupis', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Em que ano começou a Primeira Guerra Mundial?', '1914', '1905', '1918', '1939', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Quem liderou o movimento pela independência da Índia por meio da não violência?', 'Jawaharlal Nehru', 'Buda', 'Mahatma Gandhi', 'Indira Gandhi', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual documento limitou o poder do rei João Sem Terra, na Inglaterra, em 1215?', 'Bill of Rights', 'Magna Carta', 'Declaração de Independência', 'Código de Hamurabi', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Como ficou conhecido o conflito travado entre 1864 e 1870 pela Tríplice Aliança (Brasil, Argentina e Uruguai) contra o Paraguai?', 'Guerra dos Farrapos', 'Guerra de Canudos', 'Guerra do Contestado', 'Guerra do Paraguai', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual presidente governou o Brasil durante o Estado Novo (1937–1945)?', 'Juscelino Kubitschek', 'Eurico Gaspar Dutra', 'Getúlio Vargas', 'Café Filho', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual cidade foi capital do Império Romano do Oriente (Bizantino)?', 'Constantinopla', 'Atenas', 'Alexandria', 'Cartago', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual presidente norte-americano emitiu a Proclamação de Emancipação, em 1863?', 'George Washington', 'Thomas Jefferson', 'Theodore Roosevelt', 'Abraham Lincoln', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual acontecimento de 1929 provocou uma grande crise econômica mundial?', 'A crise do petróleo', 'A quebra da Bolsa de Nova York', 'A Revolução Chinesa', 'O Plano Marshall', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'História'), 'Qual cidade foi a capital do Brasil imediatamente antes de Brasília?', 'Salvador', 'São Paulo', 'Rio de Janeiro', 'Belo Horizonte', 'C');

-- Insert perguntas categória TI
INSERT INTO pergunta (categoria_id, enunciado, alternativa_a, alternativa_b, alternativa_c, alternativa_d, resposta_correta) VALUES
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'O que significa a sigla CPU?', 'Computer Personal Unit', 'Central Processing Unit', 'Central Program Utility', 'Control Processing Update', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual é a menor unidade de informação em um computador?', 'Byte', 'Kilobyte', 'Pixel', 'Bit', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Quantos bits há em um byte?', '4', '16', '8', '32', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual protocolo é usado para transferir páginas web de forma segura?', 'HTTPS', 'HTTP', 'FTP', 'SMTP', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual linguagem é usada para estruturar o conteúdo de páginas web?', 'CSS', 'HTML', 'SQL', 'Python', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'O que significa a sigla SQL?', 'Simple Question Language', 'Standard Quality Level', 'System Query Logic', 'Structured Query Language', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual estrutura de dados segue o princípio LIFO (o último a entrar é o primeiro a sair)?', 'Fila', 'Lista ligada', 'Pilha', 'Árvore', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual é a principal função de um sistema operacional?', 'Gerenciar os recursos de hardware e software', 'Navegar na internet', 'Criar documentos', 'Apenas proteger o computador contra vírus', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual destes é um sistema gerenciador de banco de dados relacional?', 'Photoshop', 'PostgreSQL', 'Chrome', 'Linux', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'O que é um endereço IP?', 'Uma senha de acesso', 'Um tipo de arquivo', 'Uma linguagem de programação', 'Um identificador de um dispositivo em uma rede', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual é a complexidade, no pior caso, da busca binária em um vetor ordenado com n elementos?', 'O(n)', 'O(n²)', 'O(log n)', 'O(1)', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual comando do Git envia os commits locais para o repositório remoto?', 'git push', 'git pull', 'git clone', 'git merge', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual paradigma de programação é predominante na linguagem Java?', 'Lógico', 'Orientado a objetos', 'Funcional puro', 'Assembly', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual camada do modelo OSI é responsável pelo roteamento de pacotes?', 'Física', 'Enlace', 'Aplicação', 'Rede', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual é a função de um compilador?', 'Executar o código linha a linha, sem traduzi-lo', 'Apagar automaticamente os erros do código', 'Traduzir o código-fonte para código executável ou intermediário', 'Conectar o programa ao banco de dados', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual dispositivo conecta redes diferentes e encaminha pacotes entre elas?', 'Roteador', 'Teclado', 'Monitor', 'Pen drive', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'O que significa a sigla RAM?', 'Read Always Memory', 'Random Access Memory', 'Rapid Application Module', 'Remote Access Method', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual comando SQL é usado para consultar dados de uma tabela?', 'INSERT', 'UPDATE', 'DELETE', 'SELECT', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual sistema de numeração os computadores utilizam internamente para representar dados?', 'Decimal', 'Romano', 'Binário', 'Hexadecimal', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Em Java, qual palavra-chave é usada para que uma classe herde de outra?', 'extends', 'implements', 'inherits', 'super', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'O que é um firewall?', 'Um programa de edição de imagens', 'Um tipo de memória', 'Um protocolo de e-mail', 'Um sistema que filtra o tráfego de rede segundo regras de segurança', 'D'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual é o valor decimal do número binário 1010?', '8', '10', '12', '14', 'B'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Em sistemas distribuídos, o que significa a sigla RPC?', 'Remote Procedure Call', 'Random Process Control', 'Rapid Packet Connection', 'Resource Process Cache', 'A'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'Qual é a porta padrão do protocolo HTTP?', '21', '25', '80', '443', 'C'),
 ((SELECT id FROM categoria WHERE nome = 'TI'), 'O que significa a sigla API?', 'Automatic Program Integration', 'Applied Process Index', 'Advanced Protocol Internet', 'Application Programming Interface', 'D');
	
