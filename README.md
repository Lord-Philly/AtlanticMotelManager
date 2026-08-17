# Atlantic Motel Manager

Aplicativo Android para gerenciamento operacional do **Atlantic Motel**, localizado em Colorado do Oeste - RO, Brasil.

Sistema offline-first para administrar apartamentos, hospedagens, cobranca, consumo, pagamentos, reservas, limpeza, manutencao e historico.

## Funcionalidades

### Tela Principal
- Grid 2x2 com todos os apartamentos
- Cores indicativas por estado: LIVRE (verde), OCUPADO (vermelho), LIMPEZA (laranja), MANUTENCAO (roxo)
- Tempo de hospedagem e valor atualizando em tempo real
- Check-in rapido com toque

### Hospedagem
- Check-in com nome do hospede
- Timer baseado em timestamps reais (sobrevive fechar/reabrir app)
- Visualizacao de valor acumulado

### Cobranca
| Tempo | Valor |
|-------|-------|
| 1h | R$ 100,00 |
| 1:30 | R$ 150,00 |
| 2h | R$ 200,00 |
| 2:30 | R$ 250,00 |
| 3h | R$ 300,00 |
| 3:30 | R$ 350,00 |
| 4h | R$ 400,00 |
| Pernoite | R$ 400,00 |

### Consumo
- Cadastro de produtos com precos
- Adicionar/remover itens da hospedagem
- Controle de quantidade
- Subtotal e total integrados

### Pagamentos
- DINHEIRO, PIX, CARTAO
- Historico completo preservado

### Reservas
- Data, horario, cliente, observacoes
- Deteccao de conflitos por apartamento/data

### Limpeza e Manutencao
- Transicao automatica para LIMPEZA apos checkout
- Botao "Limpeza Concluida" para voltar a LIVRE
- Manutencao manual com motivo
- Bloqueio de check-in durante manutencao

## Stack Tecnico

| Componente | Tecnologia |
|------------|------------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material3 |
| Banco de dados | Room (SQLite) |
| Navegacao | Navigation Compose |
| Arquitetura | MVVM |
| Build | Gradle 8.7 |
| SDK | Android 14 (API 34) |
| Min SDK | Android 8 (API 26) |

## Estrutura do Projeto

```
app/src/main/java/com/atlantic/motel/
├── AtlanticMotelApp.kt          # Application class
├── MainActivity.kt              # Entry point
├── billing/
│   └── BillingEngine.kt         # Motor de cobranca
├── data/
│   ├── dao/                     # Data Access Objects
│   │   ├── ApartmentDao.kt
│   │   ├── ConsumptionDao.kt
│   │   ├── PaymentDao.kt
│   │   ├── ProductDao.kt
│   │   ├── ReservationDao.kt
│   │   └── StayDao.kt
│   ├── database/
│   │   ├── AppDatabase.kt       # Room database
│   │   └── DatabaseProvider.kt
│   └── model/                   # Entidades
│       ├── Apartment.kt
│       ├── Consumption.kt
│       ├── Payment.kt
│       ├── Product.kt
│       ├── Reservation.kt
│       └── Stay.kt
├── ui/
│   ├── components/
│   │   └── ApartmentCard.kt     # Card do apartamento
│   ├── screen/
│   │   ├── HistoryScreen.kt     # Historico
│   │   ├── MainNavHost.kt       # Navegacao
│   │   ├── MainScreen.kt        # Tela principal
│   │   ├── ProductsScreen.kt    # Produtos
│   │   ├── ReservationScreen.kt # Reservas
│   │   └── StayScreen.kt        # Detalhes hospedagem
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── viewmodel/
    ├── ConsumptionViewModel.kt
    ├── MainViewModel.kt
    ├── ReservationViewModel.kt
    └── StayViewModel.kt
```

## Instalacao

Baixe o APK na secao [Releases](../../releases) e instale no dispositivo Android (API 26+).

Habilite "Fontes desconhecidas" nas configuracoes do Android se necessario.

## Desenvolvimento

### Requisitos
- Android Studio ou terminal com Gradle
- JDK 17
- Android SDK 34

### Build
```bash
./gradlew assembleDebug    # Debug
./gradlew assembleRelease  # Release
```

## Licenca

Uso interno - Atlantic Motel, Colorado do Oeste - RO.
