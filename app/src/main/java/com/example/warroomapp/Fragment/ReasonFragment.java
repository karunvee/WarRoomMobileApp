package com.example.warroomapp.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.warroomapp.Activity.Class.ReasonSolutionRes;
import com.example.warroomapp.Activity.MainActivity;
import com.example.warroomapp.Activity.ReasonSolutionParameter;
import com.example.warroomapp.Adaptor.ReaSolAdapter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.MainApp;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReasonFragment extends Fragment {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesMachine sharedPreferencesMachine;

    private ArrayList<ReasonSolutionParameter> ReasonContainers = new ArrayList<>();
    private EditText txtReasonCodeEN;
    private EditText txtReasonCodeTH;
    private RecyclerView reasonView;
    private ReaSolAdapter ReasonAdapter;
    private Dialog Reason_dialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPrefManager = new SharedPreferencesManager(getContext());
        sharedPrefSetting = new SharedPreferencesSetting(getContext());
        sharedPreferencesMachine = new SharedPreferencesMachine(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_season, container, false);
        Button btnAddReason = view.findViewById(R.id.btnAddReason);
        reasonView = view.findViewById(R.id.ReasonContainer);

        btnAddReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog addReason_dialog = new Dialog(getContext());
                addReason_dialog.setContentView(R.layout.dialog_add_reason);
                addReason_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addReason_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_background_corner_top));
                addReason_dialog.getWindow().setGravity(Gravity.BOTTOM);
                addReason_dialog.setCancelable(false);
                addReason_dialog.show();

                txtReasonCodeEN = addReason_dialog.findViewById(R.id.txtReasonDescriptionEN);
                txtReasonCodeTH = addReason_dialog.findViewById(R.id.txtReasonDescriptionTH);
                try{
                    Button btnCancel = addReason_dialog.findViewById(R.id.btnCancelNewReason);
                    Button btnSave = addReason_dialog.findViewById(R.id.btnSaveNewReason);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addReason_dialog.dismiss();
                        }
                    });
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MachineActionFragment.RequestBodyReasonSolutionCode requestBodyReasonCode = new MachineActionFragment.RequestBodyReasonSolutionCode(
                                    sharedPreferencesMachine.getEquipType(),
                                    sharedPreferencesMachine.getName().toString() ,
                                    txtReasonCodeEN.getText().toString(),
                                    txtReasonCodeTH.getText().toString(),
                                    sharedPrefManager.getEmpNo() + " " + sharedPrefManager.getUsername()
                            );
                            LottieAnimationView animationView = addReason_dialog.findViewById(R.id.status_animation);
                            MachineActionFragment.postReasonSolutionFunc("reason", sharedPrefManager.getTokenId(), requestBodyReasonCode, new MachineActionFragment.PostReasonSolutionCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                                    playSuccessAnimation( animationView,  addReason_dialog, "success_animation.json");
                                    getReasonList(sharedPrefManager.getTokenId(), sharedPreferencesMachine.getEquipType(), sharedPreferencesMachine.getName());
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                    playSuccessAnimation( animationView,  addReason_dialog, "fail_animation.json");
                                }
                            });
                        }
                    });
                }catch (Exception e){
                    Log.i("LOG_MSG", "ReasonFragment " + e.getMessage());
                }
            }
        });

        getReasonList(sharedPrefManager.getTokenId(), sharedPreferencesMachine.getEquipType(), sharedPreferencesMachine.getName());

        return view;
    }

    private void getReasonList(String tokenId, String EquipmentType, String ErrorCode){
        MachineActionFragment.getReasonSolutionFunc("reason", tokenId, EquipmentType, ErrorCode, new MachineActionFragment.getReasonSolutionCallback() {
            @Override
            public void onSuccess(ReasonSolutionRes res) {
                ReasonContainers.clear();
                List<ReasonSolutionRes.DataItem> data = res.getData();
                for (ReasonSolutionRes.DataItem item : data) {
                    Integer pk = item.getPk();
                    String errorCode = item.getErrorCode();
                    String enDescription = item.getEnDescription();
                    String thDescription = item.getThDescription();
                    String updateEmp = item.getUpdateEmp();
                    String updateDate = item.getUpdateDate();
                    String rating = item.getRating();

                    Log.i("LOG_MSG", "data pk " + pk);
                    ReasonContainers.add(new ReasonSolutionParameter(
                            pk, sharedPreferencesMachine.getEquipType(),errorCode,enDescription,thDescription,updateEmp,updateDate,rating
                            ));
                }
                ReasonAdapter = new ReaSolAdapter(ReasonContainers);
                ReasonAdapter.setOnItemClickListener(new ReaSolAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ReasonSolutionParameter clickedReason = ReasonContainers.get(position);
                        ((MainApp)getContext().getApplicationContext()).setPk_reasonCode(clickedReason.getId());
                        Log.i("LOG_MSG", "onItemClick Reason >> pk " + clickedReason.getId());
                    }
                });
                ReasonAdapter.setOnLongClickListener(new ReaSolAdapter.OnLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        ReasonSolutionParameter clickedReason = ReasonContainers.get(position);

                        Reason_dialog = new Dialog(getContext());
                        Reason_dialog.setContentView(R.layout.dialog_reason_solution_info);
                        Reason_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        Reason_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_background_corner_top));
                        Reason_dialog.getWindow().setGravity(Gravity.BOTTOM);
                        Reason_dialog.setCancelable(false);
                        Reason_dialog.show();

                        TextView txtErrorCode = Reason_dialog.findViewById(R.id.txtErrorCodeReaSol);
                        TextView txtEquipType = Reason_dialog.findViewById(R.id.txtEquipTypeReaSol);
                        TextView txtEmp = Reason_dialog.findViewById(R.id.txtEmpReaSol);
                        TextView txtUpdateDate = Reason_dialog.findViewById(R.id.txtDateReaSol);
                        TextView txtRating = Reason_dialog.findViewById(R.id.txtRatingReaSol);
                        EditText txtDesEN = Reason_dialog.findViewById(R.id.txtEnDesReaSol);
                        EditText txtDesTH = Reason_dialog.findViewById(R.id.txtThDesReaSol);

                        Button btnCancelSolutionInfo = Reason_dialog.findViewById(R.id.btnCancelInfoReaSol);
                        Button btnUpdateSolutionInfo = Reason_dialog.findViewById(R.id.btnUpdateInfoReaSol);

                        try{

                            OffsetDateTime offsetDateTime = OffsetDateTime.parse(clickedReason.getUpdate_date(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            String outputString = offsetDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            txtErrorCode.setText(sharedPreferencesMachine.getName());
                            txtEquipType.setText(sharedPreferencesMachine.getEquipType());
                            txtEmp.setText(clickedReason.getUpdate_emp());
                            txtUpdateDate.setText(outputString);
                            txtRating.setText(clickedReason.getRating());
                            txtDesEN.setText(clickedReason.getEn_description());
                            txtDesTH.setText(clickedReason.getTh_description());

                            btnCancelSolutionInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Reason_dialog.dismiss();
                                }
                            });
                            btnUpdateSolutionInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }
                        catch (Exception e){
                            Log.i("LOG_MSG", "Solution_dialog " + e.getMessage());
                        }
                    }
                });
                reasonView.setLayoutManager(new LinearLayoutManager(getContext()));
                reasonView.setAdapter(ReasonAdapter);

//                Toast.makeText(getContext(), res.getDetail(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void playSuccessAnimation(LottieAnimationView animationView, Dialog addReason_dialog, String jsonFile){
        animationView.setAnimation(jsonFile);
        animationView.setVisibility(View.VISIBLE);
        animationView.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationView.setVisibility(View.INVISIBLE);
                animationView.cancelAnimation();
                addReason_dialog.dismiss();
            }
        }, 2000);
    }
}