# Assembly Admin Service
Projeto para controle e de sessões de votação de pautas.

## Ferramentas e FramWorks
O projeto foi desenvolvido utilizando [SpringBoot](https://spring.io/) e [Apache Maven](https://maven.apache.org/). Para persistencia dos dados foi utilizado [SQLite](https://www.sqlite.org/).

## Build
Para compilar o projeto va para a pasta raiz (onde está localizado o arquivo pom.xml) e execute o comando maven "mvn clean package" Isso irá criar um arquivo no destino target/assembly-admin-service-1.0.0.jar.

## Run
Para rodar o serviço localmente execute o comando 'java -jar target\assembly-admin-service-1.0.0.jar', para finalizar-lo digite ctrl-c. O serviço estará acessivel localmente pelo endereço http://localhost:8080

## Endpoints

### Criar Sessão 
  _Endpoint para criação de sessões de assembleia. Cada sessão possui uma agenda com pautas para serem votadas._

* #### **URL**

  `/api/v1/sessions`

* #### **Método:**
  
  `POST`
  
* #### **Body**

```json
{
    "name"        : "string",
    "description" : "string",
    "date"        : "date"
}
```

* #### **Retornos:**
  
  * **Code:** 201 <br />
  * **Content:** 
```json
{
    "id"          : "integer",
    "name"        : "string",
    "description" : "string",
    "date"        : "date"
}
```
  * **Code:** 400 <br />
  * **Content:** 
```json
{
    "status"  : "string",
    "message" : "string",
}
```
  * **Code:** 500 <br />
  * **Content:** 
```json
{
    "status"  : "string",
    "message" : "string",
}
```
