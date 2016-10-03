// WhereIsMyPhone@andrebetz.de
package de.andrebetz.whereismyphone;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by andre on 25.09.2016.
 */

public class DataFile {

    private String   emailAddress;
    private String   emailUser;
    private String   emailPassword;
    private String   emailTo;
    private String   emailHost;
    private String   emailSub;
    private String   emailUpdate;
    private String   emailPort;

    public DataFile() {
        emailAddress    = "MailAddressFrom";
        emailUser       = "Username";
        emailPassword   = "UserPassword";
        emailTo         = "MailAddressTo";
        emailHost       = "MailHost";
        emailSub        = "MailSubject";
        emailUpdate     = "30";
	    emailPort       = "465";
    }
    public String getEmailPort() {
        return this.emailPort;
    }
    public void setEmailPort(String port) {
        this.emailPort = port;
    }
    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmailSub() {
        return emailSub;
    }

    public void setEmailSub(String emailSub) {
        this.emailSub = emailSub;
    }

    public String getEmailUpdate() {
        return emailUpdate;
    }

    public void setEmailUpdate(String emailUpdate) {
        this.emailUpdate = emailUpdate;
    }


    public static boolean dir_exists(String dir_path) {
        boolean ret = false;
        File dir = new File(dir_path);
        if(dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    public static boolean file_exists(String file_path) {
        File f = new File(file_path);
        if ( f.exists() && !f.isDirectory() )
            return true;
        return false;
    }

    public static boolean isWriteable(String filename) {
        File sample = new File(filename + ".dir");
        try {
            sample.createNewFile();
            sample.delete();
            return true;
        } catch ( IOException e) {
            return false;
        }
    }

    public static boolean checkAndCreate(String appPath) {
        if ( false == dir_exists( appPath ) ) {
            if ( isWriteable( appPath ) ) {
                new File(appPath).mkdirs();
            } else {
                return false;
            }
        }
        return true;
    }
    public static String getFilePath(Context context){
        String filePath = "";
        String packageName   = context.getPackageName();
        int iDExtCardDir     = context.getResources().getIdentifier("file_str_file_extCardDir", "string", packageName);
        String strExtCardDir = context.getString(iDExtCardDir);
        int iDAppDev         = context.getResources().getIdentifier("app_dev", "string", packageName);
        String strAppDev     = context.getString(iDAppDev);
        try {
            String extSdCard = strExtCardDir + "/" + strAppDev;
            String SdCard    = (new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getCanonicalPath()) + "/" + strAppDev;
            String rootCard  = (new File(context.getFilesDir().getAbsolutePath()).getCanonicalPath()) + "/" + strAppDev;
            if ( checkAndCreate( extSdCard ) ) {
                filePath = extSdCard;
            } else if ( checkAndCreate( SdCard ) ) {
                filePath = SdCard;
            } else {
                checkAndCreate( rootCard );
                filePath = rootCard;
            }
        } catch (IOException e) {

        }
        return filePath;
    }

    public static String getFileNameWithPath(Context context){
        String pathSettingFile = getFilePath(context);
        String packageName   = context.getPackageName();
        int iDFilenName     = context.getResources().getIdentifier("file_str_file_name", "string", packageName);
        String strFilenName  = context.getString(iDFilenName);
        return pathSettingFile + "/" + strFilenName;
    }

    boolean SaveSettings(String filepath) {
        FileOutputStream fos;
        File tempFile = new File(filepath);

        try {
            fos = new FileOutputStream(tempFile, false);
        } catch(IOException e){
            Log.w(TAG, "FileOutputStream IOException exception: - " + e.toString());
            return false;
        }

        XmlSerializer serializer = Xml.newSerializer();
        try {
            serializer.setOutput(fos, "UTF-8");
            serializer.startDocument(null, Boolean.valueOf(true));
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            serializer.startTag(null, "settings");

            serializer.startTag(null, Constants.emailAddressTag);
            serializer.text(this.emailAddress);
            serializer.endTag(null, Constants.emailAddressTag);

            serializer.startTag(null, Constants.emailUserTag);
            serializer.text(this.emailUser);
            serializer.endTag(null, Constants.emailUserTag);

            serializer.startTag(null, Constants.emailPasswordTag);
            serializer.text(this.emailPassword);
            serializer.endTag(null, Constants.emailPasswordTag);

            serializer.startTag(null, Constants.emailToTag);
            serializer.text(this.emailTo);
            serializer.endTag(null, Constants.emailToTag);

            serializer.startTag(null, Constants.emailHostTag);
            serializer.text(this.emailHost);
            serializer.endTag(null, Constants.emailHostTag);

            serializer.startTag(null, Constants.emailSubTag);
            serializer.text(this.emailSub);
            serializer.endTag(null, Constants.emailSubTag);

            serializer.startTag(null, Constants.emailUpdateTag);
            serializer.text(this.emailUpdate);
            serializer.endTag(null, Constants.emailUpdateTag);

            serializer.startTag(null, Constants.emailPortTag);
            serializer.text(this.emailPort);
            serializer.endTag(null, Constants.emailPortTag);

            serializer.endTag(null, "settings");

            serializer.endDocument();
            serializer.flush();
            fos.close();
        } catch(IOException e) {
            Log.w(TAG, "XmlSerializer IOException exception: - " + e.toString());
            return false;
        }
        return true;
    }

    boolean LoadBrewRecipe(String filepath) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filepath);
        }catch(IOException e){
            Log.w(TAG, "FileInputStream IOException exception: - " + e.toString());
            return false;
        }

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser myParser = xmlFactoryObject.newPullParser();
            myParser.setInput(fis, null);

            String actTag = "";

            int event = myParser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT)
            {
                String name = myParser.getName();
                String text = myParser.getText();
                switch (event) {

                    case XmlPullParser.START_TAG: {
                        actTag = name;
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        actTag = "";
                        break;
                    }
                    case XmlPullParser.TEXT: {
                        try {
                            if ( 0 < actTag.length() ) {
                                if (0 == actTag.compareToIgnoreCase(Constants.emailAddressTag)) {
                                    this.emailAddress = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailHostTag)) {
                                    this.emailHost = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailPasswordTag)) {
                                    this.emailPassword = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailSubTag)) {
                                    this.emailSub = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailToTag)) {
                                    this.emailTo = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailUpdateTag)) {
                                    this.emailUpdate = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailPortTag)) {
                                    this.emailPort = text;
                                } else if (0 == actTag.compareToIgnoreCase(Constants.emailUserTag)) {
                                    this.emailUser = text;
                                }
                            }
                        } catch( Exception e ) {
                            Log.w(TAG, "LoadBrewRecipe IOException exception:" + name + " " + e.toString());
                            return false;
                        }
                        break;
                    }

                }
                event = myParser.next();
            }
        } catch (Exception e) {
            Log.w(TAG, "XmlPullParser exception: - " + e.toString());
            return false;
        }
        return true;
    }
}
