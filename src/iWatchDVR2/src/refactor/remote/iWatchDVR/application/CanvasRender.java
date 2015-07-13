package refactor.remote.iWatchDVR.application;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import peersdk.VideoPixelFormat;
import refactor.remote.iWatchDVR.application.GLHandle.GLOSD;
import refactor.remote.iWatchDVR.application.GLHandle.GLVideo;
import refactor.remote.iWatchDVR.application.GLHandle.TextureBox;
import android.content.res.Configuration;
import android.graphics.PointF;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class CanvasRender implements GLSurfaceView.Renderer {
    
public static final String TAG= "__CanvasRender__";
    
    public final int BYTE_PER_FLOAT = 4;

    CanvasView mView;

    ReentrantLock mLock = new ReentrantLock();
    
    GLHandle mHandle = null;
    
    int mViewPortX      = 0;
    int mViewPortY      = 0;
    int mViewPortWidth  = 0;
    int mViewPortHeight = 0;

    float mZoom = 1.0f;
    PointF mZoomAjustShiftVector = new PointF();
    PointF mPrevZoomCenter = new PointF();
    PointF mTouchCenter = new PointF();
    PointF mShift = new PointF();
    
    float _r = 0;
    float _g = 0;
    float _b = 0;
    
    /////////////////////////////////////////
    
    public CanvasRender(CanvasView view) {
        super();
        mView = view;
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        /**
        GLES20.glClearColor(_r, _g, _b, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);        
        
        if (_r >= 1f)
            _r = 0;
        if (_g >= 1f)
            _g = 0;
        if (_b >= 1f)
            _b = 0;
        
        _r += 0.051f;
        _g += 0.021f;
        _b += 0.011f;
        */
        update();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(1f, 0f, 0f, 1f);
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        

        /////////////////////////////
        int w;
        int h;
        int x;
        int y;
        if (Utility.GetScreenRrientation(mView.mContext) == Configuration.ORIENTATION_PORTRAIT) // portrait
        {
            w = width;
            h = width * 9 / 16;
            x = 0;
            y = (height - h) / 2;
        }
        else // landscape
        {
            w = width;
            h = height;
            x = 0;
            y = 0;
        }
        
        Log.i(TAG, "onSurfaceChanged, w=" + width + ", h=" + height + "__" + x + "," + y + "," + w + "," + h);
        
        SetViewPort(x, y, w, h);
        GLES20.glViewport(mViewPortX, mViewPortY, mViewPortWidth, mViewPortHeight);
        ////////////////////////////////
        
        reInitalize();
    }

    public boolean beginVideo(int cameraId, int width, int height, int format, byte[][] plane, int stride[]) {
        mLock.lock();
        
        Visuals visuals = mView.getVisuals();
        if (visuals == null) {
            Log.i(TAG, "visuals is null");
            mLock.unlock();
            return false;
        }

        if (mHandle == null) {
            Log.i(TAG, "handle is null");
            mLock.unlock();
            return false;
        }

        GLVideo video = null;
        int index = mHandle.FindVideo(cameraId);
        if (index >= 0)
            video = mHandle.video.get(index);

        if (video == null || video.format != format || video.size.width != width || video.size.height != height  || video.texBox.size() == 0)
        {
            if (video != null)
            {
                video.release();
                video = null;
                System.gc();
            }
            
            video = mHandle.new GLVideo();
            if (index >= 0)
            {
                if (mHandle.video.size() == 0)
                    mHandle.video.add(video);
                else
                    mHandle.video.set(index, video);
            }
            else {
                
                mHandle.video.add(video);
            }    
            video.id          = cameraId;
            video.format      = format;
            video.size.width  = width;
            video.size.height = height;
            
            switch (format) 
            {           
                case VideoPixelFormat.VideoPixelFormat_I420:
                {
                    int scale[] = { 1, 2, 2 };
                    for (int i = 0; i < 3; i++)
                    {
                        TextureBox textureBox = mHandle.new TextureBox();
                            
                        InitTexture(width / scale[i], height / scale[i], textureBox);
                        textureBox.stride = textureBox.size.width;
                            
                        video.texBox.add(textureBox);
                    }
                        

                    for (int i = 0; i < video.texBox.size(); i++)
                    {   
                        TextureBox textureBox = video.texBox.get(i);
                        video.videoBits.add(new byte[textureBox.stride * textureBox.size.height]); 
                        plane[i]  = video.videoBits.get(i);
                        stride[i] = textureBox.stride;
                    }
                   
                }
                break;
                        
                default:
                   return false;
            }
        }
        else
        {
            for (int i = 0; i < video.texBox.size(); i++)
            {
                plane[i]  = video.videoBits.get(i);
                stride[i] = video.texBox.get(i).stride;
            }
        }

        video.shouldUpdate = true;
        return true;
    }
    
    public void endVideo(int id) {
        try {
            mLock.unlock();
        }
        catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    
    protected void setupVBOs() {

        // VBO
        GLES20.glGenBuffers(3, mHandle.vertexBuffer, 0);

        // IBO
        GLES20.glGenBuffers(1, mHandle.indexBuffer, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mHandle.indexBuffer[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, GLHandle.Indices.capacity(), GLHandle.Indices, GLES20.GL_STATIC_DRAW);
    }
    
    protected void update() {
        mLock.lock();
        _update();
        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
        mLock.unlock();
    }
    
    protected void _update() {
        // Clear
        float red   = 0.0f;
        float green = 0.0f;
        float blue  = 0.0f;
        float alpha = 1.0f;
        
        
        GLES20.glClearColor(red, green, blue, alpha);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glViewport(mViewPortX, mViewPortY, mViewPortWidth, mViewPortHeight);
        
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        //////////////////////////////////////
        
        Visuals visuals = mView.getVisuals();
        if (visuals == null) {      
            Log.i(TAG, "visuals is null");
            return;
        }
        
        ArrayList<ViewA> view = visuals.getView();
        if (view == null) {
            Log.i(TAG, "get no view");
            return;
        }

        for (int i = 0; i < view.size(); i++) {
            ViewA v = view.get(i);
            int id = mHandle.FindVideo(v.Channel());
            if (id < 0) {
                //Log.w(TAG, "find no video=" + id);
                continue;
            }

            GenTextureIfNeed(id);

            presentTexture(id, v);
            
            GLHandle.Indices.clear();
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, GLHandle.Indices.capacity(), GLES20.GL_UNSIGNED_BYTE, GLHandle.Indices);
        }
    }
    
    
    protected void presentTexture(int chid, ViewA view) {

        GLES20.glUseProgram(mHandle.programHandle);
        
        mHandle.vertexBuffer[0] = 0;
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mHandle.vertexBuffer[0]);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mHandle.vertexBuffer[0]);

        FloatBuffer surface;

        // Vertex Arrays
        
        //TODO if (!mView.isZoomable())
        {
            surface = createVertex(chid, mViewPortWidth, mViewPortHeight, 
                    view.X(), view.Y(), view.Width(), view.Height());
        }
        /** TODO
        else
        
        {
            Size zoomDimension  = mView.zoomDimension();
            RectF zoomView      = mView.zoomView();
            float width  = zoomDimension.width * zoomDimension.width / zoomView.width();
            float height = zoomDimension.height * zoomDimension.height / zoomView.height();
            float x = -width * zoomView.left / zoomDimension.width;
            float y = -height* zoomView.top / zoomDimension.height;
            
            surface = createVertex(chid, mViewPortWidth, mViewPortHeight, (int)x, (int)y, (int)width, (int)height);
        }*/

        surface.position(0);
        GLES20.glVertexAttribPointer(mHandle.positionSlot, 3, GLES20.GL_FLOAT, false, 9 * BYTE_PER_FLOAT, surface);
        surface.position(3);
        GLES20.glVertexAttribPointer(mHandle.colorSlot,    4, GLES20.GL_FLOAT, false, 9 * BYTE_PER_FLOAT, surface);
        surface.position(7);
        GLES20.glVertexAttribPointer(mHandle.texCoordSlot, 2, GLES20.GL_FLOAT, false, 9 * BYTE_PER_FLOAT, surface);

        ////////////////////////////////////////////////////////////////////////////////////////////
        
        
        GLVideo video = mHandle.video.get(chid);
        for (int i = 0; i < video.texBox.size(); i++) {

            byte[] data = video.videoBits.get(i);
            ByteBuffer dataBuffer = ByteBuffer.wrap(data);
            dataBuffer.order(ByteOrder.nativeOrder());
            dataBuffer.position(0);
            
            TextureBox textureBox = video.texBox.get(i);
            int tW = textureBox.size.width;
            int tH = textureBox.size.height;
            
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + i);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureBox.texture[0]);
            
            if (video.shouldUpdate)
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, tW, tH, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, dataBuffer);

            if (i == 0)
                GLES20.glUniform1i(mHandle.textureUniformY, 0);
            else if (i == 1)
                GLES20.glUniform1i(mHandle.textureUniformU, 1);
            else if (i == 2)
                GLES20.glUniform1i(mHandle.textureUniformV, 2);

        }
        
        video.shouldUpdate = false;
    }
    
    protected void flush() {
        
        mLock.lock();            
        mHandle.video.clear();
        mLock.unlock();
    }

    
    protected void reInitalize() {
        
        mLock.lock();
        if (mHandle != null) {
        
            mHandle.release();
            mHandle = null;
        }
        mHandle = new GLHandle();

        initializeShaders();
        mLock.unlock();
    }
    
    protected void initializeShaders() {

        try {
            
            compileShaders();
            
        } catch (Exception e) {
            
            e.printStackTrace();
        }
    }
    
    protected void SetViewPort(int x, int y, int width, int height) {
        mLock.lock();        
        mViewPortX      = x;
        mViewPortY      = y;
        mViewPortWidth  = width;
        mViewPortHeight = height;
        mLock.unlock();
    }
    
    protected boolean InitTexture(int width, int height, TextureBox textureBox) {
        
        final int texOption[] = { 16, 32, 64, 128, 256, 512, 1024, 2048, 4096 };
        int tWidth = 0, tHeight = 0;
        
        for (int i = 0; i < texOption.length; i++)
        {
            if (texOption[i] >= width)
            {
                tWidth = texOption[i];
                break;
            }
        }
        
        for (int i = 0; i < texOption.length; i++)
        {
            if (texOption[i] >= height)
            {
                tHeight = texOption[i];
                break;
            }
        }
        
        textureBox.size.width  = tWidth;
        textureBox.size.height = tHeight;

        return true;
    }

    protected void GenTextureIfNeed(int chid) {
        
        GLVideo video = mHandle.video.get(chid);
        if (video.shouldGenTexture) {

            for (int i = 0; i < video.texBox.size(); i++) {
                
                TextureBox texture = video.texBox.get(i);
                GLES20.glGenTextures(1, texture.texture, 0); 
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.texture[0]);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            }
            video.shouldGenTexture = false;
        }
    }
    
    protected int compileShader(String shader, int shaderType) throws Exception {
        
        int handle = GLES20.glCreateShader(shaderType);
        if (handle == 0)
            throw new Exception("glCreateShader null");;

        GLES20.glShaderSource(handle, shader);
        GLES20.glCompileShader(handle);
        
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        
        if(compileStatus[0] == GLES20.GL_FALSE) {
            
            GLES20.glDeleteShader(handle);
            handle = 0;
            throw new Exception("glCreateShader null");
        }
        
        return handle;
    }
    
    protected void linkProgram(int handle, int vertexShaderHandle, int fragmentShaderHandle) throws Exception {
        
        GLES20.glAttachShader(handle, vertexShaderHandle);
        GLES20.glAttachShader(handle, fragmentShaderHandle);
        GLES20.glLinkProgram(handle);
        
        int[] linkStatus = new int[1];
        
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, linkStatus, 0);
        
        if (linkStatus[0] == GLES20.GL_FALSE) {
        
            Log.e(TAG, GLES20.glGetProgramInfoLog(handle));
             
            GLES20.glDeleteProgram(handle);
            throw new RuntimeException("failed to link program");
        }
    }

    protected void compileShaders() throws Exception {
        final String vertexGLSL =
                "attribute vec4 Position;"
              + "attribute vec4 SourceColor;"
              + "attribute vec2 TexCoordIn;"
              + "uniform   mat3 ScaleMat;"
              + "uniform   vec4 TranslateMat;"
              + "uniform   vec4 ScaleAjustShiftMat;"
              + "varying   vec2 TexCoordOut;"
              + "varying   vec4 DestinationColor;"
              + "void main(void) "
              + "{"
              + "DestinationColor = SourceColor;"
              + "gl_Position = DestinationColor * Position;"
              + "TexCoordOut = TexCoordIn;"
              + "}";
        
        final String fragmentGLSL =
                "varying mediump vec4 DestinationColor;"
              + "varying mediump vec2 TexCoordOut;"
              + "uniform sampler2D TextureY;"
              + "uniform sampler2D TextureU;"
              + "uniform sampler2D TextureV;"
              + "const mediump vec3 delYuv     = vec3(-16.0/255.0 , -128.0/255.0 , -128.0/255.0);"
              + "const mediump vec3 matYUVRGB1 = vec3(1.164,  2.018 ,   0.0   );"
              + "const mediump vec3 matYUVRGB2 = vec3(1.164, -0.391 , -0.813  );"
              + "const mediump vec3 matYUVRGB3 = vec3(1.164,    0.0 ,  1.596  );"
              + "void main(void)"
              + "{"
              + "mediump vec3 yuvColor;"
              + "mediump vec2 texcoord;"
              + "texcoord = TexCoordOut * 1.0;"
              + "yuvColor.x = texture2D( TextureY, TexCoordOut ).x;"
              + "yuvColor.y = texture2D( TextureV, texcoord ).x;"
              + "yuvColor.z = texture2D( TextureU, texcoord ).x;"
              + "yuvColor += delYuv;"
              + "mediump vec4 rgbColor;"
              + "rgbColor.x = dot(yuvColor,matYUVRGB1);"
              + "rgbColor.y = dot(yuvColor,matYUVRGB2);"
              + "rgbColor.z = dot(yuvColor,matYUVRGB3);"
              + "rgbColor.w = 1.0;"
              + "gl_FragColor = rgbColor;"
              + "}";
        
        
        // YUV
        int vertexShaderHandle   = compileShader(vertexGLSL, GLES20.GL_VERTEX_SHADER);
        int fragmentShaderHandle = compileShader(fragmentGLSL, GLES20.GL_FRAGMENT_SHADER);
        
        
        mHandle.programHandle = GLES20.glCreateProgram();
        if (mHandle.programHandle == GLES20.GL_FALSE) {
            
             throw new Exception("glCreateProgram null");
        }
        
        linkProgram(mHandle.programHandle, vertexShaderHandle, fragmentShaderHandle);
        GLES20.glUseProgram(mHandle.programHandle);

        mHandle.positionSlot = GLES20.glGetAttribLocation(mHandle.programHandle, "Position");
        mHandle.colorSlot    = GLES20.glGetAttribLocation(mHandle.programHandle, "SourceColor");
        mHandle.texCoordSlot = GLES20.glGetAttribLocation(mHandle.programHandle, "TexCoordIn");
        
        GLES20.glEnableVertexAttribArray(mHandle.positionSlot);
        GLES20.glEnableVertexAttribArray(mHandle.colorSlot);
        GLES20.glEnableVertexAttribArray(mHandle.texCoordSlot);

        mHandle.scaleMat     = GLES20.glGetUniformLocation(mHandle.programHandle, "ScaleMat");
        mHandle.scaleAjustShiftMat     = GLES20.glGetUniformLocation(mHandle.programHandle, "ScaleAjustShiftMat");
        mHandle.translateMat = GLES20.glGetUniformLocation(mHandle.programHandle, "TranslateMat");
        
        mHandle.textureUniformY = GLES20.glGetUniformLocation(mHandle.programHandle, "TextureY");
        mHandle.textureUniformU = GLES20.glGetUniformLocation(mHandle.programHandle, "TextureU");
        mHandle.textureUniformV = GLES20. glGetUniformLocation(mHandle.programHandle, "TextureV");
    }

    protected FloatBuffer createVertex(int chid, float presentWidth, float presentHeight,  int x, int y, int width, int height) {
        
        float l = -1.0f + x * 2.0f / presentWidth;
        float t =  1.0f - y * 2.0f / presentHeight;
        float r = -1.0f + (x + width)  * 2.0f / presentWidth;
        float b =  1.0f - (y + height) * 2.0f / presentHeight;
        
        GLVideo video = mHandle.video.get(chid);
        TextureBox texture = video.texBox.get(0);
        float tl = 0.0f;
        float tr = 1.0f * (float) video.size.width  / (float) texture.size.width;
        float tt = 1.0f * (float) video.size.height / (float) texture.size.height;
        float tb = 0.0f;
        

        float[] vertex = {  r,    b,    0.0f,        // Position
                            1.0f, 1.0f, 1.0f, 1.0f,  // Color
                            tr,   tt,                // texCoords
                            
                            r,    t,    0.0f, 
                            1.0f, 1.0f, 1.0f, 1.0f, 
                            tr, tb, 
                            
                            l,    t,    0.0f, 
                            1.0f, 1.0f, 1.0f, 1.0f, 
                            tl,   tb,
                            
                            l,    b,    0.0f, 
                            1.0f, 1.0f, 1.0f, 1.0f, 
                            tl,   tt };

        FloatBuffer fb = ByteBuffer.allocateDirect(vertex.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(vertex);
        fb.position(0);
        
        return fb;
    }
    
    
    public int getViewPortX() {
        
        return mViewPortX;
    }
    
    public int getViewPortY() {
        
        return mViewPortY;
    }
    
    public int getViewPortWidth() {
        
        return mViewPortWidth;
    }
    
    public int getViewPortHeight() {
        
        return mViewPortHeight;
    }

    //==============================
}