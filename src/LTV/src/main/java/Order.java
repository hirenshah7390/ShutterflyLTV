import java.util.Date;

/**
 * Created by hiren on 6/3/2017.
 */
public class Order {
    String customerid;
    int amount;
    String key;
    Date event_time;

    //Add order
    public void newOrder() {
        CountLTV.OrderList.put(key, this);
    }

    //update order
    public void update() {
        CountLTV.OrderList.replace(key, this);
    }

}
