package edu.uncc.hw08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        MyChatsFragment.MyChatsListener, SignUpFragment.SignUpListener, CreateChatFragment.CreateChatInterface
, ChatFragment.ChatListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.rootView, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }


    @Override
    public void gotoMyChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new MyChatsFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void gotoLogin() {

    }


    @Override
    public void gotoSignUp() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new SignUpFragment())
                .commit();
    }

    @Override
    public void newChat() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new CreateChatFragment())
                .commit();
    }

    @Override
    public void chat(String chatUserId, String name) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, ChatFragment.newInstance(chatUserId, name))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void logout() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new LoginFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goToMyChats() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.rootView, new MyChatsFragment())
                .addToBackStack(null)
                .commit();
    }
}