MoneyConv
=================

A currency converter app which let you convert any currency to another currency effortlessly and 
persistently. The data is fetched by real time each 30 minutes to let you know the global 
situation of global currency.

Getting Started
---------------

This project uses Kotlin and MVVM (Model View ViewModel) architecture to make the developer easier 
to debug the application logically based on the functions and designs. 

The Prerequisites are:
* JVM 1.8
* IDE: Android Studio (__recommended__) or Intellij IDEA 

Screenshots
---------------

![Exchange Currency] (screenshots/phone_exchange_currency.png "Exchange Currency")
![Currency Rates] (screenshots/phone_currency_rates.png "Currency Rates")

Features
---------------

1. Fetch Data from [Currency Layer](https://currencylayer.com/dashboard) using MVVM architecture.
2. Store Data persistently using [Room](https://developer.android.com/jetpack/androidx/releases/room)

Common Error in Prerequisites
---------------

* `new gradle sync is not supported due to containing kotlin modules`
Go to __Preferences__ - __Kotlin Compiler__ then change __Target JVM version__ to 1.8.

Acknowledgment
---------------

1. Error Condolences Icon from [https://www.vecteezy.com/vector-art/237807-404-page-error-with-funny-figure](https://www.vecteezy.com/vector-art/237807-404-page-error-with-funny-figure)
2. Globus Free Vector Icon from [https://www.vecteezy.com/vector-art/138026-free-globus-vector](https://www.vecteezy.com/vector-art/138026-free-globus-vector)
3. Mexico Peso Money Icon from [https://www.vecteezy.com/vector-art/155110-free-mexican-peso-money-icons-vector](https://www.vecteezy.com/vector-art/155110-free-mexican-peso-money-icons-vector)

License
---------------

MIT