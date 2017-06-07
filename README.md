# ShutterflyLTV

Shutterfly screening test

Class files

•	Start:- Contains main method and json parsing

•	Customer:- class for customer

•	SiteVisit:- class for site visit.

•	Order:-  class for order.

•	Image:- class for image.

•	countLTV:- Contains methods for continuos calculation of average LTV for each sutomer and save top 10 with highest LTV. It does also maintains in memory storage.

Dates:

•	Min and Max dates have been stored on basis of first visit from first customer till the last visit of last customer. These have been decided from the input of the file only and not externally.

•	The week duration has been decided on fly while calculating LTV for each week and customers visited during that particular week.

Complexity:

•	I have used HasMap for storing the in memory data so that way complexity could be o(1) in most cases except some places where I am not searching by key. Moreover, filtering list to restrict it to current week while calculating LTV to reduce number of iterations significantly. Excluding customers having first visit in future after current week to again reduce number of iterations increases performance of the method. 

•	This solution has been made considering small to medium size input. In future for large amount of input, we can store data to external drive or database before processing or else we can use big data technology like apache spark having RDD (resilient distributed dataframe) which distributes data among cluster nodes and process them on multiple machine yet in memory which is extremely fast.

