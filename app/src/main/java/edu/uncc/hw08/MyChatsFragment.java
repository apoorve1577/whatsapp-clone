package edu.uncc.hw08;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uncc.hw08.Model.MyChats;
import edu.uncc.hw08.databinding.FragmentMyChatsBinding;
import edu.uncc.hw08.databinding.MyChatsListItemBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyChatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyChatsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ArrayList<MyChats> myChatsArrayList = new ArrayList<>();

    Map<String,Map<String,ArrayList<String>>> chatText = new HashMap<>();

    MyChatsAdapter myChatsAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyChatsFragment() {
        // Required empty public constructor
    }

    public static String TAG = "demo";

    FragmentMyChatsBinding binding;




    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyChatsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyChatsFragment newInstance(String param1, String param2) {
        MyChatsFragment fragment = new MyChatsFragment();
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
        binding = FragmentMyChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("My Chats");
        mAuth = FirebaseAuth.getInstance();
        getChats();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        myChatsAdapter = new MyChatsAdapter();
        binding.recyclerView.setAdapter(myChatsAdapter);

        binding.buttonNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.newChat();
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                mListener.logout();
            }
        });
    }

    class MyChatsAdapter extends RecyclerView.Adapter<MyChatsAdapter.ForumViewHolder> {
        @NonNull
        @Override
        public ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            MyChatsListItemBinding binding = MyChatsListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ForumViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ForumViewHolder holder, @SuppressLint("RecyclerView") int position) {
            MyChats myChats = myChatsArrayList.get(position);
            holder.setupUI(myChats);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.chat(myChats.getUserId(), myChats.getName());
                    Log.d(TAG, "onClick: myChats id" + myChats.getUserId());
                }
            });


        }

        @Override
        public int getItemCount() {
            return myChatsArrayList.size();
        }

        class ForumViewHolder extends RecyclerView.ViewHolder {
            MyChatsListItemBinding mBinding;

            public ForumViewHolder(MyChatsListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(MyChats myChats){

                mBinding.textViewMsgBy.setText(myChats.getName());
                mBinding.textViewMsgOn.setText(myChats.getTimeStamp());
                mBinding.textViewMsgText.setText(myChats.getTextChat());
            }
        }
    }

    void getChats(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Chats").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                myChatsArrayList.clear();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
if(doc.getData().get("chatText")!=null){
                    chatText = (Map<String, Map<String, ArrayList<String>>>) doc.getData().get("chatText");
                     for (Map.Entry<String, Map<String, ArrayList<String>>> chat : chatText.entrySet()) {
                        if (chat.getKey().equals(mAuth.getUid())) {
                           // ArrayList<Map<String, ArrayList<String>>> inChats = (ArrayList<Map<String, ArrayList<String>>>) chat.get(mAuth.getUid());
                                Map<String, ArrayList<String>> map =chat.getValue();
                                for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();
                                    String text = value.toString();
                                    String[] allChatsOfUser = text.split(",");
                                    int chatSize = allChatsOfUser.length;
                                    chatSize--;
                                    String[] firstChats = allChatsOfUser[chatSize].split("-");
                                    MyChats chats = new MyChats();
                                    db.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                Map<String, Object> userMap = documentSnapshot.getData();
                                                chats.setName((String) userMap.get(key));
                                                chats.setTextChat(firstChats[1]);
                                                chats.setTimeStamp(firstChats[2]);
                                                chats.setUserId(key);
                                                myChatsArrayList.add(chats);
                                            }
                                            getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    myChatsAdapter.notifyDataSetChanged();
                                                }
                                            });
                                        }
                                    });

                                }


                        }
                    }
                }
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        myChatsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    MyChatsListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof MyChatsListener)
        mListener = (MyChatsListener) context;
    }

    interface MyChatsListener {
        void newChat();
        void chat(String chatUserId, String name);
        void logout();
    }

}