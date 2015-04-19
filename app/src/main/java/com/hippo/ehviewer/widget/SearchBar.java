/*
 * Copyright (C) 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.ehviewer.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.hippo.drawable.AddDeleteDrawable;
import com.hippo.drawable.DrawerArrowDrawable;
import com.hippo.ehviewer.R;
import com.hippo.util.UiUtils;
import com.hippo.util.ViewUtils;
import com.hippo.widget.SimpleImageView;

public class SearchBar extends CardView implements View.OnClickListener,
        TextView.OnEditorActionListener {

    private static final long ANIMATION_TIME = 300;

    private SimpleImageView mMenuButton;
    private TextView mLogoTextView;
    private SimpleImageView mActionButton;
    private SearchEditText mEditText;

    private DrawerArrowDrawable mDrawerArrowDrawable;
    private AddDeleteDrawable mAddDeleteDrawable;

    private Helper mHelper;

    private boolean mInEditMode = false;

    public SearchBar(Context context) {
        super(context);
        init(context);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setRadius(UiUtils.dp2pix(context, 2));
        setCardElevation(UiUtils.dp2pix(context, 2));
        setOnClickListener(this);

        LayoutInflater.from(context).inflate(R.layout.widget_search_bar, this);
        mMenuButton = (SimpleImageView) findViewById(R.id.menu);
        mLogoTextView = (TextView) findViewById(R.id.logo);
        mActionButton = (SimpleImageView) findViewById(R.id.action);
        mEditText = (SearchEditText) findViewById(R.id.search_edit_text);

        mDrawerArrowDrawable = new DrawerArrowDrawable(getContext());
        mAddDeleteDrawable = new AddDeleteDrawable(getContext());

        mMenuButton.setDrawable(mDrawerArrowDrawable);
        mMenuButton.setOnClickListener(this);
        mLogoTextView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Slabo.ttf"));
        mActionButton.setDrawable(mAddDeleteDrawable);
        mActionButton.setOnClickListener(this);
        mEditText.setSearchBar(this);
        mEditText.setOnEditorActionListener(this);
    }

    public void setHelper(Helper helper) {
        mHelper = helper;
    }

    @Override
    public void onClick(View v) {
        if (v == this) {
            setInEditMode();
        } else if (v == mMenuButton) {
            if (mInEditMode) {
                setInNormalMode();
            } else {
                mHelper.onClickMenu();
            }
        } else if (v == mActionButton) {
            if (mInEditMode) {
                // TODO when set prefix
                mEditText.setText("");
            } else {
                mHelper.onClickAction();
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == mEditText) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {
                String query = mEditText.getText().toString();
                if (!TextUtils.isEmpty(query)) {
                    mHelper.onApplySearch(query);
                }
                return true;
            }
        }
        return false;
    }

    public void setInEditMode() {
        if (!mInEditMode) {
            mInEditMode = true;
            setClickable(false);
            ViewUtils.setVisibility(mLogoTextView, View.GONE);
            ViewUtils.setVisibility(mEditText, View.VISIBLE);
            mEditText.requestFocus();
            // show ime
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            // start animator
            ObjectAnimator oa1 = ObjectAnimator.ofFloat(mDrawerArrowDrawable, "progress", 0f, 1f);
            oa1.setDuration(ANIMATION_TIME);
            ObjectAnimator oa2 = ObjectAnimator.ofFloat(mAddDeleteDrawable, "progress", 0f, 1f);
            oa2.setDuration(ANIMATION_TIME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                oa1.setAutoCancel(true);
                oa2.setAutoCancel(true);
            }
            oa1.start();
            oa2.start();
        }
    }

    public void setInNormalMode() {
        if (mInEditMode) {
            mInEditMode = false;
            setOnClickListener(this);
            ViewUtils.setVisibility(mLogoTextView, View.VISIBLE);
            ViewUtils.setVisibility(mEditText, View.GONE);
            // hide ime
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);
            // start animator
            ObjectAnimator oa1 = ObjectAnimator.ofFloat(mDrawerArrowDrawable, "progress", 1f, 0f);
            oa1.setDuration(ANIMATION_TIME);
            ObjectAnimator oa2 = ObjectAnimator.ofFloat(mAddDeleteDrawable, "progress", 1f, 0f);
            oa2.setDuration(ANIMATION_TIME);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                oa1.setAutoCancel(true);
                oa2.setAutoCancel(true);
            }
            oa1.start();
            oa2.start();
        }
    }

    public interface Helper {
        void onClickMenu();
        void onClickAction();
        void onApplySearch(String query);
    }
}