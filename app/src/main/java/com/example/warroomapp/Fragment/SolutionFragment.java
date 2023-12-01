package com.example.warroomapp.Fragment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.airbnb.lottie.LottieAnimationView;
import com.example.warroomapp.Activity.Class.ReasonSolutionRes;
import com.example.warroomapp.Activity.HomeActivity;
import com.example.warroomapp.Activity.ReasonSolutionParameter;
import com.example.warroomapp.Adaptor.ReaSolAdapter;
import com.example.warroomapp.GlobalVariable;
import com.example.warroomapp.R;
import com.example.warroomapp.SharedPreferencesMachine;
import com.example.warroomapp.SharedPreferencesManager;
import com.example.warroomapp.SharedPreferencesSetting;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SolutionFragment extends Fragment {
    private static GlobalVariable globalVariable = new GlobalVariable();
    private SharedPreferencesManager sharedPrefManager;
    private SharedPreferencesSetting sharedPrefSetting;
    private SharedPreferencesMachine sharedPreferencesMachine;
    private ArrayList<ReasonSolutionParameter> SolutionContainers = new ArrayList<>();
    private EditText txtSolutionCodeEN;
    private EditText txtSolutionCodeTH;
    private RecyclerView solutionView;
    private ReaSolAdapter SolutionAdapter;
    private Dialog Solution_dialog;
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
        View view = inflater.inflate(R.layout.fragment_solution, container, false);
        Button btnAddSolution = view.findViewById(R.id.btnAddSolution);
        solutionView = view.findViewById(R.id.SolutionContainer);

        btnAddSolution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog addSolution_dialog = new Dialog(getContext());
                addSolution_dialog.setContentView(R.layout.dialog_add_solution);
                addSolution_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                addSolution_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_background_corner_top));
                addSolution_dialog.getWindow().setGravity(Gravity.BOTTOM);
                addSolution_dialog.setCancelable(false);
                addSolution_dialog.show();

                txtSolutionCodeEN = addSolution_dialog.findViewById(R.id.txtSolutionDescriptionEN);
                txtSolutionCodeTH = addSolution_dialog.findViewById(R.id.txtSolutionDescriptionTH);
                try{
                    Button btnCancel = addSolution_dialog.findViewById(R.id.btnCancelNewSolution);
                    Button btnSave = addSolution_dialog.findViewById(R.id.btnSaveNewSolution);

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addSolution_dialog.dismiss();
                        }
                    });
                    btnSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            MachineActionFragment.RequestBodyReasonSolutionCode requestBodyReasonCode = new MachineActionFragment.RequestBodyReasonSolutionCode(
                                    sharedPreferencesMachine.getEquipType(),
                                    sharedPreferencesMachine.getName().toString() ,
                                    txtSolutionCodeEN.getText().toString(),
                                    txtSolutionCodeTH.getText().toString(),
                                    sharedPrefManager.getEmpNo()
                            );
                            LottieAnimationView animationView = addSolution_dialog.findViewById(R.id.status_animation);
                            MachineActionFragment.postReasonSolutionFunc("solution", sharedPrefManager.getTokenId(), requestBodyReasonCode, new MachineActionFragment.PostReasonSolutionCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                                    playSuccessAnimation( animationView,  addSolution_dialog, "success_animation.json");
                                    getSolutionList(sharedPrefManager.getTokenId(), sharedPreferencesMachine.getEquipType(), sharedPreferencesMachine.getName());
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                                    playSuccessAnimation( animationView,  addSolution_dialog, "fail_animation.json");
                                }
                            });
                        }
                    });
                }catch (Exception e){
                    Log.i("LOG_MSG", "SolutionFragment " + e.getMessage());
                }
            }
        });

        getSolutionList(sharedPrefManager.getTokenId(), sharedPreferencesMachine.getEquipType(), sharedPreferencesMachine.getName());

        return view;
    }
    private void getSolutionList(String tokenId, String EquipmentType, String ErrorCode){
        MachineActionFragment.getReasonSolutionFunc("solution", tokenId, EquipmentType, ErrorCode, new MachineActionFragment.getReasonSolutionCallback() {
            @Override
            public void onSuccess(ReasonSolutionRes res) {
                SolutionContainers.clear();
                List<ReasonSolutionRes.DataItem> data = res.getData();
                for (ReasonSolutionRes.DataItem item : data) {
                    int pk = item.getPk();
                    String errorCode = item.getErrorCode();
                    String enDescription = item.getEnDescription();
                    String thDescription = item.getThDescription();
                    String updateEmp = item.getUpdateEmp();
                    String updateDate = item.getUpdateDate();
                    String rating = item.getRating();

                    SolutionContainers.add(new ReasonSolutionParameter(
                            pk, sharedPreferencesMachine.getEquipType(),errorCode,enDescription,thDescription,updateEmp,updateDate,rating
                    ));
                }
                SolutionAdapter = new ReaSolAdapter(SolutionContainers);
                SolutionAdapter.setOnItemClickListener(new ReaSolAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        ReasonSolutionParameter clickedSolution = SolutionContainers.get(position);
//                        Toast.makeText(getContext(), clickedReason.getEn_description(), Toast.LENGTH_SHORT).show();
                    }
                });
                SolutionAdapter.setOnLongClickListener(new ReaSolAdapter.OnLongClickListener() {
                    @Override
                    public void onItemLongClick(int position) {
                        ReasonSolutionParameter clickedSolution = SolutionContainers.get(position);

                        Solution_dialog = new Dialog(getContext());
                        Solution_dialog.setContentView(R.layout.dialog_reason_solution_info);
                        Solution_dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        Solution_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dialog_background_corner_top));
                        Solution_dialog.getWindow().setGravity(Gravity.BOTTOM);
                        Solution_dialog.setCancelable(false);
                        Solution_dialog.show();

                        TextView txtErrorCode = Solution_dialog.findViewById(R.id.txtErrorCodeReaSol);
                        TextView txtEquipType = Solution_dialog.findViewById(R.id.txtEquipTypeReaSol);
                        TextView txtEmp = Solution_dialog.findViewById(R.id.txtEmpReaSol);
                        TextView txtUpdateDate = Solution_dialog.findViewById(R.id.txtDateReaSol);
                        TextView txtRating = Solution_dialog.findViewById(R.id.txtRatingReaSol);
                        EditText txtDesEN = Solution_dialog.findViewById(R.id.txtEnDesReaSol);
                        EditText txtDesTH = Solution_dialog.findViewById(R.id.txtThDesReaSol);

                        Button btnCancelSolutionInfo = Solution_dialog.findViewById(R.id.btnCancelInfoReaSol);
                        Button btnUpdateSolutionInfo = Solution_dialog.findViewById(R.id.btnUpdateInfoReaSol);

                        try{

                            OffsetDateTime offsetDateTime = OffsetDateTime.parse(clickedSolution.getUpdate_date(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);
                            String outputString = offsetDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));

                            txtErrorCode.setText(sharedPreferencesMachine.getName());
                            txtEquipType.setText(sharedPreferencesMachine.getEquipType());
                            txtEmp.setText(clickedSolution.getUpdate_emp());
                            txtUpdateDate.setText(outputString);
                            txtRating.setText(clickedSolution.getRating());
                            txtDesEN.setText(clickedSolution.getEn_description());
                            txtDesTH.setText(clickedSolution.getTh_description());


                            btnCancelSolutionInfo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Solution_dialog.dismiss();
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
                solutionView.setLayoutManager(new LinearLayoutManager(getContext()));
                solutionView.setAdapter(SolutionAdapter);

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