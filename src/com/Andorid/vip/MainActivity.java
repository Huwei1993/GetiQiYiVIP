package com.Andorid.vip;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	ClipboardManager myClipboard;
	
	private EditText et_user;
	private EditText et_password;
	private String user;
	private String pass;
	private String[][] value;
	Handler msgHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		et_user = (EditText) findViewById(R.id.et_user);
		et_password = (EditText) findViewById(R.id.et_password);
	}
	public void get(View v){
		new Thread(){
			   @Override
			   public void run()
			   {
					try {
						String html = requestByHttpGet("http://www.cengfan8.com/ajax.php?code=&typename=3");
						if(!html.equals("-1")){
							String[] str = html.split(",");
							String tips = unicode2string(str[1].substring(7,str[1].length()-4));
							System.out.println("Tips:"+tips);
							if(tips.contains("��Ǹ")){
								msgHandler.post(new Runnable(){
									@Override
									public void run() {
										Toast.makeText(MainActivity.this, "���մ���������,������ʷ��¼", Toast.LENGTH_SHORT).show();  
									}
								});
							}
							else{
								tips=tips.substring(tips.indexOf("�ʺţ�")+2);
								String value[]=tips.split("���룺");
								System.out.println(value[0]);
								System.out.println(value[1]);
								user = value[0];
								pass = value[1];
								MainActivity.this.runOnUiThread(updateThread);
							}
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
			  }
			}.start();
	}
	int count=0;
	public void history(View v){
		new Thread(){
			   @Override
			   public void run()
			   {
					try {
						String html = requestByHttpGet("http://www.cengfan8.com/ajax_jilu.php?viptype=3");
						if(!html.equals("-1")){
							html=html.substring(html.indexOf("<p>")+3, html.lastIndexOf("</p>"));
							String[] str = html.split("</p><p>");
							value = new String[str.length][2];
							for (int i = 0; i < value.length; i++) {
								for (int j = 0; j < value[i].length; j++) {
									str[i]=str[i].substring(str[i].indexOf("�ʺţ�")+2);
									value[i]=str[i].split("���룺");	
								}
							}
							System.out.println(value[count][0]);
							user = value[count][0];
							pass = value[count][1];
							System.out.println(value[count][1]);
							MainActivity.this.runOnUiThread(updateThread);
							count++;
							if(count==5)
								count=0;
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
			  }
			}.start();
	}
	Runnable updateThread = new Runnable()      
    {     
         @Override    
         public void run()     
         {     
             //����UI     
             if (user != "") {  
            	 et_user.setText(user);  
             } else {  
            	 et_user.setText("��ȡ����");   
             }
             if (pass != "") {  
            	 et_password.setText(pass);  
             } else {  
            	 et_password.setText("��ȡ����");   
             }  
        }     
              
    };
    public static String unicode2string(String s) {
        List<String> list =new ArrayList<String>();
        String zz="\\\\u[0-9,a-z,A-Z]{4}";
        //������ʽ�÷��ο�API
        Pattern pattern = Pattern.compile(zz);
        Matcher m = pattern.matcher(s);
        while(m.find()){
            list.add(m.group());
        }
        for(int i=0,j=2;i<list.size();i++){
            String st = list.get(i).substring(j, j+4);
             
            //���õ�����ֵ����16���ƽ���Ϊʮ�����������ُ�תΪ�ַ�
            char ch = (char) Integer.parseInt(st, 16);
            //�õõ����ַ��滻������ʽ
            s = s.replace(list.get(i), String.valueOf(ch));
        }
        return s;
    }
	public static String requestByHttpGet(String url) throws Exception {
		//String path = "http://www.cengfan8.com/ajax_jilu.php?viptype=3";
		// �½�HttpGet����
		HttpGet httpGet = new HttpGet(url);
		// ��ȡHttpClient����
		HttpClient httpClient = new DefaultHttpClient();
		// ��ȡHttpResponseʵ��
		HttpResponse httpResp = httpClient.execute(httpGet);
		// �ж��ǹ�����ɹ�
		if (httpResp.getStatusLine().getStatusCode() == 200) {
			// ��ȡ���ص�����
			String result = EntityUtils.toString(httpResp.getEntity(), "UTF-8");
			Log.i("text", "HttpGet��ʽ����ɹ��������������£�");
			Log.i("text", result);
			return result;
		} else {
			Log.i("text", "HttpGet��ʽ����ʧ��");
			return "-1";
		}
	}

	public void copyuser(View v){
		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		ClipData copy= ClipData.newPlainText("text", et_user.getText().toString());
		myClipboard.setPrimaryClip(copy);
	}
	
	public void copypassword(View v){
		myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		ClipData copy= ClipData.newPlainText("text", et_password.getText().toString());
		myClipboard.setPrimaryClip(copy);
	}
}
