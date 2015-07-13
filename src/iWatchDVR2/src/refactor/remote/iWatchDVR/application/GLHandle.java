package refactor.remote.iWatchDVR.application;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;
import android.util.Log;

import refactor.remote.iWatchDVR.Type.Size;


public class GLHandle {

    public static final ByteBuffer Indices     = ByteBuffer.allocateDirect(6).put(new byte[] { 0, 1, 2, 2, 3, 0});
    public static final ByteBuffer osd_Indices = ByteBuffer.allocateDirect(6).put(new byte[] { 0, 1, 2, 2, 3, 0});

    public int time               = 0;
    public int update             = 0;
    
    public int[] renderBuffer     = new int[1];
    public int[] frameBuffer      = new int[1];
    public int[] vertexBuffer     = new int[3];
    public int[] osd_vertexBuffer = new int[1];
    public int[] indexBuffer      = new int[1];
    public int[] osd_indexBuffer  = new int[1];
    
    public int programHandle      = 0;
    public int positionSlot       = 0;
    public int colorSlot          = 0;
    public int texCoordSlot       = 0;
    
    public int scaleMat           = 0;
    public int scaleAjustShiftMat = 0; 
    public int translateMat       = 0;
    
    public int osd_programHandle  = 0;
    public int osd_positionSlot   = 0;
    public int osd_colorSlot      = 0;
    public int osd_texCoordSlot   = 0;
    
    public int textureUniformY    = 0;
    public int textureUniformU    = 0;
    public int textureUniformV    = 0;
    
    public int textureUniformOSD  = 0;
        
    public GLOSD               osd;
    public ArrayList<GLVideo>  video = new ArrayList<GLVideo>();
    
    
    GLHandle() {

        Indices.position(0);
        osd_Indices.position(0);
    }
    
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        //Log.w("GLHandle", "................GLHandle finalize");
    }
    
    void release() {

        for (int i = 0; i < video.size(); i++)
        {
            
            GLVideo v = video.set(i, null);
            if (v != null)
            {
                v.release();
                v = null;
            }
        }
        
        if (osd != null)
        {
            osd.release();
            osd = null;
        }
    }
    
    int FindVideo(int id)
    {
        for (int i = 0; i < video.size(); i++) {

            if (video.get(i).id == id)
               return i;
        }

        return -1;
    }
    
    
    public class GLOSD {
        
        Size              size             = new Size();
        TextureBox        texBox;
        ByteBuffer        osdBits          = null;
        boolean           shouldGenTexture = true;
        boolean           shouldUpdate     = false;
        
        GLOSD() {
            
        }
        
        void release() {

            if (texBox != null)
            {
                texBox.release();
                texBox = null;
            }

            osdBits = null;
        }
    }
    
    public class TextureBox {
        
        int[]            texture = new int[1];
        int              stride  = 0;
        Size             size    = new Size();
        
        TextureBox() {
            
        }
        
        public void release()
        {
            IntBuffer value = IntBuffer.wrap(texture);
            GLES20.glDeleteTextures(1, value);
            texture = null;
            value   = null;
        }
    }
    
    public class GLVideo {
        
        int                    id               = 0;
        int                    format           = 0;
        Size                   size             = new Size();
        ArrayList<TextureBox>  texBox           = new ArrayList<TextureBox>();
        ArrayList<byte[]>      videoBits        = new ArrayList<byte[]>();
        boolean                shouldGenTexture = true;
        boolean                shouldUpdate     = false;
        
        GLVideo() {
            
        }
        
        public void release() {
  
            for (int i = 0; i < texBox.size(); i++)
            {
                TextureBox tb = texBox.set(i, null);
                if (tb != null)
                {
                    tb.release();
                    tb = null;
                }
            }
            texBox.clear();
            texBox = null;
            
            for (int i = 0; i < videoBits.size(); i++)
            {
                byte[] vb = videoBits.set(i, null);
                vb = null;
            }
            videoBits.clear();
            videoBits = null;
        }
    }
}
