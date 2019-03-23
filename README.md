    1 - use this to load the banner image. It extend image view so you can manipulate is accordingly
    `BannerView bannerView = findViewById(R.id.banner);`

    2 - Initials AdMaxx with the partner id preferably in the application class
    `AdMaxx.init("xxxxxxxxxxxxxxxxxxxx");`

     3 - Add callbacks any where in you app where you will need to be notified  when and ad is supplied after an adrequest is fired. Load the ad image creative using the ad banner class inside the call back
    ```AdMaxx.get().addCallback(new CallBack() {
      @Override
      public void onSuccess(Ad ad) {
        Picasso.get().load(ad.getCompanionAd().getStaticResource().getCreative()).into(bannerView);

      }

      @Override
      public void onFailure(Throwable t) {

      }

    });```

    4 - Request Ad
    `AdMaxx.get().requestAd();`

    5 - When the ad starts playing call this method to notify the ad server that the ad started playing
    `AdMaxx.get().registerAdStart();`

    6 - Once and ad has finished playing call this method to notify the ad server that the ad has finished playing. This step is very important to ensure proper revenue tracking and attribution
    `AdMaxx.get().registerImpression();`

    7 - Call request ad from step 4 anytime you need an ad
