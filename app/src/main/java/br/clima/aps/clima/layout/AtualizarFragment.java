package br.clima.aps.clima.layout;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AtualizarFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AtualizarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AtualizarFragment extends Fragment {
    private AtualizaClima mTask;
    private Button btnAtualizar;
    private Spinner spinEstado;
    private AutoCompleteTextView autoMunicipio;
    private EditText idClima;
    private EditText maxClima;
    private EditText minClima;
    private EditText sensClima;
    private EditText atualClima;
    private EditText umidClima;
    private ProgressBar mProgressBar;
    private MunicipioRepo muni;
    private View view;
    private static ClimaModel climaCons;
    private static int spinPos;
    private static ArrayAdapter<String> adpAutoMun;
    private static ArrayAdapter<String> adpSpin;
    private static ClimaModel climaRetain;

    private OnFragmentInteractionListener mListener;

    public AtualizarFragment() {
        // Required empty public constructor
    }

    public static AtualizarFragment newInstance(String param1, String param2) {
        AtualizarFragment fragment = new AtualizarFragment();
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
        this.view = inflater.inflate(R.layout.fragment_atualizar, container, false);
        initializeUI();

        if (climaRetain != null){
            recoverData(false);
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
//            Toast.makeText(context, "Atualizar Clima", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        recoverData(true);
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

    public void initializeUI(){
        btnAtualizar = view.findViewById(R.id.btn_atualizar);
        spinEstado = view.findViewById(R.id.spin_estado_upd);
        List<String> municipios = muni.listEstados();

        adpSpin = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, municipios);
        adpSpin.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinEstado.setAdapter(adpSpin);

        idClima = view.findViewById(R.id.id_clima_upd);
        maxClima = view.findViewById(R.id.max_clima_upd);
        minClima = view.findViewById(R.id.min_clima_upd);
        sensClima = view.findViewById(R.id.sens_clima_upd);
        atualClima = view.findViewById(R.id.atual_clima_upd);
        umidClima = view.findViewById(R.id.umid_clima_upd);
        mProgressBar = view.findViewById(R.id.pg_cons);

        autoMunicipio = view.findViewById(R.id.auto_cidade_upd);

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                formDataToClima();
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

        umidClima.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager inputManager =
                            (InputMethodManager) getActivity().
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    formDataToClima();

                    return true;
                }
                return false;
            }
        });
    }

    private void formDataToClima() {
        ClimaModel clima = null;
        if(climaCons == null) {
            Toast.makeText(getActivity(), "Por favor preencha a Municipio corretamente", Toast.LENGTH_SHORT).show();
        } else if (idClima.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Por favor preencha o ID", Toast.LENGTH_SHORT).show();
        } else {
            clima = new ClimaModel(Integer.parseInt(idClima.getText().toString()), climaCons.getMunicipio(), climaCons.getEstado(),
                    maxClima.getText().toString(), minClima.getText().toString(), sensClima.getText().toString(),
                    atualClima.getText().toString(), umidClima.getText().toString());
            climaCons = clima;
            if (clima.validateClima()) {
                if(ClimaHttp.haveConnection(getActivity())) {
                    startUpdate();
                } else {
                    Toast.makeText(getActivity(), "Você não está conectado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class AtualizaClima extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }
        @Override
        protected String doInBackground(Void... strings) {
            publishProgress(50);
            String clima = ClimaConsume.atualizarClima(climaCons, muni.getURL());
            publishProgress(100);
            return clima;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if(result.equals("OK")) {
                Toast.makeText(getActivity(), "Dados atualizados", Toast.LENGTH_SHORT).show();
                clearForms();
            } else if(result.contains("failed to connect")){
                Toast.makeText(getActivity(), "Tempo esgotado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "ID não encontrado", Toast.LENGTH_SHORT).show();
            }
            climaCons = null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressBar.setProgress(progress[0]);
        }
    }

    private void showProgress(boolean exibir) {
        mProgressBar.setVisibility(exibir ? View.VISIBLE : View.GONE);
    }
    private void startUpdate() {
        if (mTask == null ||  mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new AtualizaClima();
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
        autoMunicipio.setText("");
        climaRetain = null;
    }
    
    private void recoverData(boolean save) {
        if (save) {
            this.climaRetain = new ClimaModel();
//            this.climaRetain.setId_clima(Integer.parseInt(idClima.getText().toString()));
            this.climaRetain.setTemp_max(maxClima.getText().toString());
            this.climaRetain.setTemp_min(minClima.getText().toString());
            this.climaRetain.setSens_term(sensClima.getText().toString());
            this.climaRetain.setTemp_atual(atualClima.getText().toString());
            this.climaRetain.setUmidade(umidClima.getText().toString());
        } else {
            spinEstado.setSelection(spinPos);
            if(this.climaCons != null) {

                autoMunicipio.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        autoMunicipio.setText(climaCons.getMunicipio());
                        autoMunicipio.setSelection(autoMunicipio.getText().length());
                        autoMunicipio.showDropDown();
                    }
                }, 500);
            }
//            idClima.setText(String.valueOf(climaRetain.getId_clima()));
            maxClima.setText(this.climaRetain.getTemp_max());
            minClima.setText(this.climaRetain.getTemp_min());
            sensClima.setText(this.climaRetain.getSens_term());
            atualClima.setText(this.climaRetain.getTemp_atual());
            umidClima.setText(this.climaRetain.getUmidade());
        }
    }
}
