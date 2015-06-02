package com.robertlancer.supershare.util;


import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.http.HttpResponseException;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DriveBackoff <T> {

  private static final int RETRY_COUNT = 5;
  private static final Random randomGenerator = new Random();

  public T execute(AbstractGoogleClientRequest<T> request, boolean failSilently) throws IOException {

    for (int i = 0; i < RETRY_COUNT; i++) {
      try {
        return request.execute();
      } catch (HttpResponseException gre) {
        switch (gre.getStatusCode()) {
          case 500:
          case 403:
            System.out.println("Retrying request " + i + ", " + gre.getMessage());
            try {
              Thread.sleep((1 << i) * 1000 + randomGenerator.nextInt(1001));
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            continue;
          default:
            System.out.println("Unknown status code so throwing error " + gre.getStatusCode());
            if (failSilently)
              System.out.println("Fail silently status code $gre.statusCode - " + gre.getMessage());
            else
              throw gre;
        }
      } catch (IOException ioe) {
        if (failSilently)
          System.out.println("Fail silently " + ioe.getMessage());
        else
          throw ioe;
      }
    }
    return null;
  }
}


