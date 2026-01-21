# 📊 Gerenciador de Portfólio de Projetos

Este projeto é uma API REST desenvolvida para o gerenciamento de portfólio de projetos. O sistema permite o controle completo do ciclo de vida de um projeto, desde a análise inicial até o encerramento, garantindo regras de negócio para membros e segurança na exclusão de dados.

---

## 🚀 Tecnologias Utilizadas
* **Java 17**
* **Spring Boot 3**
* **Spring Data JPA** 
* **PostgreSQL** 
* **Swagger/OpenAPI** 

---

## 🛠️ Regras de Negócio Implementadas

### 1. Restrição de Membros
* Apenas pessoas cadastradas com a atribuição **FUNCIONÁRIO** podem ser associadas como membros de um projeto. O sistema bloqueia a associação de Gerentes ou outros tipos de membros.

### 2. Validação de Exclusão
* Para preservar o histórico, projetos com os seguintes status **NÃO** podem ser excluídos:
  * `INICIADO`
  * `EM ANDAMENTO`
  * `ENCERRADO`
* O sistema retornará um erro `400 Bad Request` caso o usuário tente realizar essa operação.
  Fiz a logica não saiu bem no postman.

### 3. Cálculo Dinâmico de Risco
O risco do projeto é calculado automaticamente seguindo os critérios:
* **Baixo Risco:** Orçamento até R$ 100.000 e prazo de conclusão ≤ 3 meses.
* **Médio Risco:** Orçamento entre R$ 100.001 e R$ 500.000 OU prazo entre 3 a 6 meses.
* **Alto Risco:** Orçamento acima de R$ 500.000 OU prazo superior a 6 meses.

### 4. Integração
* Consumo de API externa para validação de dados de pessoas e gerentes responsáveis.

---

## 🏁 Como Executar o Projeto

1. **Clone o repositório:**
   ```bash
   git clone [https://github.com/loudedje/Projeto_Portifolio.git](https://github.com/loudedje/Projeto_Portifolio.git)
