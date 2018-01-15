package com.example.alchera.androidpng;

import android.content.res.AssetManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngReaderByte;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Created by alchera on 18. 1. 11.
 */

class FrameData
{

    //    int format; // Always ARGB32
    int width, height;
    int channel;
    byte[] data;

}

public class ImageTest extends TextBackbone {

    FrameData lhs,rhs;
    PngReaderByte pngl, pngr;
    AssetManager am;

    public void inputData(PngReaderByte pgrdr, byte[] data)
    {
        int count =0;
        while(pgrdr.hasMoreRows()) {
            ImageLineByte line = pgrdr.readRowByte();
            byte[] line1 = line.getScanlineByte();
            System.arraycopy(line1,0,data,(line1.length)*count,line1.length);
            count++;
        }

    }
    public void inputDataReverse(PngReaderByte pgrdr, byte[] data)
    {
        int k = pgrdr.imgInfo.rows - 1;
        byte aux;
        while (pgrdr.hasMoreRows()) {
            ImageLineByte line = pgrdr.readRowByte();
            byte[] line1 = line.getScanlineByte();

            System.arraycopy(line1, 0, data,
                    line1.length * (k--), line1.length);
        }

    }
    public void inputDataMirror(PngReaderByte pgrdr, byte[] data)
    {
        int channels = pgrdr.imgInfo.channels;
        byte aux;
        for (int row = 0; row < pgrdr.imgInfo.rows; row++) {
            ImageLineByte l1 = pgrdr.readRowByte();
            byte[] line = l1.getScanlineByte();
            for (int c1 = 0, c2 = pgrdr.imgInfo.cols - 1; c1 < c2; c1++, c2--) {
                for (int i = 0; i < channels; i++) {
                    aux = line[c1 * channels + i];
                    line[c1 * channels + i] = line[c2 * channels + i];
                    line[c2 * channels + i] = aux;
                }
                System.arraycopy(line,0,data,
                        (line.length)*row,line.length);

            }
        }
    }
    public boolean CheckData(){
        Random rng = new Random();

        int length = lhs.width * rhs.height*lhs.channel;
        int count = 1000;

        while(count-- > 0){
            int idx = rng.nextInt(length - 1);
            // Assert.assertEquals(lhs.data[idx], rhs.data[idx]);
            if(lhs.data[idx]!=rhs.data[idx]) return false;
        }
        return true;
    }

    @Before
    public void LoadImage1() throws Exception{
        String filename = "cake.png";
        lhs=new FrameData();

        am=context.getAssets();
        pngl = new PngReaderByte(am.open(filename));
        lhs.channel = pngl.imgInfo.channels;

        lhs.width=pngl.imgInfo.cols;
        lhs.height=pngl.imgInfo.rows;
        lhs.data = new byte[lhs.width*lhs.height*lhs.channel];

        Assert.assertNotNull(lhs);
        Assert.assertNotNull(lhs.data);

        inputData(pngl,lhs.data);

        assertNotNull(lhs);
    }

    @Before
    public void LoadImage2() throws Exception {
        String filename = "cake_flip.png";
        rhs = new FrameData();
        am = context.getAssets();
        pngr = new PngReaderByte(am.open(filename));
        rhs.channel = pngr.imgInfo.channels;

        rhs.width = pngr.imgInfo.cols;
        rhs.height = pngr.imgInfo.rows;
        rhs.data = new byte[rhs.width * rhs.height * rhs.channel];

        Assert.assertNotNull(rhs);
        Assert.assertNotNull(rhs.data);
    }

    @Test
    public void Mirror()
    {
        inputDataMirror(pngr,rhs.data);
        assertTrue(CheckData());
    }

    @Test
    public void Flip()
    {
        inputDataReverse(pngr,rhs.data);
        assertTrue(CheckData());
    }

    @Test
    public void Same()
    {
        inputData(pngr,rhs.data);
        assertTrue(CheckData());
    }
    @Test
    public void CheckSize(){
        assertEquals(lhs.width, rhs.width);
        assertEquals(lhs.height, rhs.height);
    }

    @Test
    public void EnsureChan4(){
        assertEquals(4, rhs.channel);
        assertEquals(4, lhs.channel);
    }


}
