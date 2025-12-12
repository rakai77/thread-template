# **PLAN**
* Create Crypto Desk using Compose Multiplatform with shared UI
* Implement networking using Ktor also WebSockets
* Implement using Koin DI
* Implement MVVM pattern
* Implement clean architecture with this approach data, domain, presentation
* For the data API or service used Coin Desk open API
* Refactor/adjustment existing code

#### API Source and Documentation
Documentation: https://developers.coindesk.com/documentation/data-api/spot_v1_latest_tick <br>
Websocket: https://developers.coindesk.com/documentation/data-streamer/spot_v1_latest_tick_unmapped

<br>

>>Note: Using API KEY to connect websocket, set in parameters

>>API_KEY: a34e5d75e2480aeb566247a339c844221a4eb118b05141263ac159cb7bfc8fbb


# **CONTEXT**

- the content is list data and updated/stream data 
- create home screen ui to load list (max 10)
- create card component ui for list simple with ticker name, logo (with no image just symbol), price, presentation up/down, etc
- focus on simple component, not too complex component
- when up show the card with green color background, when down set the red color background, and default set plain (white or base background card based on them app)
- Use this format: HomeScreen, HomeUiState, HomeViewModel
- use mutableStateFlow or mutableSharedState in viewModel. Best practice recommendation clean architecture in presentation layer
- show bottom sheet error when hit the API, and suggest user to retry.
- dont show error web sockets for ui screen