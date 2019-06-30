# REST Book Service

Projeto desenvolvido utilizando Spring Boot e Apache Maven.

## Build
Para compilar o projeto va para a pasta raiz (onde está localizado o arquivo pom.xml) e execute o comando maven "mvn clean package" Isso irá criar um arquivo no destino target/RESTService-1.0.0.jar.

## Run
Para rodar o serviço execute o comando 'java -jar target\RESTService-1.0.0.jar', para finalizar-lo digite ctrl-c. O serviço estará acessivel localmente pelo endereço http://localhost:8080

## Rotas

- POST /login
  - Retorna um Json com o token de acesso ao resto das rotas do serviço, deve ser enviado um Json com os campos 'user' e 'password'. Somente são aceitos os usuários cadastrados na tabela 'user' do banco do serviço. Este token deve ser enviado em TODAS as outras chamadas de rotas do serviço, no cabeçalho da requisição, com a chave 'authentication' e com o valor do token retornado por está rota.
  - Exemplo: http://localhost:8080/login
  - Exemplo de Json do corpo da mensagem: {	"user" : "jcpinto",	"password" : "provaSoftDesign" }

- GET /books 
  - Retorna um Json com uma lista de livros (configurado em um limite de 10) e uma rota para os proximos livros se houver (paginação). Está rota tambem permite pesquisa por parametros. Os parametros disponiveis são 'author', 'title', 'endYear' e 'startYear'.
  - Exemplos: http://localhost:8080/books
			  http://localhost:8080/books?endYear=1999&startYear=1990
  
- PUT /books/rent/{id} 
  - Aluga um livro no serviço.
  - Exemplo: http://localhost:8080/books/rent/2

- PUT /books/return/{id} 
  - Retorna um livro alugado no serviço.
  - Exemplo: http://localhost:8080/books/return/2
  
- GET /books/{id} 
  - Retorna um Json do livro especificado pelo ID passado ou uma mensagem de erro se ele não for encontrado.
  - Exemplo: http://localhost:8080/books/0
 
- POST /books 
  - Salva um novo livro. Os parametros do novo livro devem ser passados em Json no corpo da mensagem. Se o livro foi criado com sucesso ele é retornado, se algum erro ocorrer uma mensagem é enviada.
  - Exemplo: http://localhost:8080/books
  - Exemplo de Json do corpo da mensagem: { "title": "Guerra e paz", "author": "Leon Tolstói", "year": "1869" }. Todos os campos são obrigatórios.
 
- PUT /books/{id} 
  - Atualiza um livro já existente. Os parametros para serem atualizados devem passados em Json no corpo da mensagem. Não é possivel atualizar um livro que esteja alugado.
  - Exemplo: http://localhost:8080/books/2
  - Exemplo de Json do corpo da mensagem: { "title": "Guerra e paz", "author": "Leon Tolstói", "year": "1869" }. Todos os campos são opcionais mas pelo menos um deve ser passado. 
 
- DELETE /books/{id} 
    - Remove um livro do serviço Não é possivel remover um livro que esteja alugado.
    - Exemplo: http://localhost:8080/books/2
	
	
