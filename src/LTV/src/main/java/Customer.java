import java.util.Date;

/**
 * Created by hiren on 6/3/2017.
 */
public class Customer  {
	String last_name;
	String adr_city;
	String adr_state;
	String key;
	Date event_time;
	Date First_Visit_Date;
	Integer LTV = 0;
	int totalWeeks = 0;

	//Add Customer
	public void addCustomer() {
		CountLTV.CustomerList.put(key,this);
	}
	
	//Update Customer
	public void update() {

		CountLTV.CustomerList.replace(key,this);
	}

	public void reset_time(Date event_time) {

		if(CountLTV.Max_Date == null)
			CountLTV.Max_Date = CountLTV.Min_Date = event_time;

		else if (event_time.after(CountLTV.Max_Date)) {
			CountLTV.Max_Date = event_time;
		} else if (event_time.before(CountLTV.Min_Date)) {
			CountLTV.Min_Date = event_time;
		}

	}
}