package dev.kenji.peenoygame;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

//import com.facebook.FacebookSdk;
//import com.facebook.LoggingBehavior;
//import com.facebook.appevents.AppEventsLogger;

import java.util.Objects;


public class MainActivity extends AppCompatActivity{

    private  static final int comboNumber = 7;
    private static final int coef = 72;
    private static final int coefW = 142;
    private static final int coefE = 212;
    private int position1 = 5;
    private int position2 = 5;
    private int position3 = 5;
    private final int[] slot = {1, 2, 3, 4, 5, 6, 7};

    private RecyclerView rv1;
    private RecyclerView rv2;
    private RecyclerView rv3;
    private customManager layoutManager1;
    private customManager layoutManager2;
    private customManager layoutManager3;


    private TextView energyBallPrice;
    private TextView myPower;
    private TextView bet;

    int myCoins_val;
    int bet_val;
    int jackpot_val;

    private boolean firstRun;

    private Mechanics gameLogic;

    private SharedPreferences pref;
    public MediaPlayer mp;
    public MediaPlayer win;
    public MediaPlayer bgsound;
    public static final String PREFS_NAME = "FirstRun";


    private int playmusic;
    private int playsound;
    private ImageView music_off;
    private ImageView music_on;
    private ImageView soundon;
    private ImageView soundoff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageButton minusButton;
        ImageButton plusButton;
        SpinnerAdapter adapter;
        ImageView settingsButton;
        ImageButton spinButton;

        //FacebookSdk.fullyInitialize();
       // AppEventsLogger.activateApp(this.getApplication());
       // FacebookSdk.setIsDebugEnabled(true);
       // FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main2);

        bgsound = MediaPlayer.create(this,R.raw.bg_music);
        bgsound.setLooping(true);
        mp = MediaPlayer.create(this, R.raw.splashh);
        win = MediaPlayer.create(this, R.raw.win);

        pref = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = pref.getBoolean("firstRun", true);

        if (firstRun) {
            playmusic = 1;
            playsound = 1;
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            playmusic= pref.getInt("music", 1);
            playsound = pref.getInt("sound", 1);
            checkmusic();

        }

        Log.d("MUSIC",String.valueOf(playmusic));

        //Initializations
        gameLogic = new Mechanics();
        settingsButton = findViewById(R.id.settings);
        spinButton = findViewById(R.id.spinButton);
        plusButton = findViewById(R.id.plusButton);
        minusButton = findViewById(R.id.minusButton);
        energyBallPrice = findViewById(R.id.energyBall);
        myPower = findViewById(R.id.energy);
        bet = findViewById(R.id.bet);
        adapter = new SpinnerAdapter();

        //RecyclerView settings
        rv1 = findViewById(R.id.spinner1);
        rv2 = findViewById(R.id.spinner2);
        rv3 = findViewById(R.id.spinner3);
        rv1.setHasFixedSize(true);
        rv2.setHasFixedSize(true);
        rv3.setHasFixedSize(true);

        layoutManager1 = new customManager(this);
        layoutManager1.setScrollEnabled(false);
        rv1.setLayoutManager(layoutManager1);
        layoutManager2 = new customManager(this);
        layoutManager2.setScrollEnabled(false);
        rv2.setLayoutManager(layoutManager2);
        layoutManager3 = new customManager(this);
        layoutManager3.setScrollEnabled(false);
        rv3.setLayoutManager(layoutManager3);

        rv1.setAdapter(adapter);
        rv2.setAdapter(adapter);
        rv3.setAdapter(adapter);
        rv1.scrollToPosition(position1);
        rv2.scrollToPosition(position2);
        rv3.scrollToPosition(position3);

        setText();
        updateText();

        //RecyclerView listeners
        rv1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv1.scrollToPosition(gameLogic.getPosition(0));
                    layoutManager1.setScrollEnabled(false);
                }
            }
        });

        rv2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv2.scrollToPosition(gameLogic.getPosition(1));
                    layoutManager2.setScrollEnabled(false);
                }
            }
        });
        rv3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    rv3.scrollToPosition(gameLogic.getPosition(2));
                    layoutManager3.setScrollEnabled(false);
                    updateText();
                    if (gameLogic.getHasWon()) {
                        if (playsound == 1) {
                            win.start();
                        }
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.win_splash,findViewById(R.id.win_splash));
                        TextView winCoins = layout.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());
                        Toast toast = new Toast(MainActivity.this);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(layout);
                        toast.show();
                        gameLogic.setHasWon(false);

                    }
                }
            }
        });

        //Button listeners
        spinButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            layoutManager1.setScrollEnabled(true);
            layoutManager2.setScrollEnabled(true);
            layoutManager3.setScrollEnabled(true);
            gameLogic.getSpinResults();
            position1 = gameLogic.getPosition(0) + coef;
            position2 = gameLogic.getPosition(1) + coefW;
            position3 = gameLogic.getPosition(2) + coefE;
            rv1.smoothScrollToPosition(position1);
            rv2.smoothScrollToPosition(position2);
            rv3.smoothScrollToPosition(position3);
        });

        plusButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            gameLogic.betUp();
            updateText();
        });

        minusButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            gameLogic.betDown();
            updateText();
        });

        settingsButton.setOnClickListener(v -> {
            if(playsound == 1){
                mp.start();
            }
            showSettingsDialog();
        });

    }


    private void setText(){
        if(firstRun){
            gameLogic.setMyCoins(1000);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();

        }else {
            String coins = pref.getString("coins","");
            String bet = pref.getString("bet","");
            String jackpot = pref.getString("jackpot","");
            Log.d("COINS",coins);
            myCoins_val = Integer.parseInt(coins);
            bet_val = Integer.parseInt(bet);
            jackpot_val = Integer.parseInt(jackpot);
            gameLogic.setMyCoins(myCoins_val);
            gameLogic.setBet(bet_val);
            gameLogic.setJackpot(jackpot_val);
        }
    }

    private void updateText() {
        energyBallPrice.setText(gameLogic.getJackpot());
        myPower.setText(gameLogic.getMyCoins());
        bet.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = pref.edit();
        editor.putString("coins",gameLogic.getMyCoins());
        editor.putString("bet",gameLogic.getBet());
        editor.putString("jackpot",gameLogic.getJackpot());
        editor.apply();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {

        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.spinner_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
            View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % comboNumber;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.combo1);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.combo2);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.combo3);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.combo4);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.combo5);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.combo6);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.combo7);
                    break;
            }

        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }

    private void showSettingsDialog() {
        final Dialog dialog;

        dialog = new Dialog(this, R.style.WinDialog);
        Objects.requireNonNull(dialog.getWindow()).setContentView(R.layout.settings);

        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss()); // Close the dialog when the close button is clicked

        music_on = dialog.findViewById(R.id.music_on);
        music_on.setOnClickListener(v -> {
            playmusic = 0;
            checkmusic();
            music_on.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("music", playmusic);
            editor.apply();
        });

        music_off =  dialog.findViewById(R.id.music_off);
        music_off.setOnClickListener(v -> {
            playmusic = 1;
            bgsound.start();
            recreate();
            dialog.show();
            music_on.setVisibility(View.VISIBLE);
            music_off.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("music", playmusic);
            editor.apply();
        });

        soundon = dialog.findViewById(R.id.sounds_on);
        soundon.setOnClickListener(v -> {
            playsound = 0;
            soundon.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("sound", playsound);
            editor.apply();
        });

        soundoff = dialog.findViewById(R.id.sounds_off);
        soundoff.setOnClickListener(v -> {
            playsound = 1;
            recreate();
            dialog.show();
            soundon.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("sound", playsound);
            editor.apply();
        });

        checkmusicdraw();
        checksounddraw();

        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        bgsound.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkmusic();
    }

    private void checkmusic(){
        if (playmusic == 1){
            bgsound.start();
        }
        else {
            bgsound.pause();
        }
    }

    private void checkmusicdraw(){
        if (playmusic == 1){
            music_on.setVisibility(View.VISIBLE);
            music_off.setVisibility(View.INVISIBLE);
        }
        else {
            music_on.setVisibility(View.INVISIBLE);
            music_off.setVisibility(View.VISIBLE);
        }
    }

    private void checksounddraw(){
        if (playsound == 1){
            soundon.setVisibility(View.VISIBLE);
            soundoff.setVisibility(View.INVISIBLE);
        }
        else {
            soundon.setVisibility(View.INVISIBLE);
            soundoff.setVisibility(View.VISIBLE);
        }
    }


}

