package com.example.webweather;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint("NewApi") public class MainActivity extends Activity {
	//指定命名空间
	private static final String NAMESPACE = "http://WebXml.com.cn/";
	//给出接口地址
	private static String URL = 
			"http://www.webxml.com.cn/webservices/weatherwebservice.asmx";
	// 设置方法名
	private static final String METHOD = "getWeatherbyCityName";
	// 设置查询接口参数
	private static String ACTION = "http://WebXml.com.cn/getWeatherbyCityName";
	
	// 定义字符串，保存天气信息
	private String weatherToday;
	// 定义SoapObject对象
	private SoapObject detail;
	// 定义输入控件
	private EditText cityNameText;
	// 定义显示控件，显示天气信息
	private TextView cityMsgView;
	// 定义按钮
	private Button okButton;
	
	ProgressBar progressBar;
	
	FrameLayout loading;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // 获取控件
        cityNameText = (EditText) findViewById(R.id.cityName);
        cityMsgView = (TextView) findViewById(R.id.msg);
        okButton = (Button) findViewById(R.id.search);
        progressBar = (ProgressBar) findViewById(R.id.pb);
        loading = (FrameLayout) findViewById(R.id.loading);
        
        // 为按钮添加事件监听
        okButton.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				loading.setVisibility(View.VISIBLE);
				// 执行获取天气信息的操作
				new showWeatherAsyncTask().execute();
			}
		});
        
        
    }

 // 使用AsyncTask异步方式获取并显示天气信息
    private class showWeatherAsyncTask extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... arg0) {
			// 获取并显示天气信息	
			showWeather();
			return null;
		}
		
		// 在后台任务完成之后，在此处更新UI
		@Override
		protected void onPostExecute(String result) {
		// 显示到cityMsgView控件上
			cityMsgView.setText(weatherToday.toString());
			loading.setVisibility(View.GONE);
		}
    	
    }
    // 获取并显示天气信息
	public void showWeather() {
		// 获取需要查询的城市名称
		String city = cityNameText.getText().toString().trim();
		// 检测城市名称是否为空
		if (!city.isEmpty()) {
			// 获取指定城市的天气信息
			getWeather(city);
		}
		
	}
	// 获取指定城市的天气信息，参数cityName为指定的城市名称
	private void getWeather(String cityName) {
		try {
			// 新建SoapObject对象
			SoapObject rpc = new SoapObject(NAMESPACE, METHOD);
			// 给SoapObject对象添加属性
			rpc.addProperty("theCityName", cityName);
			 // 创建HttpTransportSE对象，并指定WebService的WSDL文档的URL
			HttpTransportSE ht = new HttpTransportSE(URL);
			// 设置debug模式
			ht.debug = true;
			// 获得序列化的envelope
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			// 设置bodyOut属性的值为SoapObject对象rpc
			envelope.bodyOut = rpc;
			// 指定webservice的类型为dotNet
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			// 使用call方法调用WebService方法
			ht.call(ACTION, envelope);
			// 获取返回结果
			SoapObject result = (SoapObject) envelope.bodyIn;
			// 使用getResponse方法获得WebService方法的返回结果
			detail = (SoapObject) result.getProperty("getWeatherbyCityNameResult");
			 // 解析返回的数据信息为SoapObject对象，对其进行解析
			parseWeather(detail);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 // 解析SoapObject对象
	private void parseWeather(SoapObject detail) {
		System.out.println(detail.toString());
		// 获取日期
		String date = detail.getProperty(6).toString();
		// 获取天气信息
		try{
//			weatherToday = detail.toString();
			weatherToday = " \t今天：" + date.split(" ")[0];
			weatherToday = weatherToday + " 天气：" + date.split(" ")[1];
			weatherToday = weatherToday + " 气温：" + detail.getProperty(5).toString();
			weatherToday = weatherToday + " 风力：" + detail.getProperty(7).toString();
			weatherToday = weatherToday + " \n\n\t" + detail.getProperty(10).toString();
			weatherToday = weatherToday + " \n\n生活指数：\n" + detail.getProperty(11).toString();
			String tomorrow = detail.getProperty(13).toString();
			weatherToday = weatherToday + " \n\n\t明天:" + tomorrow.split(" ")[0];
			weatherToday = weatherToday + " 天气：" + tomorrow.split(" ")[1];
			weatherToday = weatherToday + " 气温：" + detail.getProperty(12).toString();
			weatherToday = weatherToday + " 风力：" + detail.getProperty(14).toString();
			
			String afterTomorrow = detail.getProperty(18).toString();
			weatherToday = weatherToday + " \n\n\t后天:" + afterTomorrow.split(" ")[0];
			weatherToday = weatherToday + " 天气：" + afterTomorrow.split(" ")[1];
			weatherToday = weatherToday + " 气温：" + detail.getProperty(17).toString();
			weatherToday = weatherToday + " 风力：" + detail.getProperty(19).toString();
			weatherToday = weatherToday + " \n\n\t所在城市介绍：" + detail.getProperty(22).toString();
		}catch(ArrayIndexOutOfBoundsException e){
			weatherToday = "该地区暂时不被支持";
		}
	}
    
    
    
}
