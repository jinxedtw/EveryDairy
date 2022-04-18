package com.tw.longerrelationship.util.annotation;

import android.view.View;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


@Retention(SOURCE)
@IntDef({View.VISIBLE, View.INVISIBLE, View.GONE})
public @interface ViewState { }
