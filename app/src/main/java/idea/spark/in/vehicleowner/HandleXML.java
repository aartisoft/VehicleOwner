package idea.spark.in.vehicleowner;

import android.os.Parcel;
import android.os.Parcelable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class HandleXML implements Parcelable {
    private String rc_reson = "reason";
    private String rc_regno = "reg_no";
    private String rc_status = "status";

    private String veh_chasis = "chasi_no";
    private String veh_engine = "engine_no";
    private String veh_fueltype = "fuel_type";
    private String veh_maker = "maker";
    private String veh_owner = "owner_name";
    private String veh_regndt = "regn_dt";
    private String veh_regno = "regn_no";
    private String veh_rto = "rto";
    private String veh_state = "state";
    private String veh_age = "vehicle_age";
    private String veh_class = "vh_class";



    private String urlString = null;
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;

    public HandleXML(String url){
        this.urlString = url;
    }

    public HandleXML(Parcel in) {
        rc_reson = in.readString();
        rc_regno = in.readString();
        rc_status = in.readString();

        veh_chasis= in.readString();
        veh_engine= in.readString();
        veh_fueltype= in.readString();
        veh_maker= in.readString();
        veh_owner= in.readString();
        veh_regndt= in.readString();
        veh_regno= in.readString();
        veh_rto= in.readString();
        veh_state= in.readString();
        veh_age= in.readString();
        veh_class= in.readString();
    }

    public String getReason(){
        return rc_reson;
    }

    public String getRegno(){
        return rc_regno;
    }

    public String getStatus(){
        return rc_status;
    }

    public String getChasis(){
        return veh_chasis;
    }

    public String getengine(){
        return veh_engine;
    }

    public String getFuel(){
        return veh_fueltype;
    }

    public String getMaker(){
        return veh_maker;
    }

    public String getOwner(){
        return veh_owner;
    }

    public String getRegdt(){
        return veh_regndt;
    }

    public String getRegNo(){
        return veh_regno;
    }

    public String getRto(){
        return veh_rto;
    }
    public String getState(){
        return veh_state;
    }
    public String getVehAge(){
        return veh_age;
    }
    public String getVehClass(){
        return veh_class;
    }



    public boolean parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;

        try {
            event = myParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("reason")){
                            rc_reson = text;
                        }

                        else if(name.equals("reg_no")){
                            rc_regno = text;
                        }

                        else if(name.equals("status")){
                            rc_status = text;
                        }
                        // Vehicle Details
                        else if(name.equals("chasi_no")){
                            veh_chasis = text;
                        }
                        else if(name.equals("engine_no")){
                            veh_engine = text;
                        }
                        else if(name.equals("fuel_type")){
                            veh_fueltype = text;
                        }
                        else if(name.equals("maker")){
                            veh_maker = text;
                        }
                        else if(name.equals("owner_name")){
                            veh_owner = text;
                        }
                        else if(name.equals("regn_dt")){
                            veh_regndt = text;
                        }
                        else if(name.equals("regn_no")){
                            veh_regno = text;
                        }
                        else if(name.equals("rto")){
                            veh_rto = text;
                        }
                        else if(name.equals("state")){
                            veh_state = text;
                        }
                        else if(name.equals("vehicle_age")){
                            veh_age = text;
                        }
                        else if(name.equals("vh_class")){
                            veh_class = text;
                        }
                        else{
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean fetchXML(){
/*
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
*/
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    if(!parseXMLAndStoreIt(myparser)){
                        stream.close();
                        return false;
                    }
                    stream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(rc_reson);
        dest.writeString(rc_regno);
        dest.writeString(rc_status);

        dest.writeString(veh_chasis);
        dest.writeString(veh_engine);
        dest.writeString(veh_fueltype);
        dest.writeString(veh_maker);
        dest.writeString(veh_owner);
        dest.writeString(veh_regndt);
        dest.writeString(veh_regno);
        dest.writeString(veh_rto);
        dest.writeString(veh_state);
        dest.writeString(veh_age);
        dest.writeString(veh_class);

    }

    public static final Parcelable.Creator<HandleXML> CREATOR = new Parcelable.Creator<HandleXML>()
    {
        public HandleXML createFromParcel(Parcel in)
        {
            return new HandleXML(in);
        }
        public HandleXML[] newArray(int size)
        {
            return new HandleXML[size];
        }
    };}