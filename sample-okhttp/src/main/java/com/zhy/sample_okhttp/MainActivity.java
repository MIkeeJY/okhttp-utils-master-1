package com.zhy.sample_okhttp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Request;
import com.zhy.http.okhttp.OkHttpClientManager;
import com.zhy.http.okhttp.callback.ResultCallback;
import com.zhy.http.okhttp.request.OkHttpRequest;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 在https://github.com/hongyangAndroid/okhttp-utils基础上进行微调
 * 增加注释说明
 * 增加在线服务器测试文件上传和多文件上传
 */
public class MainActivity extends AppCompatActivity
{

    private TextView mTv;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private int i;

    public abstract class MyResultCallback<T> extends ResultCallback<T>
    {

        @Override
        public void onBefore(Request request)
        {
            super.onBefore(request);
            setTitle("loading...");
        }

        @Override
        public void onAfter()
        {
            super.onAfter();
            setTitle("Sample-okHttp");
        }
    }

    private ResultCallback<String> stringResultCallback = new MyResultCallback<String>()//
    {
        @Override
        public void onError(Request request, Exception e)
        {
            Log.e("TAG", "onError , e = " + e.getMessage());
        }

        @Override
        public void onResponse(String response)
        {
            Log.e("TAG", "onResponse , response = " + response);
            mTv.setText("operate success");
        }

        @Override
        public void inProgress(float progress)
        {

            Log.d(OkHttpClientManager.TAG,progress+"");
            mProgressBar.setProgress((int) (100 * progress));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTv = (TextView) findViewById(R.id.id_textview);
        mImageView = (ImageView) findViewById(R.id.id_imageview);
        mProgressBar = (ProgressBar) findViewById(R.id.id_progress);
        mProgressBar.setMax(100);

    }

    public void getUser(View view)
    {

        String url = "https://raw.githubusercontent.com/hongyangAndroid/okhttp-utils/master/user.gson";
        new OkHttpRequest.Builder()
                .url(url)
                .get(new MyResultCallback<User>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        Log.e("TAG", "onError , e = " + e.getMessage());
                    }

                    @Override
                    public void onResponse(User response)
                    {
                        Log.e("TAG", "onResponse , user = " + response);
                        mTv.setText(response.username);
                    }
                });

    }

    /**
     * 测试停止下载的
     * @param view
     */
    public void jumpNextActivity(View view){
        startActivity(new Intent(MainActivity.this,NextActivity.class));
        finish();
    }


    public void getUsers(View view)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("name", "zhy");
        String url = "https://raw.githubusercontent.com/hongyangAndroid/okhttp-utils/master/users.gson";
        new OkHttpRequest.Builder().url(url).params(params).post(new MyResultCallback<List<User>>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError , e = " + e.getMessage());
            }

            @Override
            public void onResponse(List<User> users)
            {
                Log.e("TAG", "onResponse , users = " + users);
                mTv.setText(users.get(0).toString());
            }
        });


    }

    public void getSimpleString(View view)
    {
        String url = "https://raw.githubusercontent.com/hongyangAndroid/okhttp-utils/master/user.gson";

        new OkHttpRequest.Builder().url(url)
                .get(new MyResultCallback<String>()
                {
                    @Override
                    public void onError(Request request, Exception e)
                    {
                        Log.e("TAG", "onError , e = " + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response)
                    {
                        mTv.setText(response);
                    }
                });

    }

    public void getHtml(View view)
    {
        //https://192.168.56.1:8443/
        //https://kyfw.12306.cn/otn/
        //https://192.168.187.1:8443/
        String url = "http://www.csdn.net/";
        new OkHttpRequest.Builder().url(url).get(new MyResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError" + e.getMessage());
            }

            @Override
            public void onResponse(String response)
            {
                mTv.setText(response);
            }
        });
    }

    public void getHttpsHtml(View view)
    {
        String url = "https://kyfw.12306.cn/otn/";
        new OkHttpRequest.Builder().url(url).get(new MyResultCallback<String>()
        {
            @Override
            public void onError(Request request, Exception e)
            {
                Log.e("TAG", "onError" + e.getMessage());
            }

            @Override
            public void onResponse(String response)
            {
                mTv.setText(response);
            }
        });
    }

    public void getImage(View view)
    {
        String url = "http://images.csdn.net/20150817/1.jpg";
        mTv.setText("");
        new OkHttpRequest.Builder().url(url).imageView(mImageView).displayImage(null);
    }


    /**
     * 服务器只判断了音频和图片的上传，其他格式应该会默认为.3gp
     * 同时支持单文件和多文件传送
     * @param view
     */
    public void uploadFile(View view)
    {

        File file = new File(Environment.getExternalStorageDirectory(), "face_cache.png");
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("user_id", "10086");

        Map<String, String> headers = new HashMap<>();
        headers.put("APP-Key", "APP-Secret222");
        headers.put("APP-Secret", "APP-Secret111");

        String url = "http://guixuan.snewfly.com/testUpload";
        new OkHttpRequest.Builder()//
                .url(url)//
                .params(params)
                .headers(headers)
                .files(new Pair<String, File>("upload", file))//
                .upload(stringResultCallback);
    }


    /**
     *服务器只判断了音频和图片的上传，其他格式应该会默认为.3gp
     * 同时支持单文件和多文件传送,限制单文件大小小于1Mb,防止有人恶意刷我服务器
     * @param view
     */
    public void multiFileUpload(View view)
    {
        File file = new File(Environment.getExternalStorageDirectory(), "face_cache.png");
        File file2 = new File(Environment.getExternalStorageDirectory(), "123.png");
        File file3 = new File(Environment.getExternalStorageDirectory(), "loginlog.txt");
        if (!file.exists())
        {
            Toast.makeText(MainActivity.this, "文件不存在，请修改文件路径", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");

        String url = "http://guixuan.snewfly.com/testUpload";
        new OkHttpRequest.Builder()//
                .url(url)//
                .params(params)
                .files(new Pair<String, File>("mFile", file), new Pair<String, File>("mFile1", file2), new Pair<String, File>("mFile3", file3))//mFile1不能相同
                .tag(MainActivity.this).upload(stringResultCallback);
    }


    public void downloadFile(View view)
    {
        i++;//测试取消，默认同时下载最大线程数5

        String url = "http://au.apk.umeng.com/uploads/apps/55274588fd98c59eca000867/_umeng_%40_10_%40_1b435a5f9b2b012297c42820e96cc812.apk";
        new OkHttpRequest.Builder()
                .url(url)
                .tag(MainActivity.this)
                .destFileDir(Environment.getExternalStorageDirectory().getAbsolutePath())
                .destFileName(i+"123.apk")
                .download(stringResultCallback);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        OkHttpClientManager.getInstance().cancelTag(MainActivity.this);//强制取消
    }
}
