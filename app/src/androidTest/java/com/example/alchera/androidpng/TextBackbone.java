package com.example.alchera.androidpng;

/**
 * Created by alchera on 18. 1. 12.
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;


import org.junit.After;
import org.junit.Before;

public class TextBackbone
{
    protected Context context;

    @Before
    public void SetupContext(){
        context = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void TestDownContext(){
        context = null;
    }

}