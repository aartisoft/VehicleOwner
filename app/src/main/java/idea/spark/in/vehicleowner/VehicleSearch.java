package idea.spark.in.vehicleowner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VehicleSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VehicleSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VehicleSearch extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private EditText regNoPart1,regNoPart2;
    private HandleXML obj;
    private Button submitBtn;
    View view;
    boolean isQueueFree;
    private FirebaseAnalytics mFirebaseAnalytics;

    public VehicleSearch() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VehicleSearch.
     */
    // TODO: Rename and change types and number of parameters
    public static VehicleSearch newInstance(String param1, String param2) {
        VehicleSearch fragment = new VehicleSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void showSnackBar(String err,boolean isActionreq){
        final Snackbar snackbar = Snackbar.make(view, err, Snackbar.LENGTH_LONG);
                if(isActionreq) {
                    snackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                            getRegistrationData();
                        }
                    });
                }
        snackbar.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_vehicle_search,container, false);

        isQueueFree=true;

        submitBtn=(Button)view.findViewById(R.id.btn_submit);
        regNoPart1=(EditText)view.findViewById(R.id.input_regpart1);
        regNoPart2=(EditText)view.findViewById(R.id.input_regpart2);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        regNoPart2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    getRegistrationData();
                    handled = true;
                }
                return handled;
            }
        });

        regNoPart1.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus)
                    regNoPart1.setHint("");
                else
                    regNoPart1.setHint(getString(R.string.regnopart1));
            }
        });

        regNoPart2.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus)
                    regNoPart2.setHint("");
                else
                    regNoPart2.setHint(getString(R.string.regnopart2));
            }
        });




        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (Character.isSpaceChar(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };

        regNoPart1.setFilters(DisableSpecialCharacters(7));



        AdView mBottomAdView = (AdView) view.findViewById(R.id.bottomBanner);
        AdRequest adBottomRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mBottomAdView.loadAd(adBottomRequest);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                getRegistrationData();
            }
        });

        return view;
    }

    public InputFilter[] DisableSpecialCharacters(int length) {
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        InputFilter[] FilterArraym = new InputFilter[2];
        FilterArraym[0] = filter;

        FilterArraym[1] = new InputFilter.LengthFilter(length);
        return FilterArraym;
    }

    private void hideSoftKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    class getData extends AsyncTask<String,Void,Integer>{
        private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isQueueFree=false;
            dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setMessage("Requesting details, please wait...");
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                if (Utils.internetStatus(getActivity())) {
                    String finalurl = Utils.STR_HACKURL + "reg1=" + params[0] + "&reg2=" + params[1];
                    obj = new HandleXML(finalurl);
                    if(!obj.fetchXML()){
                        return Utils.ERR_EXCEPTION;
                    }
                    while (obj.parsingComplete) ;
                }else{
                    return Utils.ERR_INTERNET;
                }
            }catch (Exception e){
                return Utils.ERR_EXCEPTION;
            }
            return Utils.REQ_SUCCESS;
        }

        @Override
        protected void onPostExecute(Integer aBoolean) {
            super.onPostExecute(aBoolean);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            isQueueFree=true;

            if(aBoolean==Utils.ERR_INTERNET){
                showSnackBar(Utils.STR_ERR_INTERNET,true);
                return;
            }

            if(aBoolean==Utils.ERR_EXCEPTION){
                showSnackBar(Utils.STR_ERR_EXCEPTION,false);
                return;
            }

            if(aBoolean==Utils.REQ_SUCCESS) {
                if (obj.getStatus().compareToIgnoreCase(Utils.STR_SUCCESS) == 0) {
                    Intent intent = new Intent(getActivity().getApplicationContext(),ResultScreen.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("data", obj);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    showSnackBar(Utils.STR_ERR_REJECTED,false);
                    return;
                }
            }
        }
    }

    private void getRegistrationData(){
        if(isQueueFree){
            if(regNoPart1.getText().length()>0 && regNoPart2.getText().length()>0) {
                new getData().execute(regNoPart1.getText().toString().trim(), regNoPart2.getText().toString().trim());
                Bundle bundle = new Bundle();
                bundle.putString("email",Utils.getPrimaryEmail(getActivity()));
                bundle.putString("regnopart1",regNoPart1.getText().toString().trim().toUpperCase());
                bundle.putString("regnopart2",regNoPart2.getText().toString().trim().toUpperCase());
                mFirebaseAnalytics.logEvent("searchDetails", bundle);
            }else{
                showSnackBar(Utils.STR_ERR_MISSING_DETAILS,false);
            }
        }else{
            showSnackBar(Utils.STR_ERR_QUEUE,false);
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isQueueFree=true;
        regNoPart1.setText("");
        regNoPart2.setText("");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
