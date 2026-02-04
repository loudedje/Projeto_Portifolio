# Instruções de Deploy

Este projeto utiliza Docker e Docker Compose para facilitar o build e a execução da aplicação.

## Pré-requisitos

*   Docker instalado
*   Docker Compose instalado

## Como rodar

1.  Navegue até a pasta do projeto:
    ```bash
    cd projeto-portifolio
    ```

2.  Construa e inicie os containers:
    ```bash
    docker-compose up --build -d
    ```

    Isso irá:
    *   Subir um banco de dados PostgreSQL.
    *   Compilar a aplicação Java (usando um container de build, não requer Java instalado na máquina).
    *   Iniciar a aplicação na porta 8080.

3.  Acesse a documentação da API (Swagger):
    *   http://localhost:8080/swagger-ui/index.html

## Parar a aplicação

Para parar e remover os containers:
```bash
docker-compose down
```

## Configuração

A configuração do banco de dados é feita via variáveis de ambiente no arquivo `docker-compose.yml`.
