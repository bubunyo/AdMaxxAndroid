package com.alttech.admaxx;

import java.util.List;

public class Ad {
  String id;
  List<String> impressionUrls;
  String adSystem;
  String adTitle;
  CompanionAd companionAd;

  public String getId() {
    return id;
  }

  public List<String> getImpressionUrls() {
    return impressionUrls;
  }

  public String getAdSystem() {
    return adSystem;
  }

  public String getAdTitle() {
    return adTitle;
  }

  public CompanionAd getCompanionAd() {
    return companionAd;
  }

  public Linear getLinear() {
    return linear;
  }

  Linear linear;

  public static class Creative {
    public int getSequence() {
      return sequence;
    }

    public List<Tracking> getTrackingEvents() {
      return trackingEvents;
    }

    int sequence;
    List<Tracking> trackingEvents;
  }

  public static class Tracking {
    public String getEvent() {
      return event;
    }

    public String getEventUrl() {
      return eventUrl;
    }

    String event;
    String eventUrl;
  }

  public static class CompanionAd extends Creative {
    int width;
    int height;

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    public String getCompanionClickThrough() {
      return companionClickThrough;
    }

    public StaticResource getStaticResource() {
      return staticResource;
    }

    String companionClickThrough;
    StaticResource staticResource;
  }

  public static class StaticResource {
    String creativeType;
    String creative;

    public String getCreativeType() {
      return creativeType;
    }

    public String getCreative() {
      return creative;
    }
  }

  public static class MediaFile {
    String delivery;
    String type;
    int width;
    int height;
    String uri;

    public String getDelivery() {
      return delivery;
    }

    public String getType() {
      return type;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    public String getUri() {
      return uri;
    }
  }

  public static class Linear extends Creative {
    public String getDuration() {
      return duration;
    }

    public MediaFile getMediaFile() {
      return mediaFile;
    }

    String duration;
    MediaFile mediaFile;
  }
}
