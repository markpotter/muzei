/*
 * Copyright 2014 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.apps.muzei.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import net.nurik.roman.muzei.R;

public class AnimatedMuzeiLogoFragment extends Fragment {
    private Runnable mOnFillStartedCallback;
    private View mSubtitleView;
    private AnimatedMuzeiLogoView mLogoView;
    private float mInitialLogoOffset;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInitialLogoOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16,
                getResources().getDisplayMetrics());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.animated_logo_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        mSubtitleView = view.findViewById(R.id.logo_subtitle);

        mLogoView = view.findViewById(R.id.animated_logo);
        mLogoView.setOnStateChangeListener(state -> {
            if (state == AnimatedMuzeiLogoView.STATE_FILL_STARTED) {
                mSubtitleView.setAlpha(0);
                mSubtitleView.setVisibility(View.VISIBLE);
                mSubtitleView.setTranslationY(-mSubtitleView.getHeight());

                // Bug in older versions where set.setInterpolator didn't work
                AnimatorSet set = new AnimatorSet();
                Interpolator interpolator = new OvershootInterpolator();
                ObjectAnimator a1 = ObjectAnimator.ofFloat(mLogoView, View.TRANSLATION_Y, 0);
                ObjectAnimator a2 = ObjectAnimator.ofFloat(mSubtitleView,
                        View.TRANSLATION_Y, 0);
                ObjectAnimator a3 = ObjectAnimator.ofFloat(mSubtitleView, View.ALPHA, 1);
                a1.setInterpolator(interpolator);
                a2.setInterpolator(interpolator);
                set.setDuration(500).playTogether(a1, a2, a3);
                set.start();

                if (mOnFillStartedCallback != null) {
                    mOnFillStartedCallback.run();
                }
            }
        });
        if (savedInstanceState == null) {
            reset();
        }
    }

    public void start() {
        mLogoView.start();
    }

    public void setOnFillStartedCallback(Runnable fillStartedCallback) {
        mOnFillStartedCallback = fillStartedCallback;
    }

    public void reset() {
        mLogoView.reset();
        mLogoView.setTranslationY(mInitialLogoOffset);
        mSubtitleView.setVisibility(View.INVISIBLE);
    }
}
