package idea.spark.in.vehicleowner;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import java.util.regex.Pattern;

public class Utils {

    public static final String STR_HACKURL = "https://parivahan.gov.in/rcdlstatus/service/checkRC_DLStatus/checkRCStatus?";
    public static final String STR_SUCCESS = "SUCCESS";

    public static final int ERR_INTERNET = 100;
    public static final String STR_ERR_INTERNET = "No Internet Connection";
    public static final int ERR_EXCEPTION = 102;
    public static final String STR_ERR_EXCEPTION = "Something went wrong";
    public static final String STR_ERR_REJECTED = "Invalid Details, Please Check";
    public static final String STR_ERR_MISSING_DETAILS = "Please Enter Vehice Number Details";
    public static final String STR_ERR_QUEUE = "Please wait, Request in queue";

    public static final int REQ_SUCCESS = 103;


    public static boolean internetStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param context
     * @return
     * Used to get the Device Primary Email ID
     * GET_ACCOUNTS Permission should be added in manifest file
     * DEFAULT VALUE - null
     * Last Modified By M.Kanagasabapathi on July 20, 2016
     */
    public static String getPrimaryEmail(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+

        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                return account.name;
            }
        }
        return null;
    }
}
