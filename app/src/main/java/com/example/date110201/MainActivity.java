package com.example.date110201;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    ImageView img,img2;
    ProgressBar pb,pb2;
    TextView tv;
    int readSum;
    File imgFile;
    Handler handler; //改寫runOnUiThread
    String path;

    void defaultSet()
    {
        readSum = 0;
        img.setImageBitmap(null);
        img2.setImageBitmap(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.imageView2);
        img2 = (ImageView)findViewById(R.id.imageView3);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        pb2 = (ProgressBar)findViewById(R.id.progressBar2);
        tv = (TextView)findViewById(R.id.textView);
        defaultSet();
        File f = getFilesDir();
        imgFile = new File(f.getAbsolutePath()+f.separator+"mypicture.jpeg");
        handler = new Handler(); //在主執行緒宣告，表示會在主執行緒執行
        path = "http://b4-q.mafengwo.net/s9/M00/B4/96/wKgBs1gAi8mAK_8eACErk-sdm7899.jpeg";
    }

    public void clickDown(View v)
    {
        pb.setVisibility(View.VISIBLE);
        defaultSet();
        MyAsyncTask task = new MyAsyncTask();
        task.execute(path);
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try {
//                    URL url = new URL("http://b4-q.mafengwo.net/s9/M00/B4/96/wKgBs1gAi8mAK_8eACErk-sdm7899.jpeg");
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.connect();
//                    //取得完整長度
//                    final int fullsize = conn.getContentLength();
//                    InputStream is = conn.getInputStream();
//                    byte b[] = new byte[1024];
//
//                    //暫存
//                    final ByteArrayOutputStream baop = new ByteArrayOutputStream();
//                    int reaSize;
//
//                    //Stream串流，流過回不來
//                    while ((reaSize = is.read(b)) != -1)
//                    {
//                        baop.write(b,0,reaSize);
//                        readSum += reaSize;
//
////                        runOnUiThread(new Runnable() {
////                            @Override
////                            public void run() {
////                                pb2.setMax(fullsize);
////                                pb2.setProgress(readSum);
////                                tv.setText("讀取中... "+String.valueOf(100 * readSum / fullsize) + " %");
////                            }
////                        });
//
//                        //改寫以上，用handler.post
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                pb2.setProgress(100 * readSum / fullsize);
//                                tv.setText("讀取中... "+String.valueOf(100 * readSum / fullsize) + " %");
//                            }
//                        });
//                    }
//                    byte result[] = baop.toByteArray();
//
//                    //必須 final
//                    final Bitmap bmp = BitmapFactory.decodeByteArray(result,0,result.length);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            img.setImageBitmap(bmp);
//                            pb.setVisibility(View.INVISIBLE);
//
//                            try {
//                                File f = getFilesDir();
//                                FileOutputStream fos = new FileOutputStream(imgFile);
//                                byte data[] = baop.toByteArray();
//
//                                fos.write(data);
//                                fos.close();
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                    is.close();
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
    }

    public void clickRead(View v)
    {
//        File f = getFilesDir();
//            FileInputStream fis = new FileInputStream(new File(f.getAbsolutePath()+f.separator+"mypicture.jpeg"));
//            byte data[] = new byte[fis.available()];
//            fis.read(data);
//            final Bitmap btm = BitmapFactory.decodeByteArray(data,0,data.length);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    img2.setImageBitmap(btm);
//                }
//            });
//            fis.close();
        final Bitmap btm = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        img2.setImageBitmap(btm);
    }

    class  MyAsyncTask extends AsyncTask<String,Integer,Bitmap>
    {
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bmp = null;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                final int fullsize = conn.getContentLength();
                InputStream is = conn.getInputStream();
                byte b[] = new byte[1024];

                //暫存
                final ByteArrayOutputStream baop = new ByteArrayOutputStream();
                int reaSize;

                //Stream串流，流過回不來
                while ((reaSize = is.read(b)) != -1)
                {
                    baop.write(b,0,reaSize);
                    readSum += reaSize;
                    //publishProgress(values[0],values[1],...);
                    publishProgress(readSum,100 * readSum / fullsize);
                }
                byte result[] = baop.toByteArray();
                bmp = BitmapFactory.decodeByteArray(result,0,result.length);

                //儲存圖片
                FileOutputStream fos = new FileOutputStream(imgFile);
                fos.write(result);
                fos.close();
                is.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pb2.setProgress(values[1]);
            tv.setText("載入中... "+values[1] + " %");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            img.setImageBitmap(bitmap);
            tv.setText("載入成功... 100 %");
            pb.setVisibility(View.INVISIBLE);
        }
    }
}
