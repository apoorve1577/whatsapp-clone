package edu.uncc.hw08;

import static edu.uncc.hw08.MyChatsFragment.TAG;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.uncc.hw08.Model.MyChats;
import edu.uncc.hw08.databinding.ChatListItemBinding;
import edu.uncc.hw08.databinding.FragmentChatBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "chatUSerId";
    private static final String ARG_PARAM2 = "name";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ChatAdapter chatAdapter ;

    String textChat = "";

    Map<String,Map<String,ArrayList<String>>> chatText = new HashMap<>();

    Map<String, Map<String, ArrayList<String>>> uMap = new HashMap<>();
    Map<String, ArrayList<String>> lMap = new HashMap<>();

    ArrayList<String> chatsToBeAdded = new ArrayList<>();

    ArrayList<MyChats> myChatsArrayList = new ArrayList<>();

    Map<String, Object> updateMap = new HashMap<>();

    int dbCounter =0;

    String userId = "";

    String userIdAdd = "";

    String textRemove = "";

    // TODO: Rename and change types of parameters


    String chatUserId = "";

    String otherUserId = "";

    String name ="";

    public ChatFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String chatUserId, String name) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, chatUserId);
        args.putString(ARG_PARAM2, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatUserId = getArguments().getString(ARG_PARAM1);
            name = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentChatBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Chat" + name);
        int ctr = 1;
        getUserChats();
        userId = mAuth.getUid();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAdapter = new ChatAdapter();
        binding.recyclerView.setAdapter(chatAdapter);

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = binding.editTextMessage.getText().toString();
                String chatMessage = binding.editTextMessage.getText().toString();
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm a");
                String time = sdf1.format(date);
                String dt = sdf.format(date);
                String formattedDate = dt + " at " + time;
                Random random = new Random();
                textChat = userId + "-" + chatMessage + "-" + formattedDate + "-"+random.toString();
                otherUserId = chatUserId;
                userIdAdd = userId;
                updateUserChats();
            }
        });

        binding.buttonDeleteChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCompleteChat(userId,chatUserId);
                deleteCompleteChat(chatUserId,userId);
            }
        });

        binding.buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToMyChats();
            }
        });

    }

    void deleteCompleteChat(String userId, String otherUserId){
        ArrayList<String> updatedList = new ArrayList<>();
        Map<String, ArrayList<String>> lMap = new HashMap<>();
        lMap = chatText.get(userId);
        chatText.remove(userId);
        lMap.remove(otherUserId);
        lMap.remove(otherUserId);
        if(lMap.size() !=0) {
            chatText.put(userId, lMap);
        }
        else{
            chatText.remove(userId);
        }
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("chatText", chatText);
        db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                .update(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        getUserChats();
                    }
                });

    }





    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ForumViewHolder> {
        @NonNull
        @Override
        public ChatAdapter.ForumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ChatListItemBinding binding = ChatListItemBinding.inflate(getLayoutInflater(), parent, false);
            return new ChatAdapter.ForumViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ChatAdapter.ForumViewHolder holder, @SuppressLint("RecyclerView") int position) {
            MyChats myChats = myChatsArrayList.get(position);
            holder.setupUI(myChats);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            holder.mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getItemViewType(position);
                    deleteChat(position, mAuth.getUid(), chatUserId);
                    deleteForChatUser(chatUserId, mAuth.getUid());
                }
            });
        }

        @Override
        public int getItemCount() {
            return myChatsArrayList.size();
        }

        class ForumViewHolder extends RecyclerView.ViewHolder {
            ChatListItemBinding mBinding;

            public ForumViewHolder(ChatListItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(MyChats myChats){

                if(myChats.getUserId().equals(mAuth.getUid())){
                    mBinding.imageViewDelete.setImageResource(R.drawable.ic_delete);
                }
                else{
                    mBinding.imageViewDelete.setImageResource(android.R.color.transparent);
                }

                if(mAuth.getUid().equals(myChats.getUserId())){
                    mBinding.textViewMsgBy.setText("Me");
                }
                else {
                    mBinding.textViewMsgBy.setText(name);
                }
                mBinding.textViewMsgText.setText(myChats.getTextChat());
                mBinding.textViewMsgOn.setText(myChats.getTimeStamp());
            }
        }
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
                            if(entry.getKey().equals(userIdAdd)){
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
                                uMap.put(userIdAdd,lMap);
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
                            uMap.put(userIdAdd, lMap);
                        }
                    }
                    else{
                        chatsToBeAdded.clear();
                        lMap.clear();
                        chatsToBeAdded.add(textChat);
                        lMap.put(otherUserId, chatsToBeAdded);
                        uMap.put(userIdAdd, lMap);
                    }
                    updateMap.put("chatText", uMap);
                    dbCounter++;
                    db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                            .update(updateMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    //   mListener.goToMyChats();
                                    if(dbCounter < 2){
                                        String temp = userIdAdd;
                                        userIdAdd = otherUserId;
                                        otherUserId = temp;
                                        updateUserChats();
                                    }
                                    else{
                                        getUserChats();
                                    }
                                }
                            });

                }
            }
        });
    }


    void deleteForChatUser(String userId, String chatId){
        ArrayList<String> updatedList = new ArrayList<>();
        Map<String, ArrayList<String>> lMap = new HashMap<>();
        lMap = chatText.get(userId);
        chatText.remove(userId);
        updatedList = lMap.get(chatId);
        updatedList.remove(textRemove);
        if(updatedList.size()!=0) {
            lMap.put(chatId, updatedList);
            chatText.put(userId, lMap);
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("chatText", chatText);
            db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                    .update(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getUserChats();
                        }
                    });
        }
        if(updatedList.size()==0){
            lMap.remove(chatId);
            if(lMap.size() !=0) {
                chatText.put(userId, lMap);
            }
            else{
                chatText.remove(userId);
            }
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("chatText", chatText);
            db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                    .update(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getUserChats();
                        }
                    });
        }
    }

    void deleteChat(int position, String userId, String chatId){
        ArrayList<String> updatedList = new ArrayList<>();
        Map<String, ArrayList<String>> lMap = new HashMap<>();
        lMap = chatText.get(userId);
        chatText.remove(userId);
        updatedList = lMap.get(chatId);
        textRemove = updatedList.get(position);
        updatedList.remove(position);
        if(updatedList.size()!=0) {
            lMap.put(chatId, updatedList);
            chatText.put(userId, lMap);
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("chatText", chatText);
            db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                    .update(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getUserChats();
                        }
                    });
        }
        if(updatedList.size()==0){
            lMap.remove(chatId);
            if(lMap.size() !=0) {
                chatText.put(userId, lMap);
            }
            else{
                chatText.remove(userId);
            }
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("chatText", chatText);
            db.collection("Chats").document("UWjrLbJ3oteK7r07uHyj")
                    .update(updateMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            getUserChats();
                        }
                    });
        }
    }


    void getUserChats(){
        db.collection("Chats").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                myChatsArrayList.clear();
                chatText.clear();
                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
                    chatText = (Map<String, Map<String, ArrayList<String>>>) doc.getData().get("chatText");
                    for (Map.Entry<String, Map<String, ArrayList<String>>> chat : chatText.entrySet()){
                        if(chat.getKey().equals(mAuth.getUid())){
                            Map<String, ArrayList<String>> map = chat.getValue();
                                if(map.get(chatUserId) != null){
                                    ArrayList<String> allOtherUserChats = map.get(chatUserId);
                                    for(String chats : allOtherUserChats){
                                        String[] allChatsOfUser = chats.split(",");
                                        for(String oneChat : allChatsOfUser){
                                            MyChats myChats = new MyChats();
                                            String[] chatDetail = oneChat.split("-");
                                            myChats.setUserId(chatDetail[0]);
                                            myChats.setTextChat(chatDetail[1]);
                                            if(chatDetail[2]!=null) {
                                                myChats.setTimeStamp(chatDetail[2]);
                                            }
                                            else {
                                                Date date = new Date();
                                                myChats.setTimeStamp(date.toString());
                                            }
                                            myChatsArrayList.add(myChats);
                                        }
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                chatAdapter.notifyDataSetChanged();
                                            }
                                        });
                                    }
                                    break;
                                }

                        }
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });

    }

    ChatListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ChatListener){
            mListener = (ChatListener) context;
        }
    }

    public interface ChatListener{

        void goToMyChats();
    }

}