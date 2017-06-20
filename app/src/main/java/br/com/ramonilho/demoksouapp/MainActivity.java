package br.com.ramonilho.demoksouapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // Binded Views
    @BindView(R.id.etNumA)
    EditText etNumA;

    @BindView(R.id.etNumB)
    EditText etNumB;

    CalculateAsync calculadora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.btSomar)
    public void somar() {
        double valorA = Double.parseDouble(etNumA.getText().toString());
        double valorB = Double.parseDouble(etNumB.getText().toString());

        CalculatorParams params = new CalculatorParams("+", valorA, valorB);
        calculadora = new CalculateAsync();
        calculadora.execute(params);
    }

    @OnClick(R.id.btSubtrair)
    public void subtrair() {
        double valorA = Double.parseDouble(etNumA.getText().toString());
        double valorB = Double.parseDouble(etNumB.getText().toString());

        CalculatorParams params = new CalculatorParams("-", valorA, valorB);
        calculadora = new CalculateAsync();
        calculadora.execute(params);
    }

    @OnClick(R.id.btMultiplicar)
    public void multiplicar() {
        double valorA = Double.parseDouble(etNumA.getText().toString());
        double valorB = Double.parseDouble(etNumB.getText().toString());

        CalculatorParams params = new CalculatorParams("*", valorA, valorB);
        calculadora = new CalculateAsync();
        calculadora.execute(params);
    }

    @OnClick(R.id.btDividir)
    public void dividir() {
        double valorA = Double.parseDouble(etNumA.getText().toString());
        double valorB = Double.parseDouble(etNumB.getText().toString());

        CalculatorParams params = new CalculatorParams("/", valorA, valorB);
        calculadora = new CalculateAsync();
        calculadora.execute(params);
    }

    private class CalculateAsync extends AsyncTask<CalculatorParams, Void, Double> {
        String METHOD_NAME = "Calculate";

        String SOAP_Action = "";

        String NAMESPACE = "http://ramonilho.com.br/";
        String SOAP_URL = "http://10.3.1.9:8080/Calculadora/Calculadora";

        SoapObject request;

        @Override
        protected Double doInBackground(CalculatorParams... params) {
            request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("a", params[0].a);
            request.addProperty("b", params[0].b);
            request.addProperty("o", params[0].operation);


            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

            // Registering double values:
            envelope.implicitTypes = true;
            envelope.encodingStyle = SoapSerializationEnvelope.XSD;
            MarshalDouble md = new MarshalDouble();
            md.register(envelope);

            // Setting envelope and calling
            envelope.setOutputSoapObject(request);
            HttpTransportSE httpTransport = new HttpTransportSE(SOAP_URL);
            try {
                httpTransport.call(SOAP_Action, envelope);
                return Double.parseDouble(envelope.getResponse().toString());
            } catch (Exception e) {
                e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Double result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Resultado: " + result, Toast.LENGTH_SHORT).show();
        }
    }

    private class CalculatorParams {
        String operation;
        double a, b;

        public CalculatorParams(String operation, double a, double b) {
            this.operation = operation;
            this.a = a;
            this.b = b;
        }
    }

    // Double XML Register
    private class MarshalDouble implements Marshal {
        public Object readInstance(XmlPullParser parser, String namespace,
                                   String name, PropertyInfo expected) throws IOException,
                XmlPullParserException {

            return Double.parseDouble(parser.nextText());
        }

        public void register(SoapSerializationEnvelope cm) {
            cm.addMapping(cm.xsd, "double", Double.class, this);

        }

        public void writeInstance(XmlSerializer writer, Object obj)
                throws IOException {
            writer.text(obj.toString());
        }
    }

}


