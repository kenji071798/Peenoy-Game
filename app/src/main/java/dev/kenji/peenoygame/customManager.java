package dev.kenji.peenoygame;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

public class customManager extends LinearLayoutManager {

    private boolean isScrollEnabled = true;

    public customManager(Context context) {
        super(context);
    }

    public void setScrollEnabled(boolean flag) {
        isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        return isScrollEnabled && super.canScrollVertically();
    }
}
