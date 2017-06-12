package elatesoftware.com.testkudan.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class MessageBoxBuilder {

    private static final String BUTTON_POSITIVE_TEXT = "Ok";

    public static AlertDialog build(Context context, String messageText){
      return build(context, messageText, null);
    }

    public static AlertDialog build(Context context, String messageText, DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(messageText)
                .setCancelable(false)
                .setPositiveButton(BUTTON_POSITIVE_TEXT, onClickListener);
        return builder.create();
    }
}
