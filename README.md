# Motel Manager

Aplicativo Android para gerenciamento operacional de motéis com visual luxury dark.

Sistema offline-first para administrar apartamentos, hospedagens, cobrança, consumo, pagamentos, reservas, lavanderia, manutenção, relatórios e histórico.

## Funcionalidades

### Dashboard
- Grid 2-colunas com todos os apartamentos
- Ícone **Villa** na barra inferior para apartamentos
- Saldo do dia (**AccountBalanceWallet**) no topo com valor em R$
- Cores indicativas por estado: LIVRE (verde), OCUPADO (vermelho), LIMPEZA (laranja), MANUTENÇÃO (roxo)
- Tempo de hospedagem e valor atualizando em tempo real (timer visual HH:mm:ss)
- Check-in rápido com toque

### Hospedagem
- Check-in com nome do hóspede
- Timer baseado em `SystemClock.elapsedRealtime()` (sobrevive fechar/reabrir app)
- Visualização de valor acumulado com formatação HH:mm:ss

### Cobrança
| Tempo | Valor |
|-------|-------|
| Até 1h (tolerância 10min) | R$ 100,00 |
| 1h30 | R$ 150,00 |
| 2h | R$ 200,00 |
| 2h30 | R$ 250,00 |
| 3h | R$ 300,00 |
| 3h30 | R$ 350,00 |
| 4h | R$ 400,00 |
| Pernoite | R$ 400,00 |

- Bloco extra de R$ 50,00 a cada 30 minutos após a primeira hora
- Tolerância de 10 minutos apenas na primeira hora

### Consumo
- Cadastro de produtos com preços
- Categorias com ícones: CERVEJA (Sports Bar), DRINK (Local Bar), COMBO (Liquor), GERAL (Inventory)
- Filtro por categoria na tela de produtos
- Adicionar/remover itens da hospedagem
- Controle de quantidade
- Subtotal e total integrados no checkout

### Pagamentos
- DINHEIRO (Payments), PIX (QrCode), CARTÃO (CreditCard) com ícones
- Histórico completo preservado
- Total do dia visível no topo do dashboard

### Reservas
- Formatação de data: **dd/MM** (dia/mês)
- Ano **opcional** (aparece apenas quando preenchido)
- Observações **opcionais**
- Detecção de conflitos por apartamento/data

### Lavanderia
- Controles visuais de status: Sujo (vermelho), Lavando (dourado), Limpo (verde)
- Itens: Fronha, Lençol, Toalha, Outro
- Contadores por status (Sujo/Lavando/Limpo)
- Contadores por tipo (Fronhas/Lençóis)
- Quantidades **editáveis** com botões +/- por item
- Transição de status entre Sujo → Lavando → Limpo
- Botão para adicionar novos itens via FAB

### Relatórios
- Ícone **TextSnippet** para área de relatórios
- **CalendarToday**: relatório diário
- **CurrencyExchange**: histórico de pagamentos
- Exportação TXT via Intent share (FileProvider)
- Relatório diário, semanal e mensal

### Login e Usuários
- Autenticação local com usuários padrão
- admin/admin (Administrador, MASCULINO)
- kesia/1234 (Kesia, FEMININO)
- reginaldo/1234 (Reginaldo, MASCULINO)
- Indicador de gênero no dashboard

## Visual Identity

- Fundo: Deep Black (#050505, #0A0A0A, #111111)
- Destaque: Burgundy/Wine (#5A0B16 → #B51D32)
- Metálico: Red (#C52A3D, #D63A4C)
- Dourado: Champagne (#C9A36A, #D8B982, #E0C89A)
- Texto: #F2EEEE (primário), #B9AEB1 (secundário), #6F6568 (desabilitado)
- Fonte primária: **Cormorant Garamond** (serif) — títulos
- Fonte secundária: **Manrope** — corpo
- Fonte mono: **JetBrains Mono** — valores/timer

## Ícones

- **Villa**: apartamentos (bottom nav)
- **Inventory**: produtos (bottom nav)
- **LocalLaundryService**: lavanderia (bottom nav)
- **Event**: reservas (bottom nav)
- **Receipt**: relatórios (bottom nav)
- **AccountBalanceWallet**: saldo do dia (top bar)
- **TextSnippet**: área de relatórios
- **CalendarToday**: relatório diário
- **CurrencyExchange**: histórico de pagamentos
- **Add**: + para criar
- **Logout**: sair

## Stack Técnico

| Componente | Tecnologia |
|------------|------------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material3 |
| Banco de dados | Room (SQLite) — v5 |
| Navegação | Navigation Compose |
| Arquitetura | MVVM |
| Build | Gradle 8.7, AGP 8.5.2 |
| SDK | Android 14 (API 34) |
| Min SDK | Android 8 (API 26) |
| Fontes | Cormorant Garamond, Manrope, JetBrains Mono |

## Estrutura do Projeto

```
app/src/main/java/com/atlantic/motel/
├── AtlanticMotelApp.kt              # Application class + session
├── MainActivity.kt                   # Entry point
├── billing/
│   └── BillingEngine.kt             # Motor de cobrança (R$100/h)
├── data/
│   ├── dao/
│   │   ├── ApartmentDao.kt
│   │   ├── ConsumptionDao.kt
│   │   ├── LaundryDao.kt            # DAO lavanderia
│   │   ├── PaymentDao.kt
│   │   ├── ProductDao.kt
│   │   ├── ReservationDao.kt
│   │   ├── StayDao.kt
│   │   └── UserDao.kt               # Autenticação local
│   ├── database/
│   │   └── AppDatabase.kt           # Room v5 (8 entities)
│   └── model/
│       ├── Apartment.kt
│       ├── Consumption.kt
│       ├── Laundry.kt               # Fronha/Lençol/Toalha/Outro
│       ├── Payment.kt
│       ├── Product.kt               # + ProductCategory enum
│       ├── Reservation.kt
│       ├── Stay.kt
│       └── User.kt
├── ui/
│   ├── components/
│   │   └── ApartmentCard.kt         # Card luxury dark
│   ├── icons/
│   │   └── PhosphorIcons.kt         # Ícones custom (reserva)
│   ├── screen/
│   │   ├── HistoryScreen.kt         # Histórico de pagamentos
│   │   ├── LaundryScreen.kt         # Controle lavanderia
│   │   ├── LoginScreen.kt           # Login luxury dark
│   │   ├── MainNavHost.kt           # Navegacao principal
│   │   ├── MainScreen.kt            # Dashboard + bottom nav
│   │   ├── ProductsScreen.kt        # Produtos + categorias
│   │   ├── ReportsScreen.kt         # Exportação TXT
│   │   ├── ReservationScreen.kt     # Reservas
│   │   └── StayScreen.kt            # Detalhes hospedagem
│   └── theme/
│       ├── Color.kt                 # Paleta luxury dark
│       ├── Theme.kt                 # Dark Material3
│       └── Type.kt                  # 3 fontes
└── viewmodel/
    ├── ConsumptionViewModel.kt
    ├── LaundryViewModel.kt          # ViewModel lavanderia
    ├── LoginViewModel.kt
    ├── MainViewModel.kt             # + daily total
    ├── ReportsViewModel.kt          # Export TXT
    ├── ReservationViewModel.kt
    └── StayViewModel.kt
```

## Instalação

Baixe o APK na seção [Releases](../../releases) e instale no dispositivo Android (API 26+).

Habilite "Fontes desconhecidas" nas configurações do Android se necessário.

## Desenvolvimento

### Requisitos
- JDK 17
- Android SDK 34
- Gradle 8.7

### Build
```bash
./gradlew assembleDebug    # Debug
./gradlew assembleRelease  # Release
```

### Notas de Ambiente (proot-distro)
Para build em ARM64 via proot, configure em `gradle.properties`:
```properties
android.aapt2FromMavenOverride=/path/to/aapt2-arm64
```

## Licença

Uso interno.
