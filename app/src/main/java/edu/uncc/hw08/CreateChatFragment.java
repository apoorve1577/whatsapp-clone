package edu.uncc.hw08;

import static edu.uncc.hw08.MyChatsFragment.TAG;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.uncc.hw08.Model.MyChats;
import edu.uncc.hw08.databinding.FragmentCreateChatBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateChatFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    Map<String, Object> updateMap = new HashMap<>();

    int dbCounter =0;



    String userId = "";
    String otherUserId = "";
    String textChat = "";

    ArrayList< Map<String,Object> > result = new ArrayList<>();

    ArrayList<MyChats> myChatsArrayList = new ArrayList<>();


    FragmentCreateChatBinding binding;

    FirebaseUser firebaseUser;

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<String> userList = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();

   Map<String,Map<String,ArrayList<String>>> chatText = new HashMap<>();


    ArrayList<Map<String,Map<String,ArrayList<String>>>> chatsTobeUpdated = new ArrayList<>();
    Map<String, Map<String, ArrayList<String>>> uMap = new HashMap<>();
    Map<String, ArrayList<String>> lMap = new HashMap<>();

    ArrayList<String> chatsToBeAdded = new ArrayList<>();


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CreateChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CreateChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CreateChatFragment newInstance(String param1, String param2) {
        CreateChatFragment fragment = new CreateChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCreateChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("New Chat");
        firebaseUser = mAuth.getCurrentUser();
        getUserList();
        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();
        listView = binding.listView;
        adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1,android.R.id.text1, userList);
        listView.setAdapter(adapter);

       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               Log.d(TAG, "onItemClick: listView" + i);
               otherUserId = userIdList.get(i);
               binding.textViewSelectedUser.setText(userList.get(i));
               Log.d(TAG, "onItemClick: listView" + userId + " " + otherUserId);
           }
       });

       binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               mListener.goToMyChats();
           }
       });

       binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if (!(otherUserId.length() ==0)) {
                   String chatMessage = binding.editTextMessage.getText().toString();
                   Date date = new Date();
                   SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                   SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm a");
                   String time = sdf1.format(date);
                   String dt = sdf.format(date);
                   String formattedDate = dt + " at " + time;
                   Random random = new Random();
                   textChat = userId + "-" + chatMessage + "-" + formattedDate + "-"+random.toString();
                   updateUserChats();
               }
               else{
                   Toast.makeText(getActivity(), "Select a user to chat with !", Toast.LENGTH_SHORT).show();
               }
           }
       });
    }





    void updateUserChats(){
        db.collection("Chats").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                myChatsArrayList.clear();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                    if (doc.getData().get("chatText") != null){
                        chatText = (Map<String, Map<String, ArrayList<String>>>) doc.getData().get("chatText");
                        Boolean ifUserIdFound = false;
                         for (Map.Entry<String, Map<String, ArrayList<String>>> entry : chatText.entrySet()){
                            if(entry.getKey().equals(userId)){
                                ifUserIdFound = true;
                             lMap =  entry.getValue();
                             if(lMap.get(otherUserId)!=null){
                                 ArrayList<String> lMapValue = lMap.get(otherUserId);
                                 lMapValue.add(textChat);
                                 lMap.put(otherUserId, lMapValue);
                             }
                             else{
                                 ArrayList<String> lMapValue = new ArrayList<>();
                                 lMapValue.add(textChat);
                                 lMap.put(otherUserId, lMapValue);
                             }
                             uMap.put(userId,lMap);
                            }
                            else {
                                uMap.put(entry.getKey(), entry.getValue());
                            }
                        }
                        if(!ifUserIdFound){
                            chatsToBeAdded.clear();
                            lMap.clear();
                            chatsToBeAdded.add(textChat);
                            lMap.put(otherUserId,chatsToBeAdded);
                            uMap.put(userId, lMap);
                        }
                }
                    else{
                        chatsToBeAdded.clear();
                        lMap.clear();
                       chatsToBeAdded.add(textChat);
                       lMap.put(otherUserId, chatsToBeAdded);
                       uMap.put(userId, lMap);
                    }
                    updateMap.put("chatText", uMap);
                    dbCounter++;
                    db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                            .update(updateMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                 //   mListener.goToMyChats();
                                    if(dbCounter >= 2){
                                        mListener.goToMyChats();
                                    }
                                    else{
                                        String temp = userId;
                                        userId = otherUserId;
                                        otherUserId = temp;
                                        updateUserChats();
                                    }

                                }
                            });

                }
            }
        });
    }



    void getUserList(){

        db.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userList.clear();
                userIdList.clear();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                 Map<String,Object> map = doc.getData();
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        if(!entry.getKey().equals(mAuth.getUid())) {
                            userList.add(entry.getValue().toString());
                            userIdList.add(entry.getKey().toString());
                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "run: " + userList.toString());
                        }
                    });
                }
            }
        });
    }

    CreateChatInterface mListener ;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof CreateChatInterface){
            mListener = (CreateChatInterface) context;
        }
    }

    public interface CreateChatInterface{
        void goToMyChats();
    }
}