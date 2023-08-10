# SAGRONE

### Goal
This project **SAGRONE** targets local festivals (”sagra” in Italian) and aims to provide a complete food services management
system, taking care of all their customers and employee’s needs. The resulting solution will assist the event staff in
managing orders through their lifecycle by means of an intuitive interface while providing at the same time a web
portal for customers to place orders autonomously, reducing delays and staff workload.

### Description
There are three main protagonists in the use of this web application, each one has its relative interface:

* **Customer**: he accesses from the homepage or QR Code the page with the menu of desired sagra, he chooses the dishes to order and, then, he sends its order, receiving as confirmation an order identification code 
    * he's not expected to authenticate in the platform
* **Cashier**: he is member of the event staff and has the role to retrieve order by code and accept payments from customers, providing a receipt of paid order, he can also make "physical" orders 
    * he has to authenticate in the platform through its own set of credentials
* **Administrator**: he is the unique manager of all settings relative to associated sagra, so he is able to edit the menu (add, modify, remove items) and administrate all cashiers accounts (add, edit, remove credentials) by a reserved admin interface 
    * he has to authenticate in the platform through provided administration credentials



### Project Directory Structure
```
project
│   README.md   
│   pom.xml
│
└─── src
│    └─── main
│         └─── database
│         │     ...
│         └─── java
│         │    └─── it.unipd.dei.sagrone
│         │          └─── database
│         │          │    ...
│         │          └─── filter   
│         │          │    ...
│         │          └─── resource
│         │          │    ...
│         │          └─── rest 
│         │          │    ...
│         │          └─── servlet   
│         │          │    ...
│         │          └─── utils
│         │               ...
│         └─── resources
│         │    ...
│         └─── webapp
│              └─── jsp
│              │    └─── admin
│              │    │    ...
│              │    └─── include                        
│              │         ...
│              └─── META-INF
│              └─── WEB-INF
│
└─── target
     ...
```

* **database**: it contains all the sources for the database (SQL schema creation and population script) 
* (it.unipd.dei.sagrone.)**database**: it contains all DAOs that implement the access to the database and execution of SQL statements
* **filter**: it contains classes that control who can access to some reserved web pages by authentication
* **resource**: it contains all the manipulated objects (Javabeans)
* **rest**: it contains all the rest components
* **servlet**: it contains all the servlets used the in web application
* **utils**: it contains additional file with all possible error codes
* **jsp**: it contains all Java Server Pages used to create HTML pages, isolating JSPs related to admin interface in _admin_ folder 

# Contributors
The webapp was developed with the collaboration of: Avanzi Giacomo, Campagnolo Giovanni, Carpentieri Matteo, Fincato Saverio, Gobbo Riccardo, Merotto Alberto, Merlo Simone, Rossi Gianluca, Spinosa Diego, Varini Alberto. Contact me to possibly get the contact information of other developers