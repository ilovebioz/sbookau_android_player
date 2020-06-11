package com.sectic.sbookau;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sectic.sbookau.ultils.AuthSetting;
import com.sectic.sbookau.service.PostMessageToCloudService;
import com.sectic.sbookau.ultils.DefSetting;
import com.sectic.sbookau.ultils.ValidUtils;

import es.dmoral.toasty.Toasty;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getSimpleName();

    private View oView;
    private EditText txtSuggestBook;
    private EditText txtEmail;


	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        oView = inflater.inflate(R.layout.fragment_home, container, false);
        initGUIControlsAndEvent();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onCreateView");
        }
        return oView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "onResume");
        }
    }

    private void initGUIControlsAndEvent()
    {
        ImageButton btnSuggestBook = oView.findViewById(R.id.btn_suggest_book);
        ImageButton btnSearch = oView.findViewById(R.id.btn_search_book);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText txtSearchValue = (EditText)oView.findViewById(R.id.txt_search_value);
                MainActivity oMainAct = (MainActivity)getActivity();
                if( ValidUtils.validLength(txtSearchValue, 1, 64, getString(R.string.s_res_mess_invalid_search_content)) ) {
                    oMainAct.sTextQuery = txtSearchValue.getText().toString();
                    oMainAct.displayView(-1);
                }
            }
        });

        btnSuggestBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtSuggestBook = oView.findViewById(R.id.txt_suggest_book);
                txtEmail = oView.findViewById(R.id.txt_email);

                final MainActivity oMainAct = (MainActivity) getActivity();
                if( ValidUtils.validLength(txtSuggestBook, 3, 64, getString(R.string.s_res_mess_invalid_book_name))
                        && ValidUtils.validEmail(txtEmail) ) {
                    final PostMessageToCloudService oPostMessageToCloudService = new PostMessageToCloudService(AuthSetting.gToken, txtSuggestBook.getText().toString(), txtEmail.getText().toString(), "SUGGEST");
                    oPostMessageToCloudService.oITaskCompleted = new PostMessageToCloudService.OnTaskCompleted() {
                        @Override
                        public void onTaskCompleted(boolean bResult) {
                            if(!bResult){
                                Toasty.error(getActivity(), getString(R.string.s_res_mess_suggest_book_fail), Toast.LENGTH_SHORT, true).show();
                            }else{
                                Toasty.success(getActivity(), getString(R.string.s_res_mess_suggest_book_success), Toast.LENGTH_SHORT, true).show();
                            }
                            txtSuggestBook.setText("");
                            txtEmail.setText("");
                            oMainAct.pbr_main_loading_status.setVisibility(View.GONE);
                        }
                    };
                    oMainAct.pbr_main_loading_status.setVisibility(View.VISIBLE);
                    oPostMessageToCloudService.execute();
                }
            }
        });

        if(BuildConfig.VERSION_CODE < DefSetting.gLatestCodeStep
                && DefSetting.gCodeStepMessage != null
                && !DefSetting.gCodeStepMessage.equals("")){
            Toasty.info(getActivity(), DefSetting.gCodeStepMessage, Toast.LENGTH_SHORT, true).show();
        }

        if(BuildConfig.enableDebugLogging) {
            Log.i(TAG, "initGUIControlsAndEvent");
        }
    }
}
