Backend application code:

* We always use the latest version of Spring boot, so use new features like the
  RestClient instead of RestTemplate and JDBCClient instead of JdbcTemplate
* Use the latest features of Kotlin if it makes sense
* do not check or write tests.
* Do not use comments, the code should be self-explanatory, prefer longer and meaningful names instead of comments
* Never split into Impl and interface, just assume there is one implementation

Backend database:

* Flyway for migrations, database updates


Frontend:


* We use the new web component framework webawesome.com
  You can search web-types.json to make sure you use
  the correct API, WA also have utility classes, which are all prefixed by wa-, the
  variables for colors, text etc. are also all prefixed by wa-
* The only libraries we use: 
Web awesome, HTMX, htmx-ext-loading-states 
No other libraries are in use or should be used
* Prefer inline styling if styles will not realistically be reused
* Prefix our own CSS classes with r-
* Use modern CSS features, no need to care about older browser support, using polyfills if necessary
  to support older browsers.
Use the modern Temporal API, if time,zone, calendar functionality is needed
* 