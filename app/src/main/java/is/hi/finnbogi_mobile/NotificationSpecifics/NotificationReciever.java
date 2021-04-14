package is.hi.finnbogi_mobile.NotificationSpecifics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReciever extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String USERID_ID = "NotificationReciever_userid";

    @Override
    public void onReceive(Context context, Intent intent) {
        int userID = intent.getIntExtra(USERID_ID,-1);
        Intent i = NotificationIntentService.newIntent(context, userID);
        context.startService(i);
    }
}
