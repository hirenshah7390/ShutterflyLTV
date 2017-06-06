import java.util.Date;

/**
 * Created by hiren on 6/5/2017.
 */
public class Image {
        String customer_id;
        String cameraMake;
        String cameraModel;
        String key;
        Date event_time;

        //Method to upload Image to the eventsList
        public void upload() {
            CountLTV.ImageList.put(key, this);
        }

}
