# ShutterflyLTV
Shutterfly screening test
Class files
•	Start:- Contains main method and json parsing
•	Customer:- class for customer
•	SiteVisit:- class for site visit.
•	Order:-  class for order.
•	Image:- class for image.
•	countLTV:- Contains methods for continuation calculation of LTV and maintaining in memory storage.
Dates:
•	Min and Max dates have been stored on basis of first visit from first customer till the last visit of last customer. These have been decided from the input of the file only and not externally.
•	The week duration has been decided on fly while calculating LTV for each week and customers visited during that particular week.
Complexity:
•	I have used HasMap for storing the in memory data so that way complexity could be o(1) in most cases except some places where I am not searching by key.
•	This solution has been made considering small to medium input. In future for large amount of input, we can store data to external drive or database before processing or else we can use big data technology like apache spark having RDD (resilient distributed dataframe) which distributes data among cluster nodes and process them on multiple machine yet in memory which is extremely fast.

