package plaupi.statusorder;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import plaupi.statusorder.config.Config;

public class MainActivity extends AppCompatActivity {

    ProgressDialog loading;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart = (PieChart) findViewById(R.id.chart2);
        pieChart.setCenterText(generateCenterSpannableText());
        getData();
    }


    //fungsi untuk mengambil data dari database
    private void getData() {
        loading = ProgressDialog.show(this, "Mohon Tunggu", "Pengambilan data..", false, false);

        String url = Config.URL + "dashboard/loadStatusOrder.php"; //inisialiasai url

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, "Tidak ada Koneksi", Toast.LENGTH_LONG).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    //menampilkan username dari tabel users dan aktivitas dari tabel log
    private void showJSON(String response) {
        try {

            JSONArray result = new JSONArray(response);

            JSONObject Data1 = result.getJSONObject(0);
            String Proses = Data1.getString(Config.KEY_PROSES);
            JSONObject Data2 = result.getJSONObject(1);
            String Perbaikan = Data2.getString(Config.KEY_PERBAIKAN);
            JSONObject Data3 = result.getJSONObject(2);
            String Batal  = Data3.getString(Config.KEY_BATAL);
            JSONObject Data4 = result.getJSONObject(3);
            String Selesai = Data4.getString(Config.KEY_SELESAI);

            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(Integer.parseInt(Proses), 0));
            entries.add(new Entry(Integer.parseInt(Batal), 1));
            entries.add(new Entry(Integer.parseInt(Selesai), 2));
            entries.add(new Entry(Integer.parseInt(Perbaikan), 3));

            PieDataSet dataset = new PieDataSet(entries, "# of Calls");

            ArrayList<String> labels = new ArrayList<String>();
            labels.add("Proses");
            labels.add("Batal");
            labels.add("Selesai");
            labels.add("Perbaikan");

            PieData data = new PieData(labels, dataset);
            dataset.setColors(ColorTemplate.LIBERTY_COLORS); //
            pieChart.setDescription("Status Order");
            pieChart.setData(data);

            pieChart.animateY(2500);

            pieChart.saveToGallery("/sd/mychart.jpg", 85); // 85 is the quality of the image
        } catch (JSONException e) {
            e.printStackTrace();

        }
        //parsing json
        loading.dismiss();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Status Order");
        //s.setSpan(new RelativeSizeSpan(1.7f), 0, 3, 0);
        //s.setSpan(new StyleSpan(Typeface.NORMAL), 3, s.length() - 4, 0);
        //s.setSpan(new ForegroundColorSpan(Color.GRAY), 3, s.length() - 4, 0);
        //s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        //s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        //s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }
}


