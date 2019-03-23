package com.alttech.admaxx;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.Pair;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.alttech.admaxx.AdMaxx.BASE;

public class AdMaxx {

  private static AdMaxx admaxx;
  private Ad ad;

  static String BASE = "https://api-admaxx-stage.afrad.io/api/ads/source/?v=v2&type=preroll&pid=";

  private ReqResponse r = ad -> AdMaxx.this.ad = ad;

  private String pid;
  private List<CallBack> callBacks = new ArrayList<>();

  private AdMaxx(String pid) {
    this.pid = pid;
  }

  public static void init(String pid) {
    if (admaxx != null) {
      throw new RuntimeException("AdMaxx has already been initialized");
    }

    admaxx = new AdMaxx(pid);

  }

  public static AdMaxx get() {
    if (admaxx == null) throw new RuntimeException("AdMaxx has not been initialized");
    return admaxx;
  }

  public void addCallback(CallBack callback) {
    this.callBacks.add(callback);
  }

  public void requestAd() {
    new RequestTask(pid, callBacks, r).execute();
  }

  private void registerEvent(String uri) {
    new RegisterEventTask(uri).execute();
  }

  public void registerImpression() {
    if (ad != null)
      for (String imp : ad.impressionUrls) {
        registerEvent(imp);
      }
  }

  public void registerAdStart() {
    if (ad != null)
      for (Ad.Tracking imp : ad.linear.trackingEvents) {
        registerEvent(imp.eventUrl);
      }
  }

  void registerCreateView() {
    if (ad != null)
      for (Ad.Tracking imp : ad.companionAd.trackingEvents) {
        registerEvent(imp.eventUrl);
      }
  }

  void clickThrough(Context c) {
    if (ad != null) {
      Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ad.companionAd.companionClickThrough));
      c.startActivity(browserIntent);
    }
  }
}

class RequestTask extends AsyncTask<Void, String, Pair<Ad, Throwable>> {

  private String pid;
  private List<CallBack> callBacks;
  private ReqResponse r;

  RequestTask(String pid, List<CallBack> callBacks, ReqResponse r) {
    this.pid = pid;
    this.callBacks = callBacks;
    this.r = r;
  }


  protected Pair<Ad, Throwable> doInBackground(Void... voids) {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(BASE + pid)
        .build();
    try {
      Response response = client.newCall(request).execute();
      String res = trim(response.body().string());

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document d1 = builder.parse(new InputSource(new StringReader(res)));

      Ad ad = new Ad();

      Element vastElement = (Element) d1.getElementsByTagName("VAST").item(0);

      ad.id = vastElement.getElementsByTagName("Ad").item(0).getAttributes().getNamedItem("id").getNodeValue();

      Element inLine = (Element) vastElement.getElementsByTagName("InLine").item(0);

      NodeList imps = inLine.getElementsByTagName("Impression");

      List<String> impressions = new ArrayList<>();

      for (int i = 0; i < imps.getLength(); i++) {
        Element e = (Element) imps.item(i);
        impressions.add(e.getFirstChild().getNodeValue());
      }

      ad.impressionUrls = impressions;
      ad.adSystem = inLine.getElementsByTagName("AdSystem").item(0).getFirstChild().getNodeValue();
      ad.adTitle = inLine.getElementsByTagName("AdTitle").item(0).getFirstChild().getNodeValue();


      Element crv = (Element) inLine.getElementsByTagName("Creatives").item(0);

      NodeList crvs = crv.getElementsByTagName("Creative");


      for (int i = 0; i < crvs.getLength(); i++) {

        Element e = (Element) crvs.item(i);


        switch (e.getChildNodes().item(0).getNodeName()) {
          case "CompanionAds": {
            Ad.CompanionAd c = new Ad.CompanionAd();
            c.sequence = Integer.parseInt(e.getAttribute("sequence"));
            e = (Element) e.getElementsByTagName("CompanionAds").item(0);
            e = (Element) e.getElementsByTagName("Companion").item(0);
            c.width = Integer.parseInt(e.getAttribute("width"));
            c.height = Integer.parseInt(e.getAttribute("height"));
            Element ct = (Element) e.getElementsByTagName("CompanionClickThrough").item(0);
            c.companionClickThrough = ct.getFirstChild().getNodeValue();
            ct = (Element) e.getElementsByTagName("StaticResource").item(0);
            Ad.StaticResource st = new Ad.StaticResource();
            st.creative = ct.getFirstChild().getNodeValue();
            st.creativeType = ct.getAttribute("creativeType");
            c.staticResource = st;

            Element te = (Element) e.getElementsByTagName("TrackingEvents").item(0);
            NodeList tList = te.getChildNodes();
            List<Ad.Tracking> tl = new ArrayList<>();
            for (int r = 0; r < tList.getLength(); r++) {
              if (tList.item(r) instanceof Text) continue;
              ct = (Element) tList.item(r);
              Ad.Tracking t = new Ad.Tracking();
              t.event = ct.getAttribute("event");
              t.eventUrl = ct.getFirstChild().getNodeValue();
              tl.add(t);
            }
            c.trackingEvents = tl;

            ad.companionAd = c;

            break;
          }
          case "Linear": {

            Ad.Linear l = new Ad.Linear();

            l.sequence = Integer.parseInt(e.getAttribute("sequence"));
            e = (Element) e.getElementsByTagName("Linear").item(0);

            Element d = (Element) e.getElementsByTagName("Duration").item(0);
            l.duration = d.getFirstChild().getNodeValue();

            Element te = (Element) e.getElementsByTagName("TrackingEvents").item(0);
            NodeList tList = te.getChildNodes();
            List<Ad.Tracking> tl = new ArrayList<>();
            for (int r = 0; r < tList.getLength(); r++) {
              if (tList.item(r) instanceof Text) continue;
              Element ct = (Element) tList.item(r);
              Ad.Tracking t = new Ad.Tracking();
              t.event = ct.getAttribute("event");
              t.eventUrl = ct.getFirstChild().getNodeValue();
              tl.add(t);
            }
            l.trackingEvents = tl;


            Element mfe = (Element) e.getElementsByTagName("MediaFiles").item(0);
            mfe = (Element) mfe.getElementsByTagName("MediaFile").item(0);
            Ad.MediaFile mf = new Ad.MediaFile();
            mf.uri = mfe.getFirstChild().getNodeValue();
            mf.delivery = mfe.getAttribute("delivery");
            mf.height = Integer.parseInt(mfe.getAttribute("height"));
            mf.width = Integer.parseInt(mfe.getAttribute("width"));
            mf.type = mfe.getAttribute("type");
            l.mediaFile = mf;

            ad.linear = l;
            break;
          }
        }
      }
      return new Pair<>(ad, null);

    } catch (IOException | ParserConfigurationException | SAXException e) {
      e.printStackTrace();

      return new Pair<>(null, e);

    }
  }

  @Override
  protected void onPostExecute(Pair<Ad, Throwable> pair) {
    super.onPostExecute(pair);
    if (pair.first != null) {
      r.action(pair.first);
      for (CallBack c : callBacks) {
        c.onSuccess(pair.first);
      }
    } else {
      r.action(null);
      for (CallBack c : callBacks) {
        c.onFailure(pair.second);
      }
    }
  }

  private static String trim(String input) {
    BufferedReader reader = new BufferedReader(new StringReader(input));
    StringBuilder result = new StringBuilder();
    try {
      String line;
      while ((line = reader.readLine()) != null)
        result.append(line.trim());
      return result.toString();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}


class RegisterEventTask extends AsyncTask<Void, String, Void> {

  private String uri;

  RegisterEventTask(String uri) {
    this.uri = uri;
  }

  protected Void doInBackground(Void... voids) {

    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(uri)
        .build();
    try {
      client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }


}


interface ReqResponse {
  void action(Ad ad);
}
