package com.android.mungmung_dictionary.ui.Dog_Food_Judgment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.mungmung_dictionary.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

public class Dog_Food_Negative_Effect_List extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    RecyclerView Norecview;
    myadapter2 adpter;
    ExpandableListView listMain;

    public Dog_Food_Negative_Effect_List(){

    }

    public static Dog_Food_Negative_Effect_List newInstance(String param1, String param2){
        Dog_Food_Negative_Effect_List fragment = new Dog_Food_Negative_Effect_List();
        Bundle args = new Bundle ();
        args.putString ( ARG_PARAM1, param1);
        args.putString ( ARG_PARAM2, param2 );
        fragment.setArguments ( args );
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        if ( getArguments() != null ){
            mParam1 = getArguments ().getString ( ARG_PARAM1 );
            mParam2 = getArguments ().getString ( ARG_PARAM2 );
        }
    }


    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater , @Nullable @org.jetbrains.annotations.Nullable ViewGroup container , @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.ui_dog_food_judgment_dog_food_negative_effect_list, container, false );

        Norecview = (RecyclerView)view.findViewById ( R.id.Norecview );
        Norecview.setLayoutManager ( new LinearLayoutManager ( getContext () ) );

        FirebaseRecyclerOptions<model2> options =
                new FirebaseRecyclerOptions.Builder<model2>()
                        .setQuery( FirebaseDatabase.getInstance ().getReference ().child ( "BadDictionary" ) , model2.class)
                        .build();

        adpter = new myadapter2 ( options );
        Norecview.setAdapter ( adpter );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart ();
        adpter.startListening ();
    }

    @Override
    public void onStop() {
        super.onStop ();
        adpter.stopListening ();
    }
}
