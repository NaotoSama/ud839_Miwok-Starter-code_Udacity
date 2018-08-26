package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {

    /**
     * Create a global MediaPlayer variable to be called below.
     * Handles playback of all the sound files
     */
    private MediaPlayer mMediaPlayer;


    /**
     * Create a global AudioManager variable to be called below.
     * Handles audio focus when playing a sound file
     */
    private AudioManager mAudioManager;


    /**
     * Create an OnCompletionListener variable
     * This listener gets triggered when the {@link MediaPlayer} has completed playing the audio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
            }
    };


    /**
     * Create OnAudioFocusChangeListener via android's built-in AudioManager class
     * This listener gets triggered whenever the audio focus changes (i.e., we gain or lose audio focus because of another app or device).
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (mMediaPlayer == null)                                                                //一定要加這一行語法不然跳轉到其他頁面播放音檔時會閃退!
                return;                                                                              //音檔播放完時，上面的OnCompletionListener會釋放播放器。這時若跳轉到其他頁面，onAudioFocusChange會打算把播放器暫停pause()然後回梭到播放起始點seekTo(0)，但在此之前播放器早被釋放了，所以會找不到播放器而報錯閃退。
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||                       //  「||」的意思是「或」
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means that we've lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but at a lower volume. We'll treat
                // both cases the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {                            // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();                                                                //Resume Playback.
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {                            // The AUDIOFOCUS_LOSS case means we've lost audio focus and
                releaseMediaPlayer();                                                                // Stop playback and clean up resources
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list_view);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);  //Initialize the AudioManager to request audio focus 当我们希望获取到系统服务时，可以调用Context的getSystemService方法


        /**Best method for creating a list of words
             *Create a ArrayList of words
             *ArrayList 好比材料，有各自對應的模具
             */
        final ArrayList<Word> words = new ArrayList<Word> ();
        words.add(new Word("one 一","lutti", R.drawable.number_one, R.raw.number_one));     // 語法精簡自 Word w = new Word("one","lutti"); words.add(w);
        words.add(new Word("two 二", "otiiko", R.drawable.number_two, R.raw.number_two));
        words.add(new Word("three 三", "tolookosu", R.drawable.number_three, R.raw.number_three));
        words.add(new Word("four 四", "oyyisa", R.drawable.number_four, R.raw.number_four));
        words.add(new Word("five 五", "massokka", R.drawable.number_five, R.raw.number_five));
        words.add(new Word("six 六", "temmokka", R.drawable.number_six, R.raw.number_six));
        words.add(new Word("seven 七", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("eight 八", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("nine 九", "wo’e", R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("ten 十", "na’aacha", R.drawable.number_ten, R.raw.number_ten));



        /**Best method using an Array Adapter to couple with the ListView in xml for view recycling and reduced memory usage
             *Create an {@link ArrayAdapter}, whose data source is a list of Strings. The adapter knows how to create layouts for each item in the list,
             *using the simple_list_item_1.xml layout resource defined in the Android framework. This list item layout contains a single {@link TextView},
             * which the adapter will set to display a single word. ("this" means the NumbersActivity. "simple_list_item_1" means android's pre-defined default layout file to show ListView. "words" is the ArrayList.)
             */
        WordAdapter adapter = new WordAdapter(this, words, R.color.category_numbers);

        /** Find the {@link ListView} object in the view hierarchy of the {@link Activity}. There should be a {@link ListView} with the view ID called list,
            * which is declared in the word_ListViewView.xml file.
            */
        ListView listView = (ListView) findViewById(R.id.word_list_view);

        /** Make the {@link ListView} use the {@link ArrayAdapter} we created above, so that the {@link ListView} will display list items for each word in the list of words.
             *Do this by calling the setAdapter method on the {@link ListView} object and pass in 1 argument, which is the {@link ArrayAdapter} with the variable name itemsAdapter.
             **/
        listView.setAdapter(adapter);


        /**
             * Set an OnItemClickListener onto the list view.
             **/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {                                            //setOnItemClickListener is under the AdapterView Class.
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(NumbersActivity.this, "Pronouncing 發音中", Toast.LENGTH_SHORT).show(); //Show a toast message when the word_list_view is tapped.

                Word word =words.get(position);                                                                            // Get the {@link Word} object at the given position the user clicked on
                //"words" 是指 ArrayList。叫安卓去抓此ArrayList中各個位置(position)上的素材，並將素材置入"word" variable中。"word" variable是屬於Word類別(class)

                releaseMediaPlayer();                                                                                      // Release the media player if it currently exists because we are about to play a different sound file


                /**在手指按到物件的時候就要 request audio focus。
                             *在系統抓取到每個word的位置MediaPlayer準備播放不同的音檔時 request audio focus
                             * 設置一個integer變數叫 result並對AudioManager要求AudioFocus，若result等於取得了AudioFocus，則創建並播放MediaPlayer，並對MediaPlayer設置OnCompletionListener，其內容已在最上方的CompletionListener變數中設定過
                            * 屬性之所以會是int是因為所要求的AudioFocus值都是字母全大寫的常數(例如AUDIOFOCUS_GAIN_TRANSIENT)，常數也是一種integer
                             **/
                // Request audio focus for playback
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,      //Pass in the AudioFocusChangeListener
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,                                         //Pass in the audio stream type
                        // Request short-period focus.
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);                         //Pass in audio focus type

                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {                                                // 若取得AudioFocus後，可創建MediaPlayer、開始播放並設置OnCompletionListener。We have audio focus now (granted). Start playback through the following code.
                    mMediaPlayer = MediaPlayer.create(NumbersActivity.this, word.getmAudioResourceId());               // Create and setup the {@link MediaPlayer} for the audio resource associated with the current word
                    mMediaPlayer.start();                                                                                      // Start the audio file
                    //  叫MediaPlayer透過"word" variable去抓聲音檔( word.getmAudioResourceId() )，放在NumbersActivity裡
                    //  最後叫MediaPlayer播放聲音檔


                    // Setup the OnCompletionListener on the media player, so that we can stop and release the media player once the sound has finished playing.
                    // Pass in the global variable mCompletionListener
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });

    }


    /**
     * When the activity is stopped, release the media player resources because we won't be playing any more sounds.
     * 讓App在使用者跳離畫面時釋放播放器，不繼續播放音檔
     */
    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }


    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            // Regardless of whether or not we were granted audio focus, abandon it. This also
            // unregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            // 確定不再播放音檔時，或是用戶關閉App時abandon AudioFocus
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

}



        // [Another alternative method for creating a list of words but less suited for this application]
        // words.add("one");
        // words.add("two");
        // words.add("three");
        // words.add("four");
        // words.add("five");
        // words.add("six");
        // words.add("seven");
        // words.add("eight");
        // words.add("nine");
        // words.add("ten");


        // [Alternative method for creating a list of words but less suited for this application]
        // Create an Array of words
        //    String[] words = new String[10];
        //    words[0] = "one";
        //    words[1] = "two";
        //    words[2] = "three";
        //    words[3] = "four";
        //    words[4] = "five";
        //    words[5] = "six";
        //    words[6] = "seven";
        //    words[7] = "eight";
        //    words[8] = "nine";
        //    words[9] = "ten";



        // // // [Better and compact method using For Loop]
        // Find the root view so we can add child views to it                                                        ===> LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        //  Set up counter variable, condition, and update counter variable in one place via a For Loop              ===> for (int index =0; index < words.size(); index++) {
        //  Create a new TextView called "wordView"                                                                  ===> TextView wordView = new TextView (this);
        // Set the text to be word at the current index                                                              ===> wordView.setText(words.get(index));
        // Add this TextView as another child to the root view of this layout                                        ===> rootView.addView(wordView); }



        // // // [Less ideal method using While Loop]
        // Find the root view so we can add child views to it                                                        ===> LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        // Create a variable to keep track of the current index position                                             ===> int index = 0;
        // Keep looping until we've reached the end of the ArrayList
        // (which means keep looping as long as the current index position is less than the length of the ArrayList) ===> while (index < words.size()) {
        // Create a new TextView called "wordView"                                                                   ===> TextView wordView = new TextView (this);
        // Set the text to be word at the current index                                                              ===> wordView.setText(words.get(index));
        // Add this TextView as another child to the root view of this layout                                        ===> rootView.addView(wordView);
        // Increment the index variable by 1                                                                         ===> index++; // ++ means index = index + 1
        // Repeat till the last index of the ArrayList                                                               ===> }



        // // // [The cumbersome method]
        // Find the parent view (LinearLayout with id called "rootView") of Numbers Activity XML.                    ===> LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        // Create a TextView object called "wordView" using the constructor                                          ===> TextView wordView = new TextView(this);
        // Set the text of the "wordView" TextView to be the 1st index of the ArrayList                              ===> wordView.setText(words.get(0));
        // Make "wordView" TextView the child view of "rootView"LinearLayout, by adding wordView into rootView       ===> rootView.addView(wordView);
        // Create a TextView object called "wordView2" using the constructor                                         ===> TextView wordView2 = new TextView(this);
        // Set the text of the "wordView2" TextView to be the 2nd index of the ArrayList                             ===> wordView2.setText(words.get(1));
        // Make "wordView2" TextView the child view of "rootView"LinearLayout, by adding wordView2 into rootView     ===> rootView.addView(wordView2);
        // Repeat till the last index of the ArrayList



        // // // [Less ideal way using the if/else statement to make MediaPlayer play the audio files]
        // listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        //     @override
        //     public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //             if (position == 0) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_one);
        //             } else if (position == 1) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_two);
        //             } else if (position == 2) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_three);
        //             } else if (position == 3) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_four);
        //             } else if (position == 4) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_five);
        //             } else if (position == 5) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_six);
        //             } else if (position == 6) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_seven);
        //             } else if (position == 7) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_eight);
        //             } else if (position == 8) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_nine);
        //             } else if (position == 9) {
        //             mMediaPlayer = MediaPlayer.create(NumbersActivity.this, R.raw.number_ten);
        //             }
        //             mMediaPlayer.start();
        //             }
        // });