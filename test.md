# Assembly Admin Service
Projeto para controle e de sessões de votação de pautas.

## Ferramentas e FramWorks
O projeto foi desenvolvido utilizando [SpringBoot](https://spring.io/) e [Apache Maven](https://maven.apache.org/). Para persistencia dos dados foi utilizado [SQLite](https://www.sqlite.org/).

## Build
Para compilar o projeto va para a pasta raiz (onde está localizado o arquivo pom.xml) e execute o comando maven "mvn clean package" Isso irá criar um arquivo no destino target/assembly-admin-service-1.0.0.jar.

## Run
Para rodar o serviço localmente execute o comando 'java -jar target\assembly-admin-service-1.0.0.jar', para finalizar-lo digite ctrl-c. O serviço estará acessivel localmente pelo endereço http://localhost:8080

## Endpoints
----------------------------------
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
----------------------------------
### Listar Sessões 
  _Endpoint para listar as sessões existentes._

* #### **URL**

  `/api/v1/sessions`

* #### **Método:**
  
  `GET`
  
* #### **Path Parameters**

  `offset : integer`

* #### **Retornos:**
  
  * **Code:** 200 <br />
  * **Content:** 
```json
{
    "results" : [
        {
            "id"          : "integer",
            "name"        : "string",
            "description" : "string",
            "date"        : "date"
        }, 
        ...
    ],
    "nextPage" : "string"
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
----------------------------------
### Pegar Sessão 
  _Endpoint para pegar uma sessão específica._

* #### **URL**

  `/api/v1/sessions/{sessionId}`

* #### **Método:**
  
  `GET`

* #### **Retornos:**
  
  * **Code:** 200 <br />
  * **Content:** 
```json
{
    "id"          : "integer",
    "name"        : "string",
    "description" : "string",
    "date"        : "date"
}
```
  * **Code:** 404 <br />
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
----------------------------------
### Deletar Sessão 
  _Endpoint para deletar uma sessão específica._

* #### **URL**

  `/api/v1/sessions/{sessionId}`

* #### **Método:**
  
  `DELETE`

* #### **Retornos:**
  
  * **Code:** 200 <br />
  * **Content:** 
```json
{
    "status"  : "string",
    "message" : "string",
}
```
  * **Code:** 404 <br />
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

----------------------------------
### Criar Pauta da Agenda 
  _Endpoint para criação de uma pauta da agenda de uma sessão._

* #### **URL**

  `/api/v1/sessions/{sessionId}/agenda`

* #### **Método:**
  
  `POST`
  
* #### **Body**
_O campo timeToVote é opcional deve ser enviado sempre em minutos._
```json
{
    "name"        : "string",
    "description" : "string",
    "timeToVote"  : "integer"
}
```
* #### **Retornos:**
  
  * **Code:** 201 <br />
  * **Content:** 
```json
{
    "id"            : "integer",
    "name"          : "string",
    "description"   : "string",
    "timeToVote"    : "integer",
    "pollStatus"    : "integer",
    "startVoteDate" : "date",
    "sessionId"     : "integer"
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
----------------------------------
### Listar Sessões 
  _Endpoint para listar todas as pautas da agenda de uma sessão._

* #### **URL**

  `/api/v1/sessions/{sessionId}/agenda`

* #### **Método:**
  
  `GET`
  
* #### **Path Parameters**

  `offset : integer`

* #### **Retornos:**
  
  * **Code:** 200 <br />
  * **Content:** 
```json
{
    "results" : [
        {
            "id"            : "integer",
            "name"          : "string",
            "description"   : "string",
            "timeToVote"    : "integer",
            "pollStatus"    : "integer",
            "startVoteDate" : "date",
            "sessionId"     : "integer"
        }, 
        ...
    ],
    "nextPage" : "string"
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
----------------------------------
### Pegar Sessão 
  _Endpoint para pegar uma sessão específica._

* #### **URL**

  `/api/v1/sessions/{sessionId}/agenda/{agendaId}`

* #### **Método:**
  
  `GET`

* #### **Retornos:**
  
  * **Code:** 200 <br />
  * **Content:** 
```json
{
    "id"            : "integer",
    "name"          : "string",
    "description"   : "string",
    "timeToVote"    : "integer",
    "pollStatus"    : "integer",
    "startVoteDate" : "date",
    "sessionId"     : "integer"
}
```
  * **Code:** 404 <br />
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
----------------------------------
### Deletar Sessão 
  _Endpoint para deletar uma pauta da agenda de uma sessão._

* #### **URL**

  `/api/v1/sessions/{sessionId}/agenda/{agendaId}`

* #### **Método:**
  
  `DELETE`

* #### **Retornos:**
  
  * **Code:** 200 <br />
  * **Content:** 
```json
{
    "status"  : "string",
    "message" : "string",
}
```
  * **Code:** 404 <br />
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
