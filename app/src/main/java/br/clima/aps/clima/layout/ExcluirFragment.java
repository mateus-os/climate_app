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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import br.clima.aps.clima.R;
import br.clima.aps.clima.consume.ClimaConsume;
import br.clima.aps.clima.consume.MunicipioRepo;
import br.clima.aps.clima.model.ClimaModel;
import br.clima.aps.clima.ws.ClimaHttp;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExcluirFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExcluirFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExcluirFragment extends Fragment {
    private DeleteClima mTask;
    private Button btnExcluir;
    private EditText idClima;
    private ProgressBar mProgressBar;
    private View view;
    private ClimaModel climaCons;
    private MunicipioRepo muni;

    private OnFragmentInteractionListener mListener;

    public ExcluirFragment() {
        // Required empty public constructor
    }

    public static ExcluirFragment newInstance(String param1, String param2) {
        ExcluirFragment fragment = new ExcluirFragment();
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
        this.view = inflater.inflate(R.layout.fragment_excluir, container, false);
        initializeUI();
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
//            Toast.makeText(context, "Excluir Clima", Toast.LENGTH_SHORT).show();
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

    public void initializeUI(){
        btnExcluir = view.findViewById(R.id.btn_excluir);

        idClima = view.findViewById(R.id.id_clima);
        mProgressBar = view.findViewById(R.id.pg_cons);


        btnExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                formDataToClima();
            }
        });

        idClima.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        if (idClima.getText().toString().isEmpty()){
            Toast.makeText(getActivity(), "Por favor preencha o ID", Toast.LENGTH_SHORT).show();
        } else {
            climaCons = new ClimaModel(Integer.parseInt(idClima.getText().toString()));
            if(ClimaHttp.haveConnection(getActivity())) {
                startDelete();
            } else {
                Toast.makeText(getActivity(), "Você não está conectado", Toast.LENGTH_SHORT).show();
            }
            }
    }

    class DeleteClima extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }
        @Override
        protected String doInBackground(Void... strings) {
            publishProgress(50);
            String clima = ClimaConsume.excluirClima(climaCons, muni.getURL());
            publishProgress(100);
            return clima;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            showProgress(false);
            if(result.equals("OK")) {
                Toast.makeText(getActivity(), "Dados excluídos", Toast.LENGTH_SHORT).show();
                idClima.setText("");
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
    private void startDelete() {
        if (mTask == null ||  mTask.getStatus() != AsyncTask.Status.RUNNING) {
            mTask = new DeleteClima();
            mTask.execute();
        }
    }
}
