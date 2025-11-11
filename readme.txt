This is a simple financial tracker with a console interface.

It has the following functions:
adding records (records are added as follows: year, month, day, expenses, income, profit
- profit is calculated automatically and added using the income - expenses formula);

Record manipulation functions, namely: show all records, report for a period,
sort records, find a record, delete record;

Balance manipulation functions: show balance,
rewrite balance sum of profits, add in balance.

And a database backup function.
______________________________________________________________________________________________

Technologies used: SQLite for storing records, file manipulation (
.txt file storing the balance value), JavaCore (date manipulation).

______________________________________________________________________________________________

To install it:
1) Download JDK21 from the official website:
https://download.oracle.com/java/21/latest/

2) After installation, set the following system variables:

**JAVA_HOME** = `C:\Program Files\Java\jdk-21` 
(or an alternative path like jdk-21)

Create the following in the **PATH** system variable:
`%JAVA_HOME%\bin`

3) Verification: Open a command prompt and run:
java -version
javac -version
If it shows the version, everything is fine. If it returns errors, Google guides on how to download JDK.

4) Download the run_windows folder if you're on Windows or run_linux if you're on Mac/Linux.
These folders contain their own readme files that explain how to install them for specific OSes.

______________________________________________________________________________________________
















