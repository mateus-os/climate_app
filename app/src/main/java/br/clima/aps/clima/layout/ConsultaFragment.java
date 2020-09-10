package br.clima.aps.clima.layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.clima.aps.clima.R;
import br.clima.aps.clima.consume.ClimaConsume;
import br.clima.aps.clima.consume.MunicipioRepo;
import br.clima.aps.clima.model.ClimaModel;
import br.clima.aps.clima.ws.ClimaHttp;

public class ConsultaFragment extends Fragment {
    //Objects
    public SharedPreferences prefs = null;
    private ConsultaClima mTask;
    private Button btnConsultar;
    private Spinner spinEstado;
    private AutoCompleteTextView autoMunicipio;
    private TextView idClima;
    private TextView maxClima;
    private TextView minClima;
    private TextView sensClima;
    private TextView atualClima;
    private TextView umidClima;
    private TextView tvMax;
    private TextView tvMin;
    private TextView tvAtual;
    private TextView tvUmid;
    private TextView tvSens;
    private TextView tvID;
    private ProgressBar mProgressBar;
    private MunicipioRepo muni;
    private View view;
    private static ClimaModel climaCons;
    private static int spinPos;
    private static ArrayAdapter<String> adpAutoMun;
    private static ArrayAdapter<String> adpSpin;
    private static ClimaModel climaRetain;

    private OnFragmentInteractionListener mListener;

    public ConsultaFragment() {

    }

   @Override
   public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable("clima", climaRetain);
   }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConsultaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultaFragment newInstance(String param2) {
        ConsultaFragment fragment = new ConsultaFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        muni = new MunicipioRepo(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_consulta, container, false);
        initializeUI();
        spinEstado.setSelection(spinPos);
        if (climaRetain != null) {
            fillClima(this.climaRetain);

            if (!climaRetain.getMunicipio().isEmpty()) {
                autoMunicipio.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoMunicipio.setText(climaRetain.getMunicipio());
                        autoMunicipio.setSelection(autoMunicipio.getText().length());
                        autoMunicipio.showDropDown();
                    }
                }, 500);
            }
        }

        return this.view;
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
//            Toast.makeText(context, "Consulta Clima", Toast.LENGTH_SHORT).show();
        }
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void initializeUI() {
        btnConsultar = view.findViewById(R.id.btn_consultar);
        spinEstado = view.findViewById(R.id.spin_estado);
        List<String> municipios = muni.listEstados();

        adpSpin = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, municipios);
        adpSpin.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinEstado.setAdapter(adpSpin);

        idClima = view.findViewById(R.id.id_clima);
        maxClima = view.findViewById(R.id.max_clima);
        minClima = view.findViewById(R.id.min_clima);
        sensClima = view.findViewById(R.id.sens_clima);
        atualClima = view.findViewById(R.id.atual_clima);
        umidClima = view.findViewById(R.id.umid_clima);
        tvMax = view.findViewById(R.id.tv_max);
        tvMin = view.findViewById(R.id.tv_min);
        tvAtual = view.findViewById(R.id.tv_atual);
        tvUmid = view.findViewById(R.id.tv_umid);
        tvSens = view.findViewById(R.id.tv_sens);
        tvID = view.findViewById(R.id.tv_id);
        mProgressBar = view.findViewById(R.id.pg_cons);

        autoMunicipio = view.findViewById(R.id.auto_cidade);

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preDownload();
            }
        });

        spinEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinPos = position;
                autoMunicipio.setText("");
                adpAutoMun = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                        muni.listMunicipios(parent.getItemAtPosition(position).toString()));
                autoMunicipio.setAdapter(adpAutoMun);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        autoMunicipio.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                climaCons = new ClimaModel(parent.getItemAtPosition(position).toString(),
                        spinEstado.getSelectedItem().toString());
            }
        });

        autoMunicipio.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getActivity().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    preDownload();

                    return true;
                }
                return false;
            }
        });
    }

    class ConsultaClima extends AsyncTask<Void, Integer, ClimaModel> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }
        @Override
        protected ClimaModel doInBackground(Void... strings) {
            publishProgress(50);
            ClimaModel clima = ClimaConsume.getClima(climaCons, muni.getURL());
            publishProgress(100);
            return clima;
        }
        @Override
        protected void onPostExecute(ClimaModel clima) {
            super.onPostExecute(clima);
            showProgress(false);
            if(clima != null) {
                if(clima.getResponse() == null) {
                    climaRetain = clima;
                    fillClima(clima);
                } else {
                    Toast.makeText(getActivity(), clima.getResponse(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Não há dados cadastrados para esta cidade", Toast.LENGTH_SHORT).show();
                clearForms();
            }
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressBar.setProgress(progress[0]);
        }
    }

    private void showProgress(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }

    private void preDownload() {
        clearForms();
        if(climaCons != null && !climaCons.getMunicipio().isEmpty()) {
            if (ClimaHttp.haveConnection(getActivity())) {
                startDownload();
            } else {
                Toast.makeText(getActivity(), "Você não está conectado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), "Escolha uma cidade válida", Toast.LENGTH_SHORT).show();
            clearForms();
        }
    }

    private void startDownload() {
        if (mTask == null ||  mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new ConsultaClima();
            mTask.execute();
        }
    }

    private void clearForms(){
        idClima.setText("");
        maxClima.setText("");
        minClima.setText("");
        sensClima.setText("");
        atualClima.setText("");
        umidClima.setText("");
        umidClima.setVisibility(View.INVISIBLE);
        tvMax.setVisibility(View.INVISIBLE);
        tvMin.setVisibility(View.INVISIBLE);
        tvAtual.setVisibility(View.INVISIBLE);
        tvUmid.setVisibility(View.INVISIBLE);
        tvSens.setVisibility(View.INVISIBLE);
        tvID.setVisibility(View.INVISIBLE);
    }

    private void fillClima(ClimaModel clima){
        idClima.setText(String.valueOf(clima.getId_clima()));
        maxClima.setText(clima.getTemp_max()+"°C");
        minClima.setText(clima.getTemp_min()+"°C");
        sensClima.setText(clima.getSens_term()+"°C");
        atualClima.setText(clima.getTemp_atual()+"°C");
        umidClima.setText(clima.getUmidade()+"%");
        umidClima.setVisibility(View.VISIBLE);
        tvMax.setVisibility(View.VISIBLE);
        tvMin.setVisibility(View.VISIBLE);
        tvAtual.setVisibility(View.VISIBLE);
        tvUmid.setVisibility(View.VISIBLE);
        tvSens.setVisibility(View.VISIBLE);
        tvID.setVisibility(View.VISIBLE);
        btnConsultar.requestFocus();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            umidClima.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_water, 0);
        } else umidClima.append("  ");
    }
}
