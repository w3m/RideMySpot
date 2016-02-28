package activity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.w3m.ridemyspot.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import account.SessionManager;
import entity.Rmsendpoint;
import entity.model.CollectionResponseUsers;
import entity.model.Users;

/**
 * @author manish
 * 
 */
public class SplashScreenActivity extends Activity implements View.OnClickListener {
	Context mContext = SplashScreenActivity.this;
	AccountManager mAccountManager;
	String token;
	int serverCode;
	private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    private Button mButtonRideMySpot;
    private Button mButtonAlreadySign;
	private TextView mTextViewLoading;
	private ProgressBar mProgressBar;

    private String mAdress;
    private String mType;
    private String mName;

	SessionManager mSessionManager;

    public static final int REQUEST_GET_ACCOUNTS_FOR_NEW_ACCOUNT = 101;
    public static final int REQUEST_GET_ACCOUNTS_FOR_ALREADY_SIGNIN = 102;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Splash screen view
		setContentView(R.layout.splashscreen);

		mButtonRideMySpot = (Button)findViewById(R.id.buttonRideMySpot);
        mButtonAlreadySign = (Button)findViewById(R.id.buttonAlreadySignin);
		mTextViewLoading = (TextView)findViewById(R.id.textViewLoading);
		mProgressBar = (ProgressBar)findViewById(R.id.progressBarLoading);

        mButtonRideMySpot.setOnClickListener(this);
        mButtonAlreadySign.setOnClickListener(this);

	}

	private void checkAccountPermission(final int PermissionForWhat){
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ActivityCompat.requestPermissions(SplashScreenActivity.this, new String[]{Manifest.permission.GET_ACCOUNTS}, PermissionForWhat);
                }
            });
            builder.setTitle("Ride My Spot");
            builder.setMessage("Vous avez la dernière version d'Android, magnifique!\nCependant il va vous être demandé une permission pour accèder à vos contacts. Ceci pour permettre le pré-remplissage du champs de votre email du formulaire qui va suivre.\nEn aucun cas nous accèderons à votre liste de contact, libre à vous d'accepter cette permission ou non.");
            builder.setIcon(R.drawable.ic_launcher);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
		} else {
			alertDialogRideMySpot(PermissionForWhat);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        alertDialogRideMySpot(requestCode);
	}

    private void alertDialogRideMySpot(final int isAlreadySignin){
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_user);

        final TextView email = (TextView)dialog.findViewById(R.id.add_user_email);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED) {
            final String listAdress[] = getAccountNames();
            if(listAdress.length > 1){
                dialog.findViewById(R.id.add_user_email_button).setVisibility(View.VISIBLE);
                dialog.findViewById(R.id.add_user_email_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Dialog dialog = new Dialog(mContext);
                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View convertView = (View) inflater.inflate(R.layout.email, null);
                        dialog.setTitle("Choisissez votre compte");
                        dialog.setContentView(convertView);
                        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                        lv.setAdapter(new ArrayAdapter<String>(mContext, android.R.layout.simple_list_item_1, listAdress));
                        dialog.show();

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                mAdress = parent.getItemAtPosition(position).toString();
                                email.setText(mAdress);
                                dialog.dismiss();
                            }
                        });

                    }
                });
            } else if(listAdress.length == 1){
                mAdress = listAdress[0];
                email.setText(mAdress);
            }
        }

        if(isAlreadySignin == REQUEST_GET_ACCOUNTS_FOR_ALREADY_SIGNIN){
            ((TextView) dialog.findViewById(R.id.add_user_header_text)).setText(getString(R.string.add_user_title_already_signin));
            ((TextView) dialog.findViewById(R.id.add_user_description_text)).setText(getString(R.string.add_user_description_already_signin));
            dialog.findViewById(R.id.add_user_name_text).setVisibility(View.GONE);
            dialog.findViewById(R.id.add_user_name).setVisibility(View.GONE);
            dialog.findViewById(R.id.add_user_type_text).setVisibility(View.GONE);
            dialog.findViewById(R.id.add_user_type).setVisibility(View.GONE);
        } else {
            final RadioButton radioRoller = (RadioButton) dialog.findViewById(R.id.add_user_roller);
            final RadioButton radioSkate = (RadioButton) dialog.findViewById(R.id.add_user_skate);
            final RadioButton radioBmx = (RadioButton) dialog.findViewById(R.id.add_user_bmx);

            radioRoller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mType = getString(R.string.text_roller);
                    radioSkate.setChecked(false);
                    radioBmx.setChecked(false);
                }
            });
            radioSkate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mType = getString(R.string.text_skate);
                    radioRoller.setChecked(false);
                    radioBmx.setChecked(false);
                }
            });
            radioBmx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mType = getString(R.string.text_bmx);
                    radioRoller.setChecked(false);
                    radioSkate.setChecked(false);
                }
            });
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.add_user_validate);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdress = ((TextView) dialog.findViewById(R.id.add_user_email)).getText().toString();
                mName = ((TextView) dialog.findViewById(R.id.add_user_name)).getText().toString();
                String check = "";
                boolean checkName = false;
                boolean checkType = false;
                boolean checkMail = false;
                if(isAlreadySignin != REQUEST_GET_ACCOUNTS_FOR_ALREADY_SIGNIN) {
                    if (mName.length() < 3) {
                        check += " " + getString(R.string.add_user_minimum_name);
                        checkName = true;
                    }
                    if (mType == null) {
                        check += " " + getString(R.string.add_user_minimum_type);
                        checkType = true;
                    }
                }
                if(!validate(mAdress)){
                    check += " " + getString(R.string.add_user_minimum_mail);
                    checkMail = true;
                }
                if(checkName || checkType || checkMail){
                    Toast.makeText(SplashScreenActivity.this, getString(R.string.add_user_minimum_error_text) + check, Toast.LENGTH_LONG).show();
                } else {
                    mButtonRideMySpot.setVisibility(View.INVISIBLE);
                    mButtonAlreadySign.setVisibility(View.INVISIBLE);
                    mTextViewLoading.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    new ListUsers(isAlreadySignin).execute(mAdress);
                }
            }
        });

        dialog.show();
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

	@Override
	public void onClick(View view) {
		switch (view.getId()){
            case R.id.buttonRideMySpot:
                checkAccountPermission(REQUEST_GET_ACCOUNTS_FOR_NEW_ACCOUNT);
                break;
            case R.id.buttonAlreadySignin:
                checkAccountPermission(REQUEST_GET_ACCOUNTS_FOR_ALREADY_SIGNIN);
                break;
		}
	}

    public String[] getAccountNames() {
        mAccountManager = AccountManager.get(mContext);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }


    private class ListUsers extends AsyncTask<String, Void, CollectionResponseUsers> {

        int isAlreadySignin;

        ListUsers (int isAlreadySignin){
            this.isAlreadySignin = isAlreadySignin;
        }

        @Override
        protected CollectionResponseUsers doInBackground(String... params) {
            CollectionResponseUsers User = null;
            try{
                Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
                Rmsendpoint service = builder.build();
                User = service.listUsers().setPAdress(params[0]).execute();
            } catch (Exception e){
                Log.d("impossible de récupérer les informations du compte", e.getMessage(), e);//TODO getressource
            }
            return User;
        }


        @Override
        protected void onPostExecute(CollectionResponseUsers User) {
            if(isAlreadySignin == REQUEST_GET_ACCOUNTS_FOR_ALREADY_SIGNIN) {
                if(User != null && User.getItems() != null && User.getItems().get(0) != null){
                    Users user = User.getItems().get(0);
                    mSessionManager = new SessionManager(SplashScreenActivity.this);
                    mSessionManager.createLoginSession(user.getId().toString(), user.getName(), user.getAdress(), user.getType());
                    returnToMap();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.add_user_already_loading_error), Toast.LENGTH_LONG).show();
                    returnToSplashScreen();
                }
            } else {
                if(User != null && User.getItems() != null && User.getItems().get(0) != null){
                    Toast.makeText(getBaseContext(), getString(R.string.add_user_already_error), Toast.LENGTH_LONG).show();
                    returnToSplashScreen();
                } else {
                    new AddUser().execute();
                }
            }
        }
    }

    public void returnToMap() {
        Intent intent = new Intent(mContext, MapActivity.class);
        mContext.startActivity(intent);
        finish();
    }

    public void returnToSplashScreen() {
        mButtonRideMySpot.setVisibility(View.VISIBLE);
        mButtonAlreadySign.setVisibility(View.VISIBLE);
        mTextViewLoading.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private class AddUser extends AsyncTask<Void, Void, Users>{

        public AddUser(){
        }

        @Override
        protected Users doInBackground(Void... params) {
            Users response = null;
            try{

                Rmsendpoint.Builder builder = new Rmsendpoint.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
                Rmsendpoint service = builder.build();

                Users User = new Users();

                User.setAdress(mAdress);
                User.setName(mName);
                User.setType(mType);

                response = service.insertUsers(User).execute();

            } catch (Exception e){
                Log.d(getString(R.string.add_user_loading_error_log), e.getMessage(), e);
            }
            return response;
        }

        @Override
        protected void onPostExecute(Users User) {

            if(User != null){
                mSessionManager = new SessionManager(SplashScreenActivity.this);
                mSessionManager.createLoginSession(User.getId().toString(), User.getName(), User.getAdress(), User.getType());
                returnToMap();
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.add_user_loading_error), Toast.LENGTH_LONG).show();
                returnToSplashScreen();
            }

        }
    }
}