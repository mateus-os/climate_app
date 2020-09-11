package br.clima.aps.clima;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

import br.clima.aps.clima.consume.ClimaConsume;
import br.clima.aps.clima.consume.MunicipioRepo;
import br.clima.aps.clima.layout.AtualizarFragment;
import br.clima.aps.clima.layout.ConsultaFragment;
import br.clima.aps.clima.layout.ExcluirFragment;
import br.clima.aps.clima.layout.InserirFragment;
import br.clima.aps.clima.model.MunicipiosModel;
import br.clima.aps.clima.ws.ClimaHttp;

public class MainActivity extends AppCompatActivity {

    public SharedPreferences prefs = null;
    private MunicipiosTask mTask;
    private List<MunicipiosModel> mMunicipio = null;
    private TextView mTextMensagem;
    private ProgressBar mProgressBar;
    private LayoutInflater inflater;
    private FragmentTransaction transaction;
    private MunicipioRepo muni;
    private BottomNavigationView navigation;
    private ActionBar bar;
    private Window window;
    private int screen = 0;
    public Context context = getApplication();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            transaction = fragmentManager.beginTransaction();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bar = getSupportActionBar();
                window = getWindow();
            }
            switch (item.getItemId()) {
                case R.id.navigation_consultar:
                    screen = 1;
                    transaction.replace(R.id.content, new ConsultaFragment()).commit();
                    navigation.setBackgroundColor(Color.parseColor("#3F51B5"));
                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3F51B5")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(Color.parseColor("#3949AB"));
                        window.setNavigationBarColor(Color.parseColor("#3949AB"));
                    }
                    return true;
                case R.id.navigation_atualizar:
                    screen = 2;
                    transaction.replace(R.id.content, new AtualizarFragment()).commit();
                    navigation.setBackgroundColor(Color.parseColor("#0288D1"));
                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0288D1")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(Color.parseColor("#0277BD"));
                        window.setNavigationBarColor(Color.parseColor("#0277BD"));
                    }
                    return true;
                case R.id.navigation_inserir:
                    screen = 3;
                    transaction.replace(R.id.content, new InserirFragment()).commit();
                    navigation.setBackgroundColor(Color.parseColor("#43A047"));
                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#43A047")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(Color.parseColor("#388E3C"));
                        window.setNavigationBarColor(Color.parseColor("#388E3C"));
                    }
                    return true;
                case R.id.navigation_delete:
                    screen = 4;
                    transaction.replace(R.id.content, new ExcluirFragment()).commit();
                    navigation.setBackgroundColor(Color.parseColor("#F44336"));
                    bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F44336")));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        window.setStatusBarColor(Color.parseColor("#E53935"));
                        window.setNavigationBarColor(Color.parseColor("#E53935"));
                    }
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (ProgressBar)super.findViewById(R.id.pgbar);
        mTextMensagem = (TextView)super.findViewById(R.id.tv_status);

        prefs = getSharedPreferences("br.clima.aps.clima", MODE_PRIVATE);
        muni = new MunicipioRepo(getApplicationContext());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        bar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#3949AB"));
            bar.setLogo(R.drawable.app_icon);
            bar.setDisplayUseLogoEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean("firstrun", true)) {
            Toast.makeText(getApplicationContext(), "Bem Vindo!", Toast.LENGTH_SHORT).show();
            if (mTask == null) {
                if (ClimaHttp.haveConnection(getApplicationContext())) {
                    startDownload();
                } else {
                    mTextMensagem.setText("Sem conexão");
                }
            } else if (mTask.getStatus() == AsyncTask.Status.RUNNING) {
                showProgress(true, 0);
            }
        } else {
            callScreen();
        }
    }

    class MunicipiosTask extends AsyncTask<Void, Integer, List<MunicipiosModel>> implements Serializable {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lockScreenOrientation();
            showProgress(true, 0);
        }
        @Override
        protected List<MunicipiosModel> doInBackground(Void... strings) {
            publishProgress(1);
            muni = new MunicipioRepo(getApplicationContext());
            List<MunicipiosModel> municipiosList = ClimaConsume.getAllMunicipios(muni.getURL());
            if(municipiosList != null) {
                publishProgress(2);
                muni.inserirMunicipios(municipiosList);
                return municipiosList;
            } else {
//                Toast.makeText(getApplicationContext(), "Dados não encontrados", Toast.LENGTH_SHORT).show();
                showInputDialog();
                municipiosList = ClimaConsume.getAllMunicipios(muni.getURL());
                muni.inserirMunicipios(municipiosList);
                return municipiosList;
            }
        }
        @Override
        protected void onPostExecute(List<MunicipiosModel> municipios) {
            super.onPostExecute(municipios);
            showProgress(false, 0);
            //Implementar verificação
            if (municipios != null) {
                callScreen();
                Toast.makeText(getApplicationContext(), "Dados baixados", Toast.LENGTH_SHORT).show();
                prefs.edit().putBoolean("firstrun", false).commit();
            } else {
                mTextMensagem.setText("Falha ao obter Municípios");
            }
            unlockScreenOrientation();
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            showProgress(true, progress[0]);
        }
    }

    public void showProgress(boolean exibir, int i) {
        if (exibir && i == 1) {
            mTextMensagem.setText("Baixando lista de municípios");
        } else if (exibir && i == 2){
            mTextMensagem.setText("Armazenando lista de municípios");
        }
        mTextMensagem.setVisibility(exibir ? View.VISIBLE : View.GONE);
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }
    public void startDownload() {
        if (mTask == null ||  mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new MunicipiosTask();
            mTask.execute();
        }
    }
    public void callScreen() {
        mProgressBar.setVisibility(View.GONE);
        mTextMensagem.setVisibility(View.GONE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        navigation.setVisibility(View.VISIBLE);
        transaction = fragmentManager.beginTransaction();
        switch (screen) {
            case 1:
                transaction.replace(R.id.content, new ConsultaFragment()).commit();
                break;
            case 2:
                transaction.replace(R.id.content, new AtualizarFragment()).commit();
                break;
            case 3:
                transaction.replace(R.id.content, new InserirFragment()).commit();
                break;
            case 4:
                transaction.replace(R.id.content, new ExcluirFragment()).commit();
                break;
                default:
                    transaction.replace(R.id.content, new ConsultaFragment()).commit();
        }

    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void unlockScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    public static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setShiftingMode(false);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.ed_customIp);
        final RadioGroup radioGroup = promptView.findViewById(R.id.group_radio);
        final RadioButton radioDefault = promptView.findViewById(R.id.radioDefault);
        final RadioButton radioCustom = promptView.findViewById(R.id.radioCustom);
        final RadioButton radioLocal = promptView.findViewById(R.id.radioLocal);
        final EditText etIpCustom = promptView.findViewById(R.id.ed_customIp);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        int checked = radioGroup.getCheckedRadioButtonId();

                        if(checked == radioDefault.getId()) {
                            muni.setURL("http://mtsde.sytes.net:9191/");
                        } else if(checked == radioLocal.getId()){
                            muni.setURL("http://192.168.0.100:8080/");
                        } else if(checked == radioCustom.getId()){
                            muni.setURL("http://"+etIpCustom.getText().toString()+"/");
                        }
                    }
                })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.setTitle("Defina o IP e Porta");
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.it_setip:
                showInputDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
