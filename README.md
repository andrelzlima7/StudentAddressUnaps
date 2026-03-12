# StudentAddress App

Aplicativo Android para cadastro e gerenciamento de estudantes, desenvolvido para fins acadêmicos no UNASP.

## Tecnologias

- Kotlin
- Jetpack Compose
- Room (SQLite)
- Retrofit
- WorkManager
- ViaCEP API

## Funcionalidades

- Listagem de estudantes
- Cadastro de estudantes
- Busca automática de endereço pelo CEP via [ViaCEP](https://viacep.com.br)
- Persistência local com Room (offline first)
- Sincronização automática com a API quando online

## Pré-requisitos

- Android Studio
- Android SDK 24+
- API StudentAddress rodando localmente [https://github.com/andrelzlima7/StudentAddressUnapsApi](https://github.com/andrelzlima7/StudentAddressUnapsApi)

## Configuração

Edite o arquivo `network/StudentApi.kt` com o endereço da sua API:
```kotlin
.baseUrl("http://SEU_IP:8080/")
```

## Repositório da API

[StudentAddress API](https://github.com/andrelzlima7/StudentAddressUnapsApi)

## Repositório do App

[StudentAddress App](https://github.com/andrelzlima7/StudentAddressUnaps)
