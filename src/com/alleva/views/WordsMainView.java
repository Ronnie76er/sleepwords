package com.alleva.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.alleva.R;

/**
 * User: ronnie
 * Date: 6/19/11
 */
public class WordsMainView extends RelativeLayout {

    public WordsMainView(Context context) {
        super(context);
        setupText();
    }

    private void setupText() {

        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TextView textView = (TextView)inflater.inflate(R.layout.temp, null);


        textView.setText(R.string.test);

        addView(textView);
    }

    public WordsMainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupText();
    }

    public WordsMainView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupText();
    }
}
