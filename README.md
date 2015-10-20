# PersonalAccountant
**_Mobile Development Project_** - André Barata DT228/3


## About
PersonalAccountant has the objective of making keeping track of your personal spending easier.

The idea behind it is to allow the user to take a picture an receipt and the app keeps record of the picture and the amount spent. It will also allow the user to separate the receipt into different categories so different budgets can be given to each category.

Budgets will be calculated over a configurable amount of time ie: weeks, months. Different categories can be tallied over different amounts of time and past records will be stored as spending history.

Past spending history contains the total spent, the budget and the date of beginning and ending for a specific iteration of a category.

---

**Planed Features include:**
* Automatic detection of total cost in receipts
with the option of selecting manually if detection fails
* Customizable categories
* Over budget warnings
* Export to other devices
* Spending history



<!--\pagebreak -->

## Database description


![Image of database tables](README_images/PersonalAccountant.png)

The database is composed of three tables

---

## App Flow

The app starts in a list of categories (mainpage), the list contains the name, budget total and budget spent of the category. This screen also has two buttons: Add category and add receipt.

Clicking on a member of the category list in mainpage will send to a page (receipts) with a receipts list that displays the date and the money spent. Members of this list can be clicked to see the receipt capture.
