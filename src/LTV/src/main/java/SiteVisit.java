import java.util.Date;
import java.util.HashMap;

/**
 * Created by hiren on 6/3/2017.
 * Class to track Site_visits
 */
public class SiteVisit  {
    String customer_id;
    HashMap<String, String> tags;
    String key;
    Date event_time;

    //Add visit
    public void newVisit() {
        CountLTV.SiteVisitList.put(key, this);
    }

}
