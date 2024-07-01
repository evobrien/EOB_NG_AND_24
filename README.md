NetGear Movie App

The app follows an MVVM pattern (with use cases used in the detail section). This is fairly standard 
- Stateflow is used to expose data in a unidirectional manner to the UI.
- Events are raised via an onEvent function on the view models
- Paging is used the in the app. I thought it was essential as it's possible to get hundreds of records returned if you enter a query using a very common word like "the" or something similar.
The package structure is  organized into UI, DI and Data main packages with the features organized under the UI/features package.
The detail feature is closer to how I normally organize code ( I usually use us cases that do transformations etc.).

Tech used:

Jetpack Compose:
- Jetpack Compose has been used for all UI. 
- We are using only Jetpack composables: Fragments were not actually necessary so it's pure compose.
- State Hoisting is used for composables in most cases so they are as generic and stateless as possible. Stateful composables are usually only the top level composable which "binds" the data and sets up the UI.
- The UI uses a common Screen composable and uses the slot pattern to compose individual compose functions into the specific screen instances ( a slot is basically a lambda that represents a section and you will see slots for the toolbar/app bar and content sections in the Screen composable)
  
Navigation Compose
- Navigation is implemented with Navigation Compose.
- The navigation code is located in the NavigationGraph composable (we are still using string matching for routes and arguments - newer mechanisms are available in recent alpha releases but I did not want to include an alpha library)
- The only real issue with compose is that it's not as easy to implement a collapsing toolbar with the inclusion of the search bar (no co-ordinator layout) so I've left that out for now.

Hilt - Dependency injection.
The DI code is organized into a main DI package for modules that are shared accross the app
- Repos are in the Api module
- Retrofit & network related config is available in the Network module.
- Common dependencies are located in the App module
- Database related dependencies are located in the db module
The feature specific DI that pertains to only individual features is located in feature/<feature name>/di 
I've tried to limit the use singletons whereever possible and keep the lifetime of objects to minimum reasonable lifetimes.

Paging3 lib:
Paging3 works fairly well in terms of efficiency and it's pretty smooth overall - you can observe the paging in logcat as I have an interceptor printing the requests there for debug purposes. However there are drawbacks with paging3
The search/list screen which deviates somewhat from my usual approaches to design because I used the paging3 lib. The idea there was that we use a mediator/repo that would implement a database first pattern, querying the network only when really necessary. I did not have time to implement that properly so the pager is using only the network data. 
In retrospect it may have been better to use manual paging as paging3 distorts the design somewhat - tends to become tightly coupled with the libary which is definitely an issue. 
A small database is included for caching. The db is only used by the detail feature for now but the intent was to extend it's use in the search/list screen for more efficient caching and paging, with network requests only being required if there was no query related data locally (I ran out of time to implement that).

Testing:
Turbine:
- Turbine is used for some unit tests as it makes it easy to handle stateflow events
Mockk
- Mockk is used for all mocking


Issues:
Unit tests and Paging3
- I had issues with writing tests for the viewmodel and repository for the search screen. I'm stll trying to figure out what the issue us - it's likely coroutine related. The tests that aren't working are disabled. The app itself works without issue in this area though The tests for the paging data source in this area have been implemented and they are working fine. Also the tests for the details section and related repos are implemented and working fine.

- UI issues:
- These are things I just didn't get time to implement
- First, the collpsing app bar is not implemented on the search screen. Implementing this is is a bit more involved in compose with the search bar also involved - it's not as simple/stanard as the approaches to achieve the same with the co-ordinator layout in xml ui
- Second on rotation, the search history is lost - there's a small piece of code I need to implement there to retain that.
- Third, the UI and theming is not great - I just ran out of time for that. So the UI is basically functional but not pretty.
- Fourth, the database caching/paging  for the search screen is not integrated (again, time constraints)
- Fifth, movies that have no posterpath are filtered out of the search screen - this was just a cosmetic and time related decision (I did not have time to find a good placeholder image for those so it looks better for now filtering those movies out)
- Sixth, the list screen needs a placeholder at the bottom for loading next page events and also a retry button plaholder for failed queries (right now there is no feedback there)
  
  







