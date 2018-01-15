package com.example.alchera.androidpng;

import android.content.res.AssetManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.ImageLineByte;
import ar.com.hjg.pngj.PngReaderByte;
import ar.com.hjg.pngj.PngWriter;
import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by alchera on 18. 1. 12.
 */

class PngData {
    int width;
    int height;
    int channel;
    int bit_depth;
    byte[] data;
}

public class ReadWriteTest extends TextBackbone {

    PngData input;
    PngReaderByte pngr;
    PngWriter pngo;
    AssetManager am;
    String filename;

    @Before
    public void LoadPng()throws Exception{
        filename="cat.png";
        am=context.getAssets();

        pngr = new PngReaderByte(am.open(filename));

        assertNotNull(pngr);
        assertEquals(4,pngr.imgInfo.channels);
    }

    @Test
    public void ReadPng() throws Exception{
        input = new PngData();

        input.width=pngr.imgInfo.cols;
        input.height=pngr.imgInfo.rows;
        input.channel=pngr.imgInfo.channels;
        input.bit_depth=pngr.imgInfo.bitDepth;

        int numOfByte = CaculateByteNumber(input);
        input.data = new byte[numOfByte];

        assertNotNull(input);
        assertNotNull(input.data);

        InputData(pngr,input.data);
        assertFalse(pngr.hasMoreRows());

    }

    @Test
    public void WritePng() throws Exception {
        String output ="???";
        File newImage = File.createTempFile(output, "png");

        ImageInfo pre = pngr.imgInfo;
        ImageInfo imgInfo = new ImageInfo(pre.cols,pre.rows,pre.bitDepth,true);

        assertNotNull(imgInfo);

        pngo = new PngWriter(newImage,imgInfo);
        pngo.copyChunksFrom(pngr.getChunksList(), ChunkCopyBehaviour.COPY_ALL);

        ImageLineByte iline2 = new ImageLineByte(imgInfo);
        byte[] line2 = iline2.getScanline();

        for (int r = 0; r < imgInfo.rows; r++) {
            byte [] line1 = readOneRow(pngr);
            System.arraycopy(line1, 0, line2, 0, line2.length);
            pngo.writeRow(iline2);
        }

        pngo.end();
        newImage.delete();
    }


    public byte[] readOneRow(PngReaderByte pgrdr)
    {
        ImageLineByte line = pgrdr.readRowByte();
        byte[] scanline = line.getScanlineByte();
        return scanline;
    }


    public int CaculateByteNumber(PngData data) throws Exception {
        return ((data.bit_depth*data.channel)/8)*data.height*data.width;
    }


    public void InputData(PngReaderByte pgrdr, byte[] data)
    {
        int count = 0;
        while(pgrdr.hasMoreRows()) {
            byte[] line1 = readOneRow(pgrdr);
            System.arraycopy(line1,0,data,(line1.length)*count,line1.length);
            count++;
        }
    }
    @After
    public void close()
    {
        //if(pngr!=null)
        assertNotNull(pngr);
        pngr.end();
    }

}
