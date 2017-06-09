import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import static java.lang.Math.toIntExact;
/**
 * Created by hiren on 6/3/2017.
 * Class for analytical counting of LTV and storing inmemory list for all major entities
 */
public class CountLTV {

    public static HashMap <String,Customer> CustomerList = new HashMap();
    public static HashMap<String,SiteVisit> SiteVisitList = new HashMap();
    public static HashMap<String,Order> OrderList = new HashMap();
    public static HashMap<String,Image> ImageList = new HashMap();
    public static Date Min_Date ;
    public static Date Max_Date ;
    public static final int Life_Span = 10;

    /*
     Method to find startdate of a week for any given date
    */
    public static Date weekStartDate(Date givenDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(givenDate);

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);

        return cal.getTime();//Returns Date
    }

    /*
    Method to find Enddate of a week for any given date
   */
    public static Date weekEndDate(Date givenDate){
        Calendar cal = Calendar.getInstance();
        cal.setTime(givenDate);

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.HOUR_OF_DAY, 23);

        return cal.getTime();//Returns Date
    }


    /*
    Method to calculate the top x customers with the highest LTV from data
     */
    public static void topXSimpleLTVCustomers(int x, HashMap<String,Customer> D) throws FileNotFoundException {

        //first week first date
        Min_Date = weekStartDate(Min_Date);
        Calendar c = Calendar.getInstance();
        c.setTime(Max_Date);
        c.add(Calendar.DATE, 1);
        Max_Date =  c.getTime();

        /*
        iterate in week starting from min_date for each customer to
        calculate LTV
         */
       while(Min_Date.before(Max_Date)) {
           Map<String, Customer> matchingCustomers_thisWeek;
           Map<String, SiteVisit> matchingVisits_thisWeek;
           Map<String, Order> matchingOrders_thisWeek;
           Date week_start_date = weekStartDate(Min_Date);
           Date week_end_date = weekEndDate(Min_Date);

           /*
           list of customers who have visited store at least once before current week or during this week
           to exclude remaining customers having first visit after this week for LTV calculation
            */
           HashMap<String, Customer> all_visited_Customers = new HashMap(D.entrySet().stream().parallel()
                   .filter(cv -> !cv.getValue().First_Visit_Date.after(week_end_date))
                   .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()))
           );

           //List of customer who visited just during current week.
           matchingCustomers_thisWeek = CustomerList.entrySet().stream().parallel()
                   .filter(cv -> !cv.getValue().event_time.before(week_start_date) && !cv.getValue().event_time.after(week_end_date))
                   .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

           //filter to find all matching visits for current week
           matchingVisits_thisWeek = SiteVisitList.entrySet().stream().parallel()
                   .filter(cv -> !cv.getValue().event_time.before(week_start_date) && !cv.getValue().event_time.after(week_end_date))
                   .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

           //filter to find all matching orders for current week
           matchingOrders_thisWeek = OrderList.entrySet().stream().parallel()
                   .filter(cv -> !cv.getValue().event_time.before(week_start_date) && !cv.getValue().event_time.after(week_end_date))
                   .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

           //iterate through each matching customer only of this week to update ltv
           Iterator it = matchingCustomers_thisWeek.entrySet().iterator();
           while (it.hasNext()) {

               //initialize expenditure and visits
               Double max_expenditures_this_week = 0.0;
               Integer max_visits_this_week = 0;

               Map.Entry Cust = (Map.Entry) it.next();

               String Cust_key = (String) Cust.getKey(); //current customer key
               Customer Cust_Value = (Customer) Cust.getValue(); // current customer detail

               //incrementing total week for averaging LTV at last
               Cust_Value.totalWeeks += 1;

               //total orders of this customer in current week
               Map<String, Order> totalOrders = matchingOrders_thisWeek.entrySet().stream().parallel()
                       .filter(f -> f.getValue().customerid.equals(Cust_key))
                       .collect(Collectors.toMap(m -> m.getKey(), m -> m.getValue()));

               //Order with highest spending
               Order highestOrder = totalOrders.entrySet().stream().parallel()
                       .max((o1, o2) -> (o1.getValue().amount - o2.getValue().amount))
                       .get().getValue();

               //expenditure for week for this customer
               max_expenditures_this_week += highestOrder.amount;

               //calculating total visit for this customer
               max_visits_this_week += toIntExact(matchingVisits_thisWeek.entrySet().stream().parallel()
                       .filter(f -> f.getValue().customer_id.equals(Cust_key))
                       .count());

               //averaging LTV for this customer
               Double newLTV = (Cust_Value.LTV + (52 * (max_expenditures_this_week * max_visits_this_week) * Life_Span)) / Cust_Value.totalWeeks;

               Cust_Value.LTV = newLTV.intValue();

               //update main customer list with updated LTV and total week
               CustomerList.replace(Cust_key, Cust_Value);

               //Remove it from temp all visited customer list so we can process remaining customers with 0 LTV in next while loop
               all_visited_Customers.remove(Cust_key);
           }

           //iterating remaining customers from all visited customers to average LTV with zero for current week
           it = all_visited_Customers.entrySet().iterator();
           while (it.hasNext()) {

               Map.Entry Cust = (Map.Entry) it.next();

               String Cust_key = (String) Cust.getKey();
               Customer Cust_Value = (Customer) Cust.getValue();

               //incrementing total week for averaging LTV at last
               Cust_Value.totalWeeks += 1;

               //recalculation of LTV for this customer
               Cust_Value.LTV = (Cust_Value.LTV) / Cust_Value.totalWeeks;

               //Updating LTV for this customer
               CustomerList.replace(Cust_key, Cust_Value);
           }

           //Setting next week first date by incrementing date
           c.setTime(week_end_date);
           c.add(Calendar.DATE, 1);
           Min_Date = c.getTime();

       }

       //o/p stream for o/p file
        File f = new File("output//output.txt");
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter w = new PrintWriter(fos);

        //Finding top 10 customer and storing in o/p file
        CustomerList.entrySet().stream().parallel()
                   .sorted(Map.Entry.<String, Customer>comparingByValue((k, v) -> v.LTV).reversed())
                   .limit(10)
                   .forEach(l -> w.write("CustomerId: " +l.getValue().key + ", LTV: " + l.getValue().LTV + ", noOfWeeks: " + l.getValue().totalWeeks + "\n"));

        w.close();
    }

}
