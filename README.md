Gerenciador de Portfólio de Projetos
Este projeto é um sistema de gerenciamento de portfólio de projetos, desenvolvido como parte de um desafio técnico. O sistema permite o controle completo de projetos, associando membros, gerentes e validando regras de negócio rigorosas.

🚀 Tecnologias Utilizadas
Java 17

Spring Boot 3

Spring Data JPA (Persistência de dados)

PostgreSQL/H2 (Banco de dados)

Lombok 

Maven 

🛠️ Regras de Negócio Implementadas
Restrição de Membros: Apenas pessoas com a atribuição FUNCIONÁRIO podem ser associadas a um projeto.

Cálculo de Risco: Sistema de classificação de risco para cada projeto(
• A classificação de risco deve ser calculada dinamicamente com base nas seguintes regras:
• Baixo risco: orçamento até R$ 100.000 e prazo ≤ 3 meses
• Médio risco: orçamento entre R$ 100.001 e R$ 500.000 ou prazo entre 3 a 6 meses
• Alto risco: orçamento acima de R$ 500.000 ou prazo superior a 6 meses)

Integração Externa: Consumo de API externa para validação de pessoas e gerentes.

🏁 Como Executar o Projeto
Clone o repositório:

Bash

git clone https://github.com/loudedje/Projeto_Portifolio.git

O sistema estará disponível em http://localhost:8080.
