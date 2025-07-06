package com.example.tuibikho;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.VisibleForTesting;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

public class GridSpacing extends RecyclerView.ItemDecoration {
    private final int spacing;
    private final int countSpan;
    private final boolean includeEdge;

    public GridSpacing(int spacing, int countSpan, boolean includeEdge) {
        this.spacing = spacing;
        this.countSpan = countSpan;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); //vi tri
        int column = position % countSpan; //cot cua item

        if(includeEdge){
            outRect.left = spacing - column * spacing / countSpan; //khoang cach ben trai
            outRect.right = (column + 1) * spacing / countSpan;//khoang cach ben phai

            if(position < countSpan){
                outRect.top = spacing;
            }
            outRect.bottom = spacing;
        }
        else{
            outRect.left = column * spacing / countSpan;
            outRect.right = spacing - (column + 1) * spacing / countSpan;
            if(position >= countSpan) {
                outRect.top = spacing;
            }
        }
    }
}
