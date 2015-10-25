# PersonalAccountant
**_Mobile Development Project_** - André Barata DT228/3


## About
PersonalAccountant has the objective of making keeping track of your personal spending easier.

The idea behind it is to allow the user to take a picture of a receipt and let the app keep the records of the picture and the amount spent. It will also allow the user to separate the receipt into different categories so different budgets can be given to each category.

Budgets can then be calculated over a configurable amount of time ie: weeks, months. Different categories can be tallied over different amounts of time and past records will be stored as spending history.

Past spending history contains the total spent, the budget and the dates of beginning and end for a specific iteration of a category.

These records will stored locally lowering the probability of data theft.

---


**Planned features include:**

* Automatic detection of total cost in receipts
with the option of selecting manually if detection fails
* Customizable categories
* Over budget warnings
* Export to other devices
* Spending history


<!-- \clearpage -->


## App Flow

![Image of the project screen flow](README_images/AppFlow.png)

The app starts in a list of categories (mainpage), the list contains the name, budget total and budget spent of the category. This screen also has two buttons: Add category and add receipt.

Clicking on a member of the category list in mainpage will send to a page (receipts) with a current receipts list that displays the date and the money spent. Members of this list can be clicked to see the receipt capture, there is a button to edit the category and a button to see old receipts.

Clicking "add category" will send the user to a form with inputs to pick the category name, budget, icon, counting period and an option to set when to start counting.

Clicking "add receipt" will open the camera and allow the user to that a picture of the receipt. It will also have an input to manual enter the total cost.

<!-- \clearpage -->



## Screen Mockups


![Image of the main screen](README_images/MainScreen.png) ![Image of the category form ](README_images/CategoryForm.png)

<!-- \clearpage -->

## Database description

![Image of database tables](README_images/PersonalAccountant.png | test)

The database is composed of three tables:

* Category
* SpendingHistory
* Receipt

<!-- \clearpage  -->

## Use Case

![Image of the project usecase](README_images/UseCase.png){#fig:thing}

A user can:

* Remove Categories
* Add Categories
* Edit Categories
* Add receipts
* And Remove Receipts

<!-- \clearpage -->
